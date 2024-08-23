package jmg.extender.detector;

import java.util.Map;

public class SleepDetector {

    static {
        new SleepDetector();
    }

    public SleepDetector() {
        initServerType();
    }

    public static String getServerType() {
        return "tomcat";
    }

    private static void execSleep() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
    }

    public static void initServerType() {
        String target_server_type = getServerType();
        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
            StackTraceElement[] stackTraceElements = entry.getValue();
            for (StackTraceElement element : stackTraceElements) {
                if (target_server_type.equals("tomcat") && element.getClassName().contains("org.apache.catalina.core")) {
                    execSleep();
                    return;
                }
                if (target_server_type.equals("weblogic") && element.getClassName().contains("weblogic.servlet.internal")) {
                    execSleep();
                    return;
                }
                if (target_server_type.equals("resin") && element.getClassName().contains("com.caucho.server")) {
                    execSleep();
                    return;
                }
                if (target_server_type.equals("jetty") && element.getClassName().contains("org.eclipse.jetty.server")) {
                    execSleep();
                    return;
                }
                if (target_server_type.equals("websphere") && element.getClassName().contains("com.ibm.ws")) {
                    execSleep();
                    return;
                }
                if (target_server_type.equals("undertow") && element.getClassName().contains("io.undertow.server")) {
                    execSleep();
                    return;
                }
                if (target_server_type.equals("tongweb") && element.getClassName().contains("com.tongweb.web")) {
                    execSleep();
                    return;
                }
                if (target_server_type.equals("apusic") && element.getClassName().contains("com.apusic.web")) {
                    execSleep();
                    return;
                }
                if (target_server_type.equals("spring") && element.getClassName().contains("org.springframework.web")) {
                    execSleep();
                    return;
                }
            }
        }
    }
}
