CREATE USER 'test_user'@'localhost' IDENTIFIED BY 'asdasd';

GRANT SELECT ON `test_db`.`user` TO 'test_user'@'localhost';

GRANT SELECT ON `test_db`.`transaction_info` TO 'test_user'@'localhost';

GRANT SELECT, INSERT ON `test_db`.`transaction` TO 'test_user'@'localhost';

GRANT SELECT ON `test_db`.`currency` TO 'test_user'@'localhost';

GRANT SELECT ON `test_db`.`account_info` TO 'test_user'@'localhost';

GRANT SELECT, UPDATE ON `test_db`.`account` TO 'test_user'@'localhost';