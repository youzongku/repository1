ALTER TABLE "public"."t_shop_site"
ADD COLUMN "is_usable" bool DEFAULT true;

COMMENT ON COLUMN "public"."t_shop_site"."is_usable" IS 'ÊÇ·ñ½ûÓÃ';

