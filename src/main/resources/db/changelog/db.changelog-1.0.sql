--liquibase formatted sql


--changeset Martin_Fischer:1 context:prod,dev

CREATE TABLE user (
                      user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                      first_name VARCHAR(100) NOT NULL,
                      last_name VARCHAR(100) NOT NULL,
                      email VARCHAR(100) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      password VARCHAR(255),
                      active BOOLEAN,
                      role_name VARCHAR(55),
                      UNIQUE(email)
);

--changeset Martin_Fischer:2 context:dev
INSERT INTO user (first_name, last_name, email, active, role_name, password) VALUES
                                                                                 ('Martin', 'Fischer', '1@1.cz',1,'DIRECTOR','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                                                                                 ('Sarka', 'Fischer','2@2.cz',1,'MANAGER','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                                                                                 ('Charlie', 'Brown','3@3.cz',1,'TEACHER','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                                                                                 ('Sam', 'Quited','4@4.cz',0,'PARENT','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.'),
                                                                                 ('Tom', 'Parent','5@5.cz',1,'PARENT','$2a$12$KeVi5xNQGqtFqALo355CMudCpNEiwDbv5O/75ML7wnqu4cdcpHzf.');