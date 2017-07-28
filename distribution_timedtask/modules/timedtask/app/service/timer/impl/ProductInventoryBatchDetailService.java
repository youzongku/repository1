package service.timer.impl;

import javax.inject.Inject;

import entity.timer.ProductInventoryBatchDetail;
import mapper.timer.ProductInventoryBatchDetailMapper;
import service.timer.IProductInventoryBatchDetailService;

public class ProductInventoryBatchDetailService implements
		IProductInventoryBatchDetailService {

	@Inject
	private ProductInventoryBatchDetailMapper productInventoryBatchDetailMapper;

	@Override
	public ProductInventoryBatchDetail getBatchDetailByIdentifier(
			String identifier) {
		return productInventoryBatchDetailMapper
				.getBatchDetailByIdentifier(identifier);
	}

	@Override
	public int insertSelective(ProductInventoryBatchDetail record) {
		return productInventoryBatchDetailMapper.insertSelective(record);
	}

}
