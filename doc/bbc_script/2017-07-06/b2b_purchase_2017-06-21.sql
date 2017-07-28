ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "attribution_type" int4;

COMMENT ON COLUMN "public"."t_purchase_order"."attribution_type" IS '用户归属(1:线上 ,2:线下)';