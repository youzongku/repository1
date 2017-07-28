ALTER TABLE "public"."t_dis_member"
ADD COLUMN "attribution_type" int4;

COMMENT ON COLUMN "public"."t_dis_member"."attribution_type" IS '用户归属类型(1:线上，2:为线下)';

-- 电商模式 和 vip 模式 默认为线上
UPDATE t_dis_member set attribution_type = 1 where (distribution_mode = 1 or distribution_mode = 5 ) and role_id = 2;

UPDATE t_dis_member set attribution_type = 2 where (distribution_mode != 1 and distribution_mode != 5 )  and role_id = 2;