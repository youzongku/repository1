CREATE TABLE "public"."t_dis_register_apply" (
"id" serial4 NOT NULL,
"account" varchar(50),
"register_man" varchar(50),
"status" int4,
"register_date" timestamp(6),
"create_date" timestamp(6) DEFAULT now(),
"update_date" timestamp(6),
"audit_remark" varchar(500),
"audit_reason" varchar(150),
"is_back_register" bool DEFAULT false,
"pass_word" varchar(50),
"salesman_id" int4,
"apply_remark" varchar(500),
"audit_man" varchar(50),
"register_invite_code" varchar(50),
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_dis_register_apply" IS '注册申请表';

COMMENT ON COLUMN "public"."t_dis_register_apply"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_dis_register_apply"."account" IS '注册账号';

COMMENT ON COLUMN "public"."t_dis_register_apply"."register_man" IS '注册申请人';

COMMENT ON COLUMN "public"."t_dis_register_apply"."status" IS '审核状态:  0.待审核  1.审核不通过  2.审核通过 3.已失效';

COMMENT ON COLUMN "public"."t_dis_register_apply"."register_date" IS '注册时间';

COMMENT ON COLUMN "public"."t_dis_register_apply"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_dis_register_apply"."update_date" IS '更新时间';

COMMENT ON COLUMN "public"."t_dis_register_apply"."audit_remark" IS '申请备注';

COMMENT ON COLUMN "public"."t_dis_register_apply"."audit_reason" IS '审核理由';

COMMENT ON COLUMN "public"."t_dis_register_apply"."is_back_register" IS '是否为后台注册';

COMMENT ON COLUMN "public"."t_dis_register_apply"."pass_word" IS '密码 ';

COMMENT ON COLUMN "public"."t_dis_register_apply"."salesman_id" IS '业务员id';

COMMENT ON COLUMN "public"."t_dis_register_apply"."apply_remark" IS '申请备注';

COMMENT ON COLUMN "public"."t_dis_register_apply"."audit_man" IS '审核人';

COMMENT ON COLUMN "public"."t_dis_register_apply"."register_invite_code" IS '注册邀请码';

