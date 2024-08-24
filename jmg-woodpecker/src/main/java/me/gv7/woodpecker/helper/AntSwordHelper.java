package me.gv7.woodpecker.helper;


import jmg.antsword.generator.AntSwordGenerator;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.InjectorGenerator;
import me.gv7.woodpecker.plugin.IResultOutput;
import me.gv7.woodpecker.plugin.ShellHelperPlugin;
import me.gv7.woodpecker.util.WoodpeckerResultUtil;

import java.util.Map;

public class AntSwordHelper extends ShellHelper {

    @Override
    public String getHelperTabCaption() {
        return Constants.TOOL_ANTSWORD;
    }

    @Override
    public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {
        try {
            AbstractConfig config = initConfig(customArgs);
            new AntSwordGenerator().makeShell(config);
            new InjectorGenerator().makeInjector(config);
            WoodpeckerResultUtil.printAntSwordBasicInfo(resultOutput, config);
            WoodpeckerResultUtil.printResult(resultOutput, config);
            WoodpeckerResultUtil.printDebugInfo(resultOutput, config);
        } catch (Exception e) {
            resultOutput.errorPrintln(ShellHelperPlugin.pluginHelper.getThrowableInfo(e));
        }
    }
}