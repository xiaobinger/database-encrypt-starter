package com.database.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.database.core.service.CryptoService;
import com.database.core.util.BeanUtils;
import com.database.properties.DatabaseEncryptProperties;
import com.database.typeHandler.FieldTypeHandler;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author xiongbing
 * @date 2024/11/6 16:07
 * @description
 */
@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
)})
public class MyBatisEncryptionInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        DatabaseEncryptProperties encryptProperties =
                Optional.ofNullable(SpringUtil.getBean("databaseEncryptProperties",DatabaseEncryptProperties.class))
                        .orElse(new DatabaseEncryptProperties());
        boolean encryptFlag = encryptProperties.isEncryptOpen()
                && Objects.equals(encryptProperties.getEncryptType(), 1);
        if (!encryptFlag) {
            return invocation.proceed();
        }
        /*//原始结果输出
        Object originResult = invocation.proceed();
        if (!(originResult instanceof List)  && Objects.nonNull(originResult)) {
            return originResult;
        } else if (originResult != null && CollectionUtil.isNotEmpty((List<?>) originResult)) {
            boolean countFlag = ((List<?>) originResult).size() == 1
                    && ((List<?>) originResult).get(0) instanceof Long
                    && (Long) ((List<?>) originResult).get(0) == 0L;
            if (!countFlag) {
                return originResult;
            }
            List<?> resultList = (List<?>) originResult;
            for (Object result : resultList) {
                Field[] fields = result.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object obj = field.get(result);
                    if ((!(obj instanceof Long) && Objects.nonNull(obj)) || (obj != null && (Long) obj != 0L)) {
                        return originResult;
                    }
                }
            }
        }
        clearLocalCache(invocation);*/
        return getResultWithDoEncrypt(invocation, encryptProperties);
    }




    private Object getResultWithDoEncrypt(Invocation invocation, DatabaseEncryptProperties encryptProperties) throws InvocationTargetException, IllegalAccessException {
        Object[] args = invocation.getArgs();
        String sql = getSql(args);
        if (skipWithoutEncryptTables(sql, encryptProperties)) {
            return invocation.proceed();
        }
        Object parameterObject = args[1];
        args[1] = doEncrypt(parameterObject);
        if (parameterObject instanceof MapperMethod.ParamMap) {
            handleObjectParamMap(parameterObject);
        } else {
            handleOtherFieldType(parameterObject);
            args[1] = parameterObject;
        }
        return invocation.proceed();
    }

    private void clearLocalCache(Invocation invocation) {
        Executor executor = (Executor) invocation.getTarget();
        executor.clearLocalCache();
    }
    private boolean skipWithoutEncryptTables(String sql, DatabaseEncryptProperties encryptProperties) {
        List<String> withOutEncryptTables = encryptProperties.getWithOutEncryptTables();
        if (CollectionUtil.isNotEmpty(withOutEncryptTables)) {
            for (String withOutEncryptTable : withOutEncryptTables) {
                if (sql.contains(withOutEncryptTable)) {
                    //存在不执行加密操作的表直接返回
                    return true;
                }
            }
        }
        return false;
    }

    private static String getSql(Object[] args) {
        BoundSql boundSql;
        if (args.length > 4) {
            boundSql = (BoundSql) args[5];
        } else {
            MappedStatement ms = (MappedStatement) args[0];
            boundSql = ms.getBoundSql(args[1]);
        }
        return boundSql.getSql();
    }

    private void handleObjectParamMap(Object parameterObject) {
        MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap) parameterObject;
        paramMap.forEach((key, value) -> {
            paramMap.put(key, doEncrypt(value));
            if (value instanceof LambdaQueryWrapper) {
                LambdaQueryWrapper<Object> lambdaQueryWrapper = (LambdaQueryWrapper) value;
                Map<String,Object> vMap = lambdaQueryWrapper.getParamNameValuePairs();
                vMap.forEach((k, v) -> vMap.put(k, doEncrypt(v)));
            } else {
                handleOtherFieldType(value);
            }
        });
    }

    private void handleOtherFieldType(Object parameterObject) {
        Field[] fields = Objects.nonNull(parameterObject) ? parameterObject.getClass().getDeclaredFields() : new Field[0];
        Arrays.stream(fields).filter(f -> !Modifier.isStatic(f.getModifiers())
                || !Modifier.isFinal(f.getModifiers())).forEach(field -> {
            field.setAccessible(true);
            try {
                Object obj = doEncrypt(field.get(parameterObject));
                field.set(parameterObject, obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Object doEncrypt(Object param) {
        if (Objects.nonNull(param) && param instanceof String) {
            DatabaseEncryptProperties cryptoConfig = BeanUtils.getCryptoConfig();
            CryptoService cryptoService = BeanUtils.getCryptoService();
            String parameter = (String) param;
            if((parameter.matches(FieldTypeHandler.ID_CARD_REGX)
                    || parameter.matches(FieldTypeHandler.PHONE_REGX)
                    || parameter.matches(FieldTypeHandler.BANK_CARD_REGX))) {
                parameter = BeanUtils.getCryptPrefix() + cryptoService.encrypt(parameter, cryptoConfig);
            }
            return parameter;
        }
        return param;
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
