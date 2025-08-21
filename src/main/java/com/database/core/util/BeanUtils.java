package com.database.core.util;

import cn.hutool.extra.spring.SpringUtil;
import com.database.core.service.CryptoService;
import com.database.core.service.EncryptType;
import com.database.properties.DatabaseEncryptProperties;

import java.util.Map;
import java.util.Objects;

/**
 * @author xiongbing
 * @date 2025/2/27 17:21
 * @description
 */
public class BeanUtils {

    public static DatabaseEncryptProperties getCryptoConfig() {
        return SpringUtil.getBean("databaseEncryptProperties", DatabaseEncryptProperties.class);
    }

    public static CryptoService getCryptoService() {
        DatabaseEncryptProperties databaseEncryptProperties = getCryptoConfig();
        return EncryptType.getCryptoService(databaseEncryptProperties.getEncryptType(),null);
    }

    public static CryptoService getServiceByEncryptField(String encryptField) {
        DatabaseEncryptProperties databaseEncryptProperties = getCryptoConfig();
        Map<String,String> encryptedFieldPrefixMap = databaseEncryptProperties.getEncryptedFieldPrefixMap();
        if (Objects.isNull(encryptedFieldPrefixMap)) {
            return null;
        }
        for (Map.Entry<String, String> entry : encryptedFieldPrefixMap.entrySet()) {
            if (encryptField.startsWith(entry.getValue())
                    || (encryptField.contains("/") && encryptField.contains(entry.getValue()))) {
                return EncryptType.getCryptoService(null,entry.getKey());
            }
        }
        return null;
    }

    public static String getPrefixByEncryptField(String encryptField) {
        DatabaseEncryptProperties databaseEncryptProperties = getCryptoConfig();
        Map<String,String> encryptedFieldPrefixMap = databaseEncryptProperties.getEncryptedFieldPrefixMap();
        if (Objects.isNull(encryptedFieldPrefixMap)) {
            return null;
        }
        for (Map.Entry<String, String> entry : encryptedFieldPrefixMap.entrySet()) {
            if (encryptField.startsWith(entry.getValue())
                    || (encryptField.contains("/") && encryptField.contains(entry.getValue()))) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static String getCryptPrefix(){
        DatabaseEncryptProperties databaseEncryptProperties = getCryptoConfig();
        Map<String,String> encryptedFieldPrefixMap = databaseEncryptProperties.getEncryptedFieldPrefixMap();
        EncryptType encryptType = EncryptType.getEncryptType(databaseEncryptProperties.getEncryptType());
        assert encryptType != null;
        return encryptedFieldPrefixMap.get(encryptType.getName());
    }
}
