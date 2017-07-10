use kindergan2;

SET FOREIGN_KEY_CHECKS=0;
drop table parents;
SET FOREIGN_KEY_CHECKS=1;

create table if not exists parents(
	uid int(11) primary key auto_increment,
    ID varchar(10) not null unique,
    type int(1) default 2,
    first_name varchar(25) not null,
    last_name varchar(25) not null,
    address varchar(50) not null,
    phone varchar(15) not null,
    email varchar(100) not null unique,
    encrypted_password varchar(255) not null,
    created_at datetime,
    updated_at datetime null
    );
    
ALTER TABLE parents AUTO_INCREMENT = 1;