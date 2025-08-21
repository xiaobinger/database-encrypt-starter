package com.database.typeHandler;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.database.core.service.CryptoService;
import com.database.core.util.BeanUtils;
import com.database.properties.DatabaseEncryptProperties;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author xiongbing
 * @date 2024/10/31 14:18
 * @description
 */

public class FieldTypeHandler extends BaseTypeHandler<String> {
    public final static String ID_CARD_REGX = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$";
    public final static String PHONE_REGX = "^1[3-9]\\d{9}$";
    public final static String BANK_CARD_REGX = "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11}|62[0-9]{17})$";

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 对参数进行加密
        DatabaseEncryptProperties cryptoConfig = BeanUtils.getCryptoConfig();
        CryptoService cryptoService = BeanUtils.getCryptoService();
        boolean encryptFlag = cryptoConfig.isEncryptOpen();
        if (encryptFlag && StringUtils.isNotBlank(parameter)) {
            parameter = BeanUtils.getCryptPrefix() + cryptoService.encrypt(parameter,cryptoConfig);
        }
        preparedStatement.setString(i, parameter);
    }


    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        // 对结果进行解密
        String res = resultSet.getString(s);
        if (StringUtils.isBlank(res)) {
            return res;
        }
        DatabaseEncryptProperties cryptoConfig = BeanUtils.getCryptoConfig();
        CryptoService cryptoService = BeanUtils.getServiceByEncryptField(res);
        String prefix = BeanUtils.getPrefixByEncryptField(res);
        if (StringUtils.isBlank(prefix) || Objects.isNull(cryptoService)) {
            return res;
        }
        if (res.startsWith(prefix)) {
            return cryptoService.decrypt(res.substring(prefix.length()),cryptoConfig);
        } else if (res.contains("/") && res.contains(prefix)) {
            return splitEncryptFlag(res,"/",prefix,24);
        }
        return res;
    }
    private String splitEncryptFlag(String input,String splitChar,String encryptFieldStart,Integer encryptFieldLength) {
        String indexFlag = splitChar + encryptFieldStart;
        int indexFlagLength = indexFlag.length();
        String inputTemp = input;
        DatabaseEncryptProperties cryptoConfig = BeanUtils.getCryptoConfig();
        CryptoService cryptoService = BeanUtils.getServiceByEncryptField(input);
        while(inputTemp.contains(indexFlag)) {
            String encryptField = inputTemp.substring(inputTemp.indexOf(indexFlag)+indexFlagLength,
                    inputTemp.indexOf(indexFlag)+indexFlagLength+encryptFieldLength);
            String decryptField = cryptoService.decrypt(encryptField,cryptoConfig);
            input = input.replace(encryptFieldStart + encryptField,decryptField);
            inputTemp = inputTemp.substring(inputTemp.indexOf(indexFlag)+indexFlagLength+encryptFieldLength);
        }
        return input;
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        // 对结果进行解密
        return resultSet.getString(i);
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        // 对结果进行解密
        return callableStatement.getString(i);
    }
}

