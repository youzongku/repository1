
ALTER TABLE "public"."t_dis_salesman"
ADD COLUMN "node_type" int4 DEFAULT 1;

COMMENT ON COLUMN "public"."t_dis_salesman"."node_type" IS '业务员类型（1：业务类型员工，2：管理类型员工）';



 
ALTER TABLE "public"."t_dis_organizational"
DROP COLUMN "node_type",
ADD COLUMN "node_type" int4 DEFAULT 1;
COMMENT ON COLUMN "public"."t_dis_organizational"."node_type" IS '节点类型（1：业务类型节点，2：管理类型节点）';



CREATE TABLE "public"."t_dis_emp_salesman_mapper" (
"id" serial4 NOT NULL,
"emp_id" int4,
"salesman_id" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON TABLE "public"."t_dis_emp_salesman_mapper" IS '管理员工与业务员共对应表';
COMMENT ON COLUMN "public"."t_dis_emp_salesman_mapper"."id" IS '主键id';
COMMENT ON COLUMN "public"."t_dis_emp_salesman_mapper"."emp_id" IS '管理员工id';
COMMENT ON COLUMN "public"."t_dis_emp_salesman_mapper"."salesman_id" IS '业务员工id';
ALTER TABLE "public"."t_dis_emp_salesman_mapper" ADD PRIMARY KEY ("id");




CREATE TABLE "public"."t_file_operate_record" (
"id" serial4 NOT NULL,
"operator" varchar(30),
"operate_time" timestamp(6),
"opdesc" varchar(250),
"apply_id" int4,
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON TABLE "public"."t_file_operate_record" IS '注册申请文件修改日志表';

COMMENT ON COLUMN "public"."t_file_operate_record"."id" IS '主键id';

COMMENT ON COLUMN "public"."t_file_operate_record"."operator" IS '操作人';

COMMENT ON COLUMN "public"."t_file_operate_record"."operate_time" IS '操作时间
';

COMMENT ON COLUMN "public"."t_file_operate_record"."opdesc" IS '操作简述';

COMMENT ON COLUMN "public"."t_file_operate_record"."apply_id" IS '注册申请id';


