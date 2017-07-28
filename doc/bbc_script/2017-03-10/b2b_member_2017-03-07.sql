
-- ----------------------------
-- Table structure for t_account_period_master
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_account_period_master";
CREATE TABLE "public"."t_account_period_master" (
"id" serial4 NOT NULL,
"account" varchar(255) COLLATE "default",
"total_limit" numeric,
"duty_officer" varchar(255) COLLATE "default",
"oa_audit_code" varchar(255) COLLATE "default",
"contract_no" varchar(255) COLLATE "default",
"period_type" int4,
"period_length" int4,
"recharge_total" numeric DEFAULT 0,
"recharge_left" numeric DEFAULT 0,
"create_user" varchar(255) COLLATE "default",
"create_date" timestamp(6) DEFAULT now(),
"update_date" timestamp(6),
"used_limit" numeric DEFAULT 0
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_account_period_master" IS '账期信息主表';
COMMENT ON COLUMN "public"."t_account_period_master"."id" IS '主键';
COMMENT ON COLUMN "public"."t_account_period_master"."account" IS '分销商账号';
COMMENT ON COLUMN "public"."t_account_period_master"."total_limit" IS '账期额度';
COMMENT ON COLUMN "public"."t_account_period_master"."duty_officer" IS '责任人';
COMMENT ON COLUMN "public"."t_account_period_master"."oa_audit_code" IS 'oa审批号';
COMMENT ON COLUMN "public"."t_account_period_master"."contract_no" IS '合同号';
COMMENT ON COLUMN "public"."t_account_period_master"."period_type" IS '周期类型(0：天数,1：自然月)';
COMMENT ON COLUMN "public"."t_account_period_master"."period_length" IS '周期长度';
COMMENT ON COLUMN "public"."t_account_period_master"."recharge_total" IS '总已还账期额度';
COMMENT ON COLUMN "public"."t_account_period_master"."recharge_left" IS '剩余已还额度';
COMMENT ON COLUMN "public"."t_account_period_master"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."t_account_period_master"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."t_account_period_master"."update_date" IS '更新时间';
COMMENT ON COLUMN "public"."t_account_period_master"."used_limit" IS '已用额度';

ALTER TABLE "public"."t_account_period_master" ADD PRIMARY KEY ("id");




DROP TABLE IF EXISTS "public"."t_account_period_slave";
CREATE TABLE "public"."t_account_period_slave" (
"id" serial4 NOT NULL,
"master_id" int4,
"total_limit" numeric,
"start_time" timestamp(6),
"contract_period_date" timestamp(6),
"red_line_date" timestamp(6),
"red_line_days" int4,
"state" int4 DEFAULT 0,
"performance_start_time" timestamp(6),
"performance_end_time" timestamp(6),
"create_date" timestamp(6) DEFAULT now(),
"create_user" varchar(255) COLLATE "default",
"update_date" timestamp(6),
"has_next" bool DEFAULT false,
"is_charge_off" bool DEFAULT false
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_account_period_slave" IS '账期子表';
COMMENT ON COLUMN "public"."t_account_period_slave"."master_id" IS '主表id';
COMMENT ON COLUMN "public"."t_account_period_slave"."total_limit" IS '账期额度(跟主表一致，这里存放方便计算)';
COMMENT ON COLUMN "public"."t_account_period_slave"."start_time" IS '账期开始时间';
COMMENT ON COLUMN "public"."t_account_period_slave"."contract_period_date" IS '合同账期时间';
COMMENT ON COLUMN "public"."t_account_period_slave"."red_line_date" IS '红线账期时间';
COMMENT ON COLUMN "public"."t_account_period_slave"."red_line_days" IS '红线天数（周期结束时间加+天数=红线时间）';
COMMENT ON COLUMN "public"."t_account_period_slave"."state" IS '账期状态( 0未生效,1:可使用,2:待还款,3:已逾期（账户冻结）,4:禁用中 （无法透支）,5 :已完结 ) ';
COMMENT ON COLUMN "public"."t_account_period_slave"."performance_start_time" IS '业绩周期开始时间';
COMMENT ON COLUMN "public"."t_account_period_slave"."performance_end_time" IS '业绩周期结束时间';
COMMENT ON COLUMN "public"."t_account_period_slave"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."t_account_period_slave"."update_date" IS '更新时间';
COMMENT ON COLUMN "public"."t_account_period_slave"."has_next" IS '是否已开启下一期';
COMMENT ON COLUMN "public"."t_account_period_slave"."is_charge_off" IS '是否核销';

ALTER TABLE "public"."t_account_period_slave" ADD PRIMARY KEY ("id");


DROP TABLE IF EXISTS "public"."t_ap_bill";
CREATE TABLE "public"."t_ap_bill" (
"id" serial4 NOT NULL,
"total_amount" numeric,
"arear_amount" numeric,
"account" varchar(255) COLLATE "default",
"ap_id" int4,
"recharge_left" numeric,
"verification_user" varchar(255) COLLATE "default",
"verification_date" timestamp(6),
"create_date" timestamp(6) DEFAULT now(),
"create_user" varchar(255) COLLATE "default",
"is_charge_off" bool DEFAULT false
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_ap_bill" IS '账期账单表';
COMMENT ON COLUMN "public"."t_ap_bill"."id" IS '主键';
COMMENT ON COLUMN "public"."t_ap_bill"."total_amount" IS '账单总额';
COMMENT ON COLUMN "public"."t_ap_bill"."arear_amount" IS '应还金额';
COMMENT ON COLUMN "public"."t_ap_bill"."account" IS '分销商账号';
COMMENT ON COLUMN "public"."t_ap_bill"."ap_id" IS '账期id';
COMMENT ON COLUMN "public"."t_ap_bill"."recharge_left" IS '总已还金额';
COMMENT ON COLUMN "public"."t_ap_bill"."verification_user" IS '核销人';
COMMENT ON COLUMN "public"."t_ap_bill"."verification_date" IS '核销时间';
COMMENT ON COLUMN "public"."t_ap_bill"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."t_ap_bill"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."t_ap_bill"."is_charge_off" IS '是否已经核销';

ALTER TABLE "public"."t_ap_bill" ADD PRIMARY KEY ("id");


DROP TABLE IF EXISTS "public"."t_ap_bill_order_mapping";
CREATE TABLE "public"."t_ap_bill_order_mapping" (
"id" serial4 NOT NULL,
"order_id" int4,
"bill_id" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_ap_bill_order_mapping" IS '账期账单订单关系表';
COMMENT ON COLUMN "public"."t_ap_bill_order_mapping"."id" IS '主键';
COMMENT ON COLUMN "public"."t_ap_bill_order_mapping"."order_id" IS '订单id';
COMMENT ON COLUMN "public"."t_ap_bill_order_mapping"."bill_id" IS '账单id';

ALTER TABLE "public"."t_ap_bill_order_mapping" ADD PRIMARY KEY ("id");


DROP TABLE IF EXISTS "public"."t_ap_change";
CREATE TABLE "public"."t_ap_change" (
"id" serial4 NOT NULL,
"change_amount" numeric,
"total_amount" numeric,
"describe" varchar(255) COLLATE "default",
"change_date" timestamp(6),
"account" varchar(255) COLLATE "default",
"ap_id" int4,
"type" int4 DEFAULT 0
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_ap_change" IS '账期金额变化表';
COMMENT ON COLUMN "public"."t_ap_change"."change_amount" IS '变化值(可为正负数)';
COMMENT ON COLUMN "public"."t_ap_change"."total_amount" IS '账期总额';
COMMENT ON COLUMN "public"."t_ap_change"."describe" IS '描述(充值，支付)';
COMMENT ON COLUMN "public"."t_ap_change"."change_date" IS '变化时间';
COMMENT ON COLUMN "public"."t_ap_change"."account" IS '变化时间';
COMMENT ON COLUMN "public"."t_ap_change"."ap_id" IS '账期id';
COMMENT ON COLUMN "public"."t_ap_change"."type" IS '类型:0为扣款，1位还款';

ALTER TABLE "public"."t_ap_change" ADD PRIMARY KEY ("id");



DROP TABLE IF EXISTS "public"."t_ap_change_mapping";
CREATE TABLE "public"."t_ap_change_mapping" (
"id" serial4 NOT NULL,
"bill_id" int4,
"change_id" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_ap_change_mapping" IS '账期金额变化与账单中间表';
COMMENT ON COLUMN "public"."t_ap_change_mapping"."bill_id" IS '账单id';
COMMENT ON COLUMN "public"."t_ap_change_mapping"."change_id" IS '账期金额变化表id';

ALTER TABLE "public"."t_ap_change_mapping" ADD PRIMARY KEY ("id");



DROP TABLE IF EXISTS "public"."t_ap_opt_record";
CREATE TABLE "public"."t_ap_opt_record" (
"id" serial4 NOT NULL,
"operator" varchar(255) COLLATE "default",
"operate_time" timestamp(6) DEFAULT now(),
"operate_type" int4,
"operate_desc" text COLLATE "default",
"slave_id" int4,
"master_id" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."t_ap_opt_record"."id" IS '主键';
COMMENT ON COLUMN "public"."t_ap_opt_record"."operator" IS '操作人';
COMMENT ON COLUMN "public"."t_ap_opt_record"."operate_time" IS '操作时间';
COMMENT ON COLUMN "public"."t_ap_opt_record"."operate_type" IS '操作类型（0：新增 1：修改 2：禁用 3：启用 4：生成账单  5：核销 6：开启下一期）';
COMMENT ON COLUMN "public"."t_ap_opt_record"."operate_desc" IS '操作描述';
COMMENT ON COLUMN "public"."t_ap_opt_record"."slave_id" IS '子账期id';
COMMENT ON COLUMN "public"."t_ap_opt_record"."master_id" IS '账期id';

ALTER TABLE "public"."t_ap_opt_record" ADD PRIMARY KEY ("id");

DROP TABLE IF EXISTS "public"."t_order_by_ap";
CREATE TABLE "public"."t_order_by_ap" (
"id" serial4 NOT NULL,
"pay_amount" numeric,
"pay_date" timestamp(6) DEFAULT now(),
"is_choice" int4 DEFAULT 0,
"ap_id" int4,
"refund_date" timestamp(6),
"account" varchar(255) COLLATE "default",
"status" int4,
"order_no" varchar(255) COLLATE "default",
"order_amount" numeric
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_order_by_ap" IS '账期订单表';
COMMENT ON COLUMN "public"."t_order_by_ap"."id" IS '主键';
COMMENT ON COLUMN "public"."t_order_by_ap"."pay_amount" IS '账期支付金额';
COMMENT ON COLUMN "public"."t_order_by_ap"."pay_date" IS '支付时间';
COMMENT ON COLUMN "public"."t_order_by_ap"."is_choice" IS '是否已选';
COMMENT ON COLUMN "public"."t_order_by_ap"."ap_id" IS '账期id';
COMMENT ON COLUMN "public"."t_order_by_ap"."refund_date" IS '还款时间';
COMMENT ON COLUMN "public"."t_order_by_ap"."account" IS '分销账号';
COMMENT ON COLUMN "public"."t_order_by_ap"."status" IS '订单状态';
COMMENT ON COLUMN "public"."t_order_by_ap"."order_no" IS '订单号';
COMMENT ON COLUMN "public"."t_order_by_ap"."order_amount" IS '订单金额';

ALTER TABLE "public"."t_order_by_ap" ADD PRIMARY KEY ("id");


ALTER TABLE "public"."t_account_period_slave"
ADD COLUMN "has_prev" bool DEFAULT false;

COMMENT ON COLUMN "public"."t_account_period_slave"."has_prev" IS '是否有上一期';

