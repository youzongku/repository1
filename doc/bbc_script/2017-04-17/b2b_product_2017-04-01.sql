alter table t_contract_attachment

ADD COLUMN "md_value" varchar(100);

COMMENT ON COLUMN "public"."t_contract_attachment"."md_value" IS '附件md5值';