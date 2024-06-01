package jmg.suo5.util;

import jmg.core.config.Constants;
import jmg.suo5.memshell.Suo5Filter;
import jmg.suo5.memshell.Suo5Interceptor;
import jmg.suo5.memshell.Suo5Listener;

import java.util.HashMap;
import java.util.Map;

public class ShellUtil {

    private static final Map<String, String> SHELL_CLASSNAME_MAP = new HashMap();
    private static final Map<String, Map<String, String>> toolMap = new HashMap();

    public ShellUtil() {
    }

    public static String getShellName(String toolType, String shellType) {
        Map<String, String> shellMap = toolMap.get(toolType);
        return shellMap == null ? "" : shellMap.getOrDefault(shellType, "");
    }

    public static String getShellClassName(String shellName) throws Exception {
        if (SHELL_CLASSNAME_MAP.get(shellName) == null) {
            throw new Exception("Invalid shell type '" + shellName + "'");
        } else {
            return SHELL_CLASSNAME_MAP.getOrDefault(shellName, "");
        }
    }

    static {
        SHELL_CLASSNAME_MAP.put(Suo5Listener.class.getSimpleName(), Suo5Listener.class.getName());
        SHELL_CLASSNAME_MAP.put(Suo5Filter.class.getSimpleName(), Suo5Filter.class.getName());
        SHELL_CLASSNAME_MAP.put(Suo5Interceptor.class.getSimpleName(), Suo5Interceptor.class.getName());
        Map<String, String> suo5Map = new HashMap();
        suo5Map.put(Constants.SHELL_FILTER, Suo5Filter.class.getSimpleName());
        suo5Map.put(Constants.SHELL_LISTENER, Suo5Listener.class.getSimpleName());
        suo5Map.put(Constants.SHELL_INTERCEPTOR, Suo5Interceptor.class.getSimpleName());
        toolMap.put(Constants.TOOL_SUO5, suo5Map);
    }


}
