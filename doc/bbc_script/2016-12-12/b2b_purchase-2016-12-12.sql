ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "tawprice" float8;

COMMENT ON COLUMN "public"."t_purchase_order"."tawprice" IS '总到仓价';