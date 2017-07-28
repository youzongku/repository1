ALTER TABLE "public"."t_sku_entity"
ADD COLUMN "attr_value" text;

COMMENT ON COLUMN "public"."t_sku_entity"."attr_value" IS '属性值';

INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('1', 'String', '字符串类型');
INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('2', 'Integer', '整型');
INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('3', 'Doulbe', '双精度浮点型');
INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('4', 'Boolean', '布尔型');
INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('5', 'Long', '长整形');
INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('6', 'Float', '浮点型');
INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('7', 'Char', '字符型');
INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('8', 'Byte', '字节');
INSERT INTO "public"."t_attribute_type" ("id", "type_name", "type_desc") VALUES ('9', 'Date', '日期类型');