package mapper.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.product.OperatePriceLogDto;
import entity.product.OperateProductPrice;

/**
 * @author zbc
 *
 * 2016年7月28日 上午11:50:04
 */
public interface OperateProductPriceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OperateProductPrice record);

    int insertSelective(OperateProductPrice record);

    OperateProductPrice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OperateProductPrice record);

    int updateByPrimaryKey(OperateProductPrice record);
    
    int batchInsert(List<OperateProductPrice> list);

	/**
	 * @param priceIid  价格表id
	 * @param fNameList 字段名称
	 * @author zbc
	 * @since 2016年11月5日 下午12:16:59
	 */
	List<OperateProductPrice> getRecordList(@Param("priceIid")Integer priceIid,@Param("fNameList")List<String> fNamelist);

	/**
	 * 分页查询价格操作日志
	 * @author zbc
	 * @since 2016年8月4日 上午10:05:35
	 */
	List<OperateProductPrice> getRecordPages(OperatePriceLogDto search);
	
	
	/**
	 * 获取查询记录数
	 * @author zbc
	 * @since 2016年8月4日 上午10:50:23
	 */
	int getRecordCount(OperatePriceLogDto search);
	
	
}