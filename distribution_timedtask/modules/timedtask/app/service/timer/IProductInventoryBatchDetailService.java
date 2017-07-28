package service.timer;

import entity.timer.ProductInventoryBatchDetail;

public interface IProductInventoryBatchDetailService {
	
	/**
	 * 根据唯一标识符获取云仓入仓记录
	 * @param identifier
	 * @return
	 */
	public ProductInventoryBatchDetail getBatchDetailByIdentifier(String identifier);
	
	public int insertSelective(ProductInventoryBatchDetail record);

}
