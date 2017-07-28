package dto.sales;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 发货单财务审核价格实体类
 * @author zbc
 * 2017年4月13日 上午10:14:25
 */
public class SalesPriceDto implements Serializable {

	private static final long serialVersionUID = 4900538086350032070L;

	private Integer id;

	private String salesOrderNo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date orderingDate;//下单时间
	
	private Double platformamount;//毛收入
	private Double clearanceamount;//清关金额
	private Double bbcpostage;//平台运费（调整后）
	private Double originalFreight;//平台运费（调整前）
	private Double arrvicetotal;//到仓总计
	private Double optfee;//操作费
	private Double totalcost;//到总价总成本
	private Double profit;//到仓价利润
	private Double profitmargin;//到仓价利润率
	private Double contractcharge;//合同费用
	private Double clearancepricetotal;//清货价总价
	private Double cleartotalcost;//清货价总成本
	private Double clearprofit;//清货价利润
	private Double clearprofitmargin;//清货价利润率
	private Double sdpamount;//店铺扣点金额

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

	public Date getOrderingDate() {
		return orderingDate;
	}

	public void setOrderingDate(Date orderingDate) {
		this.orderingDate = orderingDate;
	}

	public Double getSdpamount() {
		return sdpamount;
	}
	public void setSdpamount(Double sdpamount) {
		this.sdpamount = sdpamount;
	}
	public Double getPlatformamount() {
		return platformamount;
	}
	public void setPlatformamount(Double platformamount) {
		this.platformamount = platformamount;
	}
	public Double getClearanceamount() {
		return clearanceamount;
	}
	public void setClearanceamount(Double clearanceamount) {
		this.clearanceamount = clearanceamount;
	}
	public Double getBbcpostage() {
		return bbcpostage;
	}
	public void setBbcpostage(Double bbcpostage) {
		this.bbcpostage = bbcpostage;
	}
	public Double getArrvicetotal() {
		return arrvicetotal;
	}
	public void setArrvicetotal(Double arrvicetotal) {
		this.arrvicetotal = arrvicetotal;
	}
	public Double getOptfee() {
		return optfee;
	}
	public void setOptfee(Double optfee) {
		this.optfee = optfee;
	}
	public Double getTotalcost() {
		return totalcost;
	}
	public void setTotalcost(Double totalcost) {
		this.totalcost = totalcost;
	}
	public Double getProfit() {
		return profit;
	}
	public void setProfit(Double profit) {
		this.profit = profit;
	}
	public Double getProfitmargin() {
		return profitmargin;
	}
	public void setProfitmargin(Double profitmargin) {
		this.profitmargin = profitmargin;
	}
	public Double getContractcharge() {
		return contractcharge;
	}
	public void setContractcharge(Double contractcharge) {
		this.contractcharge = contractcharge;
	}
	public Double getClearancepricetotal() {
		return clearancepricetotal;
	}
	public void setClearancepricetotal(Double clearancepricetotal) {
		this.clearancepricetotal = clearancepricetotal;
	}
	public Double getCleartotalcost() {
		return cleartotalcost;
	}
	public void setCleartotalcost(Double cleartotalcost) {
		this.cleartotalcost = cleartotalcost;
	}
	public Double getClearprofit() {
		return clearprofit;
	}
	public void setClearprofit(Double clearprofit) {
		this.clearprofit = clearprofit;
	}
	public Double getClearprofitmargin() {
		return clearprofitmargin;
	}
	public void setClearprofitmargin(Double clearprofitmargin) {
		this.clearprofitmargin = clearprofitmargin;
	}
	public Double getOriginalFreight() {
		return originalFreight;
	}
	public void setOriginalFreight(Double originalFreight) {
		this.originalFreight = originalFreight;
	}
	
}
