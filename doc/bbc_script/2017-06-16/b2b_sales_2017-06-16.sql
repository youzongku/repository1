ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "erp_order_no" varchar(50);

COMMENT ON COLUMN "public"."t_product_sales_order_main"."erp_order_no" IS '订单在erp的单号';