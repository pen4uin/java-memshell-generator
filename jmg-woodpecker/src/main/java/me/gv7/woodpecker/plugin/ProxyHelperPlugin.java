package me.gv7.woodpecker.plugin;


import jmg.core.config.Constants;
import me.gv7.woodpecker.helper.NeoreGeorgHelper;
import me.gv7.woodpecker.helper.Suo5Helper;

import java.util.ArrayList;
import java.util.List;

public class ProxyHelperPlugin implements IHelperPlugin {
    public static IHelperPluginCallbacks callbacks;
    public static IPluginHelper pluginHelper;

    @Override
    public void HelperPluginMain(IHelperPluginCallbacks helperPluginCallbacks) {
        callbacks = helperPluginCallbacks;
        pluginHelper = callbacks.getPluginHelper();
        callbacks.setHelperPluginName("JMG Proxy Helper");
        callbacks.setHelperPluginVersion(Constants.JMG_VERSION);
        callbacks.setHelperPluginAutor(Constants.JMG_AUTHOR);
        callbacks.setHelperPluginDescription("Java 内存马生成器 - 代理");
        List<IHelper> helperList = new ArrayList();
        helperList.add(new Suo5Helper());
        helperList.add(new NeoreGeorgHelper());

        callbacks.registerHelper(helperList);
    }
}
