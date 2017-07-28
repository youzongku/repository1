

CREATE TABLE "public"."t_product_sales_input" (
"id" serial4 NOT NULL,
"email" varchar(50) COLLATE "default",
"sku" varchar(50) COLLATE "default",
"warehouse_id" int4,
"warehouse_name" varchar(255) COLLATE "default",
"product_img" varchar(255) COLLATE "default",
"final_selling_price" float8,
"qty" int4,
"title" varchar(240) COLLATE "default",
 PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_product_sales_input" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_product_sales_input" IS '销售发货录入表';

COMMENT ON COLUMN "public"."t_product_sales_input"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_product_sales_input"."email" IS '选择的分销商';

COMMENT ON COLUMN "public"."t_product_sales_input"."sku" IS '已选商品sku';

COMMENT ON COLUMN "public"."t_product_sales_input"."warehouse_id" IS '仓库id';

COMMENT ON COLUMN "public"."t_product_sales_input"."warehouse_name" IS '已选仓库名';

COMMENT ON COLUMN "public"."t_product_sales_input"."product_img" IS '产品图片';

COMMENT ON COLUMN "public"."t_product_sales_input"."final_selling_price" IS '平台最终售价';

COMMENT ON COLUMN "public"."t_product_sales_input"."qty" IS '所选商品数量';

COMMENT ON COLUMN "public"."t_product_sales_input"."title" IS '产品名称';