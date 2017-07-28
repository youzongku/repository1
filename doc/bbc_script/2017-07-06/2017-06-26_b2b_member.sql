CREATE TABLE "public"."t_erpgetdata_account" (
"id" serial4 NOT NULL,
"account" varchar(50) NOT NULL,
"create_time" timestamp(6) DEFAULT now(),
"desc" varchar(200),
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_erpgetdata_account" IS 'ERP拉取BBC组织架构数据账号配置';

COMMENT ON COLUMN "public"."t_erpgetdata_account"."id" IS '主键';

COMMENT ON COLUMN "public"."t_erpgetdata_account"."account" IS '拉取组织架构全量数据账号';

COMMENT ON COLUMN "public"."t_erpgetdata_account"."desc" IS '拉取组织架构全量数据账号';

ALTER TABLE "public"."t_erpgetdata_account"
ADD COLUMN "md5" varchar(39) NOT NULL;

COMMENT ON COLUMN "public"."t_erpgetdata_account"."md5" IS 'MD5值';

INSERT INTO "public"."t_erpgetdata_account" ("account", "create_time", "desc", "md5") VALUES ('bbc_organization', '2017-06-26 17:55:18.579721', 'ERP拉取组织架构信息', 'a46850d6-03c6-43fd-9958-467e487272d9');
