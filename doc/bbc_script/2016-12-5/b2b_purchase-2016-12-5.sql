ALTER TABLE "public"."t_purchase_order_audit_logs"
ADD COLUMN "audit_type" int4;

COMMENT ON COLUMN "public"."t_purchase_order_audit_logs"."audit_type" IS '审核类型：1客服审核；2财务审核';