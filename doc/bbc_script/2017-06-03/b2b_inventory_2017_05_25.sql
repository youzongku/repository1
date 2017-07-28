COMMENT ON COLUMN "public"."t_product_inventory_order_lock"."create_time" IS '最近一次库存校验时间';
ALTER TABLE "public"."t_product_inventory_order_lock" RENAME "create_time" TO "last_checktime";


ALTER TABLE "public"."t_product_inventory_order_lock"
ADD COLUMN "create_time" timestamp(6);
COMMENT ON COLUMN "public"."t_product_inventory_order_lock"."update_time" IS '更新时间';

COMMENT ON COLUMN "public"."t_product_inventory_order_lock"."create_time" IS '锁库记录生成时间';