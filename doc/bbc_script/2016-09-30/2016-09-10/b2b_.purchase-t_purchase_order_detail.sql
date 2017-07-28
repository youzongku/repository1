





ALTER TABLE "public"."t_purchase_order_detail"
ADD COLUMN "is_back" bool DEFAULT false,
ADD COLUMN "input_date" timestamp(6);

COMMENT ON COLUMN "public"."t_purchase_order_detail"."is_back" IS '是否为后台录入';

COMMENT ON COLUMN "public"."t_purchase_order_detail"."input_date" IS '录入时间';

ALTER TABLE "public"."t_purchase_order_input_pro"
ALTER COLUMN "sku" TYPE varchar(255) COLLATE "default";


