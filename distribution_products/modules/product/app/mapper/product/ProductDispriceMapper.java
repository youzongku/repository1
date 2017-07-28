package mapper.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.product.ClearancePriceDto;
import dto.product.ProductDispriceDto;
import dto.product.ProductDispriceSearch;
import entity.product.ProductDisprice;

/**
 * @author zbc
 * 2016年7月28日 上午11:50:46
 */
public interface ProductDispriceMapper{
    int deleteByPrimaryKey(Integer id);

    int insert(ProductDisprice record);

    int insertSelective(ProductDisprice record);

    ProductDispriceDto selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductDisprice record);

    int updateByPrimaryKey(ProductDisprice record);
    
    /**
     * 分页查询数据
     * @author zbc
     * @since 2016年7月29日 上午9:28:13
     */
    List<ProductDispriceDto> getProductDisPrice(ProductDispriceSearch dto);
    
    /**
     * 获取查询总数
     * @author zbc
     * @since 2016年7月29日 上午9:29:15
     */
    int getProductDisPriceCount(ProductDispriceSearch dto);
    
    /**
     * 获取可编辑数据
     * @author zbc
     * @since 2016年7月29日 下午5:55:20
     */
    List<ProductDispriceDto> getEditPriceList(ProductDispriceSearch dto);
    
    int batchUpdate(List<ProductDisprice> list);
    
    List<ProductDispriceDto> getB2cEditList(@Param("proList")List list);
    
    int batchUpdateType(List<ProductDisprice> list);
    
    ProductDispriceDto selectDisprice(@Param("sku")String sku,@Param("warehouseId")Integer warehouseId);
    
    List<ClearancePriceDto> pageSearch(ProductDispriceSearch dto);
    
    Integer pageCount(ProductDispriceSearch dto);

    /**
     * 批量更新清货价格
     * @param priceWaitUpdateLists
     * @return
     */
	int batchUpdateClearancePrice(@Param("list")List<ProductDisprice> priceWaitUpdateLists);

	List<ProductDisprice> getProductDispriceBySkuAndStockId(@Param("sku")String sku, @Param("stockId")int stockId);
}