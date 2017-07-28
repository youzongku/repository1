package dto.inventory;

/**
 * 用于传输发货订单表详细表中，purchase_id的单个具体信息
 * 
 * @author Administrator
 *
 */
public class BatchNoAndStockRelation {

	/**
	 * dis_inventory的id，同样可以对应到批次号
	 */
	private int ivyBatchId;

	/**
	 * 消耗了该批次多少库存
	 */
	private int consumeStock;

	public int getIvyBatchId() {
		return ivyBatchId;
	}

	public void setIvyBatchId(int ivyBatchId) {
		this.ivyBatchId = ivyBatchId;
	}

	public int getConsumeStock() {
		return consumeStock;
	}

	public void setConsumeStock(int consumeStock) {
		this.consumeStock = consumeStock;
	}

}
