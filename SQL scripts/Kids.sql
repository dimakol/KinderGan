use kindergan2;

drop table kids;

CREATE TABLE IF NOT EXISTS kids (
    uid INT(11) PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(25) NOT NULL,
    birth_date VARCHAR(50) NOT NULL,
    photo VARCHAR(400) NOT NULL,
    kindergan_name VARCHAR(20) NOT NULL,
    class INT(1) NOT NULL,
    parent_id VARCHAR(10),
    presence BIT DEFAULT 0,
    special VARCHAR(100),
    contact1 VARCHAR(65),
    contact2 VARCHAR(65),
    contact3 VARCHAR(65),
    created_at DATETIME,
    updated_at DATETIME NULL,
    FOREIGN KEY (parent_id)
        REFERENCES parents (ID)
);
    
ALTER TABLE kids AUTO_INCREMENT = 1;