-- 创建时间默认值
ALTER TABLE "public"."t_bbc_attribute" ALTER COLUMN "create_time" SET DEFAULT now();
-- 去除 bbc属性 属性值字段
ALTER TABLE "public"."t_bbc_attribute" DROP COLUMN "attr_value";
