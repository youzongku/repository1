CREATE TABLE "public"."t_type_base" (
"id" serial4 NOT NULL,
"name" varchar(200),
"create_date" timestamp(6),
"update_date" timestamp(6),
"is_active" bool DEFAULT true,
"create_user" varchar(50),
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_type_base" IS '产品类型表';

COMMENT ON COLUMN "public"."t_type_base"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_type_base"."name" IS '商品类别';

COMMENT ON COLUMN "public"."t_type_base"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_type_base"."update_date" IS '更新日期';

COMMENT ON COLUMN "public"."t_type_base"."is_active" IS '是否被启用 ';

COMMENT ON COLUMN "public"."t_type_base"."create_user" IS '创建人';










ALTER TABLE "public"."t_product_disprice"
ADD COLUMN "type_id" int4,
ADD COLUMN "type_name" varchar(200);

COMMENT ON COLUMN "public"."t_product_disprice"."type_id" IS '商品类别id';

COMMENT ON COLUMN "public"."t_product_disprice"."type_name" IS '类别名称（A类，B类，C类）';











