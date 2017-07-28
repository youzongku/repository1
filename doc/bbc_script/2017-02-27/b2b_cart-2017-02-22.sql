ALTER TABLE "public"."t_dis_cart_detail"
ADD COLUMN "inter_bar_code" varchar(255);
COMMENT ON COLUMN "public"."t_dis_cart_detail"."inter_bar_code" IS '国际条码';