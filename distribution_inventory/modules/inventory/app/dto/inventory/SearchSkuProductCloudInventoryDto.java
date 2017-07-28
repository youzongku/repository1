package dto.inventory;

import java.io.Serializable;
import java.util.List;

/**
 * @author longhuashen
 * @since 2016/12/6
 */
public class SearchSkuProductCloudInventoryDto implements Serializable {

    private Integer warehouseId;

    private List<String> skus;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<String> getSkus() {
        return skus;
    }

    public void setSkus(List<String> skus) {
        this.skus = skus;
    }
}
