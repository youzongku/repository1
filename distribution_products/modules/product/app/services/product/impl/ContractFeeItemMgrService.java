package services.product.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.category.CategorySearchParamDto;
import dto.contract.fee.BaseContractFeeValue;
import dto.contract.fee.ContractFeeItemDto;
import dto.contract.fee.ContractFeeItemLogDto;
import dto.contract.fee.ContractFeeItemPageQeuryParam;
import dto.contract.fee.ContractFeeItemRelatedSkuDto;
import dto.contract.fee.ContractFeeItemResult;
import dto.contract.fee.ContractFeeParam;
import dto.contract.fee.FeeRate;
import dto.contract.fee.FeeValue;
import dto.contract.fee.GetContractFeeItemsParam;
import dto.contract.fee.GetContractFeeItemsParam.ContractParam;
import dto.product.ContractQuotationsDto;
import dto.product.PageResultDto;
import entity.contract.Contract;
import entity.contract.ContractFeeItem;
import entity.contract.ContractFeeItemLog;
import entity.contract.ContractFeeItemRelatedSku;
import entity.contract.ContractFeetype;
import entity.contract.ContractQuotations;
import event.CalContractFeeEvent;
import mapper.category.CategoryBaseMapper;
import mapper.contract.ContractFeeItemLogMapper;
import mapper.contract.ContractFeeItemMapper;
import mapper.contract.ContractFeeItemRelatedSkuMapper;
import mapper.contract.ContractFeetypeMapper;
import mapper.contract.ContractMapper;
import mapper.contract.ContractQuotationsMapper;
import play.Logger;
import play.libs.Json;
import services.product.IContractFeeItemMgrService;
import services.product.IQuotedService;
import util.product.DateUtils;
import util.product.Page;
import util.product.RegExpValidatorUtils;

public class ContractFeeItemMgrService implements IContractFeeItemMgrService {
	@Inject
	private ContractFeeItemMapper contractFeeItemMapper;
	@Inject
	private ContractFeetypeMapper contractFeetypeMapper;
	@Inject
	private ContractMapper contractMapper;
	@Inject
	private ContractFeeItemRelatedSkuMapper relatedSkuMapper;
	@Inject
	private ContractFeeItemLogMapper contractFeeItemLogMapper;
	@Inject
	private EventBus ebus;
	@Inject
	private IQuotedService quotedService;
	@Inject
	private CategoryBaseMapper cateBaseMapper;
	@Inject
	private ContractQuotationsMapper contractQuotationsMapper;
	
	@Override
	public Map<String, Object> getContractFeeItems4Calculation(GetContractFeeItemsParam param) {
		// 把所有合同对应的合同费用项查询出来
		Map<String, Object> queryFeeItemsParam = Maps.newHashMap();
		queryFeeItemsParam.put("contractNoList", Lists.transform(param.getContracts(), e->e.getContractNo()));
		List<ContractFeeItem> allFeeItemList = contractFeeItemMapper.selectByConditions(queryFeeItemsParam);
		// 进行过滤，获取有效的合同费用项
		LocalDateTime payDate = param.getPayDate();
		List<ContractFeeItem> validFeeItemList = allFeeItemList.stream().filter(e->{
			LocalDateTime startDate = DateUtils.toLocalDateTime(e.getStartTime());
			LocalDateTime endTime = DateUtils.toLocalDateTime(e.getEndTime());
			return (payDate.isEqual(startDate) || payDate.isAfter(startDate))
					&& (payDate.isBefore(endTime));
		}).collect(Collectors.toList());
		
		// 根据合同分组：一个合同对应多个费用项
		Map<String, List<ContractFeeItem>> cno2FeeItemList = validFeeItemList.stream().collect(Collectors.groupingBy(e->e.getContractNo()));

		Map<String, Object> queryRelatedSkusParam = Maps.newHashMap();
		queryRelatedSkusParam.put("feeItemIdList", Lists.transform(validFeeItemList, e->e.getId()));
		List<ContractFeeItemRelatedSku> allRelatedSkus = relatedSkuMapper.selectByConditions(queryRelatedSkusParam);
		// 费用项对应的sku
		Map<Integer, List<ContractFeeItemRelatedSku>> feeItemId2RelatedSkus = allRelatedSkus.stream().collect(Collectors.groupingBy(e->e.getFeeItemId()));
		
		List<ContractFeeItemResult> result = Lists.newArrayList();
		for (ContractParam contractParam : param.getContracts()) {
			String contractNo = contractParam.getContractNo();
			// 将pros转为ContractFeeItemRelatedSku的，方便计算交集
			List<ContractFeeItemRelatedSkuDto> pros = Lists.transform(contractParam.getPros(), e->{
				return new ContractFeeItemRelatedSkuDto(e.getSku(), e.getWarehouseId());
			});
			
			// 合同对应的费用项
			List<ContractFeeItem> feeItemList = cno2FeeItemList.get(contractNo);
			if (CollectionUtils.isEmpty(feeItemList)) {
				continue;
			}
			
			List<ContractFeeItemDto> feeItemDtoList = Lists.newArrayList();
			for (ContractFeeItem feeItem : feeItemList) {
				List<ContractFeeItemRelatedSku> relatedSkus = feeItemId2RelatedSkus.get(feeItem.getId());
				if (CollectionUtils.isNotEmpty(relatedSkus)) {
					Logger.info("合同费用项：{}",feeItem);
					ContractFeeItemDto feeItemDto = new ContractFeeItemDto();
					BeanUtils.copyProperties(feeItem, feeItemDto);
					setDtoFeeItemValue(feeItem, feeItemDto);
					List<ContractFeeItemRelatedSkuDto> skuList = Lists.transform(relatedSkus, e->{
						return new ContractFeeItemRelatedSkuDto(e.getSku(), e.getWarehouseId());
					});
					skuList.retainAll(pros);// 拿到交集
					feeItemDto.setRelatedSkus(skuList);
					feeItemDtoList.add(feeItemDto);
				}
			}
			
			result.add(new ContractFeeItemResult(contractNo, feeItemDtoList));
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("suc", true);
		resultMap.put("result", result);
		return resultMap;
	}
	public static void main(String[] args) {
		List<ContractFeeItemRelatedSkuDto>  list1 = Lists.newArrayList();
		ContractFeeItemRelatedSkuDto reSku = new ContractFeeItemRelatedSkuDto("IF639",2024);
		list1.add(reSku);
		List<ContractFeeItemRelatedSkuDto>  list2 = Lists.newArrayList();
		list2.add(reSku);
		list1.retainAll(list2);
		System.out.println(Json.toJson(list1));
	}
	
	/**
	 * 检查合同商品报价
	 * @param param
	 * @param result
	 * @return
	 */
	private boolean checkContractQuotations(ContractFeeParam param, Map<String, Object> result) {
		if (param.getMatchedCdts()!=null) {
			result.clear();
			boolean noContractQuotations = false;
			if (param.getMatchedCdts()) {
				Map<String, Object> cdtsMap = param.getCdtsMap();
				cdtsMap.put("status", ContractQuotations.HAS_BEGUN);
				JsonNode paramNode = Json.toJson(cdtsMap);
				Logger.info("获取符合条件的合同价商品，参数：{}",paramNode);
				PageResultDto<ContractQuotationsDto> quoted = quotedService.getQuoted(paramNode,
						param.getOptUser());
				List<ContractQuotationsDto> quotedDtoList = quoted.getResult();
				noContractQuotations = CollectionUtils.isEmpty(quotedDtoList);
			} else {
				// 去获取合同的报价
				List<ContractQuotations> quotedDtoList = contractQuotationsMapper.selectByContractNo(param.getContractNo(), ContractQuotations.HAS_BEGUN);
				noContractQuotations = CollectionUtils.isEmpty(quotedDtoList);
			}
			if (noContractQuotations) {
				result.put("suc", false);
				result.put("msg", "没有符合条件的合同商品");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 合同费用项是否已存在
	 * @param existsItems
	 * @param result
	 * @return true存在，false不存在
	 */
	private boolean itemExistsed(List<ContractFeeItem> existsItems, Map<String, Object> result) {
		boolean itemExistsed = existsItems.stream().filter(e -> {
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime startDate = DateUtils.toLocalDateTime(e.getStartTime());
			LocalDateTime endTime = DateUtils.toLocalDateTime(e.getEndTime());
			return (today.isEqual(startDate) || today.isAfter(startDate)) && (today.isBefore(endTime));
		}).count() > 0;
		if (itemExistsed) {
			result.clear();
			result.put("suc", false);
			result.put("msg", "合同费用项已存在");
			return true;
		}
		return false;
	}
	
	@Override
	public Map<String, Object> addContractFeeItem(ContractFeeParam param) {
		
		Logger.info("新增合同费用项 in service，参数：{}",param);
		
		Map<String, Object> result = Maps.newHashMap();
		Integer feeTypeId = param.getFeeTypeId();// 费用项
		String contractNo = param.getContractNo();// 合同号
		
		// 检查费用项是否存在
		ContractFeetype feetype = contractFeetypeMapper.selectByPrimaryKey(feeTypeId);
		if (feetype==null) {
			result.put("suc", false);
			result.put("msg", "费用项不存在");
			return result;
		}
		
		// 先检查费用项类型和要设置的费用值/率是否对得上，费用类型 1：固定费用值，2：固定费用率
		if (feetype.getType()==1) {
			Double estimatedTotalCost = param.getEstimatedTotalCost();
			Double estimatedTotalPerformance = param.getEstimatedTotalPerformance();
			if (estimatedTotalCost==null || estimatedTotalPerformance==null
					|| estimatedTotalCost<0 || estimatedTotalPerformance<0
					|| !RegExpValidatorUtils.isMoney4(String.valueOf(estimatedTotalCost))
					|| !RegExpValidatorUtils.isMoney4(String.valueOf(estimatedTotalPerformance))) {
				result.put("suc", false);
				result.put("msg", "预估费用/预估业绩不合理（大于0且至多4位小数）");
				return result;
			}
		} else if (feetype.getType()==2) {
			Double feeRate = param.getFeeRate();
			if (feeRate==null || feeRate<=0 || feeRate>1 
					|| !RegExpValidatorUtils.isMoney4(String.valueOf(feeRate))) {
				result.put("suc", false);
				result.put("msg", "费用率需大于0，小于等于1（至多4位小数）");
				return result;
			}
		}
		
		// 合同是否存在
		Contract contract = contractMapper.selectByCno(contractNo);
		if (contract==null) {
			result.put("suc", false);
			result.put("msg", "合同不存在");
			return result;
		}
		
		// 合同内是否存在未结束的费用项
		List<ContractFeeItem> existsItems = contractFeeItemMapper.select(contractNo, feeTypeId);
		if (itemExistsed(existsItems, result)) {
			return result;
		}	
		
		if (!checkContractQuotations(param, result)) {
			return result;
		}
		
		ContractFeeItem newItem = new ContractFeeItem();
		// 开始时间为最近的那个相同费用项的结束时间；不存在相同的费用项，开始时间就为合同开始时间
		Optional<Date> max = existsItems.stream().map(e->e.getEndTime())
				.max(Comparator.comparing(e->e));
		newItem.setStartTime(max.orElse(contract.getContractStart()));
		// 合同结束时间到 23时59分59秒
		LocalDate contractEnd = DateUtils.toLocalDate(contract.getContractEnd());
		Date itemEndTime = DateUtils.toDate(LocalDateTime.of(contractEnd.getYear(), contractEnd.getMonth(), contractEnd.getDayOfMonth(), 23, 59, 59));
		newItem.setEndTime(itemEndTime);
		
		newItem.setContractNo(contractNo);
		newItem.setFeeType(feetype.getType());
		newItem.setFeeTypeId(feeTypeId);
		newItem.setFeeTypeName(feetype.getName());
		
		
		// 费用项的值
		String content = Json.stringify(Json.toJson(parseContractFeeValue(feetype.getType(), param)));
		newItem.setContent(content);
		newItem.setCreateUser(param.getOptUser());
		newItem.setRemarks(param.getRemarks());
		contractFeeItemMapper.insert(newItem);
		
		Integer feeItemId = newItem.getId();
		
		// 获取商品分类
		Map<Integer, String> cid2cname = getCategoryName();
		
		// 先删除关联的sku
		relatedSkuMapper.deleteByFeeItemId(feeItemId);
		
		// 是否全选
		if (param.getMatchedCdts()) {
			Map<String, Object> cdtsMap = param.getCdtsMap();
			cdtsMap.put("status", ContractQuotations.HAS_BEGUN);
			PageResultDto<ContractQuotationsDto> quoted = quotedService.getQuoted(Json.toJson(cdtsMap),
					param.getOptUser());
			List<ContractQuotationsDto> quotedDtoList = quoted.getResult();
			
			List<ContractFeeItemRelatedSku> relatedSkus = Lists.transform(quotedDtoList, e -> {
				ContractFeeItemRelatedSku relatedSku = new ContractFeeItemRelatedSku(e.getTitle(), e.getSku(), e.getWarehouseId(), e.getWarehouseName(),
						e.getCategoryId(), cid2cname.get(e.getCategoryId()), e.getContractPrice());
				relatedSku.setFeeItemId(feeItemId);
				return relatedSku;
			});
			// 保存关联的sku
			relatedSkuMapper.insertBatch(relatedSkus);
		} else {
			// 去获取合同的报价
			ImmutableMap<Object, ContractQuotations> skuWarehouseId2CQ = Maps.uniqueIndex(contractQuotationsMapper.selectByContractNo(param.getContractNo(),ContractQuotations.HAS_BEGUN), e->{
				return getKey(e.getSku(), e.getWarehouseId());
			});
			
			List<ContractFeeItemRelatedSku> transformedRelatedSkus = Lists.transform(param.getRelatedSkus(), e->{
				ContractQuotations cq = skuWarehouseId2CQ.get(getKey(e.getSku(), e.getWarehouseId()));
				ContractFeeItemRelatedSku relatedSku = new ContractFeeItemRelatedSku(cq.getTitle(), cq.getSku(), cq.getWarehouseId(), cq.getWarehouseName(),
						cq.getCategoryId(), cid2cname.get(e.getCategoryId()), cq.getContractPrice());
				relatedSku.setFeeItemId(feeItemId);
				return relatedSku;
			});
			
			// 保存关联的sku
			relatedSkuMapper.insertBatch(transformedRelatedSkus);
		}
		
		// 记录日志
		int optType = newItem.getFeeType()==1 ? ContractFeeItemLog.OPT_TYPE_ADD_ESIMATED_FEE
				: ContractFeeItemLog.OPT_TYPE_ADD_FEE_RATE;
		addLogs(null, newItem, optType);
		result.put("suc", true);
		return result;
	}
	
	private Object getKey(String sku, Integer warehouseId) {
		return sku + "_" + warehouseId;
	}
	
	@Override
	public Map<String, Object> updateContractFeeItem(ContractFeeParam param) {
		Map<String, Object> result = Maps.newHashMap();
		// 更新的话，只能更新值和关联的商品，费用项不能修改
		Integer feeItemId = param.getFeeItemId();
		ContractFeeItem item = contractFeeItemMapper.selectByPrimaryKey(feeItemId);
		// 未开始的才可以更新
		if (!checkFeeItemNotStarted(item, result)) {
			return result;
		}
		
		// 先检查费用项类型和要设置的费用值/率是否对得上，费用类型 1：固定费用值，2：固定费用率
		if (item.getFeeType() == 1) {
			Double estimatedTotalCost = param.getEstimatedTotalCost();
			Double estimatedTotalPerformance = param.getEstimatedTotalPerformance();
			if (estimatedTotalCost==null || estimatedTotalPerformance==null
					|| estimatedTotalCost<0 || estimatedTotalPerformance<0
					|| !RegExpValidatorUtils.isMoney4(String.valueOf(estimatedTotalCost))
					|| !RegExpValidatorUtils.isMoney4(String.valueOf(estimatedTotalPerformance))) {
				result.put("suc", false);
				result.put("msg", "预估费用/预估业绩不合理（大于0且至多4位小数）");
				return result;
			}
		} else if (item.getFeeType() == 2) {
			Double feeRate = param.getFeeRate();
			if (feeRate == null || feeRate <= 0 || feeRate > 1
					|| !RegExpValidatorUtils.isMoney4(String.valueOf(feeRate))) {
				result.put("suc", false);
				result.put("msg", "费用率需大于0，小于等于1（至多4位小数）");
				return result;
			}
		}
				
		if (!checkContractQuotations(param, result)) {
			return result;
		}
		
		ContractFeeItem updateItem = new ContractFeeItem();
		// 费用项的值
		String content = Json.stringify(Json.toJson(parseContractFeeValue(item.getFeeType(), param)));
		Logger.info("旧的content为：{}，新的content为：{}",item.getContent(),content);
		updateItem.setContent(content);
		updateItem.setId(item.getId());
		updateItem.setLastUpdateUser(param.getOptUser());
		updateItem.setRemarks(param.getRemarks());
		contractFeeItemMapper.updateByPrimaryKeySelective(updateItem);
		
		if (param.getMatchedCdts()!=null) {
			
			// 获取商品分类
			Map<Integer, String> cid2cname = getCategoryName();
			
			// 先删除关联的sku
			relatedSkuMapper.deleteByFeeItemId(feeItemId);
			
			// 是否全选
			if (param.getMatchedCdts()) {
				Map<String, Object> cdtsMap = param.getCdtsMap();
				cdtsMap.put("status", ContractQuotations.HAS_BEGUN);
				PageResultDto<ContractQuotationsDto> quoted = quotedService.getQuoted(Json.toJson(cdtsMap), param.getOptUser());
				List<ContractQuotationsDto> quotedDtoList = quoted.getResult();
				
				List<ContractFeeItemRelatedSku> relatedSkus = Lists.transform(quotedDtoList, e->{
					ContractFeeItemRelatedSku relatedSku = new ContractFeeItemRelatedSku(e.getTitle(), e.getSku(), e.getWarehouseId(), e.getWarehouseName(),
							e.getCategoryId(), cid2cname.get(e.getCategoryId()), e.getContractPrice());
					relatedSku.setFeeItemId(feeItemId);
					return relatedSku;
				});
				// 保存关联的sku
				relatedSkuMapper.insertBatch(relatedSkus);	
			} else {
				// 去获取合同的报价
				List<ContractQuotations> cqList = contractQuotationsMapper.selectByContractNo(item.getContractNo(),ContractQuotations.HAS_BEGUN);
				ImmutableMap<Object, ContractQuotations> skuWarehouseId2CQ = Maps.uniqueIndex(cqList, e->{
					return getKey(e.getSku(), e.getWarehouseId());
				});
				
				List<ContractFeeItemRelatedSku> transformedRelatedSkus = Lists.transform(param.getRelatedSkus(), e->{
					ContractQuotations cq = skuWarehouseId2CQ.get(getKey(e.getSku(), e.getWarehouseId()));
					ContractFeeItemRelatedSku relatedSku = new ContractFeeItemRelatedSku(cq.getTitle(), cq.getSku(), cq.getWarehouseId(), cq.getWarehouseName(),
							cq.getCategoryId(), cid2cname.get(cq.getCategoryId()), cq.getContractPrice());
					relatedSku.setFeeItemId(feeItemId);
					return relatedSku;
				});
				
				// 保存关联的sku
				relatedSkuMapper.insertBatch(transformedRelatedSkus);	
			}
		}
		
		// 记录日志
		int optType = item.getFeeType() == 1 ? ContractFeeItemLog.OPT_TYPE_UPDATE_ESIMATED_FEE
				: ContractFeeItemLog.OPT_TYPE_UPDATE_FEE_RATE;
		addLogs(item, updateItem, optType);
		
		result.put("suc", true);
		return result;
	}
	
	private Map<Integer, String> getCategoryName() {
		// 获取商品分类
		CategorySearchParamDto dto = new CategorySearchParamDto();
		dto.setLevel(1);
		Map<Integer, String> cid2cname = cateBaseMapper.realCateQuery(dto).stream()
				.collect(Collectors.toMap(e -> e.getIid(), e -> e.getCname()));
		return cid2cname;
	}
	
	@Override
	public Map<String, Object> finishAheadOfTime(Integer feeItemId, String optUser) {
		Map<String, Object> result = Maps.newHashMap();
		ContractFeeItem item = contractFeeItemMapper.selectByPrimaryKey(feeItemId);
		
		if (!checkFeeItemWorking(item, result)) {
			return result;
		}
		
		ContractFeeItem updateItem = new ContractFeeItem();
		updateItem.setEndTime(new Date());
		updateItem.setId(item.getId());
		updateItem.setLastUpdateUser(optUser);
		contractFeeItemMapper.updateByPrimaryKeySelective(updateItem);
		
		// 记录日志
		ContractFeeItemLog log = new ContractFeeItemLog(item.getId(), item.getFeeTypeName(), item.getFeeType(), optUser);
		log.setOptType(ContractFeeItemLog.OPT_TYPE_FINISH_AHEAD_OF_TIME);
		log.setContentOriginal(item.getContent());
		log.setContentNew(item.getContent());
		contractFeeItemLogMapper.insert(log);
		
		result.put("suc", true);
		return result;
	}
	
	@Override
	public Map<String, Object> inputRealFee(ContractFeeParam param) {
		Map<String, Object> result = Maps.newHashMap();
		ContractFeeItem item = contractFeeItemMapper.selectByPrimaryKey(param.getFeeItemId());
		// 已开始/已结束的才可以录入实际费用
		if (checkFeeItemNotStarted(item, result)) {
			result.clear();
			result.put("suc", false);
			result.put("msg", "合同费用项" + item.getStatusMsg());
			return result;
		}
		result.clear();
		
		// 检查类型，费用类型 1：固定费用值，2：固定费用率
		if (item.getFeeType() != 1) {
			result.put("suc", false);
			result.put("msg", "不能录入实际费用");
			return result;
		}
		
		try {
			FeeValue fv = Json.fromJson(Json.parse(item.getContent()), FeeValue.class);
			fv.setRealTotalCost(param.getRealTotalCost());
			fv.setRealTotalPerformance(param.getRealTotalPerformance());
			ContractFeeItem updateItem = new ContractFeeItem();
			updateItem.setId(item.getId());
			updateItem.setContent(Json.stringify(Json.toJson(fv)));
			updateItem.setLastUpdateUser(param.getOptUser());
			contractFeeItemMapper.updateByPrimaryKeySelective(updateItem);
			// 记录日志
			int optType = 0;
			if (item.getFeeType() == 1 && StringUtils.isNotBlank(item.getContent())) {
				optType = ContractFeeItemLog.OPT_TYPE_UPDATE_REAL_FEE;
			} else {
				optType = ContractFeeItemLog.OPT_TYPE_ADD_REAL_FEE;
			}
			addLogs(item, updateItem, optType);
		} catch (Exception e) {
			Logger.info("无法为合同费用项录入实际费用，异常：{}",e);
			result.put("suc", false);
			result.put("msg", "无法为合同费用项录入实际费用");
			return result;
		}
		// add by zbc 异步更新订单实际费用
		ebus.post(new CalContractFeeEvent(item.getId(), null));
		result.put("suc", true);
		return result;
	}
	
	@Override
	public Map<String, Object> deleteContractFeeItem(Integer feeItemId) {
		Map<String, Object> result = Maps.newHashMap();
		ContractFeeItem item = contractFeeItemMapper.selectByPrimaryKey(feeItemId);
		// 未开始的才可以删除
		if (!checkFeeItemNotStarted(item, result)) {
			return result;
		}
		
		ContractFeeItem itemDeleted = new ContractFeeItem();
		itemDeleted.setId(item.getId());
		itemDeleted.setDeleted(true);
		boolean suc = contractFeeItemMapper.updateByPrimaryKeySelective(itemDeleted)==1;
		result.put("suc", suc);
		return result;
	}
	
	/**
	 * 解析新增或更新的合同费用项的值
	 * @param feetype
	 * @param param
	 * @return
	 */
	private BaseContractFeeValue parseContractFeeValue(Integer feetype, ContractFeeParam param) {
		// 费用类型 1:固定费用值 2：固定费用率
		if (feetype == 1) {
			FeeValue fv = new FeeValue();
			fv.setEstimatedTotalCost(param.getEstimatedTotalCost());
			fv.setEstimatedTotalPerformance(param.getEstimatedTotalPerformance());
			if (param.getRealTotalCost()!=null) {
				fv.setRealTotalCost(param.getRealTotalCost());
			}
			if (param.getRealTotalPerformance()!=null) {
				fv.setRealTotalPerformance(param.getRealTotalPerformance());
			}
			return fv;
		} else if (feetype == 2) {
			FeeRate fr = new FeeRate();
			fr.setFeeRate(param.getFeeRate());
			return fr;
		}
		return null;
	}

	/**
	 * 检查合同费用项是否未开始
	 * @param item 已有的合同费用项
	 * @return 失败，返回false
	 */
	private boolean checkFeeItemNotStarted(ContractFeeItem item, Map<String, Object> result){
		if (item==null) {
			result.put("suc", false);
			result.put("msg", "合同费用项不存在");
			return false;
		}
		if (!item.notStart()) {
			result.put("suc", false);
			result.put("msg", "合同费用项" + item.getStatusMsg());
			return false;
		}
		return true;
	}
	
	
	/**
	 * 检查合同费用项是否使用中
	 * @param item 已有的合同费用项
	 * @return 失败，返回false
	 */
	private boolean checkFeeItemWorking(ContractFeeItem item, Map<String, Object> result){
		if (item==null) {
			result.put("suc", false);
			result.put("msg", "合同费用项不存在");
			return false;
		}
		if (!item.working()) {
			result.put("suc", false);
			result.put("msg", "合同费用项" + item.getStatusMsg());
			return false;
		}
		return true;
	}

	@Override
	public Page<ContractFeeItem> getContractFeeItemsPage(ContractFeeItemPageQeuryParam param) {
		List<ContractFeeItem> items = Lists.newArrayList();
		int total = contractFeeItemMapper.selectCountByPage(param);
		if (total>0) {
			items = contractFeeItemMapper.selectByPage(param);
		}
		
		Page<ContractFeeItem> page = new Page<>(items, total, param.getCurrPage(), param.getPageSize());
		return page;
	}

	/**
	 * 记录新增/修改费用值/费用率的日志
	 * @param itemOriginal
	 * @param itemNew
	 * @param optType 详见ContractFeeItemLog中的optType
	 */
	private void addLogs(ContractFeeItem itemOriginal, ContractFeeItem itemNew, int optType) {
		if (optType==0) {
			Logger.info("合同费用日志，操作类型为[{}]，不记录日志" + optType);
			return;
		}
		ContractFeeItem workingItem = itemOriginal;
		if (workingItem==null) {
			workingItem = itemNew;
		}
		// 操作人
		String optUser = "";
		if (StringUtils.isNotBlank(workingItem.getLastUpdateUser())) {
			optUser = workingItem.getLastUpdateUser();
		} else {
			optUser = workingItem.getCreateUser();
		}
		
		ContractFeeItemLog log = new ContractFeeItemLog(workingItem.getId(), workingItem.getFeeTypeName(), workingItem.getFeeType(), optUser);
		// 操作前
		log.setOptType(optType);
		if (itemOriginal!=null && StringUtils.isNoneBlank(itemOriginal.getContent())) {
			log.setContentOriginal(itemOriginal.getContent());
		}
		// 操作后
		log.setContentNew(itemNew.getContent());
		
		contractFeeItemLogMapper.insert(log);		
	}

	@Override
	public Map<String, Object> getLogs(Integer feeItemId) {
		Map<String, Object> result = Maps.newHashMap();
		ContractFeeItem item = contractFeeItemMapper.selectByPrimaryKey(feeItemId);
		if (item==null) {
			result.put("suc", false);
			result.put("msg", "参数无效");
			return result;
		}
		
		List<ContractFeeItemLog> list = contractFeeItemLogMapper.selectByFeeItemId(feeItemId);
		// 费用类型 1:固定费用值 2：固定费用率
		List<ContractFeeItemLogDto> dtoList = Lists.transform(list, e->{
			ContractFeeItemLogDto dto = new ContractFeeItemLogDto();
			BeanUtils.copyProperties(e, dto);
			// 更新前的
			dto.setFeeValueOriginal(getContractFeeValue(e.getFeeType(), e.getContentOriginal()));
			// 更新后的
			dto.setFeeValueNew(getContractFeeValue(e.getFeeType(), e.getContentNew()));
			return dto;
		});
		
		result.put("suc", true);
		result.put("result", dtoList);
		return result;
	}

	@Override
	public ContractFeeItemDto getContractFeeItemDto(Integer feeItemId) {
		ContractFeeItem item = contractFeeItemMapper.selectByPrimaryKey(feeItemId);
		if (item==null) {
			return null;
		}
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("feeItemIdList", Arrays.asList(feeItemId));
		List<ContractFeeItemRelatedSku> relatedSkus = relatedSkuMapper.selectByConditions(map);
		ContractFeeItemDto dto = new ContractFeeItemDto();
		BeanUtils.copyProperties(item, dto);
		List<ContractFeeItemRelatedSkuDto> relatedSkuDtos = Lists.transform(relatedSkus, e->{
			ContractFeeItemRelatedSkuDto relatedSkuDto = new ContractFeeItemRelatedSkuDto();
			BeanUtils.copyProperties(e, relatedSkuDto);
			return relatedSkuDto;
		});
		dto.setRelatedSkus(relatedSkuDtos);
		setDtoFeeItemValue(item, dto);
		return dto;
	}
	
	private void setDtoFeeItemValue(ContractFeeItem feeItem, ContractFeeItemDto feeItemDto) {
		if (feeItem==null || feeItemDto==null) {
			return;
		}
		feeItemDto.setContractFeeValue(getContractFeeValue(feeItem.getFeeType(), feeItem.getContent()));
	}
	
	private BaseContractFeeValue getContractFeeValue(int feeType, String content) {
		if (StringUtils.isBlank(content)) {
			return null;
		}
		
		if (feeType==1) {// 固定费用值
			return Json.fromJson(Json.parse(content), FeeValue.class);
		} else if (feeType==2) {// 固定费用率
			return Json.fromJson(Json.parse(content), FeeRate.class);
		} else {
			return null;
		}
	}
	
}
