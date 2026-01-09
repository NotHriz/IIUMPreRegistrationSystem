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
  `cat` enum('CORE','ELECTIVE') DEFAULT 'CORE',
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
INSERT INTO `courses` VALUES ('BICS1301','Elements of Programming',3,NULL,'CORE'),('BICS1302','Introduction to Computer Organisation',3,NULL,'CORE'),('BICS1303','Computer Networking',3,NULL,'CORE'),('BICS1304','Object-Oriented Programming',3,NULL,'CORE'),('BICS1305','Discrete Structures',3,NULL,'CORE'),('BICS1306','Digital & Embedded Systems',3,NULL,'CORE'),('BICS2301','Enterprise Networks',3,NULL,'CORE'),('BICS2302','Data Structures and Algorithms',3,NULL,'CORE'),('BICS2303','Intelligent Systems',3,NULL,'CORE'),('BICS2306','Software Engineering',3,NULL,'CORE'),('BIIT1301','Database Programming',3,NULL,'CORE'),('BIIT1303','System Analysis and Design',3,NULL,'CORE'),('BIIT2301','User Experience Design',3,NULL,'CORE'),('CCLM2020','Leadership and Management',1,NULL,'ELECTIVE'),('LEED1301','English for Academic Writing',3,NULL,'CORE'),('SCSH1201','Sustainable Development',2,NULL,'ELECTIVE'),('UNGS1301','Basic Philosophy and Islamic Worldview',3,NULL,'CORE'),('UNGS2290','Knowledge & Civilization in Islam',2,NULL,'ELECTIVE');
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
  `status` enum('Completed','Ongoing','Registered') DEFAULT 'Registered',
  `prereg_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`prereg_id`),
  UNIQUE KEY `student_id` (`student_id`,`course_code`),
  KEY `course_code` (`course_code`),
  KEY `section_id` (`section_id`),
  CONSTRAINT `preregistrations_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `preregistrations_ibfk_2` FOREIGN KEY (`course_code`) REFERENCES `courses` (`course_code`) ON DELETE CASCADE,
  CONSTRAINT `preregistrations_ibfk_3` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preregistrations`
--

LOCK TABLES `preregistrations` WRITE;
/*!40000 ALTER TABLE `preregistrations` DISABLE KEYS */;
INSERT INTO `preregistrations` VALUES (1,1,'BICS2301',1,'Completed','2026-01-09 18:07:16'),(2,1,'BICS2302',2,'Ongoing','2026-01-09 18:07:16'),(3,2,'BICS2301',1,'Completed','2026-01-09 18:07:16'),(4,3,'BICS2303',3,'Ongoing','2026-01-09 18:07:16'),(5,4,'BICS2306',4,'Ongoing','2026-01-09 18:07:16'),(9,6,'BICS1301',12,'Registered','2026-01-09 18:07:16'),(10,6,'BICS1302',13,'Registered','2026-01-09 18:07:16'),(11,6,'LEED1301',13,'Registered','2026-01-09 18:07:16'),(12,7,'BICS2301',1,'Registered','2026-01-09 18:07:16'),(13,7,'BICS2302',2,'Registered','2026-01-09 18:07:16'),(14,7,'SCSH1201',7,'Registered','2026-01-09 18:07:16'),(20,5,'SCSH1201',7,'Registered','2026-01-09 18:30:26'),(21,5,'UNGS2290',8,'Registered','2026-01-09 18:30:31'),(23,5,'CCLM2020',6,'Registered','2026-01-09 18:41:42'),(24,5,'BICS1301',24,'Registered','2026-01-09 18:41:48');
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
  `curr_capacity` int DEFAULT NULL,
  `schedule` varchar(50) DEFAULT NULL,
  `venue` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`section_id`),
  KEY `course_code` (`course_code`),
  CONSTRAINT `sections_ibfk_1` FOREIGN KEY (`course_code`) REFERENCES `courses` (`course_code`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sections`
--

LOCK TABLES `sections` WRITE;
/*!40000 ALTER TABLE `sections` DISABLE KEYS */;
INSERT INTO `sections` VALUES (1,'BICS2301','7','Adamu Abubakar Ibrahim',30,'T-TH 1400-1520','ICT CISCO LAB LEVEL 4C'),(2,'BICS2302','2','Aizal Yusrina Idris',27,'M-W 1530-1650','ICT LR 19 LEVEL 4C'),(3,'BICS2303','4','Dini Oktarina Dwi Handayani',17,'M-W 0830-0950','ICT TL-E5-01 LEVEL 5E'),(4,'BICS2306','4','Normi Sham Bt. Awang Abu Bakar',24,'M-W 1000-1120','ICT TL-D5-04 LEVEL 5D'),(5,'BIIT2301','3','Rafidah Binti Isa',6,'M-W 1130-1250','ICT LR 11 LEVEL 2C'),(6,'CCLM2020','26','Rs Azira Binti Sulaiman',13,'T 1700-1900','KENMS 3.10 LEVEL 3'),(7,'SCSH1201','4','Salman Ahmed Shaikh',16,'F 9000-1100','ENG LT2 E0-1'),(8,'UNGS2290','22','Abdulwahed Jalal Nori',10,'T 1000-1150','IRK LR 14 RK LVL 1'),(9,'BICS1303','5','Adamu Abubakar Ibrahim',12,'T-TH 1000-1120','ICT CISCO LAB'),(10,'BICS1303','6','Mubarak Al-Haddad',30,'M-W 1400-1520','ICT TL-E4-01'),(11,'BICS1304','12','Dini Oktarina Dwi Handayani',15,'M-W 1400-1520','ICT TEACH LAB 6'),(12,'BICS1304','13','Omar Al-Khattab',8,'T-TH 1530-1650','ICT LR 11'),(13,'BICS1305','8','Shakiroh Binti Khamis',29,'M-W 1130-1250','ICT LR 12'),(14,'BICS1305','9','Ahmad Al-Ghazali',5,'T-TH 1400-1520','ICT LR 14'),(15,'BICS1306','2','Asmarani Binti Ahmad Puzi',22,'M-W 1000-1120','ICT LT 2'),(16,'BICS1306','3','Hassan Al-Basri',10,'W 1400-1650','ICT LT 1'),(17,'LEED1301','13','Arlina Zura Binti Zuber',18,'T-TH 1400-1530','CFL SOSDEL LAB 3'),(18,'UNGS1301','31','Mohd. Abbas Bin Abdul Razak',25,'M-W 0830-0950','ADM LT 5'),(19,'BICS2301','8','Mubarak Al-Haddad',14,'M-W 0830-0950','ICT CISCO LAB'),(20,'BICS2302','3','Hussein Bin Ali',29,'T-TH 1400-1520','ICT LR 11'),(21,'BICS2303','5','Zubair Al-Faruq',11,'M-W 1400-1520','ICT LR 12'),(22,'BICS2306','5','Khalid Bin Walid',3,'M-W 1530-1650','ICT LT 1'),(23,'BIIT2301','4','Siti Aminah',20,'F 0830-1120','ICT TEACH LAB 8'),(24,'BICS1301','3','Mohd. Syarqawy Bin Hamzah',27,'T-TH 0830-0950','ICT TEACH LAB 7'),(25,'BICS1301','4','Mohd. Syarqawy Bin Hamzah',12,'M-W 0830-0950','ICT TEACH LAB 7'),(26,'BICS1302','2','Takumi Sase',19,'M-W 1130-1250','ICT LR 14'),(27,'BIIT1301','6','Mimi Liza Binti Abdul Majid',30,'M-W 1400-1520','ICT TEACH LAB 5'),(28,'BIIT1303','4','Akram M Z M Khedher',15,'T-TH 1000-1120','ICT LR 14A');
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
  `level_of_study` int NOT NULL,
  `total_credits` int DEFAULT '0',
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `matric_no` (`matric_no`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'2411001','Ahmad Faizal','ahmad.faizal@student.edu.my','pass1234',2,0),(2,'2411002','Siti Nur Aisyah','siti.aisyah@student.edu.my','password',2,0),(3,'2411003','Muhammad Iqbal','iqbal@student.edu.my','abc',2,0),(4,'2411004','Aminah Binti Rahman','aminah.rahman@student.edu.my','def',2,0),(5,'2412005','Nurul Izzah Binti Mansor','nurul.izzah@student.edu.my','pass5566',1,0),(6,'2412006','Mohamed Haziq Bin Zulkifli','haziq.z@student.edu.my','haziq123',1,0),(7,'2412007','Farah Adiba Binti Shukor','farah.adiba@student.edu.my','farah789',2,0),(8,'2412008','Abdul Rahman Bin Khalid','rahman.k@student.edu.my','rahman000',1,0),(9,'2412009','Siti Fatimah Binti Hasan','siti.fatimah@student.edu.my','siti2024',2,0);
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

-- Dump completed on 2026-01-10  3:14:06
