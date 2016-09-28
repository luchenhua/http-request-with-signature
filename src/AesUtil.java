import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {

    /**
     * 注意key和加密用到的字符串是不一样的 加密还要指定填充的加密模式和填充模式 AES密钥可以是128或者256，加密模式包括ECB, CBC等
     * ECB模式是分组的模式，CBC是分块加密后，每块与前一块的加密结果异或后再加密 第一块加密的明文是与IV变量进行异或
     */
    private static final String KEY_ALGORITHM = "AES";
    private static final String CBC_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String ECB_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * IV(Initialization Value)是一个初始值，对于CBC模式来说，它必须是随机选取并且需要保密的
     * 而且它的长度和密码分组相同(比如：对于AES 128为128位，即长度为16的byte类型数组)
     */
    private static final byte[] IV_PARAMETERS = new byte[]{8, 91, 38, 88, 80, 115, 125, 25, 74, 66, 86, 97, 92, 102, 9, 14};
    private static final byte[] SECRET_BYTES = new byte[]{12, 112, 29, 83, 20, 92, 119, 49, 101, 43, 115, 42, 62, 70, 83, 21};

    public static String decryptAesCbc(String token) {

        SecretKey key = restoreSecretKey(SECRET_BYTES);

        BASE64Decoder base64Decoder = new BASE64Decoder();

        try {
            return decodeAesCbc(base64Decoder.decodeBuffer(token), key, IV_PARAMETERS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * AES ECB 解密
     */
    private static String decodeAesEcb(byte[] decodedText, SecretKey key) {

        try {
            Cipher cipher = Cipher.getInstance(ECB_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(decodedText), "UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * AES CBC 解密
     */
    private static String decodeAesCbc(byte[] decodedText, SecretKey key, byte[] IVParameter) {

        IvParameterSpec ivParameterSpec = new IvParameterSpec(IVParameter);

        try {
            Cipher cipher = Cipher.getInstance(CBC_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

            return new String(cipher.doFinal(decodedText), "UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException
                | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 还原密钥
     */
    private static SecretKey restoreSecretKey(byte[] secretBytes) {
        return new SecretKeySpec(secretBytes, KEY_ALGORITHM);
    }
}
