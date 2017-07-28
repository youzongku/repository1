package services.product_inventory.impl;

import javax.inject.Inject;

import mapper.product_inventory.ProductInventoryDetailMapper;
import entity.product_inventory.ProductInventoryDetail;
import services.product_inventory.IProductInventoryDetailService;

public class ProductInventoryDetailService implements
		IProductInventoryDetailService {

	@Inject
    private ProductInventoryDetailMapper productInventoryDetailMapper;
	
	@Override
	public int saveOrUpdate(ProductInventoryDetail pid) {
		
		ProductInventoryDetail temp = productInventoryDetailMapper.selectByPrimaryKey(pid.getId());
		
		int effectRow = 0;
		
		if(temp == null){
			effectRow = productInventoryDetailMapper.insertSelective(pid);
		}else{
			effectRow = productInventoryDetailMapper.updateByPrimaryKeySelective(pid);
		}
		
		return effectRow;
	}

	@Override
	public int deleteBySkuAndWarehouseId(String sku,Integer warehouseId) {
		return productInventoryDetailMapper.deleteBySkuAndWarehouseId(sku,warehouseId);
	}

	@Override
	public ProductInventoryDetail selectDetail(ProductInventoryDetail record) {
		return productInventoryDetailMapper.selectByParam(record);
	}

}
