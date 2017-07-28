ALTER TABLE "public"."t_order_detail"
ADD COLUMN "clearance_price" float8;

COMMENT ON COLUMN "public"."t_order_detail"."clearance_price" IS '清货价';
ALTER TABLE "public"."t_product_micro_inventory_in_record"
ADD COLUMN "clearance_price" float8;

COMMENT ON COLUMN "public"."t_product_micro_inventory_in_record"."clearance_price" IS '清货价';