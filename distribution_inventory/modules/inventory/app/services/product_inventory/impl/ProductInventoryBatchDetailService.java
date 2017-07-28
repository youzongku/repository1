package services.product_inventory.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dto.product_inventory.ErpStockChangeQueryDto;
import services.product_inventory.IProductInventoryBatchDetailService;
import mapper.product_inventory.ProductInventoryBatchDetailMapper;
import entity.product_inventory.ProductInventoryBatchDetail;

public class ProductInventoryBatchDetailService implements
		IProductInventoryBatchDetailService {

	@Inject
	private ProductInventoryBatchDetailMapper productInventoryBatchDetailMapper;
	
	@Override
	public ProductInventoryBatchDetail getBatchDetailByIdentifier(
			String identifier) {
		return productInventoryBatchDetailMapper.getBatchDetailByIdentifier(identifier);
	}

	@Override
	public int insertSelective(ProductInventoryBatchDetail record) {
		return productInventoryBatchDetailMapper.insertSelective(record);
	}

	@Override
	public List<ProductInventoryBatchDetail> getSyncRecordByParam(ErpStockChangeQueryDto queryParam) {
		return productInventoryBatchDetailMapper.getSyncRecordByParam(queryParam);
	}

	@Override
	public int getSyncRecordTotalCountByParam(ErpStockChangeQueryDto queryParam) {
		// TODO Auto-generated method stub
		return productInventoryBatchDetailMapper.getSyncRecordTotalCountByParam(queryParam);
	}}
