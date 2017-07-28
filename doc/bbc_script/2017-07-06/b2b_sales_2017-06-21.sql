ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "attribution_type" int4;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."attribution_type" IS '用户归属(1:线上 ,2:线下)';


ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "is_need_invoice" bool DEFAULT false;

COMMENT ON COLUMN "public"."t_product_sales_order_taobao"."is_need_invoice" IS '是否需要开发票(true:是 false:否)';

COMMENT ON COLUMN "public"."t_product_sales_order_taobao"."invoice_type" IS '发票类型(1:个人，2:公司)';

COMMENT ON COLUMN "public"."t_product_sales_order_taobao"."invoice_title" IS '发票抬头';


DROP TABLE IF EXISTS "public"."t_order_export_time_config";
CREATE TABLE "public"."t_order_export_time_config" (
"id" serial4 NOT NULL,
"operator" varchar COLLATE "default",
"is_sync" bool DEFAULT true,
"path" varchar COLLATE "default",
"file_name" varchar COLLATE "default",
"create_time" timestamp(6) DEFAULT now(),
"update_time" timestamp(6),
"export_type" varchar COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_order_export_time_config" IS '导出任务控制表';
COMMENT ON COLUMN "public"."t_order_export_time_config"."id" IS '主键';
COMMENT ON COLUMN "public"."t_order_export_time_config"."operator" IS '操作人';
COMMENT ON COLUMN "public"."t_order_export_time_config"."is_sync" IS '是否同步中';
COMMENT ON COLUMN "public"."t_order_export_time_config"."path" IS '文件路径';
COMMENT ON COLUMN "public"."t_order_export_time_config"."file_name" IS '文件名称';
COMMENT ON COLUMN "public"."t_order_export_time_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_order_export_time_config"."export_type" IS '导出类型(如:saleOrderExport 等 预留字段)';


-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table t_order_export_time_config
-- ----------------------------
ALTER TABLE "public"."t_order_export_time_config" ADD PRIMARY KEY ("id");

CREATE TABLE "public"."t_product_sales_order_invoice" (
"id" serial4  NOT NULL,
"sales_order_no" varchar COLLATE "default",
"invoice_type" int4,
"invoice_title" varchar(250) COLLATE "default",
"invoice_amount" float8,
"create_date" timestamp(6),
"update_date" timestamp(6),
"create_user" varchar COLLATE "default"
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_product_sales_order_invoice" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_product_sales_order_invoice" IS '发货单发票表';

COMMENT ON COLUMN "public"."t_product_sales_order_invoice"."id" IS '主键';

COMMENT ON COLUMN "public"."t_product_sales_order_invoice"."sales_order_no" IS '发货单号';

COMMENT ON COLUMN "public"."t_product_sales_order_invoice"."invoice_type" IS '发票类型(1:个人，2:公司)';

COMMENT ON COLUMN "public"."t_product_sales_order_invoice"."invoice_title" IS '发票抬头';

COMMENT ON COLUMN "public"."t_product_sales_order_invoice"."invoice_amount" IS '发票金额';

COMMENT ON COLUMN "public"."t_product_sales_order_invoice"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_product_sales_order_invoice"."update_date" IS '更新时间';

COMMENT ON COLUMN "public"."t_product_sales_order_invoice"."create_user" IS '创建人';
