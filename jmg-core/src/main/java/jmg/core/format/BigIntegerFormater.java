package jmg.core.format;


import jmg.core.config.AbstractConfig;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerFormater implements IFormater {
    @Override
    public byte[] transform(byte[] clazzbyte, AbstractConfig config) throws IOException {
        return new BigInteger(clazzbyte).toString(36).getBytes();
    }
}