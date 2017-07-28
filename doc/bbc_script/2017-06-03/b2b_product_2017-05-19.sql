ALTER TABLE "public"."t_contract_quotations"
ADD COLUMN "category_id" int4;

COMMENT ON COLUMN "public"."t_contract_quotations"."category_id" IS '商品分类id';
