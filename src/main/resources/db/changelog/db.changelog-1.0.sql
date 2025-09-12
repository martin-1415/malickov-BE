--liquibase formatted sql


--changeset Martin_Fischer:1 context:dev,prod

CREATE TABLE user (
                      user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                      first_name VARCHAR(100) NOT NULL,
                      last_name VARCHAR(100) NOT NULL,
                      email VARCHAR(100) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      password VARCHAR(255),
                      active BOOLEAN DEFAULT TRUE,
                      role_name VARCHAR(55),
                      UNIQUE(email)
);

--changeset Martin_Fischer:2 context:dev
INSERT INTO user (first_name, last_name, email, active, role_name, password)
                VALUES
                ('Martin', 'Fischer', '1@1.cz',1,'DIRECTOR','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                ('Sarka', 'Fischer','2@2.cz',1,'MANAGER','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                ('Charlie', 'Brown','3@3.cz',1,'TEACHER','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                ('Sam', 'Quited','4@4.cz',0,'PARENT','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                ('Tom', 'Skala','5@5.cz',1,'PARENT','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                ('Jack', 'Montana','6@6.cz',1,'PARENT','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.');

--changeset Martin_Fischer:3 context:dev,prod
CREATE TABLE child (
                    child_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    department VARCHAR(55) NOT NULL,
                    birthday DATETIME(6) NOT NULL,
                    active BOOLEAN DEFAULT TRUE,
                    notes VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    user_id INT NOT NULL,
                    identificator_id INT,
                    mon TINYINT(1) DEFAULT 0,
                    tue TINYINT(1) DEFAULT 0,
                    wed TINYINT(1) DEFAULT 0,
                    thu TINYINT(1) DEFAULT 0,
                    fri TINYINT(1) DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE
);

CREATE TABLE attendance (
                       attendance_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       child_id INT NOT NULL,
                       date DATE NOT NULL,
                       plan_arrival DATETIME NOT NULL,
                       plan_leaving DATETIME NOT NULL,
                       arrival DATETIME DEFAULT NULL,
                       leaving DATETIME DEFAULT NULL,
                       excuse VARCHAR(255),
                       late_excuse VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       modified_by INT NOT NULL,
                       modified_at TIMESTAMP DEFAULT NULL,
                       department VARCHAR(55) NOT NULL,
                       FOREIGN KEY (child_id) REFERENCES child(child_id) ON DELETE CASCADE
);

--changeset Martin_Fischer:4 context:dev,prod
CREATE TABLE identificator (
           identificator_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
           identificator VARCHAR(50) NOT NULL)


--changeset Martin_Fischer:5 context:dev
INSERT INTO identificator (identificator)
                VALUES ('Sovička'),('Srdíčko'),('Kačenka do vody'),('Houba');

INSERT INTO child ( first_name,last_name, department, birthday, user_id , identificator_id)
                VALUES
                ('Laura','Skala','KINDERGARTEN','2020-01-17',4,0),
                ('Mia','Skala','NURSERY2','2022-02-01',4,1),
                ('Lucie','Montana','NURSERY1','2021-05-17',5,2),
                ('Tereza','Montana','NURSERY2','2022-07-01',5,3);