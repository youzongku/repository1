package mapper.product_inventory;

import dto.product_inventory.ProductMicroInventoryDetailDto;
import dto.product_inventory.ProductMicroInventoryDetailSearchDto;
import entity.product_inventory.ProductMicroInventoryDetail;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ProductMicroInventoryDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductMicroInventoryDetail record);

    int insertSelective(ProductMicroInventoryDetail record);

    ProductMicroInventoryDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductMicroInventoryDetail record);

    int updateByPrimaryKey(ProductMicroInventoryDetail record);

	List<ProductMicroInventoryDetail> selectByParams(ProductMicroInventoryDetailSearchDto parms);

	ProductMicroInventoryDetail selectByParam(ProductMicroInventoryDetail param);

	/**
	 * 为实现微仓商品库存扣减逻辑
	 * @param microInventoryDetailParam
	 * @return
	 */
	List<ProductMicroInventoryDetail> selectmicroInventoryDetailListForDeductByParam(
			ProductMicroInventoryDetail microInventoryDetailParam);

    List<ProductMicroInventoryDetail> selectByParamDto(@Param("paramDto")ProductMicroInventoryDetailDto productMicroInventoryDetailDto);

    /**
     * 查询微仓明细用来后台展示
     * @param reqParam
     * @return
     */
	List<ProductMicroInventoryDetail> selectBySerarchDto(ProductMicroInventoryDetailSearchDto reqParam);

	int selectTotalBySerarchDto(ProductMicroInventoryDetailSearchDto reqParam);

	List<ProductMicroInventoryDetail> selectMicroDetailBySearchDto(ProductMicroInventoryDetailSearchDto parms);

	/**
	 * 查询指定sku，wareid，account所有过期日期
	 * @param microDetailParam
	 * @return
	 */
	List<Date> getMicroDetailExpirdateDates(ProductMicroInventoryDetail microDetailParam);

	/**
	 * 查询指定过期日期，sku，wareId，account的库存数据
	 * @param reqParam
	 * @return
	 */
	ProductMicroInventoryDetail selectMicroDetailStockByExpirdate(ProductMicroInventoryDetail reqParam);

	List<ProductMicroInventoryDetail> selectProductMicroInventoryDetailsGroupByDate(@Param("account") String account, @Param("sku") String sku);
}