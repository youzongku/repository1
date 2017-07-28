ALTER TABLE "public"."t_dis_member"
ADD COLUMN "is_disabled" bool DEFAULT false;

COMMENT ON COLUMN "public"."t_dis_member"."is_disabled" IS '是否被禁用';


CREATE TABLE "public"."t_admin_operate_record" (
"id" serial4 NOT NULL,
"operator" varchar(30) COLLATE "default",
"operate_time" timestamp(6) DEFAULT now(),
"opdesc" varchar(250) COLLATE "default",
"admin_id" int4,
CONSTRAINT "t_admin_operate_record_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_admin_operate_record" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_admin_operate_record" IS '后台账号操作日志';

COMMENT ON COLUMN "public"."t_admin_operate_record"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_admin_operate_record"."operator" IS '操作人';

COMMENT ON COLUMN "public"."t_admin_operate_record"."operate_time" IS '操作时间';

COMMENT ON COLUMN "public"."t_admin_operate_record"."opdesc" IS '操作简述';

COMMENT ON COLUMN "public"."t_admin_operate_record"."admin_id" IS '后台用户id';

update t_dis_menu set name='注册审核',description='注册审核' where "position" = 37;
