-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 07, 2022 at 01:03 PM
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

-- --------------------------------------------------------

--
-- Table structure for table `item_optional`
--

CREATE TABLE `item_optional` (
  `item_id` int(10) UNSIGNED NOT NULL,
  `optional_id` int(10) UNSIGNED NOT NULL,
  `number` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

-- --------------------------------------------------------

--
-- Table structure for table `optional_item`
--

CREATE TABLE `optional_item` (
  `optional_id` int(10) UNSIGNED NOT NULL,
  `item_id` int(10) UNSIGNED NOT NULL,
  `number` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

-- --------------------------------------------------------

--
-- Table structure for table `token`
--

CREATE TABLE `token` (
  `id` varchar(32) NOT NULL,
  `bill_id` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bill`
--
ALTER TABLE `bill`
  ADD PRIMARY KEY (`id`);

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
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `optional`
--
ALTER TABLE `optional`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

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
