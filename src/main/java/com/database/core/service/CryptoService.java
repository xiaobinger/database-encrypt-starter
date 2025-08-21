package com.database.core.service;


import com.database.properties.DatabaseEncryptProperties;

/**
 * @author xiongbing
 * @date 2025/2/27 13:55
 * @description
 */
public interface CryptoService {
    String encrypt(String plainText, DatabaseEncryptProperties config);
    String decrypt(String cipherText, DatabaseEncryptProperties config);
}
