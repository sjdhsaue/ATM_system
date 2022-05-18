DROP TABLE IF EXISTS `atm`;
CREATE TABLE `atm`
(
    `atm_id`    int(11) NOT NULL,
    `atm_money` int(11) NOT NULL,
    PRIMARY KEY (`atm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK
TABLES `atm` WRITE;
INSERT INTO `atm`
VALUES (1, 953364);
UNLOCK
TABLES;
