package dto.inventory;

import java.util.List;

/**
 * 微仓变更时，通知云仓进行变更的格式实体
 * 
 * @author Alvin Du
 *
 */
public class APIInventoryDto {

	/**
	 * 1:入库，0：出库，必填项
	 */
	private Integer orderType;

	/**
	 * 订单标题，必填，销售出库、采购入库、其他出库、其他入库等等
	 */
	private String orderTitle;

	/**
	 * 订单单号，例如：TTCG_201512240000000037
	 */
	private String orderNo;

	/**
	 * 订单来源，默认b2b，除非特殊情况，否则无需修改
	 */
	private String orderOrigin = "b2b";

	/**
	 * 分销商id
	 */
	private Integer distributorId;

	/**
	 * 分销商昵称
	 */
	private String distributorName;

	/**
	 * 分销商邮箱
	 */
	private String distributorEmail;

	/**
	 * 微仓变更详情
	 */
	private List<APIInventoryDtoDetail> detailList;

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getOrderTitle() {
		return orderTitle;
	}

	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderOrigin() {
		return orderOrigin;
	}

	public void setOrderOrigin(String orderOrigin) {
		this.orderOrigin = orderOrigin;
	}

	public Integer getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(Integer distributorId) {
		this.distributorId = distributorId;
	}

	public String getDistributorName() {
		return distributorName;
	}

	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}

	public String getDistributorEmail() {
		return distributorEmail;
	}

	public void setDistributorEmail(String distributorEmail) {
		this.distributorEmail = distributorEmail;
	}

	public List<APIInventoryDtoDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<APIInventoryDtoDetail> detailList) {
		this.detailList = detailList;
	}

}
