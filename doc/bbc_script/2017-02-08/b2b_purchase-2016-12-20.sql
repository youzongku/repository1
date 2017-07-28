ALTER TABLE "public"."t_purchase_order"
ADD COLUMN "nick_name" varchar(50);

COMMENT ON COLUMN "public"."t_purchase_order"."nick_name" IS 'êÇ³Æ';

ALTER TABLE "public"."t_purchase_order_detail"
ADD COLUMN "category_name" varchar(255);

COMMENT ON COLUMN "public"."t_purchase_order_detail"."category_name" IS 'ÀàÄ¿Ãû³Æ';
