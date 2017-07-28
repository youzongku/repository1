package dto.inventory;

public class FirstInFirstOutDisIvyInfo {

	/**
	 * dis_inventory的id
	 */
	private int id;

	/**
	 * dis_inventory的id，对应的仓库的批次的库存数量
	 */
	private int stock;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

}
