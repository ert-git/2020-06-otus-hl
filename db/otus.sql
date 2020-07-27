

-- 
-- Отключение внешних ключей
-- 
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- 
-- Установить режим SQL (SQL mode)
-- 
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 
-- Установка кодировки, с использованием которой клиент будет посылать запросы на сервер
--
SET NAMES 'utf8';

-- 
-- Установка базы данных по умолчанию
--
USE otus;

--
-- Описание для таблицы cities
--
DROP TABLE IF EXISTS cities;
CREATE TABLE cities (
  id INT(11) NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB
AUTO_INCREMENT = 14
AVG_ROW_LENGTH = 8192
CHARACTER SET utf8
COLLATE utf8_general_ci;

--
-- Описание для таблицы interests
--
DROP TABLE IF EXISTS interests;
CREATE TABLE interests (
  id INT(11) NOT NULL AUTO_INCREMENT,
  name TEXT NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB
AUTO_INCREMENT = 26
AVG_ROW_LENGTH = 1170
CHARACTER SET utf8
COLLATE utf8_general_ci;

--
-- Описание для таблицы users
--
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id INT(11) NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(50) DEFAULT NULL,
  last_name VARCHAR(50) DEFAULT NULL,
  second_name VARCHAR(50) DEFAULT NULL,
  age INT(11) DEFAULT NULL COMMENT 'age in years',
  gender SMALLINT(1) DEFAULT 2 COMMENT '1 - male, 0 - female, 2 - not_set',
  city INT(11) DEFAULT NULL,
  login VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FK_users_city FOREIGN KEY (city)
    REFERENCES cities(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE = INNODB
AUTO_INCREMENT = 51
AVG_ROW_LENGTH = 5461
CHARACTER SET utf8
COLLATE utf8_general_ci;

--
-- Описание для таблицы user_friend
--
DROP TABLE IF EXISTS user_friend;
CREATE TABLE user_friend (
  user_id INT(11) NOT NULL,
  friend_id INT(11) NOT NULL,
  PRIMARY KEY (user_id, friend_id),
  CONSTRAINT FK_user_friend_friend_id FOREIGN KEY (friend_id)
    REFERENCES users(id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT FK_user_friend_users_id FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE ON UPDATE NO ACTION
)
ENGINE = INNODB
AVG_ROW_LENGTH = 8192
CHARACTER SET utf8
COLLATE utf8_general_ci;

--
-- Описание для таблицы user_interest
--
DROP TABLE IF EXISTS user_interest;
CREATE TABLE user_interest (
  user_id INT(11) NOT NULL,
  interest_id INT(11) NOT NULL,
  PRIMARY KEY (user_id, interest_id),
  CONSTRAINT FK_user_interest_interests_id FOREIGN KEY (interest_id)
    REFERENCES interests(id) ON DELETE RESTRICT ON UPDATE NO ACTION,
  CONSTRAINT FK_user_interest_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE ON UPDATE NO ACTION
)
ENGINE = INNODB
AVG_ROW_LENGTH = 8192
CHARACTER SET utf8
COLLATE utf8_general_ci;

-- 
-- Восстановить предыдущий режим SQL (SQL mode)
-- 
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

-- 
-- Включение внешних ключей
-- 
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;