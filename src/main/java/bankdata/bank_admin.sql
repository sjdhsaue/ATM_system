DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`
(
    `admin_id`       varchar(128) NOT NULL,
    `admin_password` int(11) NOT NULL,
    PRIMARY KEY (`admin_id`)
        ENGINE=InnoDB DEFAULT CHARSET=utf8;
LOCK
TABLES `admin` WRITE;

INSERT INTO `admin`
VALUES ('duzhihao', 123456),
       ('lixin', 654321),
       ('zhangkai', 123456);

UNLOCK
TABLES;

