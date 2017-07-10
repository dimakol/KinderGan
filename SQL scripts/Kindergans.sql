use kindergan2;

drop table kindergans;

CREATE TABLE IF NOT EXISTS kindergans (
    uid INT(11) PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    classes INT(1) NOT NULL,
    address VARCHAR(50) NOT NULL,
    city VARCHAR(20) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    schedule_plan VARCHAR(400) NULL
);

INSERT INTO kindergans VALUES (1, 'ברקן', 2, 'הריף 33 רמת אשכול', 'אשקלון', '086491699', NULL);
INSERT INTO kindergans VALUES (2, 'רימון', 2, 'עבודה א/9 רמת אשכול', 'אשקלון', '086722352', NULL);
INSERT INTO kindergans VALUES (3, 'חרוב', 2, 'קאפח 12 רמת אשכול', 'אשקלון', '086723956', NULL);
INSERT INTO kindergans VALUES (4, 'יצהר', 1, 'פקיעין 4 נאות אשקלוןן', 'אשקלון', '089268108', NULL);
INSERT INTO kindergans VALUES (5, 'חצב', 1, 'נווה שלום 13 נאות אשקלון', 'אשקלון', '086723487', NULL);
INSERT INTO kindergans VALUES (6, 'גלים', 1, 'הזית 15 רובע א', 'אשדוד', '088565550', NULL);
INSERT INTO kindergans VALUES (7, 'כוכב ים', 1, 'האצ"ל 6 רובע א', 'אשדוד', '088565597', NULL);
INSERT INTO kindergans VALUES (8, 'מפרש', 2, 'הראשונים 17 רובע א', 'אשדוד', '088565525', NULL);
INSERT INTO kindergans VALUES (9, 'שלדג', 1, 'אילת 25 רובע א', 'אשדוד', '088565549', NULL);