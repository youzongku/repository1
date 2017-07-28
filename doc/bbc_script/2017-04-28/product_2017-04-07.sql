ALTER TABLE "public"."t_product_disprice"
ADD COLUMN "sale_status" int4,
ADD COLUMN "clearance_rate" float8,
ADD COLUMN "clearance_price" float8;

COMMENT ON COLUMN "public"."t_product_disprice"."sale_status" IS 'erp销售状态(40：常售,30：促销，20：清货，13：预售，16:缺货，0:编辑中 )';

COMMENT ON COLUMN "public"."t_product_disprice"."clearance_rate" IS '清货率';

COMMENT ON COLUMN "public"."t_product_disprice"."clearance_price" IS '清货价';
