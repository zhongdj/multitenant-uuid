create table tbl_64char_pk_uuid (
`id`  char(32) CHARACTER SET `ascii` collate `ascii_general_ci` PRIMARY KEY ,
`first_name` varchar(200),
`last_name` varchar(200),
`email` varchar (128),
`tenant_id` INT(10)
) ENGINE=INNODB default character set 'utf8' collate='utf8_general_ci';


create table tbl_16char_pk_uuid (
`id`  char(44) CHARACTER SET `ascii` collate `ascii_general_ci` PRIMARY KEY ,
`first_name` varchar(200),
`last_name` varchar(200),
`email` varchar (128),
`tenant_id` INT(10)
) ENGINE=INNODB default character set 'utf8' collate='utf8_general_ci';


CREATE TABLE `tbl_auto_pk_uuid` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(200) DEFAULT NULL,
  `last_name` varchar(200) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `tenant_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tbl_binary_pk_uuid` (
  `id` binary(22) NOT NULL,
  `first_name` varchar(200) DEFAULT NULL,
  `last_name` varchar(200) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `tenant_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;