ALTER TABLE "public"."t_marketing_order_details"
ADD COLUMN "expiration_date" varchar(50);
COMMENT ON COLUMN "public"."t_marketing_order_details"."expiration_date" IS '商品到期日期';


ALTER TABLE "public"."t_marketing_order_details"
ADD COLUMN "arrive_ware_price" float8;
COMMENT ON COLUMN "public"."t_marketing_order_details"."arrive_ware_price" IS '到仓价';