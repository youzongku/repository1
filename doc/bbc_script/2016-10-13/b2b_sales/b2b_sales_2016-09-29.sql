ALTER TABLE "public"."t_product_sales_order_taobao"
ADD COLUMN "third_postfee" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_taobao"."third_postfee" IS '第三方运费';