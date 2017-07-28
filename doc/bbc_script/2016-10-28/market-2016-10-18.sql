DELETE from t_act_inst;
DELETE from t_activity_information_log;
DELETE from t_condt_inst;
DELETE from t_pro_act;
DELETE from t_pro_type;
DELETE from t_pvlg_inst;
DELETE from t_rel_type_pvlg_condt;



CREATE TABLE "t_pro_act_dis_mode" (
"id" serial4 NOT NULL,
"pro_act_id" int4,
"dis_mode_id" int4,
"dis_mode_name" varchar(50),
PRIMARY KEY ("id") 
);

COMMENT ON TABLE "t_pro_act_dis_mode" IS '活动关联的分销商模式';
COMMENT ON COLUMN "t_pro_act_dis_mode"."pro_act_id" IS '活动id';
COMMENT ON COLUMN "t_pro_act_dis_mode"."dis_mode_id" IS '模式id';
COMMENT ON COLUMN "t_pro_act_dis_mode"."dis_mode_name" IS '模式名称';


CREATE TABLE "t_condt_inst_ext" (
"id" serial4 NOT NULL,
"act_inst_id" int4,
"condt_inst_id" int4,
"specify_attr_value" text,
"stepped" bool DEFAULT false,
"double_up" bool DEFAULT false,
PRIMARY KEY ("id") 
);

COMMENT ON TABLE "t_condt_inst_ext" IS '额外的条件实例表';
COMMENT ON COLUMN "t_condt_inst_ext"."act_inst_id" IS '活动实例id';
COMMENT ON COLUMN "t_condt_inst_ext"."condt_inst_id" IS '条件实例id';
COMMENT ON COLUMN "t_condt_inst_ext"."specify_attr_value" IS '具体的值';
COMMENT ON COLUMN "public"."t_condt_inst_ext"."stepped" IS '可阶梯';
COMMENT ON COLUMN "public"."t_condt_inst_ext"."double_up" IS '可翻倍';


ALTER TABLE "public"."t_act_inst"
ADD COLUMN "attr" int2;
COMMENT ON COLUMN "public"."t_act_inst"."attr" IS '条件属性（1商品、2购物车、3用户）';

ALTER TABLE "public"."t_act_inst"
DROP COLUMN "priority",
DROP COLUMN "is_set_v";

ALTER TABLE "public"."t_pro_condt"
ADD COLUMN "has_ext_condt" bool default false;
COMMENT ON COLUMN "public"."t_pro_condt"."has_ext_condt" IS '是否有设置属性的选项（指定商品属性/指定购物车属性）';

ALTER TABLE "public"."t_pro_act"
DROP COLUMN "mode_ids",
DROP COLUMN "mode_names";

ALTER TABLE "public"."t_condt_inst"
ADD COLUMN "priority" int2 default 1,
ADD COLUMN "is_set_v" int2 default 0,
ADD COLUMN "has_ext_condt" bool default false;
COMMENT ON COLUMN "public"."t_condt_inst"."is_set_v" IS '是否设置了参数0否1是';
COMMENT ON COLUMN "public"."t_condt_inst"."priority" IS '优先级，默认为1';
COMMENT ON COLUMN "public"."t_condt_inst"."has_ext_condt" IS '是否有设置属性的选项（指定商品属性/指定购物车属性）';

update t_pro_condt set has_ext_condt=true where id in (1,2,3,8);

ALTER TABLE "public"."t_pro_pvlg"
ADD COLUMN "attr" int2;
COMMENT ON COLUMN "public"."t_pro_pvlg"."attr" IS '条件属性（1商品、2购物车、3用户）';


ALTER TABLE "public"."t_pvlg_inst"
ADD COLUMN "condt_inst_id" int4;
COMMENT ON COLUMN "public"."t_pvlg_inst"."condt_inst_id" IS '条件实例id';

update t_pro_pvlg set name='满赠',p_type=1,attr=1 where id=1;
update t_pro_pvlg set name='满减',p_type=2,attr=1 where id=2;
update t_pro_pvlg set name='折扣',p_type=3,attr=1 where id=3;
insert into t_pro_pvlg(id,name,is_delete,p_type,attr) values(4,'购物车满赠',false,4,2);
insert into t_pro_pvlg(id,name,is_delete,p_type,attr) values(5,'购物车满减',false,5,2);
insert into t_pro_pvlg(id,name,is_delete,p_type,attr) values(6,'整个购物车的定额折扣',false,6,2);


update t_pro_condt set is_delete=true,has_ext_condt=false where c_type=14;
update t_pro_condt set is_delete=true,has_ext_condt=false where c_type=13;
update t_pro_condt set is_delete=true,has_ext_condt=false where c_type=12;
update t_pro_condt set is_delete=true,has_ext_condt=false where c_type=11;
update t_pro_condt set is_delete=true,has_ext_condt=false where c_type=10;
update t_pro_condt set is_delete=true,has_ext_condt=false where c_type=9;
update t_pro_condt set is_delete=true,has_ext_condt=true where c_type=8;
update t_pro_condt set is_delete=true,has_ext_condt=false where c_type=7;
update t_pro_condt set is_delete=false,has_ext_condt=false where c_type=6;
update t_pro_condt set is_delete=false,has_ext_condt=false where c_type=5;
update t_pro_condt set is_delete=true,has_ext_condt=false where c_type=4;
update t_pro_condt set is_delete=true,has_ext_condt=true where c_type=3;
update t_pro_condt set is_delete=false,has_ext_condt=true where c_type=2;
update t_pro_condt set is_delete=false,has_ext_condt=true where c_type=1;









