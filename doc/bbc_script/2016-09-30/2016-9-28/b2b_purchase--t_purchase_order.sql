

ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "offline_money" float8,
ADD COLUMN "has_associated_orders" bool DEFAULT false,
ADD COLUMN "finance_money" float8,
ADD COLUMN "remark" varchar(255),
ADD COLUMN "reason" varchar(255);

COMMENT ON COLUMN "public"."t_purchase_order"."finance_money" IS '财务实收金额';

COMMENT ON COLUMN "public"."t_purchase_order"."remark" IS '财务备注';

COMMENT ON COLUMN "public"."t_purchase_order"."reason" IS '审核理由';

COMMENT ON COLUMN "public"."t_purchase_order"."status" IS '采购单状态：0待付款，1已付款，2已取消，3已失效，4待审核，5审核不通过';

COMMENT ON COLUMN "public"."t_purchase_order"."offline_money" IS '线下支付金额';

COMMENT ON COLUMN "public"."t_purchase_order"."has_associated_orders" IS '是否有无关联订单';



