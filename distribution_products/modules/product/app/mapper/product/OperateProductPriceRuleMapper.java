package mapper.product;

import java.util.List;

import dto.product.OperateRuleDto;
import entity.product.OperateProductPriceRule;

/**
 * @author zbc
 * 2016年7月28日 上午11:50:36
 */
public interface OperateProductPriceRuleMapper  {
    int deleteByPrimaryKey(Integer id);

    int insert(OperateProductPriceRule record);

    int insertSelective(OperateProductPriceRule record);

    OperateProductPriceRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OperateProductPriceRule record);

    int updateByPrimaryKey(OperateProductPriceRule record);

	/**
	 * 分页查询
	 * @author zbc
	 * @since 2016年8月4日 下午3:29:05
	 */
	List<OperateProductPriceRule> getLogPages(OperateRuleDto search);
	
	/**
	 * 分页记录数
	 * @author zbc
	 * @since 2016年8月4日 下午3:29:13
	 */
	int getLogCount(OperateRuleDto search);
	
	/**
	 * 批量插入操作日志
	 * @author zbc
	 * @since 2016年8月16日 下午6:15:11
	 */
	int batchInsert(List<OperateProductPriceRule> list);
}