package services.marketing.promotion.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;

import com.google.inject.Inject;

import dto.marketing.promotion.FullProCondtDto;
import dto.marketing.promotion.FullProPvlgDto;
import dto.marketing.promotion.FullProTypeDto;
import entity.marketing.promotion.PromotionCondition;
import entity.marketing.promotion.PromotionPrivilege;
import entity.marketing.promotion.PromotionType;
import entity.marketing.promotion.RelPromotionTypePrivilegeCondition;
import forms.marketing.promotion.PromotionTypeForm;
import forms.marketing.promotion.PromotionTypeSearchForm;
import mapper.marketing.promotion.ActivityInstanceMapper;
import mapper.marketing.promotion.ConditionJudgementTypeMapper;
import mapper.marketing.promotion.PromotionConditionMapper;
import mapper.marketing.promotion.PromotionPrivilegeMapper;
import mapper.marketing.promotion.PromotionTypeMapper;
import mapper.marketing.promotion.RelPromotionTypePrivilegeConditionMapper;
import services.marketing.promotion.IPromotionTypeService;
import util.marketing.promotion.PageInfo;

/**
 * 促销活动service接口实现
 * 
 * @author huangjc
 * @date 2016年7月25日
 */
public class PromotionTypeService implements IPromotionTypeService {

	@Inject
	private PromotionTypeMapper promotionTypeMapper;
	@Inject
	private PromotionConditionMapper conditionMapper;
	@Inject
	private PromotionPrivilegeMapper privilegeMapper;
	@Inject
	private RelPromotionTypePrivilegeConditionMapper relProTypePvlgCondtMapper;
	@Inject
	private ConditionJudgementTypeMapper condtJgmntTypeMapper;
	@Inject
	private ActivityInstanceMapper activityInstanceMapper;

	@Override
	public void updateUsed(List<Integer> proTypeIdList) {
		promotionTypeMapper.updateUsed(proTypeIdList, true);
	}

	@Override
	public FullProTypeDto getFullProTypeDto(Integer id) {
		// 获取促销类型
		PromotionType promotionType = promotionTypeMapper
				.selectByPrimaryKey(id);
		// 获取促销类型与条件/优惠的关系
		List<RelPromotionTypePrivilegeCondition> relList = relProTypePvlgCondtMapper
				.selectByProTypeId(promotionType.getId());

		// 条件id
		List<Integer> condtIdList = relList.stream()
				.filter(rel -> (rel.getCondtId() != null))
				.map(RelPromotionTypePrivilegeCondition::getCondtId)
				.collect(Collectors.toList());

		// 优惠id
		List<Integer> pvlgIdList = relList.stream()
				.filter(rel -> (rel.getPvlgId() != null))
				.map(RelPromotionTypePrivilegeCondition::getPvlgId)
				.collect(Collectors.toList());

		// 获取促销类型的条件
		List<PromotionCondition> condtList = conditionMapper
				.selectByIdList(condtIdList);
		// 条件模板和条件判断类型
		List<FullProCondtDto> fullProCondtDtoList = new ArrayList<>(
				condtList.size());
		FullProCondtDto fullCondtDto;
		try {
			for (PromotionCondition condt : condtList) {
				fullCondtDto = new FullProCondtDto();
				BeanUtils.copyProperties(fullCondtDto, condt);
				// 获取条件的判断类型
				fullCondtDto.setCondtJgmntTypeList(condtJgmntTypeMapper
						.selectByCondtId(condt.getId()));
				fullProCondtDtoList.add(fullCondtDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取促销类型的优惠
		List<PromotionPrivilege> pvlgList = privilegeMapper
				.selectByIdList(pvlgIdList);
		List<FullProPvlgDto> fullProPvlgDtoList = new ArrayList<>(
				condtList.size());
		FullProPvlgDto fullProPvlgDto;
		try {
			for (PromotionPrivilege pvlg : pvlgList) {
				fullProPvlgDto = new FullProPvlgDto();
				BeanUtils.copyProperties(fullProPvlgDto, pvlg);
				fullProPvlgDtoList.add(fullProPvlgDto);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		FullProTypeDto fullProTypeDto = new FullProTypeDto();
		try {
			BeanUtils.copyProperties(fullProTypeDto, promotionType);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		fullProTypeDto.setFullProCondtDtoList(fullProCondtDtoList);
		fullProTypeDto.setFullProPvlgDtoList(fullProPvlgDtoList);

		return fullProTypeDto;
	}

	@Override
	public PageInfo<PromotionType> getPromotionTypePage(
			PromotionTypeSearchForm form) {
		List<PromotionType> proTypeList = promotionTypeMapper
				.selectProTypeList(form);
		int proTypeCount = promotionTypeMapper.selectProTypeCount(form);
		PageInfo<PromotionType> pageInfo = new PageInfo<PromotionType>(
				form.getPageSize(), proTypeCount, form.getCurr(), proTypeList);
		return pageInfo;
	}

	@Override
	public boolean updatePromotionTypeDto(PromotionTypeForm proTypeForm) {
		Integer proTypeId = proTypeForm.getId();

		// 删除此促销类型与条件/优惠的关系
		relProTypePvlgCondtMapper.deleteByProTypeId(proTypeId);
		
		// 判断所选的条件是否可以有额外的指定属性
		String condtIds = proTypeForm.getCondtIds();
//		boolean hasExtCondt = hasExtCondt(condtIds);

		// 更新促销类型
		PromotionType proType = new PromotionType();
		proType.setId(proTypeId);
		proType.setName(proTypeForm.getName());
		proType.setAttr(proTypeForm.getAttr());
		proType.setDescription(proTypeForm.getDescription());
		proType.setLastUpdateUser(proTypeForm.getLastUpdateUser());
		int count = promotionTypeMapper.updateByPrimaryKeySelective(proType);
		// 更新失败
		if (count != 1) {
			return false;
		}

		// 重新保存关系
		saveProTypeAndCondtOrPvlgRels(proTypeId, condtIds, proTypeForm.getPvlgIds());

		return true;
	}

	@Override
	public boolean deleteProTypeById(Integer id) {
		// 删除促销类型
		int result = promotionTypeMapper.deleteSoftlyByPrimaryKey(id);
		// TODO 是否要解除关系绑定

		// 将引用了此促销类型的活动实例删除掉
		Integer proTypeId = id;
		activityInstanceMapper.deleteSoftlyByProTypeId(proTypeId);

		return result == 1;
	}

	@Override
	public List<PromotionCondition> getProCondtListByAttr(short attr) {
		return conditionMapper.getProCondtListByAttr(attr, false);
	}

	@Override
	public List<PromotionPrivilege> getProPvlgListByAttr(short attr) {
		return privilegeMapper.getProPvlgListByAttr(attr,false);
	}

	@Override
	public boolean insertPromotionTypeDto(PromotionTypeForm proTypeForm) {
		// 判断所选的条件是否可以有额外的指定属性
		String condtIds = proTypeForm.getCondtIds();
//		boolean hasExtCondt = hasExtCondt(condtIds);
		
		// 保存促销类型
		PromotionType proType = new PromotionType();
		proType.setName(proTypeForm.getName());
		proType.setAttr(proTypeForm.getAttr());
		proType.setDescription(proTypeForm.getDescription());
		proType.setCreateUser(proTypeForm.getCreateUser());
		int insertCount = promotionTypeMapper.insert(proType);
		if (insertCount != 1) {
			return false;
		}

		// 保存促销类型与条件/优惠的关系
		saveProTypeAndCondtOrPvlgRels(proType.getId(), condtIds, proTypeForm.getPvlgIds());

		return true;
	}

//	private boolean hasExtCondt(String condtIds) {
//		List<Integer> condtIdList = Stream.of(condtIds.split(",")).map(Integer::valueOf).collect(Collectors.toList());
//		List<PromotionCondition> condtList = conditionMapper.selectByIdList(condtIdList);
//		boolean hasExtCondt = condtList.stream().anyMatch(condt -> condt.isHasExtCondt());
//		return hasExtCondt;
//	}

	/**
	 * 保存促销类型与条件/优惠的关系
	 * 
	 * @param proTypeId
	 *            促销类型id
	 * @param condtIds
	 *            促销类型的条件ids
	 * @param pvlgIds
	 *            促销类型的优惠ids
	 */
	private void saveProTypeAndCondtOrPvlgRels(Integer proTypeId,
			String condtIds, String pvlgIds) {
		// 条件id
		List<Integer> condtIdList = new ArrayList<Integer>();
		for (String idStr : condtIds.split(",")) {
			condtIdList.add(Integer.valueOf(idStr));
		}

		// 优惠id
		List<Integer> pvlgIdList = new ArrayList<Integer>();
		for (String idStr : pvlgIds.split(",")) {
			pvlgIdList.add(Integer.valueOf(idStr));
		}

		saveProTypeAndCondtOrPvlgRels(proTypeId, condtIdList, pvlgIdList);
	}

	/**
	 * 保存促销类型与条件/优惠的关系
	 * 
	 * @param proTypeId
	 *            促销类型id
	 * @param condtIdList
	 *            促销类型的条件ids
	 * @param pvlgIdList
	 *            促销类型的优惠ids
	 */
	private void saveProTypeAndCondtOrPvlgRels(Integer proTypeId,
			List<Integer> condtIdList, List<Integer> pvlgIdList) {
		List<RelPromotionTypePrivilegeCondition> relList = new ArrayList<>();

		// 促销类型与条件关系
		condtIdList.forEach(condtIdStr -> {
			relList.add(new RelPromotionTypePrivilegeCondition.Builder()
					.setProTypeId(proTypeId)
					.setCondtId(Integer.valueOf(condtIdStr))
					.buildTypeAndCondtRel());
		});

		// 促销类型与优惠关系
		pvlgIdList.forEach(pvlgIdStr -> {
			relList.add(new RelPromotionTypePrivilegeCondition.Builder()
					.setProTypeId(proTypeId)
					.setPvlgId(Integer.valueOf(pvlgIdStr))
					.buildTypeAndPvlgRel());
		});

		// 保存关系
		relProTypePvlgCondtMapper.insertBatch(relList);
	}

	@Override
	public boolean canUseThisName(PromotionTypeForm proTypeForm) {
		PromotionType proTypeByName = promotionTypeMapper
				.selectProTypeByName(proTypeForm.getName());
		// 不存在，可以使用
		if (Objects.isNull(proTypeByName)) {
			return true;
		}

		// 判断是否是更新
		Integer id = proTypeForm.getId();
		if (id == null) {
			// id不为null，不是更新
			return false;
		}

		// 说明是更新
		if (proTypeByName.getId().intValue() == proTypeForm.getId().intValue()) {
			return true;
		}

		return false;
	}

	@Override
	public void copy(FullProTypeDto typeDtoExists, String newProTypeName) {

		// 新的促销类型
		PromotionType newProType = new PromotionType();
		try {
			BeanUtils.copyProperties(newProType, typeDtoExists);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		newProType.setId(null);
		newProType.setName(newProTypeName);
		promotionTypeMapper.insert(newProType);

		// 条件id
		List<Integer> condtIdList = typeDtoExists.getFullProCondtDtoList().stream().map(FullProCondtDto::getId).collect(Collectors.toList());

		// 优惠id
		List<Integer> pvlgIdList = typeDtoExists.getFullProPvlgDtoList().stream().map(FullProPvlgDto::getId).collect(Collectors.toList());

		saveProTypeAndCondtOrPvlgRels(newProType.getId(), condtIdList, pvlgIdList);
	}

}
