CREATE TABLE "t_package_mail_log" (

"id" serial4 NOT NULL,

"member_id" int4 NOT NULL,

"is_package_mail" int2 NOT NULL,

"operator" varchar(50),

"remark" varchar(500),

"create_time" timestamp(6) DEFAULT now(),

PRIMARY KEY ("id") 

);

COMMENT ON TABLE "public"."t_package_mail_log" IS '包邮设置操作日志表';
COMMENT ON COLUMN "public"."t_package_mail_log"."id" IS '主键';
COMMENT ON COLUMN "public"."t_package_mail_log"."member_id" IS '用户id';
COMMENT ON COLUMN "public"."t_package_mail_log"."is_package_mail" IS '是否包邮 1：包邮 2：不包邮';
COMMENT ON COLUMN "public"."t_package_mail_log"."operator" IS '操作人';
COMMENT ON COLUMN "public"."t_package_mail_log"."remark" IS '备注';
COMMENT ON COLUMN "public"."t_package_mail_log"."create_time" IS '操作时间';


-- 修改表
alter table t_dis_member

ADD COLUMN "is_package_mail" int2 default 2;

COMMENT ON COLUMN "public"."t_dis_member"."is_package_mail" IS '是否包邮 1：包邮 2：不包邮';