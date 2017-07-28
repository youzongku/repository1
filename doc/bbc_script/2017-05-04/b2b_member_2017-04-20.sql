CREATE TABLE "public"."t_ap_reminder_setting" (
"id" serial4,
"account" varchar(50) COLLATE "default",
"enable" bool DEFAULT false,
"days_ago" int4 DEFAULT 0,
"create_date" timestamp(6),
"create_user" varchar(255) COLLATE "default",
"last_update_date" timestamp(6),
"last_update_user" varchar(255) COLLATE "default",
PRIMARY KEY ("id")
);
COMMENT ON TABLE "public"."t_ap_reminder_setting" IS '账期短信提醒设置表';
COMMENT ON COLUMN "public"."t_ap_reminder_setting"."account" IS '分销商';
COMMENT ON COLUMN "public"."t_ap_reminder_setting"."enable" IS '是否开启短信提醒，默认false';
COMMENT ON COLUMN "public"."t_ap_reminder_setting"."days_ago" IS '提前几天短信提醒，默认为0';

CREATE TABLE "public"."t_ap_reminder_setting_logs" (
"id" serial4,
"ap_rs_id" int4,
"enable" bool,
"days_ago" int4 DEFAULT 0,
"set_suc" bool,
"create_date" timestamp(6),
"create_user" varchar(255) COLLATE "default",
PRIMARY KEY ("id")
);
COMMENT ON TABLE "public"."t_ap_reminder_setting_logs" IS '账期短信提醒设置操作日志表';
COMMENT ON COLUMN "public"."t_ap_reminder_setting_logs"."ap_rs_id" IS '账期短信提醒设置id';
COMMENT ON COLUMN "public"."t_ap_reminder_setting_logs"."enable" IS '是否开启短信提醒';
COMMENT ON COLUMN "public"."t_ap_reminder_setting_logs"."set_suc" IS '是否设置成功';
COMMENT ON COLUMN "public"."t_ap_reminder_setting_logs"."days_ago" IS '提前几天短信提醒';
COMMENT ON COLUMN "public"."t_ap_reminder_setting_logs"."create_date" IS '操作人';
COMMENT ON COLUMN "public"."t_ap_reminder_setting_logs"."create_user" IS '操作时间';


INSERT INTO t_email_template(ctype, ctitle, ccontent, ccreateuser, dcreatedate)
 VALUES('apReminder', '账期提醒功能', '【通淘国际】尊敬的客户您好，您的通淘供应链账期将于{{yyyyMMdd}}到期，为避免影响您的信用，请尽快处理。', 'reason', now())
