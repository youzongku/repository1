ALTER TABLE "public"."t_purchase_order_detail"
ADD COLUMN "inter_bar_code" varchar(255);
COMMENT ON COLUMN "public"."t_purchase_order_detail"."inter_bar_code" IS '国际条码';