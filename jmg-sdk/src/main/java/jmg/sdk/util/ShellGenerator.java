package jmg.sdk.util;

import jmg.antsword.generator.AntSwordGenerator;
import jmg.behinder.generator.BehinderGenerator;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.IShellGenerator;
import jmg.custom.generator.CustomGenerator;
import jmg.godzilla.generator.GodzillaGenerator;
import jmg.neoregeorg.generator.NeoreGeorgGenerator;
import jmg.suo5.generator.Suo5Generator;

public class ShellGenerator {
    IShellGenerator shellGenerator;

    public void makeShell(AbstractConfig config) throws Exception {
        switch (config.getToolType()) {
            case Constants.TOOL_ANTSWORD:
                shellGenerator = new AntSwordGenerator();
                break;
            case Constants.TOOL_BEHINDER:
                shellGenerator = new BehinderGenerator();
                break;
            case Constants.TOOL_GODZILLA:
                shellGenerator = new GodzillaGenerator();
                break;
            case Constants.TOOL_SUO5:
                shellGenerator = new Suo5Generator();
                break;
            case Constants.TOOL_NEOREGEORG:
                shellGenerator = new NeoreGeorgGenerator();
                break;
            case Constants.TOOL_CUSTOM:
                shellGenerator = new CustomGenerator();
                break;
            default:
                throw new IllegalArgumentException("Unsupported tool type: " + config.getToolType());
        }
        shellGenerator.makeShell(config);
    }
}
