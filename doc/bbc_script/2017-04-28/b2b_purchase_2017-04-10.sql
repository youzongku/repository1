ALTER TABLE "public"."t_purchase_order_detail"
ADD COLUMN "clearance_price" float8;

COMMENT ON COLUMN "public"."t_purchase_order_detail"."clearance_price" IS '清货价';
