-- 修改表
alter table t_product_sales_order_main

ADD COLUMN "is_package_mail" int2;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."is_package_mail" IS '是否包邮 1：包邮 2：不包邮';