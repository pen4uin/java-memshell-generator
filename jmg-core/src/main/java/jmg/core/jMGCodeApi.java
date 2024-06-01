package jmg.core;


import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.format.*;

public class jMGCodeApi {
    AbstractConfig config;

    public jMGCodeApi(AbstractConfig config) {
        this.config = config;
    }

    public byte[] generate() throws Throwable {
        byte[] clazzBytes;
        clazzBytes = config.getInjectorBytes();
        if (clazzBytes == null) {
            return null;
        }

        // 格式转换
        byte[] bytes = null;
        switch (config.getOutputFormat()) {
            case Constants.FORMAT_BCEL:
                bytes = new BCELFormater().transform(clazzBytes, config);
                break;
            case Constants.FORMAT_JSP:
                bytes = new JSPFormater().transform(clazzBytes, config);
                break;
            case Constants.FORMAT_JAR:
                bytes = new JARFormater().transform(clazzBytes, config);
                break;
            case Constants.FORMAT_JAR_AGENT:
                bytes = new JARAgentFormater().transform(clazzBytes, config);
                break;
            case Constants.FORMAT_JS:
                bytes = new JavaScriptFormater().transform(clazzBytes, config);
                break;
            case Constants.FORMAT_BASE64:
                bytes = new BASE64Formater().transform(clazzBytes, config);
                break;
            case Constants.FORMAT_BIGINTEGER:
                bytes = new BigIntegerFormater().transform(clazzBytes, config);
                break;
            default:
                bytes = clazzBytes;
                break;
        }
        return bytes;
    }
}
