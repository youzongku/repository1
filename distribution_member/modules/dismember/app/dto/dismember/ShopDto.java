package dto.dismember;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by LSL on 2015/12/16.
 */
public class ShopDto implements Serializable {

	private static final long serialVersionUID = 8942942840894413330L;

	private Integer id;// 店铺ID

	private String name;// 店铺名称
	
	private Date createDate;//添加时间
	
	private String createDateStr;

	private String uri;// 店铺链接

	private String type;// 店铺平台
	
	private String cate;// 店铺种类

	private String tel;// 联系方式

	private Integer pfid;// 平台ID

	// 都云涛添加省市县名
	private Integer provinceId;
	private Integer cityId;
	private Integer areaId;

	private String provinceName;
	private String cityName;
	private String areaName;

	private String addr;

	private String keeperName;
	
	private String shroffAccountNumber;//收款账户
	
	private String idcard;//身份证号码
	private String zipCode;//邮编
	
	private Integer parentId;
	
	private String clientid;
	
	private String clientsecret;
	
	private String shopAccount;

	private Double shopDeductionPoints;
	
	public String getShopAccount() {
		return shopAccount;
	}

	public void setShopAccount(String shopAccount) {
		this.shopAccount = shopAccount;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getClientsecret() {
		return clientsecret;
	}

	public void setClientsecret(String clientsecret) {
		this.clientsecret = clientsecret;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateDateStr() {
		return createDateStr;
	}

	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Integer getPfid() {
		return pfid;
	}

	public void setPfid(Integer pfid) {
		this.pfid = pfid;
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

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getKeeperName() {
		return keeperName;
	}

	public void setKeeperName(String keeperName) {
		this.keeperName = keeperName;
	}
	
	public String getShroffAccountNumber() {
		return shroffAccountNumber;
	}

	public void setShroffAccountNumber(String shroffAccountNumber) {
		this.shroffAccountNumber = shroffAccountNumber;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Double getShopDeductionPoints() {
		return shopDeductionPoints;
	}

	public void setShopDeductionPoints(Double shopDeductionPoints) {
		this.shopDeductionPoints = shopDeductionPoints;
	}
}
