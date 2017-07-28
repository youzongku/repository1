ALTER TABLE "public"."t_purchase_order_detail"
ADD COLUMN "contract_no" varchar(255);

COMMENT ON COLUMN "public"."t_purchase_order_detail"."contract_no" IS '合同号';
