package mapper.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.inventory.DisInventoryDto;
import dto.inventory.DisProductDto;
import entity.inventory.DisProduct;

public interface DisProductMapper extends BaseMapper<DisProduct> {

	List<DisInventoryDto> selectDisproductByWareIdAndEmail(Map<String, Object> param);
	
	Integer selectDisproductByWareIdAndEmailCount(Map<String, Object> param);
	
	DisProduct selectDisproductBySkuAndEmail(String sku,String email);
	
	/**
	 * 获取产品及其所在微仓的信息
	 * @param sku 目标SKU
	 * @param email 用户邮箱
	 * @param wid 微仓id，可选，不指定则会查询所有所在微仓的
	 * @return
	 */
	List<DisProductDto> selectDisproductAndStockBySkuAndEmail(@Param(value = "sku") String sku,
			@Param(value = "email") String email, @Param(value = "wid") Integer wid);

	List<DisProductDto> selectAllStock(@Param("email")String email);
	
	
}