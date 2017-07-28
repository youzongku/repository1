package services.dismember.impl;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import entity.dismember.DisAccount;
import entity.dismember.DisBill;
import entity.dismember.DisCredit;
import mapper.dismember.DisAccountMapper;
import mapper.dismember.DisBillMapper;
import mapper.dismember.DisCreditMapper;
import play.Logger;
import play.libs.Json;
import services.dismember.IDisBillService;
import utils.dismember.DateUtils;
import utils.dismember.ExportUtil;
import utils.dismember.HttpUtil;
import utils.dismember.JsonCaseUtil;
import utils.dismember.Types;
import vo.dismember.Page;

/**
 * Created by LSL on 2016/1/5.
 */
public class DisBillService implements IDisBillService {

    @Inject
    private DisBillMapper billMapper;
    @Inject
	private DisAccountMapper disAccountMapper;
    @Inject
	private DisCreditMapper disCreditMapper;
    
    @Override
    public List<DisBill> queryBills(Map<String, Object> params) {
        return billMapper.queryBills(params);
    }

	@Override
	public Map<String, Object> createBill(DisBill bill) {
		Map<String, Object> res = Maps.newHashMap();
		String orderNo = bill.getSerialNumber();
		String email = "";
		Logger.info("orderNo: " + orderNo);
		if (StringUtils.isEmpty(orderNo)) {
			res.put("suc", false);
			return res;
		}
		
		try {
			JsonNode node = null;
			if (orderNo.startsWith("XS")) {
				node = parseJsonNode(getSaleMain(orderNo));
			} else if (orderNo.startsWith("CG")) {
				node = parseJsonNode(getPurchase(orderNo, ""));
			}
			if(node != null) {
				email = node.has("email") ? node.get("email").asText() : "";
			}
			// 根据单号查询email
			DisAccount account = disAccountMapper.getDisAccountByEmail(email);
			if (null == account) {
				Logger.info("用户" + email + "生成交易记录失败。");
				res.put("suc", false);
				return res;
			}
			
			DisCredit credit = disCreditMapper.getDisCreditInfo(email);
			bill.setAccountId(account.getId());
			bill.setBalance(account.getBalance());
			if (null != credit && credit.getTotalCreditLimit() != null && credit.getTotalUsedAmount() != null) {
				bill.setCreditLimitBalance(credit.getTotalCreditLimit().subtract(credit.getTotalUsedAmount()));
			}
			save(bill);
			res.put("suc", true);
			return res;
		} catch (Exception e) {
			Logger.error("创建交易记录失败" + e);
			res.put("suc", false);
			return res;
		}
	}
	
	private String getPurchase(String purchaseOrderNo, String flag) {
		ObjectNode node = Json.newObject();
		node.put("purchaseOrderNo", purchaseOrderNo);
		node.put("flag", flag);
		Logger.info("getPurchase     post_string--->" + node.toString());
		String response_string = HttpUtil.httpPost(node.toString(), HttpUtil.getHostUrl() + "/purchase/getByNo");
		Logger.info("getPurchase     response_string--->" + response_string);
		return response_string;
	}
	
	private String getSaleMain(String orderNo) {
		ObjectNode node = Json.newObject();
		node.put("orderNo", orderNo);
		Logger.info("getPurchase     post_string--->" + node.toString());
		String response_string = HttpUtil.httpPost(node.toString(), HttpUtil.getHostUrl() + "/sales/getMain");
		Logger.info("getPurchase     response_string--->" + response_string);
		return response_string;
	}
	
	private JsonNode parseJsonNode(String firstRes) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(firstRes);
	}

	@Override
	public int save(DisBill bill) {
		int line = billMapper.insertSelective(bill);
		return line;
	}

	@Override
	public Map<String, Object> getBill(Integer id) {
		Map<String,Object> billMap = Maps.newHashMap();
		billMap.put("bill",billMapper.selectByPrimaryKey(id));
		return billMap;
	}

	@Override
	public Map<String,Object> getPagedBills(String string,String email,List<String> accounts) {
		Map<String,Object> res = Maps.newHashMap();
		boolean suc = false;
		try {
			JsonNode json = Json.parse(string);
			Map<String, Object> data = Maps.newHashMap();
			data.put("email",email);
			data.put("key",JsonCaseUtil.jsonCase(json, "key", Types.STR));
			data.put("accounts",accounts);
			Integer pageSize = JsonCaseUtil.jsonCase(json,"pageSize",Types.INT);
			Integer currPage = JsonCaseUtil.jsonCase(json,"currPage",Types.INT);
			data.put("pageSize",pageSize);
			data.put("currPage", currPage);
			data.put("purpose",JsonCaseUtil.jsonCase(json,"purpose",Types.STR));
			data.put("serialNumber",JsonCaseUtil.jsonCase(json,"serialNumber",Types.STR));
			data.put("applyId",JsonCaseUtil.jsonCase(json,"applyId",Types.INT) );
			data.put("sources",JsonCaseUtil.jsonCase(json,"sources",Types.INT));
			data.put("son",JsonCaseUtil.jsonCase(json,"son",Types.BIT));
			
			data.put("sidx",JsonCaseUtil.jsonCase(json,"sidx",Types.STR));
			data.put("sord",JsonCaseUtil.jsonCase(json,"sord",Types.STR));
			
			Integer time = JsonCaseUtil.jsonCase(json,"time",Types.INT);
			if (time != null) {
				Calendar ca = Calendar.getInstance();
				ca.add(Calendar.MONTH,
						-(time));
				data.put("time",
						new DateTime(ca).toString("yyyy-MM-dd HH:mm:ss"));
			}
			Integer count = billMapper.queryBillsTotal(data);
	        List<DisBill> list = billMapper.queryBills(data);
	        if(null != list && list.size() > 0){
	            for (DisBill disBill : list) {
	                disBill.setPurposeStr(Constant.STATE_MAP.get(disBill.getPurpose()));
	            }
	        }
	        suc = true;
	        res.put("bills", new Page<DisBill>(currPage,pageSize, count,list));
		} catch (Exception e) {
			Logger.info("查询交易记录异常",e);
		}
		res.put("success", suc);
		return res;
	}

	@Override
	public File export(Map<String, String[]> map,String[] header,String email, List<String> accounts) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("email", email);
		params.put("purpose", map.get("purpose") == null ? "" : map.get("purpose")[0]);
		params.put("key", map.get("key") == null ? "" : map.get("key")[0]);
		params.put("accounts", accounts);
		String[] time = map.get("time");
		if (null != time && !"".equals(time[0])) {
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.MONTH, -(Integer.parseInt(time[0])));
			params.put("time", DateUtils.date2FullDateTimeString(ca.getTime()));
		}
		List<DisBill> bills = queryBills(params);
		for (DisBill disBill : bills) {
			disBill.setPurpose(Constant.STATE_MAP.get(disBill.getPurpose()));
		}
		Logger.info("导出交易记录，导出数据条数：" + bills.size());
		return ExportUtil.export("bill.xls", header, Constant.EXPORT_BILL_MAP, bills);
	}
}
