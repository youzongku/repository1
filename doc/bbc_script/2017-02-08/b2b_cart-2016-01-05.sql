ALTER TABLE "public"."t_dis_cart_detail"
ADD COLUMN "category_name" varchar(255);

COMMENT ON COLUMN "public"."t_dis_cart_detail"."category_name" IS '类目名称';
