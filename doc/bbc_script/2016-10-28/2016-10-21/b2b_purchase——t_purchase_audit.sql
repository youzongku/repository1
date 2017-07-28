DROP TABLE IF EXISTS "public"."t_purchase_audit";
CREATE TABLE "public"."t_purchase_audit" (
"id" SERIAL NOT NULL,
"transfer_card" varchar(30) COLLATE "default",
"status" int4,
"purchase_no" varchar(30) COLLATE "default",
"transfer_number" varchar(50) COLLATE "default",
"transfer_amount" numeric(10,2),
"transfer_time" timestamp(6),
"screenshot_url" varchar(150) COLLATE "default",
"audit_reasons" varchar(150) COLLATE "default",
"audit_remark" varchar(300) COLLATE "default",
"apply_remark" varchar(300) COLLATE "default",
"transfer_name" varchar(30) COLLATE "default",
"email" varchar(30) COLLATE "default",
"recipient_card_id" int4,
"create_date" timestamp(6) DEFAULT now(),
"update_date" timestamp(6),
"transfer_type" varchar(50) COLLATE "default",
"create_user" varchar(50) COLLATE "default",
"order_amount" numeric(10,2),
"recipient_account" varchar(30) COLLATE "default",
"order_date" timestamp(6),
"received_amount" numeric(10,2),
"received_time" timestamp(6),
CONSTRAINT "t_purchase_audit_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_purchase_audit" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_purchase_audit" IS '线下转账提交审核记录';

COMMENT ON COLUMN "public"."t_purchase_audit"."id" IS '主键';

COMMENT ON COLUMN "public"."t_purchase_audit"."transfer_card" IS '转账卡号';

COMMENT ON COLUMN "public"."t_purchase_audit"."status" IS '1、待审核   2、审核通过 3、审核不通过';

COMMENT ON COLUMN "public"."t_purchase_audit"."purchase_no" IS '采购单号';

COMMENT ON COLUMN "public"."t_purchase_audit"."transfer_number" IS '交易流水号';

COMMENT ON COLUMN "public"."t_purchase_audit"."transfer_amount" IS '转账金额';

COMMENT ON COLUMN "public"."t_purchase_audit"."transfer_time" IS '转账时间';

COMMENT ON COLUMN "public"."t_purchase_audit"."screenshot_url" IS '截图路径';

COMMENT ON COLUMN "public"."t_purchase_audit"."audit_reasons" IS '审核理由';

COMMENT ON COLUMN "public"."t_purchase_audit"."audit_remark" IS '审核备注';

COMMENT ON COLUMN "public"."t_purchase_audit"."apply_remark" IS '申请备注';

COMMENT ON COLUMN "public"."t_purchase_audit"."transfer_name" IS '开户名';

COMMENT ON COLUMN "public"."t_purchase_audit"."email" IS '分销账号';

COMMENT ON COLUMN "public"."t_purchase_audit"."recipient_card_id" IS '收款账号ID';

COMMENT ON COLUMN "public"."t_purchase_audit"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_purchase_audit"."update_date" IS '更新时间';

COMMENT ON COLUMN "public"."t_purchase_audit"."transfer_type" IS '转账类型（支付宝微信银行卡之类）';

COMMENT ON COLUMN "public"."t_purchase_audit"."create_user" IS '录入人';

COMMENT ON COLUMN "public"."t_purchase_audit"."order_amount" IS '订单金额';

COMMENT ON COLUMN "public"."t_purchase_audit"."recipient_account" IS '收款账号';

COMMENT ON COLUMN "public"."t_purchase_audit"."order_date" IS '下单时间';

COMMENT ON COLUMN "public"."t_purchase_audit"."received_amount" IS '实收金额';

COMMENT ON COLUMN "public"."t_purchase_audit"."received_time" IS '到账时间';


alter table t_purchase_audit alter column transfer_amount type float8;


alter table t_purchase_audit alter column order_amount type float8;


alter table t_purchase_audit alter column received_amount type float8;

