package jmg.extender.util;

import jmg.core.config.Constants;
import jmg.extender.detector.DFSEchoDetector;
import jmg.extender.detector.DNSLogDetector;
import jmg.extender.detector.HTTPLogDetector;
import jmg.extender.detector.SleepDetector;

import java.util.HashMap;
import java.util.Map;

public class DetectorUtil {

    private static final Map<String, String> DETECTOR_CLASSNAME_MAP = new HashMap();

    public DetectorUtil() {
    }

    public static String getDetectorClassName(String detectWay) throws Exception {
        if (DETECTOR_CLASSNAME_MAP.get(detectWay) == null) {
            throw new Exception("Invalid detect way  '" + detectWay + "'");
        } else {
            return DETECTOR_CLASSNAME_MAP.getOrDefault(detectWay, "");
        }
    }

    static {
        DETECTOR_CLASSNAME_MAP.put(Constants.DETECT_DFSECHO, DFSEchoDetector.class.getName());
        DETECTOR_CLASSNAME_MAP.put(Constants.DETECT_DNS, DNSLogDetector.class.getName());
        DETECTOR_CLASSNAME_MAP.put(Constants.DETECT_HTTP, HTTPLogDetector.class.getName());
        DETECTOR_CLASSNAME_MAP.put(Constants.DETECT_SLEEP, SleepDetector.class.getName());
    }
}
