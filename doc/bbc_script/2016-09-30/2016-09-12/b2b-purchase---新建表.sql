CREATE TABLE "public"."t_purchase_gift_op_record" (
"id" serial4 NOT NULL,
"user_email" varchar(50) COLLATE "default",
"operator_email" varchar(50) COLLATE "default",
"sku" varchar(50) COLLATE "default",
"warehouse_id" int4,
"warehouse_name" varchar(50) COLLATE "default",
"qty" int4,
"operate_time" timestamp(6) DEFAULT now(),
"comments" varchar(200) COLLATE "default",
 PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_purchase_gift_op_record" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_purchase_gift_op_record" IS '采购赠品日志表';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."user_email" IS '被操作的分销商邮箱';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."operator_email" IS '操作者邮箱';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."sku" IS '赠品sku';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."warehouse_id" IS '赠品仓库id';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."warehouse_name" IS '赠品仓库名';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."qty" IS '赠品数量';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."operate_time" IS '操作时间';

COMMENT ON COLUMN "public"."t_purchase_gift_op_record"."comments" IS '操作原因';