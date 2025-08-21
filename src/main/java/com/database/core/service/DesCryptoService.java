package com.database.core.service;

import com.database.core.util.CryptoUtils;
import com.database.properties.DatabaseEncryptProperties;
import org.springframework.stereotype.Service;

/**
 * @author xiongbing
 * @date 2025/2/27 13:59
 * @description
 */
@Service
public class DesCryptoService implements CryptoService {
    @Override
    public String encrypt(String plainText, DatabaseEncryptProperties config) {
        return CryptoUtils.encryptThreeDes(config.getDesKey(), plainText);
    }

    @Override
    public String decrypt(String cipherText,DatabaseEncryptProperties config) {
        return CryptoUtils.decryptThreeDes(config.getDesKey(), cipherText);
    }
}
