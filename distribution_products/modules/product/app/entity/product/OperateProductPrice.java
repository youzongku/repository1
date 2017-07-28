package entity.product;

import java.util.Date;
import java.util.List;
import java.util.Map;

import util.product.DateUtils;

/**
 * @author zbc
 *
 * 2016年7月28日 上午11:30:42
 */
public class OperateProductPrice {
    private Integer id;
    
    private String sku;//商品编码

    private String productTitle;//商品名称

    private String categoryName;//类目名称

    private Integer categoryId;//类目id

    private Date operatorTime;//操作时间
    
    private String operator;//操作人
    
    private Double changePrice;//更改金额
    
    private String operateDesc;//操作描述
    
    private Integer warehouseId;//仓库id 
    
    private String warehouseName;//仓库名称
    
    private Integer priceIid;//商品价格表 id
    
    private Map<String,Double> changeMap;//用于接收修改金额类型，与对应金额 
    
    private String operatorTimeStr;
    
    private List<Integer> priceIidList;//商品id集合
    
    private Map<String,Double> changeFactorMap;//系数设置map
    
    private String fieldName;//字段名称
    
    private String setType;//设置类型: 系数设置FR,利润设置PF

	private String remark; //备注

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public List<Integer> getPriceIidList() {
		return priceIidList;
	}

	public void setPriceIidList(List<Integer> priceIidList) {
		this.priceIidList = priceIidList;
	}

	public Map<String, Double> getChangeFactorMap() {
		return changeFactorMap;
	}

	public void setChangeFactorMap(Map<String, Double> changeFactorMap) {
		this.changeFactorMap = changeFactorMap;
	}

	public String getOperatorTimeStr() {
		return operatorTimeStr;
	}

	public String getOperateTimeStr() {
		return DateUtils.date2string(operatorTime, DateUtils.FORMAT_FULL_DATETIME);
	}

	public Map<String, Double> getChangeMap() {
		return changeMap;
	}

	public void setChangeMap(Map<String, Double> changeMap) {
		this.changeMap = changeMap;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Double getChangePrice() {
		return changePrice;
	}

	public void setChangePrice(Double changePrice) {
		this.changePrice = changePrice;
	}

	public Integer getPriceIid() {
		return priceIid;
	}

	public void setPriceIid(Integer priceIid) {
		this.priceIid = priceIid;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

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

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Date getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Date operatorTime) {
        this.operatorTime = operatorTime;
    }

    public String getOperateDesc() {
        return operateDesc;
    }

    public void setOperateDesc(String operateDesc) {
        this.operateDesc = operateDesc;
    }


	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "OperateProductPrice{" +
				"id=" + id +
				", sku='" + sku + '\'' +
				", productTitle='" + productTitle + '\'' +
				", categoryName='" + categoryName + '\'' +
				", categoryId=" + categoryId +
				", operatorTime=" + operatorTime +
				", operator='" + operator + '\'' +
				", changePrice=" + changePrice +
				", operateDesc='" + operateDesc + '\'' +
				", warehouseId=" + warehouseId +
				", warehouseName='" + warehouseName + '\'' +
				", priceIid=" + priceIid +
				", changeMap=" + changeMap +
				", operatorTimeStr='" + operatorTimeStr + '\'' +
				", priceIidList=" + priceIidList +
				", changeFactorMap=" + changeFactorMap +
				", fieldName='" + fieldName + '\'' +
				", setType='" + setType + '\'' +
				", remark='" + remark + '\'' +
				'}';
	}
}