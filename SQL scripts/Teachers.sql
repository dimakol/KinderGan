use kindergan2;

drop table teachers;

CREATE TABLE IF NOT EXISTS teachers (
    uid INT(11) PRIMARY KEY AUTO_INCREMENT,
    ID VARCHAR(10) NOT NULL UNIQUE,
    type INT(1) DEFAULT 1,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    photo VARCHAR(400) NOT NULL,
    kindergan_name VARCHAR(20) NOT NULL,
    kindergan_city VARCHAR(20) NOT NULL,
    kindergan_class INT(1) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    encrypted_password VARCHAR(255) NOT NULL,
    notification_time TIME DEFAULT '10:00:00',
    created_at DATETIME,
    updated_at DATETIME NULL
);
    
ALTER TABLE teachers AUTO_INCREMENT = 1;