/*
Navicat PGSQL Data Transfer

Source Server         : 192.168.223.13_5432
Source Server Version : 90315
Source Host           : 192.168.223.13:5432
Source Database       : product
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90315
File Encoding         : 65001

Date: 2017-05-09 17:53:47
*/


-- ----------------------------
-- Table structure for t_export_model
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_export_model";
CREATE TABLE "public"."t_export_model" (
"id" SERIAL,
"function_id" varchar(255) COLLATE "default",
"function_param" varchar(255) COLLATE "default",
"function_result" varchar(2550) COLLATE "default",
"excel_rows" varchar(2550) COLLATE "default",
"excel_title" varchar(255) COLLATE "default",
"file_name" varchar(255) COLLATE "default",
"excel_width" varchar(2550) COLLATE "default",
"mergekey" varchar(255) COLLATE "default",
"rows_merge" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."t_export_model"."id" IS 'id';
COMMENT ON COLUMN "public"."t_export_model"."function_id" IS '函数名';
COMMENT ON COLUMN "public"."t_export_model"."function_param" IS '函数所需要的参数';
COMMENT ON COLUMN "public"."t_export_model"."function_result" IS '函数返回结果';
COMMENT ON COLUMN "public"."t_export_model"."excel_rows" IS '表格列内容';
COMMENT ON COLUMN "public"."t_export_model"."excel_title" IS '表格头';
COMMENT ON COLUMN "public"."t_export_model"."file_name" IS '下载文件名';
COMMENT ON COLUMN "public"."t_export_model"."excel_width" IS '表格列宽度';
COMMENT ON COLUMN "public"."t_export_model"."mergekey" IS '多行合并依赖列';
COMMENT ON COLUMN "public"."t_export_model"."rows_merge" IS '需要合并行所在的列';

-- ----------------------------
-- Records of t_export_model
-- ----------------------------
INSERT INTO "public"."t_export_model" VALUES ('1', 'exportInventoryDataWithDate', '[{"index":0,"key":"expiration_begin"},{"index":1,"key":"expiration_end"}]', '[{"index":0,"key":"outsku"},{"index":1,"key":"outname"},{"index":2,"key":"out_ex_date"},{"index":3,"key":"out_pack_qty"},{"index":4,"key":"out_plug_type"},{"index":5,"key":"out_package_qty"},{"index":6,"key":"out_ex_date_qty"},{"index":7,"key":"out_ex_date_qty_sum"}]', '[{"index":0,"rowName":"SKU"},{"index":1,"rowName":"商品名称"},{"index":2,"rowName":"过期日期"},{"index":3,"rowName":"箱规"},{"index":4,"rowName":"规格"},{"index":5,"rowName":"箱数"},{"index":6,"rowName":"数量"},{"index":7,"rowName":"总计数"}]', '商品库存信息', '商品库存表.xls', null, '[0]', '[0,1]');
INSERT INTO "public"."t_export_model" VALUES ('2', 'exportmarketingorder', '[{"index":0,"key":"searchText"},{"index":1,"key":"startDate"},{"index":2,"key":"endDate"},{"index":3,"key":"distributorType"},{"index":4,"key":"status"}]', '[{"index":0,"key":"createdate"},{"index":1,"key":"salesman"},{"index":2,"key":"marketorderno"},{"index":3,"key":"salesorderno"},{"index":4,"key":"sku"},{"index":5,"key":"productname"},{"index":6,"key":"qty"},{"index":7,"key":"arriveprivce"},{"index":8,"key":"arriveprivcecount"},{"index":9,"key":"remark"}]', '[{"index":0,"rowName":"下单日期"},{"index":1,"rowName":"录入人"},{"index":2,"rowName":"订单编号(YX)"},{"index":3,"rowName":"发货单号(XS)"},{"index":4,"rowName":"SKU"},{"index":5,"rowName":"中文品名"},{"index":6,"rowName":"数量"},{"index":7,"rowName":"到仓价"},{"index":8,"rowName":"到仓价小计"},{"index":9,"rowName":"备注"}]', '免费样品登记表', '样品登记表.xls', '[{"index":0,"width":6000},{"index":1,"width":4000},{"index":2,"width":7000},{"index":3,"width":7000},{"index":4,"width":4000},{"index":5,"width":8500},{"index":6,"width":2500},{"index":7,"width":3200},{"index":8,"width":3200},{"index":9,"width":6000}]', 'marketorderno', '[0,1,2,3,9]');
INSERT INTO "public"."t_export_model" VALUES ('3', 'exportsalesorder', '[{"index":0,"key":"orderStartDate"},{"index":1,"key":"orderEndDate"},{"index":2,"key":"status"},{"index":3,"key":"seachSpan"},{"index":4,"key":"warehouseId"},{"index":5,"key":"distributorType"},{"index":6,"key":"source"}]', '[{"index":0,"key":"email"},{"index":1,"key":"nickname"},{"index":2,"key":"salesorderno"},{"index":3,"key":"orderingdate"},{"index":4,"key":"statusname"},{"index":5,"key":"distypename"},{"index":6,"key":"warehousename"},{"index":7,"key":"shopname"},{"index":8,"key":"platformorderno"},{"index":9,"key":"tradeno"},{"index":10,"key":"receiver"},{"index":11,"key":"tel"},{"index":12,"key":"idcard"},{"index":13,"key":"address"},{"index":14,"key":"sku"},{"index":16,"key":"productname"},{"index":16,"key":"qty"},{"index":17,"key":"realprice"},{"index":18,"key":"shopamountnum"},{"index":19,"key":"bbcpaynum"},{"index":20,"key":"freight"},{"index":21,"key":"remark"}]', '[{"index":0,"rowName":"客户账号"},{"index":1,"rowName":"客户名称"},{"index":2,"rowName":"订单编号"},{"index":3,"rowName":"下单时间"},{"index":4,"rowName":"订单状态"},{"index":5,"rowName":"分销商类型"},{"index":6,"rowName":"发货仓库"},{"index":7,"rowName":"平台单号"},{"index":8,"rowName":"交易号"},{"index":9,"rowName":"收货人姓名"},{"index":10,"rowName":"收货人电话"},{"index":11,"rowName":"收货人身份证号"},{"index":12,"rowName":"收货人地址"},{"index":13,"rowName":"商品编号"},{"index":14,"rowName":"商品名称"},{"index":15,"rowName":"真实售价"},{"index":16,"rowName":"商品QTY"},{"index":17,"rowName":"店铺实收金额"},{"index":18,"rowName":"BBC付款金额"},{"index":19,"rowName":"分销平台运费"},{"index":20,"rowName":"订单备注"}]', '销售订单', '销售订单.xls', null, null, null);
INSERT INTO "public"."t_export_model" VALUES ('4', 'exportapply', '[{"index":0,"key":"applyType"},{"index":1,"key":"receiptMode"},{"index":2,"key":"time"},{"index":3,"key":"search"},{"index":4,"key":"auditState"},{"index":5,"key":"reviewState"}]', '[{"index":0,"key":"email"},{"index":1,"key":"nickname"},{"index":2,"key":"receiptname"},{"index":3,"key":"receiptcard"},{"index":4,"key":"transfercard"},{"index":5,"key":"cardname"},{"index":6,"key":"transfernumber"},{"index":7,"key":"transtime"},{"index":8,"key":"actualtime"},{"index":9,"key":"transferamount"},{"index":10,"key":"actualamount"},{"index":11,"key":"screenshoturl"},{"index":12,"key":"applyman"},{"index":13,"key":"applyremark"},{"index":14,"key":"auditremark"},{"index":16,"key":"reauditremark"},{"index":16,"key":"auditstatename"},{"index":17,"key":"reviewstatename"}]', '[{"index":0,"rowName":"用户名"},{"index":1,"rowName":"昵称"},{"index":2,"rowName":"收款方"},{"index":3,"rowName":"收款账户"},{"index":4,"rowName":"付款账户"},{"index":5,"rowName":"账户开户名"},{"index":6,"rowName":"付款流水号"},{"index":7,"rowName":"实际付款日期"},{"index":8,"rowName":"实际到账日期"},{"index":9,"rowName":"付款金额"},{"index":10,"rowName":"实际到账金额"},{"index":11,"rowName":"付款截图"},{"index":12,"rowName":"录入人"},{"index":13,"rowName":"充值备注"},{"index":14,"rowName":"初审备注"},{"index":15,"rowName":"复审备注"},{"index":16,"rowName":"初审状态"},{"index":17,"rowName":"复审状态"}]', '充值记录', '充值记录表.xls', null, null, null);
INSERT INTO "public"."t_export_model" VALUES ('5', 'exportbillfront', '[{"index":0,"key":"purpose"},{"index":1,"key":"time"},{"index":2,"key":"account"}]', '[{"index":0,"key":"sourcecard"},{"index":1,"key":"purposename"},{"index":2,"key":"paymenttype"},{"index":3,"key":"amount"},{"index":4,"key":"balance"},{"index":5,"key":"creditbalance"},{"index":6,"key":"createdate"},{"index":7,"key":"serialnum"}]', '[{"index":0,"rowName":"交易账号"},{"index":1,"rowName":"操作类型"},{"index":2,"rowName":"交易途径"},{"index":3,"rowName":"金额"},{"index":4,"rowName":"账户余额"},{"index":5,"rowName":"额度余额"},{"index":6,"rowName":"交易时间"},{"index":7,"rowName":"采购单号/流水号"}]', '交易记录', '交易记录表.xls', null, null, null);
INSERT INTO "public"."t_export_model" VALUES ('6', 'export_sales_volume', '[{"index":0,"key":"beginDate"},{"index":1,"key":"endDate"},{"index":2,"key":"searchText"},{"index":3,"key":"cates"},{"index":4,"key":"typeId"},{"index":5,"key":"status"}]', '[{"index":0,"key":"out_sku"},{"index":1,"key":"out_brand"},{"index":2,"key":"out_interbarcode"},{"index":3,"key":"out_packqty"},{"index":4,"key":"out_title"},{"index":5,"key":"out_arrive_ware_price"},{"index":6,"key":"out_salesvolume"}]', '[{"index":0,"rowName":"SKU"},{"index":1,"rowName":"品牌"},{"index":2,"rowName":"国际条形码"},{"index":3,"rowName":"箱规"},{"index":4,"rowName":"商品名称"},{"index":5,"rowName":"到仓价"},{"index":6,"rowName":"销售数量"}]', '商品销量表', '商品销量表.xls', '[{"index":0,"width":3000},{"index":1,"width":4000},{"index":2,"width":6000},{"index":3,"width":2000},{"index":4,"width":7000},{"index":5,"width":5000},{"index":6,"width":4000}]', null, null);

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table t_export_model
-- ----------------------------
ALTER TABLE "public"."t_export_model" ADD PRIMARY KEY ("id");
