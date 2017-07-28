ALTER TABLE "public"."t_product_sales_order_details"
ADD COLUMN "purchase_order_no" varchar(255);

COMMENT ON COLUMN "public"."t_product_sales_order_details"."purchase_order_no" IS '²É¹ºµ¥ºÅ';

