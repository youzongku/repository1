package mapper.marketing.promotion;

import org.apache.ibatis.annotations.Param;

import entity.marketing.promotion.RelConditionDataSource;
/**
 * 条件与条件数据来源关系mapper
 * @author huangjc
 * @date 2016年7月25日
 */
public interface RelConditionDataSourceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RelConditionDataSource record);

    RelConditionDataSource selectByPrimaryKey(Integer id);

    /**
     * 删除条件与条件数据来源关系
     * @param dsId 条件数据来源id
     * @param condtId 条件id
     * @return
     */
    int deleteRel(@Param("dsId")Integer dsId, @Param("condtId")Integer condtId);
}