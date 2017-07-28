ALTER TABLE "public"."t_purchase_audit"
ALTER COLUMN "audit_remark" TYPE varchar(550),
ALTER COLUMN "audit_reasons" TYPE varchar(550),
ALTER COLUMN "apply_remark" TYPE varchar(550);

ALTER TABLE "public"."t_purchase_order"
ALTER COLUMN "remark" TYPE varchar(550);
