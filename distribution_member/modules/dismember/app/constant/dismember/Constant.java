package constant.dismember;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author xu_shengen
 *
 */
public class Constant {

	public static final String PLUS = "+";
	public static final String MINUS = "-";

	/************
	 * 账期状态码常量 add by zbc start
	 ******************************************************/
	/** 0未生效 */
	public static final int AP_NOT_START = 0;
	/** 1可使用 */
	public static final int AP_AVAILABLE = 1;
	/** 2待还款 */
	public static final int AP_FOR_REFUND = 2;
	/** 3逾期 */
	public static final int AP_OVERDUE = 3;
	/** 4禁用中 */
	public static final int AP_DISABLE_THE = 4;
	/** 5已完结 */
	public static final int AP_FINISHED = 5;
	/************
	 * 账期状态码常量 add by zbc end
	 ******************************************************/

	/** 0待初审 */
	public static final Integer AUDIT_PENDING = 0; 
	/** 1审核不通过 改为拒绝 */
	public static final Integer AUDIT_NOT_PASS = 1;
	/** 2审核通过 */
	public static final Integer AUDIT_PASS = 2;
	/** 3待确认——审核异常，如收到金额与申请金额不一致时使用该状态 */
	public static final Integer AUDIT_FAIL = 3;
	/** 4待复审 */
	public static final Integer AUDIT_REVIEW = 4;

	/** 100在线支付状态，未付款 */
	public static final Integer APPLY_ONLINE_UNPAID = 100;
	/** 101在线支付状态，已付款 */
	public static final Integer APPLY_ONLINE_PAID = 101;

	/** 1充值 */
	public static final String RECHARGE = "充值";// 1
	/** 2提现 */
	public static final String WITHDRAW_CASH = "提现";// 2
	/** 3采购支付 */
	public static final String PURCHASE_PAYMENT = "采购支付";// 3
	/** 4退款 */
	public static final String REFUND = "退款";// 4
	/** 5运费支付 */
	public static final String FREIGHT = "运费支付";// 5
	/** 6采购支付含运费 */
	public static final String PURCHASE_PAYMENT_FREIGHT = "采购支付含运费";// 6
	/** 7余额核销 */
	public static final String BALANCE_REDUCE = "余额核销";// 7
	
	/** 0未使用 */
	public static final Integer NOT_USED = 0;// 未使用
	/** 1已使用 */
	public static final Integer HAS_BEEN_USED = 1;// 已使用
	/** 2未激活 */
	public static final Integer NOT_ACTIVE = 2;// 未激活
	/** 3已作废 */
	public static final Integer HAVE_USERD = 3;// 已作废
	/** 4已过期 */
	public static final Integer HAVE_EXPIRED = 4;// 已过期
	
	/** 后台用户初始密码 */
	public static final String ADMINPWD = "aa8888";

	/** 邮件类型smtp */
	public static final String EMAIL_SMTP = "smtp";
	/** 邮件类型gmail */
	public static final String EMAIL_GMAIL = "gmail";

	// 邮件模板类型
	public static final String FIND_PASSWORD = "FindPassword";
	public static final String ACTIVATE = "Activate";
	public static final String CHANGETEL = "ChangeTel";
	public static final String RESET_PAY_PASSWORD = "resetPayPassword";
	public static final String BIND_ACCOUNT_NO = "bindAccountNo";

	// 配置短信
	public static final String SEND_MSG = "sendMsg";
	public static final String BIND_CARD = "bindCard";
	public static final String SUCCESS_REGISTRATION = "successRegistration";
	public static final String CODE = "code";
	public static final String SMS_RECHARGE = "recharge";
	public static final String AP_REMINDER = "apReminder";

	/** 线下门店类型 */
	public static Map<Integer, String> STORETYPE = Maps.newHashMap();
	public static final String SHOP_NO_SEQ = "SHOP_NO_SEQ";
	/** 操作类型MAP */
	public static Map<String, String> STATE_MAP = Maps.newHashMap();

	/** 交易记录导出表格列头 */
	public static Map<String, String> EXPORT_BILL_MAP = Maps.newHashMap();

	/** 后台导出用户列头 */
	public static Map<String, String> EXPORT_USER_MAP = Maps.newHashMap();

	/** 后台导出用戶額度列头 */
	public static Map<String, String> EXPORT_CREDIT_MAP = Maps.newHashMap();

	/** 后台导出申请 */
	public static Map<String, String> EXPORT_APPLY_MAP = Maps.newHashMap();

	/** 后台线上充值到处申请猎头 */
	public static Map<String, String> EXPORT_ONLINE_APPLY_MAP = Maps.newHashMap();

	/** 后台导出所有用户及其等级信息 */
	public static Map<String, String> EXPORT_USER_RANK_MAP = Maps.newHashMap();

	/** 后台导出报价单 */
	public static Map<String, String> EXPORT_QUOTATION_MAP = Maps.newHashMap();

	/** 申请状态 */
	public static Map<Integer, String> APPLY_STATE_MAP = Maps.newHashMap();

	/** 后台导出优惠码 */
	public static Map<String, String> EXPORT_COUPONS_MAP = Maps.newHashMap();
	/** 优惠码状态 */
	public static Map<Integer, String> COUPONS_STATE_MAP = Maps.newHashMap();
	/** 活动状态 */
	public static Map<Integer, String> ACTIVE_STATE_MAP = Maps.newHashMap();
	/** 客户订单状态，用于展示 */
	public static Map<Integer, String> SALES_STATE_MAP = Maps.newHashMap();
	/** 采购单状态，用于展示 */
	public static Map<Integer, String> PURCHASE_STATE_MAP = Maps.newHashMap();

	/** 后台导出提现申请 */
	public static Map<String, String> EXPORT_WITHDRAW_APPLY_MAP = Maps.newHashMap();

	/** 注册申请上传的文件用途 */
	public static Map<String, String> REGISTER_FUNCITON_FILE = Maps.newHashMap();

	/** 账期状态描述集合 */
	public static Map<Integer, String> ACCOUNT_PERIOD_STATU_MAP = Maps.newHashMap();
	
	/** 用户归属描述集合 */
	public static Map<Integer,String> USER_ATTR_TYPE_MAP = Maps.newHashMap();

	//客户编码相关 start
	public static final String ONLINE = "8";//线上

	public static final String OFFLINE = "9";//线上

	public static final String DEALER = "01";//经销商
	public static final String KA = "02";//经销商
	public static final String IMPORT = "03";//经销商
	public static final String ECOMMERCE = "04";//电商
	public static final String VIP = "05";//VIP

	public static final String ECOMMERCE_CHANNEL = "000000000";//电商渠道
	public static final String VIP_CHANNEL = "111111111";//VIP渠道
	//客户编码相关 end

	/*********** 用户归属常量 start ***************************/
	
	public static final int USER_ATTR_TYPE_ONLINE = 1;

	public static final int USER_ATTR_TYPE_OFFLINE = 2;	
	
	/***********  用户归属常量 end   **************************/	
	
	static {
		ACCOUNT_PERIOD_STATU_MAP.put(AP_NOT_START, "未生效");
		ACCOUNT_PERIOD_STATU_MAP.put(AP_AVAILABLE, "可使用");
		ACCOUNT_PERIOD_STATU_MAP.put(AP_FOR_REFUND, "待还款");
		ACCOUNT_PERIOD_STATU_MAP.put(AP_OVERDUE, "已逾期");
		ACCOUNT_PERIOD_STATU_MAP.put(AP_DISABLE_THE, "禁用中");
		ACCOUNT_PERIOD_STATU_MAP.put(AP_FINISHED, "已完结");

		PURCHASE_STATE_MAP.put(0, "待付款");
		PURCHASE_STATE_MAP.put(1, "已付款");
		PURCHASE_STATE_MAP.put(2, "已取消");
		PURCHASE_STATE_MAP.put(3, "已失效");

		SALES_STATE_MAP.put(1, "待付款");
		SALES_STATE_MAP.put(2, "待确认");
		SALES_STATE_MAP.put(3, "待客服审核");
		SALES_STATE_MAP.put(4, "审核不通过");
		SALES_STATE_MAP.put(5, "已取消");
		SALES_STATE_MAP.put(6, "审核通过");
		SALES_STATE_MAP.put(7, "待发货");
		SALES_STATE_MAP.put(8, "发货失败");
		SALES_STATE_MAP.put(9, "已发货");
		SALES_STATE_MAP.put(10, "已收货");
		SALES_STATE_MAP.put(100, "售后待审核");
		SALES_STATE_MAP.put(101, "售后审核通过");
		SALES_STATE_MAP.put(102, "售后审核不通过");
		SALES_STATE_MAP.put(103, "待支付运费");
		SALES_STATE_MAP.put(104, "处理中");
		SALES_STATE_MAP.put(105, "已发货");
		SALES_STATE_MAP.put(106, "已完成");
		SALES_STATE_MAP.put(107, "订单挂起（存疑的状态）");
		SALES_STATE_MAP.put(108, "已退款");
		SALES_STATE_MAP.put(20, "已关闭");

		COUPONS_STATE_MAP.put(NOT_USED, "未使用");
		COUPONS_STATE_MAP.put(HAS_BEEN_USED, "已使用");
		COUPONS_STATE_MAP.put(NOT_ACTIVE, "未激活");
		COUPONS_STATE_MAP.put(HAVE_USERD, "已作废");
		COUPONS_STATE_MAP.put(HAVE_EXPIRED, "已过期");

		ACTIVE_STATE_MAP.put(NOT_ACTIVE, "未激活");
		ACTIVE_STATE_MAP.put(NOT_USED, "可使用");
		ACTIVE_STATE_MAP.put(HAS_BEEN_USED, "已过期");

		EXPORT_COUPONS_MAP.put("couponsNo", "优惠编号");
		EXPORT_COUPONS_MAP.put("istatus", "使用状态");
		EXPORT_COUPONS_MAP.put("user", "使用人");
		EXPORT_COUPONS_MAP.put("usageTimeStr", "使用时间");
		EXPORT_COUPONS_MAP.put("orderNo", "订单编号");
		// EXPORT__COUPONS_MAP.put("orderStatus", "订单状态");
		EXPORT_COUPONS_MAP.put("orderState", "订单状态");
		EXPORT_COUPONS_MAP.put("orderAmount", "订单金额");
		EXPORT_COUPONS_MAP.put("actuallyPaid", "实际支付金额");

		STATE_MAP.put("", "");
		STATE_MAP.put("1", RECHARGE);
		STATE_MAP.put("2", WITHDRAW_CASH);
		STATE_MAP.put("3", PURCHASE_PAYMENT);
		STATE_MAP.put("4", REFUND);
		STATE_MAP.put("5", FREIGHT);
		STATE_MAP.put("6", PURCHASE_PAYMENT_FREIGHT);
		STATE_MAP.put("7", BALANCE_REDUCE);

		EXPORT_BILL_MAP.put("email", "分销商");
		EXPORT_BILL_MAP.put("nickName", "名称");
		EXPORT_BILL_MAP.put("salesmanErp", "业务员");
		EXPORT_BILL_MAP.put("sourceCard", "交易账号");
		EXPORT_BILL_MAP.put("purpose", "操作类型");
		EXPORT_BILL_MAP.put("paymentType", "交易途径");
		EXPORT_BILL_MAP.put("serialNumber", "采购单号/流水号");
		EXPORT_BILL_MAP.put("amount", "金额");
		EXPORT_BILL_MAP.put("balance", "账户余额");
		EXPORT_BILL_MAP.put("creditLimitBalance", "额度余额");
		/* EXPORT_BILL_MAP.put("remark", "操作描述"); */
		EXPORT_BILL_MAP.put("create", "交易时间");

		EXPORT_USER_MAP.put("nick", "昵称");
		EXPORT_USER_MAP.put("telphone", "手机号");
		EXPORT_USER_MAP.put("loginName", "分销商账号");
		EXPORT_USER_MAP.put("realName", "姓名");
		EXPORT_USER_MAP.put("createTime", "注册时间");
		// EXPORT_USER_MAP.put("login", "最后登录时间");
		// EXPORT_USER_MAP.put("comsumerTypeName", "分销商类型");
		// EXPORT_USER_MAP.put("rankName", "等级");
		// EXPORT_USER_MAP.put("customizeDiscount", "定制折扣");
		// EXPORT_USER_MAP.put("discount", "等级折扣");
		EXPORT_USER_MAP.put("registerInviteCode", "注册邀请码");
		EXPORT_USER_MAP.put("selfInviteCode", "用户邀请码");
		EXPORT_USER_MAP.put("salesmanErp", "业务员");

		APPLY_STATE_MAP.put(AUDIT_PENDING, "待初审");
		/* APPLY_STATE_MAP.put(AUDIT_NOT_PASS, "审核不通过"); */
		APPLY_STATE_MAP.put(AUDIT_NOT_PASS, "拒绝");
		APPLY_STATE_MAP.put(AUDIT_PASS, "审核通过");
		APPLY_STATE_MAP.put(AUDIT_FAIL, "审核异常");
		APPLY_STATE_MAP.put(AUDIT_REVIEW, "待复审");
		APPLY_STATE_MAP.put(APPLY_ONLINE_UNPAID, "未支付");
		APPLY_STATE_MAP.put(APPLY_ONLINE_PAID, "支付成功");

		// 线下导出
		EXPORT_APPLY_MAP.put("id", "ID");
		EXPORT_APPLY_MAP.put("email", "用户名");
		EXPORT_APPLY_MAP.put("receiptName", "收款方");
		EXPORT_APPLY_MAP.put("receiptCard", "收款账户");
		EXPORT_APPLY_MAP.put("transferCard", "付款账户");
		EXPORT_APPLY_MAP.put("name", "账户开户名");
		EXPORT_APPLY_MAP.put("transferNumber", "付款流水号");
		EXPORT_APPLY_MAP.put("transTime", "实际付款日期");
		EXPORT_APPLY_MAP.put("actualTime", "实际到账日期");
		EXPORT_APPLY_MAP.put("transferAmount", "付款金额");
		EXPORT_APPLY_MAP.put("actualAmount", "实际到账金额");
		EXPORT_APPLY_MAP.put("screenshotUrl", "付款截图");
		EXPORT_APPLY_MAP.put("applyMan", "录入人");
		EXPORT_APPLY_MAP.put("applyRemark", "充值备注");
		EXPORT_APPLY_MAP.put("auditRemark", "初审备注");
		EXPORT_APPLY_MAP.put("reAuditRemark", "复审备注");
		EXPORT_APPLY_MAP.put("auditState", "初审状态");
		EXPORT_APPLY_MAP.put("reviewState", "复审状态");

		// 线上导出
		EXPORT_ONLINE_APPLY_MAP.put("id", "ID");
		EXPORT_ONLINE_APPLY_MAP.put("email", "用户名");
		EXPORT_ONLINE_APPLY_MAP.put("transferType", "付款方式");
		EXPORT_ONLINE_APPLY_MAP.put("transferNumber", "付款流水号");
		EXPORT_ONLINE_APPLY_MAP.put("transTime", "实际付款日期");
		EXPORT_ONLINE_APPLY_MAP.put("actualAmount", "付款金额");
		EXPORT_ONLINE_APPLY_MAP.put("auditState", "付款状态");

		EXPORT_USER_RANK_MAP.put("nick", "昵称");
		EXPORT_USER_RANK_MAP.put("telphone", "手机号");
		EXPORT_USER_RANK_MAP.put("loginName", "邮箱");
		EXPORT_USER_RANK_MAP.put("rankName", "等级");
		EXPORT_USER_RANK_MAP.put("customizeDiscount", "定制折扣");
		EXPORT_USER_RANK_MAP.put("discount", "等级折扣");

		EXPORT_QUOTATION_MAP.put("cname", "商品分类");
		EXPORT_QUOTATION_MAP.put("brand", "商品品牌");
		EXPORT_QUOTATION_MAP.put("csku", "SKU");
		EXPORT_QUOTATION_MAP.put("interBarCode", "国际条码");
		EXPORT_QUOTATION_MAP.put("ctitle", "商品名称");
		EXPORT_QUOTATION_MAP.put("disPrice", "分销价");
		EXPORT_QUOTATION_MAP.put("localPrice", "市场价");
		EXPORT_QUOTATION_MAP.put("qty", "采购数量");
		EXPORT_QUOTATION_MAP.put("batchNumber", "起批量");
		EXPORT_QUOTATION_MAP.put("packageType", "包装种类");
		EXPORT_QUOTATION_MAP.put("originCountry", "原产地");
		EXPORT_QUOTATION_MAP.put("plugType", "规格");
		EXPORT_QUOTATION_MAP.put("warehouseName", "所属仓");
		EXPORT_QUOTATION_MAP.put("productEnterprise", "生产厂家");
		EXPORT_QUOTATION_MAP.put("componentContent", "成分含量");
		EXPORT_QUOTATION_MAP.put("expirationDays", "保质期(月)");
		EXPORT_QUOTATION_MAP.put("stock", "库存");
		EXPORT_QUOTATION_MAP.put("packQty", "箱规");

		EXPORT_CREDIT_MAP.put("comsumerType", "分销商类型");
		EXPORT_CREDIT_MAP.put("userEmail", "用户名");
		EXPORT_CREDIT_MAP.put("userName", "姓名");
		EXPORT_CREDIT_MAP.put("tel", "手机号");
		EXPORT_CREDIT_MAP.put("creditLimit", "信用额度");
		EXPORT_CREDIT_MAP.put("usedAmount", "已使用额度");
		EXPORT_CREDIT_MAP.put("createuser", "责任人");
		EXPORT_CREDIT_MAP.put("limitState", "额度状态");
		EXPORT_CREDIT_MAP.put("isFinished", "是否还款");
		EXPORT_CREDIT_MAP.put("startTime", "开始时间");
		EXPORT_CREDIT_MAP.put("endTime", "失效时间");
		EXPORT_CREDIT_MAP.put("redit", "额度类型");

		EXPORT_WITHDRAW_APPLY_MAP.put("onlineApplyNo", "提现单号");
		EXPORT_WITHDRAW_APPLY_MAP.put("distributorEmail", "用户名");
		EXPORT_WITHDRAW_APPLY_MAP.put("accountPC", "开户所在省/市");
		EXPORT_WITHDRAW_APPLY_MAP.put("accountUnit", "开户银行");
		EXPORT_WITHDRAW_APPLY_MAP.put("accountUser", "开户名");
		EXPORT_WITHDRAW_APPLY_MAP.put("withdrawAccountNo", "提现账户");
		EXPORT_WITHDRAW_APPLY_MAP.put("createDateStr", "申请时间");
		EXPORT_WITHDRAW_APPLY_MAP.put("updateDateStr", "审核时间");
		EXPORT_WITHDRAW_APPLY_MAP.put("transferNumber", "转账流水号");
		EXPORT_WITHDRAW_APPLY_MAP.put("withdrawAmount", "提现金额(元)");
		EXPORT_WITHDRAW_APPLY_MAP.put("transferAmount", "转账金额(元)");
		EXPORT_WITHDRAW_APPLY_MAP.put("counterFee", "手续费(元)");
		EXPORT_WITHDRAW_APPLY_MAP.put("auditReasons", "审核理由");
		EXPORT_WITHDRAW_APPLY_MAP.put("auditStateStr", "申请状态");
		EXPORT_WITHDRAW_APPLY_MAP.put("auditMark", "审核备注");

		REGISTER_FUNCITON_FILE.put("business-licence", "营业执照");
		REGISTER_FUNCITON_FILE.put("organization-code", "组织机构代码");
		REGISTER_FUNCITON_FILE.put("tax-licence", "税务登记证");
		REGISTER_FUNCITON_FILE.put("taxpayer-licence", "一般纳税人资格证");
		REGISTER_FUNCITON_FILE.put("food-licence", "食品流通许可证");
		REGISTER_FUNCITON_FILE.put("goods-licence", "收货授权书");

		STORETYPE.put(1, "A");
		STORETYPE.put(2, "B");
		STORETYPE.put(3, "C");
		
		USER_ATTR_TYPE_MAP.put(USER_ATTR_TYPE_ONLINE, "线上");
		USER_ATTR_TYPE_MAP.put(USER_ATTR_TYPE_OFFLINE, "线下");

	}

	/** 入口标识：后台用户 */
	public static final int LOGIN_FROM_MARK_BACK = 2;
	/** 入口标识：前台用户 */
	public static final int LOGIN_FROM_MARK_PERSONAL = 1;

	/*** 电话号码正则表达式 */
	public static final String REX_TELPHONE = "[1][3458][0-9]{9}";
	/*** 银行卡绑定标识：绑定 */
	public static final int FLAG_BIND_BANK = 1;
	/*** 银行卡绑定标识：解绑 */
	public static final int FLAG_UNBIND_BANK = 0;
	/*** 申请标识：提现 */
	public static final String APPLY_TYPE_WITHDRAW = "2";
	/*** 密码输入失败限制次数锁定 */
	public static final int INPUT_ERROR_NUM_TIMES_LOCK = 5;
	/*** 密码输入失败需验证码次数 */
	public static final int INPUT_ERROR_NUM_TIMES_CODE = 3;
	/*** 输入密码锁定时间 */
	public static final int PWS_INPUT_LOCK_TIME = 1;
	/*** 发送短信类型：付款密码设置 */
	public static final int SMS_TYPE_SET_PAY_PWD = 2;
	/*** 发送短信类型：手机用户注册 */
	public static final int SMS_TYPE_PHONE_REG = 3;
	/*** 发送短信类型：忘记密码 */
	public static final int SMS_TYPE_FIND_PWD = 4;
	/*** 发送短信类型：修改手机 */
	public static final int SMS_TYPE_CHANGE_PHONE = 5;
	
}
