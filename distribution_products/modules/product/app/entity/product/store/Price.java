package entity.product.store;

/**
 * SKU价格表实体
 */
public class Price extends StoreBase {


	private static final long serialVersionUID = -4532883264263445018L;

	/**
     * 主键
     */
    private String id;

    /**
     * 实体id
     */
    private Integer entityId;

    /**
     * 价格字段
     */
    private String priceKey;

    /**
     * 价格值
     */
    private Double priceValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getPriceKey() {
        return priceKey;
    }

    public void setPriceKey(String priceKey) {
        this.priceKey = priceKey;
    }

    public Double getPriceValue() {
        return priceValue;
    }

    public void setPriceValue(Double priceValue) {
        this.priceValue = priceValue;
    }
}