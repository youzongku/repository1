package service.timer.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mapper.timer.ProductInventoryBatchDetailMapper;
import mapper.timer.ProductInventoryDetailMapper;
import mapper.timer.ProductInventoryTotalMapper;

import org.mybatis.guice.transactional.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.timer.ErpStockInResult;
import entity.timer.ErpStockInResultDetail;
import entity.timer.ProductInventoryBatchDetail;
import entity.timer.ProductInventoryDetail;
import entity.timer.ProductInventoryTotal;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import service.timer.IProductCloudInventoryService;

/**
 * @author longhuashen
 * @since 2016/12/5
 */
public class ProductCloudInventoryService implements IProductCloudInventoryService {

    @Inject
    private ProductInventoryBatchDetailMapper productInventoryBatchDetailMapper;
    
    @Inject
    private ProductInventoryTotalMapper productInventoryTotalMapper;
    
    @Inject
    private ProductInventoryDetailMapper productInventoryDetailMapper;

    public static String ERP_API_KEY = "";
	public static String ERP_HOST = "";
	public static String STOCK_INIT_API = "";
	
	static {
		if (ERP_API_KEY == "") {
			Configuration config = Play.application().configuration().getConfig("erp");
			ERP_API_KEY = config.getString("apiKey");
		}
		if (ERP_HOST == "") {
			Configuration config = Play.application().configuration().getConfig("erp");
			ERP_HOST = config.getString("host");
		}
		if (STOCK_INIT_API == "") {
			Configuration config = Play.application().configuration().getConfig("erp");
			STOCK_INIT_API = config.getString("stockInitApi");
		}
	}
    
	@Override
	public int updateExternalWearhouseProductInventory(ProductInventoryDetail productInventoryDetailParam) {
		ProductInventoryDetail oldInventoryData = productInventoryDetailMapper.selectByPrimaryKey(productInventoryDetailParam.getId());
		int changeNum=productInventoryDetailParam.getStock()-oldInventoryData.getStock();
		productInventoryDetailParam.setUpdateTime(new Date());
		int  i= productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetailParam);
		 
		ProductInventoryTotal totalParam=new ProductInventoryTotal();
		totalParam.setSku(productInventoryDetailParam.getSku());
		totalParam.setWarehouseId(productInventoryDetailParam.getWarehouseId());
		ProductInventoryTotal totalResult = productInventoryTotalMapper.selectByParam(totalParam);
		totalResult.setStock(totalResult.getStock()+changeNum);
		totalResult.setUpdateTime(new Date());
		int j = productInventoryTotalMapper.updateByPrimaryKeySelective(totalResult);
		return j;
	}
	
	@Override
	public List<ProductInventoryDetail> getExternalWarehouseInventoryDetail(Integer warehouseId) {
		List<ProductInventoryDetail> list = productInventoryDetailMapper.list(new ProductInventoryDetail(warehouseId));
		return list;
	}
	
    @Override
    @Transactional
    public ErpStockInResult erpStockInDetail(List<ProductInventoryBatchDetail> productInventoryBatchDetailList) {
        ErpStockInResult result = new ErpStockInResult();
        List<ErpStockInResultDetail> erpStockInResultDetailList = Lists.newArrayList();
        for (ProductInventoryBatchDetail productInventoryBatchDetail : productInventoryBatchDetailList) {
            productInventoryBatchDetailMapper.insertSelective(productInventoryBatchDetail);
            ProductInventoryTotal productInventoryTotal = productInventoryTotalMapper.selectBySkuAndWarehouseId(productInventoryBatchDetail.getSku(), productInventoryBatchDetail.getWarehouseId());
            if (productInventoryTotal != null) {
                Date now = new Date();
                productInventoryTotal.setStock(productInventoryTotal.getStock() + productInventoryBatchDetail.getContainerStockChange());
                productInventoryTotal.setUpdateTime(now);
                int productInventoryTotalInsertNum = productInventoryTotalMapper.updateByPrimaryKeySelective(productInventoryTotal);

                //添加云仓明细
                int productInventoryDetailNum = this.generateProductInventoryDetail(productInventoryBatchDetail);
                if (productInventoryTotalInsertNum > 0 && productInventoryDetailNum > 0) {
                    ErpStockInResultDetail erpStockInResultDetail = new ErpStockInResultDetail();
                    erpStockInResultDetail.setSku(productInventoryBatchDetail.getSku());
                    erpStockInResultDetail.setResult(true);
                    erpStockInResultDetailList.add(erpStockInResultDetail);
                } else {
                    ErpStockInResultDetail erpStockInResultDetail = new ErpStockInResultDetail();
                    erpStockInResultDetail.setSku(productInventoryBatchDetail.getSku());
                    erpStockInResultDetail.setResult(false);
                    erpStockInResultDetailList.add(erpStockInResultDetail);
                }
            } else {
                ProductInventoryTotal new_productInventoryTotal = new ProductInventoryTotal();
                new_productInventoryTotal.setSku(productInventoryBatchDetail.getSku());
                new_productInventoryTotal.setWarehouseId(productInventoryBatchDetail.getWarehouseId());
                new_productInventoryTotal.setWarehouseName(productInventoryBatchDetail.getWarehouseName());
                new_productInventoryTotal.setStock(productInventoryBatchDetail.getContainerStockChange());
                new_productInventoryTotal.setProductName(productInventoryBatchDetail.getProductName());
                Date now = new Date();
                new_productInventoryTotal.setCreateTime(now);
                new_productInventoryTotal.setUpdateTime(now);
                int productInventoryTotalUpdateNum = productInventoryTotalMapper.insertSelective(new_productInventoryTotal);

                //添加云仓明细
                int productInventoryDetailNum = this.generateProductInventoryDetail(productInventoryBatchDetail);
                if (productInventoryTotalUpdateNum > 0 && productInventoryDetailNum > 0) {
                    ErpStockInResultDetail erpStockInResultDetail = new ErpStockInResultDetail();
                    erpStockInResultDetail.setSku(productInventoryBatchDetail.getSku());
                    erpStockInResultDetail.setResult(true);
                    erpStockInResultDetailList.add(erpStockInResultDetail);
                } else {
                    ErpStockInResultDetail erpStockInResultDetail = new ErpStockInResultDetail();
                    erpStockInResultDetail.setSku(productInventoryBatchDetail.getSku());
                    erpStockInResultDetail.setResult(false);
                    erpStockInResultDetailList.add(erpStockInResultDetail);
                }
            }
        }
        result.setResult(true);
        result.setDetails(erpStockInResultDetailList);
        return result;
    }

    private int generateProductInventoryDetail(ProductInventoryBatchDetail productInventoryBatchDetail) {
        ProductInventoryDetail productInventoryDetailParam = new ProductInventoryDetail();
        productInventoryDetailParam.setSku(productInventoryBatchDetail.getSku());
        productInventoryDetailParam.setWarehouseId(productInventoryBatchDetail.getWarehouseId());
        productInventoryDetailParam.setExpirationDate(productInventoryBatchDetail.getExpirationDate());
        
        Logger.info("============productInventoryBatchDetail：{}============", productInventoryBatchDetail.toString());
        
        ProductInventoryDetail productInventoryDetail = productInventoryDetailMapper.selectByParam(productInventoryDetailParam);

        if (productInventoryDetail == null) {
            ProductInventoryDetail newProductInventoryDetail = new ProductInventoryDetail();
            newProductInventoryDetail.setSku(productInventoryBatchDetail.getSku());
            newProductInventoryDetail.setStock(productInventoryBatchDetail.getContainerStockChange());
            newProductInventoryDetail.setWarehouseId(productInventoryBatchDetail.getWarehouseId());
            newProductInventoryDetail.setWarehouseName(productInventoryBatchDetail.getWarehouseName());
            Date now = new Date();
            newProductInventoryDetail.setCreateTime(now);
            newProductInventoryDetail.setUpdateTime(now);
            newProductInventoryDetail.setExpirationDate(productInventoryBatchDetail.getExpirationDate());
            return productInventoryDetailMapper.insertSelective(newProductInventoryDetail);
        } else {
            productInventoryDetail.setStock(productInventoryDetail.getStock() + productInventoryBatchDetail.getContainerStockChange());
            productInventoryDetail.setUpdateTime(new Date());
            return productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
        }

    }

   

    


	

}
