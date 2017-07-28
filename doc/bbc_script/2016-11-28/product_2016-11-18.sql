update t_operate_product_price set  operate_desc = '零售价' where field_name = 'proposalRetailPrice';
update t_operate_product_price set operate_desc = '经销商价格'  where  field_name = 'distributorPrice';
update t_operate_product_price set operate_desc = 'Bbc价格'  where  field_name = 'electricityPrices';
update t_operate_product_price set operate_desc = 'KA经销价格'  where field_name = 'supermarketPrice' ;
update t_operate_product_price set operate_desc = '自贸区经销价格'  where field_name = 'ftzPrice' ;

update t_product_price_rule set price_classification_desc = '零售价' where  price_classification = 'proposalRetailPrice';
update t_product_price_rule set price_classification_desc = '经销商价格' where  price_classification = 'distributorPrice';
update t_product_price_rule set price_classification_desc = 'Bbc价格' where  price_classification = 'electricityPrices';
update t_product_price_rule set price_classification_desc = 'KA经销价格' where  price_classification = 'supermarketPrice';
update t_product_price_rule set price_classification_desc = '自贸区经销价格' where  price_classification = 'ftzPrice';
