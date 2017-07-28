package dto.inventory;

/**
 * @author longhuashen
 * @since 2016/12/6
 */
public enum ProductInventoryOrderLockEnum {

    INVALID("失效", 0),

    EFFECTIVE("生效", 1);

    /**
     * 值
     */
    private Integer value;

    /**
     * 描述
     */
    private String desc;

    ProductInventoryOrderLockEnum(String desc, Integer value) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }



    public String getDesc() {
        return desc;
    }

}
