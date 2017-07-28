package dto.product_inventory;

import java.text.ParseException;
import java.util.List;

import com.google.common.collect.Lists;

import entity.product_inventory.Order;
import entity.product_inventory.OrderDetail;
import utils.inventory.DateUtils;

public class PurchaseOrderRequestParam {
	private String orderNo;
	private String account;
	private String accountName;
	private String saleOrderNo;
	private List<OrderRequest> pros;
	private List<OrderRequest> change;
	
	public Order getOrder(){
		Order order=new Order();
		order.setAccount(this.account);
		order.setAccountName(this.accountName);
		order.setOrderNo(this.orderNo);
		order.setSaleOrderNo(this.saleOrderNo);
		return order;
	}
	
	public List<OrderDetail> getOrderDetailList() throws ParseException{
		List<OrderDetail> orderDetailList=Lists.newArrayList();
		if(this.pros==null || this.pros.size()<=0){
			return null;
		}
		for (OrderRequest orderRequest : this.pros) {
			OrderDetail orderDetail=new OrderDetail();
			orderDetail.setSku(orderRequest.getSku());
			orderDetail.setProductTitle(orderRequest.getProductTitle());
			orderDetail.setImgUrl(orderRequest.getImgUrl());
			orderDetail.setIsGift(orderRequest.getIsGift());
			orderDetail.setOrderNo(this.orderNo);
			orderDetail.setPurchasePrice(orderRequest.getPurchasePrice());
			orderDetail.setQty(orderRequest.getQty());
			orderDetail.setCapfee(orderRequest.getCapfee());
			orderDetail.setWarehouseId(orderRequest.getWarehouseId());
			orderDetail.setWarehouseName(orderRequest.getWarehouseName());
			orderDetail.setArriveWarePrice(orderRequest.getArriveWarePrice());
			orderDetail.setCategoryId(orderRequest.getCategoryId());
			orderDetail.setCategoryName(orderRequest.getCategoryName());
			orderDetail.setContractNo(orderRequest.getContractNo());
			orderDetail.setClearancePrice(orderRequest.getClearancePrice());
			if(orderRequest.getExpirationDate()!=null && !"null".equals(orderRequest.getExpirationDate())&&!"".equals(orderRequest.getExpirationDate())){
				orderDetail.setExpirationDate(DateUtils.string2date(orderRequest.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE));
			}
			orderDetailList.add(orderDetail);
		}
		return orderDetailList;
	}
	
	public List<OrderDetail> getChangeList() throws ParseException{
		List<OrderDetail> changeList=Lists.newArrayList();
		if(this.change==null || this.change.size()<=0){
			return null;
		}
		for (OrderRequest orderRequest : this.change) {
			OrderDetail orderDetail=new OrderDetail();
			orderDetail.setSku(orderRequest.getSku());
			orderDetail.setProductTitle(orderRequest.getProductTitle());
			orderDetail.setImgUrl(orderRequest.getImgUrl());
			orderDetail.setIsGift(orderRequest.getIsGift());
			orderDetail.setOrderNo(this.orderNo);
			orderDetail.setPurchasePrice(orderRequest.getPurchasePrice());
			orderDetail.setQty(orderRequest.getQty());
			orderDetail.setCapfee(orderRequest.getCapfee());
			orderDetail.setWarehouseId(orderRequest.getWarehouseId());
			orderDetail.setWarehouseName(orderRequest.getWarehouseName());
			orderDetail.setArriveWarePrice(orderRequest.getArriveWarePrice());
			orderDetail.setCategoryId(orderRequest.getCategoryId());
			orderDetail.setCategoryName(orderRequest.getCategoryName());
			if(orderRequest.getExpirationDate()!=null && !"null".equals(orderRequest.getExpirationDate())&&!"".equals(orderRequest.getExpirationDate())){
				orderDetail.setExpirationDate(DateUtils.string2date(orderRequest.getExpirationDate(), DateUtils.FORMAT_DATE_PAGE));
			}
			changeList.add(orderDetail);
		}
		return changeList;
	}
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public List<OrderRequest> getPros() {
		return pros;
	}
	public void setPros(List<OrderRequest> pros) {
		this.pros = pros;
	}
	public List<OrderRequest> getChange() {
		return change;
	}
	public void setChange(List<OrderRequest> change) {
		this.change = change;
	}
	public String getSaleOrderNo() {
		return saleOrderNo;
	}
	public void setSaleOrderNo(String saleOrderNo) {
		this.saleOrderNo = saleOrderNo;
	}

	@Override
	public String toString() {
		return "PurchaseOrderRequestParam [orderNo=" + orderNo + ", account=" + account + ", accountName=" + accountName
				+ ", saleOrderNo=" + saleOrderNo + ", pros=" + pros + ", change=" + change + "]";
	}
	
}
