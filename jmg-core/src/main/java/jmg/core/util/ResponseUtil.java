package jmg.core.util;

import java.util.HashMap;
import java.util.Map;


public class ResponseUtil {

    private static final Map<String, String> METHOD_BODY_MAP = new HashMap<>();

    static {
        METHOD_BODY_MAP.put("tomcat", getCommonMethodBody());
        METHOD_BODY_MAP.put("jboss", getCommonMethodBody());
        METHOD_BODY_MAP.put("weblogic", getCommonMethodBody());
        METHOD_BODY_MAP.put("glassfish", getCommonMethodBody());
        METHOD_BODY_MAP.put("resin", getResinMethodBody());
        METHOD_BODY_MAP.put("jetty", getJettyMethodBody());
        METHOD_BODY_MAP.put("websphere", getWebsphereMethodBody());
        METHOD_BODY_MAP.put("undertow", getUndertowMethodBody());
    }

    public static String getMethodBody(String serverType) {
        return METHOD_BODY_MAP.getOrDefault(serverType.toLowerCase(), "");
    }

    private static String getCommonMethodBody() {
        return "{javax.servlet.http.HttpServletResponse response = null;" +
                "        try {" +
                "            response = (javax.servlet.http.HttpServletResponse) getFV(getFV($1, \"request\"), \"response\");" +
                "        } catch (Exception ex) {" +
                "            try {" +
                "                response = (javax.servlet.http.HttpServletResponse) getFV($1, \"response\");" +
                "            } catch (Exception ex1) {" +
                "            }" +
                "        }" +
                "        return response;}";
    }

    private static String getResinMethodBody() {
        return "{javax.servlet.http.HttpServletResponse response;" +
                "        response = (javax.servlet.http.HttpServletResponse) getFV($1, \"_response\");" +
                "        return response;}";
    }

    private static String getJettyMethodBody() {
        return "{javax.servlet.http.HttpServletResponse response;\n" +
                "        try{\n" +
                "            response = (javax.servlet.http.HttpServletResponse) getFV(getFV($1,\"_channel\"),\"_response\");\n" +
                "        }catch (Exception e){\n" +
                "            response = (javax.servlet.http.HttpServletResponse) getFV(getFV($1,\"_connection\"),\"_response\");\n" +
                "        }\n" +
                "        return response;}";
    }

    private static String getWebsphereMethodBody() {
        return "{javax.servlet.http.HttpServletResponse response;" +
                "        response = (javax.servlet.http.HttpServletResponse) getFV(getFV($1, \"_connContext\"), \"_response\");" +
                "        return response;}";
    }


    private static String getUndertowMethodBody() {
        return "{javax.servlet.http.HttpServletResponse response = null;\n" +
                "java.util.Map map = (java.util.Map) getFV(getFV($1, \"exchange\"), \"attachments\");\n" +
                "Object[] keys = map.keySet().toArray();\n" +
                "for (int i = 0; i < keys.length; i++) {\n" +
                "    Object key = keys[i];\n" +
                "    if (map.get(key).toString().contains(\"ServletRequestContext\")) {\n" +
                "        response = (javax.servlet.http.HttpServletResponse) getFV(map.get(key), \"servletResponse\");\n" +
                "        break;\n" +
                "    }\n" +
                "}\n" +
                "return response;}";

    }
}
