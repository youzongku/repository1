
ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "oa_audit_no" varchar(255),
ADD COLUMN "buseness_remarks" varchar(500);

COMMENT ON COLUMN "public"."t_purchase_order"."oa_audit_no" IS 'oa审批单号，唯一的';
COMMENT ON COLUMN "public"."t_purchase_order"."buseness_remarks" IS '业务备注，remarks是客户备注';



CREATE TABLE "public"."t_purchase_stockout" (
"id" serial4 NOT NULL,
"purchase_order_no" varchar(255) COLLATE "default",
"json_str" text COLLATE "default",
"create_date" timestamp(6),
"status" int4 DEFAULT 0,
"last_update_date" timestamp(6),
PRIMARY KEY ("id")
);

ALTER TABLE "public"."t_purchase_stockout" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_purchase_stockout" IS '整批出库暂存的数据';

COMMENT ON COLUMN "public"."t_purchase_stockout"."purchase_order_no" IS '要整批出库的采购单';

COMMENT ON COLUMN "public"."t_purchase_stockout"."json_str" IS 'json数据';

COMMENT ON COLUMN "public"."t_purchase_stockout"."status" IS '状态：0未执行；1执行成功；2执行失败';






ALTER TABLE "public"."t_purchase_audit"
ADD COLUMN "recipient_name" varchar(100);

COMMENT ON COLUMN "public"."t_purchase_audit"."recipient_name" IS '收款账户名';