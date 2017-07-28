package services.warehousing;

import com.fasterxml.jackson.databind.JsonNode;

import dto.warehousing.ReturnMess;

/**
 * Created by luwj on 2016/1/6.
 *
 * 存储erp推送仓库信息、商品库存信息
 */
public interface IWarehInvenService {

    /**
     * 存储仓库信息（全量接口）
     * @param node
     * @return
     */
    public ReturnMess saveWarehouse(JsonNode node);

    /**
     * 存储商品库存信息（增量接口）
     * @param node
     * @return
     */
    public ReturnMess saveInvenInfo(JsonNode node);
}
