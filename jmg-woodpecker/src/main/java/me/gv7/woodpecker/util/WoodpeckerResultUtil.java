package me.gv7.woodpecker.util;

import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.util.CommonUtil;
import me.gv7.woodpecker.plugin.IResultOutput;
import me.gv7.woodpecker.plugin.ShellHelperPlugin;

import java.io.IOException;

public class WoodpeckerResultUtil {

    public static void printAntSwordBasicInfo(IResultOutput resultOutput, AbstractConfig config) {
        resultOutput.successPrintln("基础信息:");
        resultOutput.rawPrintln("");
        resultOutput.rawPrintln("密码: " + config.getPass());
        resultOutput.rawPrintln("请求路径: " + config.getUrlPattern());
        resultOutput.rawPrintln("请求头: " + config.getHeaderName() + ": " + config.getHeaderValue());
        resultOutput.rawPrintln("脚本类型: JSP");
        resultOutput.rawPrintln("");
    }

    public static void printBehinderBasicInfo(IResultOutput resultOutput, AbstractConfig config) {
        resultOutput.successPrintln("基础信息:");
        resultOutput.rawPrintln("");
        resultOutput.rawPrintln("密码: " + config.getPass());
        resultOutput.rawPrintln("请求路径: " + config.getUrlPattern());
        resultOutput.rawPrintln("请求头: " + config.getHeaderName() + ": " + config.getHeaderValue());
        resultOutput.rawPrintln("脚本类型: JSP");
        resultOutput.rawPrintln("");
    }

    public static void printGodzillaBasicInfo(IResultOutput resultOutput, AbstractConfig config) {
        resultOutput.successPrintln("基础信息:");
        resultOutput.rawPrintln("");
        resultOutput.rawPrintln("加密器: JAVA_AES_BASE64");
        resultOutput.rawPrintln("密码: " + config.getPass());
        resultOutput.rawPrintln("密钥: " + config.getKey());
        resultOutput.rawPrintln("请求路径: " + config.getUrlPattern());
        resultOutput.rawPrintln("请求头: " + config.getHeaderName() + ":" + config.getHeaderValue());
        resultOutput.rawPrintln("");
    }


    public static void printCustomBasicInfo(IResultOutput resultOutput, AbstractConfig config) {
        resultOutput.rawPrintln("基础信息");
        resultOutput.rawPrintln("");
        resultOutput.rawPrintln("请求路径: " + config.getUrlPattern());
        resultOutput.rawPrintln("注入器类名: " + config.getInjectorClassName());
        resultOutput.rawPrintln("");

    }

    public static void printSuo5BasicInfo(IResultOutput resultOutput, AbstractConfig config) {
        resultOutput.successPrintln("基础信息:");
        resultOutput.rawPrintln("");
        resultOutput.rawPrintln("请求路径: " + config.getUrlPattern());
        resultOutput.rawPrintln("连接指令:");
        if (config.getHeaderName().equalsIgnoreCase("user-agent")){
            resultOutput.rawPrintln(String.format("     ./suo5 -d --ua '%s' -t http://", config.getHeaderValue()));
            resultOutput.rawPrintln(String.format("     ./suo5 -d -l 0.0.0.0:7788 --auth test:test123 --ua '%s' -t http://", config.getHeaderValue()));
        }else {
            resultOutput.rawPrintln(String.format("     ./suo5 -H '%s: %s' -t http://", config.getHeaderName(), config.getHeaderValue()));
            resultOutput.rawPrintln(String.format("     ./suo5 -l 0.0.0.0:7788 --auth test:test123 -H '%s: %s' -t http://", config.getHeaderName(), config.getHeaderValue()));
        }
        resultOutput.rawPrintln("");
    }

    public static void printNeoreGeorgBasicInfo(IResultOutput resultOutput, AbstractConfig config) {
        resultOutput.successPrintln("基础信息:");
        resultOutput.rawPrintln("");
        resultOutput.rawPrintln("密钥: " + config.getKey());
        resultOutput.rawPrintln("请求路径: " + config.getUrlPattern());
        resultOutput.rawPrintln("连接指令:");
        resultOutput.rawPrintln(String.format("     python3 neoreg.py -k %s -H '%s:%s' -u http://", config.getKey(), config.getHeaderName(), config.getHeaderValue()));
        resultOutput.rawPrintln(String.format("     python3 neoreg.py --skip --proxy http://127.0.0.1:8080 -vv -k %s -H '%s:%s' -u http:// ", config.getKey(), config.getHeaderName(), config.getHeaderValue()));
        resultOutput.rawPrintln("");
    }

    public static void printExtenderBasicInfo(IResultOutput resultOutput, AbstractConfig config) {
        resultOutput.successPrintln("基础信息:");
        resultOutput.rawPrintln("");
        resultOutput.rawPrintln("探测器类名: " + config.getExtenderClassName());
        resultOutput.rawPrintln("探测器字节流长度: " + config.getExtenderBytes().length);
        resultOutput.rawPrintln("");
    }

    public static void printBasicInfo(IResultOutput resultOutput, AbstractConfig config) {
        try {
            resultOutput.warningPrintln("内存马注入器通过静态代码块和构造函数均可触发");
            resultOutput.warningPrintln(String.format("判断内存马注入成功的标志: 不带body携带对应的请求头访问，若response返回的cookie有[%s]键值对说明注入成功", config.getPass().toUpperCase()));
            resultOutput.warningPrintln("内存马连接路径尽量使用应用系统存在的路径");
            resultOutput.rawPrintln("------------------------------------------------------------------------------------------------");
        } catch (Exception ignored) {
        }
    }

    public static void printResult(IResultOutput resultOutput, AbstractConfig config) {
        if (!config.isEnabledExtender() && config.getInjectorBytesLength() == 0) {
            resultOutput.warningPrintln("请在非调试模式下使用!");
        } else if (config.isEnabledExtender() && config.getExtenderBytesLength() == 0) {
            resultOutput.warningPrintln("请在非调试模式下使用!");
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
                        resultOutput.successPrintln("结果输出:\n");
                        resultOutput.rawPrintln(config.getSavePath() + "\n");
                    } catch (Throwable e) {
                        resultOutput.errorPrintln(ShellHelperPlugin.pluginHelper.getThrowableInfo(e));
                    }
                    break;
                case Constants.FORMAT_BCEL:
                case Constants.FORMAT_JS:
                case Constants.FORMAT_BASE64:
                case Constants.FORMAT_BIGINTEGER:
                    try {
                        String result = CommonUtil.transformTotext(config);
                        resultOutput.successPrintln("结果输出:\n");
                        resultOutput.rawPrintln(result + "\n");
                    } catch (Throwable e) {
                        resultOutput.errorPrintln(ShellHelperPlugin.pluginHelper.getThrowableInfo(e));
                    }
                    break;
            }
        }
    }

    public static void printDebugInfo(IResultOutput resultOutput, AbstractConfig config) throws IOException {
        resultOutput.successPrintln("调试信息:");
        resultOutput.rawPrintln("");
        resultOutput.rawPrintln("内存马类名: " + config.getShellClassName());
        resultOutput.rawPrintln("注入器类名: " + config.getInjectorClassName());
        resultOutput.rawPrintln("内存马字节流长度: " + config.getShellBytesLength());
        resultOutput.rawPrintln("注入器字节流长度: " + config.getInjectorBytesLength());
        resultOutput.rawPrintln("");
    }
}
