package com.aidl.utils;


import java.io.ByteArrayOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**  
 *字符串 DESede(3DES) 加密  
 */ 

public class DES3Utils {  

	private static final String Algorithm = "DESede"; // 定义 加密算法,可用 DES,DESede,Blowfish  
	private static final String hexString = "0123456789ABCDEF";  

	/**  
	 *                                                     
	 * @param keybyte  加密密钥，长度为24字节  
	 * @param src     字节数组(根据给定的字节数组构造一个密钥。 )  
	 * @return  
	 */ 
	public static byte[] encryptMode(byte[] key, byte[] src) {  
		try {  
			if (key == null || key.length < 16 || src == null || src.length == 0) {
				return null;
			}
			
			byte[] keyBytes = new byte[24];
			System.arraycopy(key, 0, keyBytes, 0, 16);
			System.arraycopy(key, 0, keyBytes, 16, 8);
			
			// 根据给定的字节数组和算法构造一个密钥  
			SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);  
			// 加密  
			Cipher c1 = Cipher.getInstance(Algorithm);  
			c1.init(Cipher.ENCRYPT_MODE, deskey);  
			
			byte[] byRes = c1.doFinal(src);
			
			byte[] byResult = new byte[byRes.length-8];
			System.arraycopy(byRes, 0, byResult, 0, byResult.length);
			return byResult;  
		} catch (java.security.NoSuchAlgorithmException e1) {  
			e1.printStackTrace();  
		} catch (javax.crypto.NoSuchPaddingException e2) {  
			e2.printStackTrace();  
		} catch (Exception e3) {
			e3.printStackTrace();  
		}  

		return null;  
	}  

	/**  
	 *   
	 * @param keybyte 密钥  
	 * @param src       需要解密的数据  
	 * @return  
	 */ 
	public static byte[] decryptMode(byte[] key, byte[] src) {  
		try {  
			if (key == null || key.length < 16 || src == null || src.length < 16) {
				return null;
			}
			
			byte[] keyBytes = new byte[24];
			System.arraycopy(key, 0, keyBytes, 0, 16);
			System.arraycopy(key, 0, keyBytes, 16, 8);

			byte[] srcBytes = new byte[24];
			System.arraycopy(src, 0, srcBytes, 0, 16);
			byte[] end = { (byte)0xB5, 0x55, (byte)0xC1, 0x3E, 0x73, 0x4C, (byte)0xE7, 0x48 };
			System.arraycopy(end, 0, srcBytes, 16, 8);
			
			// 生成密钥  
			SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);  
			// 解密  
			Cipher c1 = Cipher.getInstance(Algorithm);  
			c1.init(Cipher.DECRYPT_MODE, deskey);  
			return c1.doFinal(srcBytes);  
		} catch (java.security.NoSuchAlgorithmException e1) {  
			e1.printStackTrace();  
		} catch (javax.crypto.NoSuchPaddingException e2) {  
			e2.printStackTrace();  
		} catch (Exception e3) {
			e3.printStackTrace();  
		}  

		return null;  
	}  

	/**  
	 * 字符串转为16进制  
	 * @param str  
	 * @return  
	 */ 

	public static String encode(String str)   
	{   
		//根据默认编码获取字节数组   
		byte[] bytes=str.getBytes();   
		StringBuilder sb=new StringBuilder(bytes.length*2);   

		//将字节数组中每个字节拆解成2位16进制整数   
		for(int i=0;i<bytes.length;i++)   
		{   
			sb.append(hexString.charAt((bytes[i]&0xf0)>>4));   
			sb.append(hexString.charAt((bytes[i]&0x0f)>>0));   
		}   

		return sb.toString();   
	}   

	/**  
	 *   
	 * @param bytes  
	 * @return  
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）   
	 */  
	public static String decode(String bytes)   
	{   
		ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2);   

		//将每2位16进制整数组装成一个字节   
		for(int i=0;i<bytes.length();i+=2)   
			baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));   

		return new String(baos.toByteArray());   
	}   

	// 转换成十六进制字符串  
	public static String byte2hex(byte[] b) {  
		String hs = "";  
		String stmp = "";  

		for (int n = 0; n < b.length; n++) {  
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)  
				hs = hs + "0" + stmp;  
			else 
				hs = hs + stmp;  
			if (n < b.length - 1)  
				hs = hs + ":";  
		}  
		return hs.toUpperCase();  
	} 

	/**
	 * 解密
	 * @param key  解密密钥key
	 * @param src  需要解密的字符串
	 * @return     解密后的字符串
	 */
	public static String decryptMode(String key ,String src){
		byte[] keyBytes = key.substring(0, 24).getBytes();
		byte[] decoded= decryptMode(keyBytes, parseHexStr2Byte(src));
		return new String(decoded);
	}

	/**将二进制转换成16进制 
	 * @param buf 
	 * @return 
	 */  
	public static String parseByte2HexStr(byte buf[]) {  
		StringBuffer sb = new StringBuffer();  
		for (int i = 0; i < buf.length; i++) {  
			String hex = Integer.toHexString(buf[i] & 0xFF);  
			if (hex.length() == 1) {  
				hex = '0' + hex;  
			}  
			sb.append(hex.toUpperCase());  
		}  
		return sb.toString();  
	}

	/**将16进制转换为二进制 
	 * @param hexStr 
	 * @return 
	 */  
	public static byte[] parseHexStr2Byte(String hexStr) {  
		if (hexStr.length() < 1)  
			return null;  
		byte[] result = new byte[hexStr.length()/2];  
		for (int i = 0;i< hexStr.length()/2; i++) {  
			int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
			int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
			result[i] = (byte) (high * 16 + low);  
		}  
		return result;  
	}    
} 
