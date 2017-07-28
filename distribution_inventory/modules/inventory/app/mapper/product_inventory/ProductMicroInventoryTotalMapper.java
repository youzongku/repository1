package mapper.product_inventory;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.product_inventory.MicroInventoryQueryResult;
import dto.product_inventory.ProductInventoryEnquiryRequest;
import dto.product_inventory.ProductInventoryEnquiryResult;
import dto.product_inventory.ProductMicroInventoryDetailSearchDto;
import dto.warehousing.MicroRealWarehouseDto;
import entity.product_inventory.ProductMicroInventoryTotal;

public interface ProductMicroInventoryTotalMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductMicroInventoryTotal record);

    int insertSelective(ProductMicroInventoryTotal record);

    ProductMicroInventoryTotal selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductMicroInventoryTotal record);

    int updateByPrimaryKey(ProductMicroInventoryTotal record);

    /**
     * 根据条件查询微仓总仓
     * @param microInventoryTotal
     * @return
     */
	ProductMicroInventoryTotal selectByParam(ProductMicroInventoryTotal microInventoryTotal);

	int updateMicroTotalStockNum(ProductMicroInventoryTotal microInventoryTotal);

	List<ProductMicroInventoryTotal> selectMicroTotalListByParam(ProductMicroInventoryDetailSearchDto parms);

	int getTotal(ProductMicroInventoryDetailSearchDto parms);
	
	/**
	 * 查询所有微仓库存
	 * 
	 * @param param
	 * @return
	 * @author ye_ziran
	 * @since 2017年1月6日 下午5:48:33
	 */
	List<ProductInventoryEnquiryResult> microInventory(ProductInventoryEnquiryRequest param);
	
	public List<MicroRealWarehouseDto> selectbyemail(@Param("email")String email);

	/**
	 * 前台带分页查询库存信息
	 * @param parms
	 * @return
	 */
	List<MicroInventoryQueryResult> selectForMemberViewByParam(ProductMicroInventoryDetailSearchDto parms);
	
}