ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "shop_deduction_points" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."shop_deduction_points" IS '扣点率';

ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "sdp_amount" float8;


COMMENT ON COLUMN "public"."t_product_sales_order_main"."sdp_amount" IS '扣点金额';