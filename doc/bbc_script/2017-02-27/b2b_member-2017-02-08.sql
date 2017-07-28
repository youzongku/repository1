CREATE TABLE "public"."t_dis_invoice" (
"email" varchar(255) COLLATE "default",
"invoice_title" varchar(255) COLLATE "default",
"invoice_tax_number" varchar(255) COLLATE "default",
"id" serial4 NOT NULL,
"invoice_bank" varchar(255) COLLATE "default",
"invoice_bank_account" varchar(255) COLLATE "default",
"invoice_tel" varchar(255) COLLATE "default",
"invoice_company_addr" varchar(255) COLLATE "default",
CONSTRAINT "t_dis_invoice_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_dis_invoice" OWNER TO "tomtop";

COMMENT ON TABLE "public"."t_dis_invoice" IS '分销商发票信息表';

COMMENT ON COLUMN "public"."t_dis_invoice"."email" IS '分销商账号';

COMMENT ON COLUMN "public"."t_dis_invoice"."invoice_title" IS '发票信息-抬头';

COMMENT ON COLUMN "public"."t_dis_invoice"."invoice_tax_number" IS '发票信息-纳税号';

COMMENT ON COLUMN "public"."t_dis_invoice"."invoice_bank" IS '发票信息-银行发户行';

COMMENT ON COLUMN "public"."t_dis_invoice"."invoice_bank_account" IS '发票信息-银行账号';

COMMENT ON COLUMN "public"."t_dis_invoice"."invoice_tel" IS '发票信息-联系电话';

COMMENT ON COLUMN "public"."t_dis_invoice"."invoice_company_addr" IS '发票信息-公司地址';