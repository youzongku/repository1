package mapper.marketing.promotion;

import entity.marketing.promotion.PrivilegeJudgementType;
/**
 * 优惠判断类型mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface PrivilegeJudgementTypeMapper {
	int deleteSoftlyByPrimaryKey(Integer id);
	
    int insert(PrivilegeJudgementType record);

    PrivilegeJudgementType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PrivilegeJudgementType record);

}