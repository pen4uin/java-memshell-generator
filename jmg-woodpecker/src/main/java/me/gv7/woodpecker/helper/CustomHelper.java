package me.gv7.woodpecker.helper;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.InjectorGenerator;
import jmg.core.util.ClassNameUtil;
import jmg.core.util.CommonUtil;
import jmg.custom.generator.CustomGenerator;
import me.gv7.woodpecker.plugin.ShellHelperPlugin;
import me.gv7.woodpecker.plugin.*;
import me.gv7.woodpecker.util.WoodpeckerResultUtil;
import javax.servlet.Filter;
import javax.servlet.ServletRequestListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomHelper implements IHelper {
    @Override
    public String getHelperTabCaption() {
        return Constants.TOOL_CUSTOM;
    }

    @Override
    public IArgsUsageBinder getHelperCutomArgs() {
        IArgsUsageBinder binder = ShellHelperPlugin.pluginHelper.createArgsUsageBinder();
        List<IArg> list = new ArrayList();

        IArg server_type = ShellHelperPlugin.pluginHelper.createArg();
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

        IArg shell_type = ShellHelperPlugin.pluginHelper.createArg();
        shell_type.setName("shell_type");
        shell_type.setType(7);
        List<String> enumShellType = new ArrayList();
        enumShellType.add(Constants.SHELL_LISTENER);
        enumShellType.add(Constants.SHELL_FILTER);
        enumShellType.add(Constants.SHELL_INTERCEPTOR);
        enumShellType.add(Constants.SHELL_JAKARTA_LISTENER);
        enumShellType.add(Constants.SHELL_JAKARTA_FILTER);
        shell_type.setEnumValue(enumShellType);

        IArg gadgetType = ShellHelperPlugin.pluginHelper.createArg();
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


        IArg formatType = ShellHelperPlugin.pluginHelper.createArg();
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

        IArg class_file_path = ShellHelperPlugin.pluginHelper.createArg();
        class_file_path.setName("class_file_path");
        class_file_path.setDefaultValue("/tmp/woo.class");
        class_file_path.setDescription("自定义内存马文件(.class)");
        class_file_path.setRequired(true);
        list.add(class_file_path);

        IArg injector_class_name = ShellHelperPlugin.pluginHelper.createArg();
        injector_class_name.setName("injector_class_name");
        injector_class_name.setType(0);
        injector_class_name.setRequired(false);
        injector_class_name.setDescription("自定义注入器类名");
        list.add(injector_class_name);
        IArg url_pattern = ShellHelperPlugin.pluginHelper.createArg();

        url_pattern.setName("url_pattern");
        url_pattern.setType(0);
        url_pattern.setDefaultValue("/*");
        url_pattern.setRequired(false);
        url_pattern.setDescription("自定义请求路径");
        list.add(url_pattern);

        IArg output_path = ShellHelperPlugin.pluginHelper.createArg();
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
        try {

            AbstractConfig config = new AbstractConfig();
            config.setToolType(Constants.TOOL_CUSTOM);
            config.setServerType((String) customArgs.get("server_type"));
            config.setShellType((String) customArgs.get("shell_type"));
            config.setOutputFormat((String) customArgs.get("format_type"));
            config.setGadgetType((String) customArgs.get("gadget_type"));
            if (customArgs.get("url_pattern") != null) config.setUrlPattern((String) customArgs.get("url_pattern"));
            if (customArgs.get("shell_class_name") != null)
                config.setShellClassName((String) customArgs.get("shell_class_name"));
            if (customArgs.get("injector_class_name") != null)
                config.setInjectorClassName((String) customArgs.get("injector_class_name"));
            if (customArgs.get("output_path") != null) config.setSavePath((String) customArgs.get("output_path"));
            if (customArgs.get("class_file_path") != null)
                config.setClassFilePath((String) customArgs.get("class_file_path"));
            if (config.getInjectorClassName() == null)
                config.setInjectorClassName(ClassNameUtil.getRandomInjectorClassName());
            if (config.getUrlPattern() == null) config.setUrlPattern("/*");
            config.setInjectorSimpleClassName(CommonUtil.getSimpleName(config.getInjectorClassName()));
            if (config.getSavePath() == null) config.setSavePath(System.getProperty("user.dir"));
            if (customArgs.get("bypass_jdk_module") != null) config.setEnableBypassJDKModule(Boolean.parseBoolean((String) customArgs.get("bypass_jdk_module")));

            config.setSavePath(CommonUtil.getFileOutputPath(config.getOutputFormat(), config.getInjectorSimpleClassName(), config.getSavePath()));
            File f;
            try {
                f = new File(config.getClassFilePath());
                if (!f.exists() || !f.isFile()) {
                    resultOutput.warningPrintln(ShellHelperPlugin.pluginHelper.getThrowableInfo(new Throwable("File does not exist or is not a regular file: " + config.getClassFilePath())));
                    return;
                }
                ClassPool classPool = ClassPool.getDefault();
                classPool.insertClassPath(new ClassClassPath(Filter.class));
                classPool.insertClassPath(new ClassClassPath(ServletRequestListener.class));
                classPool.makeInterface("org.springframework.web.servlet.AsyncHandlerInterceptor");
                classPool.makeInterface("org.springframework.web.servlet.HandlerInterceptor");
                String filePath = config.getClassFilePath();
                CtClass ctClass = classPool.makeClass(new DataInputStream(new FileInputStream(filePath)));
                // Bug: 修复 custom 生成的注入器 shellClassName 为空导致注入失败的问题
                config.setShellClassName(ctClass.getName());
                if (isFilterClass(ctClass)) {
                    config.setShellType("Filter");
                } else if (isListenerClass(ctClass)) {
                    config.setShellType("Listener");
                } else if (isInterceptorClass(ctClass)) {
                    config.setShellType("Interceptor");
                } else {
                    resultOutput.warningPrintln(filePath + " is neither a Filter nor a Listener.");
                    return;
                }

            } catch (Exception e) {
                resultOutput.warningPrintln(ShellHelperPlugin.pluginHelper.getThrowableInfo(new Throwable("File does not exist or is not a regular file: " + config.getClassFilePath())));
            }
            new CustomGenerator().makeShell(config);
            new InjectorGenerator().makeInjector(config);
            WoodpeckerResultUtil.printCustomBasicInfo(resultOutput, config);
            WoodpeckerResultUtil.printResult(resultOutput, config);
        } catch (Exception e) {
            resultOutput.errorPrintln(ShellHelperPlugin.pluginHelper.getThrowableInfo(e));
        }
    }


    public static boolean isFilterClass(CtClass ctClass) throws NotFoundException, IOException {
        CtClass[] interfaces = ctClass.getInterfaces();
        CtClass[] var2 = interfaces;
        int var3 = interfaces.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            CtClass intf = var2[var4];
            if (intf.getName().equals("javax.servlet.Filter")) {
                return true;
            }
        }

        return false;
    }

    public static boolean isListenerClass(CtClass ctClass) throws NotFoundException, IOException {
        CtClass[] interfaces = ctClass.getInterfaces();
        CtClass[] var2 = interfaces;
        int var3 = interfaces.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            CtClass intf = var2[var4];
            if (intf.getName().equals("javax.servlet.ServletRequestListener")) {
                return true;
            }
        }

        return false;
    }

    public static boolean isInterceptorClass(CtClass ctClass) throws NotFoundException, IOException {
        CtClass[] interfaces = ctClass.getInterfaces();
        CtClass[] var2 = interfaces;
        int var3 = interfaces.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            CtClass intf = var2[var4];
            if (intf.getName().contains("Interceptor")) {
                return true;
            }
        }

        return false;
    }
}