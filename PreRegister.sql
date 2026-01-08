CREATE DATABASE  IF NOT EXISTS `preregistration` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `preregistration`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: preregistration
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `course_code` varchar(10) NOT NULL,
  `course_name` varchar(100) NOT NULL,
  `credit_hour` int NOT NULL,
  `prerequisite_course_code` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`course_code`),
  KEY `prerequisite_course_code` (`prerequisite_course_code`),
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`prerequisite_course_code`) REFERENCES `courses` (`course_code`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES ('BICS2301','Enterprise Networks',3,NULL),('BICS2302','Data Structures and Algorithms',3,NULL),('BICS2303','Intelligent Systems',3,NULL),('BICS2306','Software Engineering',3,NULL),('BIIT2301','User Experience Design',3,NULL),('CCLM2020','Leadership and Management',1,NULL),('SCSH1201','Sustainable Development',2,NULL),('UNGS2290','Knowledge & Civilization in Islam',2,NULL);
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preregistrations`
--

DROP TABLE IF EXISTS `preregistrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `preregistrations` (
  `prereg_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `course_code` varchar(10) NOT NULL,
  `section_id` int NOT NULL,
  `semester` int NOT NULL,
  `year` int NOT NULL,
  `status` enum('Completed','Ongoing','Registered') DEFAULT 'Registered',
  `prereg_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`prereg_id`),
  UNIQUE KEY `student_id` (`student_id`,`course_code`,`semester`,`year`),
  KEY `course_code` (`course_code`),
  KEY `section_id` (`section_id`),
  CONSTRAINT `preregistrations_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `preregistrations_ibfk_2` FOREIGN KEY (`course_code`) REFERENCES `courses` (`course_code`) ON DELETE CASCADE,
  CONSTRAINT `preregistrations_ibfk_3` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preregistrations`
--

LOCK TABLES `preregistrations` WRITE;
/*!40000 ALTER TABLE `preregistrations` DISABLE KEYS */;
INSERT INTO `preregistrations` VALUES (1,1,'BICS2301',1,1,2025,'Completed','2026-01-08 14:38:48'),(2,1,'BICS2302',2,2,2026,'Ongoing','2026-01-08 14:38:48'),(3,2,'BICS2301',1,1,2025,'Completed','2026-01-08 14:38:48'),(4,3,'BICS2303',3,2,2026,'Ongoing','2026-01-08 14:38:48'),(5,4,'BICS2306',4,2,2026,'Ongoing','2026-01-08 14:38:48');
/*!40000 ALTER TABLE `preregistrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sections`
--

DROP TABLE IF EXISTS `sections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sections` (
  `section_id` int NOT NULL AUTO_INCREMENT,
  `course_code` varchar(10) NOT NULL,
  `section_code` varchar(10) NOT NULL,
  `lecturer_name` varchar(100) NOT NULL,
  `schedule` varchar(50) DEFAULT NULL,
  `venue` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`section_id`),
  KEY `course_code` (`course_code`),
  CONSTRAINT `sections_ibfk_1` FOREIGN KEY (`course_code`) REFERENCES `courses` (`course_code`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sections`
--

LOCK TABLES `sections` WRITE;
/*!40000 ALTER TABLE `sections` DISABLE KEYS */;
INSERT INTO `sections` VALUES (1,'BICS2301','7','Adamu Abubakar Ibrahim','T-TH 1400-1520','ICT CISCO LAB LEVEL 4C'),(2,'BICS2302','2','Aizal Yusrina Idris','M-W 1530-1650','ICT LR 19 LEVEL 4C'),(3,'BICS2303','4','Dini Oktarina Dwi Handayani','M-W 0830-0950','ICT TL-E5-01 LEVEL 5E'),(4,'BICS2306','4','Normi Sham Bt. Awang Abu Bakar','M-W 1000-1120','ICT TL-D5-04 LEVEL 5D'),(5,'BIIT2301','3','Rafidah Binti Isa','M-W 1130-1250','ICT LR 11 LEVEL 2C'),(6,'CCLM2020','26','Rs Azira Binti Sulaiman','TUE 1700-1900','KENMS 3.10 LEVEL 3'),(7,'SCSH1201','4','Salman Ahmed Shaikh','FRI 900-1100','ENG LT2 E0-1'),(8,'UNGS2290','22','Abdulwahed Jalal Nori','TUE 1000-1150','IRK LR 14 RK LVL 1');
/*!40000 ALTER TABLE `sections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `matric_no` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `current_semester` int NOT NULL,
  `current_year` int NOT NULL,
  `level_of_study` int NOT NULL,
  `total_credits` int DEFAULT '0',
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `matric_no` (`matric_no`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'2411001','Ahmad Faizal','ahmad.faizal@student.edu.my','pass1234',2,2,2,0),(2,'2411002','Siti Nur Aisyah','siti.aisyah@student.edu.my','password',2,2,2,0),(3,'2411003','Muhammad Iqbal','iqbal@student.edu.my','abc',2,2,2,0),(4,'2411004','Aminah Binti Rahman','aminah.rahman@student.edu.my','def',2,2,2,0);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-08 23:53:18
