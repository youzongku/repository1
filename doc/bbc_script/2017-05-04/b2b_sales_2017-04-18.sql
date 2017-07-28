ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "cs_confirm_date" timestamp(6);

COMMENT ON COLUMN "public"."t_product_sales_order_main"."cs_confirm_date" IS '最近一次的客服审核时间';
