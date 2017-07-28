
DROP TABLE IF EXISTS "public"."t_product_sales_order_fee";
CREATE TABLE "public"."t_product_sales_order_fee" (
"sales_order_no" varchar COLLATE "default",
"contract_no" varchar COLLATE "default",
"attr_key" varchar COLLATE "default",
"attr_name" varchar COLLATE "default",
"value" varchar COLLATE "default",
"create_time" timestamp(6) DEFAULT now(),
"update_time" timestamp(6),
"is_delete" bool DEFAULT false,
"fee_id" int4,
"uid" varchar(250) COLLATE "default" NOT NULL,
PRIMARY KEY ("uid")
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_product_sales_order_fee" IS '销售发货单费用表';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."sales_order_no" IS '发货单号';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."contract_no" IS '合同号';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."attr_key" IS '属性key';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."attr_name" IS '属性名称';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."value" IS '属性值';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."is_delete" IS '是否删除标识';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."fee_id" IS '费用项id';
COMMENT ON COLUMN "public"."t_product_sales_order_fee"."uid" IS '主键id';


