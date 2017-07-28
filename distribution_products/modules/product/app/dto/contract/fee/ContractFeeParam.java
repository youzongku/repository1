package dto.contract.fee;

import java.util.List;
import java.util.Map;

import entity.contract.ContractFeeItemRelatedSku;
/**
 * 封装合同费用项参数
 * 
 * @author huangjc
 */
public class ContractFeeParam {
	/** 合同费用项id */
	private Integer feeItemId;
	/** 合同号 */
	private String contractNo;
	/** 选择的费用项 */
	private Integer feeTypeId;

	/** 费用率 */
	private Double feeRate;
	
	/** 预估总费用 */
	private Double estimatedTotalCost;
	/** 预估总业绩 */
	private Double estimatedTotalPerformance;

	/** 实际总费用 */
	private Double realTotalCost;
	/** 实际总业绩 */
	private Double realTotalPerformance;
	
	private String optUser;
	
	private String remarks;
	
	// 符合条件的（matchedCdts）和 relatedSkus是互斥的
	private Boolean matchedCdts = null;
	// 条件
	private Map<String,Object> cdtsMap = null;
	
	/** 关联的sku */
	private List<ContractFeeItemRelatedSku> relatedSkus;

	public Map<String, Object> getCdtsMap() {
		return cdtsMap;
	}

	public void setCdtsMap(Map<String, Object> cdtsMap) {
		this.cdtsMap = cdtsMap;
	}

	public Boolean getMatchedCdts() {
		return matchedCdts;
	}

	public void setMatchedCdts(Boolean matchedCdts) {
		this.matchedCdts = matchedCdts;
	}

	public Integer getFeeItemId() {
		return feeItemId;
	}

	public void setFeeItemId(Integer feeItemId) {
		this.feeItemId = feeItemId;
	}

	public List<ContractFeeItemRelatedSku> getRelatedSkus() {
		return relatedSkus;
	}

	public void setRelatedSkus(List<ContractFeeItemRelatedSku> relatedSkus) {
		this.relatedSkus = relatedSkus;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Integer getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(Integer feeTypeId) {
		this.feeTypeId = feeTypeId;
	}

	public Double getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(Double feeRate) {
		this.feeRate = feeRate;
		this.estimatedTotalCost = null;
		this.estimatedTotalPerformance = null;
		this.realTotalCost = null;
		this.realTotalPerformance = null;
	}

	public Double getEstimatedTotalCost() {
		return estimatedTotalCost;
	}

	public void setEstimatedTotalCost(Double estimatedTotalCost) {
		this.estimatedTotalCost = estimatedTotalCost;
	}

	public Double getEstimatedTotalPerformance() {
		return estimatedTotalPerformance;
	}

	public void setEstimatedTotalPerformance(Double estimatedTotalPerformance) {
		this.estimatedTotalPerformance = estimatedTotalPerformance;
	}

	public Double getRealTotalCost() {
		return realTotalCost;
	}

	public void setRealTotalCost(Double realTotalCost) {
		this.realTotalCost = realTotalCost;
	}

	public Double getRealTotalPerformance() {
		return realTotalPerformance;
	}

	public void setRealTotalPerformance(Double realTotalPerformance) {
		this.realTotalPerformance = realTotalPerformance;
	}

	public String getOptUser() {
		return optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "ContractFeeParam [feeItemId=" + feeItemId + ", contractNo=" + contractNo + ", feeTypeId=" + feeTypeId
				+ ", feeRate=" + feeRate + ", estimatedTotalCost=" + estimatedTotalCost + ", estimatedTotalPerformance="
				+ estimatedTotalPerformance + ", realTotalCost=" + realTotalCost + ", realTotalPerformance="
				+ realTotalPerformance + ", optUser=" + optUser + ", remarks=" + remarks + ", matchedCdts="
				+ matchedCdts + ", cdtsMap=" + cdtsMap + ", relatedSkus=" + relatedSkus + "]";
	}

}
