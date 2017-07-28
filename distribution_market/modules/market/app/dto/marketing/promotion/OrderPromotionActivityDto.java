package dto.marketing.promotion;

import java.util.List;

public class OrderPromotionActivityDto {
	// 用户属性(1.普通分销商 2.合营分销商 3.内部分销售 4。新注册用户 5.第一次购物用户 6.3个月内未购物的用户)
	private Integer userAttr;
	//用户账号
	private String account;
	// 用户模式
	private Integer userMode;
	// 金额
	private Double money;
	// 商品总重量(单位克)
	private Double totalWeight;
	// 收货地址（XX省XX市XX区格式）
	private Integer cityId;
	// 商品总数量
	private Integer totalNumber;
	//支付时间
	private Long  paymentTime;
	// 商品详情List
	private List<CommodityDetail> commodity;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getUserMode() {
		return userMode;
	}

	public void setUserMode(Integer userMode) {
		this.userMode = userMode;
	}

	public Integer getUserAttr() {
		return userAttr;
	}

	public void setUserAttr(Integer userAttr) {
		this.userAttr = userAttr;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(Double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}

	public List<CommodityDetail> getCommodity() {
		return commodity;
	}

	public void setCommodity(List<CommodityDetail> commodity) {
		this.commodity = commodity;
	}

	public Long getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Long paymentTime) {
		this.paymentTime = paymentTime;
	}

	@Override
	public String toString() {
		return "OrderPromotionActivityDto [userAttr=" + userAttr
				+ ", userMode=" + userMode + ", money=" + money
				+ ", totalWeight=" + totalWeight + ", cityId=" + cityId
				+ ", totalNumber=" + totalNumber + ", paymentTime="
				+ paymentTime + ", commodity=" + commodity + "]";
	}

}
