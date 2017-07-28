package services.dismember;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface IHttpService {
	
	/**
	 * 获取采购单
	 * @param purchaseOrderNo
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getPurchaseOrder(String purchaseOrderNo)
			throws JsonProcessingException, IOException;
	
	/**
	 * 获取发货单
	 * {
		 suc:true,
		 saleMain:{},
		 saleBase:{},
		 details:[]
		 }
	 * @param salesOrderNo
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getSalesOrder(String salesOrderNo)
			throws JsonProcessingException, IOException;
	
	/**
	 * 获取合并发货单
	 * @param hbNo
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getCombinedSalesOrder(String hbNo)
			throws JsonProcessingException, IOException;
	
	public default JsonNode parseString(String str) throws JsonProcessingException, IOException {
		ObjectMapper obj = new ObjectMapper();
		return obj.readTree(str);
	}
}
