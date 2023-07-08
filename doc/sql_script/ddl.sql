-- --------------------------------------------------------
-- 호스트:                          iameeo.com
-- 서버 버전:                        5.1.73 - Source distribution
-- 서버 OS:                        redhat-linux-gnu
-- HeidiSQL 버전:                  9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- iameeo_db 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `iameeo_db` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `iameeo_db`;

-- 테이블 iameeo_db.COMBINE_PRODUCT 구조 내보내기
CREATE TABLE IF NOT EXISTS `COMBINE_PRODUCT` (
  `seq` int(11) NOT NULL AUTO_INCREMENT,
  `product_shop` varchar(50) DEFAULT NULL,
  `product_regdate` datetime DEFAULT NULL,
  `product_code` varchar(50) DEFAULT NULL,
  `product_url` varchar(500) DEFAULT NULL,
  `product_title` varchar(200) DEFAULT NULL,
  `product_text` mediumtext,
  `product_price` varchar(50) DEFAULT NULL,
  `product_thumbImg` varchar(200) DEFAULT NULL,
  `product_color` varchar(200) DEFAULT NULL,
  `product_size` varchar(200) DEFAULT NULL,
  `product_price2` varchar(50) DEFAULT NULL,
  `product_price3` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=MyISAM AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;

-- 테이블 iameeo_db.COMBINE_PRODUCT_IMG 구조 내보내기
CREATE TABLE IF NOT EXISTS `COMBINE_PRODUCT_IMG` (
  `seq` int(11) NOT NULL AUTO_INCREMENT,
  `product_regdate` datetime DEFAULT NULL,
  `product_shop` varchar(50) DEFAULT NULL,
  `product_new_img_url` varchar(500) DEFAULT NULL,
  `product_seq` int(11) DEFAULT NULL,
  `product_img_sort` int(11) DEFAULT NULL,
  `product_img_url` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=MyISAM AUTO_INCREMENT=61 DEFAULT CHARSET=latin1;

-- 테이블 iameeo_db.combine_shop 구조 내보내기
CREATE TABLE IF NOT EXISTS `combine_shop` (
  `seq` int(11) NOT NULL AUTO_INCREMENT,
  `shop_name` varchar(50) DEFAULT NULL,
  `shop_url` varchar(500) DEFAULT NULL,
  `shop_id` varchar(50) DEFAULT NULL,
  `shop_pw` varchar(50) DEFAULT NULL,
  `shop_open` char(1) DEFAULT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- 테이블 데이터 iameeo_db.combine_shop:1 rows 내보내기
/*!40000 ALTER TABLE `combine_shop` DISABLE KEYS */;
INSERT INTO `combine_shop` (`seq`, `shop_name`, `shop_url`, `shop_id`, `shop_pw`, `shop_open`) VALUES
	(1, 'ddmshu', 'https://ddmshu.com', 'lmychoicel', 'slrtmslrtm1', 'Y');
/*!40000 ALTER TABLE `combine_shop` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;


ALTER DATABASE iameeo_db DEFAULT CHARACTER SET utf8;
ALTER TABLE combine_product DEFAULT CHARACTER SET utf8;
ALTER TABLE combine_product_img DEFAULT CHARACTER SET utf8;