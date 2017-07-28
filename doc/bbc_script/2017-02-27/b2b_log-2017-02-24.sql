/*
Navicat PGSQL Data Transfer

Source Server         : new_192.168.223.14
Source Server Version : 90315
Source Host           : 192.168.223.14:5432
Source Database       : b2b_log
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90315
File Encoding         : 65001

Date: 2017-02-24 09:35:41
*/


-- ----------------------------
-- Table structure for t_data_sync_php
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_data_sync_php";
CREATE TABLE "public"."t_data_sync_php" (
"uid" varchar(39) COLLATE "default" NOT NULL,
"key" varchar(255) COLLATE "default",
"opt_type" varchar(255) COLLATE "default",
"opt_time" timestamp(6) DEFAULT now(),
"content" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_data_sync_php" IS 'b2b数据变更 同步php日志记录表';
COMMENT ON COLUMN "public"."t_data_sync_php"."uid" IS '主键,uuid字符串类型';
COMMENT ON COLUMN "public"."t_data_sync_php"."key" IS '同步涉及内容';
COMMENT ON COLUMN "public"."t_data_sync_php"."opt_type" IS '操作类型(create,update,delete)';
COMMENT ON COLUMN "public"."t_data_sync_php"."opt_time" IS '操作时间';
COMMENT ON COLUMN "public"."t_data_sync_php"."content" IS '判断唯一性的json串';

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table t_data_sync_php
-- ----------------------------
ALTER TABLE "public"."t_data_sync_php" ADD PRIMARY KEY ("uid");



/*
Navicat PGSQL Data Transfer

Source Server         : new_192.168.223.14
Source Server Version : 90315
Source Host           : 192.168.223.14:5432
Source Database       : b2b_log
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90315
File Encoding         : 65001

Date: 2017-02-24 09:35:55
*/


-- ----------------------------
-- Table structure for t_key_description
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_key_description";
CREATE TABLE "public"."t_key_description" (
"id" serial4 NOT NULL,
"key" varchar(255) COLLATE "default",
"value" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_key_description" IS 'key值对照表';
COMMENT ON COLUMN "public"."t_key_description"."id" IS '主键';
COMMENT ON COLUMN "public"."t_key_description"."key" IS 'key';
COMMENT ON COLUMN "public"."t_key_description"."value" IS 'key描述';

-- ----------------------------
-- Records of t_key_description
-- ----------------------------
INSERT INTO "public"."t_key_description" VALUES ('1', 'store', '店铺');
INSERT INTO "public"."t_key_description" VALUES ('2', 'user', '用户信息');
INSERT INTO "public"."t_key_description" VALUES ('3', 'cloud_inventory', '云仓库存');
INSERT INTO "public"."t_key_description" VALUES ('4', 'purchase_order', '采购订单');
INSERT INTO "public"."t_key_description" VALUES ('5', 'sales_order', '发货单');
INSERT INTO "public"."t_key_description" VALUES ('6', 'micro_inventory', '微仓库存');
INSERT INTO "public"."t_key_description" VALUES ('7', 'bill', '交易记录');

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table t_key_description
-- ----------------------------
ALTER TABLE "public"."t_key_description" ADD PRIMARY KEY ("id");
