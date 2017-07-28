alter table t_banner_info

ADD COLUMN "type" int2 DEFAULT 0;

COMMENT ON COLUMN "public"."t_banner_info"."type" IS '广告类型 0：banner 1：浮窗图  2：楼层广告图 3：通栏广告图';


alter table t_banner_info

ADD COLUMN "bg_color" varchar(20) default '#E4EDF6';

COMMENT ON COLUMN "public"."t_banner_info"."bg_color" IS '背景色';


alter table t_banner_info

ADD COLUMN "category_id" int4 default 0;

COMMENT ON COLUMN "public"."t_banner_info"."category_id" IS '关联的类目id';


alter table t_banner_info

ADD COLUMN "parent_id" int4 default 0;

COMMENT ON COLUMN "public"."t_banner_info"."parent_id" IS '父类目id';