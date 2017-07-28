alter table t_product_sales_order_main

ADD COLUMN "erp_status" int4 DEFAULT -2;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."erp_status" IS 'erp状态';