package dto.marketing.promotion.condt.value;

/**
 * 指定商品属性或指定购物车属性的值
 * 
 * @author huangjc
 * @date 2016年10月17日
 */
public class SpecifyAttrValue {
	//
	// attrType:1/2(数量或金额),
	// jType:gt/gteq/ ltv,
	// singleVal:12,
	// ltvVal:{
	// minJType:gt/gteq,
	// minValue:1,
	// maxJType:lt/lteq,
	// maxValue:3
	// }

	private Integer attrType = 1;// 1数量/2金额
	private String jType; // gt/gteq/ltv
	// 当jType是gt/gteq时，singleVal有值
	private Double singleVal = 0.0;
	// 当jType字段是ltv时，只有val有值
	private LtvVal ltvVal;

	/**
	 * 区间的值
	 */
	public static class LtvVal {
		private String minJType; // gt/gteq
		private Double minValue;
		private String maxJType; // lt/lteq
		private Double maxValue;

		public String getMinJType() {
			return minJType;
		}

		public void setMinJType(String minJType) {
			this.minJType = minJType;
		}

		public Double getMinValue() {
			return minValue;
		}

		public void setMinValue(Double minValue) {
			this.minValue = minValue;
		}

		public String getMaxJType() {
			return maxJType;
		}

		public void setMaxJType(String maxJType) {
			this.maxJType = maxJType;
		}

		public Double getMaxValue() {
			return maxValue;
		}

		public void setMaxValue(Double maxValue) {
			this.maxValue = maxValue;
		}

		@Override
		public String toString() {
			return "Val [minJType=" + minJType + ", minValue=" + minValue
					+ ", maxJType=" + maxJType + ", maxValue=" + maxValue + "]";
		}
	}

	public Double getSingleVal() {
		return singleVal;
	}

	public void setSingleVal(Double singleVal) {
		this.singleVal = singleVal;
	}

	public Integer getAttrType() {
		return attrType;
	}

	public void setAttrType(Integer attrType) {
		this.attrType = attrType;
	}

	public String getjType() {
		return jType;
	}

	public void setjType(String jType) {
		this.jType = jType;
	}

	public LtvVal getLtvVal() {
		return ltvVal;
	}

	public void setLtvVal(LtvVal ltvVal) {
		this.ltvVal = ltvVal;
	}

	@Override
	public String toString() {
		return "SpecifyAttrValue [attrType=" + attrType + ", jType=" + jType
				+ ", singleVal=" + singleVal + ", ltvVal=" + ltvVal + "]";
	}

}
