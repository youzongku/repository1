package dto.sales;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import util.sales.Constant;

@ApiModel(value = "发货单-财务")
public class SalesOrderInfo4FinanceDto {
	private Integer id;
	@ApiModelProperty("销售订单单号")
	private String salesOrderNo;
	@ApiModelProperty("订单在erp的单号")
	private String erpOrderNo;// 订单在erp的单号
	@ApiModelProperty("分销商")
	private String email;
	@ApiModelProperty("状态")
	private Integer status;
	@ApiModelProperty("名称")
	private String nickName;
	@ApiModelProperty("客服")
	private String customerService;
	@ApiModelProperty("毛收入")
	private Double platformAmount;// 毛收入
	@ApiModelProperty("清关金额")
	private Double clearanceAmount;// 清关金额
	@ApiModelProperty("平台运费（调整后）")
	private Double bbcPostage;// 平台运费（调整后）
	@ApiModelProperty("平台运费（调整前）")
	private Double originalFreight;// 平台运费（调整前）
	@ApiModelProperty("到仓总计")
	private Double arrviceTotal;// 到仓总计
	@ApiModelProperty("操作费")
	private Double optfee;// 操作费
	@ApiModelProperty("到总价总成本")
	private Double totalCost;// 到总价总成本
	@ApiModelProperty("到仓价利润")
	private Double profit;// 到仓价利润
	@ApiModelProperty("到仓价利润率")
	private Double profitMargin;// 到仓价利润率
	@ApiModelProperty("合同费用")
	private Double contractCharge;// 合同费用
	@ApiModelProperty("清货价总价")
	private Double clearancePriceTotal;// 清货价总价
	@ApiModelProperty("清货价总成本")
	private Double clearTotalCost;// 清货价总成本
	@ApiModelProperty("清货价利润")
	private Double clearProfit;// 清货价利润
	@ApiModelProperty("清货价利润率")
	private Double clearProfitMargin;// 清货价利润率
	@ApiModelProperty("店铺扣点金额")
	private Double sdpAmount;// 店铺扣点金额

	public static final int STATUS_FRONT = 1;
	public static final int STATUS_MANAGER = 2;
	private int frontOrManager = STATUS_MANAGER;// 前台查询（1）还是后台查询（2），默认是前台查询

	/**
	 * 状态描述，1：待采购，2：待通知发货，3：待审核，4：审核不通过，5：已取消，6：审核通过
	 */
	public String getStatusDesc() {
		String statusDesc = "";
		if (frontOrManager == STATUS_FRONT) {
			statusDesc = Constant.SALES_ORDER_STATE_FRONT.get(this.status);
		} else if (frontOrManager == STATUS_MANAGER) {
			statusDesc = Constant.SALES_ORDER_STATE_MANAGER.get(this.status);
		}
		return statusDesc;
	}

	public String getErpOrderNo() {
		return erpOrderNo;
	}

	public void setErpOrderNo(String erpOrderNo) {
		this.erpOrderNo = erpOrderNo;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setFrontOrManager(int frontOrManager) {
		this.frontOrManager = frontOrManager;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSalesOrderNo() {
		return salesOrderNo;
	}

	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCustomerService() {
		return customerService;
	}

	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}

	public Double getPlatformAmount() {
		return platformAmount;
	}

	public void setPlatformAmount(Double platformAmount) {
		this.platformAmount = platformAmount;
	}

	public Double getClearanceAmount() {
		return clearanceAmount;
	}

	public void setClearanceAmount(Double clearanceAmount) {
		this.clearanceAmount = clearanceAmount;
	}

	public Double getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(Double bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public Double getOriginalFreight() {
		return originalFreight;
	}

	public void setOriginalFreight(Double originalFreight) {
		this.originalFreight = originalFreight;
	}

	public Double getArrviceTotal() {
		return arrviceTotal;
	}

	public void setArrviceTotal(Double arrviceTotal) {
		this.arrviceTotal = arrviceTotal;
	}

	public Double getOptfee() {
		return optfee;
	}

	public void setOptfee(Double optfee) {
		this.optfee = optfee;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

	public Double getProfitMargin() {
		return profitMargin;
	}

	public void setProfitMargin(Double profitMargin) {
		this.profitMargin = profitMargin;
	}

	public Double getContractCharge() {
		return contractCharge;
	}

	public void setContractCharge(Double contractCharge) {
		this.contractCharge = contractCharge;
	}

	public Double getClearancePriceTotal() {
		return clearancePriceTotal;
	}

	public void setClearancePriceTotal(Double clearancePriceTotal) {
		this.clearancePriceTotal = clearancePriceTotal;
	}

	public Double getClearTotalCost() {
		return clearTotalCost;
	}

	public void setClearTotalCost(Double clearTotalCost) {
		this.clearTotalCost = clearTotalCost;
	}

	public Double getClearProfit() {
		return clearProfit;
	}

	public void setClearProfit(Double clearProfit) {
		this.clearProfit = clearProfit;
	}

	public Double getClearProfitMargin() {
		return clearProfitMargin;
	}

	public void setClearProfitMargin(Double clearProfitMargin) {
		this.clearProfitMargin = clearProfitMargin;
	}

	public Double getSdpAmount() {
		return sdpAmount;
	}

	public void setSdpAmount(Double sdpAmount) {
		this.sdpAmount = sdpAmount;
	}

	@Override
	public String toString() {
		return "SalesOrderInfo4FinanceDto [id=" + id + ", salesOrderNo=" + salesOrderNo + ", erpOrderNo=" + erpOrderNo
				+ ", email=" + email + ", status=" + status + ", nickName=" + nickName + ", customerService="
				+ customerService + ", platformAmount=" + platformAmount + ", clearanceAmount=" + clearanceAmount
				+ ", bbcPostage=" + bbcPostage + ", originalFreight=" + originalFreight + ", arrviceTotal="
				+ arrviceTotal + ", optfee=" + optfee + ", totalCost=" + totalCost + ", profit=" + profit
				+ ", profitMargin=" + profitMargin + ", contractCharge=" + contractCharge + ", clearancePriceTotal="
				+ clearancePriceTotal + ", clearTotalCost=" + clearTotalCost + ", clearProfit=" + clearProfit
				+ ", clearProfitMargin=" + clearProfitMargin + ", sdpAmount=" + sdpAmount + ", frontOrManager="
				+ frontOrManager + "]";
	}

}
