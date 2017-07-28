INSERT INTO "public"."t_email_template" ("ctype", "ctitle", "ccontent", "ccreateuser", "dcreatedate") VALUES ('bindCard', '绑定银行卡', '【通淘国际】亲爱的用户，您正在进行提现绑定银行卡操作。验证码是{{code}}，五分钟内有效', 'reason', '2017-04-11 17:46:20.34221');
INSERT INTO "public"."t_email_template" ("ctype", "ctitle", "ccontent", "ccreateuser", "dcreatedate") VALUES ('successRegistration', '成功注册', '【通淘国际】已为您成功注册分销账号！访问https://www.tomtop.com.cn来管理您的分销平台。用户名：{{email}}  密码：{{passWord}} ', 'reason', '2017-04-11 17:49:43.957425');
INSERT INTO "public"."t_email_template" ("ctype", "ctitle", "ccontent", "ccreateuser", "dcreatedate") VALUES ('code', '验证码', '【通淘国际】亲爱的用户，您的验证码是{{code}}，5分钟内有效。', 'reason', '2017-04-11 18:04:25.779457');
INSERT INTO "public"."t_email_template" ("ctype", "ctitle", "ccontent", "ccreateuser", "dcreatedate") VALUES ('recharge', '充值', '【通淘国际】尊敬的用户，近期Bbc后台网站上有充值申请需要您去{{msg}}', 'reason', '2017-04-11 18:08:13.695568');


INSERT INTO "public"."t_email_account" ("iwebsiteid", "ctype", "csmtphostname", "iserverport", "cusername", "cemail", "cpassword", "ccreateuser", "dcreatedate") VALUES ('1', 'sendMsg', 'http://sms.253.com/msg/', '25', 'N8870432', NULL, 'YPFyqC9G07cf80', 'reason', '2017-04-11 17:05:27');
