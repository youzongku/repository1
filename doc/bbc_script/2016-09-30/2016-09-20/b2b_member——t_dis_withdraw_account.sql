DROP TABLE IF EXISTS "public"."t_dis_withdraw_account";
CREATE TABLE "public"."t_dis_withdraw_account" (
"id" SERIAL NOT NULL,
"withdraw_account" varchar(50) COLLATE "default",
"account_user" varchar(50) COLLATE "default",
"account_unit" varchar(100) COLLATE "default",
"distributor_email" varchar(50) COLLATE "default",
"create_time" TIMESTAMP(6) DEFAULT now(),
CONSTRAINT "t_dis_withdraw_account_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_dis_withdraw_account" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_dis_withdraw_account" IS '提现账户表(用于分销商提现时绑定银行或第三方支付账号)';

COMMENT ON COLUMN "public"."t_dis_withdraw_account"."id" IS '主键';

COMMENT ON COLUMN "public"."t_dis_withdraw_account"."withdraw_account" IS '提现账号';

COMMENT ON COLUMN "public"."t_dis_withdraw_account"."account_user" IS '账号所属人(银行卡持卡人或支付账号所有人)';

COMMENT ON COLUMN "public"."t_dis_withdraw_account"."account_unit" IS '账号所属单位(银行或第三方支付)';

COMMENT ON COLUMN "public"."t_dis_withdraw_account"."distributor_email" IS '分销商邮箱账号';

COMMENT ON COLUMN "public"."t_dis_withdraw_account"."create_time" IS '创建时间';

