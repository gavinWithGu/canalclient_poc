CREATE SCHEMA `Kuma_Merge_DB` DEFAULT CHARACTER SET utf8 ;

CREATE TABLE `Kuma_Merge_DB`.`kuma_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8;