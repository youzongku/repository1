ALTER TABLE "public"."t_product_disprice"
ADD COLUMN "vip_price" float8;

COMMENT ON COLUMN "public"."t_product_disprice"."vip_price" IS 'vip价格';


INSERT INTO "public"."t_product_price_rule" ("id", "c_rule", "status", "last_operator", "last_operator_time", "factor", "default_factor", "price_classification", "price_classification_desc", "field_name", "profit_rule") 
VALUES ('9', '$p*$f', 'f', NULL, NULL, NULL, NULL, 'vipPrice', 'VIP价格', 'proposalRetailPrice', '$p-$f');

