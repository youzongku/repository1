ALTER TABLE "public"."t_operate_product_price"
ADD COLUMN "field_name" varchar(255);

COMMENT ON COLUMN "public"."t_operate_product_price"."field_name" IS '字段名称';



update t_operate_product_price set field_name = 'floorPrice' where  operate_desc = '市场最低价';
update t_operate_product_price set field_name = 'proposalRetailPrice' where  operate_desc = '零售价';
update t_operate_product_price set field_name = 'distributorPrice' where  operate_desc = '经销商价格';
update t_operate_product_price set field_name = 'electricityPrices' where  operate_desc = 'Bbc价格';
update t_operate_product_price set field_name = 'supermarketPrice' where  operate_desc = 'KA经销价格';
update t_operate_product_price set field_name = 'disCompanyCost' where  operate_desc = '营销成本价';
update t_operate_product_price set field_name = 'marketInterventionPrice' where  operate_desc = '市场干预供货价';
update t_operate_product_price set field_name = 'ftzPrice' where  operate_desc = '自贸区经销价格';