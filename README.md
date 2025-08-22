# database-encrypt-starter
自定义实现数据库加密存储,可配置多种算法
```xml
<dependencies>
    <dependency>
        <groupId>io.github.xiaobinger</groupId>
        <artifactId>database-encrypt-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
配置文件
```yaml
database:
  encrypt:
    encryptOpen: false
    #加密算法配置 1:sm4 2:aes 3:rsa 4:des
    encryptType: 1
    #SM4密钥长度为16位，AES密钥长度为16/24/32位，DES密钥长度为24位
    sm4Key: XXXXXXXXXXXXXXXX
    aesKey: XXXXXXXXXXXXXXXX
    desKey: XXXXXXXXXXXXXXXXXXXXXXXXXXXX
    rsaPublicKey: 
    rsaPrivateKey: 
    #不加密的表名
    withOutEncryptTables:
      - sys_bank_card_bin
    #加密算法自定义前缀
    encryptedFieldPrefixMap: {"sm4":"~++","aes":"!##","rsa":"*$$","des":"^&&"}
```
