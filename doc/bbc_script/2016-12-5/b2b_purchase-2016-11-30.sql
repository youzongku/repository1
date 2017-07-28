ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "customer_service" varchar(255);

COMMENT ON COLUMN "public"."t_purchase_order"."customer_service" IS '客服账号';

ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "reduce_price" float8;

COMMENT ON COLUMN "public"."t_purchase_order"."reduce_price" IS '减价金额';
