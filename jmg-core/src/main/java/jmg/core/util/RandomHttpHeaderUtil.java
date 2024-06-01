package jmg.core.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;


public class RandomHttpHeaderUtil {

    private static final Random RANDOM = new Random();

    public static Map.Entry<String, String> generateHeader() {
        String key = generateRandomKey();
        String value = generateRandomValue(key);
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private static String generateRandomKey() {
        String[] keys = {"Referer","User-Agent"};
        return keys[RANDOM.nextInt(keys.length)];
    }

    private static String generateRandomValue(String key) {
        switch (key) {
            case "Referer":
            case "User-Agent":
                return generateRandomValue();
            default:
                return "";
        }
    }

    private static String generateRandomValue() {
        return CommonUtil.genRandomLengthString(4);
    }

}
