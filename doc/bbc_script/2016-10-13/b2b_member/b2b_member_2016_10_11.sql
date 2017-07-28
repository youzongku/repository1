ALTER TABLE "public"."t_dis_member"
ADD COLUMN "is_back_register" bool DEFAULT false,
ADD COLUMN "register_man" varchar(50),
ADD COLUMN "salesman_erp" varchar(255);

COMMENT ON COLUMN "public"."t_dis_member"."is_back_register" IS '是否为后台注册';

COMMENT ON COLUMN "public"."t_dis_member"."register_man" IS '注册人';

COMMENT ON COLUMN "public"."t_dis_member"."salesman_erp" IS '业务人员erp账号（由后台人员注册选择业务人员时，此业务员的erp账号）';
     
