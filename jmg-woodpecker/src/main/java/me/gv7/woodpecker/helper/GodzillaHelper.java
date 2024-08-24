package me.gv7.woodpecker.helper;

import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.InjectorGenerator;
import jmg.godzilla.generator.GodzillaGenerator;
import me.gv7.woodpecker.plugin.IResultOutput;
import me.gv7.woodpecker.plugin.ShellHelperPlugin;
import me.gv7.woodpecker.util.WoodpeckerResultUtil;

import java.util.Map;

public class GodzillaHelper extends ShellHelper {

    @Override
    public String getHelperTabCaption() {
        return Constants.TOOL_GODZILLA;
    }

    @Override
    public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {
        try {
            AbstractConfig config = initConfig(customArgs);
            new GodzillaGenerator().makeShell(config);
            new InjectorGenerator().makeInjector(config);
            WoodpeckerResultUtil.printGodzillaBasicInfo(resultOutput, config);
            WoodpeckerResultUtil.printResult(resultOutput, config);
            WoodpeckerResultUtil.printDebugInfo(resultOutput, config);
        } catch (Exception e) {
            resultOutput.errorPrintln(ShellHelperPlugin.pluginHelper.getThrowableInfo(e));
        }
    }
}