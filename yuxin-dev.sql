/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : localhost:3306
 Source Schema         : yuxin-dev

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 11/06/2020 17:15:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_msg
-- ----------------------------
DROP TABLE IF EXISTS `chat_msg`;
CREATE TABLE `chat_msg`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `send_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `accept_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sign_flag` int(1) NOT NULL,
  `create_time` datetime(0) NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_msg
-- ----------------------------
INSERT INTO `chat_msg` VALUES ('200402B201BFFP28', '200325BSACFW69KP', '200325BSACFW69Kh', '撒有哪啦', 1, '2020-04-02 07:30:47');
INSERT INTO `chat_msg` VALUES ('200402B7NF772W00', '200325BSACFW69KP', 'lkafdjgalsdg', '你好', 1, '2020-04-02 07:47:53');
INSERT INTO `chat_msg` VALUES ('200402B7Z98RMZ7C', '200325BSACFW69KP', 'lkafdjgalsdg', '嘿嘿', 1, '2020-04-02 07:48:43');
INSERT INTO `chat_msg` VALUES ('200402B9T5CRGC00', '200325BSACFW69Kh', '200325BSACFW69KP', '嘿 ', 1, '2020-04-02 07:54:17');
INSERT INTO `chat_msg` VALUES ('200402B9X01Y29YW', '200325BSACFW69Kh', 'lkafdjgalsdg', '啦啦啦啦啦', 1, '2020-04-02 07:54:28');
INSERT INTO `chat_msg` VALUES ('200402B9XWHYCARP', '200325BSACFW69Kh', '200325BSACFW69KP', 'OK', 1, '2020-04-02 07:54:34');
INSERT INTO `chat_msg` VALUES ('200402BFRRBH0FA8', 'lkafdjgalsdg', '200325BSACFW69KP', '你好', 1, '2020-04-02 08:09:08');
INSERT INTO `chat_msg` VALUES ('200402BFSK5ANNTC', 'lkafdjgalsdg', '200325BSACFW69Kh', '哈哈哈', 0, '2020-04-02 08:09:14');
INSERT INTO `chat_msg` VALUES ('200402BFYSDNYWM8', 'lkafdjgalsdg', '200325BSACFW69KP', '略略略', 1, '2020-04-02 08:09:41');
INSERT INTO `chat_msg` VALUES ('200402BG0AR99CSW', 'lkafdjgalsdg', '200325BSACFW69Kh', '行', 0, '2020-04-02 08:09:51');
INSERT INTO `chat_msg` VALUES ('200402C0C7SFPCPH', '200325BSACFW69KP', '200325BSACFW69Kh', 'woo', 0, '2020-04-02 08:50:09');
INSERT INTO `chat_msg` VALUES ('200402CC42WP204H', '200325BSACFW69KP', 'lkafdjgalsdg', 'nih', 0, '2020-04-02 09:25:19');

-- ----------------------------
-- Table structure for friends_request
-- ----------------------------
DROP TABLE IF EXISTS `friends_request`;
CREATE TABLE `friends_request`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `send_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `accept_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `request_date_time` datetime(0) NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for my_friends
-- ----------------------------
DROP TABLE IF EXISTS `my_friends`;
CREATE TABLE `my_friends`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `my_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `my_friend_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of my_friends
-- ----------------------------
INSERT INTO `my_friends` VALUES ('1', '200325BSACFW69Kh', '200325BSACFW69KP');
INSERT INTO `my_friends` VALUES ('2', '200325BSACFW69KP', '200325BSACFW69Kh');
INSERT INTO `my_friends` VALUES ('200401111019001118720', 'lkafdjgalsdg', '200325BSACFW69Kh');
INSERT INTO `my_friends` VALUES ('200401111019030478848', '200325BSACFW69Kh', 'lkafdjgalsdg');
INSERT INTO `my_friends` VALUES ('AFDSF', 'lkafdjgalsdg', '200325BSACFW69KP');
INSERT INTO `my_friends` VALUES ('FA', '200325BSACFW69KP', 'lkafdjgalsdg');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `face_image` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `face_image_big` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `nickname` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `qrcode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `cid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('200325BSACFW69Kh', 'zhangsan', 'jFy0qOV6Ens+iV8rs+cwjw==', 'das', 'das', '哈哈哈', 'd', 'fadfa');
INSERT INTO `users` VALUES ('200325BSACFW69KP', 'yuxin', 'jFy0qOV6Ens+iV8rs+cwjw==', 'M00/00/00/wKi3iF59vSCAVVb9AABXh6egPYI318_80x80.png', 'M00/00/00/wKi3iF59vSCAVVb9AABXh6egPYI318.png', '小蜻蜓', 'df', 'ff3dd56f9c34936abc18fe3d972697f9');
INSERT INTO `users` VALUES ('lkafdjgalsdg', 'ssss', 'jFy0qOV6Ens+iV8rs+cwjw==', 'afd', 'afdasd', '卧槽', 'dd', 'dfasd');

SET FOREIGN_KEY_CHECKS = 1;
