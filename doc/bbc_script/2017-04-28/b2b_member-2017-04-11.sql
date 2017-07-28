CREATE TABLE "public"."t_dis_shop_dp_logs" (
	"id" serial4,
	"shop_id" int4,
	"shop_name" varchar(200) COLLATE "default",
	"email" varchar(50) COLLATE "default",
	"deduction_points" float8,
	"create_user" varchar(50) COLLATE "default",
	"create_time" timestamp(6)
);

COMMENT ON TABLE "public"."t_dis_shop_dp_logs" IS '设置分销商店铺店铺扣点日志';
COMMENT ON COLUMN "public"."t_dis_shop_dp_logs"."shop_id" IS '店铺ID';
COMMENT ON COLUMN "public"."t_dis_shop_dp_logs"."shop_name" IS '店铺名称';
COMMENT ON COLUMN "public"."t_dis_shop_dp_logs"."shop_name" IS '分销商';
COMMENT ON COLUMN "public"."t_dis_shop_dp_logs"."deduction_points" IS '店铺扣点率';
COMMENT ON COLUMN "public"."t_dis_shop_dp_logs"."create_user" IS '操作人';
COMMENT ON COLUMN "public"."t_dis_shop_dp_logs"."create_time" IS '设置时间';