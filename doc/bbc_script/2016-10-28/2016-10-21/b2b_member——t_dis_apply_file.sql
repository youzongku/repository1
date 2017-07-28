CREATE TABLE "public"."t_dis_apply_file" (
"id" serial4 NOT NULL,
"name" varchar(100),
"url" varchar(100),
"apply_id" int4,
"create_date" timestamp(6) DEFAULT now(),
"update_date" timestamp(6),
"type" varchar(50),
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_dis_apply_file" IS '注册申请文件表';

COMMENT ON COLUMN "public"."t_dis_apply_file"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_dis_apply_file"."name" IS '文件名';

COMMENT ON COLUMN "public"."t_dis_apply_file"."url" IS '文件url';

COMMENT ON COLUMN "public"."t_dis_apply_file"."apply_id" IS '注册申请id';

COMMENT ON COLUMN "public"."t_dis_apply_file"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_dis_apply_file"."update_date" IS '更新时间';

COMMENT ON COLUMN "public"."t_dis_apply_file"."type" IS '申请文件的用途';

