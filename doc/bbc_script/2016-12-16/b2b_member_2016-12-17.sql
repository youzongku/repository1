
CREATE TABLE "public"."t_vip_invite_code" (
"id" serial4 NOT NULL,
"invite_code" varchar(255) COLLATE "default",
"count" int4 DEFAULT 0,
"in_use" bool DEFAULT false,
"create_date" timestamp(6) DEFAULT now(),
"update_date" timestamp(6),
CONSTRAINT "t_vip_invite_code_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_vip_invite_code" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_vip_invite_code" IS 'vip邀请码表';

COMMENT ON COLUMN "public"."t_vip_invite_code"."id" IS '主键';

COMMENT ON COLUMN "public"."t_vip_invite_code"."invite_code" IS 'vip邀请码';

COMMENT ON COLUMN "public"."t_vip_invite_code"."count" IS '使用人数(预留字段,目前 一个邀请码只能被使用一次)';

COMMENT ON COLUMN "public"."t_vip_invite_code"."in_use" IS '是否被使用';

COMMENT ON COLUMN "public"."t_vip_invite_code"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_vip_invite_code"."update_date" IS '创建时间';