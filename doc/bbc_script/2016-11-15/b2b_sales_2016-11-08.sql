CREATE TABLE "public"."t_product_sales_buffer" (
"id" serial4 NOT NULL,
"data_detail" text,
"email" varchar(50),
"create_date" timestamp(6),
"update_date" timestamp(6),
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_product_sales_buffer" IS '销售发货前台缓存数据表';

COMMENT ON COLUMN "public"."t_product_sales_buffer"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_product_sales_buffer"."data_detail" IS '缓存数据详情';

COMMENT ON COLUMN "public"."t_product_sales_buffer"."email" IS '分销商邮箱';

COMMENT ON COLUMN "public"."t_product_sales_buffer"."create_date" IS '创建日期 ';

COMMENT ON COLUMN "public"."t_product_sales_buffer"."update_date" IS '更新日期';

