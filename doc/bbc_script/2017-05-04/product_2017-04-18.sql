ALTER TABLE "public"."t_product_base"
ADD COLUMN "salable" int4 DEFAULT 1;

COMMENT ON COLUMN "public"."t_product_base"."salable" IS '非卖状态（0不可卖，1可卖）';

CREATE TABLE "public"."t_product_base_logs" (
"id" serial4,
"csku" varchar(40) COLLATE "default",
"istatus" int4,
"salable" int4,
"opt_type" int4,
"opt_user" varchar(50) COLLATE "default",
"opt_date" timestamp(6) DEFAULT now(),
PRIMARY KEY ("id")
);

COMMENT ON TABLE "public"."t_product_base_logs" IS '修改产品基础日志表';
COMMENT ON COLUMN "public"."t_product_base_logs"."id" IS '主键';
COMMENT ON COLUMN "public"."t_product_base_logs"."csku" IS 'SKU';
COMMENT ON COLUMN "public"."t_product_base_logs"."istatus" IS '状态(在售、停售、下架)';
COMMENT ON COLUMN "public"."t_product_base_logs"."salable" IS '非卖状态（0不可卖，1可卖）';
COMMENT ON COLUMN "public"."t_product_base_logs"."opt_type" IS '操作类型';
COMMENT ON COLUMN "public"."t_product_base_logs"."opt_user" IS '操作人';
COMMENT ON COLUMN "public"."t_product_base_logs"."opt_date" IS '新品期结束时间';
