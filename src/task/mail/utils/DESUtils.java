package task.mail.utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class DESUtils {
	// 密钥
	private static Key key;
	// KEY种子
	private static String KEY_STR = "encrypt@cncounter.com";
	// 常量
	public static final String UTF_8 = "UTF-8";
	public static final String DES = "DES";

	// 静态初始化
	static {
		try {
			// KEY 生成器
			KeyGenerator generator = KeyGenerator.getInstance(DES);
			// 初始化,安全随机算子
			generator.init(new SecureRandom(KEY_STR.getBytes(UTF_8)));
			// 生成密钥
			key = generator.generateKey();
			generator = null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对源字符串加密,返回 BASE64编码后的加密字符串
	 * 
	 * @param source
	 *            源字符串,明文
	 * @return 密文字符串
	 */
	public static String encode(String source) {
		try {
			// 根据编码格式获取字节数组
			byte[] sourceBytes = source.getBytes(UTF_8);
			// DES 加密模式
			Cipher cipher = Cipher.getInstance(DES);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			// 加密后的字节数组
			byte[] encryptSourceBytes = cipher.doFinal(sourceBytes);
			// Base64编码器
			BASE64Encoder base64Encoder = new BASE64Encoder();
			return base64Encoder.encode(encryptSourceBytes);
		} catch (Exception e) {
			// throw 也算是一种 return 路径
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对本工具类 encode() 方法加密后的字符串进行解码/解密
	 * 
	 * @param encrypted
	 *            被加密过的字符串,即密文
	 * @return 明文字符串
	 */
	public static String decode(String encrypted) {
		// Base64解码器
		BASE64Decoder base64Decoder = new BASE64Decoder();
		try {
			// 先进行base64解码
			byte[] cryptedBytes = base64Decoder.decodeBuffer(encrypted);
			// DES 解密模式
			Cipher cipher = Cipher.getInstance(DES);
			cipher.init(Cipher.DECRYPT_MODE, key);
			// 解码后的字节数组
			byte[] decryptStrBytes = cipher.doFinal(cryptedBytes);
			// 采用给定编码格式将字节数组变成字符串
			return new String(decryptStrBytes, UTF_8);
		} catch (Exception e) {
			// 这种形式确实适合处理工具类
			throw new RuntimeException(e);
		}
	}
	
	 /** 
     * md5或者sha-1加密 
     * @param inputText  要加密的内容 
     * @param algorithmName  加密算法名称：md5或者sha-1，不区分大小写 
     * @return 
     */  
    private static String encrypt(String inputText, String algorithmName) {  
        if (inputText == null || "".equals(inputText.trim())) {  
            throw new IllegalArgumentException("请输入要加密的内容");  
        }  
        if (algorithmName == null || "".equals(algorithmName.trim())) {  
            algorithmName = "md5";  
        }  
        String encryptText = null;  
        try {  
            MessageDigest m = MessageDigest.getInstance(algorithmName);  
            m.update(inputText.getBytes("UTF8"));  
            byte s[] = m.digest();  
            // m.digest(inputText.getBytes("UTF8"));  
            return hex(s);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        return encryptText;  
    }  
  
    // 返回十六进制字符串  
    private static String hex(byte[] arr) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < arr.length; ++i) {  
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1,3));  
        }  
        return sb.toString();  
    } 
	
	// md5加密  
    public static String md5(String inputText) {  
        return encrypt(inputText, "md5");  
    }  
  
    // sha加密  
    public static String sha(String inputText) {  
        return encrypt(inputText, "sha-1");  
    }  

	// 单元测试
	public static void main(String[] args) {
		// 需要加密的字符串
		String email = "study00x#l";
		// 加密
		String encrypted = encode(email);
		// 解密
		String decrypted = DESUtils.decode(encrypted);
		// 输出结果;
		System.out.println("email: " + email);
		System.out.println(sha(encrypted));
		System.out.println("encrypted: " + encrypted);
		System.out.println("decrypted: " + decrypted);
		System.out.println(decrypted.substring(0, decrypted.length()-3));
		
	}

}
