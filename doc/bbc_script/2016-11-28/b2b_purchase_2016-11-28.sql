CREATE TABLE "public"."t_dis_quotation" (
"id" serial4 NOT NULL,
"discount_rate" varchar(10) COLLATE "default",
"excel_name" varchar(100) COLLATE "default",
"excel_info" text COLLATE "default",
"dis_email" varchar(50) COLLATE "default",
"create_date" timestamp(6),
"made_user" varchar(50) COLLATE "default",
"is_build_order" bool DEFAULT false,
"update_date" timestamp(6),
"disname" varchar(50) COLLATE "default",
"req_body" text COLLATE "default",
"bind_dis_email" timestamp(6),
CONSTRAINT "t_dis_quotation_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_dis_quotation" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_dis_quotation" IS '分销商报价单记录表';

COMMENT ON COLUMN "public"."t_dis_quotation"."id" IS '主键';

COMMENT ON COLUMN "public"."t_dis_quotation"."discount_rate" IS '利润率折扣';

COMMENT ON COLUMN "public"."t_dis_quotation"."excel_name" IS '导出Excel名称';

COMMENT ON COLUMN "public"."t_dis_quotation"."excel_info" IS 'Excel内容(JSON串)';

COMMENT ON COLUMN "public"."t_dis_quotation"."dis_email" IS '分销商ID';

COMMENT ON COLUMN "public"."t_dis_quotation"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_dis_quotation"."made_user" IS '制作人';

COMMENT ON COLUMN "public"."t_dis_quotation"."is_build_order" IS '是否生成订单';

COMMENT ON COLUMN "public"."t_dis_quotation"."update_date" IS '更新时间';

COMMENT ON COLUMN "public"."t_dis_quotation"."disname" IS '分销商名称(真实姓名)';

COMMENT ON COLUMN "public"."t_dis_quotation"."req_body" IS '请求参数';

COMMENT ON COLUMN "public"."t_dis_quotation"."bind_dis_email" IS '更新绑定分销商时间';