ALTER TABLE "public"."t_condt_inst"
ADD COLUMN "parent_id" int4;
COMMENT ON COLUMN "public"."t_condt_inst"."parent_id" IS '父条件实例id';