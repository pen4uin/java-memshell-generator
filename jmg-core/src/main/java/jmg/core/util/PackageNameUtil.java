package jmg.core.util;

import java.util.Random;

public class PackageNameUtil {

    private static final String[] packageNames = {
            "org.springframework",
            "org.apache.commons",
            "org.apache.logging",
            "org.apache",
            "com.fasterxml.jackson",
            "org.junit",
            "org.apache.commons.lang",
            "org.apache.http.client",
            "com.google.gso",
            "ch.qos.logback"
    };

    public static String generatePackageName() {
        Random random = new Random();
        String packageName = packageNames[random.nextInt(packageNames.length)];
        return packageName;
    }


    public static String getRandomPackageName() {
        return generatePackageName();
    }
}
