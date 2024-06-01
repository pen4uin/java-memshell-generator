package jmg.core.generator;

import javassist.ClassPool;
import jmg.core.config.AbstractConfig;

public interface IShellGenerator {
    ClassPool pool = ClassPool.getDefault();

    void initShell(AbstractConfig config);

    byte[] makeShell(AbstractConfig config) throws Exception;

    byte[] modifyShell(String className, AbstractConfig config);
}
