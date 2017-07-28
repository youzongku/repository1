package entity.timer;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺信息
 * @author Administrator
 *
 */
public class ShopDto implements Serializable {

	private static final long serialVersionUID = 5166470099418422724L;

	private Integer id;

	private Integer accountId;

	private String shopName;

	private String shopUrl;

	private String remarks;

	private Date createDate;

	private Date lastUpdateDate;

	private String email;// 用户邮箱

	private String telphone;

	private Integer platformId;// 店铺平台ID(淘宝、天猫、京东、亚马逊......)

	private Integer categoryId;// 店铺类型ID(B2B/B2C/C2C/O2O......)

	private String otherPlatform;// 其他(店铺平台为其他时使用)
	
	private String shroffAccountNumber;//收款账户

	// 都云涛新增属性，省市县id，省市县名
	private Integer provinceId;
	private Integer cityId;
	private Integer areaId;

	private String provinceName;
	private String cityName;
	private String areaName;

	// 都云涛新增属性
	/**
	 * 详细地址
	 */
	private String addr;

	// 都云涛新增属性
	/**
	 * 店主姓名
	 */
	private String keeperName;

	// 都云涛新增属性
	/**
	 * 所在平台名
	 */
	private String platformName;
	
	private String idcard;//身份证号码
	private String zipCode;//邮编
	
	private Integer parentId;//父Id
	
	//add by xuse API获取订单配置参数
	private String clientid;
	private String redirecturi;
	private String clientsecret;
	private String code;
	private String accesstoken;
	private String refreshtoken;	
	private Date createtime;
	private Date endtime;
	private Date updatetime;
	//线上店铺账号（方便有赞店铺拉取订单）
	private String shopAccount;

	private String shopNo;

	public String getShopNo() {
		return shopNo;
	}

	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}

	public String getShopAccount() {
		return shopAccount;
	}

	public void setShopAccount(String shopAccount) {
		this.shopAccount = shopAccount;
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

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public Integer getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getOtherPlatform() {
		return otherPlatform;
	}

	public void setOtherPlatform(String otherPlatform) {
		this.otherPlatform = otherPlatform;
	}

	public String getShroffAccountNumber() {
		return shroffAccountNumber;
	}

	public void setShroffAccountNumber(String shroffAccountNumber) {
		this.shroffAccountNumber = shroffAccountNumber;
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

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getRedirecturi() {
		return redirecturi;
	}

	public void setRedirecturi(String redirecturi) {
		this.redirecturi = redirecturi;
	}

	public String getClientsecret() {
		return clientsecret;
	}

	public void setClientsecret(String clientsecret) {
		this.clientsecret = clientsecret;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getRefreshtoken() {
		return refreshtoken;
	}

	public void setRefreshtoken(String refreshtoken) {
		this.refreshtoken = refreshtoken;
	}
	
}