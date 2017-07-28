package dto.product_inventory;

import java.util.List;
import java.util.Map;

/**
 * @author longhuashen
 * @since 2017/1/4
 */
public class CloudAndMicroInventoryDto {

    private String account;
    
    private List<Map<String,Integer>> skuWarehouseIdArray;

    private List<String> skus;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<Map<String, Integer>> getSkuWarehouseIdArray() {
		return skuWarehouseIdArray;
	}

	public void setSkuWarehouseIdArray(List<Map<String, Integer>> skuWarehouseIdArray) {
		this.skuWarehouseIdArray = skuWarehouseIdArray;
	}

	public List<String> getSkus() {
        return skus;
    }

    public void setSkus(List<String> skus) {
        this.skus = skus;
    }
}
