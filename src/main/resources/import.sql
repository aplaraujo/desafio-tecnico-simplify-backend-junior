INSERT INTO tb_user (name, email, password) VALUES ('Caroline Nair da Costa', 'carolinenairdacosta@outllok.com', '$2a$16$EC1tzBitQLKaVFGXkTrlZuhQDokYeY.RZECP/5P1z481oXMhTRoLO');
INSERT INTO tb_user (name, email, password) VALUES ('Hugo Vitor Martins', 'hugovitormartins@iclud.com', '$2a$16$TDGM88Cg8rW8JuY/9hjtu.NLqxpWX8vcLvQQ123jxKykabLKmWcMm');

INSERT INTO tb_role (authority) VALUES ('ROLE_USER');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);

INSERT INTO tb_todo(name, description, done, priority, user_id) VALUES ('Lavar a louça', 'Lorem ipsum dolor sit amet', 1, 'BAIXA', 1);
INSERT INTO tb_todo(name, description, done, priority, user_id) VALUES ('Organizar a despensa', 'Lorem ipsum dolor sit amet', 0, 'BAIXA', 1);
INSERT INTO tb_todo(name, description, done, priority, user_id) VALUES ('Passar aspirador na sala', 'Lorem ipsum dolor sit amet', 1, 'BAIXA', 2);
INSERT INTO tb_todo(name, description, done, priority, user_id) VALUES ('Trocar a roupa de cama', 'Lorem ipsum dolor sit amet', 0, 'BAIXA', 2);
INSERT INTO tb_todo(name, description, done, priority, user_id) VALUES ('Regar as plantas', 'Lorem ipsum dolor sit amet', 0, 'BAIXA', 1);
INSERT INTO tb_todo(name, description, done, priority, user_id) VALUES ('Tirar o lixo', 'Lorem ipsum dolor sit amet', 1, 'BAIXA', 2);