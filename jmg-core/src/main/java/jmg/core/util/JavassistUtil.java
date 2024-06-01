package jmg.core.util;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;

import java.util.List;

/**
 * javassist 工具类
 */
public class JavassistUtil {

    private static ClassPool pool = ClassPool.getDefault();

    public static void addMethod(CtClass ctClass, String methodName, String methodBody) throws Exception {
        ctClass.defrost();
        try {
            // 已存在，修改
            CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
            ctMethod.setBody(methodBody);
        } catch (NotFoundException ignored) {
            // 不存在，直接添加
            CtMethod method = CtNewMethod.make(methodBody, ctClass);
            ctClass.addMethod(method);
        }
    }


    public static void addField(CtClass ctClass, String fieldName, String fieldValue) throws Exception {
        ctClass.defrost();
        try {
            // 已存在，删除
            CtField field = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(field);
            // ctClass.addField(CtField.make(String.format("private static String %s =  \"%s\";", fieldName, fieldValue), ctClass));
            try {
                CtField defField = new CtField(pool.getCtClass("java.lang.String"), fieldName, ctClass);
                defField.setModifiers(Modifier.PUBLIC);
                ctClass.addField(defField, "\"" + fieldValue + "\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (NotFoundException ignored) {
//            // 不存在，直接添加
//            ctClass.addField(CtField.make(String.format("private static String %s =  \"%s\";", fieldName, fieldValue), ctClass));

            try {
                CtField defField = new CtField(pool.getCtClass("java.lang.String"), fieldName, ctClass);
                defField.setModifiers(Modifier.STATIC);
                ctClass.addField(defField, "\"" + fieldValue + "\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addStaticField(CtClass ctClass, String fieldName, String fieldValue) throws Exception {
        ctClass.defrost();
        try {
            // 已存在，删除
            CtField field = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(field);
            // ctClass.addField(CtField.make(String.format("private static String %s =  \"%s\";", fieldName, fieldValue), ctClass));
            try {
                CtField defField = new CtField(pool.getCtClass("java.lang.String"), fieldName, ctClass);
                defField.setModifiers(Modifier.PUBLIC);
                defField.setModifiers(Modifier.STATIC);
                ctClass.addField(defField, "\"" + fieldValue + "\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (NotFoundException ignored) {
//            // 不存在，直接添加
//            ctClass.addField(CtField.make(String.format("private static String %s =  \"%s\";", fieldName, fieldValue), ctClass));

            try {
                CtField defField = new CtField(pool.getCtClass("java.lang.String"), fieldName, ctClass);
                defField.setModifiers(Modifier.STATIC);
                ctClass.addField(defField, "\"" + fieldValue + "\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void extendClass(CtClass ctClass, String superClassName) throws Exception {
        ctClass.defrost();
        CtClass interfaceClass = pool.makeClass(superClassName);
        ctClass.setSuperclass(pool.get(interfaceClass.getName()));
    }

    public static void implementInterface(CtClass ctClass, String interfaceClassName) throws Exception {
        ctClass.defrost();

        CtClass interfaceClass = pool.makeInterface(interfaceClassName);
        CtClass[] ctClasses = new CtClass[]{interfaceClass};
        ctClass.setInterfaces(ctClasses);
    }


    public static void addAnnotation(CtClass ctClass, String interfaceClassName) throws Exception {
        ctClass.defrost();
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute clazzAnnotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation clazzAnnotation = new Annotation(convertClassNameToFilePath(interfaceClassName), constPool);
        clazzAnnotationsAttribute.setAnnotation(clazzAnnotation);
        ctClass.getClassFile().addAttribute(clazzAnnotationsAttribute);
    }


    // 删除内存马 SourceFileAttribute (源文件名) 信息
    public static void removeSourceFileAttribute(CtClass ctClass) {
        ctClass.defrost();
        ClassFile classFile = ctClass.getClassFile2();

        try {
            // javassist.bytecode.ClassFile.removeAttribute  Since: 3.21
            CommonUtil.invokeMethod(classFile, "removeAttribute", new Class[]{String.class}, new Object[]{SourceFileAttribute.tag});
        } catch (Exception e) {
            try {
                // 兼容 javassist v3.20 及以下
                List<AttributeInfo> attributes = (List<AttributeInfo>) CommonUtil.getFV(classFile, "attributes");
                removeAttribute(attributes, SourceFileAttribute.tag);
            } catch (Exception ignored) {
            }
        }
    }


    public static synchronized AttributeInfo removeAttribute(List<AttributeInfo> attributes, String name) {
        if (attributes == null) return null;

        for (AttributeInfo ai : attributes)
            if (ai.getName().equals(name)) if (attributes.remove(ai)) return ai;

        return null;
    }


    public static void addFieldIfNotNull(CtClass ctClass, String fieldName, String fieldValue) throws Exception {
        if (fieldValue != null) {
            JavassistUtil.addField(ctClass, fieldName, fieldValue);
        }
    }

    public static void addStaticFieldIfNotNull(CtClass ctClass, String fieldName, String fieldValue) throws Exception {
        if (fieldValue != null) {
            JavassistUtil.addStaticField(ctClass, fieldName, fieldValue);
        }
    }

    public static void setNameIfNotNull(CtClass ctClass, String className) throws Exception {
        if (className != null) {
            ctClass.setName(className);
        }
    }

    public static String convertClassNameToFilePath(String className) {
        return className.replace(".", "/");
    }

}
