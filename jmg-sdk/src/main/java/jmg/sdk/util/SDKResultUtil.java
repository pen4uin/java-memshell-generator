package jmg.sdk.util;

import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.util.CommonUtil;

import java.io.IOException;

public class SDKResultUtil {

    public static void printAntSwordBasicInfo(AbstractConfig config) {
        System.out.println("基础信息:");
        System.out.println("");
        System.out.println("密码: " + config.getPass());
        System.out.println("请求路径: " + config.getUrlPattern());
        System.out.println("请求头: " + config.getHeaderName() + ": " + config.getHeaderValue());
        System.out.println("脚本类型: JSP");
        System.out.println("");
    }

    public static void printBehinderBasicInfo(AbstractConfig config) {
        System.out.println("基础信息:");
        System.out.println("");
        System.out.println("密码: " + config.getPass());
        System.out.println("请求路径: " + config.getUrlPattern());
        System.out.println("请求头: " + config.getHeaderName() + ": " + config.getHeaderValue());
        System.out.println("脚本类型: JSP");
        System.out.println("");
    }

    public static void printGodzillaBasicInfo(AbstractConfig config) {
        System.out.println("基础信息:");
        System.out.println("");
        System.out.println("加密器: JAVA_AES_BASE64");
        System.out.println("密码: " + config.getPass());
        System.out.println("密钥: " + config.getKey());
        System.out.println("请求路径: " + config.getUrlPattern());
        System.out.println("请求头: " + config.getHeaderName() + ":" + config.getHeaderValue());
        System.out.println("");
    }


    public static void printCustomBasicInfo(AbstractConfig config) {
        System.out.println("基础信息");
        System.out.println("");
        System.out.println("请求路径: " + config.getUrlPattern());
        System.out.println("注入器类名: " + config.getInjectorClassName());
        System.out.println("");

    }

    public static void printSuo5BasicInfo(AbstractConfig config) {
        System.out.println("基础信息:");
        System.out.println("");
        System.out.println("请求路径: " + config.getUrlPattern());
        System.out.println("连接指令:");
        if (config.getHeaderName().equalsIgnoreCase("user-agent")) {
            System.out.println(String.format("     ./suo5 -d --ua '%s' -t http://", config.getHeaderValue()));
            System.out.println(String.format("     ./suo5 -d -l 0.0.0.0:7788 --auth test:test123 --ua '%s' -t http://", config.getHeaderValue()));
        } else {
            System.out.println(String.format("     ./suo5 -H '%s: %s' -t http://", config.getHeaderName(), config.getHeaderValue()));
            System.out.println(String.format("     ./suo5 -l 0.0.0.0:7788 --auth test:test123 -H '%s: %s' -t http://", config.getHeaderName(), config.getHeaderValue()));
        }
        System.out.println("");
    }

    public static void printNeoreGeorgBasicInfo(AbstractConfig config) {
        System.out.println("基础信息:");
        System.out.println("");
        System.out.println("密钥: " + config.getKey());
        System.out.println("请求路径: " + config.getUrlPattern());
        System.out.println("连接指令:");
        System.out.println(String.format("     python3 neoreg.py -k %s -H '%s:%s' -u http://", config.getKey(), config.getHeaderName(), config.getHeaderValue()));
        System.out.println(String.format("     python3 neoreg.py --skip --proxy http://127.0.0.1:8080 -vv -k %s -H '%s:%s' -u http:// ", config.getKey(), config.getHeaderName(), config.getHeaderValue()));
        System.out.println("");
    }

    public static void printExtenderBasicInfo(AbstractConfig config) {
        System.out.println("基础信息:");
        System.out.println("");
        System.out.println("探测器类名: " + config.getExtenderClassName());
        System.out.println("探测器字节流长度: " + config.getExtenderBytes().length);
        System.out.println("");
    }

    public static void printResult(AbstractConfig config) {
        if (!config.isEnabledExtender() && config.getInjectorBytesLength() == 0) {
            System.out.println("请在非调试模式下使用!");
        } else if (config.isEnabledExtender() && config.getExtenderBytesLength() == 0) {
            System.out.println("请在非调试模式下使用!");
        } else {
            switch (config.getOutputFormat()) {
                case Constants.FORMAT_CLASS:
                case Constants.FORMAT_JSP:
                case Constants.FORMAT_JAR_AGENT:
                case Constants.FORMAT_JAR:
                    try {
                        if (config.isEnabledExtender()) {
                            CommonUtil.transformExtenderToFile(config);
                        } else {
                            CommonUtil.transformToFile(config);
                        }
                        System.out.println("结果输出:\n");
                        System.out.println(config.getSavePath() + "\n");
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.FORMAT_BCEL:
                case Constants.FORMAT_JS:
                case Constants.FORMAT_BASE64:
                case Constants.FORMAT_BIGINTEGER:
                    try {
                        String result = CommonUtil.transformTotext(config);
                        System.out.println("结果输出:\n");
                        System.out.println(result + "\n");
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public static void printBasicInfo(AbstractConfig config) throws Throwable {
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

    }

    public static void printDebugInfo(AbstractConfig config) throws IOException {
        System.out.println("调试信息:");
        System.out.println("");
        System.out.println("内存马类名: " + config.getShellClassName());
        System.out.println("注入器类名: " + config.getInjectorClassName());
        System.out.println("内存马字节流长度: " + config.getShellBytesLength());
        System.out.println("注入器字节流长度: " + config.getInjectorBytesLength());
        System.out.println("");
    }
}
