ALTER TABLE "t_dis_member" ADD COLUMN "create_user" varchar(50),ADD COLUMN "if_add_permision" bool default false;

COMMENT ON COLUMN "t_dis_member"."create_user" IS '创建人';

COMMENT ON COLUMN "t_dis_member"."if_add_permision" IS '是否附加权限';

ALTER TABLE "t_dis_role" ADD COLUMN "create_user" varchar(50);

COMMENT ON COLUMN "t_dis_role"."create_user" IS '创建人';

ALTER TABLE "t_dis_role_menu_mapper" ADD COLUMN "if_html_display" bool default false;

COMMENT ON COLUMN "t_dis_role_menu_mapper"."if_html_display" IS 'H5页面是否显示';

CREATE TABLE "t_dis_member_menu" (
	"id" SERIAL,
	"member_id" int4,
	"menu_id" int4,
	"if_html_display" bool,
	PRIMARY KEY ("id")
);

COMMENT ON COLUMN "t_dis_member_menu"."id" IS '主键ID';

COMMENT ON COLUMN "t_dis_member_menu"."member_id" IS '用户ID';

COMMENT ON COLUMN "t_dis_member_menu"."menu_id" IS '栏目ID';

COMMENT ON COLUMN "t_dis_member_menu"."if_html_display" IS 'H5页面是否显示';

alter table t_dis_member add column "is_delete" bool default true;
