package com.database.core.util;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author luohuiwen
 * @date 2024/3/4 19:56
 * Rsa加解密工具类
 */
public class RsaUtil {
    /**
     *通过base64密文和base64私钥进行解密
     * @param plainText 密文
     * @return  返回明文
     */
    public static String decrypt(String plainText,String privateKeyStr) {
        try {
            // 解码 Base64 编码的密文和密钥为字节数组
            byte[] plainTextBytes = Base64.getDecoder().decode(plainText);
            byte[] privateKeyData = Base64.getDecoder().decode(privateKeyStr);
            // 创建私钥对象
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyData);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            // 创建 Cipher 对象并解密密文
            // 使用 RSA 的 PKCS1 填充模式
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(plainTextBytes);
            // 将解密后的字节数组转换为字符串
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    /**
     * 明文通过公钥加密，返回base64的密文
     * @param decryptedText 明文字段
     * @return 返回base64密文
     */
    public static String encrypt(String decryptedText,String publicKeyStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            // 创建 X509EncodedKeySpec 对象
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            // 根据 X509EncodedKeySpec 对象生成 PublicKey 对象
            // 这里使用 RSA 算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            // 创建 Cipher 对象并加密明文
            // 使用 RSA 的 PKCS1 填充模式
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(decryptedText.getBytes());
            // 将加密后的字节数组转换为base64的字符串
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }

    }
}
