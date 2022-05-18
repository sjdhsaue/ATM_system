DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    `user_card`     int(11) NOT NULL,
    `user_password` int(11) NOT NULL,
    `user_name`     varchar(128) NOT NULL,
    `user_money`    int(11) NOT NULL,
    `user_action`   int(11) NOT NULL,
    PRIMARY KEY (`user_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK
TABLES `user` WRITE;
INSERT INTO `user`
VALUES (100001, 123456, '陈平安', 199800, 1),
       (100003, 123456, '徐凤年', 149900, 1),
       (337072, 123456, '胡蓉', 10000, 1),
       (472715, 123456, '老王', 1000000, 1),
       (479511, 123456, '李欣', 110000, 1),
       (828468, 123456, '屈伍庆', 100000, 1),
       (849208, 123456, '贺程', 100000, 1),
       (849454, 123456, '刘子光', 10000, 1),
       (914885, 123456, '叶望龙', 90000, 1),
       (1051201, 123456, '贺程', 10000000, 1);

UNLOCK
TABLES;
