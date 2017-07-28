ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "back_in" bool DEFAULT false;

COMMENT ON COLUMN "public"."t_purchase_order"."back_in" IS '后台录入标识';