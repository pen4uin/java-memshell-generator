package jmg.core.format;

import java.lang.reflect.Method;

public class BCELoader {
    static {
        new BCELoader();
    }

    private String getClassName() {
        return "";
    }

    private String getBase64String() {
        return "";
    }

    public BCELoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception e) {
            try {
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                byte[] clazzBytes = decodeFromBase64(getBase64String());
                Class clazz = (Class) defineClass.invoke(classLoader, clazzBytes, 0, clazzBytes.length);
                clazz.newInstance();
            } catch (Exception ee) {
            }
        }
    }

    public static byte[] decodeFromBase64(String input) {
        byte[] var2 = null;

        Class var1;
        try {
            var1 = Class.forName("java.util.Base64");
            Object var3 = var1.getMethod("getDecoder").invoke((Object) null, (Object[]) null);
            var2 = (byte[]) ((byte[]) var3.getClass().getMethod("decode", String.class).invoke(var3, input));
        } catch (Exception var6) {
            try {
                var1 = Class.forName("sun.misc.BASE64Decoder");
                Object var4 = var1.newInstance();
                var2 = (byte[]) ((byte[]) var4.getClass().getMethod("decodeBuffer", String.class).invoke(var4, input));
            } catch (Exception var5) {
            }
        }

        return var2;
    }
}