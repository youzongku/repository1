alter table "public"."t_dis_operate_apply" add column "withdraw_account_id" int4;
comment on column "public"."t_dis_operate_apply"."withdraw_account_id" is '提现账号ID';

alter table "public"."t_dis_operate_apply" add column "withdraw_amount" numeric(10,2);
comment on column "public"."t_dis_operate_apply"."withdraw_amount" is '提现金额';
