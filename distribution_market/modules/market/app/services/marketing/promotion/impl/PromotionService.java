package services.marketing.promotion.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.marketing.promotion.ActInstWithProTypeDto;
import dto.marketing.promotion.CommodityDetail;
import dto.marketing.promotion.FullActInstDto;
import dto.marketing.promotion.FullCondtInstDto;
import dto.marketing.promotion.FullProActDto;
import dto.marketing.promotion.FullProCondtDto;
import dto.marketing.promotion.FullProPvlgDto;
import dto.marketing.promotion.FullProTypeDto;
import dto.marketing.promotion.FullPvlgInstDto;
import dto.marketing.promotion.OrderPageReturnsDto;
import dto.marketing.promotion.OrderPromotionActivityDto;
import dto.marketing.promotion.ProActWithActInstsAndProTypesDto;
import dto.marketing.promotion.condt.value.FinalCondtValue;
import dto.marketing.promotion.pvlg.value.Donation.SingleDonation;
import dto.marketing.promotion.pvlg.value.FinalPvlgValue;
import entity.marketing.promotion.ActivityInformationLog;
import entity.marketing.promotion.ActivityInstance;
import entity.marketing.promotion.ConditionInstance;
import entity.marketing.promotion.ConditionInstanceExt;
import entity.marketing.promotion.ConditionJudgementType;
import entity.marketing.promotion.PrivilegeInstance;
import entity.marketing.promotion.PromotionActivity;
import entity.marketing.promotion.PromotionActivityDisMode;
import forms.marketing.promotion.ActivityInstanceSearchForm;
import mapper.marketing.promotion.ActivityInformationLogMapper;
import mapper.marketing.promotion.ActivityInstanceMapper;
import mapper.marketing.promotion.ConditionInstanceExtMapper;
import mapper.marketing.promotion.ConditionInstanceMapper;
import mapper.marketing.promotion.ConditionJudgementTypeMapper;
import mapper.marketing.promotion.PrivilegeInstanceMapper;
import mapper.marketing.promotion.PromotionActivityDisModeMapper;
import mapper.marketing.promotion.PromotionActivityMapper;
import play.Logger;
import play.libs.Json;
import services.marketing.promotion.IHttpService;
import services.marketing.promotion.IPromotionService;
import services.marketing.promotion.IPromotionTypeService;
import util.marketing.promotion.JsonCaseUtil;
import util.marketing.promotion.PageInfo;

/**
 * 促销活动实例接口实现
 * 
 * @author huangjc
 * @date 2016年7月25日
 */
public class PromotionService implements IPromotionService {
	@Inject
	private ActivityInstanceMapper activityInstanceMapper;
	@Inject
	private PromotionActivityMapper promotionActivityMapper;
	@Inject
	private IPromotionTypeService promotionTypeService;
	@Inject
	private ConditionInstanceMapper conditionInstanceMapper;
	@Inject
	private PrivilegeInstanceMapper privilegeInstanceMapper;
	@Inject
	private ConditionJudgementTypeMapper condtJgmntTypeMapper;
	@Inject
	private ActivityInformationLogMapper activityInformationLogMapper;
	@Inject
	private PromotionActivityDisModeMapper promotionActivityDisModeMapper;
	@Inject
	private ConditionInstanceExtMapper conditionInstanceExtMapper;
	@Inject
	private IHttpService  httpService;
	
	private static DecimalFormat f = new DecimalFormat("###0.00");
	static {
		f.setRoundingMode(RoundingMode.HALF_UP);
	}
	@Override
	public PromotionActivity getPromotionActivityById(int proActId) {
		PromotionActivity activity = promotionActivityMapper
				.selectByPrimaryKey(proActId);
		activity.setDisModeList(promotionActivityDisModeMapper
				.selectByProActId(activity.getId()));
		return activity;
	}

	@Override
	public FullProActDto getFullProActDtoByProActId(int proActId) {
		// 促销活动
		PromotionActivity proAct = this.promotionActivityMapper
				.selectByPrimaryKey(proActId);
		// 活动实例
		List<ActivityInstance> actInstList = this.activityInstanceMapper
				.selectByProActId(proAct.getId());
		
		// 查询模式
		List<PromotionActivityDisMode> disModeList = promotionActivityDisModeMapper.selectByProActId(proActId);
		
		FullProActDto fullProActDto = new FullProActDto();
		try {
			BeanUtils.copyProperties(fullProActDto, proAct);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fullProActDto.setDisModeList(disModeList);

		// 查询全的活动实例
		actInstList.forEach(actInst -> {
			fullProActDto.getFullActInstDtoList().add(
					this.getFullActInstDtoByActInstId(actInst.getId()));
		});

		return fullProActDto;
	}
	
	@Override
	public FullActInstDto getFullActInstDtoByActInstId(int actInstId) {
		/*
		// 获取活动实例
		ActivityInstance actInst = activityInstanceMapper
				.selectByPrimaryKey(actInstId);
		if (actInst == null) {
			return null;
		}

		FullActInstDto fullActInstDto = new FullActInstDto();
		try {
			BeanUtils.copyProperties(fullActInstDto, actInst);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<FullCondtInstDto> fullCondtInstDtoList = new ArrayList<>();
//		List<FullPvlgInstDto> fullPvlgInstDtoList = new ArrayList<>();
//		fullActInstDto.setFullPvlgInstDtoList(fullPvlgInstDtoList);// 优惠实例

		// 获取活动实例的条件实例
		List<ConditionInstance> condtInstList = conditionInstanceMapper
				.selectByActInstId(actInstId);
		if(condtInstList.size() < 1){
			return null;
		}
		// 获取条件的判断类型
		condtInstList
				.forEach(condtInst -> {
					FullCondtInstDto fullCondtInstDto = new FullCondtInstDto();
					try {
						BeanUtils.copyProperties(fullCondtInstDto, condtInst);
					} catch (Exception e) {
						e.printStackTrace();
					}
					List<ConditionJudgementType> condtJgmntTypeList = condtJgmntTypeMapper
							.selectByCondtId(condtInst.getCondtId());
					fullCondtInstDto.setCondtJgmntTypeList(condtJgmntTypeList);
					fullCondtInstDtoList.add(fullCondtInstDto);
				});

		// 条件实例id
		List<Integer> condtInstIdList = fullCondtInstDtoList.stream().map(FullCondtInstDto::getId).collect(Collectors.toList());
		// 批量查询优惠
		List<PrivilegeInstance> pvlgInstList = privilegeInstanceMapper.selectByCondtInstIdList(condtInstIdList);
		// 额外的指定属性
		List<ConditionInstanceExt> extList = conditionInstanceExtMapper.selectByCondtInstIdList(condtInstIdList);
				
		// 按照条件实例id分组
		Map<Integer, List<PrivilegeInstance>> pvlgInstMapByCondtInstId = pvlgInstList.stream().collect(Collectors.groupingBy(PrivilegeInstance::getCondtInstId));
		Map<Integer, List<ConditionInstanceExt>> extMapByCondtInstId = extList.stream().collect(Collectors.groupingBy(ConditionInstanceExt::getCondtInstId));
		try {
			FullPvlgInstDto fullPvlgInstDto;
			for(FullCondtInstDto fullCondtInstDto : fullCondtInstDtoList){
				fullPvlgInstDto = new FullPvlgInstDto();
				if(pvlgInstMapByCondtInstId.get(fullCondtInstDto.getId()) != null){
					BeanUtils.copyProperties(fullPvlgInstDto, pvlgInstMapByCondtInstId.get(fullCondtInstDto.getId()).get(0));
					
					// 给条件实例设置优惠和额外的指定属性
					fullCondtInstDto.setFullPvlgInstDto(fullPvlgInstDto);
					fullCondtInstDto.setCondtInstExt(extMapByCondtInstId.get(fullCondtInstDto.getId()).get(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 对条件实例进行分组，按照parentId来分组
		Map<Boolean, List<FullCondtInstDto>> FullCondtInstDtoMapByParentId = fullCondtInstDtoList.stream().
				collect(Collectors.partitioningBy(fullCondtInstDto -> {return fullCondtInstDto.getParentId() == null;}));
		List<FullCondtInstDto> parentFullCondtInstDtoList = FullCondtInstDtoMapByParentId.get(true);
		List<FullCondtInstDto> childFullCondtInstDtoList = FullCondtInstDtoMapByParentId.get(false);
		
		// 将子条件绑定到对应的父条件中
		parentFullCondtInstDtoList.forEach(parent -> {
			if(parent.getCondtInstExt().isStepped()){// 要可阶梯才有子条件
				List<FullCondtInstDto> children = childFullCondtInstDtoList.stream().filter(child -> 
				child.getParentId().intValue() == parent.getId().intValue()).collect(Collectors.toList());
				parent.setSubCondtInstDtoList(children);
			}
		});
		
		fullActInstDto.setFullCondtInstDtoList(parentFullCondtInstDtoList);// 条件实例
		*/
		

		// 获取活动实例
		ActivityInstance actInst = activityInstanceMapper
				.selectByPrimaryKey(actInstId);
		if (actInst == null) {
			return null;
		}

		FullActInstDto fullActInstDto = new FullActInstDto();
		try {
			BeanUtils.copyProperties(fullActInstDto, actInst);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<FullCondtInstDto> fullCondtInstDtoList = new ArrayList<>();
		fullActInstDto.setFullCondtInstDtoList(fullCondtInstDtoList);// 条件实例

		// List<FullPvlgInstDto> fullPvlgInstDtoList = new ArrayList<>();
		// fullActInstDto.setFullPvlgInstDtoList(fullPvlgInstDtoList);// 优惠实例

		// 获取活动实例的条件实例
		List<ConditionInstance> condtInstList = conditionInstanceMapper
				.selectByActInstId(actInstId);
		if (condtInstList.size() < 1) {
			return null;
		}
		// 获取条件的判断类型
		condtInstList
				.forEach(condtInst -> {
					FullCondtInstDto fullCondtInstDto = new FullCondtInstDto();
					try {
						BeanUtils.copyProperties(fullCondtInstDto, condtInst);
					} catch (Exception e) {
						e.printStackTrace();
					}
					List<ConditionJudgementType> condtJgmntTypeList = condtJgmntTypeMapper
							.selectByCondtId(condtInst.getCondtId());
					fullCondtInstDto.setCondtJgmntTypeList(condtJgmntTypeList);
					fullCondtInstDtoList.add(fullCondtInstDto);
				});

		// 条件实例id
		List<Integer> condtInstIdList = fullCondtInstDtoList.stream()
				.map(FullCondtInstDto::getId).collect(Collectors.toList());
		// 批量查询优惠
		List<PrivilegeInstance> pvlgInstList = privilegeInstanceMapper
				.selectByCondtInstIdList(condtInstIdList);
		// 额外的指定属性
		List<ConditionInstanceExt> extList = conditionInstanceExtMapper
				.selectByCondtInstIdList(condtInstIdList);

		// 按照条件实例id分组
		Map<Integer, List<PrivilegeInstance>> pvlgInstMapByCondtInstId = pvlgInstList
				.stream().collect(
						Collectors
								.groupingBy(PrivilegeInstance::getCondtInstId));
		Map<Integer, List<ConditionInstanceExt>> extMapByCondtInstId = extList
				.stream()
				.collect(
						Collectors
								.groupingBy(ConditionInstanceExt::getCondtInstId));
		try {
			FullPvlgInstDto fullPvlgInstDto;
			for (FullCondtInstDto fullCondtInstDto : fullCondtInstDtoList) {
				fullPvlgInstDto = new FullPvlgInstDto();
				if (pvlgInstMapByCondtInstId.get(fullCondtInstDto.getId()) != null) {
					BeanUtils.copyProperties(
							fullPvlgInstDto,
							pvlgInstMapByCondtInstId.get(
									fullCondtInstDto.getId()).get(0));

					fullCondtInstDto.setFullPvlgInstDto(fullPvlgInstDto);
					fullCondtInstDto.setCondtInstExt(extMapByCondtInstId.get(
							fullCondtInstDto.getId()).get(0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fullActInstDto;
	}
	
	@Override
	public FullCondtInstDto getFullCondtInstDtoByCondtInstId(int condtInstId) {
		FullCondtInstDto fullCondtInstDto = new FullCondtInstDto();
		ConditionInstance condtInst = conditionInstanceMapper.selectByPrimaryKey(condtInstId);
		try {
			BeanUtils.copyProperties(fullCondtInstDto, condtInst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 条件判断类型
		List<ConditionJudgementType> condtJgmntTypeList = condtJgmntTypeMapper
				.selectByCondtId(condtInst.getCondtId());
		fullCondtInstDto.setCondtJgmntTypeList(condtJgmntTypeList);
		
		// 优惠
		FullPvlgInstDto fullPvlgInstDto = new FullPvlgInstDto();
		PrivilegeInstance pvlgInst = privilegeInstanceMapper.selectByCondtInstId(condtInstId);
		try {
			BeanUtils.copyProperties(fullPvlgInstDto, pvlgInst);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		fullCondtInstDto.setFullPvlgInstDto(fullPvlgInstDto);
		
		ConditionInstanceExt ext = conditionInstanceExtMapper.selectByCondtInstId(condtInst.getId());
		fullCondtInstDto.setCondtInstExt(ext);

		return fullCondtInstDto;
	}

	@Override
	public PageInfo<PromotionActivity> getAllActivity(
			ActivityInstanceSearchForm form) {
		Integer size = promotionActivityMapper.getRowSize(form);
		List<PromotionActivity> list = promotionActivityMapper
				.getAllActivity(form);
		Logger.info("form:" + form);
		PageInfo<PromotionActivity> pageInfo = new PageInfo<PromotionActivity>(
				form.getPageSize(), size, form.getCurr(), list);
		return pageInfo;
	}
	
	@Override
	public ProActWithActInstsAndProTypesDto getProActWithActInstsAndProTypesDto(
			Integer proActId) {
		ProActWithActInstsAndProTypesDto dto = new ProActWithActInstsAndProTypesDto();
		dto.setActInstWithProTypeDtoList(activityInstanceMapper
				.getActivityInstanceAndType(proActId));
		PromotionActivity proAct = promotionActivityMapper
				.selectByPrimaryKey(proActId);
		try {
			BeanUtils.copyProperties(dto, proAct);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		// 模式
		dto.setDisModeList(promotionActivityDisModeMapper
				.selectByProActId(proActId));
		return dto;
	}
	
	@Override
	public Map<Integer,List<ActInstWithProTypeDto>> getActInstWithProTypeDtoListByGroup(Integer proActId) {
		
		List<ActInstWithProTypeDto> ActInstWithProTypeDtoList = activityInstanceMapper.getActivityInstanceAndType(proActId);		
		
		// 分组操作
		Map<Integer, List<ActInstWithProTypeDto>> groupByProTypeId = ActInstWithProTypeDtoList.stream().collect(Collectors.groupingBy(ActInstWithProTypeDto::getProTypeId));
		
		return groupByProTypeId;
	}
	
	@Override
	public boolean addPromotionActivity(PromotionActivity activity) {
		Logger.info("activity:" + activity);
		activity.setDelete(true);
		int count = promotionActivityMapper.insertPromotion(activity);
		Integer proActId = activity.getId();

		List<Integer> modeIdList = Stream.of(activity.getModeIds().split(","))
				.map(Integer::valueOf).collect(Collectors.toList());
		List<String> modeNameList = Arrays.asList(activity.getModeNames()
				.split(","));

		List<PromotionActivityDisMode> disModeList = new ArrayList<>();
		for (int i = 0; i < modeNameList.size(); i++) {
			disModeList.add(new PromotionActivityDisMode(proActId, modeIdList
					.get(i), modeNameList.get(i)));
		}
		promotionActivityDisModeMapper.insertBatch(disModeList);

		return count == 1;
	}
	
	@Override
	public void updateCondtInstsAndPvlgInstsValue(Integer actInstId,
			Integer matchType, List<FinalCondtValue> finalCondtValueList,
			ConditionInstanceExt condtInstExt,
			List<FinalPvlgValue> finalPvlgValueList) {
		// 判断活动条件是全部满足还是满足任意
//		ActivityInstance actInst = new ActivityInstance();
//		actInst.setId(actInstId);
//		actInst.setMatchType(matchType);
//		actInst.setIsSetV((short) 1);
//		activityInstanceMapper.updateByPrimaryKeySelective(actInst);

		// 更新条件的json值
		for(FinalCondtValue fcv : finalCondtValueList){
			fcv.setIsSetV((short)1);
		}
		conditionInstanceMapper.updateBatch(finalCondtValueList);

		// 更新额外的条件
		conditionInstanceExtMapper.updateByPrimaryKeySelective(condtInstExt);
		
		// 更新优惠的json值
		privilegeInstanceMapper.updateBatch(finalPvlgValueList);
	}

	

	@Override
	public boolean updateEndTime(Date date, Integer id, String lastCreateUser) {
		return promotionActivityMapper.updateEndTime(date, id, lastCreateUser) == 1;
	}

	@Override
	public boolean addActivityInstance(int proActId, int proTypeId) {
		// 获取促销类型
		FullProTypeDto fullProTypeDto = promotionTypeService
				.getFullProTypeDto(proTypeId);
		Logger.info("获取到的促销类型============" + fullProTypeDto);

		// 创建促销实例
		ActivityInstance actInst = new ActivityInstance();
		actInst.setProActId(proActId);
		actInst.setProTypeId(proTypeId);
		actInst.setName(fullProTypeDto.getName());
		actInst.setAttr(fullProTypeDto.getAttr());
		// 保存促销实例
		activityInstanceMapper.insert(actInst);

		int actInstId = actInst.getId();
		Logger.info("活动实例id=" + actInstId);

		// 创建条件实例
		List<FullProCondtDto> fullProCondtDtoList = fullProTypeDto
				.getFullProCondtDtoList();
		Logger.info("条件模板=" + fullProCondtDtoList);
//		List<ConditionInstance> condtInstIdList = new ArrayList<ConditionInstance>();
		List<Integer> condtInstIdList = new ArrayList<Integer>();
		for(FullProCondtDto fullProCondtDto : fullProCondtDtoList){
			ConditionInstance condtInst = new ConditionInstance();
			condtInst.setActInstId(actInstId);// 活动实例id
			condtInst.setCondtId(fullProCondtDto.getId());// 条件模板id
			condtInst.setName(fullProCondtDto.getName());// 条件名称
			condtInst.setAttr(fullProCondtDto.getAttr());// 条件属性
			condtInst.setcType(fullProCondtDto.getcType());// 条件的类型
			condtInst.setIsSetV((short) 0);
			condtInst.setHasExtCondt(fullProCondtDto.isHasExtCondt());
//			condtInstList.add(condtInst);
			// 插入数据库
			conditionInstanceMapper.insert(condtInst);
			// 保存条件实例id
			condtInstIdList.add(condtInst.getId());
		}
//		Logger.info("条件实例=" + condtInstList);
		// 保存条件实例
//		conditionInstanceMapper.insertBatch(condtInstList);

		// 创建优惠实例
		List<FullProPvlgDto> fullProPvlgDtoList = fullProTypeDto
				.getFullProPvlgDtoList();
		
		List<PrivilegeInstance> pvlgInstList = new ArrayList<PrivilegeInstance>();
		for(Integer condtInstId : condtInstIdList){
			for(FullProPvlgDto fullProPvlgDto : fullProPvlgDtoList){
				PrivilegeInstance pvlgInst = new PrivilegeInstance();
				pvlgInst.setActInstId(actInstId);// 活动实例id
				pvlgInst.setCondtInstId(condtInstId);// 添加实例id
				pvlgInst.setProPvlgId(fullProPvlgDto.getId());// 优惠模板id
				pvlgInst.setName(fullProPvlgDto.getName());// 优惠名称
				pvlgInst.setpType(fullProPvlgDto.getpType());// 优惠类型
				pvlgInstList.add(pvlgInst);
			}
		}
		
		// 保存优惠实例
		privilegeInstanceMapper.insertBatch(pvlgInstList);
		
		// 创建额外的条件属性，这里不管hasExtCondt是true还是false，都会创建
		// hasExtCondt的作用是：是否解析ConditionInstanceExt的specifyAttrValue字段值
		List<ConditionInstanceExt> extList = new ArrayList<>();
		for(Integer condtInstId : condtInstIdList){
			extList.add(new ConditionInstanceExt(actInstId,condtInstId,false,false));
		}
		conditionInstanceExtMapper.insertBatch(extList);
		
		return true;
	}

	@Override
	public boolean updateActivityInstance(List<ActivityInstance> list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean updatePromotionActivity(PromotionActivity activity) {
		PromotionActivity updateParam = new PromotionActivity();
		int proActId = activity.getId();
		updateParam.setId(proActId);
		updateParam.setName(activity.getName());
		updateParam.setDescription(activity.getDescription());
		updateParam.setStartTime(activity.getStartTime());
		updateParam.setEndTime(activity.getEndTime());
		updateParam.setLastUpdateUser(activity.getLastUpdateUser());
		updateParam.setDelete(false);
		int result = promotionActivityMapper
				.updateByPrimaryKeySelective(updateParam);

		// 模式，先删除，再重新添加
		promotionActivityDisModeMapper.deleteByProActId(proActId);

		List<Integer> modeIdList = Stream.of(activity.getModeIds().split(","))
				.map(Integer::valueOf).collect(Collectors.toList());
		List<String> modeNameList = Arrays.asList(activity.getModeNames()
				.split(","));

		List<PromotionActivityDisMode> disModeList = new ArrayList<>();
		for (int i = 0; i < modeNameList.size(); i++) {
			disModeList.add(new PromotionActivityDisMode(proActId, modeIdList
					.get(i), modeNameList.get(i)));
		}
		promotionActivityDisModeMapper.insertBatch(disModeList);

		return result == 1;
	}

	@Override
	public boolean canUseThisName(Integer id, String name) {
		List<PromotionActivity> proActListByName = promotionActivityMapper
				.getProActivitiesByName(name);
		// 不存在，可以使用
		if (proActListByName == null || proActListByName.size() == 0) {
			return true;
		}

		// 过滤掉过期的活动
		long unexpiredCount = proActListByName.stream()
				.filter(proAct -> proAct.getStatus().intValue() != 3).count();
		// 过滤后，活动数量为0，说明没有活动，名字可以使用
		if (unexpiredCount == 0) {
			return true;
		}

		// 过滤掉过期的活动，拿到第一个未过期活动（同一个名字，未过期活动只有一个）
		List<PromotionActivity> unexpiredProActList = proActListByName.stream()
				.filter(proAct -> proAct.getStatus().intValue() != 3)
				.collect(Collectors.toList());
		PromotionActivity proAct = unexpiredProActList.get(0);

		// 判断是否是更新
		if (id == null) {
			// id不为null，不是更新
			return false;
		}

		// 说明是更新
		if (proAct.getId().intValue() == id.intValue()) {
			return true;
		}

		return false;
	}

	@Override
	public List<FullProActDto> getExecutoryActivity(int mode) {
		List<PromotionActivity> proActList = promotionActivityMapper
				.getExecutoryActivity();

		// 模式
		for (PromotionActivity eachProAct : proActList) {
			eachProAct.setDisModeList(promotionActivityDisModeMapper
					.selectByProActId(eachProAct.getId()));
		}

		proActList = filterByMode(mode, proActList);

		List<FullProActDto> dtoList = new ArrayList<>();
		FullProActDto dto;
		try {
			for (int i = 0; i < proActList.size(); i++) {
				dto = new FullProActDto();
				BeanUtils.copyProperties(dto, proActList.get(i));
				FullProActDto fullProActDto = this.getFullProActDtoByProActId(proActList.get(i).getId());
				dto.setFullActInstDtoList(filterUnSet(fullProActDto.getFullActInstDtoList()));
				dtoList.add(dto);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return dtoList;
	}

	/**
	 * 进行用户模式过滤
	 * 
	 * @param mode
	 *            用户模式
	 * @param dtoList
	 *            正在进行中的活动
	 * @return
	 */
	private List<PromotionActivity> filterByMode(int mode,
			List<PromotionActivity> dtoList) {
		if (dtoList == null || dtoList.size() == 0) {
			return new ArrayList<PromotionActivity>();
		}
		// 过滤操作
		List<PromotionActivity> checkDtoList = dtoList
				.stream()
				.filter(checkDto -> {
					List<Integer> modeIdList = checkDto.getDisModeList()
							.stream().map(PromotionActivityDisMode::getDisModeId)
							.collect(Collectors.toList());
					if (modeIdList.size() == 0) {
						return true;
					}
					long count = modeIdList.stream()
							.filter(modeId -> modeId == mode).count();
					return count == 1;
				}).collect(Collectors.toList());

		return checkDtoList;
	}

	@Override
	public int saveActivityInformationLog(ActivityInformationLog log) {

		return activityInformationLogMapper.insert(log);
	}

	@Override
	public boolean updateProActNotDelete(int proActId, String name) {
		int result = promotionActivityMapper.updateProActNotDelete(proActId,
				name);
		List<Integer> proTypeIdList = activityInstanceMapper
				.selectByProActId(proActId).stream()
				.map(actInst -> actInst.getProTypeId())
				.collect(Collectors.toList());
		promotionTypeService.updateUsed(proTypeIdList);
		return result == 1;
	}

	@Override
	public boolean deleteActInstByIds(String actInstIds) {
		String[] idArr = actInstIds.split(",");
		List<Integer> idList = new ArrayList<Integer>();
		for (String id : idArr) {
			idList.add(Integer.valueOf(id));
		}
		activityInstanceMapper.deleteSoftlyByIdList(idList);
		return true;
	}

	@Override
	public OrderPageReturnsDto getActivityByPvlg(List<Integer> idList) {
		OrderPageReturnsDto dto = null;
		if (idList.size() != 0) {
			dto = promotionActivityMapper.getActivityByPvlg(idList.get(0));

		}
		if (dto == null) {
			dto = new OrderPageReturnsDto();
		}
		dto.setPrvlInst(privilegeInstanceMapper.byIdGetPri(idList));
		return dto;
	}

	@Override
	public int getProActUnSetVCondtInstCount(int proActId) {
		
		FullProActDto fullProActDto = getFullProActDtoByProActId(proActId);
		
		long count = fullProActDto.getFullActInstDtoList().stream()
			.flatMap(fullActInstDto -> fullActInstDto.getFullCondtInstDtoList().stream())
			.filter(fullCondtInstDto -> fullCondtInstDto.getIsSetV() == 0).count();
		
		return (int) count;
	}

	@Override
	public int getActInstCountByProActId(int proActId) {
		int count = activityInstanceMapper.getActInstCountByProActId(proActId);
		return count;
	}

	@Override
	public boolean updatePriorityByCondtInstId(int condtInstId, int priority) {
		int count = conditionInstanceMapper.updatePriorityByPrimaryKey(condtInstId,priority);
		
		return count == 1;
	}

	@Override
	public List<FullProActDto> getPaymentTimeParticipateActivaty(int mode,
			Date paymentTime) {
		List<PromotionActivity> proActList = promotionActivityMapper
				.getPaymentTimeParticipateActivaty(paymentTime);

		// 查询模式
		for (PromotionActivity eachProAct : proActList) {
			eachProAct.setDisModeList(promotionActivityDisModeMapper
					.selectByProActId(eachProAct.getId()));
		}
		proActList = filterByMode(mode, proActList);

		List<FullProActDto> dtoList = new ArrayList<>();
		FullProActDto dto;
		try {
			for (int i = 0; i < proActList.size(); i++) {
				dto = new FullProActDto();
				BeanUtils.copyProperties(dto, proActList.get(i));
				FullProActDto fullProActDto = this
						.getFullProActDtoByProActId(proActList.get(i).getId());
				dto.setFullActInstDtoList(filterUnSet(fullProActDto.getFullActInstDtoList()));
				dtoList.add(dto);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return dtoList;
	}
	
	/**
	 * 过滤掉未设置参数的条件
	 * @author zbc
	 * @since 2016年10月28日 上午10:54:57
	 */
	public  List<FullActInstDto> filterUnSet(List<FullActInstDto> actIntList){
		for(FullActInstDto actInt:actIntList){
			Map<Boolean,List<FullCondtInstDto>>  actInstMap = actInt.getFullCondtInstDtoList().stream().collect(Collectors.partitioningBy(e->e.getCondtJgmntValue()!=null));
			actInt.setFullCondtInstDtoList(actInstMap.get(true));
		}
		return actIntList;
	}

	@Override
	public boolean pausePromotionActivity(int id) {
		int count = promotionActivityMapper.pauseByPrimaryKey(id);
		return count == 1;
	}

	@Override
	public boolean activateProActPaused(int id) {
		PromotionActivity proAct = promotionActivityMapper.selectByPrimaryKey(id);
		// 计算它是否是过期的
		if(proAct.getStatus() == 3){
			return false;
		}
		Date startTime = proAct.getStartTime();
		Date endTime = proAct.getEndTime();
		Date now = new Date();
		int count = 0;
		if(now.after(startTime) && now.before(endTime) && proAct.getStatus() == 4){
			count = promotionActivityMapper.activateProActPaused(id, 2);
		}else if(now.before(startTime)){
			count = promotionActivityMapper.updateStatusByPrimaryKey(id, 1);
		}else if(now.after(endTime)){
			count = promotionActivityMapper.updateStatusByPrimaryKey(id, 3);
		}
		return count == 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> executePvlgInst(List<FullActInstDto> actInstList, OrderPromotionActivityDto dtoArg) {
		Map<String,Object> res = Maps.newHashMap();
		try {
			FullPvlgInstDto  pri = null;
			int multiple = 0;
			JsonNode gift = null;
			double sum = dtoArg.getMoney(); 
			ObjectMapper map = new ObjectMapper();
			List<SingleDonation> list = null;
			List<SingleDonation> giftList = Lists.newArrayList();
			List<FullPvlgInstDto> priList = Lists.newArrayList();
			//商品属性优先于购物车属性, 折扣优先于满减 ,定义排序规则比较器
			Collections.sort(actInstList,new TypeComparator());
			for(FullActInstDto actInst:actInstList){
				pri = actInst.getFullCondtInstDtoList().get(0).getFullPvlgInstDto();
				priList.add(pri);
				multiple = pri.getMultiple();
				gift = Json.parse(pri.getPvlgValue());
				if(pri.getpType() == 1 || pri.getpType() == 4){
					list = map.readValue(gift.get("donations").toString(), new TypeReference<List<SingleDonation>>() {});
					for(SingleDonation donation:list){
						//商品数量要乘以箱规
						donation.setNum(donation.getUnitNum()== 0?donation.getNum()*multiple:
							donation.getUnitNum()*donation.getNum()*multiple);
					}
					giftList.addAll(list);
				}else if(pri.getpType() == 2 || pri.getpType() == 5){
					//TODO 是否要讲减价均摊到每个商品上
					sum = new BigDecimal(sum).subtract(new BigDecimal(gift.get("moneyReduce").asDouble()).multiply(new BigDecimal(multiple))).doubleValue();
				}else if(pri.getpType() == 3 ){
					List<String> skuWareList = Lists.transform(actInst.getCommodits(),e->e.getSku() + "_" + e.getWarehouseId());
					//指定商品打折： 匹配商品打折
					for(CommodityDetail cd:dtoArg.getCommodity()){
						if(skuWareList.contains(cd.getSku()+"_"+cd.getWarehouseId())){
							//匹配商品打折
							cd.setTotalPrice(new BigDecimal(cd.getTotalPrice()).multiply(new BigDecimal(gift.get("num").asDouble())).divide(new BigDecimal(100)).doubleValue());
						}
					}
					//打折出现了冲突
					sum = dtoArg.getCommodity().stream().mapToDouble(e->e.getTotalPrice()).sum();
				}else if(pri.getpType() == 6){
					//购物车打折：总计打折
					sum = new BigDecimal(sum).multiply(new BigDecimal(gift.get("num").asDouble()).divide(new BigDecimal(100))).doubleValue();
				} 
			}
			if(giftList.size()>0){
				List<String> skus = Lists.transform(giftList, z->z.getSku());
				JsonNode result = null;
				try {
					JsonNode proNode = httpService.getProducts(skus, null, null,dtoArg.getUserMode(),dtoArg.getAccount());
					if(proNode.get("data") !=null && proNode.get("data").get("result") != null){
						result = proNode.get("data").get("result");
					}
				} catch (IOException e1) {
				}
				if(result != null){
					Map<String,JsonNode> skuMap = Maps.newHashMap(); 
					for(JsonNode product:result){
						skuMap.put(product.get("csku").asText()+"_"+product.get("warehouseId").asInt(), product);
					}
					JsonNode item = null;
					for(SingleDonation g:giftList){
						item = skuMap.get(g.getSku()+"_"+g.getWarehouseId());
						if(item != null){
							g.setCategoryId(JsonCaseUtil.jsonToInteger(item.get("categoryId")));
							g.setCategoryName(JsonCaseUtil.jsonToString(item.get("cname")));
							g.setRealPrice(JsonCaseUtil.jsonToDouble(item.get("disPrice")));
							g.setMarketPrice(JsonCaseUtil.jsonToDouble(item.get("proposalRetailPrice")));
							g.setClearancePrice(JsonCaseUtil.jsonToDouble(item.get("clearancePrice")));
						}
					}
				}
			}
			res.put("suc", true);
			res.put("sum", f.format(sum));
			res.put("gift", giftList);
			res.put("priList", priList);
			
		} catch (IOException e) {
			Logger.info("获取优惠信息异常",e);
			res.put("suc", false);
			res.put("msg", "获取优惠信息异常");
		}
		return res;
		
	}
	private final static Map<Integer,Integer>  SORTMAP = Maps.newHashMap();
	static{
		SORTMAP.put(1, 1);//商品属性 满赠
		SORTMAP.put(2, 3);//商品属性 满减
		SORTMAP.put(3, 2);//商品属性 折扣
		SORTMAP.put(4, 4);//购物车属性 满赠
		SORTMAP.put(5, 6);//购物车属性 满减
		SORTMAP.put(6, 5);//购物车属性 折扣
	}
	@SuppressWarnings("rawtypes")
	public static class TypeComparator implements Comparator {
		@Override
		public int compare(Object o1, Object o2) {
			FullActInstDto p1 = (FullActInstDto) o1; // 强制转换  
			FullActInstDto p2 = (FullActInstDto) o2; 
			return SORTMAP.get(p2.getFullCondtInstDtoList().get(0).getFullPvlgInstDto().getpType())-
					SORTMAP.get(p1.getFullCondtInstDtoList().get(0).getFullPvlgInstDto().getpType());
		}  
    }  
}
