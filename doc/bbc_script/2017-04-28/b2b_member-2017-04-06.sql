alter table t_dis_shop

ADD COLUMN "deduction_points" float8;

COMMENT ON COLUMN "public"."t_dis_shop"."deduction_points" IS '店铺扣点';