CREATE TABLE "public"."t_return_order" (
"id" serial4 NOT NULL,
"return_order_no" varchar(255) COLLATE "default",
"email" varchar(255) COLLATE "default",
"nick_name" varchar(255) COLLATE "default",
"salesman" varchar(255) COLLATE "default",
"total_return_amount" float8,
"user_expect_total_return_amount" float8,
"actual_total_return_amount" float8,
"status" int4,
"application_time" timestamp,
"remarks" varchar(500) COLLATE "default",
"audit_remarks" varchar(500) COLLATE "default",
"create_time" timestamp,
"create_user" varchar(255) COLLATE "default",
"last_update_time" timestamp,
"last_update_user" varchar(255) COLLATE "default",
PRIMARY KEY ("id")
);

ALTER TABLE "public"."t_return_order" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_return_order" IS '采购退货单';
COMMENT ON COLUMN "public"."t_return_order"."return_order_no" IS '退货单号';
COMMENT ON COLUMN "public"."t_return_order"."total_return_amount" IS '退款总金额，根据详情计算出来的';
COMMENT ON COLUMN "public"."t_return_order"."user_expect_total_return_amount" IS '用户填的退款金额';
COMMENT ON COLUMN "public"."t_return_order"."actual_total_return_amount" IS '实际退款总金额，审核时可以修改total_return_amount';
COMMENT ON COLUMN "public"."t_return_order"."status" IS '状态：待审核1；审核通过2；审核未通过3；取消4；完成5';
COMMENT ON COLUMN "public"."t_return_order"."application_time" IS '申请时间';
COMMENT ON COLUMN "public"."t_return_order"."remarks" IS '备注';
COMMENT ON COLUMN "public"."t_return_order"."audit_remarks" IS '审核备注';


CREATE TABLE "public"."t_return_order_detail" (
"id" serial4 NOT NULL,
"return_order_id" int4,
"return_order_no" varchar(255) COLLATE "default",
"purchase_order_no" varchar(255) COLLATE "default",
"product_title" varchar(255) COLLATE "default",
"img_url" varchar(255) COLLATE "default",
"sku" varchar(50) COLLATE "default",
"warehouse_id" int4,
"warehouse_name" varchar(255) COLLATE "default",
"purchase_price" float8,
"capfee" float8,
"purchase_time" varchar(255) COLLATE "default",
"expiration_date" varchar(255) COLLATE "default",
"return_qty" int4,
"qty" int4,
"residue_num" int4,
"sub_total_return_amount" float8,
"in_record_id" int4,
"day_space" int4,
"coefficient" float8,
PRIMARY KEY ("id")
);

ALTER TABLE "public"."t_return_order_detail" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_return_order_detail" IS '采购退货单明细';
COMMENT ON COLUMN "public"."t_return_order_detail"."return_order_id" IS '退货单id';
COMMENT ON COLUMN "public"."t_return_order_detail"."return_order_no" IS '退货单号';
COMMENT ON COLUMN "public"."t_return_order_detail"."purchase_order_no" IS '所属采购单';
COMMENT ON COLUMN "public"."t_return_order_detail"."purchase_price" IS '采购价';
COMMENT ON COLUMN "public"."t_return_order_detail"."capfee" IS '均摊价';
COMMENT ON COLUMN "public"."t_return_order_detail"."purchase_time" IS '采购时间';
COMMENT ON COLUMN "public"."t_return_order_detail"."expiration_date" IS '过期时间';
COMMENT ON COLUMN "public"."t_return_order_detail"."qty" IS '采购数量';
COMMENT ON COLUMN "public"."t_return_order_detail"."residue_num" IS '申请退货时，所剩余的数量';
COMMENT ON COLUMN "public"."t_return_order_detail"."return_qty" IS '退货数量';
COMMENT ON COLUMN "public"."t_return_order_detail"."sub_total_return_amount" IS '退款小计，capfee乘以return_qty';
COMMENT ON COLUMN "public"."t_return_order_detail"."day_space" IS '距离到期日期间隔天数';
COMMENT ON COLUMN "public"."t_return_order_detail"."coefficient" IS '退款系数';

CREATE TABLE "public"."t_return_order_logs" (
"id" serial4 NOT NULL,
"return_order_no" varchar(255) COLLATE "default",
"status" int4,
"create_time" timestamp,
"create_user" varchar(255) COLLATE "default",
PRIMARY KEY ("id")
);

ALTER TABLE "public"."t_return_order_logs" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_return_order_logs" IS '采购退货单';
COMMENT ON COLUMN "public"."t_return_order_logs"."return_order_no" IS '退货单号';
COMMENT ON COLUMN "public"."t_return_order_logs"."status" IS '状态';


CREATE TABLE "public"."t_return_amount_coefficient" (
"id" serial4 NOT NULL,
"sku" varchar(50) COLLATE "default",
"warehouse_id" int4,
"coefficient_value" varchar(255) COLLATE "default",
"create_time" timestamp,
"create_user" varchar(255) COLLATE "default",
"last_update_time" timestamp,
"last_update_user" varchar(255) COLLATE "default",
PRIMARY KEY ("id")
);
ALTER TABLE "public"."t_return_amount_coefficient" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_return_amount_coefficient" IS '商品退款比例';
COMMENT ON COLUMN "public"."t_return_amount_coefficient"."coefficient_value" IS '商品退款系数（按距到期日期天数设置）';

CREATE TABLE "public"."t_return_amount_coefficient_logs" (
"id" serial4 NOT NULL,
"coefficient_id" int4,
"sku" varchar(50) COLLATE "default",
"warehouse_id" int4,
"log_value" varchar(255) COLLATE "default",
"create_time" timestamp,
"create_user" varchar(255) COLLATE "default",
PRIMARY KEY ("id")
);
ALTER TABLE "public"."t_return_amount_coefficient_logs" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_return_amount_coefficient_logs" IS '商品退款比例日志';
COMMENT ON COLUMN "public"."t_return_amount_coefficient_logs"."log_value" IS '具体的值';


INSERT INTO "public"."t_sequence" VALUES ('3', 'RETURN_ORDER_NO', '0', '1', '退货单号');
