
DROP SEQUENCE "public"."t_attr_group_id_seq";
CREATE SEQUENCE "public"."t_attr_group_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_attribute_set_id_seq";
CREATE SEQUENCE "public"."t_attribute_set_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_attribute_type_id_seq";
CREATE SEQUENCE "public"."t_attribute_type_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;
SELECT setval('"public"."t_attribute_type_id_seq"', 1, true);

DROP SEQUENCE "public"."t_attrset_attr_id_seq";
CREATE SEQUENCE "public"."t_attrset_attr_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_bbc_attribute_id_seq";
CREATE SEQUENCE "public"."t_bbc_attribute_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_bbcattr_erpattr_id_seq";
CREATE SEQUENCE "public"."t_bbcattr_erpattr_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_category_attrset_id_seq";
CREATE SEQUENCE "public"."t_category_attrset_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_category_id_seq";
CREATE SEQUENCE "public"."t_category_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_costomer_type_id_seq";
CREATE SEQUENCE "public"."t_costomer_type_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_credit_mapper_id_seq";
CREATE SEQUENCE "public"."t_credit_mapper_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_erp_attribute_id_seq";
CREATE SEQUENCE "public"."t_erp_attribute_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_group_id_seq";
CREATE SEQUENCE "public"."t_group_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_image_id_seq";
CREATE SEQUENCE "public"."t_image_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_operate_product_price_id_seq";
CREATE SEQUENCE "public"."t_operate_product_price_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_operate_product_price_rule_id_seq";
CREATE SEQUENCE "public"."t_operate_product_price_rule_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_product_price_rule_id_seq";
CREATE SEQUENCE "public"."t_product_price_rule_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_setattr_multivalue_id_seq";
CREATE SEQUENCE "public"."t_setattr_multivalue_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_sku_entity_id_seq";
CREATE SEQUENCE "public"."t_sku_entity_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_sku_id_seq";
CREATE SEQUENCE "public"."t_sku_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_spu_category_id_seq";
CREATE SEQUENCE "public"."t_spu_category_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_spu_id_seq";
CREATE SEQUENCE "public"."t_spu_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_spu_sku_id_seq";
CREATE SEQUENCE "public"."t_spu_sku_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP SEQUENCE "public"."t_translate_id_seq";
CREATE SEQUENCE "public"."t_translate_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

DROP TABLE IF EXISTS "public"."t_attr_group";
CREATE TABLE "public"."t_attr_group" (
"id" int4 DEFAULT nextval('t_attr_group_id_seq'::regclass) NOT NULL,
"group_id" int4,
"attr_id" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_attr_group" IS '属性与分组映射表';
COMMENT ON COLUMN "public"."t_attr_group"."group_id" IS '分组id';
COMMENT ON COLUMN "public"."t_attr_group"."attr_id" IS '属性id';

DROP TABLE IF EXISTS "public"."t_attr_multivalue";
CREATE TABLE "public"."t_attr_multivalue" (
"id" varchar(50) COLLATE "default" NOT NULL,
"attr_id" int4,
"content_text" text COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_attr_multivalue" IS '属性字段可选值表';
COMMENT ON COLUMN "public"."t_attr_multivalue"."attr_id" IS '属性id';
COMMENT ON COLUMN "public"."t_attr_multivalue"."content_text" IS '内容';

DROP TABLE IF EXISTS "public"."t_attribute_set";
CREATE TABLE "public"."t_attribute_set" (
"id" int4 DEFAULT nextval('t_attribute_set_id_seq'::regclass) NOT NULL,
"set_name" varchar(255) COLLATE "default",
"status" int4,
"set_desc" varchar(255) COLLATE "default",
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_attribute_set" IS '属性集表';
COMMENT ON COLUMN "public"."t_attribute_set"."id" IS '主键';
COMMENT ON COLUMN "public"."t_attribute_set"."set_name" IS '属性集名称';
COMMENT ON COLUMN "public"."t_attribute_set"."status" IS '状态';
COMMENT ON COLUMN "public"."t_attribute_set"."set_desc" IS '属性集描述';

DROP TABLE IF EXISTS "public"."t_attribute_type";
CREATE TABLE "public"."t_attribute_type" (
"id" int4 DEFAULT nextval('t_attribute_type_id_seq'::regclass) NOT NULL,
"type_name" varchar(255) COLLATE "default",
"type_desc" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_attribute_type" IS '字段属性类型表';
COMMENT ON COLUMN "public"."t_attribute_type"."id" IS '主键id';
COMMENT ON COLUMN "public"."t_attribute_type"."type_name" IS '属性类型（String、Double、Date、Integer）';


INSERT INTO "public"."t_attribute_type" VALUES ('1', 'String', '字符串类型');
INSERT INTO "public"."t_attribute_type" VALUES ('2', 'Integer', '整型');
INSERT INTO "public"."t_attribute_type" VALUES ('3', 'Doulbe', '双精度浮点型');
INSERT INTO "public"."t_attribute_type" VALUES ('4', 'Boolean', '布尔型');
INSERT INTO "public"."t_attribute_type" VALUES ('5', 'Long', '长整形');
INSERT INTO "public"."t_attribute_type" VALUES ('6', 'Float', '浮点型');
INSERT INTO "public"."t_attribute_type" VALUES ('7', 'Char', '字符型');
INSERT INTO "public"."t_attribute_type" VALUES ('8', 'Byte', '字节');
INSERT INTO "public"."t_attribute_type" VALUES ('9', 'Date', '日期类型');


DROP TABLE IF EXISTS "public"."t_attrset_attr";
CREATE TABLE "public"."t_attrset_attr" (
"id" int4 DEFAULT nextval('t_attrset_attr_id_seq'::regclass) NOT NULL,
"set_id" int4,
"attribute_id" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_attrset_attr" IS '属性集与属性映射';
COMMENT ON COLUMN "public"."t_attrset_attr"."set_id" IS '属性集id';
COMMENT ON COLUMN "public"."t_attrset_attr"."attribute_id" IS '属性id';

DROP TABLE IF EXISTS "public"."t_bbc_attribute";
CREATE TABLE "public"."t_bbc_attribute" (
"id" int4 DEFAULT nextval('t_bbc_attribute_id_seq'::regclass) NOT NULL,
"attr_name" varchar(255) COLLATE "default",
"attr_key" varchar(255) COLLATE "default",
"attr_desc" varchar(255) COLLATE "default",
"status" int4,
"attr_type" varchar(50) COLLATE "default",
"type_id" int4,
"is_null" bool,
"is_show" bool,
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date,
"attr_value" text COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_bbc_attribute" IS 'BBC属性表

';
COMMENT ON COLUMN "public"."t_bbc_attribute"."attr_name" IS '属性名称';
COMMENT ON COLUMN "public"."t_bbc_attribute"."attr_key" IS '属性key，实体类属性名称';
COMMENT ON COLUMN "public"."t_bbc_attribute"."attr_desc" IS '属性描述';
COMMENT ON COLUMN "public"."t_bbc_attribute"."status" IS '属性状态';
COMMENT ON COLUMN "public"."t_bbc_attribute"."attr_type" IS '属性类型（文本、下拉、单选、多选）';
COMMENT ON COLUMN "public"."t_bbc_attribute"."type_id" IS '字段类型';
COMMENT ON COLUMN "public"."t_bbc_attribute"."is_null" IS '是否可为空';
COMMENT ON COLUMN "public"."t_bbc_attribute"."is_show" IS '是否展示';
COMMENT ON COLUMN "public"."t_bbc_attribute"."attr_value" IS '属性值';


DROP TABLE IF EXISTS "public"."t_bbcattr_erpattr";
CREATE TABLE "public"."t_bbcattr_erpattr" (
"id" int4 DEFAULT nextval('t_bbcattr_erpattr_id_seq'::regclass) NOT NULL,
"bbc_attr_id" int4,
"erp_attr_id" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_bbcattr_erpattr" IS 'BBC属性与ERP属性映射表';

DROP TABLE IF EXISTS "public"."t_category";
CREATE TABLE "public"."t_category" (
"id" int4 DEFAULT nextval('t_category_id_seq'::regclass) NOT NULL,
"category_name" varchar(255) COLLATE "default",
"category_desc" varchar(255) COLLATE "default",
"parent_id" int4,
"status" int4,
"category_id" int4,
"level" int4,
"position" int4,
"is_show" bool,
"is_navigation" bool,
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_category" IS '商品分类表';
COMMENT ON COLUMN "public"."t_category"."category_name" IS '分类名称';
COMMENT ON COLUMN "public"."t_category"."category_desc" IS '分类描述';
COMMENT ON COLUMN "public"."t_category"."parent_id" IS '父类目id';
COMMENT ON COLUMN "public"."t_category"."status" IS '状态';
COMMENT ON COLUMN "public"."t_category"."category_id" IS '类目id（erp一致）';
COMMENT ON COLUMN "public"."t_category"."level" IS '分类级别';
COMMENT ON COLUMN "public"."t_category"."position" IS '位置排序';
COMMENT ON COLUMN "public"."t_category"."is_navigation" IS '是否导航显示';

DROP TABLE IF EXISTS "public"."t_category_attrset";
CREATE TABLE "public"."t_category_attrset" (
"id" int4 DEFAULT nextval('t_category_attrset_id_seq'::regclass) NOT NULL,
"set_id" int4,
"cid" int4,
"category_id" int4,
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_category_attrset" IS '分类与属性集映射，多对多';
COMMENT ON COLUMN "public"."t_category_attrset"."set_id" IS '属性集id';
COMMENT ON COLUMN "public"."t_category_attrset"."cid" IS '商品分类表主键';
COMMENT ON COLUMN "public"."t_category_attrset"."category_id" IS '商品分类id（erp一致）';

DROP TABLE IF EXISTS "public"."t_erp_attribute";
CREATE TABLE "public"."t_erp_attribute" (
"id" int4 DEFAULT nextval('t_erp_attribute_id_seq'::regclass) NOT NULL,
"erp_attr_key" varchar(255) COLLATE "default",
"erp_attr_name" varchar(255) COLLATE "default",
"erp_attr_desc" varchar(255) COLLATE "default",
"erp_attr_type" varchar(255) COLLATE "default",
"type_id" int4,
"create_user" varchar(50) COLLATE "default",
"create_time" timestamp(6),
"update_time" timestamp(6)
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_erp_attribute" IS 'ERP属性表';
COMMENT ON COLUMN "public"."t_erp_attribute"."erp_attr_key" IS 'ERP传入Json Key';
COMMENT ON COLUMN "public"."t_erp_attribute"."erp_attr_name" IS 'erp属性名称';
COMMENT ON COLUMN "public"."t_erp_attribute"."erp_attr_desc" IS 'ERP属性描述';
COMMENT ON COLUMN "public"."t_erp_attribute"."erp_attr_type" IS 'ERP属性展现类型（下拉、文本、、、、）';
COMMENT ON COLUMN "public"."t_erp_attribute"."type_id" IS 'Json数据key对应的value数据类型';

DROP TABLE IF EXISTS "public"."t_group";
CREATE TABLE "public"."t_group" (
"id" int4 DEFAULT nextval('t_group_id_seq'::regclass) NOT NULL,
"group_name" varchar(255) COLLATE "default",
"group_desc" varchar(255) COLLATE "default",
"is_active" bool,
"is_show" bool,
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_group" IS '属性分组';
COMMENT ON COLUMN "public"."t_group"."group_name" IS '分组名称';
COMMENT ON COLUMN "public"."t_group"."group_desc" IS '分组描述';
COMMENT ON COLUMN "public"."t_group"."is_active" IS '是否激活';
COMMENT ON COLUMN "public"."t_group"."is_show" IS '是否展示';

DROP TABLE IF EXISTS "public"."t_image";
CREATE TABLE "public"."t_image" (
"id" int4 DEFAULT nextval('t_image_id_seq'::regclass) NOT NULL,
"entity_id" int4,
"imgurl" varchar(200) COLLATE "default",
"bthumbnail" bool,
"position" int4,
"bsmallimage" bool,
"bmainimage" bool
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_image" IS 'SKU图片表

';
COMMENT ON COLUMN "public"."t_image"."id" IS '主键';
COMMENT ON COLUMN "public"."t_image"."entity_id" IS '实体id';
COMMENT ON COLUMN "public"."t_image"."imgurl" IS '图片链接';
COMMENT ON COLUMN "public"."t_image"."bthumbnail" IS '是否缩略图';
COMMENT ON COLUMN "public"."t_image"."position" IS '排序';
COMMENT ON COLUMN "public"."t_image"."bsmallimage" IS '是否是小图';
COMMENT ON COLUMN "public"."t_image"."bmainimage" IS '是否是主图';

DROP TABLE IF EXISTS "public"."t_price";
CREATE TABLE "public"."t_price" (
"id" varchar(50) COLLATE "default",
"entity_id" int4,
"price_key" varchar(50) COLLATE "default",
"price_value" float8
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_price" IS 'SKU价格表';
COMMENT ON COLUMN "public"."t_price"."entity_id" IS '实体id';
COMMENT ON COLUMN "public"."t_price"."price_key" IS '价格字段';
COMMENT ON COLUMN "public"."t_price"."price_value" IS '价格值';


DROP TABLE IF EXISTS "public"."t_setattr_multivalue";
CREATE TABLE "public"."t_setattr_multivalue" (
"id" int4 DEFAULT nextval('t_setattr_multivalue_id_seq'::regclass) NOT NULL,
"set_attr_id" int4,
"value_id" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_setattr_multivalue" IS '属性集与属性关联属性可选值表（用于分类属性可选值属于哪些属性集）';
COMMENT ON COLUMN "public"."t_setattr_multivalue"."set_attr_id" IS '属性集与属性映射表id';
COMMENT ON COLUMN "public"."t_setattr_multivalue"."value_id" IS '可选项值id';

DROP TABLE IF EXISTS "public"."t_sku";
CREATE TABLE "public"."t_sku" (
"id" int4 DEFAULT nextval('t_sku_id_seq'::regclass) NOT NULL,
"sku" varchar(50) COLLATE "default",
"name" varchar(255) COLLATE "default",
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_sku" IS 'SKU表';
COMMENT ON COLUMN "public"."t_sku"."id" IS '主键';
COMMENT ON COLUMN "public"."t_sku"."sku" IS 'sku编号';
COMMENT ON COLUMN "public"."t_sku"."name" IS '名称';

DROP TABLE IF EXISTS "public"."t_sku_attr";
CREATE TABLE "public"."t_sku_attr" (
"id" varchar(50) COLLATE "default" NOT NULL,
"sku" varchar(50) COLLATE "default",
"attr_id" int4,
"attr_key" varchar(255) COLLATE "default",
"attr_type" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_sku_attr" IS 'SKU与属性映射表';
COMMENT ON COLUMN "public"."t_sku_attr"."id" IS 'uuid';
COMMENT ON COLUMN "public"."t_sku_attr"."sku" IS 'sku编号';
COMMENT ON COLUMN "public"."t_sku_attr"."attr_key" IS '属性key';
COMMENT ON COLUMN "public"."t_sku_attr"."attr_type" IS '属性类型（文本、下拉、单选、多选）';

DROP TABLE IF EXISTS "public"."t_sku_entity";
CREATE TABLE "public"."t_sku_entity" (
"id" int4 DEFAULT nextval('t_sku_entity_id_seq'::regclass) NOT NULL,
"content_id" varchar(50) COLLATE "default",
"multiselect" bool,
"attr_type" varchar(255) COLLATE "default",
"attr_id" varchar(50) COLLATE "default",
"attr_key" varchar(50) COLLATE "default",
"data_type" varchar(50) COLLATE "default",
"attr_value" text COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_sku_entity" IS 'SKU实体表

';
COMMENT ON COLUMN "public"."t_sku_entity"."content_id" IS '数据值表id（uuid）';
COMMENT ON COLUMN "public"."t_sku_entity"."multiselect" IS 'multiselect为true时，取content_id，去其他表取值';
COMMENT ON COLUMN "public"."t_sku_entity"."attr_type" IS '属性类型（文本、下拉、单选、多选）';
COMMENT ON COLUMN "public"."t_sku_entity"."attr_id" IS '属性id（uuid）';
COMMENT ON COLUMN "public"."t_sku_entity"."attr_key" IS '属性key';
COMMENT ON COLUMN "public"."t_sku_entity"."data_type" IS '数据类型';
COMMENT ON COLUMN "public"."t_sku_entity"."attr_value" IS '属性值';

DROP TABLE IF EXISTS "public"."t_spu";
CREATE TABLE "public"."t_spu" (
"id" int4 DEFAULT nextval('t_spu_id_seq'::regclass) NOT NULL,
"spu" varchar(50) COLLATE "default",
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_spu" IS 'SPU信息';
COMMENT ON COLUMN "public"."t_spu"."id" IS '主键';
COMMENT ON COLUMN "public"."t_spu"."spu" IS 'SPU编号';
COMMENT ON COLUMN "public"."t_spu"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."t_spu"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_spu"."update_time" IS '更新时间';

DROP TABLE IF EXISTS "public"."t_spu_category";
CREATE TABLE "public"."t_spu_category" (
"id" int4 DEFAULT nextval('t_spu_category_id_seq'::regclass) NOT NULL,
"category_id" int4,
"spu_id" int4,
"spu" varchar(50) COLLATE "default",
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_spu_category" IS '类目与SPU映射关系';
COMMENT ON COLUMN "public"."t_spu_category"."id" IS '主键';
COMMENT ON COLUMN "public"."t_spu_category"."category_id" IS '类目id';
COMMENT ON COLUMN "public"."t_spu_category"."spu_id" IS 'sku表id';
COMMENT ON COLUMN "public"."t_spu_category"."spu" IS 'spu表编号';

DROP TABLE IF EXISTS "public"."t_spu_sku";
CREATE TABLE "public"."t_spu_sku" (
"id" int4 DEFAULT nextval('t_spu_sku_id_seq'::regclass) NOT NULL,
"spu_no" varchar(50) COLLATE "default",
"spu_id" int4,
"sku_no" varchar(50) COLLATE "default",
"sku_id" int4,
"create_user" varchar(50) COLLATE "default",
"create_time" date,
"update_time" date
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_spu_sku" IS 'SPU与SKU映射表';
COMMENT ON COLUMN "public"."t_spu_sku"."id" IS '主键';
COMMENT ON COLUMN "public"."t_spu_sku"."spu_no" IS 'spu表编号';
COMMENT ON COLUMN "public"."t_spu_sku"."spu_id" IS 'spu表id';
COMMENT ON COLUMN "public"."t_spu_sku"."sku_no" IS 'sku表编号';
COMMENT ON COLUMN "public"."t_spu_sku"."sku_id" IS 'SKU表id';

DROP TABLE IF EXISTS "public"."t_translate";
CREATE TABLE "public"."t_translate" (
"id" int4 DEFAULT nextval('t_translate_id_seq'::regclass) NOT NULL,
"entity_id" int4,
"ctitle" varchar(255) COLLATE "default",
"cdescription" varchar(255) COLLATE "default",
"cshortdescription" varchar(255) COLLATE "default",
"ckeyword" varchar(255) COLLATE "default",
"cmetatitle" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_translate" IS 'SKU详情表';
COMMENT ON COLUMN "public"."t_translate"."entity_id" IS '实体id';
COMMENT ON COLUMN "public"."t_translate"."ctitle" IS 'title';
COMMENT ON COLUMN "public"."t_translate"."cdescription" IS '描述';
COMMENT ON COLUMN "public"."t_translate"."cshortdescription" IS '短描述';
COMMENT ON COLUMN "public"."t_translate"."ckeyword" IS '关键字';
COMMENT ON COLUMN "public"."t_translate"."cmetatitle" IS 'meta title';

ALTER SEQUENCE "public"."t_attr_group_id_seq" OWNED BY "t_attr_group"."id";
ALTER SEQUENCE "public"."t_attribute_set_id_seq" OWNED BY "t_attribute_set"."id";
ALTER SEQUENCE "public"."t_attribute_type_id_seq" OWNED BY "t_attribute_type"."id";
ALTER SEQUENCE "public"."t_attrset_attr_id_seq" OWNED BY "t_attrset_attr"."id";
ALTER SEQUENCE "public"."t_bbc_attribute_id_seq" OWNED BY "t_bbc_attribute"."id";
ALTER SEQUENCE "public"."t_bbcattr_erpattr_id_seq" OWNED BY "t_bbcattr_erpattr"."id";
ALTER SEQUENCE "public"."t_category_attrset_id_seq" OWNED BY "t_category_attrset"."id";
ALTER SEQUENCE "public"."t_category_id_seq" OWNED BY "t_category"."id";
ALTER SEQUENCE "public"."t_erp_attribute_id_seq" OWNED BY "t_erp_attribute"."id";
ALTER SEQUENCE "public"."t_group_id_seq" OWNED BY "t_group"."id";
ALTER SEQUENCE "public"."t_image_id_seq" OWNED BY "t_image"."id";
ALTER SEQUENCE "public"."t_setattr_multivalue_id_seq" OWNED BY "t_setattr_multivalue"."id";
ALTER SEQUENCE "public"."t_sku_entity_id_seq" OWNED BY "t_sku_entity"."id";
ALTER SEQUENCE "public"."t_sku_id_seq" OWNED BY "t_sku"."id";
ALTER SEQUENCE "public"."t_spu_category_id_seq" OWNED BY "t_spu_category"."id";
ALTER SEQUENCE "public"."t_spu_id_seq" OWNED BY "t_spu"."id";
ALTER SEQUENCE "public"."t_spu_sku_id_seq" OWNED BY "t_spu_sku"."id";
ALTER SEQUENCE "public"."t_translate_id_seq" OWNED BY "t_translate"."id";

ALTER TABLE "public"."t_attr_group" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_attr_multivalue" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_attribute_set" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_attrset_attr" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_bbc_attribute" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_bbcattr_erpattr" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_category" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_category_attrset" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_erp_attribute" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_group" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_sku" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_sku_attr" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_sku_entity" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_spu" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_spu_category" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_spu_sku" ADD PRIMARY KEY ("id");

ALTER TABLE "public"."t_translate" ADD PRIMARY KEY ("id");
