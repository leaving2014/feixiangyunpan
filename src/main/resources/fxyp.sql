/*
 Navicat Premium Data Transfer

 Source Server         : MariaDB
 Source Server Type    : MariaDB
 Source Server Version : 100605
 Source Host           : localhost:3306
 Source Schema         : fxyp

 Target Server Type    : MariaDB
 Target Server Version : 100605
 File Encoding         : 65001

 Date: 23/05/2022 19:02:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for excel
-- ----------------------------
DROP TABLE IF EXISTS `excel`;
CREATE TABLE `excel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `data` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '表格json数据',
  `identifier` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '唯一识别码',
  `collaborate` tinyint(1) NOT NULL DEFAULT 0 COMMENT '协同编辑(默认0,不开启, 1:开启)',
  `permissions` tinyint(1) NULL DEFAULT 1 COMMENT '协同编辑用户权限(0: 所有权限,包含编辑和下载, 1: 编辑,2:只读)',
  `max_num_editors` int(11) NULL DEFAULT 100 COMMENT '最大协同编辑人数(默认为100)',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '类型(1:网盘文件,2: 在线表格)',
  `file_id` bigint(20) NULL DEFAULT NULL COMMENT '文件id(网盘文件id)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '文件id',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10073 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '文件名称',
  `origin_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件原始名称',
  `file_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '文件路径',
  `is_dir` tinyint(1) NULL DEFAULT NULL COMMENT '是否为目录',
  `file_ext` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件扩展名',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小(单位B)',
  `file_type` tinyint(1) NULL DEFAULT NULL COMMENT '文件类型(0未知,1图片,2文档,3视频,4种子,5音频,6其他)',
  `identifier` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件md5(用于快速上传)',
  `file_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件url',
  `is_shared` tinyint(1) NULL DEFAULT 0 COMMENT '文件是否共享(0不共享,1共享)',
  `storage_type` tinyint(1) NULL DEFAULT 0 COMMENT '文件存储类型 (0:本地存储 1:cos对象存储)',
  `audit` tinyint(1) NULL DEFAULT NULL COMMENT '文件审核(-1:审核未通过,0:审核中,1:审核通过)',
  `origin` tinyint(1) NULL DEFAULT 0 COMMENT '文件来源(0: 用户上传,1: 文件引用(用户保存的分享文件),2: 离线下载)',
  `parent_path_id` bigint(20) NULL DEFAULT -1 COMMENT '文件的父目录id (根目录下的目录父目录和文件为-1)',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '文件是否删除(逻辑删除,0:未删除,1:删除)',
  `user_id` bigint(20) NOT NULL COMMENT '文件上传用户id',
  `file_update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '文件更新时间',
  `file_create_time` datetime NULL DEFAULT NULL COMMENT '文件创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 775 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `available` bit(1) NULL DEFAULT NULL COMMENT '角色是否可用',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `role` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '角色名称',
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for share
-- ----------------------------
DROP TABLE IF EXISTS `share`;
CREATE TABLE `share`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` tinyint(4) NULL DEFAULT NULL COMMENT '文件共享类型(0为公共分享,1为私密分享,2为好友分享)',
  `batch_num` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件分享批次号',
  `file_id` bigint(20) NULL DEFAULT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `extraction_code` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件提取码',
  `browse_times` bigint(20) NULL DEFAULT 0 COMMENT '分享链接浏览次数',
  `save_times` bigint(20) NULL DEFAULT 0,
  `download_times` bigint(20) NULL DEFAULT 0 COMMENT '分享文件下载次数',
  `expired` int(11) NULL DEFAULT NULL COMMENT '文件分享有效期,可取值1,7,30,0( 0为永久不过期)',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '分享状态(0正常,1已失效,2已取消分享,3被冻结)',
  `share_time` datetime NULL DEFAULT NULL COMMENT '文件分享时间',
  `expired_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '文件分享表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for storage
-- ----------------------------
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `storage_size` bigint(20) NOT NULL DEFAULT 1073741824 COMMENT '存储空间大小(默认1073741824,1GB)',
  `storage_size_used` bigint(20) NULL DEFAULT 0 COMMENT '已使用空间大小',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户存储空间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint(19) NOT NULL AUTO_INCREMENT,
  `operation_method` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `operation_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `parameter` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `time_consuming` int(11) NULL DEFAULT NULL,
  `log_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `error_log_msg` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `operation_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 198 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `nick_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '/images/avatar/avatar.jpg' COMMENT '头像地址',
  `phone_number` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `sex` tinyint(1) NULL DEFAULT 2 COMMENT '性别(0男，1女，2未知）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `role` tinyint(1) NOT NULL DEFAULT 1 COMMENT '角色 (1普通用户2管理员3超级管理员)',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '账号状态（ 0正常1停用）',
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`user_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
