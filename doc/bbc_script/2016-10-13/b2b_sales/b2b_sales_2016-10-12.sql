ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "compensation_payment_no" varchar(255),
ADD COLUMN "compensation_payment_amount" float8,
ADD COLUMN "compensation_pay_date" timestamp(6),   
ADD COLUMN "compensation_payment_type" varchar(50),
ADD COLUMN "dis_prime_cost" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."compensation_payment_no" IS '订单补差支付交易号';
COMMENT ON COLUMN "public"."t_product_sales_order_main"."compensation_pay_date" IS '订单补差支付时间';
COMMENT ON COLUMN "public"."t_product_sales_order_main"."compensation_payment_type" IS '订单补差支付方式';
COMMENT ON COLUMN "public"."t_product_sales_order_main"."dis_prime_cost" IS 'm站订单分销总成本';