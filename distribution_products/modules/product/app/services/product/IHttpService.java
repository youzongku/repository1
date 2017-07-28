package services.product;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dto.contract.fee.ContractFeeItemDto;
import dto.product.inventory.InventoryCloudLockDto;
import dto.product.inventory.SaleLockDto;

public interface IHttpService {
	
	/**
	 * 通知sales模块计算合同费用
	 */
	JsonNode notifyCalContractFee(ContractFeeItemDto feeItem) throws JsonProcessingException,
	IOException; 
	
	/**
	 * 获取云仓商品到期日期
	 * 
	 * @param email
	 * @param params 内含sku & warehouseId
	 * @return [{"id":8932,"sku":"IF942-1","stock":93786,"warehouseId":2024,"warehouseName":"深圳仓",
				"updateTime":"2017-02-24","createTime":"2017-01-24","expirationDate":"2017-03-25"},...]
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getCloudProductsExpirationDate(List<ObjectNode> skuWarehouseIdNodeList) throws JsonProcessingException,
	IOException;

	/**
	 * 获取商品库存api
	 * @author zbc
	 * @since 2017年1月4日 下午4:11:27
	 */
	public JsonNode getProStock(String email,List<String> skus)
			throws Exception;

	/**
	 * 获取微仓数据
	 * @author zbc
	 * @since 2017年1月7日 下午5:04:40
	 */
	public JsonNode getMriStock(String email, List<String> skus, Integer warehouseId) throws Exception;
	
	/**
	 * 查询用户信息
	 * @param email
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getMemberInfo(String email) throws JsonProcessingException, IOException;

	/**
	 * KA锁库 ——》 云仓锁库
	 * @author zbc
	 * @since 2017年4月21日 下午3:29:09
	 */
	public JsonNode cloudLock(InventoryCloudLockDto cloudLock) throws JsonProcessingException, IOException;

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月22日 上午11:01:11
	 */
	public JsonNode getOrderDetails(String orderNO) throws JsonProcessingException, IOException;

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月24日 上午9:26:53
	 */
	public JsonNode microLock(SaleLockDto param) throws JsonProcessingException, IOException;

	/**
	 * 
	 * @author zbc
	 * @since 2017年4月24日 上午9:56:05
	 */
	public JsonNode getMicroProductsExpirationDate(String email, List<ObjectNode> skuWarehouseIdNodeList)
			throws JsonProcessingException, IOException;

}
