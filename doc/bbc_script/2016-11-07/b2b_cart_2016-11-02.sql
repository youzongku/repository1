ALTER TABLE "public"."t_cart_active"
ADD COLUMN "active_plvg" text;

COMMENT ON COLUMN "public"."t_cart_active"."active_plvg" IS '活动优惠json字符串';
