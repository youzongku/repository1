ALTER TABLE "t_dis_operate_apply"
ADD COLUMN "counter_fee" numeric(10,2);

COMMENT ON COLUMN "t_dis_operate_apply"."counter_fee" IS '手续费';


ALTER TABLE t_dis_withdraw_account ADD COLUMN if_effective bool default true;

COMMENT ON COLUMN "t_dis_withdraw_account"."if_effective" IS '是否有效';