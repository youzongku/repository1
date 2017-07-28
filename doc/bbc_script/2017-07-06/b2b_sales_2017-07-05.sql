ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "order_send_date" timestamp(6);

COMMENT ON COLUMN "public"."t_product_sales_order_main"."order_send_date" IS '发货时间';

