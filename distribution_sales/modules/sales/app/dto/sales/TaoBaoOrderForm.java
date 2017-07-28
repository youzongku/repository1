package dto.sales;


import java.util.List;

import entity.platform.order.template.TaoBaoOrder;

@SuppressWarnings("serial")
public class TaoBaoOrderForm extends TaoBaoOrder {
	
	private String province;//省
	private String city;//市
	private String county;//县or区
	private String street;//详细地址or街道
	private List<String> orderList;//订单编号集合
	private Integer pageNo;//页码
	private Integer pageSize;//每页条数
	
	
	public List<String> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<String> orderList) {
		this.orderList = orderList;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	
}
