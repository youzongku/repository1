ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "immediate_delivery" bool DEFAULT false;

COMMENT ON COLUMN "public"."t_purchase_order"."immediate_delivery" IS '立即发货标识';