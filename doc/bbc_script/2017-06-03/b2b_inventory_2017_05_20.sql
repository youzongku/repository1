ALTER TABLE "public"."t_product_inventory_batch_detail"
ADD COLUMN "create_time" timestamp;

COMMENT ON COLUMN "public"."t_product_inventory_batch_detail"."create_time" IS '记录创建时间';