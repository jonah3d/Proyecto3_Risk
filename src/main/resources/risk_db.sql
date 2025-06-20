CREATE DATABASE  IF NOT EXISTS `risk_worlddomination_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `risk_worlddomination_db`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: nozomi.proxy.rlwy.net    Database: risk_worlddomination_db
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `avatars`
--

DROP TABLE IF EXISTS `avatars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `avatars` (
  `id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK5ipkcwwamgfo456avmn5vr198` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avatars`
--

LOCK TABLES `avatars` WRITE;
/*!40000 ALTER TABLE `avatars` DISABLE KEYS */;
INSERT INTO `avatars` VALUES (1,'Sergent','https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/imge3.png'),(2,'Captain','https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/imge2.png'),(3,'Private','https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/imge1.png'),(4,'Colonel','https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/imge4.png');
/*!40000 ALTER TABLE `avatars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `avatars_seq`
--

DROP TABLE IF EXISTS `avatars_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `avatars_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avatars_seq`
--

LOCK TABLES `avatars_seq` WRITE;
/*!40000 ALTER TABLE `avatars_seq` DISABLE KEYS */;
INSERT INTO `avatars_seq` VALUES (1);
/*!40000 ALTER TABLE `avatars_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `border`
--

DROP TABLE IF EXISTS `border`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `border` (
  `country1_id` bigint NOT NULL,
  `country2_id` bigint NOT NULL,
  PRIMARY KEY (`country1_id`,`country2_id`),
  KEY `country2_id` (`country2_id`),
  CONSTRAINT `FK8w16k3c2t6uf0ehrqhc3lq6br` FOREIGN KEY (`country2_id`) REFERENCES `country` (`id`),
  CONSTRAINT `FKfvv3ildxgero95uiu0v5kvlev` FOREIGN KEY (`country1_id`) REFERENCES `country` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `border`
--

LOCK TABLES `border` WRITE;
/*!40000 ALTER TABLE `border` DISABLE KEYS */;
INSERT INTO `border` VALUES (1,2),(9,2),(1,3),(2,3),(2,4),(3,4),(9,4),(4,5),(9,5),(3,6),(4,6),(4,7),(5,7),(6,7),(6,8),(7,8),(2,9),(5,9),(8,10),(10,11),(10,12),(11,12),(21,12),(11,13),(12,13),(9,14),(14,15),(14,16),(15,16),(15,17),(16,17),(18,17),(15,18),(17,18),(21,18),(17,19),(18,19),(16,20),(17,20),(19,20),(19,22),(21,22),(21,23),(22,23),(21,24),(23,24),(23,25),(24,25),(23,26),(25,26),(20,27),(27,28),(28,29),(1,30),(29,30),(27,31),(28,31),(29,31),(30,31),(30,32),(31,32),(30,33),(32,33),(20,34),(27,34),(32,35),(34,35),(19,36),(20,36),(22,36),(34,36),(34,37),(35,37),(36,37),(35,38),(37,38),(38,39),(39,40),(39,41),(40,42),(41,42);
/*!40000 ALTER TABLE `border` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_types`
--

DROP TABLE IF EXISTS `card_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_types` (
  `id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_types`
--

LOCK TABLES `card_types` WRITE;
/*!40000 ALTER TABLE `card_types` DISABLE KEYS */;
INSERT INTO `card_types` VALUES (1,'Infantry'),(2,'Cavalry'),(3,'Artillery '),(4,'Common');
/*!40000 ALTER TABLE `card_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cards`
--

DROP TABLE IF EXISTS `cards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cards` (
  `id` bigint NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `c_types` bigint NOT NULL,
  `country_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `country_id` (`country_id`),
  KEY `c_types` (`c_types`),
  CONSTRAINT `cards_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`),
  CONSTRAINT `cards_ibfk_2` FOREIGN KEY (`c_types`) REFERENCES `card_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cards`
--

LOCK TABLES `cards` WRITE;
/*!40000 ALTER TABLE `cards` DISABLE KEYS */;
INSERT INTO `cards` VALUES (1,'01_alaska.png',1,1),(2,'02_northwestTerritory.png',3,2),(3,'03_alberta.png',2,3),(4,'04_ontario.png',2,4),(5,'05_quebec.png',2,5),(6,'06_westernUnitedStates.png',3,6),(7,'07_easternUnitedStates.png',3,7),(8,'08_centralamerica.png',3,8),(9,'09_greenland.png',2,9),(10,'10_venezuela.png',1,10),(11,'11_peru.png',1,11),(12,'12_brazil.png',3,12),(13,'13_argentina.png',1,13),(14,'14_iceland.png',1,14),(15,'15_greatbritain.png',3,15),(16,'16_scandinavia.png',2,16),(17,'17_notherneurope.png',3,17),(18,'18_awesterneurope',3,18),(19,'19_southerneurope.png',3,19),(20,'20_ukraine.png',2,20),(21,'21_northafrica.png',2,21),(22,'22_egypt.png',1,22),(23,'23_eastafrica.png',1,23),(24,'24_congo.png',1,24),(25,'25_southafrica.png',3,25),(26,'26_madagascar.png',2,26),(27,'27_ural.png',2,27),(28,'28_siberia.png',2,28),(29,'29_yakutsk.png',2,29),(30,'30_kamchatka.png',1,30),(31,'31_Irkutsk.png',2,31),(32,'32_Mongolia.png',1,32),(33,'33_japan.png',3,33),(34,'34_afghanistan.png',2,34),(35,'35_china.png',1,35),(36,'36_middleeast.png',1,36),(37,'37_india.png',2,37),(38,'38_southeastasia.png',1,38),(39,'39_indonesia.png',3,39),(40,'40_newguinea.png',1,40),(41,'41_westernaustralia.png',3,41),(42,'42_easternaustralia.png',3,42),(43,'00_extra.png',4,NULL);
/*!40000 ALTER TABLE `cards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `continent`
--

DROP TABLE IF EXISTS `continent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `continent` (
  `extra_tropes` int NOT NULL,
  `id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  `max_country` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `continent`
--

LOCK TABLES `continent` WRITE;
/*!40000 ALTER TABLE `continent` DISABLE KEYS */;
INSERT INTO `continent` VALUES (5,1,'North America',9),(2,2,'South America',4),(5,3,'Europe',7),(3,4,'Africa',6),(7,5,'Asia',12),(2,6,'Australia',4);
/*!40000 ALTER TABLE `continent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `country` (
  `continent_id` bigint NOT NULL,
  `id` bigint NOT NULL,
  `image` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `continent_id` (`continent_id`),
  CONSTRAINT `FKpymfsgrl32dy3gtl9r7rykkjg` FOREIGN KEY (`continent_id`) REFERENCES `continent` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES (1,1,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/alaska-2.png','Alaska'),(1,2,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/northwest_territory-8.png','Northwest Territory'),(1,3,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/alberta-3.png','Alberta'),(1,4,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/ontario-4.png','Ontario'),(1,5,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/quebec.png','Quebec'),(1,6,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/western_united_states.png','Western United States'),(1,7,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/eastern_united_states.png','Eastern United States'),(1,8,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/central_america-8.png','Central America'),(1,9,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/greenland.png','Greenland'),(2,10,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/venezuela-2.png','Venezuela'),(2,11,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/peru.png','Peru'),(2,12,'https://github.com/jonah3d/riskimg/blob/main/brazil-6.png','Brazil'),(2,13,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/argentina-5.png','Argentina'),(3,14,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/iceland-8.png','Iceland'),(3,15,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/great_britain-6.png','Great Britain'),(3,16,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/scandinavia-1.png','Scandinavia'),(3,17,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/northern_europe-4.png','Northern Europe'),(3,18,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/western_europe-9.png','Western Europe'),(3,19,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/southern_europe-0.png','Southern Europe'),(3,20,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/ukraine-9.png','Ukraine'),(4,21,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/north_africa-2.png','North Africa'),(4,22,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/egypt-6.png','Egypt'),(4,23,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/east_africa-5.png','East Africa'),(4,24,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/congo-8.png','Congo'),(4,25,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/south_africa-9.png','South Africa'),(4,26,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/madagascar-9.png','Madagascar'),(5,27,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/ural-6.png','Ural'),(5,28,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/siberia-4.png','Siberia'),(5,29,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/yakursk-2.png','Yakutsk'),(5,30,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/kamchatka-4.png','Kamchatka'),(5,31,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/irkutsk.png','Irkutsk'),(5,32,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/mongolia-7.png','Mongolia'),(5,33,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/japan-0.png','Japan'),(5,34,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/afghanistan-7.png','Afghanistan'),(5,35,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/china-8.png','China'),(5,36,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/middle_east-7.png','Middle East'),(5,37,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/india-2.png','India'),(5,38,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/siam-3.png','Southeast Asia'),(6,39,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/indonesia-5.png','Indonesia'),(6,40,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/new_guinea.png','New Guinea'),(6,41,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/western_australia-8.png','Western Australia'),(6,42,'https://raw.githubusercontent.com/jonah3d/riskimg/refs/heads/main/eastern_australia.png','Eastern Australia');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country_countries`
--

DROP TABLE IF EXISTS `country_countries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `country_countries` (
  `countries_id` bigint NOT NULL,
  `country_id` bigint NOT NULL,
  KEY `FKemw50eq4gte5m8fx44r9y289t` (`countries_id`),
  KEY `FKmvuhr12p764r0vlak9crkw5uw` (`country_id`),
  CONSTRAINT `FKemw50eq4gte5m8fx44r9y289t` FOREIGN KEY (`countries_id`) REFERENCES `country` (`id`),
  CONSTRAINT `FKmvuhr12p764r0vlak9crkw5uw` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country_countries`
--

LOCK TABLES `country_countries` WRITE;
/*!40000 ALTER TABLE `country_countries` DISABLE KEYS */;
/*!40000 ALTER TABLE `country_countries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ma`
--

DROP TABLE IF EXISTS `ma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ma` (
  `card_id` bigint NOT NULL,
  `player_id` bigint NOT NULL,
  PRIMARY KEY (`card_id`,`player_id`),
  KEY `player_id` (`player_id`),
  CONSTRAINT `ma_ibfk_1` FOREIGN KEY (`card_id`) REFERENCES `cards` (`id`),
  CONSTRAINT `ma_ibfk_2` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ma`
--

LOCK TABLES `ma` WRITE;
/*!40000 ALTER TABLE `ma` DISABLE KEYS */;
/*!40000 ALTER TABLE `ma` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `occupy`
--

DROP TABLE IF EXISTS `occupy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `occupy` (
  `country_id` bigint NOT NULL,
  `player_id` bigint NOT NULL,
  `troops` int NOT NULL,
  PRIMARY KEY (`country_id`,`player_id`),
  KEY `player_id` (`player_id`),
  CONSTRAINT `occupy_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`),
  CONSTRAINT `occupy_ibfk_2` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `occupy`
--

LOCK TABLES `occupy` WRITE;
/*!40000 ALTER TABLE `occupy` DISABLE KEYS */;
/*!40000 ALTER TABLE `occupy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partida`
--

DROP TABLE IF EXISTS `partida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `partida` (
  `id` bigint NOT NULL,
  `date` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `max_players` int NOT NULL,
  `admin_id` bigint DEFAULT NULL,
  `turn_player_id` bigint DEFAULT NULL,
  `turn_state` bigint NOT NULL,
  `is_public` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `turn_state` (`turn_state`),
  CONSTRAINT `partida_ibfk_1` FOREIGN KEY (`turn_state`) REFERENCES `states` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partida`
--

LOCK TABLES `partida` WRITE;
/*!40000 ALTER TABLE `partida` DISABLE KEYS */;
/*!40000 ALTER TABLE `partida` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
  `id` bigint NOT NULL,
  `user_id` int NOT NULL,
  `partida_id` bigint NOT NULL,
  `skf_numero` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`partida_id`),
  UNIQUE KEY `partida_id` (`partida_id`,`skf_numero`),
  CONSTRAINT `player_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `player_ibfk_2` FOREIGN KEY (`partida_id`) REFERENCES `partida` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `states`
--

DROP TABLE IF EXISTS `states`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `states` (
  `id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `states`
--

LOCK TABLES `states` WRITE;
/*!40000 ALTER TABLE `states` DISABLE KEYS */;
INSERT INTO `states` VALUES (0,'Wait'),(1,'Initial placement'),(2,'Country reinforcement'),(3,'Tropes reinforcement'),(4,'Combat'),(5,'Fortification'),(6,'Final');
/*!40000 ALTER TABLE `states` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `avatar_id` int NOT NULL,
  `games` int DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `wins` int DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`),
  KEY `FKi3f9wa4rqv70moyxivn9x6gnx` (`avatar_id`),
  CONSTRAINT `FKi3f9wa4rqv70moyxivn9x6gnx` FOREIGN KEY (`avatar_id`) REFERENCES `avatars` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (3,0,1,0,'djadja@milaifonatanals.org','Django','Freeman','$2a$10$6HSJpPB7NJ7aEAoevyz6P.hVkO049hDZI0cETOpIHyDVoIm2rWXze','django'),(3,0,3,0,'djangolagartija22@milaifonatanals.org','Django2','Freeman22','$2a$10$gJNw7BJcr40lMsOCHOy4YOxfYkqOe4f32ikI.oP8ztad1WqCLmyRy','django2'),(1,0,4,0,'wq@mail.com','asd','wq','$2a$10$NsEYzPCpGMA1LY3FSeh7Yeexhv0Uoe9cKjOXYbR5rybUsVF7tJ5Lm','asd'),(3,0,5,0,'test2@test.com','Prova2','Proveta2','$2a$10$fNHExW1yKiWeiVwp9BoBqu20vCAIM5PlrZXj8XFAdZpiYnYYOobBS','testing'),(1,0,6,0,'jlop@gmail.com','Joan','Lopez','$2a$10$PYl1QxNjRO8SlaAogP.v9uYNn3AShXuRP515voXCQn41mqBVM739S','jlope'),(1,0,7,0,'bpotrony@milaifontanals.org','bru','potrony','$2a$10$RlzOB9GXhafiY49xiKxP1eVfw48dlo7XbrgYjYC8ReL.O.J0L1mpW','bpotrony'),(4,0,8,0,'jarthur@gmail.com','jonathan','arthur','$2a$10$XX7MW4JAqAI4ml7oVXQDleZaojO1LxDj0zaT6dcMeCvbiJlBS.Xhe','jarthur'),(2,0,9,0,'marksand2001@gmail.com','Mark','Villoria','$2a$10$X0jNwXc.eYFOPa7SMw/sUu0P/AVyVUa9ceCTbzI3uDEe057vtABju','Markitos01'),(3,0,11,0,'djangolagwartija22@milaifonatanals.org','Django2','Freeman22','$2a$10$havcvMm6TL1oYstpY7OmpOdVLQj98YqjhkvytLPQI2kH9WFq0ESPm','djangoqwe2'),(3,0,12,0,'mgotera@gmail.com','marc','gotera','$2a$10$T7VdOonSfJl/X6Eg1QgebueIJZ4n5GdcfgiBpCW56Z762p/2GMYqu','mgotera'),(2,0,13,0,'pgotera@gmail.com','pepe','gotera','$2a$10$xSIK79LoB33Y9YYqwFbK7OID32.rlhztAOWkS6x/.K7Ax2/PjFPSG','pgotera'),(4,0,14,0,'izansv2008@gmail.com','Izan','Sandiumenge','$2a$10$UGoIk2elrVwgWnR3x2cWOeYQhzSosweDGG2El18MQrl7BHNd3HlqS','izansv08'),(1,0,15,0,'sajolion@gmail.com','Sandra','Jojolion','$2a$10$nAVwcyrZf84oE2U4SxhO3OnTJNHjXYaqdoxJqeVWar4aRyt.AQ2J.','suika'),(2,0,16,0,'rpotrony@gmail.com','Roc','Potrony','$2a$10$KTOt9xfAn7vyajr0shn2SOEhrTFjGIvaU.5v59oq/yxjCltwzx0xK','rpotrony'),(1,0,17,0,'mayuunesa@gmail.com','malaka','malakese','$2a$10$zDHyHsei9C/bmAg1pbbj9.ML68bczZQlM6dXMT5u885PmB6moW4a6','malaka'),(1,0,19,0,'yago@gmail.com','yagoba','yagobiense','$2a$10$4YrsOBDHCCba/XFCYjdXrutjVpEpc2s7gMgJSw5dqWTdtjxqrc8c6','yagoba'),(1,0,20,0,'broko@gmail.com','broko','brokoso','$2a$10$LL3iOwIKLF1DCDJgcneyHu6kxVN8Sb0mBZSFYpgM7LnW/bDbhWVYy','broko'),(3,0,21,0,'pol@milaifonatanals.org','pol','carol','$2a$10$CO.sWaZocNIHFFG4uapbuuevPtYOV3BO6yiHMOxn/oWUykFRdvWRm','pol'),(3,0,22,0,'meme@gmail.com','meme','memez','$2a$10$w8I1bQZ59RutLf7i20VSxupUp9L6Vz5rTS9c88IrD9yiprubJQNry','meme'),(1,0,23,0,'teta@gmail.com','teta','tetez','$2a$10$BTSeLN/RMMXe3RmkXj8dmuKEjXeG9zxw9RxWq3kh8EXoVz4zOn/ia','teta'),(3,0,24,0,'alba@milaifonatanals.org','alba','alba','$2a$10$P5MTaCpOt6i9YlfvTAcWvO8adavv46AMdz2BQlKZM4v35kilH8.9G','alba'),(1,0,25,0,'bpot@gmail.com','Bru','Potrony','$2a$10$RgXvR79bsMGtjco44167A.03iBiVO7n.x12E0R/IrQAE2DSqm8trO','bpot'),(3,0,26,0,'rpot@gmail.com','Roc','Potrony','$2a$10$hwvg0tTjHN61YsJQxx2jeurXRExD9HSLOijEURpf7U8hyKJ51pNGG','rpot'),(1,2,27,0,'dsfa@gmail.com','bru','potrony','$2a$10$Ci3iuDw.9axTWaSRGEyBE.BSN72BwiNe.bbgLvl0ngJ6Hk9XRVPhu','bru'),(3,1,28,0,'elena@milaifonatanals.org','elena','elena','$2a$10$ofbuk0roN5CbP6qlafwo7e5gIqmXCXdyGSLanmnbPu/Z.pJJOgx8a','elena'),(1,0,29,0,'mark@gmail.com','mark2','mark2','$2a$10$k86RkSlvP.AxnDjgJhX1feg8s8W02v.KwSo89dUUwd0jTCwX6ENtO','mark2'),(1,0,30,0,'markito@gmail.com','markos','markez','$2a$10$tqeTXmknQfikz7BgQjTY9eof3Lpss4YTc/Rwt4wZ1T89b9t7cuEVm','mark3'),(4,1,31,0,'roc@gmail.com','roc','roc','$2a$10$RJ6kH07jw5nEENrdXzwpHOePP6ZGCo7Z7.JH13wmdF8MpzyNLr6Bi','roc'),(1,11,32,3,'brup@gmail.com','brup','brup','$2a$10$oKaRDV4c8TXp6ZqefcOlN.JJniPiV8fBspcSNE34oOKronV4qhllm','brup'),(2,0,33,0,'rocp@gmail.com','rocp','rocp','$2a$10$6rY9z9tow3BD9F789yEuF.Auqze/wj0kpfsXz80DU.boXal4nz3dm','rocp'),(2,36,34,16,'rege@gmail.com','rege','rege','$2a$10$NrGdEMBJfnQ6HZL5OEZI1ekIzCS/a4/SOfufkEvMgG.Z3df5NsGem','rege'),(1,39,35,16,'malaka@gmail.com','rega','malaka','$2a$10$PhWTQ6XCHSsgLvkZQ49PJeToOX.ghjiDBRn7JYvvQKSm99mRgl3kq','rega'),(4,11,36,5,'kwekubaah@gmail.com','Kweku  Baah','Arthur','$2a$10$P8bohVU9WX.ZiPxHKIVOheuMGpp9PdUshcy260PcOOO55JLDjPQTa','kweku'),(1,40,37,15,'masandivillo01@gmail.com','Markitos','Do Samba','$2a$10$TGGgOgbp8qQXubyAqqk74OyAubsgc0F7kUXzlgtUnIVVp8wboORYi','Mayuu'),(4,38,38,22,'izansv08@gmail.com','izan','sv','$2a$10$MF342fQwqYP.4hlK/Bec7eAxTZNe6gFw.1d9/LCD4.Pdaeflvsd3O','izan');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-28 21:07:38
