ALTER TABLE "public"."t_marketing_order_details"
ADD COLUMN "inter_bar_code" varchar(255);
COMMENT ON COLUMN "public"."t_marketing_order_details"."inter_bar_code" IS '国际条码';

ALTER TABLE "public"."t_product_sales_order_details"
ADD COLUMN "inter_bar_code" varchar(255);
COMMENT ON COLUMN "public"."t_product_sales_order_details"."inter_bar_code" IS '国际条码';