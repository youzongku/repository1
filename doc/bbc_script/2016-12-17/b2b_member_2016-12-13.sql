INSERT INTO "public"."t_dis_mode" ("id", "dis_mode") VALUES ('5', 'VIP');




CREATE TABLE "public"."t_account_operate_record" (
"id" serial4 NOT NULL,
"operator" varchar(30),
"operate_time" timestamp(6),
"opdesc" varchar(255),
"account_id" int4,
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_account_operate_record" IS '账户核减信息日志表';

COMMENT ON COLUMN "public"."t_account_operate_record"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_account_operate_record"."operator" IS '操作人';

COMMENT ON COLUMN "public"."t_account_operate_record"."operate_time" IS '操作时间';

COMMENT ON COLUMN "public"."t_account_operate_record"."opdesc" IS '操作描述';

COMMENT ON COLUMN "public"."t_account_operate_record"."account_id" IS '账户id';

