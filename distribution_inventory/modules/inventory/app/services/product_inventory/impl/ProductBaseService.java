package services.product_inventory.impl;

import java.util.List;

import com.google.inject.Inject;

import mapper.product_inventory.ProductBaseMapper;
import entity.product_inventory.ProductBase;
import services.product_inventory.IProductBaseService;

public class ProductBaseService implements IProductBaseService {

	@Inject
	private ProductBaseMapper productBaseMapper;
	
	@Override
	public List<ProductBase> getProductsByStatus(Integer status) {
		return productBaseMapper.selectByStatus(status);
	}

	@Override
	public String getProductTitle(String sku) {
		return productBaseMapper.getProductTitle(sku);
	}

	@Override
	public List<ProductBase> getProductsInSalesBySku(String sku) {
		return productBaseMapper.getProductsInSalesBySku(sku);
	}

	@Override
	public List<Integer> getProductCategoryBySku(String sku) {
		
		return productBaseMapper.getCategoryBySku(sku);
	}

}
