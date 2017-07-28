ALTER TABLE "public"."t_product_sales_order_details"
ADD COLUMN "clearance_price" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_details"."clearance_price" IS '清货价';

ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "clearance_price_total" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."clearance_price_total" IS '清货价总计';

ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "clear_total_cost" float8,
ADD COLUMN "clear_profit" float8,
ADD COLUMN "clear_profit_margin" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."clear_total_cost" IS '清货价总计';

COMMENT ON COLUMN "public"."t_product_sales_order_main"."clear_profit" IS '清货价利润';

COMMENT ON COLUMN "public"."t_product_sales_order_main"."clear_profit_margin" IS '清货价利润率';