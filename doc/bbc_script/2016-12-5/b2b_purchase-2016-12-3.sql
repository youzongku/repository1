CREATE TABLE "public"."t_purchase_order_audit_logs" (
"id" serial4 NOT NULL,
"status" int4,
"purchase_no" varchar(30) COLLATE "default",
"json_value" varchar(3000) COLLATE "default",
"audit_user" varchar(50) COLLATE "default",
"audit_date" timestamp(6) DEFAULT now(),
"audit_type" int4,
PRIMARY KEY ("id")
)
;

ALTER TABLE "public"."t_purchase_order_audit_logs" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_purchase_order_audit_logs" IS '采购单审核历史记录';

COMMENT ON COLUMN "public"."t_purchase_order_audit_logs"."id" IS '主键';

COMMENT ON COLUMN "public"."t_purchase_order_audit_logs"."status" IS '采购单状态';

COMMENT ON COLUMN "public"."t_purchase_order_audit_logs"."purchase_no" IS '采购单号';

COMMENT ON COLUMN "public"."t_purchase_order_audit_logs"."json_value" IS 'json数据';

COMMENT ON COLUMN "public"."t_purchase_order_audit_logs"."audit_user" IS '审核人';

COMMENT ON COLUMN "public"."t_purchase_order_audit_logs"."audit_date" IS '审核时间';

COMMENT ON COLUMN "public"."t_purchase_order_audit_logs"."audit_type" IS '审核类型：1客服审核；2财务审核';