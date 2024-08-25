package jmg.core.util;


// https://github.com/BeichenDream/Kcon2021Code/tree/master/bypassJdk
public class JDKBypassUtil {

    public static String bypassJDKModuleBody() throws Exception {
        return "{try {\n" +
                "            Class unsafeClass = Class.forName(\"sun.misc.Unsafe\");\n" +
                "            java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField(\"theUnsafe\");\n" +
                "            unsafeField.setAccessible(true);\n" +
                "            Object unsafe = unsafeField.get(null);\n" +
                "            java.lang.reflect.Method getModuleM = Class.class.getMethod(\"getModule\", new Class[0]);\n" +
                "            Object module = getModuleM.invoke(Object.class, (Object[]) null);\n" +
                "            java.lang.reflect.Method objectFieldOffsetM = unsafe.getClass().getMethod(\"objectFieldOffset\", new Class[]{java.lang.reflect.Field.class});\n" +
                "            java.lang.reflect.Field moduleF = Class.class.getDeclaredField(\"module\");\n" +
                "            Object offset = objectFieldOffsetM.invoke(unsafe, new Object[]{moduleF});\n" +
                "            java.lang.reflect.Method getAndSetObjectM = unsafe.getClass().getMethod(\"getAndSetObject\", new Class[]{Object.class, long.class, Object.class});\n" +
                "            getAndSetObjectM.invoke(unsafe, new Object[]{this.getClass(), offset, module});\n" +
                "        } catch (Exception ignored) {\n" +
                "        }}";
    }
}
