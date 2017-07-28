package services.product.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import mapper.category.ProductVcategoryMapMapper;
import mapper.category.VirtualCategoryMapper;
import mapper.product.ProductBaseMapper;
import services.product.IProductVcategoryMapService;
import dto.category.CategorySearchParamDto;
import dto.product.PageResultDto;
import dto.product.ProductListInfoDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import entity.category.ProductVcategoryMapper;

public class ProductVcategoryMapService implements IProductVcategoryMapService {

	@Inject
	private ProductVcategoryMapMapper vmapMapper;
	@Inject
	private VirtualCategoryMapper vCategoryMapper;
	@Inject
	private ProductBaseMapper productBaseMapper;
	
	@Override
	public PageResultDto getProductList(CategorySearchParamDto paramDto) {
		List<ProductListInfoDto> list = vmapMapper.getProductList(paramDto);
		Integer count = vmapMapper.getProductListTotal(paramDto);
		return new PageResultDto(paramDto.getPageSize(),count, paramDto.getPageNo(), list);
	}

	@Override
	public void queryChild(List<Integer> catIds ,Set<Integer> all) {
		if(null != catIds && catIds.size() > 0){
			all.addAll(catIds);
			List<Integer> ids = vCategoryMapper.queryChild(catIds);
			if(null != ids && ids.size() >0){
				all.addAll(ids);
				queryChild(ids,all);
			}
		}
	}

	@Override
	public PageResultDto getSkuLists(List<Integer> list, ProductSearchParamDto searchDto) {
		if (list.size() <= 0) {
			return new PageResultDto(null, 0, null, new ArrayList<>());
		}
		List<ProductVcategoryMapper> mapList = vmapMapper.getProVcategory(list);
		if(mapList.size() <= 0) {
			return new PageResultDto(null, 0, null, new ArrayList<>());
		}
		searchDto.setProVList(mapList);
		searchDto.setModel(searchDto.getModel());
		searchDto.setCategoryId(null);
		List<ProductLite> productList = productBaseMapper.products(searchDto);
		return new PageResultDto(searchDto.getPageSize(), productBaseMapper.productCount(searchDto), searchDto.getCurrPage(), productList);
	}
	
}
