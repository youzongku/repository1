CREATE TABLE "public"."t_marketing_order" (
"id" serial4 NOT NULL,
"marketing_order_no" varchar(255) NOT NULL,
"total_amount" float8,
"sales_order_no" varchar(50),
"status" int4,
"email" varchar(50),
"nick_name" varchar(50),
"dis_mode" int4,
"distributor_type" int4,
"salesman" varchar(100),
"province_id" int4,
"city_id" int4,
"region_id" int4,
"province_name" varchar(50),
"city_name" varchar(50),
"region_name" varchar(100),
"address_detail" varchar(500),
"receiver" varchar(255),
"receiver_tel" varchar(255),
"receiver_postcode" varchar(50),
"logistics_mode" varchar(255) COLLATE "default",
"logistics_type_code" varchar(255) COLLATE "default",
"bbc_postage" float8,
"orderer" varchar(255),
"orderer_tel" varchar(255),
"orderer_postcode" varchar(50),
"business_remark" varchar(500),
"create_user" varchar(100),
"create_date" timestamp(6),
"last_update_user" varchar(100),
"last_update_date" timestamp(6),
PRIMARY KEY ("id")
);

ALTER TABLE "public"."t_marketing_order" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_marketing_order" IS '营销单';
COMMENT ON COLUMN "public"."t_marketing_order"."marketing_order_no" IS '营销单号';
COMMENT ON COLUMN "public"."t_marketing_order"."total_amount" IS '营销单商品金额，不包含运费';
COMMENT ON COLUMN "public"."t_marketing_order"."sales_order_no" IS '销售单号';
COMMENT ON COLUMN "public"."t_marketing_order"."status" IS '状态';
COMMENT ON COLUMN "public"."t_marketing_order"."email" IS '分销商';
COMMENT ON COLUMN "public"."t_marketing_order"."nick_name" IS '分销商昵称';
COMMENT ON COLUMN "public"."t_marketing_order"."distributor_type" IS '分销商类型（1：普通 2：合营 3：内部）';
COMMENT ON COLUMN "public"."t_marketing_order"."dis_mode" IS '分销模式:1、电商，2、经销商，3、KA直营，4、进口专营';
COMMENT ON COLUMN "public"."t_marketing_order"."salesman" IS '业务员';
COMMENT ON COLUMN "public"."t_marketing_order"."province_id" IS '省id';
COMMENT ON COLUMN "public"."t_marketing_order"."province_name" IS '省份';
COMMENT ON COLUMN "public"."t_marketing_order"."city_id" IS '市id';
COMMENT ON COLUMN "public"."t_marketing_order"."city_name" IS '市名称';
COMMENT ON COLUMN "public"."t_marketing_order"."region_id" IS '区id';
COMMENT ON COLUMN "public"."t_marketing_order"."region_name" IS '区名称';
COMMENT ON COLUMN "public"."t_marketing_order"."address_detail" IS '详细地址';
COMMENT ON COLUMN "public"."t_marketing_order"."receiver" IS '收货人';
COMMENT ON COLUMN "public"."t_marketing_order"."receiver_tel" IS '收货人电话';
COMMENT ON COLUMN "public"."t_marketing_order"."receiver_postcode" IS '收货人邮编';
COMMENT ON COLUMN "public"."t_marketing_order"."logistics_mode" IS '物流方式';
COMMENT ON COLUMN "public"."t_marketing_order"."logistics_type_code" IS '物流方式代码';
COMMENT ON COLUMN "public"."t_marketing_order"."bbc_postage" IS '运费';
COMMENT ON COLUMN "public"."t_marketing_order"."orderer" IS '下单人';
COMMENT ON COLUMN "public"."t_marketing_order"."orderer_tel" IS '下单人电话';
COMMENT ON COLUMN "public"."t_marketing_order"."orderer_postcode" IS '下单人邮编';
COMMENT ON COLUMN "public"."t_marketing_order"."business_remark" IS '业务备注';
COMMENT ON COLUMN "public"."t_marketing_order"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."t_marketing_order"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."t_marketing_order"."last_update_user" IS '最后修改人';
COMMENT ON COLUMN "public"."t_marketing_order"."last_update_date" IS '最后修改时间';

CREATE TABLE "public"."t_marketing_order_details" (
"id" serial4 NOT NULL,
"marketing_order_id" int4 NOT NULL,
"marketing_order_no" varchar(255) NOT NULL,
"product_name" varchar(255),
"product_img" varchar(255),
"sku" varchar(255),
"qty" int4,
"dis_price" float8,
"warehouse_id" int4,
"warehouse_name" varchar(255),
PRIMARY KEY ("id")
);

ALTER TABLE "public"."t_marketing_order_details" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_marketing_order_details" IS '营销单明细';
COMMENT ON COLUMN "public"."t_marketing_order_details"."marketing_order_id" IS '营销单id';
COMMENT ON COLUMN "public"."t_marketing_order_details"."marketing_order_no" IS '营销单号';
COMMENT ON COLUMN "public"."t_marketing_order_details"."product_name" IS '商品名称';
COMMENT ON COLUMN "public"."t_marketing_order_details"."product_img" IS '商品图片url';
COMMENT ON COLUMN "public"."t_marketing_order_details"."sku" IS 'sku';
COMMENT ON COLUMN "public"."t_marketing_order_details"."qty" IS '数量';
COMMENT ON COLUMN "public"."t_marketing_order_details"."dis_price" IS '价格';
COMMENT ON COLUMN "public"."t_marketing_order_details"."warehouse_id" IS '查看id';
COMMENT ON COLUMN "public"."t_marketing_order_details"."warehouse_name" IS '仓库名称';

CREATE TABLE "public"."t_marketing_order_audit_logs" (
"id" serial4 NOT NULL,
"status" int4,
"marketing_order_no" varchar(30),
"passed" int4,
"remarks" varchar(500),
"audit_user" varchar(50),
"audit_date" timestamp(6),
"audit_type" int4,
PRIMARY KEY ("id")
);

ALTER TABLE "public"."t_marketing_order_audit_logs" OWNER TO "tomtop";
COMMENT ON TABLE "public"."t_marketing_order_audit_logs" IS '营销单审核记录';
COMMENT ON COLUMN "public"."t_marketing_order_audit_logs"."status" IS '当次营销单审核状态';
COMMENT ON COLUMN "public"."t_marketing_order_audit_logs"."marketing_order_no" IS '营销单号';
COMMENT ON COLUMN "public"."t_marketing_order_audit_logs"."passed" IS '页面选择是否通过：0通过 1不通过';
COMMENT ON COLUMN "public"."t_marketing_order_audit_logs"."remarks" IS '审核填的备注';
COMMENT ON COLUMN "public"."t_marketing_order_audit_logs"."audit_user" IS '审核人';
COMMENT ON COLUMN "public"."t_marketing_order_audit_logs"."audit_date" IS '审核时间';
COMMENT ON COLUMN "public"."t_marketing_order_audit_logs"."audit_type" IS '审核类型：1初审；2复审';

ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "created_from" int4 default 30;
COMMENT ON COLUMN "public"."t_product_sales_order_main"."created_from" IS '发货单从哪里创建的：30正常（前台，后台，整批出库）；50营销单';