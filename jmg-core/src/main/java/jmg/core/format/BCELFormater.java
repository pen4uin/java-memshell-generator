package jmg.core.format;

import jmg.core.config.AbstractConfig;
import me.gv7.woodpecker.bcel.HackBCELs;

import java.io.IOException;

public class BCELFormater implements IFormater {


    public byte[] transform(byte[] clazzbyte, AbstractConfig config) throws IOException {
        // 解决 BCEL 的classloader 的问题
        byte[] bcelClzBytes = BCELoaderGenerator.generatorBCELoaderClass(config);
        return HackBCELs.encode(bcelClzBytes).getBytes();
    }
}
