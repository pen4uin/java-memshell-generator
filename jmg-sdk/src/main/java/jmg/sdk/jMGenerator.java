package jmg.sdk;

import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.InjectorGenerator;
import jmg.core.util.CommonUtil;
import jmg.sdk.util.ShellGenerator;


public class jMGenerator {

    private AbstractConfig config;

    public jMGenerator(AbstractConfig config) {
        this.config = config;

    }

    /**
     * 生成内存马字节流、注入器字节流
     */
    public void genPayload() throws Exception {
        new ShellGenerator().makeShell(this.config);
        new InjectorGenerator().makeInjector(this.config);
    }

    /**
     * 内存马字节流
     */
    public byte[] getShellBytes() {
        return this.config.getShellBytes();
    }

    /**
     * 注入器字节流
     */
    public byte[] getInjectorBytes() {
        return this.config.getInjectorBytes();
    }

    public void printPayload() {
        System.out.println("配置信息:");
        System.out.println(this.config.getToolType() + " " + this.config.getServerType() + " " + this.config.getShellType() + " " + this.config.getOutputFormat() + "\n");
        System.out.println("结果输出:");
        System.out.println(this.formatPayload());
        System.out.println();
    }

    /**
     * 处理注入器字节流
     */
    public String formatPayload() {
        switch (config.getOutputFormat()) {
            case Constants.FORMAT_CLASS:
            case Constants.FORMAT_JSP:
            case Constants.FORMAT_JAR:
            case Constants.FORMAT_JAR_AGENT:
                try {
                    CommonUtil.transformToFile(config);
                    return config.getSavePath();
                } catch (Throwable e) {
                }
                break;
            case Constants.FORMAT_BCEL:
            case Constants.FORMAT_JS:
            case Constants.FORMAT_BASE64:
            case Constants.FORMAT_BIGINTEGER:
                try {
                    return CommonUtil.transformTotext(config);
                } catch (Throwable e) {
                }
                break;
        }
        return "";
    }
}
