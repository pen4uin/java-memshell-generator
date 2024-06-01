package jmg.behinder.util;

import jmg.behinder.memshell.BehinderFilter;
import jmg.behinder.memshell.BehinderInterceptor;
import jmg.behinder.memshell.BehinderListener;
import jmg.core.config.Constants;

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
        SHELL_CLASSNAME_MAP.put(BehinderListener.class.getSimpleName(), BehinderListener.class.getName());
        SHELL_CLASSNAME_MAP.put(BehinderFilter.class.getSimpleName(), BehinderFilter.class.getName());
        SHELL_CLASSNAME_MAP.put(BehinderInterceptor.class.getSimpleName(), BehinderInterceptor.class.getName());

        Map<String, String> behinderMap = new HashMap();
        behinderMap.put(Constants.SHELL_FILTER, BehinderFilter.class.getSimpleName());
        behinderMap.put(Constants.SHELL_LISTENER, BehinderListener.class.getSimpleName());
        behinderMap.put(Constants.SHELL_INTERCEPTOR, BehinderInterceptor.class.getSimpleName());
        toolMap.put(Constants.TOOL_BEHINDER, behinderMap);

    }


}
