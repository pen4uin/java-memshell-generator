package jmg.core.format;


import jmg.core.config.AbstractConfig;

import java.io.IOException;

public interface IFormater {
    public byte[] transform(byte[] clazzbyte, AbstractConfig config) throws Exception;
}
