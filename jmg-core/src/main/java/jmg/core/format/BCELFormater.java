package jmg.core.format;

import jmg.core.config.AbstractConfig;
import jmg.core.util.ClassNameUtil;
import me.gv7.woodpecker.bcel.HackBCELs;

import java.io.IOException;

public class BCELFormater implements IFormater {


    public byte[] transform(byte[] clazzbyte, AbstractConfig config) throws IOException {
        // 解决 BCEL ClassLoader 带来的问题
        if (config.isEnabledExtender()){
            config.setBytesInLoader(config.getExtenderBytes());
            config.setClassNameInLoader(config.getExtenderClassName());
        }else{
            config.setBytesInLoader(config.getInjectorBytes());
            config.setClassNameInLoader(config.getInjectorClassName());
        }
        config.setLoaderClassName(ClassNameUtil.getRandomLoaderClassName());
        byte[] bcelClzBytes = BCELoaderGenerator.generatorBCELoaderClass(config);
        return HackBCELs.encode(bcelClzBytes).getBytes();
    }
}
