package com.database.core.service;

import com.database.core.util.RsaUtil;
import com.database.properties.DatabaseEncryptProperties;
import org.springframework.stereotype.Service;

/**
 * @author xiongbing
 * @date 2025/2/27 13:59
 * @description
 */
@Service
public class RsaCryptoService implements CryptoService {
    @Override
    public String encrypt(String plainText, DatabaseEncryptProperties config) {
        return RsaUtil.encrypt(plainText,config.getRsaPublicKey());
    }

    @Override
    public String decrypt(String cipherText,DatabaseEncryptProperties config) {
        return RsaUtil.decrypt(cipherText,config.getRsaPrivateKey());
    }
}
