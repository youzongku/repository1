package service.discart;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import dto.discart.OrderDetail;
import dto.discart.ProSearch;

public interface IHttpService {
	
	/**
	 * 查询库存
	 * @param skus
	 * @return
	 * @throws Exception
	 */
	public String getProStock(List<String> skus) throws Exception;

	/**
	 * 获取商品信息（查询不到非卖品）
	 * 
	 * @param skus
	 * @param warehouseId
	 * @param distributionMode 
	 * @return
	 */
	public JsonNode fgetProducts(ProSearch search)throws JsonProcessingException, IOException;
	
	/**
	 * 获取商品信息（可以查询到非卖品）
	 * 
	 * @param skus
	 * @param warehouseId
	 * @param distributionMode 
	 * @return
	 */
	public JsonNode getProducts(ProSearch search)throws JsonProcessingException, IOException;

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月23日 下午2:39:24
	 */
	public JsonNode postPurchase(Integer distributorType,String email,List<OrderDetail> orderDetail,
			Double totalPrice,Double couponsAmount,String couponsCode,String remarks,Boolean isChoose,
			String logisticsMode,Double bbcPostage,String logisticsTypeCode,Boolean immediateDelivery)  throws JsonProcessingException, IOException;

	/**
	 * 保存采购单发货信息
	 * @author zbc
	 * @since 2017年5月25日 下午6:13:54
	 * @param pno
	 * @param jsonStr
	 * @return
	 */
	public JsonNode savePurchaseSaleOut(String pno, String jsonStr);

	/**
	 * 获取运费
	 * @author zbc
	 * @since 2017年5月26日 上午11:35:51
	 * @param warehouseId
	 * @param shippingCode
	 * @param skuList
	 * @param provinceId
	 * @param cityId
	 * @return
	 */
	public JsonNode getFreight(Integer warehouseId, String shippingCode, JsonNode skuList, Integer provinceId, Integer cityId)
			throws JsonProcessingException, IOException;


	/**
	 * 校验优惠码有效性 
	 * @author zbc
	 * @since 2017年5月26日 下午2:11:02
	 * @param orderAmount 订单金额
	 * @param couponsNo   优惠码
	 * @return
	 */
	public JsonNode getCouponsInfo(Double orderAmount, String couponsNo) throws JsonProcessingException, IOException;
}
