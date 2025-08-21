package com.database.core.service;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.database.properties.DatabaseEncryptProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author xiongbing
 * @date 2025/2/27 13:59
 * @description
 */
@Service
public class Sm4CryptoService implements CryptoService {
    @Override
    public String encrypt(String plainText, DatabaseEncryptProperties config) {
        SymmetricCrypto symmetricCrypto = SmUtil.sm4(config.getSm4Key().getBytes(StandardCharsets.UTF_8));
        return symmetricCrypto.encryptBase64(plainText);
    }

    @Override
    public String decrypt(String cipherText,DatabaseEncryptProperties config) {
        SymmetricCrypto symmetricCrypto = SmUtil.sm4(config.getSm4Key().getBytes(StandardCharsets.UTF_8));
        return symmetricCrypto.decryptStr(cipherText);
    }
}
