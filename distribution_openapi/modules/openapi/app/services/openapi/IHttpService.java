package services.openapi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import play.mvc.Http.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IHttpService {

	/**
	 * 获取店铺信息
	 * 
	 * @param email
	 *            分销账号
	 * @param shopName
	 *            店铺名称
	 * @param platformId
	 *            平台ID
	 * @return
	 */
	public JsonNode getShopInfo(String email, String shopName, Integer platformId, Context context) throws JsonProcessingException, IOException;

	/**
	 * 获取分销用户客服账号
	 * 
	 * @param email
	 *            分销账号
	 * @return
	 */
	public JsonNode custaccount(String email) throws JsonProcessingException, IOException;

	/**
	 * 获取商品信息
	 * 
	 * @param skus
	 * @param warehouseId
	 * @param distributionMode 
	 * @return
	 */
	public JsonNode fgetProducts(String email, List<String> skus, Integer warehouseId, Integer distributionMode)throws JsonProcessingException, IOException;
	/**
	 * 获取省ID
	 * 
	 * @param province
	 * @return
	 */
	public JsonNode getProvinces(String province) throws JsonProcessingException, IOException;


	/**
	 * @param warehouseId
	 * @param shippingCode
	 * @param skuList
	 * @param provinceId
	 * @param countryId
	 * @return
	 */
	public JsonNode getFreight(Integer warehouseId, String shippingCode, JsonNode skuList, Integer provinceId,
			Integer countryId) throws JsonProcessingException, IOException;

	/**
	 * 获取分销商信息
	 * 
	 * @param email
	 * @return
	 */
	public JsonNode getMemberInfo(String email) throws JsonProcessingException, IOException;

	/**
	 * 检查库存信息
	 * 
	 * @param email
	 * @param salesOrderNo
	 * @param sd
	 * @return
	 */
	// TODO -------------仓库接口，需要进行对接--------------
/*	public JsonNode checkInventory(String email, String salesOrderNo, List<SaleDetail> sd) throws JsonProcessingException, IOException;*/
/*	*//**
	 * 扣除库存
	 * 
	 * @param email
	 *            分销账号
	 * @param sd
	 *            扣减详情
	 * @return
	 */
	// TODO -------------仓库接口，需要进行对接--------------
	/*
	public String deductionStock(String email, List<SaleDetail> sd) throws JsonProcessingException, IOException;
*/
	/**
	 * 生成采购单
	 * 
	 * @param pMap
	 *            生成采购单参数
	 * @param context 
	 * @return
	 */
	public JsonNode postPurchase(Map<String, Object> pMap, Context context) throws JsonProcessingException, IOException;

	/**
	 * 分页查询采购订单
	 * @author zbc
	 * @since 2016年8月26日 下午7:31:19
	 */
	JsonNode viewpurchase(Map<String, Object> param) throws JsonProcessingException, IOException;

	/**
	 * 采购入库
	 * 
	 * @param email
	 *            分销账号
	 * @param updIvyParam
	 *            入库详情
	 * @param purchaseNo
	 *            采购单号
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	// TODO -------------仓库接口，需要进行对接--------------
	/*public JsonNode updateInventory(String email, List<SaleDetail> updIvyParam, String purchaseNo) throws JsonProcessingException, IOException;

	*/

}
