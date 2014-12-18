--
-- Host: %    Database: philippj_tinte
-- ------------------------------------------------------
-- Server version	5.6.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Temporary table structure for view `every_lied_in_every_liederbuch_view`
--


DROP TABLE IF EXISTS `every_lied_in_every_liederbuch_view`;
/*!50001 DROP VIEW IF EXISTS `every_lied_in_every_liederbuch_view`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `every_lied_in_every_liederbuch_view` (
  `id` tinyint NOT NULL,
  `Titel` tinyint NOT NULL,
  `id_liederbuch` tinyint NOT NULL,
  `Buchname` tinyint NOT NULL,
  `Rubrik` tinyint NOT NULL,
  `tonality` tinyint NOT NULL,
  `created_at` tinyint NOT NULL,
  `updated_at` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `fkliederbuchlied`
--

DROP TABLE IF EXISTS `fkliederbuchlied`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fkliederbuchlied` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `liederbuch_id` bigint(20) NOT NULL,
  `lied_id` bigint(20) NOT NULL,
  `Liednr` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `PreventFromDifferentLiedNrForTheSameSongInASongbook` (`liederbuch_id`,`lied_id`),
  UNIQUE KEY `PreventFromHavingTheSameLiedNrTwiceInOneSongbook` (`liederbuch_id`,`Liednr`),
  KEY `lied_idx` (`lied_id`),
  KEY `liederbuch_idx` (`liederbuch_id`),
  CONSTRAINT `fkLiederbuchLiedLied` FOREIGN KEY (`lied_id`) REFERENCES `lied` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkLiederbuchLiedLiederbuch` FOREIGN KEY (`liederbuch_id`) REFERENCES `liederbuch` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fkliederbuchlied`
--

LOCK TABLES `fkliederbuchlied` WRITE;
/*!40000 ALTER TABLE `fkliederbuchlied` DISABLE KEYS */;
INSERT INTO `fkliederbuchlied` (`id`, `liederbuch_id`, `lied_id`, `Liednr`) VALUES (1,1,1,'1000');
INSERT INTO `fkliederbuchlied` (`id`, `liederbuch_id`, `lied_id`, `Liednr`) VALUES (2,1,2,'1001');
INSERT INTO `fkliederbuchlied` (`id`, `liederbuch_id`, `lied_id`, `Liednr`) VALUES (3,2,1,'100');
INSERT INTO `fkliederbuchlied` (`id`, `liederbuch_id`, `lied_id`, `Liednr`) VALUES (8,1,6,'39');
/*!40000 ALTER TABLE `fkliederbuchlied` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fkliedlied`
--

DROP TABLE IF EXISTS `fkliedlied`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fkliedlied` (
  `lied1_id` bigint(20) NOT NULL,
  `lied2_id` bigint(20) NOT NULL,
  `comment` text,
  `type` int(11) DEFAULT NULL,
  KEY `PrimaryKey` (`lied1_id`,`lied2_id`),
  KEY `fkLiedLiedLied1_idx` (`lied1_id`),
  KEY `fkLiedLiedLied2_idx` (`lied2_id`),
  CONSTRAINT `fkLiedLiedLied1` FOREIGN KEY (`lied1_id`) REFERENCES `lied` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fkLiedLiedLied2` FOREIGN KEY (`lied2_id`) REFERENCES `lied` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fkliedlied`
--

LOCK TABLES `fkliedlied` WRITE;
/*!40000 ALTER TABLE `fkliedlied` DISABLE KEYS */;
/*!40000 ALTER TABLE `fkliedlied` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(5) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `language`
--

LOCK TABLES `language` WRITE;
/*!40000 ALTER TABLE `language` DISABLE KEYS */;
INSERT INTO `language` (`id`, `code`, `name`) VALUES (1,'DE','Deutsch');
INSERT INTO `language` (`id`, `code`, `name`) VALUES (2,'EN','Englisch');
INSERT INTO `language` (`id`, `code`, `name`) VALUES (3,'FR','Französisch');
INSERT INTO `language` (`id`, `code`, `name`) VALUES (4,'SP','Spanisch');
INSERT INTO `language` (`id`, `code`, `name`) VALUES (5,'HEBR','Hebräisch');
/*!40000 ALTER TABLE `language` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lied`
--

DROP TABLE IF EXISTS `lied`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lied` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Titel` text NOT NULL,
  `rubrik_id` bigint(20) DEFAULT NULL,
  `Stichwoerter` text,
  `Bemerkungen` text,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `externalLink` text,
  `lastEditUser_id` bigint(20) NOT NULL,
  `tonality` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `rubrik_idx` (`rubrik_id`),
  KEY `lastEditUser_idx` (`lastEditUser_id`),
  CONSTRAINT `liedLastEditUser` FOREIGN KEY (`lastEditUser_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `liedRubrik` FOREIGN KEY (`rubrik_id`) REFERENCES `rubrik` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lied`
--

LOCK TABLES `lied` WRITE;
/*!40000 ALTER TABLE `lied` DISABLE KEYS */;
INSERT INTO `lied` (`id`, `Titel`, `rubrik_id`, `Stichwoerter`, `Bemerkungen`, `created_at`, `updated_at`, `externalLink`, `lastEditUser_id`, `tonality`) VALUES (1,'Bless the Lord my Soul',3,NULL,NULL,'2014-06-11 22:06:48','2014-06-11 22:06:48',NULL,1,'d');
INSERT INTO `lied` (`id`, `Titel`, `rubrik_id`, `Stichwoerter`, `Bemerkungen`, `created_at`, `updated_at`, `externalLink`, `lastEditUser_id`, `tonality`) VALUES (2,'Adoramus Te, Domine!',3,NULL,NULL,'2014-06-11 22:06:48','2014-06-11 22:06:48',NULL,3,'G');
INSERT INTO `lied` (`id`, `Titel`, `rubrik_id`, `Stichwoerter`, `Bemerkungen`, `created_at`, `updated_at`, `externalLink`, `lastEditUser_id`, `tonality`) VALUES (3,'Christus ist auferweckt, Halleluja',1,NULL,NULL,'2014-06-11 22:06:48','2014-06-11 22:06:48',NULL,1,'F');
INSERT INTO `lied` (`id`, `Titel`, `rubrik_id`, `Stichwoerter`, `Bemerkungen`, `created_at`, `updated_at`, `externalLink`, `lastEditUser_id`, `tonality`) VALUES (6,'Lobe den Herrn, meine Seele',4,NULL,NULL,'2014-06-11 22:06:48','2014-10-02 11:59:14',NULL,3,'E');
/*!40000 ALTER TABLE `lied` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`philippj_tinte`@`%`*/ /*!50003 TRIGGER onInsertLiedSetCreatedAt BEFORE INSERT ON lied FOR EACH ROW set NEW.created_at = NOW() */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Temporary table structure for view `lied_table_view`
--

DROP TABLE IF EXISTS `lied_table_view`;
/*!50001 DROP VIEW IF EXISTS `lied_table_view`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `lied_table_view` (
  `id` tinyint NOT NULL,
  `Titel` tinyint NOT NULL,
  `Rubrik` tinyint NOT NULL,
  `tonality` tinyint NOT NULL,
  `created_at` tinyint NOT NULL,
  `updated_at` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `liederbuch`
--

DROP TABLE IF EXISTS `liederbuch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liederbuch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Buchname` text,
  `Beschreibung` text,
  `mnemonic` varchar(5) NOT NULL,
  `locked` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liederbuch`
--

LOCK TABLES `liederbuch` WRITE;
/*!40000 ALTER TABLE `liederbuch` DISABLE KEYS */;
INSERT INTO `liederbuch` (`id`, `Buchname`, `Beschreibung`, `mnemonic`, `locked`) VALUES (1,'Adoray Liederordner','Der grosse Liederordner, der sämtlichen Adorays der Schweiz zur Verfügung steht.','AL',1);
INSERT INTO `liederbuch` (`id`, `Buchname`, `Beschreibung`, `mnemonic`, `locked`) VALUES (2,'Dir singen wir 2','Das klassische Grüne','DSW2',1);
INSERT INTO `liederbuch` (`id`, `Buchname`, `Beschreibung`, `mnemonic`, `locked`) VALUES (3,'Adonai','Das Liederbuch Adonai von den Seligpreisungen Zug.','AI',1);
INSERT INTO `liederbuch` (`id`, `Buchname`, `Beschreibung`, `mnemonic`, `locked`) VALUES (4,'Luzern','Die eigenen Lieder des Adoray Luzern','LU',0);
INSERT INTO `liederbuch` (`id`, `Buchname`, `Beschreibung`, `mnemonic`, `locked`) VALUES (5,'Zug','Die eigenen Lieder des Adoray Zug','ZG',0);
/*!40000 ALTER TABLE `liederbuch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liedtext`
--

DROP TABLE IF EXISTS `liedtext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liedtext` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lied_id` bigint(20) NOT NULL,
  `Ueberschrift` text,
  `UeberschriftTyp` text,
  `Strophe` text,
  `refrain_id` bigint(20) DEFAULT NULL,
  `Reihenfolge` int(11) DEFAULT NULL,
  `language_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `language_id` (`language_id`),
  KEY `lied_idx` (`lied_id`),
  KEY `refrain_idx` (`refrain_id`),
  CONSTRAINT `liedtextLanguage` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `liedtextLied` FOREIGN KEY (`lied_id`) REFERENCES `lied` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `liedtextRefrain` FOREIGN KEY (`refrain_id`) REFERENCES `refrain` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liedtext`
--

LOCK TABLES `liedtext` WRITE;
/*!40000 ALTER TABLE `liedtext` DISABLE KEYS */;
INSERT INTO `liedtext` (`id`, `lied_id`, `Ueberschrift`, `UeberschriftTyp`, `Strophe`, `refrain_id`, `Reihenfolge`, `language_id`) VALUES (1,6,NULL,NULL,'Der meine Sünden vergeben hat,<br/>der mich von Krankheit gesund gemacht,<br/>den will ich preisen mit Psalmen und Weisen,<br/>von Herzen Ihm ewiglich singen:<br/>',1,2,1);
INSERT INTO `liedtext` (`id`, `lied_id`, `Ueberschrift`, `UeberschriftTyp`, `Strophe`, `refrain_id`, `Reihenfolge`, `language_id`) VALUES (2,6,NULL,NULL,'Der mich im Leiden getröstet hat,<br/>der meinen Mund wieder fröhlich macht,<br/>den will ich preisen mit Psalmen und Weisen,<br/>von Herzen Ihm ewiglich singen:',1,1,1);
INSERT INTO `liedtext` (`id`, `lied_id`, `Ueberschrift`, `UeberschriftTyp`, `Strophe`, `refrain_id`, `Reihenfolge`, `language_id`) VALUES (3,6,NULL,NULL,'Ohne Refrain!<br>Der mich vom Tode errettet hat,<br/>der mich behütet bei Tag und Nacht,<br/>den will ich preisen mit Psalmen und Weisen,<br/>von Herzen Ihm ewiglich singen:',NULL,3,1);
INSERT INTO `liedtext` (`id`, `lied_id`, `Ueberschrift`, `UeberschriftTyp`, `Strophe`, `refrain_id`, `Reihenfolge`, `language_id`) VALUES (4,6,NULL,NULL,'Der Erd und Himmel zusammenhält,<br/>unter Sein göttliches Jawort stellt,<br/>den will ich preisen mit Psalmen und Weisen,<br/>von Herzen Ihm ewiglich singen:',1,4,1);
/*!40000 ALTER TABLE `liedtext` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `liedview`
--

DROP TABLE IF EXISTS `liedview`;
/*!50001 DROP VIEW IF EXISTS `liedview`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `liedview` (
  `id` tinyint NOT NULL,
  `Liednr` tinyint NOT NULL,
  `Titel` tinyint NOT NULL,
  `id_liederbuch` tinyint NOT NULL,
  `Buchname` tinyint NOT NULL,
  `Rubrik` tinyint NOT NULL,
  `tonality` tinyint NOT NULL,
  `created_at` tinyint NOT NULL,
  `updated_at` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `logging`
--

DROP TABLE IF EXISTS `logging`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logging` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `table` varchar(45) DEFAULT NULL,
  `message` text,
  `user_id` bigint(20) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `logger` varchar(256) DEFAULT NULL,
  `level` varchar(32) DEFAULT NULL,
  `thread` int(11) DEFAULT NULL,
  `file` varchar(255) DEFAULT NULL,
  `line` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `loggingUser` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logging`
--

LOCK TABLES `logging` WRITE;
/*!40000 ALTER TABLE `logging` DISABLE KEYS */;
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (1,NULL,'User logged in. (user=correct@login.ch, id=3)',NULL,'2014-10-02 11:59:09','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\auth\\Authentication.php','45');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (2,NULL,'User logged in. (user=correct@login.ch, id=3)',NULL,'2014-10-02 11:59:09','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\auth\\Authentication.php','45');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (3,NULL,'3 ## correct@login.ch ## numberinbook ## select *, (select Liednr from fkliederbuchlied l where l.liederbuch_id = b.id and l.lied_id = ?) Liednr from liederbuch b; ## i, 1',NULL,'2014-10-02 11:59:09','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (4,NULL,'3 ## correct@login.ch ## liedtext ## SELECT * FROM liedtext WHERE  lied_id = ?  ORDER BY Reihenfolge ASC ## s, 6',NULL,'2014-10-02 11:59:10','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (5,NULL,'3 ## correct@login.ch ## user ## DELETE FROM user WHERE id = 8 ## ',NULL,'2014-10-02 11:59:10','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (6,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user ## ',NULL,'2014-10-02 11:59:10','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (7,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user WHERE  firstname = ?  AND  lastname = ?  ## ss, Peter, Schnur',NULL,'2014-10-02 11:59:10','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (8,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user ORDER BY lastname ASC ## ',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (9,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user LIMIT 1, 1 ## ',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (10,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user LIMIT 1, 1 ## ',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (11,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user WHERE  additionalInfos = ?  ## s, Luzern',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (12,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user WHERE  id = ?  ## s, 3',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (13,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user ## ',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (14,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user ## ',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (15,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user ORDER BY id DESC ## ',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (16,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user WHERE  firstname = ?  LIMIT 1, 1 ## s, Peter',NULL,'2014-10-02 11:59:11','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (17,NULL,'3 ## correct@login.ch ## lied ## DELETE FROM lied WHERE id = 4 ## ',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (18,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id_liederbuch = ?  ORDER BY ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## s, 1',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (19,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id = ?  AND  id_liederbuch = ?  ORDER BY ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## ss, 1, 1',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (20,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id = ?  AND  id_liederbuch = ?  ORDER BY ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## ss, 1, 2',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (21,NULL,'User logged in. (user=correct@login.ch, id=3)',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\auth\\Authentication.php','45');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (22,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id_liederbuch = ?  AND  (  Titel LIKE ?  OR  Liednr = ?  )  ORDER BY ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## sss, 1, %Halleluja%, Halleluja',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (23,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id_liederbuch = ?  AND  (  Titel LIKE ?  OR  Liednr = ?  )  ORDER BY ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## sss, 1, %le%, le',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (24,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id_liederbuch = ?  ORDER BY Tonality ASC, ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## s, 1',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (25,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id_liederbuch = ?  ORDER BY ISNULL(Liednr) DESC, Liednr * 1 DESC, Liednr DESC, ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## s, 1',NULL,'2014-10-02 11:59:12','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (26,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id_liederbuch = ?  AND  (  Titel LIKE ?  OR  Liednr = ?  )  ORDER BY ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## sss, 1, %1001%, 1001',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (27,NULL,'3 ## correct@login.ch ## liedview ## SELECT * FROM liedview WHERE  id_liederbuch = ?  AND  (  Titel LIKE ?  OR  Liednr = ?  )  ORDER BY ISNULL(Liednr) ASC, Liednr * 1 ASC, Liednr ASC ## sss, 1, %100%, 100',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (28,NULL,'3 ## correct@login.ch ## refrain ## SELECT * FROM refrain WHERE  lied_id = ?  ORDER BY RefrainNr ASC ## s, 6',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (29,NULL,'User logged in. (user=correct@login.ch, id=3)',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\auth\\Authentication.php','45');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (30,NULL,'User logged in. (user=correct@login.ch, id=3)',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\auth\\Authentication.php','45');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (31,NULL,'User logged in. (user=correct@login.ch, id=3)',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\auth\\Authentication.php','45');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (32,NULL,'User logged in. (user=correct@login.ch, id=3)',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\auth\\Authentication.php','45');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (33,NULL,'3 ## correct@login.ch ## user ## SELECT * FROM user ## ',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (34,NULL,'3 ## correct@login.ch ## rubrik ## SELECT * FROM rubrik ## ',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (35,NULL,'3 ## correct@login.ch ## liedtext ## SELECT * FROM liedtext WHERE  lied_id = ?  ORDER BY Reihenfolge ASC ## s, 6',NULL,'2014-10-02 11:59:13','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (36,NULL,'3 ## correct@login.ch ## liedtext ## SELECT * FROM liedtext WHERE  lied_id = ?  ORDER BY Reihenfolge ASC ## s, 6',NULL,'2014-10-02 11:59:14','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (37,NULL,'3 ## correct@login.ch ## liedtext ## SELECT * FROM liedtext WHERE  lied_id = ?  ORDER BY Reihenfolge ASC ## s, 6',NULL,'2014-10-02 11:59:14','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (38,NULL,'3 ## correct@login.ch ## liedtext ## SELECT * FROM liedtext WHERE  lied_id = ?  ORDER BY Reihenfolge ASC ## s, 6',NULL,'2014-10-02 11:59:14','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
INSERT INTO `logging` (`id`, `table`, `message`, `user_id`, `timestamp`, `logger`, `level`, `thread`, `file`, `line`) VALUES (39,NULL,'3 ## correct@login.ch ## lied ## DELETE FROM lied WHERE id = 5 ## ',NULL,'2014-10-02 11:59:14','dbLogger','INFO',8360,'C:\\Workspace\\pdt\\AdoraySongserver\\src\\Scotty\\changebacktrack\\ChangeBacktrack.php','21');
/*!40000 ALTER TABLE `logging` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media`
--

DROP TABLE IF EXISTS `media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `media` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lied_id` bigint(20) NOT NULL,
  `path` text NOT NULL,
  `remark` text,
  PRIMARY KEY (`id`),
  KEY `mediaLied` (`lied_id`),
  CONSTRAINT `mediaLied` FOREIGN KEY (`lied_id`) REFERENCES `lied` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media`
--

LOCK TABLES `media` WRITE;
/*!40000 ALTER TABLE `media` DISABLE KEYS */;
/*!40000 ALTER TABLE `media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pdf`
--

DROP TABLE IF EXISTS `pdf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pdf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `path` text,
  `export` tinyint(1) DEFAULT NULL,
  `lied_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pdfLied` (`lied_id`),
  CONSTRAINT `pdfLied` FOREIGN KEY (`lied_id`) REFERENCES `lied` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pdf`
--

LOCK TABLES `pdf` WRITE;
/*!40000 ALTER TABLE `pdf` DISABLE KEYS */;
/*!40000 ALTER TABLE `pdf` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refrain`
--

DROP TABLE IF EXISTS `refrain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refrain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lied_id` bigint(20) DEFAULT NULL,
  `Reihenfolge` int(11) DEFAULT NULL,
  `Refrain` text,
  `language_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `language_id` (`language_id`),
  KEY `lied_idx` (`lied_id`),
  CONSTRAINT `refrainLanguage` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `refrainLied` FOREIGN KEY (`lied_id`) REFERENCES `lied` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refrain`
--

LOCK TABLES `refrain` WRITE;
/*!40000 ALTER TABLE `refrain` DISABLE KEYS */;
INSERT INTO `refrain` (`id`, `lied_id`, `Reihenfolge`, `Refrain`, `language_id`) VALUES (1,6,1,'Lobe den Herrn, meine Seele,<br/>und Seinen heiligen Namen.<br/>Was Er dir Gutes getan hat,<br/>Seele, vergiss es nicht, Amen.<br/><br/>|: Lobe, lobe den Herrn, <br/>lobe den Herrn, meine Seele. :|',1);
/*!40000 ALTER TABLE `refrain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rubrik`
--

DROP TABLE IF EXISTS `rubrik`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rubrik` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Rubrik` text,
  `Reihenfolge` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rubrik`
--

LOCK TABLES `rubrik` WRITE;
/*!40000 ALTER TABLE `rubrik` DISABLE KEYS */;
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (1,'Auferstehung',1);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (2,'Heilg Geist',2);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (3,'Anbetung',3);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (4,'Übergangslieder',4);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (5,'Maria',5);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (6,'Scharnierlieder',6);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (7,'Thereslieder',7);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (8,'Messlieder',8);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (9,'Dank',9);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (10,'Bitte',10);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (11,'Segen',11);
INSERT INTO `rubrik` (`id`, `Rubrik`, `Reihenfolge`) VALUES (12,'Andere',12);
/*!40000 ALTER TABLE `rubrik` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settings` (
  `key` varchar(50) NOT NULL,
  `value` text,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settings`
--

LOCK TABLES `settings` WRITE;
/*!40000 ALTER TABLE `settings` DISABLE KEYS */;
INSERT INTO `settings` (`key`, `value`) VALUES ('database.schema.version','100011');
/*!40000 ALTER TABLE `settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(150) DEFAULT NULL,
  `hash` varchar(200) DEFAULT NULL,
  `firstname` varchar(100) DEFAULT NULL,
  `lastname` varchar(100) DEFAULT NULL,
  `rights` text,
  `additionalInfos` text,
  `active` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`id`, `email`, `hash`, `firstname`, `lastname`, `rights`, `additionalInfos`, `active`) VALUES (1,'philippjenni@bluemail.ch','48d59cd2e9b434f37f41f7f0848205b792e589fa','Philipp','Jenni',NULL,'Luzern',1);
INSERT INTO `user` (`id`, `email`, `hash`, `firstname`, `lastname`, `rights`, `additionalInfos`, `active`) VALUES (2,'notActive@google.com','3da541559918a808c2402bba5012f6c60b27661c','Not','Active',NULL,'Luzern',NULL);
INSERT INTO `user` (`id`, `email`, `hash`, `firstname`, `lastname`, `rights`, `additionalInfos`, `active`) VALUES (3,'correct@login.ch','4829764b4082d0c138feac79de89b24275a769dd','Correct-Hans','Login-Bucher',NULL,'Bern',1);
INSERT INTO `user` (`id`, `email`, `hash`, `firstname`, `lastname`, `rights`, `additionalInfos`, `active`) VALUES (4,'andre.anders@genf.ch','3da541559918a808c2402bba5012f6c60b27661c','Andre','Anders',NULL,'Genf',1);
INSERT INTO `user` (`id`, `email`, `hash`, `firstname`, `lastname`, `rights`, `additionalInfos`, `active`) VALUES (5,'gleicher.name@1.ch','3da541559918a808c2402bba5012f6c60b27661c','Peter','Sowieso',NULL,'Fribourg',1);
INSERT INTO `user` (`id`, `email`, `hash`, `firstname`, `lastname`, `rights`, `additionalInfos`, `active`) VALUES (6,'gleicher.name@2.ch','3da541559918a808c2402bba5012f6c60b27661c','Peter','Schnur',NULL,'Fribourg',1);
INSERT INTO `user` (`id`, `email`, `hash`, `firstname`, `lastname`, `rights`, `additionalInfos`, `active`) VALUES (7,'gleicher.name@3.ch','3da541559918a808c2402bba5012f6c60b27661c','Angela','Schnur',NULL,'Fribourg',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `every_lied_in_every_liederbuch_view`
--

/*!50001 DROP TABLE IF EXISTS `every_lied_in_every_liederbuch_view`*/;
/*!50001 DROP VIEW IF EXISTS `every_lied_in_every_liederbuch_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=MERGE */
/*!50013 DEFINER=`philippj_tinte`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `every_lied_in_every_liederbuch_view` AS select `l`.`id` AS `id`,`l`.`Titel` AS `Titel`,`b`.`id` AS `id_liederbuch`,`b`.`Buchname` AS `Buchname`,`l`.`Rubrik` AS `Rubrik`,`l`.`tonality` AS `tonality`,`l`.`created_at` AS `created_at`,`l`.`updated_at` AS `updated_at` from (`lied_table_view` `l` join `liederbuch` `b`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `lied_table_view`
--

/*!50001 DROP TABLE IF EXISTS `lied_table_view`*/;
/*!50001 DROP VIEW IF EXISTS `lied_table_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=MERGE */
/*!50013 DEFINER=`philippj_tinte`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `lied_table_view` AS select `l`.`id` AS `id`,`l`.`Titel` AS `Titel`,`r`.`Rubrik` AS `Rubrik`,`l`.`tonality` AS `tonality`,`l`.`created_at` AS `created_at`,`l`.`updated_at` AS `updated_at` from (`lied` `l` left join `rubrik` `r` on((`l`.`rubrik_id` = `r`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `liedview`
--

/*!50001 DROP TABLE IF EXISTS `liedview`*/;
/*!50001 DROP VIEW IF EXISTS `liedview`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=MERGE */
/*!50013 DEFINER=`philippj_tinte`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `liedview` AS select `l`.`id` AS `id`,`ll`.`Liednr` AS `Liednr`,`l`.`Titel` AS `Titel`,`l`.`id_liederbuch` AS `id_liederbuch`,`l`.`Buchname` AS `Buchname`,`l`.`Rubrik` AS `Rubrik`,`l`.`tonality` AS `tonality`,`l`.`created_at` AS `created_at`,`l`.`updated_at` AS `updated_at` from (`every_lied_in_every_liederbuch_view` `l` left join `fkliederbuchlied` `ll` on(((`l`.`id` = `ll`.`lied_id`) and (`l`.`id_liederbuch` = `ll`.`liederbuch_id`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-12-08 17:54:00
