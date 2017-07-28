ALTER TABLE "public"."t_marketing_order"
ADD COLUMN "tawprice" float8;

COMMENT ON COLUMN "public"."t_marketing_order"."tawprice" IS '总到仓价';
