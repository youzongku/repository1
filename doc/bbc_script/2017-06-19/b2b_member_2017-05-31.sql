ALTER TABLE "public"."t_dis_account"
ADD COLUMN "period_frozen" float8 DEFAULT 0;

COMMENT ON COLUMN "public"."t_dis_account"."period_frozen" IS '账期冻结金额：当账期剩余到达该金额时冻结账户';

