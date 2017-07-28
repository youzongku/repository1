ALTER TABLE "public"."t_product_sales_order_details"
ADD COLUMN "arrive_ware_price" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_details"."arrive_ware_price" IS '到仓价(历史数据中获取)';

ALTER TABLE "public"."t_product_sales_order_details"
ADD COLUMN "cap_fee" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_details"."cap_fee" IS '均摊价格（历史数据中获取）';


ALTER TABLE "public"."t_product_sales_order_main"
ALTER COLUMN "id" SET DEFAULT '',
ADD COLUMN "is_close" bool DEFAULT false;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."is_close" IS '是否已经关闭过';


