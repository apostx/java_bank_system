-- DROP --

DROP VIEW IF EXISTS `account_info`;
DROP VIEW IF EXISTS `transaction_info`;

DROP TABLE IF EXISTS `transaction`;
DROP TABLE IF EXISTS `account`;
DROP TABLE IF EXISTS `currency`;
DROP TABLE IF EXISTS `user`;


-- TABLE --

CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(8) NOT NULL,
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE `currency` (
  `id` int(2) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `short_name` varchar(3) NOT NULL,
  `symbol` varchar(3) NOT NULL,
  PRIMARY KEY (`id`)
) AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


CREATE TABLE `account` (
  `account_number` varchar(24) NOT NULL,
  `user_id` int(10) NOT NULL,
  `currency_id` int(2) NOT NULL,
  `balance` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY (`account_number`),
  FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) DEFAULT CHARSET=utf8;


CREATE TABLE `transaction` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `source_account_number` varchar(24) NOT NULL,
  `target_account_number` varchar(24) NOT NULL,
  `amount` int(10) NOT NULL,
  `source_balance` int(10) NOT NULL,
  `target_balance` int(10) NOT NULL,
  `timestamp` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`source_account_number`) REFERENCES `account` (`account_number`),
  FOREIGN KEY (`target_account_number`) REFERENCES `account` (`account_number`)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- VIEW --

CREATE VIEW `account_info` AS SELECT
account.user_id,
account.account_number,
account.currency_id,
currency.short_name,
account.balance
FROM
account
INNER JOIN currency ON account.currency_id = currency.id ;


CREATE VIEW `transaction_info` AS SELECT
`transaction`.id,
`transaction`.source_account_number,
`transaction`.target_account_number,
source_account.user_id AS source_user_id,
target_account.user_id AS target_user_id,
currency.short_name AS currency,
`transaction`.amount,
`transaction`.source_balance,
`transaction`.target_balance,
`transaction`.`timestamp`
FROM
`transaction`
INNER JOIN account AS source_account ON `transaction`.source_account_number = source_account.account_number
INNER JOIN account AS target_account ON `transaction`.target_account_number = target_account.account_number
INNER JOIN currency ON source_account.currency_id = currency.id ;


-- INSERT --

INSERT INTO `user` VALUES ('1', 'asdasd1', 'A8F5F167F44F4964E6C998DEE827110C');
INSERT INTO `user` VALUES ('2', 'asdasd2', 'A8F5F167F44F4964E6C998DEE827110C');

INSERT INTO `currency` VALUES ('1', 'Hungarian Forint', 'HUF', 'Ft');
INSERT INTO `currency` VALUES ('2', 'Euro', 'EUR', '€');
INSERT INTO `currency` VALUES ('3', 'US Dollar', 'USD', '$');

INSERT INTO `account` VALUES ('012345670123456701234567', '1', '1', '0001000000');
INSERT INTO `account` VALUES ('123456701234567012345670', '1', '1', '0001000000');
INSERT INTO `account` VALUES ('234567012345670123456701', '1', '2', '0001000000');
INSERT INTO `account` VALUES ('345670123456701234567012', '2', '2', '0001000000');
INSERT INTO `account` VALUES ('456701234567012345670123', '2', '3', '0001000000');
INSERT INTO `account` VALUES ('567012345670123456701234', '2', '3', '0001000000');
