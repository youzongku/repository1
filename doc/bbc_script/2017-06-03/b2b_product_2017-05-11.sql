CREATE TABLE "public"."t_inventory_oprecord" (
"id" serial4 NOT NULL,
"lock_id" int4,
"opuser" varchar(50) COLLATE "default",
"opdate" timestamp(6) DEFAULT now(),
"comment" varchar(500) COLLATE "default",
CONSTRAINT "t_inventory_oprecord_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_inventory_oprecord" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_inventory_oprecord" IS 'KA锁库操作日志表';

COMMENT ON COLUMN "public"."t_inventory_oprecord"."lock_id" IS '锁库id';

COMMENT ON COLUMN "public"."t_inventory_oprecord"."opuser" IS '操作人';

COMMENT ON COLUMN "public"."t_inventory_oprecord"."opdate" IS '操作时间';

COMMENT ON COLUMN "public"."t_inventory_oprecord"."comment" IS '操作内容';

CREATE TABLE "public"."t_ivy_opt_detail" (
"id" serial4 NOT NULL,
"sku" varchar COLLATE "default",
"title" varchar COLLATE "default",
"inter_bar_code" varchar COLLATE "default",
"warehouse_id" int4,
"warehouse_name" varchar COLLATE "default",
"expiration_date" timestamp(6),
"num" int4,
"oprecord_id" int4,
CONSTRAINT "t_ivy_opt_detail_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_ivy_opt_detail" OWNER TO "tomtop";

COMMENT ON COLUMN "public"."t_ivy_opt_detail"."sku" IS '商品编码';

COMMENT ON COLUMN "public"."t_ivy_opt_detail"."title" IS '商品名称';

COMMENT ON COLUMN "public"."t_ivy_opt_detail"."inter_bar_code" IS '国际条形码';

COMMENT ON COLUMN "public"."t_ivy_opt_detail"."warehouse_id" IS '仓库id';

COMMENT ON COLUMN "public"."t_ivy_opt_detail"."warehouse_name" IS '仓库名称';

COMMENT ON COLUMN "public"."t_ivy_opt_detail"."expiration_date" IS '到期日期';

COMMENT ON COLUMN "public"."t_ivy_opt_detail"."num" IS '释放数量';

COMMENT ON COLUMN "public"."t_ivy_opt_detail"."oprecord_id" IS '操作日志id';


