package me.gv7.woodpecker.helper;

import jmg.behinder.generator.BehinderGenerator;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.InjectorGenerator;
import me.gv7.woodpecker.plugin.IResultOutput;
import me.gv7.woodpecker.plugin.ShellHelperPlugin;
import me.gv7.woodpecker.util.JMGResultUtil;

import java.util.Map;

public class BehinderHelper extends ShellHelper {


    @Override
    public String getHelperTabCaption() {
        return Constants.TOOL_BEHINDER;
    }

    @Override
    public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {
        try {
            AbstractConfig config = initConfig(customArgs);
            new BehinderGenerator().makeShell(config);
            new InjectorGenerator().makeInjector(config);
            JMGResultUtil.printBehinderBasicInfo(resultOutput, config);
            JMGResultUtil.printResult(resultOutput, config);
            JMGResultUtil.printDebugInfo(resultOutput, config);
        } catch (Exception e) {
            resultOutput.errorPrintln(ShellHelperPlugin.pluginHelper.getThrowableInfo(e));
        }
    }
}