package jmg.core.format;

import javassist.ClassPool;
import javassist.CtClass;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.template.SpringMVCAgentTransformer;
import jmg.core.template.TomcatAgentTransformer;
import jmg.core.util.CommonUtil;
import jmg.core.util.JavassistUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

// todo: 其他中间件

public class JARAgentFormater implements IFormater {
    public byte[] transform(byte[] clazzbyte, AbstractConfig config) throws Exception {
        String className = TomcatAgentTransformer.class.getName();
        String simpleName = TomcatAgentTransformer.class.getSimpleName();
        if (config.getServerType().equals(Constants.SERVER_TOMCAT)) {
            className = TomcatAgentTransformer.class.getName();
            simpleName = TomcatAgentTransformer.class.getSimpleName();
        } else if (config.getServerType().equals(Constants.SERVER_SPRING_MVC)) {
            className = SpringMVCAgentTransformer.class.getName();
            simpleName = SpringMVCAgentTransformer.class.getSimpleName();
        } else {
            throw new RuntimeException(String.format("Java Agent 暂时只支持 %s、%s",Constants.SERVER_TOMCAT,Constants.SERVER_SPRING_MVC));
        }

        String classFileName = simpleName.replace('.', '/') + ".class";
        ClassPool pool = ClassPool.getDefault();
//        Note: jar 包中的文件不能通过文件路径读取，需要通过流读取
//        File jarFile = new File(JARAgentFormater.class.getClassLoader().getResource("jmg-agent.jar").getFile());

        InputStream jarStream = JARAgentFormater.class.getClassLoader().getResourceAsStream("jmg-agent.jar");
        File jarFile = File.createTempFile("jmg-agent", ".jar");
        try (FileOutputStream out = new FileOutputStream(jarFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = jarStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        Manifest manifest = createManifest(simpleName);
        File tempJarFile = File.createTempFile("tempJar", ".jar");

        try (JarFile jar = new JarFile(jarFile);
             JarOutputStream tempJar = new JarOutputStream(new FileOutputStream(tempJarFile), manifest)) {

            copyJarEntries(jar, tempJar);

            addModifiedClassToJar(pool, className, simpleName, classFileName, tempJar, config.getPass(), CommonUtil.encodeBase64(clazzbyte));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Files.readAllBytes(Paths.get(tempJarFile.getAbsolutePath()));
    }

    private Manifest createManifest(String simpleName) {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue("Agent-Class", simpleName);
        manifest.getMainAttributes().putValue("Can-Redefine-Classes", "true");
        manifest.getMainAttributes().putValue("Can-Retransform-Classes", "true");
        manifest.getMainAttributes().putValue("Main-Class", simpleName);
        return manifest;
    }

    private void copyJarEntries(JarFile jar, JarOutputStream tempJar) throws IOException {
        Enumeration<JarEntry> jarEntries = jar.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry entry = jarEntries.nextElement();
            try (InputStream entryInputStream = jar.getInputStream(entry)) {
                tempJar.putNextEntry(entry);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = entryInputStream.read(buffer)) != -1) {
                    tempJar.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private void addModifiedClassToJar(ClassPool pool, String className, String simpleName, String classFileName, JarOutputStream tempJar, String injectFlag, String injectorCode) throws Exception {
        CtClass ctClass = pool.get(className);
        ctClass.getClassFile().setVersionToJava5();
        ctClass.setName(simpleName);
        JavassistUtil.addMethod(ctClass, "getInjectorCode", "return \"" + injectorCode + "\";");
        tempJar.putNextEntry(new JarEntry(classFileName));
        tempJar.write(ctClass.toBytecode());
        ctClass.detach();
    }
}