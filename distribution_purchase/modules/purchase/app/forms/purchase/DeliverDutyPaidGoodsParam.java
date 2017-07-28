package forms.purchase;

/**
 * 封装完税仓发货输入的数据
 * 
 * @author huangjc
 * @date 2016年12月14日
 */
public class DeliverDutyPaidGoodsParam {
	private String receiver;// 收货人
	private String telephone;// 手机
	private Integer provinceId;// 省份id
	private String address;// 收货地址
	private String postCode;// 邮编
	private String shippingCode;// 运送方式
	private String shippingName;// 运送方式名称
//	private Double money2Paid;// 待支付金额（包含运费）
	private Double bbcPostage;// 运费

//	public Double getMoney2Paid() {
//		return money2Paid;
//	}
//
//	public void setMoney2Paid(Double money2Paid) {
//		this.money2Paid = money2Paid;
//	}

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public String getShippingName() {
		return shippingName;
	}

	public void setShippingName(String shippingName) {
		this.shippingName = shippingName;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getShippingCode() {
		return shippingCode;
	}

	public void setShippingCode(String shippingCode) {
		this.shippingCode = shippingCode;
	}

	@Override
	public String toString() {
		return "DeliverDutyPaidGoodsParam [receiver=" + receiver
				+ ", telephone=" + telephone + ", provinceId=" + provinceId
				+ ", address=" + address + ", postCode=" + postCode
				+ ", shippingCode=" + shippingCode + ", shippingName="
				+ shippingName + ", bbcPostage="
				+ bbcPostage + "]";
	}

}
