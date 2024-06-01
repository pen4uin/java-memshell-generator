package jmg.gui.util;

import me.gv7.woodpecker.plugin.exprs.*;
import jmg.core.config.AbstractConfig;

public class JExprUtil {
    public static String[] genExprPayload(AbstractConfig config){
        byte[] bytes = config.getInjectorBytes();
        switch (config.getExprEncoder()){
            case "EL":
                return new ELExpr().genMemShell(bytes);
            case "FreeMarker":
                return new FreeMarkerExpr().genMemShell(bytes);
            case "OGNL":
                return new OGNLExpr().genMemShell(bytes);
            case "SpEL":
                return new SpELExpr().genMemShell(bytes);
            case "Velocity":
                return new VelocityExpr().genMemShell(bytes);
            case "ScriptEngineManager(JS)":
                return new ScriptEngineManagerExpr().genMemShell(bytes);
        }
        return null;
    }

    public static void printResult(String[] results) throws Exception {
        if (results != null && results.length > 0) {
            String[] var3 = results;
            int var4 = results.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String result = var3[var5];
                TextPaneUtil.successPrintln(result);
            }
        } else {
            TextPaneUtil.warningPrintln("暂不支持\n");
        }

    }
}