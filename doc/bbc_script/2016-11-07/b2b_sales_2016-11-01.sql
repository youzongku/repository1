ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "dis_mode" int4;

COMMENT ON COLUMN "public"."t_product_sales_order_main"."dis_mode" IS '分销模式:1、电商，2、经销商，3、KA直营，4、进口专营';


ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "tax_fee" float8;
COMMENT ON COLUMN "public"."t_product_sales_order_main"."tax_fee" IS '税金';

ALTER TABLE "public"."t_product_sales_order_main"
ADD COLUMN "gst" float8,
ADD COLUMN "vat" float8,
ADD COLUMN "import_tar" float8,
ADD COLUMN "postal_fee" float8;
COMMENT ON COLUMN "public"."t_product_sales_order_main"."gst" IS '消费税';
COMMENT ON COLUMN "public"."t_product_sales_order_main"."vat" IS '增值税税';
COMMENT ON COLUMN "public"."t_product_sales_order_main"."import_tar" IS '关税';
COMMENT ON COLUMN "public"."t_product_sales_order_main"."postal_fee" IS '行邮税';

ALTER TABLE "public"."t_product_sales_order_details"
ADD COLUMN "gst_rate" float8,
ADD COLUMN "vat_rate" float8,
ADD COLUMN "import_tar_rate" float8,
ADD COLUMN "postal_fee_rate" float8,
ADD COLUMN "logistic_fee" float8;
COMMENT ON COLUMN "public"."t_product_sales_order_details"."gst_rate" IS '消费税税率';
COMMENT ON COLUMN "public"."t_product_sales_order_details"."vat_rate" IS '增值税税率';
COMMENT ON COLUMN "public"."t_product_sales_order_details"."import_tar_rate" IS '关税税率';
COMMENT ON COLUMN "public"."t_product_sales_order_details"."postal_fee_rate" IS '行邮税税率';
COMMENT ON COLUMN "public"."t_product_sales_order_details"."logistic_fee" IS '头程运费';