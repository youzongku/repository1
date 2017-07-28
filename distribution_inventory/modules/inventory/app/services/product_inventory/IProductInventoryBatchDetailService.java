package services.product_inventory;

import java.util.Date;
import java.util.List;

import dto.product_inventory.ErpStockChangeQueryDto;
import entity.product_inventory.ProductInventoryBatchDetail;

public interface IProductInventoryBatchDetailService {
	
	/**
	 * 根据唯一标识符获取云仓入仓记录
	 * @param identifier
	 * @return
	 */
	public ProductInventoryBatchDetail getBatchDetailByIdentifier(String identifier);
	
	public int insertSelective(ProductInventoryBatchDetail record);

	public List<ProductInventoryBatchDetail> getSyncRecordByParam(ErpStockChangeQueryDto queryParam);

	public int getSyncRecordTotalCountByParam(ErpStockChangeQueryDto queryParam); 

}
