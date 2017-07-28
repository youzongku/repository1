ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "rejected_by_finance" bool DEFAULT false;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."rejected_by_finance" IS '是否由财务驳回，此字段只有在发货单财务审核才有用（只有审核不通过才为true，其余为false）';