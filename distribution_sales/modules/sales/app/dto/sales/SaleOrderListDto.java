package dto.sales;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import entity.sales.SaleDetail;
import util.sales.Constant;

public class SaleOrderListDto {

	private int id;
	private int status;// 状态
	private String pno;// 平台单号
	private String sno;// 销售单单号
	private String odate;// 下单日期
	private String receiver;// 收货人姓名
	private String bbcPostage;//分销平台运费
	private String purchaseOrderNo;//可能存在的缺货采购单单号
	private List<SaleDetail> saleDetails;//销售单详情
	private String statusMess;
	private String couponsCode;
	private Double couponsAmount;
	// 是否已通知发货（0：未通知，1：已通知
	private Integer isNotified;
	/**
	 * 是否合并发货标识:true 已合并  false 未合并
	 */
	private Boolean isCombine;
	
	public static final int STATUS_FRONT = 1;
	public static final int STATUS_MANAGER = 2;
	private int frontOrManager = 1;// 前台查询（1）还是后台查询（2），默认是前台查询

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date confirmReceiptDate;//确认收货时间

	private boolean shFlag;//是否可以发起售后
	private String thirdLogistisCode;//物流公司编码
	
	public String getStatusMess() {
		if(frontOrManager==1){
			statusMess = Constant.SALES_ORDER_STATE_FRONT.get(this.status);
		}else if(frontOrManager==2){
			statusMess = Constant.SALES_ORDER_STATE_MANAGER.get(this.status);
		}
		return statusMess;
	}

	public Boolean getIsCombine() {
		return isCombine;
	}

	public void setIsCombine(Boolean isCombine) {
		this.isCombine = isCombine;
	}

	public Integer getIsNotified() {
		return isNotified;
	}

	public void setIsNotified(Integer isNotified) {
		this.isNotified = isNotified;
	}

	public void setFrontOrManager(int frontOrManager) {
		this.frontOrManager = frontOrManager;
	}

	public void setStatusMess(String statusMess) {
		this.statusMess = statusMess;
	}

	public String getCouponsCode() {
		return couponsCode;
	}

	public void setCouponsCode(String couponsCode) {
		this.couponsCode = couponsCode;
	}

	public Double getCouponsAmount() {
		return couponsAmount;
	}

	public void setCouponsAmount(Double couponsAmount) {
		this.couponsAmount = couponsAmount;
	}

	public String getBbcPostage() {
		return bbcPostage;
	}

	public void setBbcPostage(String bbcPostage) {
		this.bbcPostage = bbcPostage;
	}

	public List<SaleDetail> getSaleDetails() {
		return saleDetails;
	}

	public void setSaleDetails(List<SaleDetail> saleDetails) {
		this.saleDetails = saleDetails;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPno() {
		return pno;
	}

	public void setPno(String pno) {
		this.pno = pno;
	}

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getOdate() {
		return odate;
	}

	public void setOdate(String odate) {
		this.odate = odate;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Date getConfirmReceiptDate() {
		return confirmReceiptDate;
	}

	public void setConfirmReceiptDate(Date confirmReceiptDate) {
		this.confirmReceiptDate = confirmReceiptDate;
	}

	public boolean isShFlag() {
		return shFlag;
	}

	public void setShFlag(boolean shFlag) {
		this.shFlag = shFlag;
	}

	public String getThirdLogistisCode() {
		return thirdLogistisCode;
	}

	public void setThirdLogistisCode(String thirdLogistisCode) {
		this.thirdLogistisCode = thirdLogistisCode;
	}
}
