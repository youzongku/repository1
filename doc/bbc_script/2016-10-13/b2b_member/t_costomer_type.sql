/*
Navicat PGSQL Data Transfer

Source Server         : b2b
Source Server Version : 90309
Source Host           : 192.168.220.72:5433
Source Database       : b2b_member
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90309
File Encoding         : 65001

Date: 2016-10-12 15:23:00
*/


-- ----------------------------
-- Table structure for t_costomer_type
-- ----------------------------
create sequence "public"."t_costomer_type_id_seq" increment by 1 minvalue 1 no maxvalue start with 1;
DROP TABLE IF EXISTS "public"."t_costomer_type";
CREATE TABLE "public"."t_costomer_type" (
"id" int4 DEFAULT nextval('t_costomer_type_id_seq'::regclass) NOT NULL,
"customer_name" varchar(100) COLLATE "default",
"create_date" date DEFAULT now(),
"update_date" date DEFAULT now()
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_costomer_type" IS '分销商类型表';
COMMENT ON COLUMN "public"."t_costomer_type"."id" IS '主键';
COMMENT ON COLUMN "public"."t_costomer_type"."customer_name" IS '分销商类型';
COMMENT ON COLUMN "public"."t_costomer_type"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."t_costomer_type"."update_date" IS '更新时间';

-- ----------------------------
-- Records of t_costomer_type
-- ----------------------------
INSERT INTO "public"."t_costomer_type" VALUES ('1', '外部分销商', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_costomer_type" VALUES ('2', '合营分销商', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_costomer_type" VALUES ('3', '内部分销商', '2016-10-12', '2016-10-12');

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table t_costomer_type
-- ----------------------------
ALTER TABLE "public"."t_costomer_type" ADD PRIMARY KEY ("id");
