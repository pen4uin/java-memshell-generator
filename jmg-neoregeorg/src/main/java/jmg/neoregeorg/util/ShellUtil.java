package jmg.neoregeorg.util;

import jmg.core.config.Constants;
import jmg.neoregeorg.memshell.NeoreGeorgFilter;
import jmg.neoregeorg.memshell.NeoreGeorgInterceptor;
import jmg.neoregeorg.memshell.NeoreGeorgListener;

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
        SHELL_CLASSNAME_MAP.put(NeoreGeorgListener.class.getSimpleName(), NeoreGeorgListener.class.getName());
        SHELL_CLASSNAME_MAP.put(NeoreGeorgFilter.class.getSimpleName(), NeoreGeorgFilter.class.getName());
        SHELL_CLASSNAME_MAP.put(NeoreGeorgInterceptor.class.getSimpleName(), NeoreGeorgInterceptor.class.getName());
        Map<String, String> regeorgMap = new HashMap();
        regeorgMap.put(Constants.SHELL_FILTER, NeoreGeorgFilter.class.getSimpleName());
        regeorgMap.put(Constants.SHELL_LISTENER, NeoreGeorgListener.class.getSimpleName());
        regeorgMap.put(Constants.SHELL_INTERCEPTOR, NeoreGeorgInterceptor.class.getSimpleName());
        toolMap.put(Constants.TOOL_NEOREGEORG, regeorgMap);
    }
}
