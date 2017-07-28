package mapper.marketing.promotion;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.marketing.promotion.PromotionPrivilege;
/**
 * 促销优惠mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface PromotionPrivilegeMapper {
	
	/**
	 * 根据条件属性获取优惠
	 * @param attr 条件属性
	 * @param isDelete
	 * @return
	 */
	List<PromotionPrivilege> getProPvlgListByAttr(@Param("attr")short attr, @Param("isDelete")boolean isDelete);
	
	List<PromotionPrivilege> selectAll(boolean isDelete);
	
	List<PromotionPrivilege> selectByIdList(@Param("idList")List<Integer> idList);
	
    int insert(PromotionPrivilege record);

    PromotionPrivilege selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PromotionPrivilege record);
}