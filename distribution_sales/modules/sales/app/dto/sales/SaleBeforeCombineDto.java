package dto.sales;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModelProperty;

import entity.sales.SaleBase;
import entity.sales.SaleMain;
import util.sales.IDUtils;

/**
 * 合并发货单参数实体
 * @author zbc
 * 2017年5月20日 上午10:01:54
 */
public class SaleBeforeCombineDto {

	@ApiModelProperty("分销商")
	private String account;
	
	@ApiModelProperty("分销商名称")
	private String nickName;
	
	@ApiModelProperty("订单")
	private List<String> orderNos;		

	@ApiModelProperty("订单数量")
	private Integer orderCount;
	
	@ApiModelProperty("种类数量")
	private Integer kindCount;
	
	@ApiModelProperty("商品总数量")
	private Integer totalQty;
	
	@ApiModelProperty("原先的总的运费")
	private Double originalTotalBbcPostage;
	
	@ApiModelProperty("收件人")
	private Set<String> receivers = Sets.newHashSet();
	
	@ApiModelProperty("联系方式")
	private String tel;
	
	@ApiModelProperty("收货地址 ")
	private String address;
	
	@ApiModelProperty("物流方式代码 ")
	private String logisticsTypeCode;
	
	@ApiModelProperty("物流名称 ")
	private String logisticsMode; 
	
	@ApiModelProperty("合并发货运费")
	private Double bbcPostage;
	
	@ApiModelProperty("待支付金额")
	private Double orderTotalAmount;

	@ApiModelProperty("合并发货信息uid")
	private String uid;
	
	/**
	 * 仓库id
	 */
	private Integer warehouseId;
	
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	
	private Integer distributorType;
	
	/**
	 * 分销渠道
	 */
	private Integer disMode;
	
	/**
	 * 业务员
	 */
	private String saleMan;

	public SaleBeforeCombineDto() {
		this.uid = IDUtils.getUUID();
	}

	public SaleBeforeCombineDto(SaleMain main,SaleBase base) {
		super();
		this.account = main.getEmail();
		this.nickName = main.getNickName();
		this.tel = base.getTel();
		this.address = base.getAddress();
		this.logisticsTypeCode = base.getLogisticsTypeCode();
		this.logisticsMode = base.getLogisticsMode();
		this.uid = IDUtils.getUUID();
		this.warehouseId  = main.getWarehouseId();
		this.warehouseName = main.getWarehouseName();
		this.distributorType = main.getDistributorType();
		this.disMode = main.getDisMode();
		this.saleMan = base.getCustomerservice();
	}
	
	public Double getOriginalTotalBbcPostage() {
		return originalTotalBbcPostage;
	}

	public void setOriginalTotalBbcPostage(Double originalTotalBbcPostage) {
		this.originalTotalBbcPostage = originalTotalBbcPostage;
	}

	public String getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(String saleMan) {
		this.saleMan = saleMan;
	}

	public Integer getDisMode() {
		return disMode;
	}

	public void setDisMode(Integer disMode) {
		this.disMode = disMode;
	}

	public Integer getDistributorType() {
		return distributorType;
	}

	public void setDistributorType(Integer distributorType) {
		this.distributorType = distributorType;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public List<String> getOrderNos() {
		return orderNos;
	}

	public void setOrderNos(List<String> orderNos) {
		this.orderCount = orderNos.size();
		this.orderNos = orderNos;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public Integer getKindCount() {
		return kindCount;
	}

	public void setKindCount(Integer kindCount) {
		this.kindCount = kindCount;
	}

	public Integer getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(Integer totalQty) {
		this.totalQty = totalQty;
	}

	public Set<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(Set<String> receivers) {
		this.receivers = receivers;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLogisticsTypeCode() {
		return logisticsTypeCode;
	}

	public void setLogisticsTypeCode(String logisticsTypeCode) {
		this.logisticsTypeCode = logisticsTypeCode;
	}

	public String getLogisticsMode() {
		return logisticsMode;
	}

	public void setLogisticsMode(String logisticsMode) {
		this.logisticsMode = logisticsMode;
	}

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public Double getOrderTotalAmount() {
		return orderTotalAmount;
	}

	public void setOrderTotalAmount(Double orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "SaleBeforeCombineDto [account=" + account + ", nickName=" + nickName + ", orderNos=" + orderNos
				+ ", orderCount=" + orderCount + ", kindCount=" + kindCount + ", totalQty=" + totalQty
				+ ", originalTotalBbcPostage=" + originalTotalBbcPostage + ", receivers=" + receivers + ", tel=" + tel
				+ ", address=" + address + ", logisticsTypeCode=" + logisticsTypeCode + ", logisticsMode="
				+ logisticsMode + ", bbcPostage=" + bbcPostage + ", orderTotalAmount=" + orderTotalAmount + ", uid="
				+ uid + ", warehouseId=" + warehouseId + ", warehouseName=" + warehouseName + ", distributorType="
				+ distributorType + ", disMode=" + disMode + ", saleMan=" + saleMan + "]";
	}
	
}
