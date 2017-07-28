package services.purchase;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import entity.purchase.DisQuotation;

/**
 * Created by luwj on 2016/3/4.
 */
public interface IDisQuotationService {
	
	/**
	 * 添加
	 * @param dq
	 * @return
	 */
	public boolean addDisQuotation(DisQuotation dq);
	
	/**
	 * 更新
	 * @param dq
	 * @return
	 */
	public boolean updateByIdSelective(DisQuotation dq);
	
	/**
	 * 根据id获取
	 * @param id
	 * @return
	 */
	public DisQuotation getDisQuotationById(int id);

    /**
     * 列表
     * @param node
     * @return
     */
    public String getRecord(JsonNode node);

    /**
     * 生成记录
     * @param node
     * @return
     */
    public String saveRecord(JsonNode node);

	/**
	 * 生成订单 更新报价单状态
	 * @param main
	 * @param email
	 */
	public Map<String,Object> buildOrder(JsonNode main);
}
