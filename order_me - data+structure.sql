-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 07, 2022 at 08:38 PM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 8.1.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `order_me`
--

-- --------------------------------------------------------

--
-- Table structure for table `bill`
--

CREATE TABLE `bill` (
  `id` int(10) UNSIGNED NOT NULL,
  `person` int(11) NOT NULL DEFAULT 0,
  `type` varchar(100) NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'OPEN',
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `index_category`
--

CREATE TABLE `index_category` (
  `category` varchar(100) NOT NULL,
  `number` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `index_category`
--

INSERT INTO `index_category` (`category`, `number`) VALUES
('ชานม 22 ออนซ์', 1),
('ช็อกโกแลตวิปครีม 16 ออนซ์', 3),
('บิงซูออนท็อป', 4),
('มัทฉะนมสด 22 ออนซ์', 2);

-- --------------------------------------------------------

--
-- Table structure for table `item`
--

CREATE TABLE `item` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(150) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `category` varchar(100) NOT NULL,
  `image` text DEFAULT NULL,
  `price` float NOT NULL DEFAULT 0,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `check_quantity` tinyint(1) NOT NULL DEFAULT 0,
  `display` tinyint(1) NOT NULL DEFAULT 1,
  `version` bigint(20) NOT NULL,
  `flag` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `item`
--

INSERT INTO `item` (`id`, `name`, `description`, `category`, `image`, `price`, `quantity`, `check_quantity`, `display`, `version`, `flag`) VALUES
(1, 'เฉาก๊วย', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(2, 'ไข่มุกบราวน์ชูการ์', NULL, 'OPTION', NULL, 10, 0, 0, 1, 0, 0),
(3, 'เยลลี่สมุนไพร', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(4, 'มัทชะเยลลี่', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(5, 'ถั่วแดงอะซูกิ', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(6, 'พุดดิ้งนมสด', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(7, 'บราวน์ชูการ์เยลลี่', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(8, 'เยลลี่ว่านหางจระเข้', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(9, 'วานิลลาครัมเบิล', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(10, 'ช็อกโกแลตครัมเบิล', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(11, 'หวาน 0%', NULL, 'OPTION', NULL, 0, 0, 0, 1, 0, 0),
(12, 'หวาน 25%', NULL, 'OPTION', NULL, 0, 0, 0, 1, 0, 0),
(13, 'หวาน 50%', NULL, 'OPTION', NULL, 0, 0, 0, 1, 0, 0),
(14, 'หวาน 75%', NULL, 'OPTION', NULL, 0, 0, 0, 1, 0, 0),
(15, 'หวาน 100%', NULL, 'OPTION', NULL, 0, 0, 0, 1, 0, 0),
(16, 'แยกน้ำแข็ง', NULL, 'OPTION', NULL, 5, 0, 0, 1, 0, 0),
(17, 'แยกท็อปปิ้ง', NULL, 'OPTION', NULL, 5, 0, 0, 1, 0, 0),
(18, 'แยกน้ำแข็งและแยกท็อปปิ้ง', NULL, 'OPTION', NULL, 5, 0, 0, 1, 0, 0),
(19, 'ปั่น', NULL, 'OPTION', NULL, 20, 0, 0, 1, 0, 0),
(20, 'โมจิทามะ', NULL, 'OPTION', NULL, 19, 0, 0, 1, 0, 0),
(21, 'เมเปิลไซรัป', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(22, 'ครอฟเฟิล', NULL, 'OPTION', NULL, 29, 0, 0, 1, 0, 0),
(23, 'ซอสช็อกโกแลต', NULL, 'OPTION', NULL, 15, 0, 0, 1, 0, 0),
(24, 'วาราบิ', NULL, 'OPTION', NULL, 19, 0, 0, 1, 0, 0),
(25, 'ถุงเก็บความเย็นและน้ำแข็งแห้ง', NULL, 'OPTION', NULL, 10, 0, 0, 1, 0, 0),
(26, 'ชานม', 'รสชาติที่แท้จริงสไตล์ญี่ปุ่น หอมชานมเป็นเอกลักษณ์ เหมาะกับทุกคนแน่นอน สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ชานม 22 ออนซ์', '20220407183750.jpg', 40, 0, 0, 1, 3, 0),
(27, 'ชานมไข่มุก', 'รสชาติที่แท้จริงสไตล์ญี่ปุ่น พร้อมไข่มุกหนุบหนับ สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ชานม 22 ออนซ์', '20220407184514.jpg', 50, 0, 0, 1, 2, 0),
(28, 'ชานมรสนมเผือก', 'ชานมเข้มข้น เพิ่มด้วยกลิ่นและความอร่อยจากเผือก อยากท้าให้ลอง สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ชานม 22 ออนซ์', '20220407184700.jpg', 55, 0, 0, 1, 2, 0),
(29, 'ชานมรสนมช็อกโกแลต', 'ชานมเข้มข้น แซมด้วยกลิ่นและความอร่อยจากช็อกโกแลต อยากท้าให้ลอง สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ชานม 22 ออนซ์', '20220407184854.jpg', 55, 0, 0, 1, 2, 0),
(30, 'ชานมรสคาราเมล', 'ชานมสไตล์ญี่ปุ่น จับมือผสานความอร่อยหอมละมุนจากคาราเมล จะเติมท็อปปิ้งก็อร่อยคูณสอง สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ชานม 22 ออนซ์', '20220407185025.jpg', 60, 0, 0, 1, 5, 0),
(31, 'ดับเบิ้ลแบลคชานม (ชานมใส่เฉาก๊วยและไข่มุก)', 'ดับเบิ้ลความอร่อยไปกับชานมไข่มุกสูตรพิเศษ พร้อมท็อปปิ้งแบบยกกำลังสองไข่มุกบราวน์ชูการ์และเฉาก๊วยสุดฟินเคี้ยวหนุบหนับไปเลย สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ชานม 22 ออนซ์', '20220407193741.jpg', 65, 0, 0, 1, 2, 0),
(32, 'ดับเบิ้ลแบลคชานม (ชานมใส่เฉาก๊วยและไข่มุก)', 'เติมความฟินยิ่งกว่าเดิม กับชานมสูตรเข้มข้น ใส่ท็อปปิ้งเป็นเฉาก๊วยและพุดดิ้งนม คือที่สุดของความลงตัว สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ชานม 22 ออนซ์', '20220407193854.jpg', 65, 0, 0, 1, 3, 0),
(33, 'เดอะทรีชานม (ชานมใส่ไข่มุก,เฉาก๊วยและพุดดิ้งนม)', 'พาฟินแบบจัดเต็ม การรวมตัวของท้อปปิ้งยอดนิยม 3 ท็อปปิ้ง พุดดิ้งคาราเมลเนื้อขาวเนียนนุ่ม เฉาก๊วยเนื้อแน่นและไข่มุกบราวน์ชูการ์หอมๆกับมัทฉะซิกเนเจอร์ของฟุกุ สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ชานม 22 ออนซ์', '20220407194533.jpg', 80, 0, 0, 1, 2, 0),
(34, 'มัทฉะนมสด (ชาเขียวนมสด)', 'เมนูสุดฮิตของฟุกุ รสชาติเป็นเอกลักษณ์หอมเด่นไม่เหมือนใคร มัทฉะแท้คุณภาพดีนำเข้าจากประเทศญี่ปุ่น สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'มัทฉะนมสด 22 ออนซ์', '20220407195010.jpg', 80, 0, 0, 1, 0, 0),
(35, 'มัทฉะใส่ชานม (ชาเขียวใส่ชานม)', 'การรวมตัวของสองเมนูขายดีตลอดกาล ทั้งมัทฉะและชานม รวมมกันเป็นรสชาติความอร่อยที่ลงตัว สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'มัทฉะนมสด 22 ออนซ์', '20220407195325.jpg', 85, 0, 0, 1, 0, 0),
(36, 'มัทฉะรสนมเผือก (ชาเขียวรสนมเผือก)', 'มัทฉะรสเผือก เพิ่มความหอมด้วยนมเผือก ดื่มด่ำความอร่อยที่นุ่มนวลของสองรสชาติในแก้วเดียว สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'มัทฉะนมสด 22 ออนซ์', '20220407195408.jpg', 90, 0, 0, 1, 0, 0),
(37, 'มัทฉะรสนมช็อกโกแลต (ชาเขียวรสช็อกโกแลต)', 'อร่อยแบบสองสไตล์ ระหว่างมัทฉะกับนมช็อกโกแลต ความหอมของมัทฉะซ่อนรสช็อกโกแลตถือเป็นเมนูที่เข้ากันได้ดี สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'มัทฉะนมสด 22 ออนซ์', '20220407195506.jpg', 90, 0, 0, 1, 0, 0),
(38, 'มัทฉะรสคาราเมล (ชาเขียวรสคาราเมล)', 'อร่อยแบบสองเท่า ระหว่างมัทฉะกับรสชาติหอมหวานคาราเมลที่ต้องลิ้มลอง สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'มัทฉะนมสด 22 ออนซ์', '20220407195629.jpg', 95, 0, 0, 1, 0, 0),
(39, 'ดับเบิ้ลแบลคมัทฉะ (ชาเขียวใส่เฉาก๊วยและไข่มุก)', 'มัทฉะแสนอร่อยจัดเต็มทั้งไข่มุกสุดหนึบหนับและเฉาก๊วยเคี้ยวนุ่มลิ้น ทานรวมกับมัทฉะสูตรเข้มข้น อร่อยแบบยกกำลังสองกันไปเลย สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'มัทฉะนมสด 22 ออนซ์', '20220407195740.jpg', 105, 0, 0, 1, 0, 0),
(40, 'แบลคแอนด์ไวท์มัทฉะ (ชาเขียวใส่พุดดิ้งนมและเฉาก๊วย)', 'จัดเต็มความฟิน ไปกับเครื่องดื่มมัทฉะสูตรเข้มข้น ท็อปด้วยไข่มุกบราวน์ชูการ์ หวานหนึบหนับและเฉาก๊วย ได้ลองแล้วจะติดใจ สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'มัทฉะนมสด 22 ออนซ์', '20220407195908.jpg', 110, 0, 0, 1, 0, 0),
(41, 'เดอะทรีมัทฉะ (ชาเขียวใส่ไข่มุก,เฉาก๊วยและพุดดิ้งนม)', 'อร่อยเน้นๆไปกับ 3 ท็อปปิ้งขายดี ไข่มุกบราวน์ชูการ์ เฉาก๊วยและพุดดิ้งนมคาราเมล ทานคู่กับมัทฉะแล้วอร่อยเลิศต้องบอกเลยทีเดียว สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'มัทฉะนมสด 22 ออนซ์', '20220407195952.jpg', 120, 0, 0, 1, 0, 0),
(42, 'ชาจัสมินวิปครีมช็อกโกแลต', 'วิปครีมสูตรพิเศษรสช็อกโกแลตและความหอมกลิ่น บอกเลยว่าต้องโดน สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ช็อกโกแลตวิปครีม 16 ออนซ์', '20220407220036.jpg', 85, 0, 0, 1, 0, 0),
(43, 'นมสดวิปครีมช็อกโกแลต', 'นมสดแท้ๆ กับวิปครีมช็อกโกแลต อร่อยเป็นเอกลักษณ์ ลงตัวเป็นที่สุด สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ช็อกโกแลตวิปครีม 16 ออนซ์', '20220407220242.jpg', 85, 0, 0, 1, 0, 0),
(44, 'ชาไทยวิปครีมช็อกโกแลต', 'ชาไทยสุดกลมกล่อมกินกับวิปครีมช็อกโกแลตคือดีงาม สามารถเลือกรูปแบบ ระดับความหวานและท็อปปิ้งได้', 'ช็อกโกแลตวิปครีม 16 ออนซ์', '20220407220419.jpg', 85, 0, 0, 1, 1, 0),
(45, 'นมสดบิงซูชาไทย', 'นมสดแท้สุดละมุน ท็อปด้วยเกล็ดน้ำแข็งบิงซูรสชาไทย เข้มข้นเป็นเอกลักษณ์ เนื้อบิงซูเนียนละเอียดละลายในปาก เมนูแสนอร่อยที่ไม่เหมือนใคร สามารถเลือกรูปแบบได้', 'บิงซูออนท็อป', '20220407220712.jpg', 100, 0, 0, 1, 0, 0),
(46, 'รอยัลชาไทยบิงซูชาไทย', 'รอยัลชาไทยรสชาติเข้มข้นนุ่มนวล หอมกลิ่นชาไทยอันเป็นเอกลักษณ์ อร่อยคูณสอง ท็อปด้วยบิงซูชาไทยเนื้อเนียนละลายในปาก ได้ความอร่อยไปอีกเท่าตัวเลย สามารถเลือกรูปแบบได้', 'บิงซูออนท็อป', '20220407220819.jpg', 100, 0, 0, 1, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `item_optional`
--

CREATE TABLE `item_optional` (
  `item_id` int(10) UNSIGNED NOT NULL,
  `optional_id` int(10) UNSIGNED NOT NULL,
  `number` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `item_optional`
--

INSERT INTO `item_optional` (`item_id`, `optional_id`, `number`) VALUES
(26, 1, 1),
(26, 2, 2),
(26, 3, 3),
(26, 4, 4),
(27, 1, 1),
(27, 2, 2),
(27, 3, 3),
(27, 4, 4),
(28, 1, 1),
(28, 3, 3),
(28, 4, 4),
(28, 5, 2),
(29, 1, 1),
(29, 3, 3),
(29, 4, 4),
(29, 5, 2),
(30, 1, 1),
(30, 3, 3),
(30, 4, 4),
(30, 5, 2),
(31, 1, 1),
(31, 2, 2),
(31, 3, 3),
(31, 4, 4),
(32, 1, 1),
(32, 2, 2),
(32, 3, 3),
(32, 4, 4),
(33, 1, 1),
(33, 2, 2),
(33, 3, 3),
(33, 4, 4),
(34, 1, 1),
(34, 3, 3),
(34, 4, 4),
(34, 5, 2),
(35, 1, 1),
(35, 3, 3),
(35, 4, 4),
(35, 5, 2),
(36, 1, 1),
(36, 3, 3),
(36, 4, 4),
(36, 5, 2),
(37, 1, 1),
(37, 3, 3),
(37, 4, 4),
(37, 5, 2),
(38, 1, 1),
(38, 3, 3),
(38, 4, 4),
(38, 5, 2),
(39, 1, 1),
(39, 3, 3),
(39, 4, 4),
(39, 5, 2),
(40, 1, 1),
(40, 3, 3),
(40, 4, 4),
(40, 5, 2),
(41, 1, 1),
(41, 3, 3),
(41, 4, 4),
(41, 5, 2),
(42, 1, 1),
(42, 3, 4),
(42, 5, 2),
(42, 7, 3),
(43, 1, 1),
(43, 3, 4),
(43, 5, 2),
(43, 7, 3),
(44, 1, 1),
(44, 3, 4),
(44, 5, 2),
(44, 7, 3),
(45, 6, 1),
(46, 6, 1);

-- --------------------------------------------------------

--
-- Table structure for table `optional`
--

CREATE TABLE `optional` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(150) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `min` int(11) NOT NULL,
  `max` int(11) NOT NULL,
  `flag` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `optional`
--

INSERT INTO `optional` (`id`, `name`, `description`, `min`, `max`, `flag`) VALUES
(1, 'เลือก - ท็อปปิ้ง', 'เลือกได้ถึง 10 (ถ้าต้องการ)', 0, 10, 0),
(2, 'ระดับความหวาน', 'เลือก 1', 1, 1, 0),
(3, 'เลือกรูปเเบบการเสิร์ฟ', 'เลือกได้ถึง 1 (ถ้าต้องการ)', 0, 1, 0),
(4, 'เลือก ปั่น', 'เลือกได้ถึง 1 (ถ้าต้องการ)', 0, 1, 0),
(5, 'เลือกระดับความหวาน', 'เลือก 1', 1, 1, 0),
(6, 'บรรจุภัณฑ์ - เมนูของหวาน', 'เลือก 1', 1, 1, 0),
(7, 'ท้อปปิ้งพิเศษเฉพาะกลุ่มวิปครีม', 'เลือกได้ถึง 5 (ถ้าต้องการ)', 0, 5, 0);

-- --------------------------------------------------------

--
-- Table structure for table `optional_item`
--

CREATE TABLE `optional_item` (
  `optional_id` int(10) UNSIGNED NOT NULL,
  `item_id` int(10) UNSIGNED NOT NULL,
  `number` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `optional_item`
--

INSERT INTO `optional_item` (`optional_id`, `item_id`, `number`) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3),
(1, 4, 4),
(1, 5, 5),
(1, 6, 6),
(1, 7, 7),
(1, 8, 8),
(1, 9, 9),
(1, 10, 10),
(2, 11, 1),
(2, 12, 2),
(2, 13, 3),
(2, 14, 4),
(2, 15, 5),
(3, 16, 1),
(3, 17, 2),
(3, 18, 3),
(4, 19, 1),
(5, 14, 1),
(5, 15, 2),
(6, 25, 1),
(7, 20, 1),
(7, 21, 2),
(7, 22, 3),
(7, 23, 4),
(7, 24, 5);

-- --------------------------------------------------------

--
-- Table structure for table `order_info`
--

CREATE TABLE `order_info` (
  `id` int(10) UNSIGNED NOT NULL,
  `bill_id` int(10) UNSIGNED NOT NULL,
  `item_id` int(10) UNSIGNED NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `comment` varchar(500) DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT '',
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `id` int(10) UNSIGNED NOT NULL,
  `channel` varchar(50) NOT NULL DEFAULT 'UNKNOWN',
  `ref1` varchar(20) NOT NULL,
  `ref2` varchar(20) DEFAULT NULL,
  `ref3` varchar(20) DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'UNPAID',
  `generate_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`generate_info`)),
  `confirm_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`confirm_info`)),
  `created_timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `bill_id` int(10) UNSIGNED DEFAULT NULL,
  `total` float NOT NULL DEFAULT 0,
  `receipt` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`receipt`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `select_item`
--

CREATE TABLE `select_item` (
  `order_id` int(10) UNSIGNED NOT NULL,
  `optional_id` int(10) UNSIGNED NOT NULL,
  `item_id` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `table_info`
--

CREATE TABLE `table_info` (
  `id` int(11) NOT NULL,
  `available` tinyint(1) NOT NULL,
  `bill_id` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `table_info`
--

INSERT INTO `table_info` (`id`, `available`, `bill_id`) VALUES
(1, 1, NULL),
(2, 1, NULL),
(3, 1, NULL),
(4, 1, NULL),
(5, 1, NULL),
(6, 1, NULL),
(7, 1, NULL),
(8, 1, NULL),
(9, 1, NULL),
(10, 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `token`
--

CREATE TABLE `token` (
  `id` varchar(32) NOT NULL,
  `bill_id` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `token`
--

INSERT INTO `token` (`id`, `bill_id`) VALUES
('OdLXh_hXQ9nABz4eYfxtbbLD5DN1n1BG', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bill`
--
ALTER TABLE `bill`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `index_category`
--
ALTER TABLE `index_category`
  ADD PRIMARY KEY (`category`);

--
-- Indexes for table `item`
--
ALTER TABLE `item`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- Indexes for table `item_optional`
--
ALTER TABLE `item_optional`
  ADD PRIMARY KEY (`item_id`,`optional_id`),
  ADD KEY `optional_id` (`optional_id`);

--
-- Indexes for table `optional`
--
ALTER TABLE `optional`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `optional_item`
--
ALTER TABLE `optional_item`
  ADD PRIMARY KEY (`optional_id`,`item_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `order_info`
--
ALTER TABLE `order_info`
  ADD PRIMARY KEY (`id`),
  ADD KEY `billId` (`bill_id`),
  ADD KEY `itemId` (`item_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bill_id` (`bill_id`);

--
-- Indexes for table `select_item`
--
ALTER TABLE `select_item`
  ADD PRIMARY KEY (`order_id`,`item_id`),
  ADD KEY `optional_id` (`optional_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Indexes for table `table_info`
--
ALTER TABLE `table_info`
  ADD PRIMARY KEY (`id`),
  ADD KEY `billId` (`bill_id`);

--
-- Indexes for table `token`
--
ALTER TABLE `token`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bill_id` (`bill_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bill`
--
ALTER TABLE `bill`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `item`
--
ALTER TABLE `item`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT for table `optional`
--
ALTER TABLE `optional`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `order_info`
--
ALTER TABLE `order_info`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `item_optional`
--
ALTER TABLE `item_optional`
  ADD CONSTRAINT `item_optional_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`),
  ADD CONSTRAINT `item_optional_ibfk_2` FOREIGN KEY (`optional_id`) REFERENCES `optional` (`id`);

--
-- Constraints for table `optional_item`
--
ALTER TABLE `optional_item`
  ADD CONSTRAINT `optional_item_ibfk_1` FOREIGN KEY (`optional_id`) REFERENCES `optional` (`id`),
  ADD CONSTRAINT `optional_item_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`);

--
-- Constraints for table `order_info`
--
ALTER TABLE `order_info`
  ADD CONSTRAINT `order_info_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`id`),
  ADD CONSTRAINT `order_info_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`);

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`id`);

--
-- Constraints for table `select_item`
--
ALTER TABLE `select_item`
  ADD CONSTRAINT `select_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order_info` (`id`),
  ADD CONSTRAINT `select_item_ibfk_2` FOREIGN KEY (`optional_id`) REFERENCES `optional` (`id`),
  ADD CONSTRAINT `select_item_ibfk_3` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`);

--
-- Constraints for table `table_info`
--
ALTER TABLE `table_info`
  ADD CONSTRAINT `table_info_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`id`);

--
-- Constraints for table `token`
--
ALTER TABLE `token`
  ADD CONSTRAINT `token_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
