package me.gv7.woodpecker.helper;


import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.InjectorGenerator;
import jmg.suo5.generator.Suo5Generator;
import me.gv7.woodpecker.plugin.IResultOutput;
import me.gv7.woodpecker.plugin.ProxyHelperPlugin;
import me.gv7.woodpecker.util.WoodpeckerResultUtil;

import java.util.Map;

public class Suo5Helper extends ProxyHelper {

    @Override
    public String getHelperTabCaption() {
        return Constants.TOOL_SUO5;
    }

    @Override
    public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {
        try {
            AbstractConfig config = initConfig(customArgs, new AbstractConfig());
            new Suo5Generator().makeShell(config);
            new InjectorGenerator().makeInjector(config);
            WoodpeckerResultUtil.printSuo5BasicInfo(resultOutput, config);
            WoodpeckerResultUtil.printResult(resultOutput, config);
            WoodpeckerResultUtil.printDebugInfo(resultOutput, config);
        } catch (Exception e) {
            resultOutput.errorPrintln(ProxyHelperPlugin.pluginHelper.getThrowableInfo(e));
        }
    }

}
