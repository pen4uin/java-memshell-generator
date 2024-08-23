package jmg.core.util;

import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.jMGCodeApi;
import me.gv7.woodpecker.tools.common.FileUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CommonUtil {

    // 合并两个数组
    public static <T> T[] concatenateArrays(T[] array1, T[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        T[] result = Arrays.copyOf(array1, length1 + length2);

        System.arraycopy(array2, 0, result, length1, length2);

        return result;
    }

    public static void setFV(Object var0, String var1, Object val) throws Exception {
        getF(var0, var1).set(var0, val);
    }

    public static Object getFV(Object obj, String fieldName) throws Exception {
        Field field = getF(obj, fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    public static Field getF(Object obj, String fieldName) throws NoSuchFieldException {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    public static synchronized Object invokeMethod(Object targetObject, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(targetObject, methodName, new Class[0], new Object[0]);
    }


    public static Object invokeMethod(final Object obj, final String methodName, Class[] paramClazz, Object[] param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = (obj instanceof Class) ? (Class) obj : obj.getClass();
        Method method = null;

        Class tempClass = clazz;
        while (method == null && tempClass != null) {
            try {
                if (paramClazz == null) {
                    // Get all declared methods of the class
                    Method[] methods = tempClass.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        if (methods[i].getName().equals(methodName) && methods[i].getParameterTypes().length == 0) {
                            method = methods[i];
                            break;
                        }
                    }
                } else {
                    method = tempClass.getDeclaredMethod(methodName, paramClazz);
                }
            } catch (NoSuchMethodException e) {
                tempClass = tempClass.getSuperclass();
            }
        }
        if (method == null) {
            throw new NoSuchMethodException(methodName);
        }
        method.setAccessible(true);
        if (obj instanceof Class) {
            try {
                return method.invoke(null, param);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            try {
                return method.invoke(obj, param);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }


    public static String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(5) + 2;
        for (int i = 0; i < length; i++) {
            char c = (char) (random.nextInt(26) + 'a');
            if (i == 0) {
                c = Character.toUpperCase(c);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static byte[] base64Decode(String var0) throws Exception {
        byte[] var2 = null;

        Class var1;
        try {
            var1 = Class.forName("java.util.Base64");
            Object var3 = var1.getMethod("getDecoder").invoke((Object) null, (Object[]) null);
            var2 = (byte[]) ((byte[]) var3.getClass().getMethod("decode", String.class).invoke(var3, var0));
        } catch (Exception var6) {
            try {
                var1 = Class.forName("sun.misc.BASE64Decoder");
                Object var4 = var1.newInstance();
                var2 = (byte[]) ((byte[]) var4.getClass().getMethod("decodeBuffer", String.class).invoke(var4, var0));
            } catch (Exception var5) {
            }
        }
        return var2;
    }


    public static String encodeBase64(byte[] bs) throws Exception {
        String value = null;
        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", (Class[]) null).invoke(base64, (Object[]) null);
            value = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, bs);
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, bs);
            } catch (Exception var5) {
            }
        }
        return value;
    }


    public static String genRandomLengthString(int minLength) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(6) + minLength;
        for (int i = 0; i < length; i++) {
            char c = (char) (random.nextInt(26) + 'a');
            if (i == 0) {
                c = Character.toUpperCase(c);
            }
            sb.append(c);
        }
        return sb.toString();
    }


    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom random = new SecureRandom();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(52);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getMd5(String text) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append(Integer.toHexString((0x000000FF & aByte) | 0xFFFFFF00).substring(6));
        }
        return builder.toString();
    }

    public static byte[] gzipCompress(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(data);
        }
        return out.toByteArray();
    }

    public static byte[] gzipDecompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return outputStream.toByteArray();
    }


    public static byte[] getFileBytes(String file) throws Exception {
        File f = new File(file);
        int length = (int) f.length();
        byte[] data = new byte[length];
        (new FileInputStream(f)).read(data);
        return data;
    }


    public static String getSimpleName(String className) {
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < className.length() - 1) {
            return className.substring(lastDotIndex + 1);
        }
        return className;
    }


    public static String getFileOutputPath(String format_type, String class_simple_name, String output_path) {
        String file_output_path = null;

        String fileSeparator = File.separator;
        File file = new File(output_path);
        if (output_path.endsWith(".class") || output_path.endsWith(".jar") || output_path.endsWith(".jsp")) {
            output_path = file.getParent();

        }
        String[] parts = output_path.split(Pattern.quote(fileSeparator));
        boolean isFilePath = false; // 添加标记用于判断是否为文件路径
        for (String part : parts) {
            if (part.contains(".")) {
                isFilePath = true;
                break;
            } else {
                if (!output_path.endsWith(fileSeparator)) {
                    output_path = output_path + fileSeparator;
                }
            }
        }

        if (isFilePath) { // 如果是文件路径直接返回
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            return file.getAbsolutePath();
        }

        File dir = new File(output_path);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        // 判断输出格式
        switch (format_type) {
            case Constants.FORMAT_CLASS:
                file_output_path = output_path + class_simple_name + ".class";
                break;
            case Constants.FORMAT_JAR:
            case Constants.FORMAT_JAR_AGENT:
                file_output_path = output_path + class_simple_name + ".jar";
                break;
            case Constants.FORMAT_JSP:
                file_output_path = output_path + class_simple_name + ".jsp";
                break;
            default:
                break;
        }
        return file_output_path;
    }


    public static void transformExtenderToFile(AbstractConfig config) throws Throwable {
        config.setJarClassName(config.getExtenderClassName());
        config.setSavePath(getFileOutputPath(config.getOutputFormat(), config.getExtenderSimpleClassName(), config.getSavePath()));
        jMGCodeApi codeApi = new jMGCodeApi(config);
        FileUtil.writeFile(config.getSavePath(), codeApi.generate());
    }

    public static void transformToFile(AbstractConfig config) throws Throwable {
        config.setSavePath(getFileOutputPath(config.getOutputFormat(), config.getInjectorSimpleClassName(), config.getSavePath()));
        jMGCodeApi codeApi = new jMGCodeApi(config);
        FileUtil.writeFile(config.getSavePath(), codeApi.generate());
    }

    // base64/bcel/js/biginteger
    public static String transformTotext(AbstractConfig config) throws Throwable {
        jMGCodeApi codeApi = new jMGCodeApi(config);
        return new String(codeApi.generate());
    }

    public static String getThrowableStackTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        return stringWriter.toString();
    }

}
