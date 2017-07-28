ALTER TABLE "public"."t_purchase_order_detail"
ADD COLUMN "cap_fee" float8;

COMMENT ON COLUMN "public"."t_purchase_order_detail"."cap_fee" IS '均摊价格(实际支付价格均摊到每个正价商品上）';