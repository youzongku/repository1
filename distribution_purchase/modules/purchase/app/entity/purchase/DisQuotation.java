package entity.purchase;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * 分销商报价单记录实体
 */
public class DisQuotation implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String discountRate;//利润率折扣

    private String excelName;//导出Excel名称

    private String excelInfo;//Excel内容

    private String disEmail;//分销商ID

    private String disname;//分销商名称

    private Date bindDisEmail;//绑定分销商时间

    private Date createDate;//创建时间

    @SuppressWarnings("unused")
	private String createDateStr;

    private String madeUser;//制作人

    private Boolean isBuildOrder;//是否生成订单

    private Date updateDate;//更新时间

    @SuppressWarnings("unused")
	private String updateDateStr;

    private String reqBody;//导出参数

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(String discountRate) {
        this.discountRate = discountRate;
    }

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    public String getExcelInfo() {
        return excelInfo;
    }

    public void setExcelInfo(String excelInfo) {
        this.excelInfo = excelInfo;
    }

    public String getDisEmail() {
        return disEmail;
    }

    public void setDisEmail(String disEmail) {
        this.disEmail = disEmail;
    }

    public String getDisname() {
        return disname;
    }

    public void setDisname(String disname) {
        this.disname = disname;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateDateStr() {
        return createDate != null ? new DateTime(createDate).toString("yyyy-MM-dd") : "";
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getMadeUser() {
        return madeUser;
    }

    public void setMadeUser(String madeUser) {
        this.madeUser = madeUser;
    }

    public Boolean getIsBuildOrder() {
        return isBuildOrder;
    }

    public void setIsBuildOrder(Boolean isBuildOrder) {
        this.isBuildOrder = isBuildOrder;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateDateStr() {
        return updateDate != null ? new DateTime(updateDate).toString("yyyy-MM-dd") : "";
    }

    public void setUpdateDateStr(String updateDateStr) {
        this.updateDateStr = updateDateStr;
    }

    public String getReqBody() {
        return reqBody;
    }

    public void setReqBody(String reqBody) {
        this.reqBody = reqBody;
    }

    public Date getBindDisEmail() {
        return bindDisEmail;
    }

    public void setBindDisEmail(Date bindDisEmail) {
        this.bindDisEmail = bindDisEmail;
    }
}