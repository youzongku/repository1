CREATE TABLE "public"."t_payment_method" (
"id" serial4 NOT NULL,
"name" varchar(100),
"key" varchar(100),
"create_time" timestamp(6) DEFAULT now(),
"update_time" timestamp(6) DEFAULT now(),
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_payment_method" IS '系统支付方式';

COMMENT ON COLUMN "public"."t_payment_method"."id" IS '主键';

COMMENT ON COLUMN "public"."t_payment_method"."name" IS '支付描述';

COMMENT ON COLUMN "public"."t_payment_method"."key" IS '支付类型code（余额支付：balance、支付宝支付：zhifubao、微信支付：weixin、易极付：easy、易极付-微信扫码：easy-wx、现金支付：cash、线下转账：cash-online）';

COMMENT ON COLUMN "public"."t_payment_method"."create_time" IS '创建时间';

COMMENT ON COLUMN "public"."t_payment_method"."update_time" IS '更新时间';


CREATE TABLE "public"."t_payment_condition" (
"id" serial4 NOT NULL,
"model" int4,
"dis_type" int4,
"backstage" bool,
"purpose" int4,
"create_time" timestamp(6) DEFAULT now(),
"update_time" timestamp(6) DEFAULT now(),
"create_user" varchar(50),
"foreground" bool,
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_payment_condition" IS '支付方式条件表';

COMMENT ON COLUMN "public"."t_payment_condition"."id" IS '主键';

COMMENT ON COLUMN "public"."t_payment_condition"."model" IS '分销商模式';

COMMENT ON COLUMN "public"."t_payment_condition"."dis_type" IS '分销商类型';

COMMENT ON COLUMN "public"."t_payment_condition"."backstage" IS '是否后台展示';

COMMENT ON COLUMN "public"."t_payment_condition"."purpose" IS '用途（1：充值、2：采购、3：销售）';

COMMENT ON COLUMN "public"."t_payment_condition"."create_time" IS '创建时间';

COMMENT ON COLUMN "public"."t_payment_condition"."update_time" IS '更新时间';

COMMENT ON COLUMN "public"."t_payment_condition"."create_user" IS '创建人';

COMMENT ON COLUMN "public"."t_payment_condition"."foreground" IS '是否前台展示';


CREATE TABLE "public"."t_payment_mapper" (
"id" serial4 NOT NULL,
"method_id" int4,
"condition_id" int4,
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_payment_mapper" IS '条件与支付方式映射表';

COMMENT ON COLUMN "public"."t_payment_mapper"."method_id" IS '支付方式id';

COMMENT ON COLUMN "public"."t_payment_mapper"."condition_id" IS 't_payment_condition表主键id，1对多（method_id）';


INSERT INTO "public"."t_payment_method" ( "name", "key") VALUES ('余额支付', 'balance');
INSERT INTO "public"."t_payment_method" ( "name", "key") VALUES ('支付宝支付', 'zhifubao');
INSERT INTO "public"."t_payment_method" ( "name", "key") VALUES ('易极付', 'easy');
INSERT INTO "public"."t_payment_method" ( "name", "key") VALUES ('易极付-微信扫码', 'easy-wx');
INSERT INTO "public"."t_payment_method" ( "name", "key") VALUES ('现金支付', 'cash');
INSERT INTO "public"."t_payment_method" ( "name", "key") VALUES ('线下转账', 'cash-online');
INSERT INTO "public"."t_payment_method" ( "name", "key") VALUES ('微信支付','weixin');


ALTER TABLE "public"."t_dis_bill"
ADD COLUMN "trade_no" varchar(255);

COMMENT ON COLUMN "public"."t_dis_bill"."trade_no" IS '交易流水号';



ALTER TABLE "public"."t_dis_operate_apply"
ADD COLUMN "apply_man" varchar(100);

COMMENT ON COLUMN "public"."t_dis_operate_apply"."apply_man" IS '申请人';





