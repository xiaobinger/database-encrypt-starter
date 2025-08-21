package com.database.core.service;

import lombok.Getter;

import java.util.Objects;

/**
 * @author xiongbing
 * @date 2025/2/27 14:00
 * @description
 */
@Getter
public enum EncryptType {
    SM4(1, "sm4",new Sm4CryptoService()),
    AES(2, "aes",new AesCryptoService()),
    RSA(3, "rsa",new RsaCryptoService()),
    DES(4, "des",new DesCryptoService()),
    ;

    private final Integer type;
    private final String name;
    private final CryptoService cryptoService;

    EncryptType(Integer type, String name, CryptoService cryptoService) {
        this.type = type;
        this.name = name;
        this.cryptoService = cryptoService;
    }

    public static EncryptType getEncryptType(Integer type) {
        for (EncryptType encryptType : EncryptType.values()) {
            if (encryptType.getType().equals(type)) {
                return encryptType;
            }
        }
        return null;
    }

    public static EncryptType getEncryptName(String name) {
        for (EncryptType encryptType : EncryptType.values()) {
            if (encryptType.getName().equals(name)) {
                return encryptType;
            }
        }
        return null;
    }

    public static CryptoService getCryptoService(Integer type, String name) {
        EncryptType encryptType = Objects.nonNull(type) ? getEncryptType(type) : getEncryptName(name);
        if (encryptType == null) {
            return null;
        }
        return encryptType.getCryptoService();
    }
}
