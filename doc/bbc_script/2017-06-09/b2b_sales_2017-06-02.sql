ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "is_combine" bool DEFAULT false,
ADD COLUMN "combine_order_no" varchar;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."is_combine" IS '是否合并发货标识';

COMMENT ON COLUMN "public"."t_product_sales_order_main"."combine_order_no" IS '合并发货单主单号';


INSERT INTO "public"."t_sequence" 
("id", "seq_name", "current_value", "increment_", "remark") 
VALUES ('5', 'COMBINE_SALE_NO', '1', '1', '合并发货单');

ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "combine_order_count" int4;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."combine_order_count" IS '合并发货单数量';