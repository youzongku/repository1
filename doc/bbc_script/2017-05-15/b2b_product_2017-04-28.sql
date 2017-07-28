
ALTER TABLE "public"."t_contract_cost"
ADD COLUMN "status" int4 DEFAULT 1;;

COMMENT ON COLUMN "public"."t_contract_cost"."status" IS '合同费用状态(1、未开始 2、已开始 3、已结束)';

-- 初始化费用状态
UPDATE t_contract_cost c set status =  case 
			when (c.start_time > now() ) then 1
			when (c.start_time <= now() and  c.end_time >= now()) then 2
			when ( c.end_time < now()) then 3
			ELSE null
			END 
where status is null;
