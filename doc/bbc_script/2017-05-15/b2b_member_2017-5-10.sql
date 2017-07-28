alter table t_dis_register_apply

ADD COLUMN "province_code" int4;

COMMENT ON COLUMN "public"."t_dis_register_apply"."province_code" IS '省编码';


alter table t_dis_register_apply

ADD COLUMN "city_code" int4;

COMMENT ON COLUMN "public"."t_dis_register_apply"."city_code" IS '市编码';


alter table t_dis_register_apply

ADD COLUMN "area_code" int4;

COMMENT ON COLUMN "public"."t_dis_register_apply"."area_code" IS '区编码';


alter table t_dis_register_apply

ADD COLUMN "distribution_mode" int4;

COMMENT ON COLUMN "public"."t_dis_register_apply"."distribution_mode" IS '分销渠道';



alter table t_dis_member

ADD COLUMN "user_code" varchar(50);

COMMENT ON COLUMN "public"."t_dis_member"."user_code" IS '客户编码';


--既然存电商和vip生成客户编码
update t_dis_member a set user_code = CONCAT('000000000804',trim(to_char(id,'00000'))) where a.distribution_mode = 1;

update t_dis_member a set user_code = CONCAT('111111111805',trim(to_char(id,'00000'))) where a.distribution_mode = 5;