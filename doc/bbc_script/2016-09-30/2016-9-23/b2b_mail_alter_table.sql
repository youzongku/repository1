ALTER TABLE "t_disemail_verify" ADD COLUMN "send_params" text;
COMMENT ON COLUMN "t_disemail_verify"."send_params" IS '发送邮件参数';