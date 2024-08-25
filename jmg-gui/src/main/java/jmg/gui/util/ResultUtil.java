package jmg.gui.util;

import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.jMGCodeApi;
import jmg.core.util.CommonUtil;

public class ResultUtil {

    public static void printAntSwordBasicInfo(AbstractConfig config) {
        TextPaneUtil.successPrintln("基础信息:");
        TextPaneUtil.rawPrintln("");
        TextPaneUtil.rawPrintln("密码: " + config.getPass());
        TextPaneUtil.rawPrintln("请求路径: " + config.getUrlPattern());
        TextPaneUtil.rawPrintln("请求头: " + config.getHeaderName() + ": " + config.getHeaderValue());
        TextPaneUtil.rawPrintln("脚本类型: JSP");
        TextPaneUtil.rawPrintln("");

    }

    public static void printGodzillaBasicInfo(AbstractConfig config) {
        switch (config.getShellType()) {
            case Constants.SHELL_LISTENER:
            case Constants.SHELL_FILTER:
            case Constants.SHELL_INTERCEPTOR:
            case Constants.SHELL_JAKARTA_LISTENER:
            case Constants.SHELL_JAKARTA_FILTER:
                TextPaneUtil.successPrintln("基础信息:");
                TextPaneUtil.rawPrintln("");
                TextPaneUtil.rawPrintln("加密器: JAVA_AES_BASE64");
                TextPaneUtil.rawPrintln("密码: " + config.getPass());
                TextPaneUtil.rawPrintln("密钥: " + config.getKey());
                TextPaneUtil.rawPrintln("请求路径: " + config.getUrlPattern());
                TextPaneUtil.rawPrintln("请求头: " + config.getHeaderName() + ": " + config.getHeaderValue());
                TextPaneUtil.rawPrintln("脚本类型: JSP");
                TextPaneUtil.rawPrintln("");
                break;
            case Constants.SHELL_WF_HANDLERMETHOD:
                TextPaneUtil.successPrintln("基础信息:");
                TextPaneUtil.rawPrintln("");
                TextPaneUtil.rawPrintln("加密器: JAVA_AES_BASE64");
                TextPaneUtil.rawPrintln("密码: " + config.getPass());
                TextPaneUtil.rawPrintln("密钥: " + config.getKey());
                TextPaneUtil.rawPrintln("请求路径: " + config.getUrlPattern());
                TextPaneUtil.rawPrintln("");
                break;
        }
    }

    public static void printBehinderBasicInfo(AbstractConfig config) {
        TextPaneUtil.successPrintln("基础信息:");
        TextPaneUtil.rawPrintln("");
        TextPaneUtil.rawPrintln("密码: " + config.getPass());
        TextPaneUtil.rawPrintln("请求路径: " + config.getUrlPattern());
        TextPaneUtil.rawPrintln("请求头: " + config.getHeaderName() + ": " + config.getHeaderValue());
        TextPaneUtil.rawPrintln("脚本类型: JSP");
        TextPaneUtil.rawPrintln("内存马类名: " + config.getShellClassName());
        TextPaneUtil.rawPrintln("注入器类名: " + config.getInjectorClassName());
        TextPaneUtil.rawPrintln("");

    }

    public static void printSuo5BasicInfo(AbstractConfig config) {
        TextPaneUtil.successPrintln("基础信息:");
        TextPaneUtil.rawPrintln("");
        TextPaneUtil.rawPrintln("请求路径: " + config.getUrlPattern());
        TextPaneUtil.rawPrintln("连接指令:");
        if (config.getHeaderName().equalsIgnoreCase("user-agent")) {
            TextPaneUtil.rawPrintln(String.format("     ./suo5 -d --ua '%s' -t http://", config.getHeaderValue()));
            TextPaneUtil.rawPrintln(String.format("     ./suo5 -d -l 0.0.0.0:7788 --auth test:test123 --ua '%s' -t http://", config.getHeaderValue()));
        } else {
            TextPaneUtil.rawPrintln(String.format("     ./suo5 -H '%s: %s' -t http://", config.getHeaderName(), config.getHeaderValue()));
            TextPaneUtil.rawPrintln(String.format("     ./suo5 -l 0.0.0.0:7788 --auth test:test123 -H '%s: %s' -t http://", config.getHeaderName(), config.getHeaderValue()));
        }
        TextPaneUtil.rawPrintln("");
    }

    public static void printNeoreGeorgBasicInfo(AbstractConfig config) {
        TextPaneUtil.successPrintln("基础信息:");
        TextPaneUtil.rawPrintln("");
        TextPaneUtil.rawPrintln("密钥: " + config.getKey());
        TextPaneUtil.rawPrintln("请求路径: " + config.getUrlPattern());
        TextPaneUtil.rawPrintln("连接指令:");
        TextPaneUtil.rawPrintln(String.format("     python3 neoreg.py -k %s -H '%s:%s' -u http://", config.getKey(), config.getHeaderName(), config.getHeaderValue()));
        TextPaneUtil.rawPrintln(String.format("     python3 neoreg.py --skip --proxy http://127.0.0.1:8080 -vv -k %s -H '%s:%s' -u http:// ", config.getKey(), config.getHeaderName(), config.getHeaderValue()));
        TextPaneUtil.rawPrintln("");
    }

    public static void printDebugInfo(AbstractConfig config) {
        TextPaneUtil.successPrintln("调试信息:");
        TextPaneUtil.rawPrintln("");
        TextPaneUtil.rawPrintln("内存马类名: " + config.getShellClassName());
        TextPaneUtil.rawPrintln("注入器类名: " + config.getInjectorClassName());
        TextPaneUtil.rawPrintln("内存马字节流长度: " + config.getShellBytesLength());
        TextPaneUtil.rawPrintln("注入器字节流长度: " + config.getInjectorBytesLength());
        TextPaneUtil.rawPrintln("");
    }

    public static void resultOutput(AbstractConfig config) throws Throwable {
        switch (config.getToolType()) {
            case Constants.TOOL_ANTSWORD:
                printAntSwordBasicInfo(config);
                break;
            case Constants.TOOL_BEHINDER:
                printBehinderBasicInfo(config);
                break;
            case Constants.TOOL_GODZILLA:
                printGodzillaBasicInfo(config);
                break;
            case Constants.TOOL_SUO5:
                printSuo5BasicInfo(config);
                break;
            case Constants.TOOL_NEOREGEORG:
                printNeoreGeorgBasicInfo(config);
                break;
        }


        if (config.getExprEncoder() != null) {
            jMGCodeApi codeApi = new jMGCodeApi(config);
            codeApi.generate();
            String[] results = JExprUtil.genExprPayload(config);
            JExprUtil.printResult(results);
        } else {
            switch (config.getOutputFormat()) {
                case Constants.FORMAT_CLASS:
                case Constants.FORMAT_JSP:
                case Constants.FORMAT_JAR:
                case Constants.FORMAT_JAR_AGENT:
                    try {
                        CommonUtil.transformToFile(config);
                        TextPaneUtil.successPrintln("结果输出:\n");
                        TextPaneUtil.rawPrintln(config.getSavePath() + "\n");
                    } catch (Throwable e) {
                    }
                    break;
                case Constants.FORMAT_BCEL:
                case Constants.FORMAT_JS:
                case Constants.FORMAT_BASE64:
                case Constants.FORMAT_BIGINTEGER:
                    try {
                        String result = CommonUtil.transformTotext(config);
                        TextPaneUtil.successPrintln("结果输出:\n");
                        TextPaneUtil.rawPrintln(result + "\n");
                    } catch (Throwable e) {
                    }
                    break;
            }
        }

        printDebugInfo(config);
    }

}
