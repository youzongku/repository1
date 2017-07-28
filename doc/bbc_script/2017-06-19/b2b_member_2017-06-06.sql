CREATE TABLE "public"."t_ap_order_details" (
"id" serial4,
"ap_order_id" int4,
"order_no" varchar(50) COLLATE "default",
"sku" varchar(50) COLLATE "default",
"product_name" varchar(200) COLLATE "default",
"qty" int4,
"warehouse_id" int4,
"warehouse_name" varchar(20) COLLATE "default",
"sales_order_no" varchar(50) COLLATE "default",
PRIMARY KEY ("id")
);

ALTER TABLE "public"."t_ap_order_details" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_ap_order_details" IS '账期支付的订单详情';

COMMENT ON COLUMN "public"."t_ap_order_details"."id" IS '主键';

COMMENT ON COLUMN "public"."t_ap_order_details"."ap_order_id" IS '订单id';

COMMENT ON COLUMN "public"."t_ap_order_details"."order_no" IS '订单号';

COMMENT ON COLUMN "public"."t_ap_order_details"."sales_order_no" IS '当order_no是合并单时，sales_order_no有值';