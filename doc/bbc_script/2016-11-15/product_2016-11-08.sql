ALTER TABLE "public"."t_product_price_rule"
ADD COLUMN "profit_rule" varchar(255);

COMMENT ON COLUMN "public"."t_product_price_rule"."c_rule" IS '设置价格系数 计算公式';

COMMENT ON COLUMN "public"."t_product_price_rule"."profit_rule" IS '设置利润 计算公式';

UPDATE t_product_price_rule
SET profit_rule = '$p+$f'
WHERE
	price_classification IN (
		'floorPrice',
		'proposalRetailPrice',
		'disCompanyCost',
		'marketInterventionPrice'
	);

UPDATE t_product_price_rule
SET profit_rule = '$p-$f'
WHERE
	price_classification IN (
		'ftzPrice',
		'supermarketPrice',
		'electricityPrices',
		'distributorPrice'
	);

ALTER TABLE "public"."t_product_price_factor"
ADD COLUMN "profit" float8,
ADD COLUMN "create_date" timestamp(6) DEFAULT now(),
ADD COLUMN "update_date" timestamp(6);

COMMENT ON COLUMN "public"."t_product_price_factor"."profit" IS '最后一次设置的利润值';

COMMENT ON COLUMN "public"."t_product_price_factor"."create_date" IS '创建时间';

COMMENT ON COLUMN "public"."t_product_price_factor"."update_date" IS '更新时间'; 
