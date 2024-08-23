package jmg.custom.generator;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import jmg.core.config.AbstractConfig;
import jmg.core.generator.IShellGenerator;
import jmg.core.util.CommonUtil;

import javax.servlet.Filter;
import javax.servlet.ServletRequestListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class CustomGenerator implements IShellGenerator {
    @Override
    public void initShell(AbstractConfig config) {
        File f;
        try {
            f = new File(config.getClassFilePath());
            if (!f.exists() || !f.isFile()) {
                return;
            }
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(new ClassClassPath(Filter.class));
            classPool.insertClassPath(new ClassClassPath(ServletRequestListener.class));
            classPool.makeInterface("org.springframework.web.servlet.AsyncHandlerInterceptor");
            classPool.makeInterface("org.springframework.web.servlet.HandlerInterceptor");
            String filePath = config.getClassFilePath();
            CtClass ctClass = classPool.makeClass(new DataInputStream(new FileInputStream(filePath)));
            config.setShellClassName(ctClass.getName());
            ctClass.detach();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] makeShell(AbstractConfig config) throws Exception {
        initShell(config);
        byte[] bytes = CommonUtil.getFileBytes(config.getClassFilePath());
        config.setShellBytes(bytes);
        config.setShellBytesLength(bytes.length);
        config.setShellGzipBase64String(CommonUtil.encodeBase64(CommonUtil.gzipCompress(bytes)));
        return bytes;
    }

    @Override
    public byte[] modifyShell(String className, AbstractConfig config) {
        return null;
    }
}
