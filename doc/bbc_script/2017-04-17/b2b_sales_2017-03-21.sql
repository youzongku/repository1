ALTER TABLE "public"."t_product_sales_order_details"
ADD COLUMN "contract_no" varchar(255);

COMMENT ON COLUMN "public"."t_product_sales_order_details"."contract_no" IS '合同号';


ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "arrvice_total" float8,
ADD COLUMN "total_cost" float8,
ADD COLUMN "profit" float8,
ADD COLUMN "profit_margin" float8,
ADD COLUMN "contract_charge" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."arrvice_total" IS '到仓价总计';

COMMENT ON COLUMN "public"."t_product_sales_order_main"."total_cost" IS '订单总成本 ';

COMMENT ON COLUMN "public"."t_product_sales_order_main"."profit" IS '利润';

COMMENT ON COLUMN "public"."t_product_sales_order_main"."profit_margin" IS '利润率';

COMMENT ON COLUMN "public"."t_product_sales_order_main"."contract_charge" IS '合同费用';

