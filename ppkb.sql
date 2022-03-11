-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 13, 2019 at 06:00 AM
-- Server version: 10.1.35-MariaDB
-- PHP Version: 7.2.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ppkb`
--

-- --------------------------------------------------------

--
-- Table structure for table `identitas`
--

CREATE TABLE `identitas` (
  `NIK` varchar(17) NOT NULL,
  `Nama` varchar(26) DEFAULT NULL,
  `Alamat` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `kepemilikan`
--

CREATE TABLE `kepemilikan` (
  `Id` int(11) NOT NULL,
  `NIK` varchar(17) NOT NULL,
  `Nomor_Polisi` varchar(12) NOT NULL,
  `No_Urut` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `motor`
--

CREATE TABLE `motor` (
  `Nomor_Polisi` varchar(12) NOT NULL,
  `Jenis` varchar(20) NOT NULL,
  `Th_Pembuatan` varchar(15) NOT NULL,
  `Warna_KB` varchar(15) NOT NULL,
  `Isi_Silinder` varchar(10) NOT NULL,
  `No_Rangka` varchar(20) NOT NULL,
  `No_Mesin` varchar(15) NOT NULL,
  `No_BPKB` varchar(10) NOT NULL,
  `Bahan_Bakar` enum('Bensin','Solar') NOT NULL,
  `Warna_TNKB` varchar(10) NOT NULL,
  `Berat_KB` int(11) NOT NULL,
  `Jumlah Sumbu` int(11) NOT NULL,
  `Penumpanng` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `stnk`
--

CREATE TABLE `stnk` (
  `No_Urut` varchar(10) NOT NULL,
  `No_Kohir` varchar(20) NOT NULL,
  `Tgl_Penetapan` date NOT NULL,
  `Tgl_Berakhir` date NOT NULL,
  `Petugas_Penetapan` varchar(26) NOT NULL,
  `Korektor` varchar(26) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `identitas`
--
ALTER TABLE `identitas`
  ADD PRIMARY KEY (`NIK`);

--
-- Indexes for table `kepemilikan`
--
ALTER TABLE `kepemilikan`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `NIK` (`NIK`),
  ADD KEY `Nomor_Polisi` (`Nomor_Polisi`),
  ADD KEY `No_Urut` (`No_Urut`);

--
-- Indexes for table `motor`
--
ALTER TABLE `motor`
  ADD PRIMARY KEY (`Nomor_Polisi`);

--
-- Indexes for table `stnk`
--
ALTER TABLE `stnk`
  ADD PRIMARY KEY (`No_Urut`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `kepemilikan`
--
ALTER TABLE `kepemilikan`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `kepemilikan`
--
ALTER TABLE `kepemilikan`
  ADD CONSTRAINT `kepemilikan_ibfk_1` FOREIGN KEY (`NIK`) REFERENCES `identitas` (`NIK`),
  ADD CONSTRAINT `kepemilikan_ibfk_2` FOREIGN KEY (`No_Urut`) REFERENCES `stnk` (`No_Urut`),
  ADD CONSTRAINT `kepemilikan_ibfk_3` FOREIGN KEY (`Nomor_Polisi`) REFERENCES `motor` (`Nomor_Polisi`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
