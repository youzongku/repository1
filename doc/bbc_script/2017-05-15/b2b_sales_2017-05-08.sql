ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "estimated_catfee_in_order" float8,
ADD COLUMN "real_catfee_in_order" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."estimated_catfee_in_order" IS '订单预估平摊费用';
COMMENT ON COLUMN "public"."t_product_sales_order_main"."real_catfee_in_order" IS '订单实际平摊费用';