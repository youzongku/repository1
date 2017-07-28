package entity.product;

import java.io.Serializable;
import java.util.Date;

public class ProductDisprice implements Serializable{

	private static final long serialVersionUID = -7161185845921013384L;

	private Integer id;

    private String sku;

    private Integer disStockId;

    private Double disProfitRate;

    private Double disProfit;

    private Double disVat;

    private Double disStockFee;

    private String disShippingType;

    private Double disOtherCost;

    private Double disTotalCost;

    private Double disTransferFee;

    private Double disListFee;

    private Double disTradeFee;

    private Double disPayFee;

    private Double disPostalFee;

    private Double disImportTar;

    private Double disGst;

    private Double disInsurance;

    private Double disTotalVat;

    private Double cost;

    private Double disFreight;

    private Double disPrice;

    private Double disCifPrice;

    private Date operateDate;

    private Double floorPrice;

    private Double proposalRetailPrice;

    private Double distributorPrice;

    private Double electricityPrices;

    private Double supermarketPrice;

    private Double arriveWarePrice;

    private Double disCompanyCost;

    private Double commissionFees;

    private Double oceanFreight;

    private Double deliveryTakingFee;

    private Double customsInspectionFee;

    private Double stockFee;

    private Double logisticFee;

    private Double lableMakingFee;

    private Double otherIncidentals;

    private Double postalFeeRate;

    private Double importTarRate;

    private Double gstRate;

    private Double vatRate;

    private Double marketInterventionPrice;

    private Double ftzPrice;

    private Double destinationIncidentals;

    private Double costomsDeclarenceCharges;

    private Double costomsCensoringCharges;

    private Double customsInspectionCharges;

    private Double labelRecordCharges;

    private Double inspectionAndQuarantineCharges;

    private Double labelServiceCharges;

    private Double importAgentServiceCharges;

    private Double otherReimbursedExpenses;

    private Double labelingCharge;

    private Double minimumProfitRate;

    private Double otherFinanceIncidentals;

    private Integer typeId;

    private String typeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getDisStockId() {
        return disStockId;
    }

    public void setDisStockId(Integer disStockId) {
        this.disStockId = disStockId;
    }

    public Double getDisProfitRate() {
        return disProfitRate;
    }

    public void setDisProfitRate(Double disProfitRate) {
        this.disProfitRate = disProfitRate;
    }

    public Double getDisProfit() {
        return disProfit;
    }

    public void setDisProfit(Double disProfit) {
        this.disProfit = disProfit;
    }

    public Double getDisVat() {
        return disVat;
    }

    public void setDisVat(Double disVat) {
        this.disVat = disVat;
    }

    public Double getDisStockFee() {
        return disStockFee;
    }

    public void setDisStockFee(Double disStockFee) {
        this.disStockFee = disStockFee;
    }

    public String getDisShippingType() {
        return disShippingType;
    }

    public void setDisShippingType(String disShippingType) {
        this.disShippingType = disShippingType;
    }

    public Double getDisOtherCost() {
        return disOtherCost;
    }

    public void setDisOtherCost(Double disOtherCost) {
        this.disOtherCost = disOtherCost;
    }

    public Double getDisTotalCost() {
        return disTotalCost;
    }

    public void setDisTotalCost(Double disTotalCost) {
        this.disTotalCost = disTotalCost;
    }

    public Double getDisTransferFee() {
        return disTransferFee;
    }

    public void setDisTransferFee(Double disTransferFee) {
        this.disTransferFee = disTransferFee;
    }

    public Double getDisListFee() {
        return disListFee;
    }

    public void setDisListFee(Double disListFee) {
        this.disListFee = disListFee;
    }

    public Double getDisTradeFee() {
        return disTradeFee;
    }

    public void setDisTradeFee(Double disTradeFee) {
        this.disTradeFee = disTradeFee;
    }

    public Double getDisPayFee() {
        return disPayFee;
    }

    public void setDisPayFee(Double disPayFee) {
        this.disPayFee = disPayFee;
    }

    public Double getDisPostalFee() {
        return disPostalFee;
    }

    public void setDisPostalFee(Double disPostalFee) {
        this.disPostalFee = disPostalFee;
    }

    public Double getDisImportTar() {
        return disImportTar;
    }

    public void setDisImportTar(Double disImportTar) {
        this.disImportTar = disImportTar;
    }

    public Double getDisGst() {
        return disGst;
    }

    public void setDisGst(Double disGst) {
        this.disGst = disGst;
    }

    public Double getDisInsurance() {
        return disInsurance;
    }

    public void setDisInsurance(Double disInsurance) {
        this.disInsurance = disInsurance;
    }

    public Double getDisTotalVat() {
        return disTotalVat;
    }

    public void setDisTotalVat(Double disTotalVat) {
        this.disTotalVat = disTotalVat;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getDisFreight() {
        return disFreight;
    }

    public void setDisFreight(Double disFreight) {
        this.disFreight = disFreight;
    }

    public Double getDisPrice() {
        return disPrice;
    }

    public void setDisPrice(Double disPrice) {
        this.disPrice = disPrice;
    }

    public Double getDisCifPrice() {
        return disCifPrice;
    }

    public void setDisCifPrice(Double disCifPrice) {
        this.disCifPrice = disCifPrice;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public Double getFloorPrice() {
        return floorPrice;
    }

    public void setFloorPrice(Double floorPrice) {
        this.floorPrice = floorPrice;
    }

    public Double getProposalRetailPrice() {
        return proposalRetailPrice;
    }

    public void setProposalRetailPrice(Double proposalRetailPrice) {
        this.proposalRetailPrice = proposalRetailPrice;
    }

    public Double getDistributorPrice() {
        return distributorPrice;
    }

    public void setDistributorPrice(Double distributorPrice) {
        this.distributorPrice = distributorPrice;
    }

    public Double getElectricityPrices() {
        return electricityPrices;
    }

    public void setElectricityPrices(Double electricityPrices) {
        this.electricityPrices = electricityPrices;
    }

    public Double getSupermarketPrice() {
        return supermarketPrice;
    }

    public void setSupermarketPrice(Double supermarketPrice) {
        this.supermarketPrice = supermarketPrice;
    }

    public Double getArriveWarePrice() {
        return arriveWarePrice;
    }

    public void setArriveWarePrice(Double arriveWarePrice) {
        this.arriveWarePrice = arriveWarePrice;
    }

    public Double getDisCompanyCost() {
        return disCompanyCost;
    }

    public void setDisCompanyCost(Double disCompanyCost) {
        this.disCompanyCost = disCompanyCost;
    }

    public Double getCommissionFees() {
        return commissionFees;
    }

    public void setCommissionFees(Double commissionFees) {
        this.commissionFees = commissionFees;
    }

    public Double getOceanFreight() {
        return oceanFreight;
    }

    public void setOceanFreight(Double oceanFreight) {
        this.oceanFreight = oceanFreight;
    }

    public Double getDeliveryTakingFee() {
        return deliveryTakingFee;
    }

    public void setDeliveryTakingFee(Double deliveryTakingFee) {
        this.deliveryTakingFee = deliveryTakingFee;
    }

    public Double getCustomsInspectionFee() {
        return customsInspectionFee;
    }

    public void setCustomsInspectionFee(Double customsInspectionFee) {
        this.customsInspectionFee = customsInspectionFee;
    }

    public Double getStockFee() {
        return stockFee;
    }

    public void setStockFee(Double stockFee) {
        this.stockFee = stockFee;
    }

    public Double getLogisticFee() {
        return logisticFee;
    }

    public void setLogisticFee(Double logisticFee) {
        this.logisticFee = logisticFee;
    }

    public Double getLableMakingFee() {
        return lableMakingFee;
    }

    public void setLableMakingFee(Double lableMakingFee) {
        this.lableMakingFee = lableMakingFee;
    }

    public Double getOtherIncidentals() {
        return otherIncidentals;
    }

    public void setOtherIncidentals(Double otherIncidentals) {
        this.otherIncidentals = otherIncidentals;
    }

    public Double getPostalFeeRate() {
        return postalFeeRate;
    }

    public void setPostalFeeRate(Double postalFeeRate) {
        this.postalFeeRate = postalFeeRate;
    }

    public Double getImportTarRate() {
        return importTarRate;
    }

    public void setImportTarRate(Double importTarRate) {
        this.importTarRate = importTarRate;
    }

    public Double getGstRate() {
        return gstRate;
    }

    public void setGstRate(Double gstRate) {
        this.gstRate = gstRate;
    }

    public Double getVatRate() {
        return vatRate;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }

    public Double getMarketInterventionPrice() {
        return marketInterventionPrice;
    }

    public void setMarketInterventionPrice(Double marketInterventionPrice) {
        this.marketInterventionPrice = marketInterventionPrice;
    }

    public Double getFtzPrice() {
        return ftzPrice;
    }

    public void setFtzPrice(Double ftzPrice) {
        this.ftzPrice = ftzPrice;
    }

    public Double getDestinationIncidentals() {
        return destinationIncidentals;
    }

    public void setDestinationIncidentals(Double destinationIncidentals) {
        this.destinationIncidentals = destinationIncidentals;
    }

    public Double getCostomsDeclarenceCharges() {
        return costomsDeclarenceCharges;
    }

    public void setCostomsDeclarenceCharges(Double costomsDeclarenceCharges) {
        this.costomsDeclarenceCharges = costomsDeclarenceCharges;
    }

    public Double getCostomsCensoringCharges() {
        return costomsCensoringCharges;
    }

    public void setCostomsCensoringCharges(Double costomsCensoringCharges) {
        this.costomsCensoringCharges = costomsCensoringCharges;
    }

    public Double getCustomsInspectionCharges() {
        return customsInspectionCharges;
    }

    public void setCustomsInspectionCharges(Double customsInspectionCharges) {
        this.customsInspectionCharges = customsInspectionCharges;
    }

    public Double getLabelRecordCharges() {
        return labelRecordCharges;
    }

    public void setLabelRecordCharges(Double labelRecordCharges) {
        this.labelRecordCharges = labelRecordCharges;
    }

    public Double getInspectionAndQuarantineCharges() {
        return inspectionAndQuarantineCharges;
    }

    public void setInspectionAndQuarantineCharges(Double inspectionAndQuarantineCharges) {
        this.inspectionAndQuarantineCharges = inspectionAndQuarantineCharges;
    }

    public Double getLabelServiceCharges() {
        return labelServiceCharges;
    }

    public void setLabelServiceCharges(Double labelServiceCharges) {
        this.labelServiceCharges = labelServiceCharges;
    }

    public Double getImportAgentServiceCharges() {
        return importAgentServiceCharges;
    }

    public void setImportAgentServiceCharges(Double importAgentServiceCharges) {
        this.importAgentServiceCharges = importAgentServiceCharges;
    }

    public Double getOtherReimbursedExpenses() {
        return otherReimbursedExpenses;
    }

    public void setOtherReimbursedExpenses(Double otherReimbursedExpenses) {
        this.otherReimbursedExpenses = otherReimbursedExpenses;
    }

    public Double getLabelingCharge() {
        return labelingCharge;
    }

    public void setLabelingCharge(Double labelingCharge) {
        this.labelingCharge = labelingCharge;
    }

    public Double getMinimumProfitRate() {
        return minimumProfitRate;
    }

    public void setMinimumProfitRate(Double minimumProfitRate) {
        this.minimumProfitRate = minimumProfitRate;
    }

    public Double getOtherFinanceIncidentals() {
        return otherFinanceIncidentals;
    }

    public void setOtherFinanceIncidentals(Double otherFinanceIncidentals) {
        this.otherFinanceIncidentals = otherFinanceIncidentals;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}