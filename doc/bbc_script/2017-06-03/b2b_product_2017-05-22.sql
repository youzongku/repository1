CREATE TABLE "t_contract_fee_items" (
"id" serial4 NOT NULL,
"fee_type_id" int4,
"fee_type_name" varchar(255),
"fee_type" int4,
"contract_no" varchar(50),
"start_time" timestamp(6),
"end_time" timestamp(6),
"content" varchar(2000),
"remarks" text,
"deleted" bool DEFAULT false,
"create_user" varchar(50),
"create_time" timestamp(6),
"last_update_user" varchar(50),
"update_time" timestamp(6),
PRIMARY KEY ("id") 
);

COMMENT ON TABLE "t_contract_fee_items" IS '合同费用项对应值表';
COMMENT ON COLUMN "t_contract_fee_items"."id" IS '主键';
COMMENT ON COLUMN "t_contract_fee_items"."fee_type_id" IS '费用项类型id';
COMMENT ON COLUMN "t_contract_fee_items"."fee_type_name" IS '费用类型名称';
COMMENT ON COLUMN "t_contract_fee_items"."fee_type" IS '费用项数据类型（百分比、固定值）';
COMMENT ON COLUMN "t_contract_fee_items"."contract_no" IS '合同号';
COMMENT ON COLUMN "t_contract_fee_items"."start_time" IS '开始时间，目前默认合同开始时间';
COMMENT ON COLUMN "t_contract_fee_items"."end_time" IS '结束时间，目前默认合同结束时间';
COMMENT ON COLUMN "t_contract_fee_items"."content" IS '具体值json串';
COMMENT ON COLUMN "t_contract_fee_items"."deleted" IS '是否被删除，默认false';
COMMENT ON COLUMN "t_contract_fee_items"."create_user" IS '创建人';
COMMENT ON COLUMN "t_contract_fee_items"."last_update_user" IS '最后更新人';

CREATE TABLE "t_contract_fee_items_related_skus" (
"id" serial4 NOT NULL,
"fee_item_id" int4,
"product_name" varchar(200),
"sku" varchar(50),
"warehouse_id" int4,
"warehouse_name" varchar(50),
"category_id" int4,
"category_name" varchar(50),
"contract_price" float8,
PRIMARY KEY ("id")
);
COMMENT ON TABLE "t_contract_fee_items_related_skus" IS '合同费用项适用的商品';
COMMENT ON COLUMN "t_contract_fee_items_related_skus"."id" IS '主键';
COMMENT ON COLUMN "t_contract_fee_items_related_skus"."fee_item_id" IS '合同费用项id';
COMMENT ON COLUMN "t_contract_fee_items_related_skus"."contract_price" IS '合同报价';


DROP TABLE IF EXISTS "public"."t_contract_fee_item_logs";
CREATE TABLE "public"."t_contract_fee_item_logs" (
"id" serial4 NOT NULL,
"fee_item_id" int4,
"fee_type_name" varchar(255) COLLATE "default",
"fee_type" int4,
"content_original" varchar(2000) COLLATE "default",
"content_new" varchar(2000) COLLATE "default",
"opt_type" int4,
"opt_user" varchar(50) COLLATE "default",
"opt_time" timestamp(6)
)
WITH (OIDS=FALSE);
COMMENT ON TABLE "public"."t_contract_fee_item_logs" IS '合同费用项日志';
COMMENT ON COLUMN "public"."t_contract_fee_item_logs"."id" IS '主键';
COMMENT ON COLUMN "public"."t_contract_fee_item_logs"."fee_item_id" IS '合同费用项id';
COMMENT ON COLUMN "public"."t_contract_fee_item_logs"."fee_type_name" IS '费用类型名称';
COMMENT ON COLUMN "public"."t_contract_fee_item_logs"."fee_type" IS '费用项数据类型（百分比、固定值）';
COMMENT ON COLUMN "public"."t_contract_fee_item_logs"."content_original" IS '更新前的具体值json串';
COMMENT ON COLUMN "public"."t_contract_fee_item_logs"."content_new" IS '更新后的具体值json串';
COMMENT ON COLUMN "public"."t_contract_fee_item_logs"."opt_type" IS '操作类型';
COMMENT ON COLUMN "public"."t_contract_fee_item_logs"."opt_user" IS '创建人';

ALTER TABLE "public"."t_contract_fee_item_logs" ADD PRIMARY KEY ("id");



CREATE TABLE "t_contract_feetype" (

"id" serial4 NOT NULL,

"name" varchar(255),

"desc" varchar(255),

"type" int4,

"create_time" timestamp(6) DEFAULT now(),

"create_user" varchar(50),

PRIMARY KEY ("id") 

);


COMMENT ON TABLE "t_contract_feetype" IS '费用项类型表';

COMMENT ON COLUMN "t_contract_feetype"."id" IS '主键id';

COMMENT ON COLUMN "t_contract_feetype"."name" IS '费用项名称';

COMMENT ON COLUMN "t_contract_feetype"."desc" IS '费用项描述';

COMMENT ON COLUMN "t_contract_feetype"."type" IS '费用项数据类型（1 固定费用值 2：固定费用率）';

COMMENT ON COLUMN "t_contract_feetype"."create_time" IS '创建时间';

COMMENT ON COLUMN "t_contract_feetype"."create_user" IS '创建人';