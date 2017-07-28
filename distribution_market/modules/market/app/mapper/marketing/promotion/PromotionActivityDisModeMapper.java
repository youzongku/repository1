package mapper.marketing.promotion;

import java.util.List;

import entity.marketing.promotion.PromotionActivityDisMode;

public interface PromotionActivityDisModeMapper {
	int deleteByPrimaryKey(Integer id);

	int deleteByProActId(Integer proActId);

	int insert(PromotionActivityDisMode record);

	int insertBatch(List<PromotionActivityDisMode> records);

	PromotionActivityDisMode selectByPrimaryKey(Integer id);

	List<PromotionActivityDisMode> selectByProActId(Integer proActId);

	int updateByPrimaryKeySelective(PromotionActivityDisMode record);

}