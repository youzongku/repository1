CREATE TABLE "t_kdniao_order" (

"id" serial4 NOT NULL,

"sales_order_no" varchar(100),

"logistic_code" varchar(100),

"print_template" text,

"create_date" timestamp(6) DEFAULT now(),

PRIMARY KEY ("id") 

);

COMMENT ON TABLE "public"."t_kdniao_order" IS '快递鸟电子面单表';
COMMENT ON COLUMN "public"."t_kdniao_order"."id" IS '主键';
COMMENT ON COLUMN "public"."t_kdniao_order"."sales_order_no" IS '订单号';
COMMENT ON COLUMN "public"."t_kdniao_order"."logistic_code" IS '快递单号';
COMMENT ON COLUMN "public"."t_kdniao_order"."print_template" IS '电子面单内容';
COMMENT ON COLUMN "public"."t_kdniao_order"."create_date" IS '创建时间';


CREATE TABLE "t_platform_config" (

"id" serial4 NOT NULL,

"platform_code" varchar(100),

"platform_key" varchar(100),

"platform_value" varchar(100),

"create_date" timestamp(6) DEFAULT now(),

PRIMARY KEY ("id") 

);

COMMENT ON TABLE "public"."t_platform_config" IS '平台配置表';
COMMENT ON COLUMN "public"."t_platform_config"."id" IS '主键';
COMMENT ON COLUMN "public"."t_platform_config"."platform_code" IS '平台编码';
COMMENT ON COLUMN "public"."t_platform_config"."platform_key" IS '平台key';
COMMENT ON COLUMN "public"."t_platform_config"."platform_value" IS '平台key对应的值';
COMMENT ON COLUMN "public"."t_platform_config"."create_date" IS '创建时间';


INSERT INTO "t_platform_config" VALUES (1, 'kdniao', 'EBusinessID', '1284128', now());
INSERT INTO "t_platform_config" VALUES (2, 'kdniao', 'appKey', 'c1dcacad-ed09-4a63-a234-6ed4d4c6a8b9', now());
INSERT INTO "t_platform_config" VALUES (3, 'kdniao', 'orderTracesSub', 'http://api.kdniao.cc/api/dist', now());
INSERT INTO "t_platform_config" VALUES (4, 'kdniao', 'orderOnline', 'http://api.kdniao.cc/api/Eorderservice', now());
INSERT INTO "t_platform_config" VALUES (5, 'kdniao', 'CustomerName', 'K755172653', now());
INSERT INTO "t_platform_config" VALUES (6, 'kdniao', 'MonthCode', 'WaDrp3X8', now());