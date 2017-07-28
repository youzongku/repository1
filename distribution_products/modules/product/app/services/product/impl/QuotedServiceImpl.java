package services.product.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.JsonResult;
import dto.product.ContractQuotationsAddDto;
import dto.product.ContractQuotationsDto;
import dto.product.ContractQuotationsProDto;
import dto.product.PageResultDto;
import dto.product.ProductDispriceDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import entity.contract.Contract;
import entity.contract.ContractQuotations;
import entity.contract.QuotedOprecord;
import mapper.contract.ContractCostMapper;
import mapper.contract.ContractMapper;
import mapper.contract.ContractQuotationsMapper;
import mapper.contract.QuotedOprecordMapper;
import mapper.product.ProductBaseMapper;
import mapper.product.ProductDispriceMapper;
import play.Logger;
import play.libs.Json;
import services.base.utils.JsonFormatUtils;
import services.product.IProductBaseService;
import services.product.IProductEnquiryService;
import services.product.IQuotedService;
import session.ISessionService;
import util.product.DateUtils;
import util.product.JsonCaseUtil;

public class QuotedServiceImpl implements IQuotedService {

	@Inject
	private ContractMapper contractMapper;

	@Inject
	private ProductBaseMapper productBaseMapper;

	@Inject
	private ProductDispriceMapper disPriceMapper;

	@Inject
	private ContractQuotationsMapper quotationsMapper;

	@Inject
	private QuotedOprecordMapper qopMapper;
	
	@Inject
	private ContractCostMapper costMapper;

	@Override
	public Map<String, Object> addQuoted(Map<String, String[]> params, String opUser) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			Integer cid = Integer.parseInt(JsonCaseUtil.getString(params.get("cid")));
			Contract con = contractMapper.selectByPrimaryKey(cid);
			if (con == null) {
				result.put("suc", false);
				result.put("msg", "该合同不存在。");
				return result;
			}
			String sku = JsonCaseUtil.getString(params.get("sku"));
			int categoryId = JsonCaseUtil.getInteger(params.get("categoryId"));
			
			Integer warehouseId = Integer.parseInt(JsonCaseUtil.getString(params.get("warehouseId")));
			Date start = DateUtils.string2date(JsonCaseUtil.getString(params.get("start")), "yyyy-MM-dd");
			Date end = DateUtils.string2date(JsonCaseUtil.getString(params.get("end")), "yyyy-MM-dd");
			ContractQuotations cq = new ContractQuotations();
			cq.setSku(sku);
			cq.setCategoryId(categoryId);
			cq.setContractNo(con.getContractNo());
			cq.setWarehouseId(warehouseId);
			
			cq.setContractStart(start == null ? con.getContractStart() : start);
			cq.setContractEnd(end == null ? con.getContractEnd() : end);
			if(cq.getContractStart().after(cq.getContractEnd())) {
				result.put("suc", false);
				result.put("msg", "报价开始时间不能大于报价结束时间。");
				return result;
			}
			String price = JsonCaseUtil.getString(params.get("contractPrice"));
			if (StringUtils.isEmpty(price)) {
				result.put("suc", false);
				result.put("msg", "定价不能为空");
				return result;
			}
			cq.setContractPrice(Double.parseDouble(price));
			cq.setCreateTime(new Date());
			cq.setCreateUser(opUser);
			cq.setStatus(ContractQuotations.HAVE_NOT_STARTED);
			if (!setProductInfo(cq, sku, warehouseId, con.getDistributionMode())) {
				result.put("suc", false);
				result.put("msg", "商品信息不存在。");
				return result;
			}
			quotationsMapper.insertSelective(cq);

			//报价立即生效
			ContractQuotations contractQuotations = quotationsMapper.selectByPrimaryKey(cq.getId());
			if (contractQuotations.getContractStart().before(new Date())) {
				contractQuotations.setStatus(ContractQuotations.HAS_BEGUN);
				quotationsMapper.updateByPrimaryKeySelective(contractQuotations);
			}

			setRecord(cq.getId(), opUser, "添加报价成功。");
			result.put("suc", true);
			result.put("msg", "添加报价成功。");
		} catch (Exception e) {
			Logger.error("添加报价失败。", e);
			result.put("suc", false);
			result.put("msg", "添加报价失败。");
		}
		return result;
	}

	private void setRecord(Integer qid, String opUser, String comment) {
		QuotedOprecord record = new QuotedOprecord(qid,opUser,comment,new Date());
		qopMapper.insertSelective(record);
	}

	private boolean setProductInfo(ContractQuotations cq, String sku, Integer warehouseId, Integer model) {
		ProductSearchParamDto productSearchDto = new ProductSearchParamDto();
		List<String> skuList = Lists.newArrayList();
		skuList.add(sku);
		productSearchDto.setSkuList(skuList);
		productSearchDto.setModel(model);
		productSearchDto.setWarehouseId(warehouseId);
		List<ProductLite> list = productBaseMapper.products(productSearchDto);
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		ProductLite product = list.get(0);
		cq.setTitle(product.getCtitle());
		cq.setPurchasePrice(product.getDisPrice());
		cq.setWarehouseName(product.getWarehouseName());
		cq.setImgUrl(product.getImageUrl());
		cq.setInterBarCode(product.getInterBarCode());
		ProductDispriceDto disprice = disPriceMapper.selectDisprice(sku, warehouseId);
		if (null != disprice) {
			cq.setArriveWarePrice(disprice.getArriveWarePrice());
		}
		return true;
	}

	@Override
	public Map<String, Object> deleteQuoted(Integer qid, String opUser) {
		Map<String, Object> result = Maps.newHashMap();
		ContractQuotations cq = quotationsMapper.selectByPrimaryKey(qid);
		if(cq.getStatus() > 1) {
			result.put("suc", false);
			result.put("msg", "该状态下的报价信息不能被删除。");
			return result;
		}
		quotationsMapper.deleteByPrimaryKey(qid);
		result.put("suc", true);
		result.put("msg", "删除报价成功。");
		setRecord(qid, opUser, "删除报价成功。");
		return result;
	}

	@Override
	public PageResultDto<ContractQuotationsDto> getQuoted(JsonNode node, String string) {
		Map<String, Object> param = Maps.newHashMap();
		Integer currPage = node.has("page") ? node.get("page").asInt() : null;
		Integer pageSize = node.has("rows") ? node.get("rows").asInt() : null;
		param.put("page", currPage);
		param.put("rows", pageSize);
		
		String searchText = JsonCaseUtil.getString(node, "search", null);
		
		// 多个sku
		if (StringUtils.isNotBlank(searchText)) {
			if (searchText.indexOf(',')!=-1) {
				Set<String> skuSet = Sets.newHashSet();
				String[] skuArray = searchText.split(",");
				for (String sku : skuArray) {
					if (StringUtils.isNotBlank(sku)) {
						skuSet.add(sku.trim());
					}
				}
				param.put("skuList", Lists.newArrayList(skuSet));
			} else {
				param.put("search", searchText);
			}
		}
		
		param.put("start", JsonCaseUtil.getString(node, "start", null));
		param.put("end", JsonCaseUtil.getString(node, "end", null));
		param.put("sku", JsonCaseUtil.getString(node, "sku", null));
		param.put("contractNo", JsonCaseUtil.getString(node, "contractNo", null));
		param.put("warehouseId", node.has("warehouseId") && StringUtils.isNotEmpty(node.get("warehouseId").asText())
				? node.get("warehouseId").asInt() : null);
		param.put("categoryId", node.has("categoryId") && StringUtils.isNotEmpty(node.get("categoryId").asText())
				? node.get("categoryId").asInt() : null);
		param.put("status", node.has("status") && StringUtils.isNotEmpty(node.get("status").asText())
				? node.get("status").asInt() : null);
		param.put("sidx", JsonCaseUtil.getString(node, "sidx", null));
		param.put("sord", JsonCaseUtil.getString(node, "sord", null));
		Logger.info("获取合同报价列表，参数：{}",param);
		List<ContractQuotations> conts = quotationsMapper.select(param);
		List<ContractQuotationsDto> contracts = transformation(conts);
		Integer count = quotationsMapper.selectCount(param);
		return new PageResultDto<ContractQuotationsDto>(pageSize, count, currPage, contracts);
	}

	private List<ContractQuotationsDto> transformation(List<ContractQuotations> conts) {
		List<ContractQuotationsDto> dtos = Lists.newArrayList();
		ContractQuotationsDto dto = null;
		for (ContractQuotations con : conts) {
			dto = new ContractQuotationsDto();
			BeanUtils.copyProperties(con, dto);
			dto.setContractStart(DateUtils.date2string(con.getContractStart(), "yyyy-MM-dd"));
			dto.setContractEnd(DateUtils.date2string(con.getContractEnd(), "yyyy-MM-dd"));
			dtos.add(dto);
		}
		return dtos;
	}

	/*@Override
	public Map<String, Object> updateQuoted(JsonNode node, String opUser) {
		Map<String, Object> result = Maps.newHashMap();
		Integer qid = JsonCaseUtil.jsonToInteger(node.get("qid"));
		ContractQuotations cq = quotationsMapper.selectByPrimaryKey(qid);
		if (cq == null) {
			result.put("suc", false);
			result.put("msg", "未查询到该报价信息。");
			return result;
		}
		//结束的报价不能被更新
		if (cq.getStatus() == 3) {
			result.put("suc", false);
			result.put("msg", "该状态下的报价信息不能被更新。");
			return result;
		}
		ContractQuotations record = new ContractQuotations();
		record.setId(qid);
		if (cq.getStatus() == 1) {
			record.setContractPrice(JsonCaseUtil.jsonToDouble(node.get("contractPrice")));
		}
		record.setContractStart(JsonCaseUtil.jsonStrToDate(node.get("start"), "yyyy-MM-dd"));
		record.setContractEnd(JsonCaseUtil.jsonStrToDate(node.get("end"), "yyyy-MM-dd"));
		if(record.getContractStart().after(record.getContractEnd())) {
			result.put("suc", false);
			result.put("msg", "报价开始时间不能大于报价结束时间。");
			return result;
		}
		record.setUpdateTime(new Date());
		quotationsMapper.updateByPrimaryKeySelective(record);
		setRecord(qid, opUser,
				"更新报价【" + (record.getContractPrice() == null ? cq.getContractPrice() : record.getContractPrice()) + "】【"
						+ DateUtils.date2string(record.getContractStart(), "yyyy-MM-dd") + "】【"
						+ DateUtils.date2string(record.getContractEnd(), "yyyy-MM-dd") + "】");
		result.put("suc", true);
		result.put("msg", "报价信息更新成功。");
		return result;
	}*/

	@Override
	public List<QuotedOprecord> getOprecord(Integer qid) {
		if (null == qid) {
			return Lists.newArrayList();
		}
		List<QuotedOprecord> oplist = qopMapper.queryRecord(qid);
		transRecord(oplist);
		return oplist;
	}

	private void transRecord(List<QuotedOprecord> oplist) {
		for (QuotedOprecord quotedOprecord : oplist) {
			quotedOprecord.setOpdateStr(DateUtils.date2string(quotedOprecord.getOpdate(), "yyyy-MM-dd HH:mm:ss"));
		}
	}

	@Override
	public void autoOpenNotStartQuoted() {
		Integer line = quotationsMapper.updateNotStartQuoted();
		Integer line2 = quotationsMapper.updateEndedQuoted();
		Logger.info("自动开启报价：" + line + ",结束报价:" + line2);
		Logger.info("自动开启费用:{},自动结束费用:{}",costMapper.updateNotStartCost(),costMapper.updateEndedCost());
	}

	@Override
	public List<ContractQuotations> queryQuoted(List<String> skus, String email) {
		return null;
	}

	@Override
	public JsonResult<?> batchAdd(String string, String operator) {
		try {
			ContractQuotationsAddDto dto = JsonFormatUtils.jsonToBean(string, ContractQuotationsAddDto.class);
			List<ContractQuotationsProDto> pros = dto.getPros();
			if(dto.getPros() == null || dto.getPros().size()==0){
				return JsonResult.newIns().result(false).msg("请添加商品");
			}
			if(!validCheckPros(pros)){
				return JsonResult.newIns().result(false).msg("商品详情数据不全");
			}
			Contract con = contractMapper.selectByPrimaryKey(dto.getCid());
			if (con == null) {
				return JsonResult.newIns().result(false).msg("该合同不存在。");
			}
			//校验报价是否重复
			JsonResult<?> res  = quotaRepeatCheck(pros, con);
			if(!res.getResult()){
				return res;
			}
			List<ContractQuotations> cqs = Lists.newArrayList();
			res = getQuotations(operator, pros, con,cqs);
			if(!res.getResult()){
				return res;
			}
			
			cqs.forEach(e->{
				quotationsMapper.insertSelective(e);
				setRecord(e.getId(), operator, "添加报价成功。");
			});
			return res;
		} catch (Exception e) {
			Logger.info("批量新增报价异常:{}",e);
			return JsonResult.newIns().result(false).msg("批量新增报价异常。");
		}
	}
	
	/**
	 * @author zbc
	 * @since 2017年5月3日 上午11:23:35
	 * @param pros
	 * @param con
	 * @return
	 */
	private JsonResult<?> quotaRepeatCheck(List<ContractQuotationsProDto> pros, Contract con) {
		List<ContractQuotations> exists = quotationsMapper.selectByContractNo(con.getContractNo(),null);
		List<String> skus = Lists.newArrayList();
		if(exists.size()>0){
			for(ContractQuotationsProDto pro:pros){
				if(exists.stream().filter(e->e.getStatus() !=3&&e.getSku().equals(pro.getSku())&&e.getWarehouseId().equals(pro.getWarehouseId())).findAny().isPresent()){
					skus.add(pro.getSku());
				}
			}
		}
		if(skus.size()>0){
			return JsonResult.newIns().result(false).msg("商品:"+skus.toString()+"已添加报价，不可重复添加");
		}
		return JsonResult.newIns().result(true);
	}

	/**
	 * @author zbc
	 * @since 2017年5月3日 上午11:23:29
	 * @param operator
	 * @param pros
	 * @param con
	 * @param cqs
	 * @return
	 */
	private JsonResult<?> getQuotations(String operator, List<ContractQuotationsProDto> pros, Contract con, List<ContractQuotations> cqs) {
		ContractQuotations cq;
		List<String> skus = Lists.transform(pros, p->p.getSku());
		ProductSearchParamDto productSearchDto = new ProductSearchParamDto();
		productSearchDto.setSkuList(skus);
		productSearchDto.setModel(con.getDistributionMode());
		List<ProductLite> products = productBaseMapper.products(productSearchDto);
		Map<String,ProductLite> productMap = Maps.newHashMap();
		for(ProductLite product:products){
			productMap.put(product.getCsku()+"||"+product.getWarehouseId(), product);
		}
		ProductLite product;
		for(ContractQuotationsProDto pro:pros){
			product = productMap.get(pro.getSku()+"||"+pro.getWarehouseId());
			if(product != null){
				cq = new ContractQuotations(con,pro,product,new Date(),operator,1);
				ProductDispriceDto disprice = disPriceMapper.selectDisprice(pro.getSku(),pro.getWarehouseId());
				if (null != disprice) {
					cq.setArriveWarePrice(disprice.getArriveWarePrice());
				}
				//已开始
				if (cq.getContractStart().before(new Date())) {
					cq.setStatus(ContractQuotations.HAS_BEGUN);
				}
				//已结束
				if (DateUtils.dateAddDays(cq.getContractEnd(), 1).before(new Date())) {
					cq.setStatus(ContractQuotations.FINISHED);
				}
				cqs.add(cq);
			}else{
				return JsonResult.newIns().result(false).msg(pro.getSku()+"商品不存在");
			}
		}
		return JsonResult.newIns().result(true).msg("添加报价成功");
	}
	
	private boolean validCheckPros(List<ContractQuotationsProDto> pros){
		return pros.stream()
				.filter(p->{
					return p.getSku() == null 
							|| p.getWarehouseId() == null 
							|| p.getContractPrice() == null
							|| p.getCategoryId() == null;
				})
				.count()==0;
	}

	@Override
	public JsonResult<?> earlyTermination(Integer id, String adminAccount) {
		try {
			ContractQuotations quota = quotationsMapper.selectByPrimaryKey(id);
			if(quota == null){
				return JsonResult.newIns().result(false).msg("该报价不存在");
			}
			if(quota.getStatus() == ContractQuotations.HAVE_NOT_STARTED){
				return JsonResult.newIns().result(false).msg("该报价未开始");
			}
			if(quota.getStatus() == ContractQuotations.FINISHED){
				return JsonResult.newIns().result(false).msg("该报价已结束");
			}
			quota.setStatus(ContractQuotations.FINISHED);
			quotationsMapper.updateByPrimaryKeySelective(quota);
			setRecord(quota.getId(), adminAccount, "提前结束报价。");
			return JsonResult.newIns().result(true).msg("提前结束报价成功");
		} catch (Exception e) {
			Logger.info("提前结束报价异常{}",e);
			return JsonResult.newIns().result(false).msg("提前结束报价异常");
		}
	}

	@Override
	public JsonResult<?> getQuoted(Integer id) {
		ContractQuotations quota = quotationsMapper.selectByPrimaryKey(id);
		if(quota == null){
			return JsonResult.newIns().result(false).msg("该报价不存在");
		}
		ContractQuotationsDto dto = new ContractQuotationsDto();
		BeanUtils.copyProperties(quota, dto);
		dto.setContractStart(DateUtils.date2string(quota.getContractStart(), "yyyy-MM-dd"));
		dto.setContractEnd(DateUtils.date2string(quota.getContractEnd(), "yyyy-MM-dd"));
		return JsonResult.newIns().result(true).data(dto);
	}

	@Override
	public JsonResult<?> updateQuoted(String  str, String opUser) {
		try {
			JsonNode node = Json.parse(str);
			Integer qid = JsonCaseUtil.jsonToInteger(node.get("qid"));
			ContractQuotations cq = quotationsMapper.selectByPrimaryKey(qid);
			Double contractPrice = JsonCaseUtil.jsonToDouble(node.get("contractPrice"));
			if(contractPrice == null){
				return JsonResult.newIns().result(false).msg("合同价不能为空。");
			}
			if (cq == null) {
				return JsonResult.newIns().result(false).msg("未查询到该报价信息。");
			}
			//结束的报价不能被更新
			if (cq.getStatus() == ContractQuotations.FINISHED||cq.getStatus() == ContractQuotations.HAS_BEGUN) {
				return JsonResult.newIns().result(false).msg("该状态下的报价信息不能被更新。");
			}
			Double originContractPrice = cq.getContractPrice();
			cq.setContractPrice(contractPrice);
			cq.setUpdateTime(new Date());
			quotationsMapper.updateByPrimaryKeySelective(cq);
			setRecord(qid, opUser, "报价信息更新成功。由【" + originContractPrice + "】更新为【" + contractPrice + "】");
			return JsonResult.newIns().result(true).msg("报价信息更新成功。");
		} catch (Exception e) {
			Logger.info("更新报价异常{}",e);
			return JsonResult.newIns().result(false).msg("更新报价异常。");
		}
		
	}

	@Inject
	private IProductBaseService baseService;
	@Inject
	private IProductEnquiryService prodEnquiryService;
	@Inject
	private ISessionService sessionService;
	
	@Override
	public Map<String, Object> batchSetCategoryId() {
		ProductSearchParamDto searchDto = new ProductSearchParamDto();
		// 缓存类目
		baseService.loadCategory();
		Logger.info("查询商品的参数：" + searchDto);
		if (searchDto.getModel() == null) {
			searchDto.setModel(sessionService.get("model") != null
					? Integer.parseInt(sessionService.get("model").toString()) : null);
		}
		if (StringUtils.isEmpty(searchDto.getEmail())) {
			searchDto.setEmail(sessionService.get("email") != null ? sessionService.get("email").toString() : null);
		}
		PageResultDto<ProductLite> pageResultDto = prodEnquiryService.products(searchDto);
		
		Map<String, Object> result = Maps.newHashMap();
		// 合同报价的商品
		List<ContractQuotations> conts = quotationsMapper.select(Maps.newHashMap());
		if (CollectionUtils.isEmpty(conts)) {
			result.put("suc", false);
			result.put("msg", "没有合同报价的商品");
			return result;
		}
		
		List<ContractQuotations> list = conts.stream().filter(e->e.getCategoryId()==null).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(list)) {
			result.put("suc", false);
			result.put("msg", "所有合同报价的商品已有商品分类，不需要设置");
			return result;
		}
		
		Map<String, List<ContractQuotations>> sku2CQList = list.stream().collect(Collectors.groupingBy(e->e.getSku()));
		
		// 原来的商品
		Map<String,Integer> sku2CategoryId = Maps.newHashMap();
		for (ProductLite pl : pageResultDto.getResult()) {
			sku2CategoryId.put(pl.getCsku(), pl.getCategoryId());
		}
		
		List<ContractQuotations> allCq = Lists.newArrayListWithCapacity(list.size());
		// 给商品报价设置商品分类id
		for (Map.Entry<String, List<ContractQuotations>> entry : sku2CQList.entrySet()) {
			String sku = entry.getKey();
			List<ContractQuotations> cqList = entry.getValue();
			
			Integer categoryId = sku2CategoryId.get(sku);
			if (categoryId!=null) {
				for (ContractQuotations cq : cqList) {
					cq.setCategoryId(categoryId);
					allCq.add(cq);
				}
			}
		}
		
		// 进行更新
		int count = quotationsMapper.batchUpdateCategoryId(allCq);
		
		result.put("suc", true);
		result.put("msg", "有"+allCq.size()+"个合同报价商品要设置商品分类");
		return result;
	}
}
