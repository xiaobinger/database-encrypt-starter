package com.database.core.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
/**
 * @author xiongbing
 * @date 2025/2/27 15:21
 * @description
 */


public class CryptoUtils {
    // AES加密算法参数
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    // 初始化向量长度
    private static final int IV_LENGTH = 16;

    //3DES加密算法参数
    private static final String THREE_DES_ALGORITHM = "DESede/CBC/PKCS5Padding";
    private static final String THREE_DES = "DESede";
    // 初始化向量长度
    private static final int THREE_DES_IV_LENGTH = 8;

    /**
     * AES加密
     * @param key 密钥（任意长度，内部会做SHA-256处理）
     * @param plainText 明文
     * @return Base64编码的加密结果（包含IV）
     */
    public static String encryptAes(String key, String plainText) {
        try {
            // 生成密钥（MD5处理）
            byte[] keyBytes = MessageDigest.getInstance("MD5")
                    .digest(key.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES);
            return encrypt(plainText, secretKey, ALGORITHM, IV_LENGTH);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES解密
     * @param key 密钥
     * @param encryptedText Base64编码的加密字符串（包含IV）
     * @return 解密后的明文
     */
    public static String decryptAes(String key, String encryptedText) {
        try {
            // 生成密钥
            byte[] keyBytes = MessageDigest.getInstance("MD5")
                    .digest(key.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES);
            return decrypt(encryptedText, secretKey, ALGORITHM, IV_LENGTH);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * 3DES加密
     * @param key 密钥
     * @param plainText 明文
     * @return Base64编码的加密结果（包含IV）
     */
    public static String encryptThreeDes(String key, String plainText) {
        try {
            // 生成符合规范的3DES密钥
            byte[] keyBytes = processKey(key);
            DESedeKeySpec spec = new DESedeKeySpec(keyBytes);
            SecretKey secretKey = SecretKeyFactory.getInstance(THREE_DES).generateSecret(spec);
            return encrypt(plainText, secretKey, THREE_DES_ALGORITHM, THREE_DES_IV_LENGTH);
        } catch (Exception e) {
            throw new RuntimeException("3DES加密失败", e);
        }
    }

    /**
     * 3DES解密
     * @param key 密钥
     * @param encryptedText Base64编码的加密字符串（包含IV）
     * @return 解密后的明文
     */
    public static String decryptThreeDes(String key, String encryptedText) {
        try {
            // 生成符合规范的3DES密钥
            byte[] keyBytes = processKey(key);
            DESedeKeySpec spec = new DESedeKeySpec(keyBytes);
            SecretKey secretKey = SecretKeyFactory.getInstance(THREE_DES).generateSecret(spec);
            return decrypt(encryptedText, secretKey, THREE_DES_ALGORITHM, THREE_DES_IV_LENGTH);
        } catch (Exception e) {
            throw new RuntimeException("3DES解密失败", e);
        }
    }

    private static String encrypt(String plainText, SecretKey secretKey,String algorithm,int ivLength) throws Exception{
        // 生成随机IV
        byte[] ivBytes = new byte[ivLength];
        java.security.SecureRandom.getInstanceStrong().nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        // 加密
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        // 合并IV和加密数据
        byte[] combined = new byte[ivBytes.length + encrypted.length];
        System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
        System.arraycopy(encrypted, 0, combined, ivBytes.length, encrypted.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    private static String decrypt(String encryptedText, SecretKey secretKey,String algorithm,int ivLength) throws Exception{
        // 解码Base64
        byte[] combined = Base64.getDecoder().decode(encryptedText);
        // 分离IV和加密数据
        byte[] ivBytes = new byte[ivLength];
        byte[] encryptedBytes = new byte[combined.length - ivLength];
        System.arraycopy(combined, 0, ivBytes, 0, ivBytes.length);
        System.arraycopy(combined, ivBytes.length, encryptedBytes, 0, encryptedBytes.length);

        // 解密
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted, StandardCharsets.UTF_8);

    }

    /**
     * 密钥处理（自动补全到24字节）
     * 3DES要求密钥长度必须为24字节（实际有效位168位）
     */
    private static byte[] processKey(String key) throws Exception {
        byte[] inputBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(inputBytes);
        byte[] result = new byte[24];

        // 取前24字节（若不足则循环填充）
        for (int i = 0; i < 24; i++) {
            result[i] = hash[i % hash.length];
        }
        return result;
    }
}
