package jmg.core.util;



import jmg.core.config.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ClassNameUtil {
    static String[] injectorClassNames = new String[]{"SignatureUtils", "NetworkUtils", "KeyUtils", "EncryptionUtils", "SessionDataUtil", "SOAPUtils", "ReflectUtil", "HttpClientUtil", "EncryptionUtil", "XMLUtil", "JSONUtil", "FileUtils", "DateUtil", "StringUtil", "MathUtil", "HttpUtil", "CSVUtil", "ImageUtil", "ThreadUtil", "ReportUtil", "EncodingUtil", "ConfigurationUtil", "HTMLUtil", "SerializationUtil"};
    static String[] prefixNames = new String[]{"AbstractMatcher", "WebSocketUpgrade", "Session", "WhiteBlackList", "Log4jConfig", "SecurityHandler", "ContextLoader", "ServletContext", "ServletContextAttribute", "ServletRequest"};


    public static String getRandomName(String[]... arrays) {
        List<String> classNames = new ArrayList<>();
        for (String[] array : arrays) {
            for (String className : array) {
                classNames.add(className);
            }
        }
        Random random = new Random();
        int index = random.nextInt(classNames.size());
        return classNames.get(index);
    }


    public static String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(2) + 1; // 生成1-3之间的随机数
        for (int i = 0; i < length; i++) {
            char c = (char) (random.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getRandomInjectorClassName(){

        return PackageNameUtil.getRandomPackageName() + "." + generateRandomString() + "." + ClassNameUtil.getRandomName(injectorClassNames);
    }

    public static String getRandomExtenderClassName(){

        return PackageNameUtil.getRandomPackageName() + "." + generateRandomString() + "." + ClassNameUtil.getRandomName(injectorClassNames);
    }

    public static String getRandomLoaderClassName(){

        return PackageNameUtil.getRandomPackageName() + "." + generateRandomString() + "." + ClassNameUtil.getRandomName(injectorClassNames);
    }


    public static String getClassPrefixName(){
        return ClassNameUtil.getRandomName(prefixNames);
    }

    public static String getRandomShellClassName(String shellType) {

        if (shellType.contains(Constants.SHELL_LISTENER)){
            return PackageNameUtil.getRandomPackageName() + "." + ClassNameUtil.getClassPrefixName() + CommonUtil.generateRandomString()  + "Listener";
        }
        if (shellType.contains(Constants.SHELL_INTERCEPTOR)){
            return PackageNameUtil.getRandomPackageName() + "." + ClassNameUtil.getClassPrefixName() + CommonUtil.generateRandomString()  + "Interceptor";
        }
        if (shellType.contains(Constants.SHELL_WF_HANDLERMETHOD)){
            return PackageNameUtil.getRandomPackageName() + "." + ClassNameUtil.getClassPrefixName() + CommonUtil.generateRandomString()  + "Handler";
        }
        return PackageNameUtil.getRandomPackageName() + "." + ClassNameUtil.getClassPrefixName() + CommonUtil.generateRandomString() + "Filter";
    }
}
