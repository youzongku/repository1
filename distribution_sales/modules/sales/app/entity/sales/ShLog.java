package entity.sales;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ShLog {
    private Integer id;

    private Integer shOrderId;

    private Integer type;

    private Integer isProductReturn;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String operator;

    private Integer result;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShOrderId() {
        return shOrderId;
    }

    public void setShOrderId(Integer shOrderId) {
        this.shOrderId = shOrderId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsProductReturn() {
        return isProductReturn;
    }

    public void setIsProductReturn(Integer isProductReturn) {
        this.isProductReturn = isProductReturn;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}