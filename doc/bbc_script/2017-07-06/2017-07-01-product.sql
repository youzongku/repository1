/*
Navicat PGSQL Data Transfer

Source Server         : 192.168.223.13_5432
Source Server Version : 90315
Source Host           : 192.168.223.13:5432
Source Database       : product
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90315
File Encoding         : 65001

Date: 2017-06-30 16:53:18
*/


-- ----------------------------
-- Table structure for t_export_sync_result
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_export_sync_result";
CREATE TABLE "public"."t_export_sync_result" (
"id" SERIAL primary key,
"operator" varchar(32) COLLATE "default",
"file_name" varchar(32) COLLATE "default",
"export_result" int4,
"create_time" timestamp(6),
"update_time" timestamp(6),
"msg" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."t_export_sync_result"."id" IS '主键';
COMMENT ON COLUMN "public"."t_export_sync_result"."operator" IS '操作者';
COMMENT ON COLUMN "public"."t_export_sync_result"."file_name" IS '导出文件名称';
COMMENT ON COLUMN "public"."t_export_sync_result"."export_result" IS '导出结果';
COMMENT ON COLUMN "public"."t_export_sync_result"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_export_sync_result"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_export_sync_result"."msg" IS '信息';

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------
