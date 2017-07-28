
-- 修改表
alter table t_product_sales_order_main

ADD COLUMN "is_package_mail" int2;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."is_package_mail" IS '是否包邮 1：包邮 2：不包邮';

CREATE TABLE "public"."t_sales_hb_delivery" (
"id" serial4 NOT NULL,
"sales_hb_no" varchar(100),
"total_amount_postage_inclusive" float8,
"total_bbc_postage" float8,
"original_total_bbc_postage" float8,
"account" varchar(50),
"warehouse_id" int4,
"warehouse_name" varchar(50),
"qties" int4,
"status" int4,
"consumer_type" int4,
"distribution_mode" int4,
"salesman" varchar(50),
"nick_name" varchar(50),
"receiver" varchar(50),
"telephone" varchar(50),
"address" varchar(300),
"logistics_information" varchar(50),
"create_time" timestamp(6),
"create_user" varchar(50),
"last_update_time" timestamp(6),
"last_update_user" varchar(50),
PRIMARY KEY ("id")
);
ALTER TABLE "public"."t_sales_hb_delivery" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_sales_hb_delivery" IS '费用项类型表';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."id" IS '主键id';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."sales_hb_no" IS '合并单号';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."total_amount_postage_inclusive" IS '发货单的订单金额总计，包含运费';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."total_bbc_postage" IS '合并后的总运费';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."original_total_bbc_postage" IS '合并前的总运费';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."account" IS '分销商';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."warehouse_id" IS '仓库id';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."warehouse_name" IS '仓库名称';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."qties" IS '合并数量';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."status" IS '状态';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."consumer_type" IS '分销商类型（1：普通分销商，2：合营分销商，3：内部分销商）';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."distribution_mode" IS '分销商 模式(1,电商 2，经销商 3 ,商超)';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."salesman" IS '业务员';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."nick_name" IS '名称';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."receiver" IS '收货人';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."telephone" IS '电话';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."address" IS '地址';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."logistics_information" IS '物流信息';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."create_time" IS '操作时间';
COMMENT ON COLUMN "public"."t_sales_hb_delivery"."create_user" IS '操作人';


CREATE TABLE "public"."t_sales_hb_delivery_details" (
"id" serial4 NOT NULL,
"sales_hb_id" int4,
"sales_hb_no" varchar(100),
"sales_order_no" varchar(100),
"shop_id" int4,
"sales_order_id" int4,
"purchase_order_no" varchar(100),
PRIMARY KEY ("id")
);
ALTER TABLE "public"."t_sales_hb_delivery_details" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_sales_hb_delivery_details" IS '费用项类型表';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_details"."id" IS '主键id';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_details"."sales_hb_id" IS '合并发货id';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_details"."sales_hb_no" IS '合并发货号';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_details"."sales_order_no" IS '销售单单号';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_details"."shop_id" IS '店铺id';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_details"."sales_order_id" IS '销售单id';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_details"."purchase_order_no" IS '缺货采购单单号';


CREATE TABLE "public"."t_sales_hb_delivery_logs" (
"id" serial4 NOT NULL,
"sales_hb_id" int4,
"sales_hb_no" varchar(100),
"status" int4,
"remarks" varchar(2000),
"opt_type" int4,
"opt_time" timestamp(6),
"opt_user" varchar(50),
PRIMARY KEY ("id")
);
ALTER TABLE "public"."t_sales_hb_delivery_logs" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_sales_hb_delivery_logs" IS '费用项类型表';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_logs"."id" IS '主键id';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_logs"."sales_hb_id" IS '合并发货id';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_logs"."sales_hb_no" IS '合并发货号';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_logs"."status" IS '状态';
COMMENT ON COLUMN "public"."t_sales_hb_delivery_logs"."opt_type" IS '操作类型：1合并发货单，2客服审核，3财务审核';
