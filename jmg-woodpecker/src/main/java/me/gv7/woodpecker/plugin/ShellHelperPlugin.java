package me.gv7.woodpecker.plugin;

import jmg.core.config.Constants;
import me.gv7.woodpecker.helper.*;

import java.util.ArrayList;
import java.util.List;

public class ShellHelperPlugin implements IHelperPlugin {
    public static IHelperPluginCallbacks callbacks;
    public static IPluginHelper pluginHelper;

    @Override
    public void HelperPluginMain(IHelperPluginCallbacks helperPluginCallbacks) {
        callbacks = helperPluginCallbacks;
        pluginHelper = callbacks.getPluginHelper();
        callbacks.setHelperPluginName("JMG Shell Helper");
        callbacks.setHelperPluginAutor(Constants.JMG_AUTHOR);
        callbacks.setHelperPluginVersion(Constants.JMG_VERSION);
        callbacks.setHelperPluginDescription("Java 内存马生成器");
        List<IHelper> helperList = new ArrayList<>();
        helperList.add(new AntSwordHelper());
        helperList.add(new BehinderHelper());
        helperList.add(new GodzillaHelper());
        helperList.add(new CustomHelper());
        callbacks.registerHelper(helperList);
    }
}
