
DROP TABLE IF EXISTS "public"."t_charges_oprecord";
CREATE TABLE "public"."t_charges_oprecord" (
"id" serial4 NOT NULL,
"cid" int4,
"opuser" varchar(50) COLLATE "default",
"opdate" timestamp(6) DEFAULT now(),
"comment" varchar(500) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_charges_oprecord" IS '合同费用操作日志';
COMMENT ON COLUMN "public"."t_charges_oprecord"."cid" IS '费用id';
COMMENT ON COLUMN "public"."t_charges_oprecord"."opuser" IS '操作人';
COMMENT ON COLUMN "public"."t_charges_oprecord"."opdate" IS '操作时间';
COMMENT ON COLUMN "public"."t_charges_oprecord"."comment" IS '操作内容';

ALTER TABLE "public"."t_charges_oprecord" ADD PRIMARY KEY ("id");

DROP TABLE IF EXISTS "public"."t_contract_cost";
CREATE TABLE "public"."t_contract_cost" (
"id" serial4 NOT NULL,
"contract_no" varchar(255) COLLATE "default",
"cost_type_id" int4,
"scale_of_charges" float8,
"start_time" timestamp(6),
"end_time" timestamp(6),
"create_time" timestamp(6) DEFAULT now(),
"update_time" timestamp(6),
"create_user" varchar(255) COLLATE "default",
"remark" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_contract_cost" IS '合同费用表';
COMMENT ON COLUMN "public"."t_contract_cost"."id" IS '主键';
COMMENT ON COLUMN "public"."t_contract_cost"."contract_no" IS '合同号';
COMMENT ON COLUMN "public"."t_contract_cost"."cost_type_id" IS '费用类型id';
COMMENT ON COLUMN "public"."t_contract_cost"."scale_of_charges" IS '费用率';
COMMENT ON COLUMN "public"."t_contract_cost"."start_time" IS '开始时间';
COMMENT ON COLUMN "public"."t_contract_cost"."end_time" IS '结束时间';
COMMENT ON COLUMN "public"."t_contract_cost"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_contract_cost"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_contract_cost"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."t_contract_cost"."remark" IS '备注';

ALTER TABLE "public"."t_contract_cost" ADD PRIMARY KEY ("id");




DROP TABLE IF EXISTS "public"."t_contract_cost_type";
CREATE TABLE "public"."t_contract_cost_type" (
"id" serial4 NOT NULL,
"type" varchar(255) COLLATE "default",
"formula" varchar(255) COLLATE "default",
"formula_desc" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_contract_cost_type" IS '合同费用类型';
COMMENT ON COLUMN "public"."t_contract_cost_type"."id" IS '主键';
COMMENT ON COLUMN "public"."t_contract_cost_type"."type" IS '费用类型';
COMMENT ON COLUMN "public"."t_contract_cost_type"."formula" IS '计算公式';
COMMENT ON COLUMN "public"."t_contract_cost_type"."formula_desc" IS '公式描述';


INSERT INTO "public"."t_contract_cost_type" VALUES ('1', '合同扣点', '$p*$f', '商品供货总价×费用率');

ALTER TABLE "public"."t_contract_cost_type" ADD PRIMARY KEY ("id");