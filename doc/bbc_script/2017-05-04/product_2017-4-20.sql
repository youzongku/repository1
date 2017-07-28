ALTER TABLE "public"."t_operate_product_price"
ADD COLUMN "remark" varchar(500);
COMMENT ON COLUMN "public"."t_operate_product_price"."remark" IS '修改价格备注';
