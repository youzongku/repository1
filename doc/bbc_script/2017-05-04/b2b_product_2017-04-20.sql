CREATE TABLE "public"."t_inventory_lock" (
"id" serial4  NOT NULL,
"account" varchar(255) COLLATE "default",
"lock_no" varchar(255) COLLATE "default",
"nick_name" varchar(255) COLLATE "default",
"sale_man" varchar(255) COLLATE "default",
"create_user" varchar(255) COLLATE "default",
"create_date" timestamp(6) DEFAULT now(),
"update_date" timestamp(6),
"remark" varchar(255) COLLATE "default",
"estimated_shipping_time" timestamp(6),
"status" int4 DEFAULT 1,
"is_left_stock" int4 DEFAULT 1,
CONSTRAINT "t_inventory_lock_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_inventory_lock" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_inventory_lock" IS 'KA锁库主表';

COMMENT ON COLUMN "public"."t_inventory_lock"."id" IS '主键';

COMMENT ON COLUMN "public"."t_inventory_lock"."account" IS '分销商账号';

COMMENT ON COLUMN "public"."t_inventory_lock"."lock_no" IS '锁库单号（系统生成）';

COMMENT ON COLUMN "public"."t_inventory_lock"."nick_name" IS '分销商昵称';

COMMENT ON COLUMN "public"."t_inventory_lock"."sale_man" IS '业务员';

COMMENT ON COLUMN "public"."t_inventory_lock"."create_user" IS '创建人';

COMMENT ON COLUMN "public"."t_inventory_lock"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_inventory_lock"."update_date" IS '更新时间';

COMMENT ON COLUMN "public"."t_inventory_lock"."status" IS '锁库状态(0：无效 1：生效)';

COMMENT ON COLUMN "public"."t_inventory_lock"."is_left_stock" IS '是否剩余库存(0:否;1:是)';



CREATE TABLE "public"."t_inventory_lock_detail" (
"id" serial4 NOT NULL,
"sku" varchar(255) COLLATE "default",
"expiration_date" timestamp(6),
"lock_num" int4,
"warehouse_id" int4,
"warehouse_name" varchar(255) COLLATE "default",
"title" varchar(255) COLLATE "default",
"inter_bar_code" varchar(255) COLLATE "default",
"left_num" int4,
"lock_id" int4,
"lock_no" varchar(32) COLLATE "default",
CONSTRAINT "t_inventory_lock_detail_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_inventory_lock_detail" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_inventory_lock_detail" IS 'KA锁库详情表';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."id" IS '主键';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."sku" IS '商品编码';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."expiration_date" IS '到期日期';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."lock_num" IS '锁库数量';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."warehouse_id" IS '仓库id';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."warehouse_name" IS '仓库名称';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."title" IS '商品名称';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."inter_bar_code" IS '国际条形码';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."left_num" IS '剩余数量';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."lock_id" IS '锁库表id';

COMMENT ON COLUMN "public"."t_inventory_lock_detail"."lock_no" IS '锁库号';


CREATE TABLE "public"."t_inventory_order" (
"id" serial4 NOT NULL,
"order_no" varchar(255) COLLATE "default",
"qty" int4,
"sku" varchar(255) COLLATE "default",
"warehouse_id" int4,
"warehouse_name" varchar(255) COLLATE "default",
"title" varchar(255) COLLATE "default",
"inter_bar_code" varchar(255) COLLATE "default",
"detail_id" int4,
"lock_no" varchar(255) COLLATE "default",
"status" int4 DEFAULT 0,
"account" varchar(255) COLLATE "default",
CONSTRAINT "t_inventory_order_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_inventory_order" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_inventory_order" IS 'KA锁库订单使用记录表';

COMMENT ON COLUMN "public"."t_inventory_order"."id" IS '主键';

COMMENT ON COLUMN "public"."t_inventory_order"."order_no" IS '订单号';

COMMENT ON COLUMN "public"."t_inventory_order"."qty" IS '从锁库详情扣除的数量';

COMMENT ON COLUMN "public"."t_inventory_order"."sku" IS '商品编号';

COMMENT ON COLUMN "public"."t_inventory_order"."warehouse_name" IS '仓库名称';

COMMENT ON COLUMN "public"."t_inventory_order"."title" IS '商品名称';

COMMENT ON COLUMN "public"."t_inventory_order"."inter_bar_code" IS '国际条形码';

COMMENT ON COLUMN "public"."t_inventory_order"."detail_id" IS '锁库详情表id';

COMMENT ON COLUMN "public"."t_inventory_order"."lock_no" IS '锁库单号';

COMMENT ON COLUMN "public"."t_inventory_order"."status" IS '锁定状态(-1:释放,0:临时锁定，1，永久锁定) 预留字段，目前不做释放操作';

COMMENT ON COLUMN "public"."t_inventory_order"."account" IS '分销商账号';


INSERT INTO "public"."t_sequence" ("id", "seq_name", "current_value", "increment_", "remark") VALUES ('2', 'LOCK_NO', '1', '1', 'KA锁库号');

ALTER TABLE "public"."t_inventory_order"
ADD COLUMN "create_date" timestamp DEFAULT now();

COMMENT ON COLUMN "public"."t_inventory_order"."create_date" IS '创建时间';

ALTER TABLE "public"."t_inventory_order"
ADD COLUMN "expiration_date" timestamp;

COMMENT ON COLUMN "public"."t_inventory_order"."expiration_date" IS '到期日期';
