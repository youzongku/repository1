package services.product_inventory.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mybatis.guice.transactional.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.inventory.ProductCloudInventoryResult;
import dto.inventory.SearchSkuProductCloudInventoryDto;
import dto.product_inventory.CloudAndMicroInventoryDto;
import dto.product_inventory.CloudAndMicroInventoryResult;
import dto.product_inventory.CloudExpirationFormatResult;
import dto.product_inventory.ErpProExpiration;
import dto.product_inventory.ErpStockInResult;
import dto.product_inventory.ErpStockInResultDetail;
import dto.product_inventory.InventoryDetailDistributeDto;
import dto.product_inventory.ProductMicroInventoryDetailSearchDto;
import dto.product_inventory.ProductMicroInventoryOrderLockDto;
import dto.product_inventory.SearchCloudInventoryDetail;
import dto.product_inventory.SearchCloudInventoryResult;
import entity.product_inventory.OrderDetail;
import entity.product_inventory.ProductInventoryBatchDetail;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryOrderLock;
import entity.product_inventory.ProductInventoryTotal;
import entity.product_inventory.ProductMicroInventoryOrderLock;
import entity.product_inventory.ProductMicroInventoryTotal;
import mapper.product_inventory.OrderDetailMapper;
import mapper.product_inventory.ProductInventoryBatchDetailMapper;
import mapper.product_inventory.ProductInventoryDetailMapper;
import mapper.product_inventory.ProductInventoryOrderLockMapper;
import mapper.product_inventory.ProductInventoryTotalMapper;
import mapper.product_inventory.ProductMicroInventoryInRecordMapper;
import mapper.product_inventory.ProductMicroInventoryOrderLockMapper;
import mapper.product_inventory.ProductMicroInventoryTotalMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.product_inventory.IProductCloudInventoryService;
import utils.inventory.HttpUtil;

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
    private ProductInventoryOrderLockMapper productInventoryOrderLockMapper;

    @Inject
    private ProductInventoryDetailMapper productInventoryDetailMapper;

    @Inject
    ProductMicroInventoryTotalMapper productMicroInventoryTotalMapper;

    @Inject
    ProductMicroInventoryOrderLockMapper productMicroInventoryOrderLockMapper;
    
    @Inject
    ProductMicroInventoryInRecordMapper microInventoryInRecordMapper;
    
    @Inject
    OrderDetailMapper orderDetailMapper;

    public static String ERP_API_KEY = "";
	public static String ERP_HOST = "";
	public static String STOCK_INIT_API = "";
	
	static {
		if (ERP_API_KEY.equals("")) {
			Configuration config = Play.application().configuration().getConfig("erp");
			ERP_API_KEY = config.getString("apiKey");
		}
		if (ERP_HOST.equals("")) {
			Configuration config = Play.application().configuration().getConfig("erp");
			ERP_HOST = config.getString("host");
		}
		if (STOCK_INIT_API.equals("")) {
			Configuration config = Play.application().configuration().getConfig("erp");
			STOCK_INIT_API = config.getString("stockInitApi");
		}
	}
	@Override
	public int updateProductTotal(ProductInventoryTotal totalResult) {
		return productInventoryTotalMapper.updateByPrimaryKeySelective(totalResult);
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
            	int oldStock=productInventoryTotal.getStock();
                Date now = new Date();
                productInventoryTotal.setStock(productInventoryTotal.getStock() + productInventoryBatchDetail.getContainerStockChange());
                productInventoryTotal.setUpdateTime(now);
                Logger.info("时间{},批次入库sku{}库存变更详情：变更后库存{},变更前{}", now,productInventoryTotal.getSku(),productInventoryTotal.getStock(),oldStock);
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
                Logger.info("时间{},批次入库sku{}新建云仓总仓记录：{}", now,new_productInventoryTotal.getSku(),new_productInventoryTotal);
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

    @Override
    public SearchCloudInventoryResult list(SearchSkuProductCloudInventoryDto searchSkuProductCloudInventoryDto) {
        List<String> skuList = searchSkuProductCloudInventoryDto.getSkus();
        Integer warehouseId = searchSkuProductCloudInventoryDto.getWarehouseId();
        SearchCloudInventoryResult result = new SearchCloudInventoryResult();
        List<SearchCloudInventoryDetail> details = Lists.newArrayList();
        if (skuList != null && skuList.size() > 0) {
            for (int i = 0; i < skuList.size(); i++) {
                String sku = skuList.get(i);
                ProductInventoryTotal productInventoryTotal = productInventoryTotalMapper.selectBySkuAndWarehouseId(skuList.get(i), warehouseId);
                if (productInventoryTotal != null) {
                    List<ProductInventoryOrderLock> productInventoryOrderLocks = productInventoryOrderLockMapper.listBySkuAndWarehouseIdEffective(sku, warehouseId);
                    int deductQty = 0;
                    if (productInventoryOrderLocks != null && productInventoryOrderLocks.size() > 0)
                        for (ProductInventoryOrderLock productInventoryOrderLock : productInventoryOrderLocks) {
                            deductQty += productInventoryOrderLock.getStockLocked();
                        }
                    SearchCloudInventoryDetail searchCloudInventoryDetail = new SearchCloudInventoryDetail();
                    searchCloudInventoryDetail.setSku(sku);
                    searchCloudInventoryDetail.setNum(productInventoryTotal.getStock() - deductQty);
                    details.add(searchCloudInventoryDetail);
                } else {
                    SearchCloudInventoryDetail searchCloudInventoryDetail = new SearchCloudInventoryDetail();
                    searchCloudInventoryDetail.setSku(sku);
                    searchCloudInventoryDetail.setMsg("没有找到对应的云仓库存记录");
                    details.add(searchCloudInventoryDetail);
                }
            }
        }
        result.setType(1);
        result.setDetails(details);
        return result;
    }

    @Override
    public ProductCloudInventoryResult inventoryLocks(List<ProductInventoryOrderLock> params) {
        for (ProductInventoryOrderLock productInventoryOrderLock : params) {
            productInventoryOrderLock.setCreateTime(new Date());
            productInventoryOrderLockMapper.insertSelective(productInventoryOrderLock);
        }

        return new ProductCloudInventoryResult(true, null);
    }

    @Override
    public List<CloudAndMicroInventoryResult> searchProductCloudAndMicroInventory(CloudAndMicroInventoryDto cloudAndMicroInventoryDto) {
        List<CloudAndMicroInventoryResult> list = Lists.newArrayList();
        if (cloudAndMicroInventoryDto.getAccount() == null || "".equals(cloudAndMicroInventoryDto.getAccount())) {//只查云仓
            if (cloudAndMicroInventoryDto.getSkus() != null && cloudAndMicroInventoryDto.getSkus().size() > 0) {
                for (String sku : cloudAndMicroInventoryDto.getSkus()) {
                    List<ProductInventoryTotal> productInventoryTotalList = this.getProductInventoryTotals(sku);
                    if (productInventoryTotalList != null && productInventoryTotalList.size() > 0) {
                        for (ProductInventoryTotal productInventoryTotal : productInventoryTotalList) {
                            CloudAndMicroInventoryResult cloudAndMicroInventoryResult = new CloudAndMicroInventoryResult();
                            cloudAndMicroInventoryResult.setSku(sku);
                            cloudAndMicroInventoryResult.setWarehouseId(productInventoryTotal.getWarehouseId());
                            cloudAndMicroInventoryResult.setCloudInventory(productInventoryTotal.getStock() - this.getCloudLockStockSum(productInventoryTotal.getWarehouseId(), sku));
                            list.add(cloudAndMicroInventoryResult);
                        }
                    } else {
                            CloudAndMicroInventoryResult cloudAndMicroInventoryResult = new CloudAndMicroInventoryResult();
                            cloudAndMicroInventoryResult.setSku(sku);
                            list.add(cloudAndMicroInventoryResult);
                    }
                }
            }
        } else {//查云仓和微仓
            if (cloudAndMicroInventoryDto.getSkus() != null && cloudAndMicroInventoryDto.getSkus().size() > 0) {
                for (String sku : cloudAndMicroInventoryDto.getSkus()) {

                    List<ProductInventoryTotal> productInventoryTotalList = this.getProductInventoryTotals(sku);
                    if (productInventoryTotalList != null && productInventoryTotalList.size() > 0) {
                        for (ProductInventoryTotal productInventoryTotal : productInventoryTotalList) {
                            CloudAndMicroInventoryResult cloudAndMicroInventoryResult = new CloudAndMicroInventoryResult();
                            cloudAndMicroInventoryResult.setSku(sku);
                            cloudAndMicroInventoryResult.setWarehouseId(productInventoryTotal.getWarehouseId());
                            cloudAndMicroInventoryResult.setCloudInventory(productInventoryTotal.getStock() - this.getCloudLockStockSum(productInventoryTotal.getWarehouseId(), sku));
                            cloudAndMicroInventoryResult.setMicroInventory(this.gerMicroEffectiveStock(cloudAndMicroInventoryDto.getAccount(), productInventoryTotal.getWarehouseId(), sku));
                            list.add(cloudAndMicroInventoryResult);
                        }
                    } else {
                        ProductMicroInventoryDetailSearchDto parms = new ProductMicroInventoryDetailSearchDto();
                        parms.setAccount(cloudAndMicroInventoryDto.getAccount());
                        parms.setSkus(Lists.newArrayList(sku));
                        List<ProductMicroInventoryTotal> productMicroInventoryTotalList = productMicroInventoryTotalMapper.selectMicroTotalListByParam(parms);
                        if (productMicroInventoryTotalList != null && productMicroInventoryTotalList.size() > 0) {
                            for (ProductMicroInventoryTotal productMicroInventoryTotal : productMicroInventoryTotalList) {
                                CloudAndMicroInventoryResult cloudAndMicroInventoryResult = new CloudAndMicroInventoryResult();
                                cloudAndMicroInventoryResult.setSku(sku);
                                cloudAndMicroInventoryResult.setWarehouseId(productMicroInventoryTotal.getWarehouseId());
                                cloudAndMicroInventoryResult.setMicroInventory(this.gerMicroEffectiveStock(productMicroInventoryTotal.getAccount(), productMicroInventoryTotal.getWarehouseId(), sku));
                                list.add(cloudAndMicroInventoryResult);
                            }
                        } else {
                            CloudAndMicroInventoryResult cloudAndMicroInventoryResult = new CloudAndMicroInventoryResult();
                            cloudAndMicroInventoryResult.setSku(sku);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public InventoryDetailDistributeDto getCloudInventoryDetail(String s_main) {
    	InventoryDetailDistributeDto result=new InventoryDetailDistributeDto();
        JsonNode jsonNode = Json.parse(s_main);
        String sku = jsonNode.get("sku").asText();
        Integer warehouseId = jsonNode.get("warehouseId").asInt();

        ProductInventoryDetail productInventoryDetailParam = new ProductInventoryDetail();
        productInventoryDetailParam.setSku(sku);
        productInventoryDetailParam.setWarehouseId(warehouseId);
        List<ProductInventoryDetail> productInventoryDetailList = productInventoryDetailMapper.list(productInventoryDetailParam);
        productInventoryDetailList=productInventoryDetailList.stream()
				 .filter(e->e.getStock()>0)
				 .collect(Collectors.toList());
        result.setCloudInventoryDetailList(productInventoryDetailList);
		//查询被订单锁定的数量
		int cloudLockStockSum = this.getCloudLockStockSum(warehouseId,sku);
		result.setOrderLockNum(cloudLockStockSum+"");
		//查询微仓屯货数量
		int microTotalStockpile = microInventoryInRecordMapper.getTotalMicroInventoryStockpile(warehouseId,sku);
		result.setMicroInventoryStockpile(microTotalStockpile+"");
		//查询处于订单流转过程中的数量（已扣减微仓，但订单还没有推送至HK）
		String orderFlowNum=this.getOrderFlowNum(sku,warehouseId);
		result.setOrderFlowNum(orderFlowNum);
		//查询ka经销商占用的库存
		String kaLockNumStr=this.getKaLockNum(sku,warehouseId);
		result.setKaDistributorLockNum(kaLockNumStr);
		return result;
    }

    private String getKaLockNum(String sku, Integer warehouseId) {
		String kaLockNum="0";
		Map<String,String> param=Maps.newHashMap();
		String kaLockNumStr="";
		try {
			Configuration config = Play.application().configuration().getConfig("b2b");
			String baseurl = config.getString("b2bBaseUrl");
			String url=baseurl+"/product/lock/stock";
			
			
			param.put("sku", sku);
			param.put("wareId", warehouseId+"");
			kaLockNumStr = HttpUtil.get(param, url);
			JsonNode kaLockNumNode = Json.parse(kaLockNumStr);
			kaLockNum=kaLockNumNode.get("stock").asText();
		} catch (Exception e) {
			Logger.info("查询ka经销商占用库存数参数{}，返回结果{}", param.toString(),kaLockNumStr);
			Logger.info("查询ka经销商占用库存数发生异常{}", e);
			kaLockNum="查询ka经销商占用库存数发生异常";
		}
		return kaLockNum;
	}

	private String getOrderFlowNum(String sku, Integer warehouseId) {
    	String orderFlowNum="0";
    	try {
			Configuration config = Play.application().configuration().getConfig("b2c");
			String baseurl = config.getString("b2cBaseUrl");
			String url=baseurl+"/checkout/queryExistOrder";
			List<OrderDetail> orderDetailDeductMicroInventory = orderDetailMapper.selectOrderBySkuAndStatus(sku, warehouseId);
			if(orderDetailDeductMicroInventory.isEmpty()){
				return orderFlowNum;
			}
			List<String> orderNoList=Lists.newArrayList();
			int totailNum=0;
			for(OrderDetail tmepDetail: orderDetailDeductMicroInventory){
				totailNum+=tmepDetail.getQty();
				orderNoList.add(tmepDetail.getOrderNo());
			}
			String orderNoExistHkStr = HttpUtil.post(Json.toJson(orderNoList).toString(), url);
			JsonNode orderNoExistHkNode = Json.parse(orderNoExistHkStr);
			if(orderNoExistHkNode==null || orderNoExistHkNode.size()<=0){
				orderFlowNum=totailNum+"";
				return orderFlowNum;
			}
			for(JsonNode tempNode: orderNoExistHkNode){
				String orderNoHKExist = tempNode.asText();
				orderDetailDeductMicroInventory= orderDetailDeductMicroInventory.stream()
				.filter(e->!e.getOrderNo().equals(orderNoHKExist))
				.collect(Collectors.toList());
			}
			int orderExistFilterTotalNum=0;
			for(OrderDetail tempDetail: orderDetailDeductMicroInventory){
				orderExistFilterTotalNum+=tempDetail.getQty();
			}
			orderFlowNum=orderExistFilterTotalNum+"";
		} catch (Exception e) {
			Logger.info("获取商品处于流转中的订单发送异常{}", e);
			orderFlowNum="获取商品处于流转中的订单发送异常";
		}
		return orderFlowNum;
	}

	/**
     * 查询某个sku云仓被锁定的数量
     *
     * @param warehouseId
     * @param sku
     * @return
     */
    private int getCloudLockStockSum(int warehouseId, String sku) {
        ProductInventoryOrderLock inventoryOrderLockParam = new ProductInventoryOrderLock();
        inventoryOrderLockParam.setSku(sku);
        inventoryOrderLockParam.setWarehouseId(warehouseId);
        inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
        List<ProductInventoryOrderLock> inventoryOrderLockList = productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
        int inventoryLockNum = 0;
        if (inventoryOrderLockList != null && inventoryOrderLockList.size() > 0) {
            for (ProductInventoryOrderLock inventoryOrderLock : inventoryOrderLockList) {
                inventoryLockNum += inventoryOrderLock.getStockLocked();
            }
        }

        return inventoryLockNum;

    }

    /**
     * 查询云仓
     *
     * @param sku
     * @return
     */
    private List<ProductInventoryTotal> getProductInventoryTotals(String sku) {
        ProductInventoryTotal inventoryTotalParam = new ProductInventoryTotal();
        inventoryTotalParam.setSku(sku);
        List<ProductInventoryTotal> totalList= productInventoryTotalMapper.query(inventoryTotalParam);
        if(totalList.isEmpty()){
        	return totalList;
        }
        for(ProductInventoryTotal tempTotal: totalList){
        	ProductInventoryDetail detailParam= new ProductInventoryDetail();
        	detailParam.setSku(tempTotal.getSku());
        	detailParam.setWarehouseId(tempTotal.getWarehouseId());
        	int totalNum=productInventoryDetailMapper.getTotalNumBySkuAndWarehouseId(detailParam);
        	tempTotal.setStock(totalNum);
        }
        return totalList;
    }


    /**
     * 获取微仓有效库存
     *
     * @param account
     * @param warehouseId
     * @param sku
     * @return
     */
    private int gerMicroEffectiveStock(String account, int warehouseId, String sku) {
        ProductMicroInventoryTotal productMicroInventoryTotal = this.getProductMicroInventoryTotal(account, warehouseId, sku);

        if (productMicroInventoryTotal == null) {
            return 0;
        }

        //微仓锁定总数
        ProductMicroInventoryOrderLockDto productMicroInventoryOrderLockDto = new ProductMicroInventoryOrderLockDto();
        productMicroInventoryOrderLockDto.setAccount(account);
        productMicroInventoryOrderLockDto.setSku(sku);
        productMicroInventoryOrderLockDto.setWarehouseId(warehouseId);
        productMicroInventoryOrderLockDto.setIsEffective(-1);//锁定的记录
        List<ProductMicroInventoryOrderLock> productMicroInventoryOrderLockList = productMicroInventoryOrderLockMapper.query(productMicroInventoryOrderLockDto);

        int lockSum = 0;
        if (productMicroInventoryOrderLockList != null && productMicroInventoryOrderLockList.size() > 0) {
            for (ProductMicroInventoryOrderLock productMicroInventoryOrderLock : productMicroInventoryOrderLockList) {
                lockSum += productMicroInventoryOrderLock.getStockLocked();
            }
        }
        //微仓有效库存
        return productMicroInventoryTotal.getStock() - lockSum;
    }

    /**
     * 检查微仓总表
     *
     * @param account
     * @param warehouseId
     * @param sku
     * @return
     */
    private ProductMicroInventoryTotal getProductMicroInventoryTotal(String account, int warehouseId, String sku) {
        ProductMicroInventoryTotal productMicroInventoryTotalParam = new ProductMicroInventoryTotal();
        productMicroInventoryTotalParam.setSku(sku);
        productMicroInventoryTotalParam.setWarehouseId(warehouseId);
        productMicroInventoryTotalParam.setAccount(account);
        return productMicroInventoryTotalMapper.selectByParam(productMicroInventoryTotalParam);
    }

	@Override
	public int insertSelective(ProductInventoryTotal record) {
		return productInventoryTotalMapper.insertSelective(record);
	}

	@Override
	public int deleteBySkuAndWarehouseId(String sku, Integer warehouseId) {
		return productInventoryTotalMapper.deleteBySkuAndWarehouseId(sku, warehouseId);
	}

	@Override
	public Map<String, Object> erpStock(String s_main) {
		Map<String,Object> res = Maps.newHashMap();
		boolean suc = false;
		String msg = null;
		try {
			JsonNode node = Json.parse(s_main);
			Map<String,Object> postMap = Maps.newHashMap();
			postMap.put("skus", node.get("skus"));
			postMap.put("stock_id", node.get("warehouseId").asInt());
			postMap.put("timestamp", 0);
			String result =  HttpUtil.post(
					Json.toJson(postMap).toString(),
					ERP_HOST + STOCK_INIT_API + "?api_key="
							+ ERP_API_KEY);
			JsonNode resJson = Json.parse(result);
			Logger.info("erp查询库存信息:[{}]",resJson);
			if("200".equals(resJson.get("code").asText())){
				suc = true;
				ObjectMapper map = new ObjectMapper();
				List<ErpProExpiration> list = map.readValue(resJson.get("result").toString(),new TypeReference<List<ErpProExpiration>>(){});
				res.put("data", list);
			}else{
				msg = "查询erp库存失败";
			}
		} catch (Exception e) {
			msg = "查询erp库存异常";
			Logger.info(msg,e);
		}
		res.put("suc", suc);
		res.put("msg", msg);
		return res;
	}

	@Override
	public List<ProductInventoryDetail> getExternalWarehouseInventoryDetail(Integer warehouseId) {
		List<ProductInventoryDetail> list = productInventoryDetailMapper.list(new ProductInventoryDetail(warehouseId));
		return list;
	}

	@Override
	public int updateExternalWearhouseProductInventory(ProductInventoryDetail productInventoryDetailParam) {
		ProductInventoryDetail oldInventoryData = productInventoryDetailMapper.selectByPrimaryKey(productInventoryDetailParam.getId());
		int changeNum=productInventoryDetailParam.getStock()-oldInventoryData.getStock();
		productInventoryDetailParam.setUpdateTime(new Date());
		productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetailParam);
		 
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
	public List<CloudExpirationFormatResult> searchProductCloudInventory(
			List<ProductInventoryDetail> reqParams) {
		List<ProductInventoryDetail> resultList = Lists.newArrayList();
		for(ProductInventoryDetail requestParam: reqParams){
			 List<ProductInventoryDetail> productInventoryDetailList = productInventoryDetailMapper.list(requestParam);
			 if (productInventoryDetailList != null && productInventoryDetailList.size() > 0) {
				 
				 for (ProductInventoryDetail productInventoryDetail : productInventoryDetailList) {//减指定过期日期的锁定数量
					 productInventoryDetail.setStock(productInventoryDetail.getStock()- this.getCloudLockStockSumWithExpirationDate(productInventoryDetail));
				 }
				 
				int lockSum = this.getCloudLockStockSumWithExpirationIsNull(requestParam.getWarehouseId(), requestParam.getSku());//无指定过期日期被锁定的数量
	            for (ProductInventoryDetail productInventoryDetail : productInventoryDetailList) {
	                if (lockSum > 0) {
	                        if (productInventoryDetail.getStock() <= lockSum) {
	                            lockSum = lockSum - productInventoryDetail.getStock();
	                            productInventoryDetail.setStock(0);
	                            resultList.add(productInventoryDetail);
	                        } else {
	                            productInventoryDetail.setStock(productInventoryDetail.getStock() - lockSum);
	                            resultList.add(productInventoryDetail);
	                            lockSum = 0;
	                        }
	                } else {
	                    resultList.add(productInventoryDetail);
	                }
	            }
	        }
		}
		List<ProductInventoryDetail> list = resultList.stream().filter(d -> d.getStock() > 0).collect(Collectors.toList());
		List<CloudExpirationFormatResult> resultlist=Lists.newArrayList();
		for(ProductInventoryDetail detail:list){
			resultlist.add(new CloudExpirationFormatResult(detail));
		}
		return resultlist;
	}

	/**
	 * 指定过期日期sku，warehouseId锁定的总量
	 * @param productInventoryDetail
	 * @return
	 */
	private Integer getCloudLockStockSumWithExpirationDate(ProductInventoryDetail productInventoryDetail) {
		ProductInventoryOrderLock inventoryOrderLockParam = new ProductInventoryOrderLock();
        inventoryOrderLockParam.setSku(productInventoryDetail.getSku());
        inventoryOrderLockParam.setWarehouseId(productInventoryDetail.getWarehouseId());
        inventoryOrderLockParam.setExpirationDate(productInventoryDetail.getExpirationDate());
        inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
        List<ProductInventoryOrderLock> inventoryOrderLockList = productInventoryOrderLockMapper.selectInventoryEffectiveLockListByParams(inventoryOrderLockParam);
        int inventoryLockNum = 0;
        if (inventoryOrderLockList != null && inventoryOrderLockList.size() > 0) {
            for (ProductInventoryOrderLock inventoryOrderLock : inventoryOrderLockList) {
                inventoryLockNum += inventoryOrderLock.getStockLocked();
            }
        }

        return inventoryLockNum;
	}

	private int getCloudLockStockSumWithExpirationIsNull(Integer warehouseId, String sku) {
		ProductInventoryOrderLock inventoryOrderLockParam = new ProductInventoryOrderLock();
        inventoryOrderLockParam.setSku(sku);
        inventoryOrderLockParam.setWarehouseId(warehouseId);
        inventoryOrderLockParam.setIsEffective((short) 1);//查询时为1  或者 -1
        List<ProductInventoryOrderLock> inventoryOrderLockList = productInventoryOrderLockMapper.selectEffectiveListByParamsAndExpirationDateIsNull(inventoryOrderLockParam);
        int inventoryLockNum = 0;
        if (inventoryOrderLockList != null && inventoryOrderLockList.size() > 0) {
            for (ProductInventoryOrderLock inventoryOrderLock : inventoryOrderLockList) {
                inventoryLockNum += inventoryOrderLock.getStockLocked();
            }
        }

        return inventoryLockNum;
	}

	@Override
	public ProductCloudInventoryResult setCloudStockByWarehouseId(String param) {
		JsonNode reqParam = Json.parse(param);
		if(!reqParam.has("type")){
			return new ProductCloudInventoryResult(false,"参数不正确！");
		}
		if(reqParam.get("type").asInt()==0){//设置所有2012仓所有商品库存增加 指定数量
			int stockNum = reqParam.get("stockNum").asInt();
			//查询出所有杭州仓2012云仓明细
			List<ProductInventoryDetail> resultInventoryDetailList = productInventoryDetailMapper.selectInventoryDetailBySkuAndWarehouseId(null,2012);
			if(resultInventoryDetailList==null||resultInventoryDetailList.size()<=0){
				return new ProductCloudInventoryResult(false, "云仓库存中无2012仓商品，请先初始化！");
			}
			
			for (ProductInventoryDetail inventoryDetail : resultInventoryDetailList) {
				Integer oldStockNum = inventoryDetail.getStock();
				//修改云仓明细
				inventoryDetail.setStock(oldStockNum+stockNum);
				inventoryDetail.setUpdateTime(new Date());
				productInventoryDetailMapper.updateByPrimaryKeySelective(inventoryDetail);
				ProductInventoryTotal totalResult = productInventoryTotalMapper.selectBySkuAndWarehouseId(inventoryDetail.getSku(), inventoryDetail.getWarehouseId());
				totalResult.setStock(totalResult.getStock()+stockNum);
				totalResult.setUpdateTime(new Date());
				productInventoryTotalMapper.updateByPrimaryKeySelective(totalResult);
				Logger.info("人为修改杭州仓商品{}库存数据信息：beforeChangeStockNum={},afterChangeStockNum={}",inventoryDetail.toString(), oldStockNum,oldStockNum+stockNum);
				
			}
			return new ProductCloudInventoryResult(true, "修改成功!");
		}
		if(reqParam.get("type").asInt()==1){//修改指定商品库存数据
			JsonNode jsonNode = reqParam.get("skusAndNum");
			if(jsonNode==null || jsonNode.size()<=0){
				return new ProductCloudInventoryResult(false, "参数不正确");
			}
			for (JsonNode skuAndNum : jsonNode) {
				String sku = skuAndNum.get("sku").asText();
				int stockNum = skuAndNum.get("num").asInt();
				//查询云仓总仓
				ProductInventoryTotal inventroTotal = productInventoryTotalMapper.selectBySkuAndWarehouseId(sku, 2012);
				if(inventroTotal==null){
					return new ProductCloudInventoryResult(false, "云仓无该商品信息，请先初始化！");
				}
				//修改云仓总仓库存
				Integer oldStockNum = inventroTotal.getStock();
				inventroTotal.setStock(oldStockNum+stockNum);
				inventroTotal.setUpdateTime(new Date());
				productInventoryTotalMapper.updateByPrimaryKeySelective(inventroTotal);
				//修改云仓明细
				List<ProductInventoryDetail> inventoryDetailList = productInventoryDetailMapper.selectInventoryDetailBySkuAndWarehouseId(sku, 2012);
				ProductInventoryDetail productInventoryDetail = inventoryDetailList.get(0);
				Integer DetailOldStockNum = productInventoryDetail.getStock();
				productInventoryDetail.setStock(DetailOldStockNum+stockNum);
				productInventoryDetail.setUpdateTime(new Date());
				productInventoryDetailMapper.updateByPrimaryKeySelective(productInventoryDetail);
				Logger.info("人为修改杭州仓商品{}库存数据信息：beforeChangeStockNum={},afterChangeStockNum={}",productInventoryDetail.toString(), DetailOldStockNum,DetailOldStockNum+stockNum);
			}
			return new ProductCloudInventoryResult(true, "修改成功");
		}
		return new ProductCloudInventoryResult(false, "参数有误！");
	}

	@Override
	public ProductInventoryTotal getProductBySkuAndWarehouseId(ProductInventoryTotal pit) {
		ProductInventoryTotal param=new ProductInventoryTotal();
		param.setSku(pit.getSku());
		param.setWarehouseId(pit.getWarehouseId());
		ProductInventoryTotal selectByParam = productInventoryTotalMapper.selectByParam(param);
		return selectByParam;
	}

	@Override
	public String getInventoryDispersion(String sku, Integer warehouseId) {
		Map<String,Object> result=Maps.newHashMap();
		int cloudLockStockSum = this.getCloudLockStockSum(warehouseId,sku);
		//查询被订单锁定的数量
		result.put("cloudLockStock", cloudLockStockSum);
		//查询微仓屯货数量
		int microTotalStockpile = microInventoryInRecordMapper.getTotalMicroInventoryStockpile(warehouseId,sku);
		result.put("microTotalStockpile", microTotalStockpile);
		//查询处于订单流转过程中的数量（已扣减微仓，但订单还没有推送至HK）
		String orderFlowNum=this.getOrderFlowNum(sku,warehouseId);
		result.put("orderFlowNum", orderFlowNum);
		//查询ka经销商占用的库存
		String kaLockNumStr=this.getKaLockNum(sku,warehouseId);
		result.put("kaLockNumStr", kaLockNumStr);
		String result_s = Json.toJson(result).toString();
		return result_s;
	}

	
}
