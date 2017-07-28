CREATE TABLE "t_sh_order" (

"id" serial4,

"sh_order_no" varchar(40),

"xs_order_no" varchar(40),

"status" int4,

"email" varchar(50),

"dis_mode" int4,

"dis_name" varchar(50),

"create_time" timestamp(6) DEFAULT now(),

"update_time" timestamp(6) DEFAULT now(),

"business_erp" varchar(50),

"order_amount" float8,

"demand_amount" float8,

"actual_amount" float8,

"qa_desc" varchar(1000),

"demand_qty" int4,

"warehouse_id" int4,

"warehouse_name" varchar(50),

"product_img" varchar(255),

"product_name" varchar(500),

"sku" varchar(200),

"is_product_return" int4,

"company" varchar(50),

"express_code" varchar(200),

"finance_confirm_time" timestamp(6) DEFAULT now(),

"send_product_time" timestamp(6) DEFAULT now(),

"received_product_time" timestamp(6) DEFAULT now(),

"detail_order_id" int4

);



COMMENT ON TABLE "t_sh_order" IS '售后单';

COMMENT ON COLUMN "t_sh_order"."id" IS '主键';

COMMENT ON COLUMN "t_sh_order"."sh_order_no" IS '售后单号';

COMMENT ON COLUMN "t_sh_order"."xs_order_no" IS '销售单号';

COMMENT ON COLUMN "t_sh_order"."status" IS '售后单状态';

COMMENT ON COLUMN "t_sh_order"."email" IS '分销商账号';

COMMENT ON COLUMN "t_sh_order"."dis_mode" IS '分销商模式';

COMMENT ON COLUMN "t_sh_order"."dis_name" IS '分销商名称';

COMMENT ON COLUMN "t_sh_order"."create_time" IS '创建时间';

COMMENT ON COLUMN "t_sh_order"."update_time" IS '更新时间';

COMMENT ON COLUMN "t_sh_order"."business_erp" IS '业务员erp账号';

COMMENT ON COLUMN "t_sh_order"."order_amount" IS '退货单总金额';

COMMENT ON COLUMN "t_sh_order"."demand_amount" IS '客户要求退款金额';

COMMENT ON COLUMN "t_sh_order"."actual_amount" IS '实际退款金额';

COMMENT ON COLUMN "t_sh_order"."qa_desc" IS '问题描述';

COMMENT ON COLUMN "t_sh_order"."demand_qty" IS '要求退货数量';

COMMENT ON COLUMN "t_sh_order"."warehouse_id" IS '仓库id';

COMMENT ON COLUMN "t_sh_order"."warehouse_name" IS '仓库名称';

COMMENT ON COLUMN "t_sh_order"."product_img" IS '商品图片地址';

COMMENT ON COLUMN "t_sh_order"."product_name" IS '商品名称';

COMMENT ON COLUMN "t_sh_order"."sku" IS 'sku';

COMMENT ON COLUMN "t_sh_order"."is_product_return" IS '是否需要寄回商品';

COMMENT ON COLUMN "t_sh_order"."company" IS '快递公司';

COMMENT ON COLUMN "t_sh_order"."express_code" IS '快递单号';

COMMENT ON COLUMN "t_sh_order"."finance_confirm_time" IS '财务确认通过时间';

COMMENT ON COLUMN "t_sh_order"."send_product_time" IS '寄回商品时间';

COMMENT ON COLUMN "t_sh_order"."received_product_time" IS '确认收货时间';

COMMENT ON COLUMN "t_sh_order"."detail_order_id" IS '确定销售单号下的sku';



CREATE TABLE "t_sh_order_detail" (

"id" serial4,

"sh_order_id" int4,

"sh_order_no" varchar(50),

"purchase_order_no" varchar(50),

"sku" varchar(50),

"qty" int4,

"warehouse_id" int4,

"warehouse_name" varchar(50),

"product_img" varchar(255),

"product_name" varchar(500),

"purchase_price" float8,

"capfee" float8,

"arrive_ware_price" float8,

"create_time" timestamp(6) DEFAULT now(),

"update_time" timestamp(6) DEFAULT now(),

"expiration_date" timestamp(6),

"inter_bar_code" varchar(255)

);



COMMENT ON TABLE "t_sh_order_detail" IS '售后单详情';

COMMENT ON COLUMN "t_sh_order_detail"."id" IS '主键';

COMMENT ON COLUMN "t_sh_order_detail"."sh_order_id" IS '售后单id';

COMMENT ON COLUMN "t_sh_order_detail"."sh_order_no" IS '商品对应的发货单号';

COMMENT ON COLUMN "t_sh_order_detail"."purchase_order_no" IS '商品对应的采购单号';

COMMENT ON COLUMN "t_sh_order_detail"."sku" IS 'SKU';

COMMENT ON COLUMN "t_sh_order_detail"."qty" IS '数量';

COMMENT ON COLUMN "t_sh_order_detail"."warehouse_id" IS '仓库id';

COMMENT ON COLUMN "t_sh_order_detail"."warehouse_name" IS '仓库名称';

COMMENT ON COLUMN "t_sh_order_detail"."product_img" IS '商品图片地址';

COMMENT ON COLUMN "t_sh_order_detail"."product_name" IS '商品名称';

COMMENT ON COLUMN "t_sh_order_detail"."purchase_price" IS '采购价';

COMMENT ON COLUMN "t_sh_order_detail"."capfee" IS '均摊价';

COMMENT ON COLUMN "t_sh_order_detail"."arrive_ware_price" IS '到仓价';

COMMENT ON COLUMN "t_sh_order_detail"."create_time" IS '创建时间';

COMMENT ON COLUMN "t_sh_order_detail"."update_time" IS '更新时间';

COMMENT ON COLUMN "t_sh_order_detail"."expiration_date" IS '到期时间';

COMMENT ON COLUMN "t_sh_order_detail"."inter_bar_code" IS '国际条码';




CREATE TABLE "t_sh_attachment" (

"id" serial4,

"sh_order_id" int4,

"attachment_name" varchar(255),

"attachment_path" varchar(1000),

"create_time" timestamp(6) DEFAULT now()

);



COMMENT ON TABLE "t_sh_attachment" IS '售后单附件表';

COMMENT ON COLUMN "t_sh_attachment"."id" IS '主键';

COMMENT ON COLUMN "t_sh_attachment"."sh_order_id" IS '售后单ID';

COMMENT ON COLUMN "t_sh_attachment"."attachment_name" IS '附件名称';

COMMENT ON COLUMN "t_sh_attachment"."attachment_path" IS '附件路径';

COMMENT ON COLUMN "t_sh_attachment"."create_time" IS '创建时间';



CREATE TABLE "t_sh_log" (

"id" serial4,

"sh_order_id" int4,

"type" int4,

"is_product_return" int4,

"remark" varchar(1000),

"create_time" timestamp(6) DEFAULT now(),

"operator" varchar(50),

"result" int4

);



COMMENT ON TABLE "t_sh_log" IS '售后单日志表';

COMMENT ON COLUMN "t_sh_log"."id" IS '主键';

COMMENT ON COLUMN "t_sh_log"."sh_order_id" IS '售后单ID';

COMMENT ON COLUMN "t_sh_log"."type" IS '日志类型';

COMMENT ON COLUMN "t_sh_log"."is_product_return" IS '是否需要寄回商品';

COMMENT ON COLUMN "t_sh_log"."remark" IS '备注';

COMMENT ON COLUMN "t_sh_log"."create_time" IS '创建时间';

COMMENT ON COLUMN "t_sh_log"."operator" IS '操作人';

COMMENT ON COLUMN "t_sh_log"."result" IS '确认结果';



