package com.database.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author xiongbing
 * @date 2024/11/5 10:01
 * @description 数据库加密配置
 */
@ConfigurationProperties(prefix = "database.encrypt")
@Component
@Data
public class DatabaseEncryptProperties {

    private boolean encryptOpen;

    /**
     * 加密方式 1:SM4 2:AES 3:RSA 4:3DES
     */
    private Integer encryptType;

    /**
     * sm4加密密钥
     */
    private String sm4Key;
    /**
     * aes加密密钥
     */
    private String aesKey;
    /**
     * rsa公钥
     */
    private String rsaPublicKey;
    /**
     * rsa私钥
     */
    private String rsaPrivateKey;
    /**
     * 3des加密密钥
     */
    private String desKey;

    /**
     * 忽略加密操作的表
     */
    private List<String> withOutEncryptTables;

    private Map<String,String> encryptedFieldPrefixMap;

}
