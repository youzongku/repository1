ALTER TABLE "public"."t_product_sales_order_base"
ADD COLUMN "original_freight" float8;

COMMENT ON COLUMN "public"."t_product_sales_order_base"."original_freight" IS '下单时计算的实际运费，默认与bbc_postage的初始值一样（bbc_postage字段值可能会修改）';

update t_product_sales_order_base set original_freight=bbc_postage;