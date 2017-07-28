CREATE TABLE "t_purchase_order_associated" (
"id" serial4 NOT NULL,
"mo_id" int4,
"so_id" int4,
PRIMARY KEY ("id") 
);

COMMENT ON TABLE "t_purchase_order_associated" IS '订单关联表';
COMMENT ON COLUMN "t_purchase_order_associated"."mo_id" IS '主订单id';
COMMENT ON COLUMN "t_purchase_order_associated"."so_id" IS '从订单id';