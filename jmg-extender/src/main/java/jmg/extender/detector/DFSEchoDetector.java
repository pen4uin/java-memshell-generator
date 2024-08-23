package jmg.extender.detector;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

public class DFSEchoDetector {
    static HashSet<Object> h;
    static ClassLoader cl = Thread.currentThread().getContextClassLoader();
    static Class hsr;//HTTPServletRequest.class
    static Class hsp;//HTTPServletResponse.class
    static Object r;
    static Object p;

    static {
        new DFSEchoDetector();
    }

    private static String server_type = "none";


    public DFSEchoDetector() {
        initServerType();
        r = null;
        p = null;
        h =new HashSet<Object>();
        try {
            hsr = cl.loadClass("javax.servlet.http.HttpServletRequest");
            hsp = cl.loadClass("javax.servlet.http.HttpServletResponse");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        F(Thread.currentThread(),0);
    }

    private static boolean i(Object obj){
        if(obj==null|| h.contains(obj)){
            return true;
        }

        h.add(obj);
        return false;
    }
    private static void p(Object o, int depth){
        if(depth > 52||(r !=null&& p !=null)){
            return;
        }
        if(!i(o)){
            if(r ==null&&hsr.isAssignableFrom(o.getClass())){
                r = o;
                //Tomcat特殊处理
                try {
                    Method getResponse = r.getClass().getMethod("getResponse");
                    p = getResponse.invoke(r);
                } catch (Exception e) {
                    r=null;
                }

            }else if(p ==null&&hsp.isAssignableFrom(o.getClass())){
                p =  o;
            }
            if(r !=null&& p !=null){
                try {
                    PrintWriter pw =  (PrintWriter)hsp.getMethod("getWriter").invoke(p);
                    pw.println(server_type);
                    pw.flush();
                    pw.close();

                }catch (Exception e){
                }
                return;
            }

            F(o,depth+1);
        }
    }
    private static void F(Object start, int depth){

        Class n=start.getClass();
        do{
            for (Field declaredField : n.getDeclaredFields()) {
                declaredField.setAccessible(true);
                Object o = null;
                try{
                    o = declaredField.get(start);

                    if(!o.getClass().isArray()){
                        p(o,depth);
                    }else{
                        for (Object q : (Object[]) o) {
                            p(q, depth);
                        }

                    }

                }catch (Exception e){
                }
            }

        }while(
                (n = n.getSuperclass())!=null
        );
    }

    public static void initServerType() {
        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
            StackTraceElement[] stackTraceElements = entry.getValue();
            for (StackTraceElement element : stackTraceElements) {
                if (element.getClassName().contains("org.apache.catalina.core")) {
                    server_type = "tomcat";
                    return;
                }
                if (element.getClassName().contains("weblogic.servlet.internal")) {
                    server_type = "weblogic";
                    return;
                }
                if (element.getClassName().contains("com.caucho.server")) {
                    server_type = "resin";
                    return;
                }
                if (element.getClassName().contains("org.eclipse.jetty.server")) {
                    server_type = "jetty";
                    return;
                }
                if (element.getClassName().contains("com.ibm.ws")) {
                    server_type = "websphere";
                    return;
                }
                if (element.getClassName().contains("io.undertow.server")) {
                    server_type = "undertow";
                    return;
                }
                if (element.getClassName().contains("com.tongweb.web")) {
                    server_type = "tongweb";
                    return;
                }
                if (element.getClassName().contains("com.apusic.web")) {
                    server_type = "apusic";
                    return;
                }
                if (element.getClassName().contains("org.springframework.web")) {
                    server_type = "spring";
                    return;
                }
            }
        }
    }
}