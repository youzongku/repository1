ALTER TABLE "public"."t_return_order_logs"
ADD COLUMN "audit_remarks" varchar(500);

COMMENT ON COLUMN "public"."t_return_order_logs"."audit_remarks" IS '审核备注';
