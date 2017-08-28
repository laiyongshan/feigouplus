package com.example.youhe.youhecheguanjiaplus.utils; /**
 * union
 * version 1.0
 * data:2011-11-18
 * UnionDesEncrypt des及3des的加解密
 * Union3DesEncrypt
 */
import java.security.spec.KeySpec;  
import javax.crypto.Cipher;  
import javax.crypto.SecretKey;  
import javax.crypto.SecretKeyFactory;  
import javax.crypto.spec.DESKeySpec;  
import javax.crypto.spec.DESedeKeySpec;

public class UnionDes {

    static String DES = "DES/ECB/NoPadding";  
    static String TriDes = "DESede/ECB/NoPadding";  
    
    /**
     * 十六进制字符串转二进制
     * @param str
     * @return
     */
    public static byte[] hex2byte(String str) {
        int len = str.length();
        String stmp = null;
        byte bt[] = new byte[len / 2];
        for (int n = 0; n < len / 2; n++) {
            stmp = str.substring(n * 2, n * 2 + 2);
            bt[n] = (byte) (Integer.parseInt(stmp, 16));
        }
        
        return bt;
    }
    
    /**
     *二进制转十六进制字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + "";
            }
        }
        
        return hs;
    }

    /**
     * des加密
     * @param key 密钥
     * @param data 明文数据 16进制且长度为16的整数倍
     * @return  密文数据
     */
    public static byte[] UnionDesEncrypt(byte key[], byte data[]) {  
  
        try {  
            KeySpec ks = new DESKeySpec(key);  
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");  
            SecretKey ky = kf.generateSecret(ks);  
  
            Cipher c = Cipher.getInstance(DES);  
            c.init(Cipher.ENCRYPT_MODE, ky);  
            return c.doFinal(data);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }

    /**
     * des解密
     * @param key 密钥
     * @param data 密文数据 16进制且长度为16的整数倍
     * @return 明文数据
     */
    public static byte[] UnionDesDecrypt(byte key[], byte data[]) {  
  
        try {  
            KeySpec ks = new DESKeySpec(key);  
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");  
            SecretKey ky = kf.generateSecret(ks);  
  
            Cipher c = Cipher.getInstance(DES);  
            c.init(Cipher.DECRYPT_MODE, ky);  
            return c.doFinal(data);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }

    /**
     * 3des加密
     * @param key 密钥
     * @param data 明文数据 16进制且长度为16的整数倍
     * @return  密文数据
     */
    public static byte[] Union3DesEncrypt(byte key[], byte data[]) {  
        try {  
            byte[] k = new byte[24];  
  
            int len = data.length;  
            if(data.length % 8 != 0){  
                len = data.length - data.length % 8 + 8;  
            }  
            byte [] needData = null;  
            if(len != 0)  
                needData = new byte[len];  
              
            for(int i = 0 ; i< len ; i++){  
                needData[i] = 0x00;  
            }  
              
            System.arraycopy(data, 0, needData, 0, data.length);  
              
            if (key.length == 16) {  
                System.arraycopy(key, 0, k, 0, key.length);  
                System.arraycopy(key, 0, k, 16, 8);  
            } else {  
                System.arraycopy(key, 0, k, 0, 24);  
            }  
  
            KeySpec ks = new DESedeKeySpec(k);  
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");  
            SecretKey ky = kf.generateSecret(ks);  
  
            Cipher c = Cipher.getInstance(TriDes);  
            c.init(Cipher.ENCRYPT_MODE, ky);  
            return c.doFinal(needData);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
  
    }

    /**
     * 3des解密
     * @param key 密钥
     * @param data 密文数据 16进制且长度为16的整数倍
     * @return   明文数据
     */
    public static byte[] Union3DesDecrypt(byte key[], byte data[]) {  
        try {  
            byte[] k = new byte[24];  
  
            int len = data.length;  
            if(data.length % 8 != 0){  
                len = data.length - data.length % 8 + 8;  
            }  
            byte [] needData = null;  
            if(len != 0)  
                needData = new byte[len];  
              
            for(int i = 0 ; i< len ; i++){  
                needData[i] = 0x00;  
            }  
              
            System.arraycopy(data, 0, needData, 0, data.length);  
              
            if (key.length == 16) {  
                System.arraycopy(key, 0, k, 0, key.length);  
                System.arraycopy(key, 0, k, 16, 8);  
            } else {  
                System.arraycopy(key, 0, k, 0, 24);  
            }  
            KeySpec ks = new DESedeKeySpec(k);  
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");  
            SecretKey ky = kf.generateSecret(ks);  
  
            Cipher c = Cipher.getInstance(TriDes);  
            c.init(Cipher.DECRYPT_MODE, ky);  
            return c.doFinal(needData);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
  
    }
    /**
     * 数据解密
     * @param key 密钥 支持单倍和多倍密钥
     * @param data 密文数据 16进制且长度为16的整数倍
     * @return 明文数据
     */
    public static String UnionDecryptData(String key, String data)
    {
     if((key.length() != 16) && (key.length() != 32) && (key.length() != 48))
     {
      return(null);
     }
     if(data.length()%16 != 0)
     {
      return"";
     }
     int lenOfKey = 0;
     lenOfKey = key.length();
     String strEncrypt = "";
     byte sourData[] = hex2byte(data);
     switch(lenOfKey)
     {
     case 16:
      byte deskey8[] = hex2byte(key);      
      byte encrypt[] = UnionDesDecrypt(deskey8, sourData);
      strEncrypt = byte2hex(encrypt);
      break;
     case 32:
     case 48:
      String newkey1 = "";
      if(lenOfKey == 32)
      {
       String newkey = key.substring(0, 16);
       newkey1 = key+newkey;
      }else
      {
       newkey1 = key;
      }
      byte deskey24[] = hex2byte(newkey1);
      byte desEncrypt[] = Union3DesDecrypt(deskey24, sourData);
      strEncrypt = byte2hex(desEncrypt);
     }
     return strEncrypt;
    }

    /**
     * 加密数据
     * @param key 密钥 16进制且长度为16的整数倍
     * @param data 明文数据 16进制且长度为16的整数倍
     * @return  密文数据
     */
    public static String UnionEncryptData(String key, String data)
    {
     if((key.length() != 16) && (key.length() != 32) && (key.length() != 48))
     {
      return(null);
     }
     if(data.length()%16 != 0)
     {
      return"";
     }
     int lenOfKey = 0;
     lenOfKey = key.length();
     String strEncrypt = "";
     byte sourData[] = hex2byte(data);
     switch(lenOfKey)
     {
     case 16:
      byte deskey8[] = hex2byte(key);      
      byte encrypt[] = UnionDesEncrypt(deskey8, sourData);
      strEncrypt = byte2hex(encrypt);
      break;
     case 32:
     case 48:
      String newkey1 = "";
      if(lenOfKey == 32)
      {
       String newkey = key.substring(0, 16);
       newkey1 = key+newkey;
      }else
      {
       newkey1 = key;
      }
      byte deskey24[] = hex2byte(newkey1);
      byte desEncrypt[] = Union3DesEncrypt(deskey24, sourData);
      strEncrypt = byte2hex(desEncrypt);
      break;
     }
     return strEncrypt;
    }
    /**
     * 加密
     */
    public static String MyEncryptData(String key,String data) {
    	String dString="";
    	if (data.length()<10) {
    		dString="0"+data.length()+data;
		}else {
			dString=data.length()+data;
		}
    	String a="";
    	if (dString.length()<24) {
			for (int i = 0; i <24-dString.length(); i++) {
				a+="0";
			}
		}
//		else if (dString.length()<36) {
//            for (int i = 0; i <36-dString.length(); i++) {
//                a+="0";
//            }
//        }else if (dString.length()<48) {
//            for (int i = 0; i <48-dString.length(); i++) {
//                a+="0";
//            }
//        }

    	dString+=a;
//    	System.out.println(dString);
    	String hex=byte2hex(dString.getBytes());
//    	System.out.println(hex);
		return UnionEncryptData(key,hex);
	}
    /**
     * 解密
      */
    public static String MyUnionDecryptData(String key,String data) {
    	if (key==null||data==null||key==""||data=="") {
			return "";
		}
		String soure= new String(hex2byte(UnionDecryptData(key, data)));
		int number;
		if (soure.substring(0, 1)=="0") {
			 number=Integer.parseInt(soure.substring(1,1));
		}else {
			number=Integer.parseInt(soure.substring(0,2));
		}
		return soure.substring(2,2+number);
	}
   
//    public static void main(String[] args) {
//     String key = "112233445566778888776655443322112212122112212212";
//     String data ="55g@ggvv.6,6665650";
//
//
////     System.out.println(bytesToHexString(data.getBytes()));
////     System.out.println(new String(hexStringToBytes("353536")));
//////     System.out.println(byte2hex(("1121213").getBytes()));
////
//     String encData = MyEncryptData(key,data);
//     System.out.println(key);
//     System.out.println(MyEncryptData(key, data));
////
//     String sourData = MyUnionDecryptData(key,encData);
//     System.out.println(sourData);
//
//    }

   
}