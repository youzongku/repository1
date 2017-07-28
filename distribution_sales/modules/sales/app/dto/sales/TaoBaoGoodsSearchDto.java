package dto.sales;

import java.util.List;

/**
 * 淘宝订单商品信息搜索条件dto
 * @author ouyangyaxiong
 * @date 2016年4月9日下午2:47:46
 */
public class TaoBaoGoodsSearchDto {
	private List<String> skuList;
	
	private String orderNo;//订单编号
	
	private String email;//订单归属人邮箱
	
	private List<String> orderNoList;
	
	private String warehouseId;

	public TaoBaoGoodsSearchDto(){
		
	}
	
	public TaoBaoGoodsSearchDto(List<String> skuList, String orderNo, String email, List<String> orderNoList,
			String warehouseId) {
		this.skuList = skuList;
		this.orderNo = orderNo;
		this.email = email;
		this.orderNoList = orderNoList;
		this.warehouseId = warehouseId;
	}

	public List<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<String> skuList) {
		this.skuList = skuList;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getOrderNoList() {
		return orderNoList;
	}

	public void setOrderNoList(List<String> orderNoList) {
		this.orderNoList = orderNoList;
	}
	

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Override
	public String toString() {
		return "TaoBaoGoodsSearchDto [skuList=" + skuList + ", orderNo=" + orderNo + ", orderNoList=" + orderNoList
				+ "]";
	}
	
}
