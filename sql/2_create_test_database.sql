CREATE DATABASE IF NOT EXISTS onkibot_test;
USE onkibot_test;

CREATE TABLE IF NOT EXISTS user(
        user_id                 INT             PRIMARY KEY     AUTO_INCREMENT,
        email                   VARCHAR(100)    NOT NULL UNIQUE,
        encoded_password        CHAR(60)        NOT NULL,
        name                    VARCHAR(20)     NOT NULL,
        created_time            DATETIME        NOT NULL,
        is_instructor           BOOL            NOT NULL
);

CREATE TABLE IF NOT EXISTS course(
        course_id               INT             PRIMARY KEY     AUTO_INCREMENT,
        name                    VARCHAR(100)    NOT NULL,
        description             TEXT            NOT NULL
);

CREATE TABLE IF NOT EXISTS attends(
        user_id                 INT,
        course_id               INT,
        PRIMARY KEY (user_id, course_id),
        FOREIGN KEY (user_id)
                REFERENCES user(user_id)
                ON UPDATE CASCADE,
        FOREIGN KEY (course_id)
                REFERENCES course(course_id)
                ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS category(
        category_id             INT             PRIMARY KEY AUTO_INCREMENT,
        course_id               INT             NOT NULL,
        name                    VARCHAR(50)     NOT NULL,
        description             TEXT            NOT NULL,
        FOREIGN KEY (course_id)
                REFERENCES course(course_id)
                ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS resource(
        resource_id                     INT                     PRIMARY KEY AUTO_INCREMENT,
        category_id                     INT                     NOT NULL,
        name                            VARCHAR(50)             NOT NULL,
        body                            TEXT                    NOT NULL,
        publisher_user_id               INT                     NOT NULL,
        FOREIGN KEY (category_id)
                REFERENCES category(category_id)
                ON UPDATE CASCADE,
        FOREIGN KEY (publisher_user_id)
                REFERENCES user(user_id)
                ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS external_resource(
        external_resource_id            INT             PRIMARY KEY AUTO_INCREMENT,
        resource_id                     INT             NOT NULL,
        url                             VARCHAR(255)    NOT NULL,
        publisher_user_id               INT             NOT NULL,
        FOREIGN KEY (resource_id)
                REFERENCES resource(resource_id)
                ON UPDATE CASCADE,
        FOREIGN KEY (publisher_user_id)
                REFERENCES user(user_id)
                ON UPDATE CASCADE

);

CREATE TABLE IF NOT EXISTS resource_feedback(
        resource_feedback_id            INT             PRIMARY KEY AUTO_INCREMENT,
        resource_id                     INT             NOT NULL,
        comment                         TEXT            NOT NULL,
        feedback_user_id                INT             NOT NULL,
        FOREIGN KEY (resource_id)
                REFERENCES resource(resource_id)
                ON UPDATE CASCADE,
        FOREIGN KEY (feedback_user_id)
                REFERENCES user(user_id)
                ON UPDATE CASCADE

);
