ALTER TABLE "public"."t_order_detail"
ADD COLUMN "contract_no" varchar(255);

COMMENT ON COLUMN "public"."t_order_detail"."contract_no" IS '合同号';

ALTER TABLE "public"."t_product_micro_inventory_in_record"
ADD COLUMN "contract_no" varchar(255);

COMMENT ON COLUMN "public"."t_product_micro_inventory_in_record"."contract_no" IS '合同号';
