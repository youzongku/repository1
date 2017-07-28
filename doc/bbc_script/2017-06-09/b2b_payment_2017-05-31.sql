--易极付余额支付配置
INSERT INTO t_yiji_config 
(yiji_url, post_protocol, get_protocol, trade_service, version, partner_id, sign_type, return_url, notify_url, secret_key, create_user, create_date, single_pay_service, single_pay_version, mark) 
VALUES 
(null,null, null, null, null, '20160427020011206988', null, null, null, 'ac230fc0132c1fc1f2d993296be4f5b0', 'luwj', now(), null, null, 'yjf_payer'),
('https://api.yiji.com', 'httpPost', 'httpGet', 'fastPayTradeMergePay', '2.0', '20160427020011207606', 'MD5', 'http://bbc.tomtop.hk/payment/yijipay/syncnotify', 'http://bbc.tomtop.hk/pament/yijipay/asynnotify', '4f864e41d0c2dd937bcca6d168f07f92', 'luwj', now(), NULL, NULL, 'yjf_payee');


--合并支付新增字段
ALTER TABLE t_yijifu_merge_result ADD COLUMN trade_no VARCHAR(64);

COMMENT ON COLUMN t_yijifu_merge_result.trade_no IS '交易号';

ALTER TABLE t_yijifu_merge_result ADD COLUMN merch_order_no VARCHAR(64);

COMMENT ON COLUMN t_yijifu_merge_result.merch_order_no IS '订单号';

ALTER TABLE t_yijifu_merge_result ADD COLUMN creat_trade_result TEXT;

COMMENT ON COLUMN t_yijifu_merge_result.creat_trade_result IS '支付详情';

ALTER TABLE t_yijifu_merge_result ADD COLUMN creat_result VARCHAR(16);

COMMENT ON COLUMN t_yijifu_merge_result.creat_result IS '支付结果';

ALTER TABLE t_yijifu_merge_result ADD COLUMN trade_amount VARCHAR(16);

COMMENT ON COLUMN t_yijifu_merge_result.trade_amount IS '支付金额';

--已经实名认证通过的实名信息表
CREATE TABLE t_checked_realname (
id SERIAL not null,
real_name varchar(32),
id_card varchar(32),
create_date timestamp(6),
PRIMARY KEY (id)
);

ALTER TABLE t_checked_realname OWNER TO tomtop;

COMMENT ON TABLE t_checked_realname IS '已实名认证通过的实名信息';

COMMENT ON COLUMN t_checked_realname.id IS 'id';

COMMENT ON COLUMN t_checked_realname.real_name IS '姓名';

COMMENT ON COLUMN t_checked_realname.id_card IS '身份证号';