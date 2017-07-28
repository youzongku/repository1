package dto.inventory;

import java.util.List;

public class SaleOrderDetailPurIdInfo {

	private String saleOrderDetailOrderId;

	private List<BatchNoAndStockRelation> batchNoAndStockRelations;

	public String getSaleOrderDetailOrderId() {
		return saleOrderDetailOrderId;
	}

	public void setSaleOrderDetailOrderId(String saleOrderDetailOrderId) {
		this.saleOrderDetailOrderId = saleOrderDetailOrderId;
	}

	public List<BatchNoAndStockRelation> getBatchNoAndStockRelations() {
		return batchNoAndStockRelations;
	}

	public void setBatchNoAndStockRelations(
			List<BatchNoAndStockRelation> batchNoAndStockRelations) {
		this.batchNoAndStockRelations = batchNoAndStockRelations;
	}

}
