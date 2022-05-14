## 说明

基于 ```seata TCC``` 模式的 **转账** 案例

## 服务
* transfer-business 转账入口服务
* transfer-out      账户转出服务
* transfer-in       账户转入服务
* eureka-server     注册中心服务
* transfer-common   实体类、mapper

## 接口
```
http://localhost:8181/transfer?fromAccount=A&toAccount=B&amount=10
```

* fromAccount 转出方用户名
* toAccount 转入方用户名
* amount 资金数量

## mysql 脚本
```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `accountNo` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `amount` decimal(20,2) DEFAULT '0.00',
  `freezedAmount` decimal(20,2) unsigned DEFAULT '0.00',
  PRIMARY KEY (`accountNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of account
-- ----------------------------
BEGIN;
INSERT INTO `account` (`accountNo`, `amount`, `freezedAmount`) VALUES ('A', 100.00, 0.00);
INSERT INTO `account` (`accountNo`, `amount`, `freezedAmount`) VALUES ('B', 100.00, 0.00);
COMMIT;
```
