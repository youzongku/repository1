package services.dismember.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.dismember.CreditDto;
import dto.dismember.ExportCreditDto;
import entity.dismember.CreditOperationRecord;
import entity.dismember.CustomerCredit;
import entity.dismember.DisCredit;
import entity.dismember.DisMember;
import mapper.dismember.CreditOperationRecordMapper;
import mapper.dismember.CustomerCreditMapper;
import mapper.dismember.DisCreditMapper;
import mapper.dismember.DisMemberMapper;
import play.Logger;
import play.libs.Json;
import services.dismember.ICreditService;
import vo.dismember.Page;

/**
 * @author hanfs 描述： 2016年4月25日
 */
public class CreditService implements ICreditService {

	@Inject
	private DisCreditMapper disCreditMapper;
	@Inject
	private DisMemberMapper disMemberMapper;
	@Inject
	private CreditOperationRecordMapper creditOperationRecordMapper;
	@Inject
	private CustomerCreditMapper creditConfing;

	@Override
	public Page<CreditDto> getCreditsByPage(Map<String, Object> params) {
		// 更新过期失效状态
		disCreditMapper.updateStatusForOverDate();
		//已失效，已用额度为0的条目更改为已还款
		disCreditMapper.updatestatusForisFinshed();
		Integer pageSize = params.get("pageSize") == null ? null : Integer.parseInt(params.get("pageSize")+"");
		Integer startNum = params.get("startNum") == null ? null : Integer.parseInt(params.get("startNum")+"");
		Integer currPage = params.get("currPage") == null ? null : Integer.parseInt(params.get("currPage")+"");
		params.put("pageSize", null);// 去除分页条件，对用户不分页，只进行用户的查询
		// 用户信用度列表
		List<CreditDto> creditDtos = new ArrayList<CreditDto>();
		List<DisMember> members = disMemberMapper.getMembersByPage(params);
		String email;
		List<DisCredit> creditsOfMember;
		CreditDto creditDto = null;
		for (DisMember disMember : members) {
			email = disMember.getEmail();
			params.put("email", email);
			// 按条件查询用户对应的信用额度，如果没有对应的信用额度则不展示该用户
			creditsOfMember = disCreditMapper.getCreditsByCondition(params);
			if (creditsOfMember != null && creditsOfMember.size() > 0) {
				creditDto = new CreditDto();
				creditDto.setMember(disMember);
				creditDto.setCredits(creditsOfMember);
				creditDtos.add(creditDto);
			}
		}
		int rows = creditDtos.size();
		// 数据分页
		if (rows > 0) {
			if (currPage * pageSize >= rows) {
				creditDtos = creditDtos.subList(startNum, rows);
			} else {
				creditDtos = creditDtos.subList(startNum, currPage * pageSize);
			}
		}
		return new Page<CreditDto>(currPage, pageSize, rows, creditDtos);
	}

	@Override
	public CreditDto getCreditInfo(Integer id) {
		CreditDto creditDto = new CreditDto();
		DisCredit credit = disCreditMapper.selectByPrimaryKey(id);
		if (credit != null && !Strings.isNullOrEmpty(credit.getEmail())) {
			DisMember member = new DisMember();
			member.setEmail(credit.getEmail());
			member = disMemberMapper.getMember(member);
			creditDto.setMember(member);
			creditDto.setCredits(Arrays.asList(new DisCredit[] { credit }));
		}
		return creditDto;
	}

	@Override
	public Map<String, Object> addCredit(DisCredit credit) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//过滤内部分销商不能添加永久额度，普通分销商不能添加额度
		if (Strings.isNullOrEmpty(credit.getEmail())) {
			resultMap.put("suc", false);
			resultMap.put("msg", "用户账户不能为空");
			return resultMap;
		}
		
		DisMember member = new DisMember();
		member.setEmail(credit.getEmail());
		member = disMemberMapper.getMember(member);
		CustomerCredit config = creditConfing.getCreditConfig(member);
		if(config != null){
			if(credit.getRedit().equals(1)&&!config.getHasShortCredit()){
				resultMap.put("suc", false);
				resultMap.put("msg", "添加额度失败，该类型分销商不能添加临时额度");
				return resultMap;
			}
			if(credit.getRedit().equals(2)&&!config.getHasLongCredit()){
				resultMap.put("suc", false);
				resultMap.put("msg", "添加额度失败，该类型分销商不能添加永久额度");
				return resultMap;
			}
		}else{
			resultMap.put("suc", false);
			resultMap.put("msg", "未找到额度配置，不能添加信用额度");
			return resultMap;
		}
		
		// 额度类型
		Integer redit = credit.getRedit();
		// 邮箱
		String email = credit.getEmail();
		// 临时额度是否还清还款
		boolean isfinished = true;
		// 客户永久额度是否已经存在（默认否 标示不存在）
		boolean isExist = false;
		// 临时额度，检查是否未还款
		DisCredit search = null;
		if (redit != null && redit.equals(1)) {
			search = new DisCredit();
			search.setEmail(email);
			search.setRedit(redit);
			search.setIsFinished(false);
			DisCredit resultCredit = disCreditMapper.getDisCredit(search, 1);
			if (resultCredit != null) {
				isfinished = false;
			}
		} else {// 用户是否已经存在永久额度
			search = new DisCredit();
			search.setEmail(email);
			search.setRedit(redit);
			DisCredit resultCredit = disCreditMapper.getDisCredit(search, null);
			if (resultCredit != null) {
				isExist = true;
			}
		}
		
		// 不存在且已还款则添加额度
		if (!isExist && isfinished) {
			int result = disCreditMapper.insertSelective(credit);
			// 添加操作记录
			CreditOperationRecord record = new CreditOperationRecord();
			String comments = credit.getCreateuser() + "添加" + (redit == 1 ? "临时额度" : "永久额度")
					+ (result > 0 ? "成功,额度值为" : "失败,额度值为") + credit.getCreditLimit();
			record.setComments(comments);
			record.setOperatorEmail(credit.getCreateuser());
			record.setOperatorType(redit);
			record.setOperatorResult(result > 0 ? 1 : 0);
			record.setUserEmail(credit.getEmail());
			int re = creditOperationRecordMapper.insertSelective(record);
			Logger.info("添加操作日志结果：" + (re > 0 ? "成功" : "失败"));
			resultMap.put("suc", result > 0 ? true : false);
			resultMap.put("msg", result > 0 ? "添加额度成功" : "添加额度失败");
			return resultMap;
		}
		
		resultMap.put("suc", false);
		if (isExist) {
			resultMap.put("msg", "客户已存在永久额度，不可再次添加");
		}
		if (!isfinished) {
			resultMap.put("msg", "客户存在未还清临时额度，不可再次添加");
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> updateCredit(String currEmail, DisCredit credit) {
		DisCredit originCredit = disCreditMapper.selectByPrimaryKey(credit.getId());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Logger.info("originCredit>>>>>>>>>>>>>>>"+Json.toJson(originCredit));
		if(originCredit == null){
			resultMap.put("suc", false);
			resultMap.put("msg", "没有查询到指定用户");
			return resultMap;
		}
		
		DisCredit search = new  DisCredit();
		Integer redit = originCredit.getRedit();
		search.setId(originCredit.getId());
		search.setCreditLimit(credit.getCreditLimit());
		search.setUpdatedate(new Date());
		int result = disCreditMapper.updateByPrimaryKeySelective(search);
		// 添加操作记录
		CreditOperationRecord record = new CreditOperationRecord();
		String comments = currEmail + "修改" + (redit == 1 ? "临时额度" : "永久额度") + (result > 0 ? "成功,修改之前额度值为" : "失败,修改之前额度值为")
				+ originCredit.getCreditLimit();
		record.setComments(comments);
		record.setOperatorEmail(currEmail);
		record.setOperatorType(redit);
		record.setOperatorResult(result > 0 ? 1 : 0);
		record.setUserEmail(originCredit.getEmail());
		int re = creditOperationRecordMapper.insertSelective(record);
		Logger.info("添加操作日志结果：" + (re > 0 ? "成功" : "失败")+re);
		resultMap.put("suc", result > 0 ? true : false);
		resultMap.put("msg", result > 0 ? "修改额度成功" : "修改额度失败");
		return resultMap;
	}
	
	@Override
	public Map<String, Object> changeActivated(String currEmail, DisCredit credit) {
		DisCredit originCredit = disCreditMapper.selectByPrimaryKey(credit.getId());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Logger.info("originCredit>>>>>>>>>>>>>>>"+Json.toJson(originCredit));
		if(originCredit == null){
			resultMap.put("suc", false);
			resultMap.put("msg", "没有查询到指定用户");
			return resultMap;
		}
		
		Integer redit = originCredit.getRedit();
		DisCredit search = new  DisCredit();
		Boolean flag = originCredit.getIsActivated();
		if(flag != null){
			search.setIsActivated(!flag);
		}
		search.setId(originCredit.getId());
		search.setUpdatedate(new Date());
		int result = disCreditMapper.updateByPrimaryKeySelective(search);
		// 添加操作记录
		CreditOperationRecord record = new CreditOperationRecord();
		String comments = currEmail +"用户之前状态为"+(originCredit.getIsActivated() == true ? "禁用" : "启用");
		record.setComments(comments);
		record.setOperatorEmail(currEmail);
		record.setOperatorType(redit);
		record.setOperatorResult(result > 0 ? 1 : 0);
		record.setUserEmail(originCredit.getEmail());
		int re = creditOperationRecordMapper.insertSelective(record);
		Logger.info("添加操作日志结果：" + (re > 0 ? "成功" : "失败")+re);
		resultMap.put("suc", result > 0 ? true : false);
		resultMap.put("msg", result > 0 ? "修改额度成功" : "修改额度失败");
		return resultMap;
	}
	
	@Override
	public List<ExportCreditDto> getExportCreditData(Map<String, Object> params) {
		List<ExportCreditDto> exportCreditList = new ArrayList<ExportCreditDto>();
		List<DisMember> members = disMemberMapper.getMembersByPage(params);
		for (DisMember disMember : members) {
			String email = disMember.getEmail();
			params.put("email", email);
			List<DisCredit> credits = disCreditMapper.getCreditsByCondition(params);
			if (!disMember.getComsumerType().equals(1) && credits != null && credits.size() > 0) {
				for (DisCredit disCredit : credits) {
					ExportCreditDto dto = new ExportCreditDto();
					dto.copyPropertyByCredit(disCredit);
					dto.copyPorpertyByMember(disMember);
					exportCreditList.add(dto);
				}
			}
		}
		return exportCreditList;
	}

	@Override
	public List<DisCredit> getDisCreditInfo(Map<String, Object> params) {
		List<DisCredit> credits = disCreditMapper.getCreditsByCondition(params);
		if(credits != null && credits.size() == 2 && credits.get(0).getRedit() == 2){
			Collections.reverse(credits);
			Logger.info("credits---------->"+Json.toJson(credits));
		}
		return credits;
	}

	@Override
	public boolean isRepay(JsonNode paramJson) {
		if (paramJson == null || paramJson.isNull()) {
			return false;
		}
		
		JsonNode emailJson = paramJson.get("email");
		if (emailJson == null) {
			return false;
		}

		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("email",emailJson.asText());
		List<DisCredit> creditsByEmail = disCreditMapper.getCreditsByCondition(paramMap);
		if (CollectionUtils.isEmpty(creditsByEmail)) {
			return true;
		}
		
		for (DisCredit credit : creditsByEmail) {
			//额度存在使用金额即代表未还款
			if(credit.getUsedAmount().compareTo(new java.math.BigDecimal(0))>0){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean delCreditAndRecordByEmail(JsonNode paramJson) {
		JsonNode emailJson = null;
		if (paramJson != null && (emailJson = paramJson.get("email")) != null) {
			//删除所有额度
			this.disCreditMapper.deleteByEmail(emailJson.asText());
			//删除所有额度操作记录
			this.creditOperationRecordMapper.deleteByEmail(emailJson.asText());
			return true;
		}
		
		return false;
	}

	@Override
	public String getCreditByEmail(String email) {
		ObjectNode result = Json.newObject();
		Map<String, Object> params = Maps.newHashMap();//查询信用额度的查询参数
		params.put("email", email);
		params.put("isFinished", false);
		List<DisCredit> credits = disCreditMapper.getCreditsByCondition(params);
		BigDecimal debt = BigDecimal.ZERO;
		if (credits != null){
			for (DisCredit credit : credits) {
				debt = debt.add(credit.getUsedAmount());
			}
		}
		result.put("suc", true);
		result.put("debt", debt.doubleValue());
		return result.toString();
	}

}
