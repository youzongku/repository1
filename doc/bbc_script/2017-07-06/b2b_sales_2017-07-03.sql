CREATE OR REPLACE FUNCTION "public"."exportso4finance"(IN searchtext varchar, IN status varchar, OUT email varchar, OUT nickname varchar, OUT salesorderno varchar, OUT customerservice varchar, OUT statusname varchar, OUT platformamount float8, OUT bbcpostage float8, OUT arrvicetotal float8, OUT optfee float8, OUT totalcost float8, OUT profit float8, OUT profitmargin float8, OUT contractcharge float8, OUT clearancepricetotal float8, OUT cleartotalcost float8, OUT clearprofit float8, OUT clearprofitmargin float8, OUT sdpamount float8)
  RETURNS SETOF "pg_catalog"."record" AS $BODY$ 
DECLARE
	sqltext TEXT := 'SELECT
		sm.sales_order_no, sm.email, sm.nick_name, sm.status, sb.customer_service, 
		sm.platform_amount,
		sb.bbc_postage,
		sb.original_freight,
		sm.arrvice_total,
		sm.opt_fee,
		sm.total_cost,
		sm.profit,
		sm.profit_margin,
		sm.contract_charge,
		sm.clearance_price_total,
		sm.clear_total_cost,
		sm.clear_profit,
		sm.clear_profit_margin,
		sm.sdp_amount
	FROM
		t_product_sales_order_main sm
	LEFT JOIN t_product_sales_order_base sb ON sb.sales_order_id = sm."id"	
	WHERE 1 = 1' ;
sqlresult record;
BEGIN
IF "length"(status)>0 THEN
sqltext:=sqltext || ' and sm.status IN(' || status || ')';
END IF;
IF "length"(searchtext)>0 THEN
sqltext:=sqltext || ' and (
											lower(sm.sales_order_no) like lower(''%' || searchtext || '%'')
											or lower(sm.email) like lower(''%' || searchtext || '%'')
	                    or lower(sm.nick_name) like lower(''%' || searchtext || '%'')
	                    or lower(sb.customer_service) like lower(''%' || searchtext || '%'')
											)';
END IF;
FOR sqlresult IN EXECUTE sqltext loop
email:=sqlresult.email;
nickname:=sqlresult.nick_name;
salesorderno:=sqlresult.sales_order_no;
customerservice:=sqlresult.customer_service;
CASE 
WHEN sqlresult.status='1' or sqlresult.status='103' THEN
statusname='待付款';
WHEN sqlresult.status='2' THEN
statusname='待用户确认';
WHEN sqlresult.status='3' THEN
statusname='待客服确认';
WHEN sqlresult.status='4' or sqlresult.status='5' or sqlresult.status='20' THEN
statusname='已关闭';
WHEN sqlresult.status='11' THEN
statusname='待财务确认';
WHEN sqlresult.status='12' THEN
statusname='待二次支付';
WHEN sqlresult.status='6' or sqlresult.status='7' THEN
statusname='待发货';
WHEN sqlresult.status='13' THEN
statusname='待发货（数据传输中）';
WHEN sqlresult.status='104' THEN
statusname='待发货（erp）';
WHEN sqlresult.status='9' THEN
statusname='待收货';
WHEN sqlresult.status='10' or sqlresult.status='106' THEN
statusname='已完成';
ELSE
statusname='';
END CASE;
platformamount:=sqlresult.platform_amount;
bbcpostage:=sqlresult.bbc_postage;
arrvicetotal:=sqlresult.arrvice_total;
optfee:=sqlresult.opt_fee;
totalcost:=sqlresult.total_cost;
profit:=sqlresult.profit;
profitmargin:=sqlresult.profit_margin;
contractcharge:=sqlresult.contract_charge;
clearancepricetotal:=sqlresult.clearance_price_total;
cleartotalcost:=sqlresult.clear_total_cost;
clearprofit:=sqlresult.clear_profit;
clearprofitmargin:=sqlresult.clear_profit_margin;
sdpamount:=sqlresult.sdp_amount;
RETURN NEXT;
END loop;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE COST 100
 ROWS 1000
;