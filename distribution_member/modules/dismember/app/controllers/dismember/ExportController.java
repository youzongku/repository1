package controllers.dismember;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import dto.dismember.AdminDto;
import dto.dismember.ApplyDto;
import dto.dismember.WithdrawBalanceDto;
import entity.dismember.DisActive;
import entity.dismember.DisCoupons;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.base.utils.StringUtils;
import services.dismember.IActiveService;
import services.dismember.IApplyService;
import services.dismember.IDisBillService;
import services.dismember.IDisMemberService;
import services.dismember.IDisSalesmanService;
import services.dismember.ILoginService;
import utils.dismember.DateUtils;
import utils.dismember.ExportUtil;
import utils.dismember.HttpUtil;

public class ExportController extends Controller {

	@Inject
	private IDisBillService billService;

	@Inject
	private ILoginService loginService;

	@Inject
	private IApplyService applyService;

	@Inject
	private IDisMemberService memberService;
	
	@Inject
	private IActiveService activeService;
	
	@Inject
	private IDisSalesmanService salesManService;
	
	/**
	 * 前台：交易记录导出
	 * @author zbc
	 * @since 2017年3月16日 上午10:02:18
	 */
	public Result exportBills() throws UnsupportedEncodingException {
		if (!loginService.isLogin(1)) {
			return redirect(HttpUtil.getUrl() + "/personal/login.html");
		}
		Map<String, String[]> map = request().queryString();
		if (null == map) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("参数错误");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		if (null == header || header.length <= 0) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("列头不能为空。");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		response().setHeader("Content-disposition",
				"attachment;filename=" + new String("交易记录".getBytes(), "ISO8859-1") + ".xls");
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		return ok(billService.export(map,header,loginService.getLoginContext(1).getEmail(),null));
	}

	/**
	 * 后台：交易记录导出
	 * @author zbc
	 * @since 2017年3月16日 上午10:02:38
	 */
	public Result backExportBills() throws UnsupportedEncodingException {
		if (!loginService.isLogin(2)) {
			return redirect(HttpUtil.getUrl() + "/backstage/login.html");
		}
		Map<String, String[]> map = request().queryString();
		if (null == map) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("参数错误");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		if (null == header || header.length <= 0) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("列头不能为空。");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		response().setHeader("Content-disposition",
				"attachment;filename=" + new String("交易记录".getBytes(), "ISO8859-1") + ".xls");
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		return ok(billService.export(map,header,null,
				salesManService.relateAccounts(loginService.getLoginContext(Constant.LOGIN_FROM_MARK_BACK).getEmail())));
	}
	/**
	 * 导出用户
	 * 
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws IOException
	 */
	public Result exportUser() throws UnsupportedEncodingException {
		if (!loginService.isLogin(2)) {
			return redirect(HttpUtil.getHostUrl() + "/sysadmin");
		}
		Map<String, String[]> map = request().queryString();
		if (null == map) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("参数错误");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		if (null == header || header.length <= 0) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("列头不能为空。");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		
		Logger.info("获取到的headers为：");
		Stream.of(header).forEach(e->{
			Logger.info(e+",");
		});
		
		Map<String, Object> params = Maps.newHashMap();
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		response().setHeader("Content-disposition", "attachment;filename=" + new String("普通用户".getBytes(), "ISO8859-1") + ".xls");
		String sregdate = map.containsKey("sregdate") ? map.get("sregdate")[0] : "";
		String eregdate = map.containsKey("eregdate") ? map.get("eregdate")[0] : "";
		String slogdate = map.containsKey("slogdate") ? map.get("slogdate")[0] : "";
		String elogdate = map.containsKey("elogdate") ? map.get("elogdate")[0] : "";
		Integer rankId = map.containsKey("rank") && !Strings.isNullOrEmpty(map.get("rank")[0])
				? Integer.valueOf(map.get("rank")[0]) : null;
		eregdate = Strings.isNullOrEmpty(eregdate) ? eregdate
				: new DateTime(eregdate).plusDays(1).toString("yyyy-MM-dd");
		elogdate = Strings.isNullOrEmpty(elogdate) ? elogdate
				: new DateTime(elogdate).plusDays(1).toString("yyyy-MM-dd");
		params.put("roleId", 2);
		params.put("search", map.containsKey("search") ? map.get("search")[0] : "");
		params.put("sregdate", sregdate);
		params.put("eregdate", eregdate);
		params.put("slogdate", slogdate);
		params.put("elogdate", elogdate);
		params.put("rankId", rankId);
		Integer comsumerType = 0;
		if (map.containsKey("comsumerType")) {
			String comsumerTypeStr = utils.dismember.StringUtils.getString(map.get("comsumerType")[0], false);
			comsumerType = "".equals(comsumerTypeStr) ? 0 : Integer.valueOf(comsumerTypeStr);
		}
		params.put("comsumerType", comsumerType);
		List<AdminDto> dtos = memberService.getAdminsByPage(params).getList();
		String salesAccount = "";
		for (AdminDto dto : dtos) {//此字段要求要查询分销商出最终关联的业务员信息
			if (dto.getSalesmanErp() == null) {
				salesAccount = (String) memberService.getCustomerServiceAccount(dto.getEmail()).get("account");
				dto.setSalesmanErp(salesAccount);
			}
		}
		List<String> list = Lists.newArrayList();
		for (String head : header) {
			if (Constant.EXPORT_USER_MAP.containsKey(head)) {
				list.add(head);
			} 
		}
		String[] newHeader = new String[list.size()];
		newHeader = list.toArray(newHeader);
		Logger.info(loginService.getLoginContext(2).getEmail() + "导出用户，导出数据条数：" + dtos.size());
		return ok(ExportUtil.export("user.xls", newHeader, Constant.EXPORT_USER_MAP, dtos));
	}

	/**
	 * 导出充值申请
	 * 
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws IOException
	 */
	public Result exportApply() throws UnsupportedEncodingException {
		if (!loginService.isLogin(2)) {
			return redirect(HttpUtil.getHostUrl() + "/sysadmin");
		}
		Map<String, String[]> map = request().queryString();
		if (null == map) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("参数错误");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		if (null == header || header.length <= 0) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("列头不能为空。");
			result.put("suc", false);
			result.put("code", "3");
			return ok(Json.toJson(result));
		}
		
		Map<String, Object> param = Maps.newHashMap();
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		response().setHeader("Content-disposition", "attachment;filename=" + new String("充值申请".getBytes(), "ISO8859-1") + ".xls");
		// 初审状态 ---0:待审核,1:审核不通过,2:审核通过,3:待确认
		param.put("auditState", map.containsKey("auditState") && !StringUtils.isEmpty(map.get("auditState")[0])
				? Integer.parseInt(map.get("auditState")[0]) : null);
		// 复审状态 ---1:审核不通过,2:审核通过,3:待确认,4:待复审
		param.put("reviewState", map.containsKey("reviewState") && !StringUtils.isEmpty(map.get("reviewState")[0])
				? Integer.parseInt(map.get("reviewState")[0]) : null);
		// 取分销商填写的转账时间
		if (map.containsKey("time") && !StringUtils.isEmpty(map.get("time")[0])) {
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.MONTH, -Integer.parseInt(map.get("time")[0]));
			param.put("time", DateUtils.date2FullDateTimeString(ca.getTime()));
		}
		param.put("receiptMode", map.containsKey("receiptMode") && !StringUtils.isEmpty(map.get("receiptMode")[0])
				? Integer.parseInt(map.get("receiptMode")[0]) : null);
		param.put("search",
				map.containsKey("search") && !StringUtils.isEmpty(map.get("search")[0]) ? map.get("search")[0] : null);
		
		//在线充值
		if(map.get("isOnline")!=null){
			param.put("isOnline", 1);
		}

        param.put("applyType", map.containsKey("applyType") &&
            !Strings.isNullOrEmpty(map.get("applyType")[0]) ?
            map.get("applyType")[0] : null
        );

		List<ApplyDto> list = applyService.query(param);
		Logger.info(loginService.getLoginContext(2).getEmail() + "申请记录，导出数据条数：" + list.size());
		
		//在线充值
		if(map.get("isOnline")!=null){
			return ok(ExportUtil.export("applys.xls", header, Constant.EXPORT_ONLINE_APPLY_MAP, list));
		}
		
		//一般充值
		return ok(ExportUtil.export("applys.xls", header, Constant.EXPORT_APPLY_MAP, list));
	}

	/**
	 * 导出用户及其等级信息
	 * 
	 * @return
	 */
	public Result exportUserRank() {
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			return redirect(HttpUtil.getHostUrl() + "/sysadmin");
		}
		Map<String, String[]> map = request().queryString();
		if (map == null) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("code", 2);
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		if (null == header || header.length <= 0) {
			Logger.info("Excel表格标题不能为空");
			result.put("suc", false);
			result.put("code", "3");
			return ok(Json.toJson(result));
		}
		
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		response().setHeader("Content-disposition", "attachment;filename=user&level.xls");
		Integer roleId = map.containsKey("role") && !Strings.isNullOrEmpty(map.get("role")[0])
				? Integer.valueOf(map.get("role")[0]) : null;
		Integer rankId = map.containsKey("rank") && !Strings.isNullOrEmpty(map.get("rank")[0])
				? Integer.valueOf(map.get("rank")[0]) : null;
		String search = map.containsKey("search") ? map.get("search")[0] : null;
		Map<String, Object> params = Maps.newHashMap();
		params.put("roleId", roleId);
		params.put("rankId", rankId);
		params.put("search", search);
		List<AdminDto> dtos = memberService.getAdminsByPage(params).getList();
		// 将所有未定制折扣的折扣值置空
		for (AdminDto adminDto : dtos) {
			if (!adminDto.getIsCustomized()) {
				adminDto.setCustomizeDiscount("");
			} else {
				adminDto.setCustomizeDiscount(adminDto.getCustomizeDiscount() + "%");
			}
			adminDto.setDiscount(adminDto.getDiscount() + "%");
		}
		Logger.info("导出所有用户及其等级信息记录条数：{}", dtos.size());
		return ok(ExportUtil.export("user&level.xls", header, Constant.EXPORT_USER_RANK_MAP, dtos));
	}
	
	/**
	 * 优惠码导出
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public Result exportCoupons() throws UnsupportedEncodingException{
		Map<String, Object> result = Maps.newHashMap();
		if (!loginService.isLogin(2)) {
			return redirect(HttpUtil.getHostUrl() + "/sysadmin");
		}
		Map<String, String[]> map = request().queryString();
		if (map == null) {
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("code", 2);
			return ok(Json.toJson(result));
		}
		String[] header = map.get("header");
		if (null == header || header.length <= 0) {
			Logger.info("Excel表格标题不能为空");
			result.put("suc", false);
			result.put("code", "3");
			return ok(Json.toJson(result));
		}
		Logger.info("表头信息:[{}]",Arrays.toString(header));
		
		Map<String, Object> params = Maps.newHashMap();
		Integer activeId = map.containsKey("activeId") && !Strings.isNullOrEmpty(map.get("activeId")[0])
				? Integer.valueOf(map.get("activeId")[0]) : null;
		Integer status = map.containsKey("status") && !Strings.isNullOrEmpty(map.get("status")[0])
		? Integer.valueOf(map.get("status")[0]) : null;
		String usedStartDate = map.containsKey("usedStartDate") ? map.get("usedStartDate")[0] : null;
		String usedEndDate = map.containsKey("usedEndDate") ? map.get("usedEndDate")[0] : null;
		String seachSpan = map.containsKey("seachSpan") ? map.get("seachSpan")[0] : null;
		params.put("activeId", activeId);
		params.put("status",status);
		params.put("usedStartDate",usedStartDate);
		params.put("usedEndDate",usedEndDate);
		params.put("seachSpan",seachSpan);
		DisActive active  =activeService.getActive(activeId);
		String filename = active.getCouponsName();
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		response().setHeader("Content-disposition", "attachment;filename="+new String(filename.getBytes(),"ISO8859-1")+".xls");
		List<DisCoupons> list = activeService.queryCoupons(params);
		Logger.info(loginService.getLoginContext(2).getEmail() + "优惠码，导出数据条数：" + list.size());
		return ok(ExportUtil.export("active.xls", header, Constant.EXPORT_COUPONS_MAP, list));
	}

	/**
	 * 导出提现申请
	 * @Author LSL on 2016-09-22 12:06:25
	 */
	public Result exportWithdraw() throws UnsupportedEncodingException {
        if (!loginService.isLogin(2)) {
            return redirect(HttpUtil.getHostUrl() + "/sysadmin");
        }
        JSONObject result = new JSONObject();
        Map<String, String[]> paramMap = request().queryString();
        if (paramMap == null) {
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("code", 2);
            return ok(result.toString());
        }
        Logger.debug("exportWithdraw    paramMap----->" + Json.toJson(paramMap).toString());
        String[] header = paramMap.get("header");
        if (null == header || header.length == 0) {
            Logger.info("Excel表格标题不能为空");
            result.put("suc", false);
            result.put("code", "3");
            return ok(result.toString());
        }
        
        response().setContentType("application/vnd.ms-excel;charset=utf-8");
        response().setHeader("Content-disposition", "attachment;filename=" +
                new String("WithdrawApply".getBytes(),"ISO8859-1")+".xls");
        List<WithdrawBalanceDto> list = applyService.queryWithdrawRecord(paramMap);
        Logger.debug("queryWithdrawRecord    [list size]----->" + list.size());
        Logger.debug("queryWithdrawRecord    即将导出提现申请Excel......");
        return ok(ExportUtil.export("WithdrawApply.xls", header, Constant.EXPORT_WITHDRAW_APPLY_MAP, list));
	}

}