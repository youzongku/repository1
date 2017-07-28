package mapper.marketing.promotion;

import entity.marketing.promotion.RelPrivilegeJudgementType;
/**
 * 优惠与优惠判断类型mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface RelPrivilegeJudgementTypeMapper {
	/**
	 * 删除优惠与优惠判断类型关系
	 * @param pvlgId 优惠id
	 * @param pvlgJgmntTypeId 优惠类型id
	 * @return
	 */
	int deleteRel(Integer pvlgId,Integer pvlgJgmntTypeId);
	
    int deleteByPrimaryKey(Integer id);

    int insert(RelPrivilegeJudgementType record);

    RelPrivilegeJudgementType selectByPrimaryKey(Integer id);

}