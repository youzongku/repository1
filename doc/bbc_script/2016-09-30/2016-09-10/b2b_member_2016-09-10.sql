ALTER TABLE "public"."t_dis_menu"
ADD COLUMN "is_hfive" bool DEFAULT false;

COMMENT ON COLUMN "public"."t_dis_menu"."is_hfive" IS '是否展示在h5的菜单栏';
ALTER TABLE "public"."t_dis_menu"
ADD COLUMN "sort" int4;

COMMENT ON COLUMN "public"."t_dis_menu"."sort" IS '用于排序（排序规则为由大到小）';


ALTER TABLE "public"."t_dis_operate_apply"
ADD COLUMN "re_audit_remark" varchar(500),
ADD COLUMN "apply_remark" varchar(200);

COMMENT ON COLUMN "public"."t_dis_operate_apply"."re_audit_remark" IS '复审备注';

COMMENT ON COLUMN "public"."t_dis_operate_apply"."apply_remark" IS '申请备注';


