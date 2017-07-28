package services.product_inventory.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.google.inject.Inject;

import component.elasticsearch.CloudInventoryDoc;
import component.elasticsearch.MicroInventoryDoc;
import dto.product_inventory.ProductInventoryEnquiryRequest;
import dto.product_inventory.ProductInventoryEnquiryResult;
import entity.product_inventory.ProductInventoryBatchDetail;
import mapper.product_inventory.ProductInventoryTotalMapper;
import mapper.product_inventory.ProductMicroInventoryTotalMapper;
//import mapper.product_inventory.ProductInventoryContainerNumberMapper;
import services.product_inventory.IProductInventoryService;

public class ProductInventoryService implements IProductInventoryService {
	
	@Inject
	ProductInventoryTotalMapper cloudInventoryMapper;
	
	@Inject
	ProductMicroInventoryTotalMapper microInventoryMapper;

//	@Inject
//	ProductInventoryContainerNumberMapper productInventoryContainerNumberMapper;
	
	@Override
	public Map<String, String> receiveStockInAndUpdateCloud(List<ProductInventoryBatchDetail> ps) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CloudInventoryDoc> cloudInventory() {
		List<CloudInventoryDoc> docs = new ArrayList<>();
		ProductInventoryEnquiryRequest param = new ProductInventoryEnquiryRequest();
		List<ProductInventoryEnquiryResult> resList = cloudInventoryMapper.cloudInventory(param);
		for (ProductInventoryEnquiryResult inv : resList) {
			CloudInventoryDoc doc = new CloudInventoryDoc();
			BeanUtils.copyProperties(inv, doc);
			docs.add(doc);
		}
		return docs;
	}

	@Override
	public List<MicroInventoryDoc> microInventory(String disAccount) {
		List<MicroInventoryDoc> docs = new ArrayList<>();
		ProductInventoryEnquiryRequest param = new ProductInventoryEnquiryRequest();
		param.setDisAccount(disAccount);
		List<ProductInventoryEnquiryResult> resList = microInventoryMapper.microInventory(param);
		for (ProductInventoryEnquiryResult inv : resList) {
			MicroInventoryDoc doc = new MicroInventoryDoc();
			BeanUtils.copyProperties(inv, doc);
			docs.add(doc);
		}
		return docs;
	}

//	@Override
//	public ProductInventoryContainerNumber selectProConNumByParams(ProductInventoryContainerNumber productICN) {
//		// TODO Auto-generated method stub
//		return productInventoryContainerNumberMapper.selectProConNumByParams(productICN);
//	}

}
