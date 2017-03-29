CREATE USER 'onkibot_test'@'%' IDENTIFIED BY 'onkibot_test';
GRANT ALL PRIVILEGES ON onkibot_test.* TO 'onkibot_test'@'%';
FLUSH PRIVILEGES;
