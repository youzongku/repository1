
create sequence "public"."t_credit_mapper_id_seq" increment by 1 minvalue 1 no maxvalue start with 1;
DROP TABLE IF EXISTS "public"."t_credit_mapper";
CREATE TABLE "public"."t_credit_mapper" (
"id" int4 DEFAULT nextval('t_credit_mapper_id_seq'::regclass) NOT NULL,
"customer_mode" int4,
"customer_type" int4,
"has_long_credit" bool DEFAULT false,
"has_short_credit" bool DEFAULT false,
"create_user" varchar(55) COLLATE "default" DEFAULT 'admin'::character varying,
"create_date" date DEFAULT now(),
"update_date" date DEFAULT now()
)
WITH (OIDS=FALSE);

COMMENT ON TABLE "public"."t_credit_mapper" IS '分销商模式与类型所拥有的额度';
COMMENT ON COLUMN "public"."t_credit_mapper"."id" IS '主键';
COMMENT ON COLUMN "public"."t_credit_mapper"."customer_mode" IS '分销商模式Id';
COMMENT ON COLUMN "public"."t_credit_mapper"."customer_type" IS '分销商类型Id';
COMMENT ON COLUMN "public"."t_credit_mapper"."has_long_credit" IS '是否拥有永久额度';
COMMENT ON COLUMN "public"."t_credit_mapper"."has_short_credit" IS '是否拥有临时额度';
COMMENT ON COLUMN "public"."t_credit_mapper"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."t_credit_mapper"."create_date" IS '创建时间';
COMMENT ON COLUMN "public"."t_credit_mapper"."update_date" IS '更新时间';


INSERT INTO "public"."t_credit_mapper" VALUES ('1', '1', '1', 'f', 'f', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('2', '2', '1', 't', 't', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('3', '3', '1', 'f', 'f', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('4', '4', '1', 'f', 'f', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('5', '1', '2', 't', 't', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('6', '2', '2', 't', 't', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('7', '3', '2', 'f', 'f', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('8', '4', '2', 'f', 'f', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('9', '1', '3', 'f', 't', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('10', '2', '3', 't', 't', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('11', '3', '3', 'f', 'f', 'admin', '2016-10-12', '2016-10-12');
INSERT INTO "public"."t_credit_mapper" VALUES ('12', '4', '3', 'f', 'f', 'admin', '2016-10-12', '2016-10-12');

ALTER TABLE "public"."t_credit_mapper" ADD PRIMARY KEY ("id");
