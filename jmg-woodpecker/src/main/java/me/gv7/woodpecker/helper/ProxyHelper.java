package me.gv7.woodpecker.helper;

import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.util.ClassNameUtil;
import jmg.core.util.CommonUtil;
import jmg.core.util.RandomHttpHeaderUtil;
import me.gv7.woodpecker.plugin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pen4uin
 * @time 2024/8/22 22:55
 */

public class ProxyHelper implements IHelper {


    @Override
    public String getHelperTabCaption() {
        return null;
    }

    public IArgsUsageBinder getHelperCutomArgs() {
        IArgsUsageBinder binder = ProxyHelperPlugin.pluginHelper.createArgsUsageBinder();
        List<IArg> list = new ArrayList();
        IArg server_type = ProxyHelperPlugin.pluginHelper.createArg();
        server_type.setName("server_type");
        server_type.setType(7);
        List<String> enumServerType = new ArrayList();
        enumServerType.add(Constants.SERVER_TOMCAT);
        enumServerType.add(Constants.SERVER_SPRING_MVC);
        enumServerType.add(Constants.SERVER_JETTY);
        enumServerType.add(Constants.SERVER_RESIN);
        enumServerType.add(Constants.SERVER_WEBLOGIC);
        enumServerType.add(Constants.SERVER_WEBSPHERE);
        enumServerType.add(Constants.SERVER_UNDERTOW);
        enumServerType.add(Constants.SERVER_GLASSFISH);
        server_type.setEnumValue(enumServerType);
        server_type.setDefaultValue(Constants.SERVER_TOMCAT);
        server_type.setRequired(true);
        server_type.setDescription("自定义中间件");
        list.add(server_type);

        IArg shell_type = ProxyHelperPlugin.pluginHelper.createArg();
        shell_type.setName("shell_type");
        shell_type.setType(7);
        List<String> enumShellType = new ArrayList();
        enumShellType.add(Constants.SHELL_FILTER);
        enumShellType.add(Constants.SHELL_LISTENER);
        enumShellType.add(Constants.SHELL_INTERCEPTOR);
        enumShellType.add(Constants.SHELL_JAKARTA_LISTENER);
        enumShellType.add(Constants.SHELL_JAKARTA_FILTER);
        shell_type.setEnumValue(enumShellType);
        shell_type.setDefaultValue(Constants.SHELL_LISTENER);
        shell_type.setRequired(true);
        shell_type.setDescription("自定义内存马类型");
        list.add(shell_type);

        IArg gadgetType = ProxyHelperPlugin.pluginHelper.createArg();
        gadgetType.setName("gadget_type");
        gadgetType.setType(7);
        List<String> enumGadgetType = new ArrayList();
        enumGadgetType.add(Constants.GADGET_NONE);
        enumGadgetType.add(Constants.GADGET_JDK_TRANSLET);
        enumGadgetType.add(Constants.GADGET_XALAN_TRANSLET);
        enumGadgetType.add(Constants.GADGET_FJ_GROOVY);
        enumGadgetType.add(Constants.GADGET_SNAKEYAML);
        gadgetType.setEnumValue(enumGadgetType);
        gadgetType.setDefaultValue(Constants.GADGET_NONE);
        gadgetType.setRequired(true);
        gadgetType.setDescription("自定义利用链");
        list.add(gadgetType);

        IArg formatType = ProxyHelperPlugin.pluginHelper.createArg();
        formatType.setName("format_type");
        formatType.setType(7);
        List<String> enumFormattType = new ArrayList();
        enumFormattType.add(Constants.FORMAT_BASE64);
        enumFormattType.add(Constants.FORMAT_BIGINTEGER);
        enumFormattType.add(Constants.FORMAT_BCEL);
        enumFormattType.add(Constants.FORMAT_CLASS);
        enumFormattType.add(Constants.FORMAT_JAR);
        enumFormattType.add(Constants.FORMAT_JAR_AGENT);
        enumFormattType.add(Constants.FORMAT_JSP);
        formatType.setEnumValue(enumFormattType);
        formatType.setDefaultValue(Constants.FORMAT_BASE64);
        formatType.setRequired(true);
        formatType.setDescription("自定义输出格式");
        list.add(formatType);

        IArg shell_class_name = ProxyHelperPlugin.pluginHelper.createArg();
        shell_class_name.setName("shell_class_name");
        shell_class_name.setType(0);
        shell_class_name.setRequired(false);
        shell_class_name.setDescription("自定义内存马类名");
        list.add(shell_class_name);
        IArg injector_class_name = ProxyHelperPlugin.pluginHelper.createArg();
        injector_class_name.setName("injector_class_name");
        injector_class_name.setType(0);
        injector_class_name.setRequired(false);
        injector_class_name.setDescription("自定义注入器类名");
        list.add(injector_class_name);
        IArg output_path = ProxyHelperPlugin.pluginHelper.createArg();
        output_path.setName("output_path");
        output_path.setType(0);
        output_path.setDefaultValue("workdir");
        output_path.setRequired(false);
        output_path.setDescription("自定义输出路径");
        list.add(output_path);

        IArg enable_bypass_jdk_module = ShellHelperPlugin.pluginHelper.createArg();
        enable_bypass_jdk_module.setName("bypass_jdk_module");
        enable_bypass_jdk_module.setType(7);
        List<String> enumenableBypass = new ArrayList();
        enumenableBypass.add(String.valueOf(false));
        enumenableBypass.add(String.valueOf(true));
        enable_bypass_jdk_module.setEnumValue(enumenableBypass);
        enable_bypass_jdk_module.setDefaultValue(String.valueOf(false));
        enable_bypass_jdk_module.setRequired(false);
        enable_bypass_jdk_module.setDescription("绕过高版本 JDK Module 访问限制");
        list.add(enable_bypass_jdk_module);
        binder.setArgsList(list);
        return binder;
    }


    @Override
    public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {

    }

    public AbstractConfig initConfig(Map<String, Object> customArgs, AbstractConfig config) {
        config.setToolType(getHelperTabCaption());
        config.setServerType((String) customArgs.get("server_type"));
        config.setShellType((String) customArgs.get("shell_type"));
        config.setOutputFormat((String) customArgs.get("format_type"));
        config.setGadgetType((String) customArgs.get("gadget_type"));

        // 用户自定义
        if (customArgs.get("url_pattern") != null) config.setUrlPattern((String) customArgs.get("url_pattern"));
        if (customArgs.get("shell_class_name") != null)
            config.setShellClassName((String) customArgs.get("shell_class_name"));
        if (customArgs.get("injector_class_name") != null)
            config.setInjectorClassName((String) customArgs.get("injector_class_name"));
        if (customArgs.get("output_path") != null) config.setSavePath((String) customArgs.get("output_path"));

        // 无自定义则随机生成
        Map.Entry<String, String> header = RandomHttpHeaderUtil.generateHeader();
        if (config.getHeaderName() == null) config.setHeaderName(header.getKey());
        if (config.getHeaderValue() == null) config.setHeaderValue(header.getValue());

        if (config.getUrlPattern() == null) config.setUrlPattern("/*");
        if (config.getSavePath() == null) config.setSavePath(System.getProperty("user.dir"));
        if (config.getInjectorClassName() == null)
            config.setInjectorClassName(ClassNameUtil.getRandomInjectorClassName());
        if (config.getInjectorSimpleClassName() == null)
            config.setInjectorSimpleClassName(CommonUtil.getSimpleName(config.getInjectorClassName()));
        if (config.getShellClassName() == null)
            config.setShellClassName(ClassNameUtil.getRandomShellClassName(config.getShellType()));
        if (config.getShellSimpleClassName() == null)
            config.setShellSimpleClassName(CommonUtil.getSimpleName(config.getShellClassName()));
        if (customArgs.get("bypass_jdk_module") != null) config.setEnableBypassJDKModule(Boolean.parseBoolean((String) customArgs.get("bypass_jdk_module")));

        config.setSavePath(CommonUtil.getFileOutputPath(config.getOutputFormat(), config.getInjectorSimpleClassName(), config.getSavePath()));

        return config;
    }
}
