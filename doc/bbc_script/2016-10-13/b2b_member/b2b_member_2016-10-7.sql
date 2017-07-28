ALTER TABLE "t_dis_account"
ADD COLUMN "input_error_num_times" int4 DEFAULT 0,
ADD COLUMN "disable_time" timestamp;

COMMENT ON COLUMN "public"."t_dis_account"."input_error_num_times" IS '支付密码输入错误次数';

COMMENT ON COLUMN "public"."t_dis_account"."disable_time" IS '禁用时间';