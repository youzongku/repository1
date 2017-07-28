package dto.product.inventory;

import java.util.List;

/**
 * @author longhuashen
 * @since 2016/12/16
 */
public class CreateSaleOrderResult {

    //1 发货成功 2 缺货 3 云仓锁不了 4 系统错误,5.直接去锁云仓成功
    private Integer type;
    
    private List<ShipingDto> successOrLocks;

    private List<PurchaseDto> purchases;
    
    public CreateSaleOrderResult(){
    	
    }

    /**
	 * @param type
	 * @param successOrLocks
	 * @param purchases
	 */
	public CreateSaleOrderResult(int type, List<ShipingDto> successOrLocks, List<PurchaseDto> purchases) {
		super();
		this.type = type;
		this.successOrLocks = successOrLocks;
		this.purchases = purchases;
	}

	public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<ShipingDto> getSuccessOrLocks() {
        return successOrLocks;
    }

    public void setSuccessOrLocks(List<ShipingDto> successOrLocks) {
        this.successOrLocks = successOrLocks;
    }

    public List<PurchaseDto> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<PurchaseDto> purchases) {
        this.purchases = purchases;
    }
}
