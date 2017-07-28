ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "nick_name" varchar(50);

COMMENT ON COLUMN "public"."t_product_sales_order_main"."nick_name" IS '分销商昵称';


DROP TABLE IF EXISTS "public"."t_audit_remark";
CREATE TABLE "public"."t_audit_remark" (
"id" serial4 NOT NULL,
"ip" varchar COLLATE "default",
"order_id" int4,
"operator" varchar(255) COLLATE "default",
"remark" text COLLATE "default",
"status" int4,
"create_date" timestamp(6) DEFAULT now()
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_audit_remark" IS '后台财务审核备注表，用于在线支付回调取值';
COMMENT ON COLUMN "public"."t_audit_remark"."id" IS '主键';
COMMENT ON COLUMN "public"."t_audit_remark"."order_id" IS '订单id';
COMMENT ON COLUMN "public"."t_audit_remark"."operator" IS '操作人';
COMMENT ON COLUMN "public"."t_audit_remark"."remark" IS '备注';
COMMENT ON COLUMN "public"."t_audit_remark"."status" IS '状态';
COMMENT ON COLUMN "public"."t_audit_remark"."create_date" IS '创建时间';

ALTER TABLE "public"."t_audit_remark" ADD PRIMARY KEY ("id");



ALTER TABLE "public"."t_product_sales_order_details"
ADD COLUMN "expiration_date" timestamp(6);

COMMENT ON COLUMN "public"."t_product_sales_order_details"."expiration_date" IS '到期日期(微仓出库明细)';



ALTER TABLE "public"."t_marketing_order_details"
ADD COLUMN "category_id" int4,
ADD COLUMN "category_name" varchar(255);

COMMENT ON COLUMN "public"."t_marketing_order_details"."category_id" IS '类目名称';

COMMENT ON COLUMN "public"."t_marketing_order_details"."category_name" IS '类目名称';
