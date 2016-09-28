import org.apache.xerces.impl.dv.util.Base64;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DesUtil {

    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    public static final String CHARACTER_TYPE = "UTF-8";

    /**
     * 加密
     */
    public static String encode(String key, byte[] data) throws Exception {

        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

            byte[] bytes = cipher.doFinal(data);

            return Base64.encode(bytes);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 解密
     */
    public static byte[] decode(String key, byte[] data) throws Exception {

        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);

            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 解密
     */
    public static String decode(String key, String data) {

        byte[] bytes;
        String value;

        try {
            bytes = decode(key, Base64.decode(data));
            value = new String(bytes, CHARACTER_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
            value = "";
        }

        return value;
    }

    /**
     * 加密
     */
    public static String encode(String key, String data) throws Exception {

        return encode(key, data.getBytes(CHARACTER_TYPE));
    }
}
