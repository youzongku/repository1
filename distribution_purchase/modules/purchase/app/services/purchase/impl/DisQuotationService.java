package services.purchase.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mapper.purchase.DisQuotationMapper;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.libs.Json;
import services.purchase.IDisQuotationService;
import services.purchase.IHttpService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.purchase.QuotationsItem;
import dto.purchase.ReturnMess;
import entity.purchase.DisQuotation;

/**
 * Created by luwj on 2016/3/4.
 */
public class DisQuotationService implements IDisQuotationService {

	@Inject private DisQuotationMapper disQuotationMapper;
	@Inject private IHttpService httpService;

	/**
	 * 列表
	 * 
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getRecord(JsonNode node) {
		QuotationsItem item = new QuotationsItem();
		ReturnMess returnMess = new ReturnMess("0", "");
		List<DisQuotation> quotations = null;
		int totalCount = 0;
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> result = mapper.convertValue(node, Map.class);
			quotations = disQuotationMapper.getRecord(result);
			totalCount = disQuotationMapper.getRecordCount(result);
		} catch (Exception e) {
			Logger.error("Exception:", e);
			returnMess = new ReturnMess("1", "系统异常");
		}
		item.setReturnMess(returnMess);
		item.setQuos(quotations);
		item.setTotalCount(totalCount);
		return Json.toJson(item).toString();
	}

	/**
	 * 生成记录
	 * 
	 * @param node
	 * @return
	 */
	public String saveRecord(JsonNode node) {
		DisQuotation disQuotation = new DisQuotation();
		String discountRate = "";
		String excelName = "";
		String madeUser = "";
		String disEmail = "";
		String remark = "";
		if (node.has("discountRate")) {
			discountRate = node.get("discountRate").asText();
		}
		if (node.has("excelName")) {
			excelName = node.get("excelName").asText();
		}
		if (node.has("madeUser")) {
			madeUser = node.get("madeUser").asText();
		}
		if (node.has("disEmail")) {
			disEmail = node.get("disEmail").asText();
		}
		if (node.has("remark")) {
			remark = node.get("remark").asText();// 1:关联 0： 取消关联
		}
		try {
			// 有id就更新
			if (node.has("id") && StringUtils.isNotBlank(node.get("id").asText())) {
				disQuotation = disQuotationMapper.selectByPrimaryKey(node.get("id").asInt());
				if (disQuotation == null) {
					return Json.toJson(new ReturnMess("1", "参数有误，数据不存在")).toString();
				}
				
				disQuotation.setDiscountRate(discountRate);
				disQuotation.setExcelName(excelName);
				disQuotation.setMadeUser(madeUser);
				disQuotation.setUpdateDate(new Date());
				
				if (StringUtils.isNotBlank(remark) && "1".equals(remark)) {// 关联
					JsonNode dismemberNode = httpService.getDismemberByEmail(disEmail);
					Logger.info("获取到的分销商为：{}",dismemberNode);
					if (!dismemberNode.get("suc").asBoolean()) {
						return Json.toJson(new ReturnMess("1", "关联的分销商ID是无效的!")).toString();
					}
					
					String realName = dismemberNode.get("result").get("realName").asText();
					disQuotation.setDisname(realName);
				} else if (disQuotation.getIsBuildOrder() && StringUtils.isNotBlank(remark) && "0".equals(remark)) {// 无法取消关联
					return Json.toJson(new ReturnMess("1", "已经生成订单的报价单不能取消分销商关联!")).toString();
				} 
				
				if (StringUtils.isNotBlank(remark) && "0".equals(remark)) {
					// 取消关联
					disQuotation.setDisname("");
				}
				
				disQuotation.setDisEmail(disEmail);
				disQuotation.setBindDisEmail(new Date());
				disQuotationMapper.updateByPrimaryKeySelective(disQuotation);
				return Json.toJson(new ReturnMess("0", String.valueOf(node.get("id").asInt()))).toString();
			}
			
			// 保存
			boolean parametersOk = node.has("discountRate") && node.has("excelName") && node.has("madeUser")
					&& node.has("header") && node.has("iidList");
			if (!parametersOk) {
				return Json.toJson(new ReturnMess("1", "传入参数有误")).toString();
			}
			
			disQuotation.setDiscountRate(discountRate);
			disQuotation.setExcelName(excelName);
			disQuotation.setReqBody(node.toString());// 请求参数
			disQuotation.setMadeUser(madeUser);
			disQuotation.setCreateDate(new Date());
			disQuotationMapper.insertSelective(disQuotation);
			return Json.toJson(new ReturnMess("0", String.valueOf(disQuotation.getId()))).toString();
		} catch (Exception e) {
			Logger.error("Exception:", e);
			return Json.toJson(new ReturnMess("1", "系统异常")).toString();
		}
	}

	/**
	 * 修改记录
	 * 
	 * @param node
	 * @return
	 */
	public String updateRecord(JsonNode node) {
		if (!node.has("id")) {
			return Json.toJson(new ReturnMess("1", "传入参数有误")).toString();
		}
		
		DisQuotation disQuotation = disQuotationMapper.selectByPrimaryKey(node.get("id").asInt());
		if (disQuotation == null) {
			return Json.toJson(new ReturnMess("1", "查无记录")).toString();
		}
		
		if (node.has("disEmail")) {
			disQuotation.setDisEmail(node.get("disEmail").asText());
		}
		if (node.has("isBuildOrder")) {
			disQuotation.setIsBuildOrder(node.get("isBuildOrder").asBoolean());
		}
		disQuotation.setUpdateDate(new Date());
		disQuotationMapper.updateByPrimaryKeySelective(disQuotation);
		
		ReturnMess returnMess = new ReturnMess("0", "");
		return Json.toJson(returnMess).toString();
	}

	@Override
	public Map<String, Object> buildOrder(JsonNode main) {
		DisQuotation disQuotation = disQuotationMapper.selectByPrimaryKey(main.get("id").asInt());
		Map<String, Object> result = Maps.newHashMap();
		if (disQuotation == null) {
			result.put("suc", false);
			result.put("msg", "找不到对应的报价单");
			return result;
		}
		
		disQuotation.setIsBuildOrder(true);
		boolean flag = disQuotationMapper.updateByPrimaryKey(disQuotation) > 0;
		result.put("suc", flag);
		result.put("msg", flag ? "更新报价单成功" : "更新报价失败");
		return result;
	}

	@Override
	public DisQuotation getDisQuotationById(int id) {
		return disQuotationMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean updateByIdSelective(DisQuotation dq) {
		int count = disQuotationMapper.updateByPrimaryKeySelective(dq);
		return count == 1;
	}

	@Override
	public boolean addDisQuotation(DisQuotation dq) {
		int count = disQuotationMapper.insertSelective(dq);
		return count == 1;
	}

}
