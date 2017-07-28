package services.product.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.elasticsearch.common.collect.Maps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.JsonResult;
import dto.product.ContractCostDto;
import dto.product.PageResultDto;
import entity.contract.ChargesOprecord;
import entity.contract.Contract;
import entity.contract.ContractCost;
import entity.contract.ContractCostType;
import mapper.contract.ChargesOprecordMapper;
import mapper.contract.ContractCostMapper;
import mapper.contract.ContractCostTypeMapper;
import mapper.contract.ContractMapper;
import play.Logger;
import play.libs.Json;
import services.base.utils.DoubleCalculateUtils;
import services.product.IContractChargesService;
import util.product.DateUtils;
import util.product.JsonCaseUtil;

/**
 * @author zbc 2017年3月25日 下午3:26:11
 */
public class ContractChargesService implements IContractChargesService {

	@Inject
	private ContractCostMapper costMapper;
	@Inject
	private ContractCostTypeMapper typeMapper;
	@Inject
	private ChargesOprecordMapper oprecordMapper;
	@Inject
	private ContractMapper contractMapper;
	@Inject
	private EventBus ebus;

	/**
	 * { "cno":"HT2017032410345200000023", "typeId":1, "scaleOfCharges":0.12,
	 * "startTime":"2017-03-25 12:00:00", "endTime":"2017-03-26 12:00:00",
	 * "remark":"remark" }
	 * 
	 * @return { "suc":true, "msg":"信息描述" }
	 */
	@Override
	public Map<String, Object> create(String string, String admin) {
		try {
			JsonNode json = Json.parse(string);
			String contractNo = JsonCaseUtil.jsonToString(json.get("cno"));
			Contract contract = contractMapper.selectByCno(contractNo);
			if (contract == null) {
				return resMap(false, "合同不存在");
			}
			if (costMapper.selectByContractNo(contractNo).size() > 0) {
				return resMap(false, "该合同已经添加费用，不能重复添加");
			}
			Integer typeId = JsonCaseUtil.jsonToInteger(json.get("typeId"));
			Double scaleOfCharges = JsonCaseUtil.jsonToDouble(json.get("scaleOfCharges"));
			String remark = JsonCaseUtil.jsonToString(json.get("remark"));
			ContractCost cost = new ContractCost(contractNo, typeId, scaleOfCharges, contract.getContractStart(),
					contract.getContractEnd(), admin, remark);
			if (new Date().after(cost.getStartTime())) {
				cost.setStatus(ContractCost.HAS_BEGUN);
			}
			if (DateUtils.dateAddDays(cost.getEndTime(), 1).before(new Date())) {
				cost.setStatus(ContractCost.FINISHED);
			}
			costMapper.insertSelective(cost);
			saveRecord(cost.getId(), admin, "新增合同费用信息");
			return resMap(true, "新增合同费用信息成功");
		} catch (Exception e) {
			Logger.info("插入合同费用信息异常" + e);
			return resMap(false, "新增合同费用信息异常");
		}
	}

	private void saveRecord(Integer id, String admin, String comment) {
		oprecordMapper.insertSelective(new ChargesOprecord(id, admin, comment));
	}

	private Map<String, Object> resMap(boolean suc, String msg) {
		Map<String, Object> res = Maps.newHashMap();
		res.put("suc", suc);
		res.put("msg", msg);
		return res;
	}

	private Map<String, Object> resMap(boolean suc, String msg, String[] keys, Object[] datas) {
		Map<String, Object> res = Maps.newHashMap();
		res.put("suc", suc);
		res.put("msg", msg);
		for (int i = 0; i < keys.length; i++) {
			res.put(keys[i], datas[i]);
		}
		return res;
	}

	@Override
	public List<ContractCostType> getTypes() {
		return typeMapper.select();
	}

	/**
	 * { "id":2, "scaleOfCharges":0.12, "startTime":"2017-03-25 12:00:00",
	 * "endTime":"2017-03-26 12:00:00", "remark":"remark" }
	 */
	@Override
	public Map<String, Object> update(String string, String admin) {
		try {
			JsonNode json = Json.parse(string);
			Integer id = JsonCaseUtil.jsonToInteger(json.get("id"));
			ContractCost contractCost = costMapper.selectByPrimaryKey(id);
			if (contractCost != null) {
				if (contractCost.getStatus() != ContractCost.HAVE_NOT_STARTED) {
					return resMap(false, "非未开始状态的不能修改");
				}
				Double originScaleOfCharges = contractCost.getScaleOfCharges();
				Double scaleOfCharges = JsonCaseUtil.jsonToDouble(json.get("scaleOfCharges"));
				String remark = JsonCaseUtil.jsonToString(json.get("remark"));
				contractCost.setScaleOfCharges(scaleOfCharges);
				contractCost.setRemark(remark);
				contractCost.setUpdateTime(new Date());
				costMapper.updateByPrimaryKey(contractCost);
				saveRecord(id, admin, "更新合同费用信息成功。由【" + originScaleOfCharges + "】更新为【" + scaleOfCharges+ "】");
				return resMap(true,"更新合同费用信息成功");
			}else{
				return resMap(false,"该数据不存在");
			}
		} catch (Exception e) {
			Logger.info("更新合同费用信息异常" + e);
			return resMap(false, "更新合同费用信息异常");
		}
	}

	@Override
	public ContractCost get(Integer id) {
		return costMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<ChargesOprecord> oprecord(Integer id) {
		return oprecordMapper.selectByCid(id);
	}

	/**
	 *
	 * {"page":1, "rows":10, "search":1, "start":"2017-03-26",
	 * "end":"2017-03-27", "typeId":1,
	 * "sidx":"scale_of_charges"/"end_time"/null, "sord":"asc"/"desc"/null }
	 */
	@Override
	public Map<String, Object> pageSearch(String string) {
		try {
			JsonNode node = Json.parse(string);
			Map<String, Object> param = Maps.newHashMap();
			Integer currPage = node.has("page") ? node.get("page").asInt() : null;
			Integer pageSize = node.has("rows") ? node.get("rows").asInt() : null;
			param.put("page", currPage);
			param.put("rows", pageSize);
			param.put("search", JsonCaseUtil.jsonToString(node.get("search")));
			param.put("start", JsonCaseUtil.jsonToString(node.get("start")));
			param.put("end", JsonCaseUtil.jsonToString(node.get("end")));
			param.put("typeId", JsonCaseUtil.jsonToInteger(node.get("typeId")));
			param.put("sidx", JsonCaseUtil.getString(node, "sidx", null));
			param.put("sord", JsonCaseUtil.getString(node, "sord", null));
			List<ContractCostDto> list = costMapper.pageSearch(param);
			Integer count = costMapper.pageCount(param);
			return resMap(true, "查询成功", new String[] { "pages" },
					new Object[] { new PageResultDto(pageSize, count, currPage, list) });
		} catch (Exception e) {
			Logger.info("合同费用分页查询异常", e);
			return resMap(false, "合同费用分页查询异常");
		}
	}

	/**
	 * { 
	 * 		"chargeMapList":[ 
	 * 			{ "contractNo":"HT2017032410345200000023", "sum":69.6}
	 * 		], 
	 * 		"salesOrderNo":"XS001", 
	 * 		"payDate":"2017-03-25 15:00:00" 
	 * }
	 */
	@Override
	public Map<String, Object> match(String string) {
		try {
			// 合同费用
			BigDecimal contractCharge = BigDecimal.ZERO;
			JsonNode json = Json.parse(string);
			ObjectMapper map = new ObjectMapper();
			List<Map<String, Object>> chargeMapList = map.readValue(json.get("chargeMapList").toString(),
					new TypeReference<List<Map<String, Object>>>() {
					});
//			String salesOrderNo = JsonCaseUtil.jsonToString(json.get("salesOrderNo"));
			String payDate = JsonCaseUtil.jsonToString(json.get("payDate"));
			Map<String, Object> param = Maps.newHashMap();
			param.put("payDate", payDate);
			String cno = null;
			ContractCost cost = null;
			Map<String, String> subs = Maps.newHashMap();
			for (Map<String, Object> chargeMap : chargeMapList) {
				cno = chargeMap.get("contractNo").toString();
				param.put("cno", cno);
				cost = costMapper.matchCost(param);
				if (cost != null) {
					subs.put("\\$p", chargeMap.get("sum").toString());
					subs.put("\\$f", cost.getScaleOfCharges().toString());
					// 计算费用
					contractCharge = contractCharge.add(new BigDecimal(runJS(subs, cost.getFormula())));
				}
			}

			return resMap(true, "匹配成功", new String[] { "charge" },
					new Object[] { contractCharge.setScale(3, BigDecimal.ROUND_HALF_UP) });
		} catch (Exception e) {
			Logger.info("匹配费用异常" + e);
			return resMap(false, "匹配费用异常");
		}
	}

	private Double runJS(Map<String, String> subs, String ruleValue) {
		ScriptEngineManager manager = new ScriptEngineManager(ClassLoader.getSystemClassLoader());
		ScriptEngine engine = manager.getEngineByName("js");
		try {
			subs.put("ceil", "Math.ceil");
			subs.put("floor", "Math.floor");
			for (String subKey : subs.keySet()) {
				ruleValue = ruleValue.replaceAll(subKey, subs.get(subKey));
			}
			Double cost = new Double(String.valueOf(engine.eval(ruleValue)));
			DoubleCalculateUtils duti = new DoubleCalculateUtils(cost);
			// 保留多位，最后再去保留N位
			return new BigDecimal(duti.doubleValue()).setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception e) {
			Logger.error("****************************************************************");
			Logger.error("* Run Js Error !");
			Logger.error("* Rule: " + ruleValue);
			Logger.error("****************************************************************");
			Logger.error("runJS Exception Details", e);
			return null;
		}
	}

	@Override
	public JsonResult<?> delete(Integer id, String admin) {
		ContractCost cost = costMapper.selectByPrimaryKey(id);
		if (cost == null) {
			return JsonResult.newIns().result(false).msg("合同费用不存在");
		}
		if (cost.getStatus() != ContractCost.HAVE_NOT_STARTED) {
			return JsonResult.newIns().result(false).msg("非未开始状态不能删除");
		}
		costMapper.deleteByPrimaryKey(id);
		saveRecord(id, admin, "删除合同费用");
		return JsonResult.newIns().result(true).msg("删除合同费用成功");
	}

	@Override
	public JsonResult<?> earlyTermination(Integer id, String admin) {
		ContractCost cost = costMapper.selectByPrimaryKey(id);
		if (cost == null) {
			return JsonResult.newIns().result(false).msg("合同费用不存在");
		}
		if (cost.getStatus() != ContractCost.HAS_BEGUN) {
			return JsonResult.newIns().result(false).msg("非已开始状态不能提前");
		}
		cost.setStatus(ContractCost.FINISHED);
		cost.setUpdateTime(new Date());
		saveRecord(id, admin, "提前结束合同费用");
		costMapper.updateByPrimaryKeySelective(cost);
		return JsonResult.newIns().result(true).msg("提前结束合同费用成功");
	}

}
