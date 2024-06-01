package jmg.core.format;


import jmg.core.config.AbstractConfig;

import java.io.IOException;
import java.util.Base64;

public class BASE64Formater implements IFormater {
    @Override
    public byte[] transform(byte[] clazzbyte, AbstractConfig config) throws IOException {
        Base64.Encoder base64Encoder = Base64.getEncoder();
        return new String(base64Encoder.encode(clazzbyte)).replace("\n", "").replace("\r", "").getBytes();
    }
}