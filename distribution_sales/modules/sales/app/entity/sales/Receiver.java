package entity.sales;

import java.io.Serializable;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModel;

@ApiModel
public class Receiver implements Serializable {
	private Integer id;

	private String receiverName;// 收件人姓名

	private String receiverTel;// 收件人电话

	private String receiverAddr;// 收件人地址

	private String receiverIdcard;// 收件人证件号码

	private Integer salesOrderId;// 销售订单id

	// 都云涛新增
	private Integer provinceId;// 省id
	private Integer cityId;// 市id
	private Integer areaId;// 县id

	private String email;
	private Integer postCode;

	private Date modifyDate;
	
	private String provinceName;// 省份名称
	
	private String cityName;// 省份名称
	
	private String areaName;// 省份名称
	
	// 将几个字段拼接起来
	// receiver_name province_namecity_namearea_namereceiver_addr receiver_idcard post_code
	private String stringMsg;// 

	public String getStringMsg() {
		return stringMsg;
	}

	public void setStringMsg(String stringMsg) {
		this.stringMsg = stringMsg;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverTel() {
		return receiverTel;
	}

	public void setReceiverTel(String receiverTel) {
		this.receiverTel = receiverTel;
	}

	public String getReceiverAddr() {
		return receiverAddr;
	}

	public void setReceiverAddr(String receiverAddr) {
		this.receiverAddr = receiverAddr;
	}

	public String getReceiverIdcard() {
		return receiverIdcard;
	}

	public void setReceiverIdcard(String receiverIdcard) {
		this.receiverIdcard = receiverIdcard;
	}

	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getPostCode() {
		return postCode;
	}

	public void setPostCode(Integer postCode) {
		this.postCode = postCode;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

}