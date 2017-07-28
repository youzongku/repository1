CREATE TABLE "t_contract" (
"id" serial4 NOT NULL,
"contract_no" varchar(50) NOT NULL,
"account" varchar(50),
"phone" varchar(50),
"distribution_mode" int4,
"distribution_type" int4,
"distribution_name" varchar(255),
"contract_start" timestamp(6),
"contract_end" timestamp(6),
"bussiness_erp" varchar(50),
"create_time" timestamp(6) DEFAULT now(),
"update_time" timestamp(6) DEFAULT now(),
"create_user" varchar(50),
PRIMARY KEY ("id") 
);

COMMENT ON TABLE "t_contract" IS '合同表';
COMMENT ON COLUMN "t_contract"."id" IS '主键';
COMMENT ON COLUMN "t_contract"."contract_no" IS '合同号';
COMMENT ON COLUMN "t_contract"."account" IS '分销商账号';
COMMENT ON COLUMN "t_contract"."phone" IS '分销商电话';
COMMENT ON COLUMN "t_contract"."distribution_mode" IS '分销商渠道';
COMMENT ON COLUMN "t_contract"."distribution_type" IS '分销商类型';
COMMENT ON COLUMN "t_contract"."distribution_name" IS '分销商名称';
COMMENT ON COLUMN "t_contract"."contract_start" IS '合同开始时间';
COMMENT ON COLUMN "t_contract"."contract_end" IS '合同结束时间';
COMMENT ON COLUMN "t_contract"."bussiness_erp" IS '业务员erp账号';
COMMENT ON COLUMN "t_contract"."create_time" IS '创建时间';
COMMENT ON COLUMN "t_contract"."update_time" IS '更新时间';
COMMENT ON COLUMN "t_contract"."create_user" IS '创建人';

CREATE TABLE "t_contract_attachment" (
"id" serial4 NOT NULL,
"contract_no" varchar(50) NOT NULL,
"file_name" varchar(500),
"file_type" varchar(50),
"file_path" varchar(1000),
"status" int4 DEFAULT 0,
"create_time" timestamp(6) DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON TABLE "t_contract_attachment" IS '合同附件表';
COMMENT ON COLUMN "t_contract_attachment"."id" IS '主键ID';
COMMENT ON COLUMN "t_contract_attachment"."contract_no" IS '合同号';
COMMENT ON COLUMN "t_contract_attachment"."file_name" IS '文件名称';
COMMENT ON COLUMN "t_contract_attachment"."file_type" IS '文件后缀';
COMMENT ON COLUMN "t_contract_attachment"."file_path" IS '文件路径';
COMMENT ON COLUMN "t_contract_attachment"."status" IS '状态';
COMMENT ON COLUMN "t_contract_attachment"."create_time" IS '创建时间';

CREATE TABLE "t_contract_quotations" (
"id" serial4 NOT NULL,
"contract_no" varchar(50),
"sku" varchar(50),
"title" varchar(255),
"img_url" varchar(255),
"warehouse_id" int4,
"warehouse_name" varchar(50),
"purchase_price" float8,
"contract_price" float8,
"arrive_ware_price" float8,
"is_discount" bool DEFAULT false,
"discount" float8,
"contract_start" timestamp(6),
"contract_end" timestamp(6),
"status" int4,
"create_time" timestamp(6) DEFAULT now(),
"update_time" timestamp(6) DEFAULT now(),
"create_user" varchar(50),
PRIMARY KEY ("id") 
);

COMMENT ON TABLE "t_contract_quotations" IS '合同报价';
COMMENT ON COLUMN "t_contract_quotations"."id" IS '主键';
COMMENT ON COLUMN "t_contract_quotations"."contract_no" IS '合同编号';
COMMENT ON COLUMN "t_contract_quotations"."sku" IS 'SKU';
COMMENT ON COLUMN "t_contract_quotations"."title" IS '商品标题';
COMMENT ON COLUMN "t_contract_quotations"."img_url" IS '图片地址';
COMMENT ON COLUMN "t_contract_quotations"."warehouse_id" IS '仓库id';
COMMENT ON COLUMN "t_contract_quotations"."warehouse_name" IS '仓库名称';
COMMENT ON COLUMN "t_contract_quotations"."purchase_price" IS '采购价';
COMMENT ON COLUMN "t_contract_quotations"."contract_price" IS '合同价';
COMMENT ON COLUMN "t_contract_quotations"."arrive_ware_price" IS '到仓价';
COMMENT ON COLUMN "t_contract_quotations"."is_discount" IS '是否折扣(预留字段)';
COMMENT ON COLUMN "t_contract_quotations"."discount" IS '折扣值(预留字段)';
COMMENT ON COLUMN "t_contract_quotations"."contract_start" IS '合同报价开始';
COMMENT ON COLUMN "t_contract_quotations"."contract_end" IS '合同报价结束';
COMMENT ON COLUMN "t_contract_quotations"."status" IS '状态';
COMMENT ON COLUMN "t_contract_quotations"."create_time" IS '创建时间';
COMMENT ON COLUMN "t_contract_quotations"."update_time" IS '更新时间';
COMMENT ON COLUMN "t_contract_quotations"."create_user" IS '创建人';

ALTER TABLE "public"."t_contract"
ADD COLUMN "status" int4;

COMMENT ON COLUMN "public"."t_contract"."status" IS '合同状态';

ALTER TABLE "public"."t_contract_quotations"
ADD COLUMN "inter_bar_code" varchar(255);

COMMENT ON COLUMN "public"."t_contract_quotations"."inter_bar_code" IS '国际条码';

CREATE TABLE "t_quoted_oprecord" (
"id" serial4,
"qid" int4,
"opuser" varchar(50),
"opdate" timestamp(6) DEFAULT now(),
"comment" varchar(500)
);

COMMENT ON TABLE "t_quoted_oprecord" IS '报价操作日志表';
COMMENT ON COLUMN "t_quoted_oprecord"."qid" IS '报价id';
COMMENT ON COLUMN "t_quoted_oprecord"."opuser" IS '操作人';
COMMENT ON COLUMN "t_quoted_oprecord"."opdate" IS '操作时间';
COMMENT ON COLUMN "t_quoted_oprecord"."comment" IS '操作内容';

CREATE TABLE "t_contract_oprecord" (
"id" serial4,
"cid" int4,
"opuser" varchar(50),
"opdate" timestamp(6) DEFAULT now(),
"comment" varchar(500)
);

COMMENT ON TABLE "t_contract_oprecord" IS '合同操作记录';
COMMENT ON COLUMN "t_contract_oprecord"."cid" IS '合同id';
COMMENT ON COLUMN "t_contract_oprecord"."opuser" IS '操作人';
COMMENT ON COLUMN "t_contract_oprecord"."opdate" IS '操作时间';
COMMENT ON COLUMN "t_contract_oprecord"."comment" IS '内容';

ALTER TABLE "public"."t_contract_oprecord"
ALTER COLUMN "cid" TYPE varchar(50);

COMMENT ON COLUMN "public"."t_contract_oprecord"."cid" IS '合同编号';

ALTER TABLE "public"."t_contract_oprecord" RENAME "cid" TO "cno";



CREATE TABLE "public"."t_sequence" (
"id" serial4,
"seq_name" varchar(255) COLLATE "default",
"current_value" int8 DEFAULT 0,
"increment_" int4 DEFAULT 1,
"remark" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."t_sequence"."seq_name" IS '序列名';
COMMENT ON COLUMN "public"."t_sequence"."current_value" IS '当前序列值';
COMMENT ON COLUMN "public"."t_sequence"."increment_" IS '步长';
COMMENT ON COLUMN "public"."t_sequence"."remark" IS '备注';

INSERT INTO "public"."t_sequence" VALUES ('1', 'CONTRACT_NO', '14', '1', '合同号');