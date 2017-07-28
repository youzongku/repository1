package mapper.purchase;

import org.apache.ibatis.annotations.Param;

import entity.purchase.PurchaseStockout;

public interface PurchaseStockoutMapper extends BaseMapper<PurchaseStockout> {
	int insert(PurchaseStockout pso);
	
	PurchaseStockout selectStockout(@Param("purchaseOrderNo") String purchaseOrderNo,
			@Param("status") Integer status);
	
	int updateStatusById(@Param("id") Integer id, @Param("status") Integer status);
}
