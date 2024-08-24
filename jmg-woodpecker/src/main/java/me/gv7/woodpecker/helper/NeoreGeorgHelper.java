package me.gv7.woodpecker.helper;


import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.InjectorGenerator;
import jmg.neoregeorg.generator.NeoreGeorgGenerator;
import me.gv7.woodpecker.plugin.IResultOutput;
import me.gv7.woodpecker.plugin.ProxyHelperPlugin;
import me.gv7.woodpecker.util.WoodpeckerResultUtil;

import java.util.Map;

public class NeoreGeorgHelper extends ProxyHelper {

    @Override
    public String getHelperTabCaption() {
        return Constants.TOOL_NEOREGEORG;
    }

    @Override
    public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {
        try {
            AbstractConfig config = initConfig(customArgs, new AbstractConfig());
            config.setKey("neorgkey");
            new NeoreGeorgGenerator().makeShell(config);
            new InjectorGenerator().makeInjector(config);
            WoodpeckerResultUtil.printNeoreGeorgBasicInfo(resultOutput, config);
            WoodpeckerResultUtil.printResult(resultOutput, config);
            WoodpeckerResultUtil.printDebugInfo(resultOutput, config);
        } catch (Exception e) {
            resultOutput.errorPrintln(ProxyHelperPlugin.pluginHelper.getThrowableInfo(e));
        }
    }

}
