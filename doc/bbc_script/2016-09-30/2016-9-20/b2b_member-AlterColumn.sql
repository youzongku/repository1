INSERT INTO t_sequence (id, seq_name, current_value, increment_, remark)
VALUES (2, 'WITHDRAW_AMOUNT_NO', 1, 1, '提现单号');

ALTER TABLE t_dis_account ADD COLUMN frozen_amount NUMERIC(10,2);

COMMENT ON COLUMN t_dis_account.frozen_amount IS '暂冻结金额';

ALTER TABLE t_dis_withdraw_account ADD COLUMN is_bind INTEGER;

COMMENT ON COLUMN t_dis_withdraw_account.is_bind IS '0:解绑, 1:绑定';


INSERT INTO t_email_template (ctype, ctitle, ccontent, ccreateuser, dcreatedate)
VALUES('bindAccountNo' ,'绑定提现帐号-通淘供应链', '<p><!--StartFragment --></p>

<p style="margin-left: 440px;">
	<span style="font-family:微软雅黑;">
		<span style="color:#4A7CC7;">
			<span style="font-size:18px;">亲爱的&nbsp;</span>
		</span>
		<span style="font-size:14px;">{{toemail}}：</span>
	</span>
</p>

<p style="margin-left: 440px;"><br />
	<span style="font-family:微软雅黑;">
		<span style="font-size:14px;">您正在进行提现绑定银行卡操作。<span style="color:#FF0000;">请在两小时内点击一下链接进行身份验证。</span></span>
	</span>
</p>

<p style="margin-left: 440px;"><br />
	<span style="font-family:微软雅黑;">
		<span style="font-size:14px;">如非本人操作，请忽略。</span>
	</span><br />
	<span style="font-family:微软雅黑;">
		<span style="font-size:14px;">
			<img src="" />链接：
			<a href="{{url}}">
				<span style="color:#0000FF;"><ins>{{url}}</ins></span>
			</a><br /><br />
			如果链接不工作,请复制整个链接到浏览器地址栏回车Enter运行。
		</span>
	</span>
</p>

<p style="margin-left: 440px;"><br />
<span style="font-family:微软雅黑;"><span style="font-size:14px;">请勿直接回复该邮件，谢谢！</span></span></p>', 'reason', now());


INSERT INTO t_email_variable (ctype, cname, cremark, ccreateuser, dcreatedate)
VALUES('bindAccountNo', 'url', '绑定提现帐号地址', 'reason', now()),
('bindAccountNo', 'toemail', '收件人', 'reason', now())

ALTER TABLE t_dis_withdraw_account ADD COLUMN account_type INTEGER;

COMMENT ON COLUMN t_dis_withdraw_account.account_type IS '0:银行卡, 1:支付宝';

ALTER TABLE t_dis_withdraw_account ADD COLUMN account_province VARCHAR(50);

COMMENT ON COLUMN t_dis_withdraw_account.account_province IS '开户行所在省';

ALTER TABLE t_dis_withdraw_account ADD COLUMN account_city VARCHAR(50);

COMMENT ON COLUMN t_dis_withdraw_account.account_city IS '开户行所在市';