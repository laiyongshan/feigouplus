package com.example.youhe.youhecheguanjiaplus.utils;

import java.security.NoSuchAlgorithmException;  
import java.security.Security;  
import java.util.Random;  
  
import javax.crypto.Cipher;  
import javax.crypto.KeyGenerator;  
import javax.crypto.SecretKey;  
import javax.crypto.SecretKeyFactory;  
import javax.crypto.spec.DESedeKeySpec;  
import javax.crypto.spec.IvParameterSpec;  
  
@SuppressWarnings("restriction")  
public class TripleDES {  
//    static {  
//        Security.addProvider(new com.sun.crypto.provider.SunJCE());  
//    }  

  
    private static final String MCRYPT_TRIPLEDES = "DESede";  
//    static String DES = "DES/ECB/NoPadding";  
//    static String TriDes = "DESede/ECB/NoPadding";  
    private static final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";  
//    private static final String TRANSFORMATION = "DESede/ECB/NoPadding";  

    /**
     * 解密
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MCRYPT_TRIPLEDES);  
        SecretKey sec = keyFactory.generateSecret(spec);  
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);  
//        IvParameterSpec IvParameters = new IvParameterSpec(iv);  
//        cipher.init(Cipher.DECRYPT_MODE, sec, IvParameters);  
        cipher.init(Cipher.DECRYPT_MODE, sec);  
        return cipher.doFinal(data);  
    }
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MCRYPT_TRIPLEDES);
        SecretKey sec = keyFactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, sec);
        return cipher.doFinal(data);
    }

    /**
     * 加密
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");  
        SecretKey sec = keyFactory.generateSecret(spec);  
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);  
//        IvParameterSpec IvParameters = new IvParameterSpec(iv);  
//        cipher.init(Cipher.ENCRYPT_MODE, sec, IvParameters);  
        cipher.init(Cipher.ENCRYPT_MODE, sec); 
        return cipher.doFinal(data);  
    }
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey sec = keyFactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//        IvParameterSpec IvParameters = new IvParameterSpec(iv);
//        cipher.init(Cipher.ENCRYPT_MODE, sec, IvParameters);
        cipher.init(Cipher.ENCRYPT_MODE, sec);
        return cipher.doFinal(data);
    }

    public static byte[] generateSecretKey() throws NoSuchAlgorithmException {  
        KeyGenerator keygen = KeyGenerator.getInstance(MCRYPT_TRIPLEDES);  
        return keygen.generateKey().getEncoded();  
    }  
  
    public static byte[] randomIVBytes() {  
        Random ran = new Random();  
        byte[] bytes = new byte[8];  
        for (int i = 0; i < bytes.length; ++i) {  
            bytes[i] = (byte) ran.nextInt(Byte.MAX_VALUE + 1);  
        }  
        return bytes;  
    }
    /**
     * 二行制转字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b!=null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}

    /**
     * 十六进制转 字节数组
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

//    public static void main(String args[]) throws Exception {
//        String plainText = "11223344556677888877665544332211221212211221221200";
////        final byte[] secretBytes = TripleDES.generateSecretKey();
//        final byte[] secretBytes = "112233445566778888776655".getBytes();
//
//        final byte[] ivbytes = TripleDES.randomIVBytes();
//        System.out.println("plain text: " + plainText);
//        System.out.println(plainText.length());
//        byte[] encrypt = TripleDES.encrypt(plainText.getBytes(), secretBytes, ivbytes);
//        System.out.println("cipher text: " + byte2hex(encrypt));
//        System.out.println("decrypt text: " + new String(TripleDES.decrypt(encrypt, secretBytes, ivbytes), "UTF-8"));
//    }
  
}  