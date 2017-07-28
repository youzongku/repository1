ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "opt_fee" float8 DEFAULT 0.00;
COMMENT ON COLUMN "public"."t_product_sales_order_main"."opt_fee" IS '操作费，默认为0';