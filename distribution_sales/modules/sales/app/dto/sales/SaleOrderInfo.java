package dto.sales;

import java.util.List;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import entity.sales.OperateRecord;
import entity.sales.Receiver;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;

@ApiModel(value="发货单内容")
public class SaleOrderInfo {
	@ApiModelProperty("销售订单主体")
	private SaleMain saleMain;// 销售订单主体
	@ApiModelProperty("销售订单基本信息")
	private SaleBase saleBase;// 销售订单基本信息
	@ApiModelProperty("销售订单收件人信息")
	private Receiver receiver;// 销售订单收件人信息
	@ApiModelProperty("销售订单商品列表信息")
	private List<SaleDetail> saleDetails;// 销售订单商品列表信息
	@ApiModelProperty("操作记录列表")
	private List<OperateRecord> opRecordList;// 操作记录列表

	public SaleMain getSaleMain() {
		return saleMain;
	}

	public void setSaleMain(SaleMain saleMain) {
		this.saleMain = saleMain;
	}

	public SaleBase getSaleBase() {
		return saleBase;
	}

	public void setSaleBase(SaleBase saleBase) {
		this.saleBase = saleBase;
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public List<SaleDetail> getSaleDetails() {
		return saleDetails;
	}

	public void setSaleDetails(List<SaleDetail> saleDetails) {
		this.saleDetails = saleDetails;
	}

	public List<OperateRecord> getOpRecordList() {
		return opRecordList;
	}

	public void setOpRecordList(List<OperateRecord> opRecordList) {
		this.opRecordList = opRecordList;
	}

}
