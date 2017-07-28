package mapper.marketing.promotion;

import org.apache.ibatis.annotations.Param;

import entity.marketing.promotion.RelPrivilegeDataSource;
/**
 * 优惠与优惠数据来源关系mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface RelPrivilegeDataSourceMapper {
	/**
	 * 删除优惠与优惠数据来源关系
	 * @param pvlgId 优惠id
	 * @param dsId 优惠数据来源id
	 * @return
	 */
	int deleteRel(@Param("pvlgId")Integer pvlgId,@Param("dsId")Integer dsId);
	
    int deleteByPrimaryKey(Integer id);

    int insert(RelPrivilegeDataSource record);

    RelPrivilegeDataSource selectByPrimaryKey(Integer id);
}