package dto.purchase.returnod;

import com.fasterxml.jackson.databind.JsonNode;

import entity.purchase.returnod.ReturnAmountCoefficient;

/**
 * 重新封装了商品信息，包含了商品退款系数
 * 
 * @author huangjc
 * @date 2017年2月15日
 */
public class ReturnAmountCoefficientDto {
	// 商品信息，json串
	private JsonNode productInfo;
	// 商品退款系数
	private ReturnAmountCoefficient rac;
	
	public ReturnAmountCoefficientDto(JsonNode productInfo,
			ReturnAmountCoefficient rac) {
		super();
		this.productInfo = productInfo;
		this.rac = rac;
	}

	public ReturnAmountCoefficient getRac() {
		return rac;
	}

	public void setRac(ReturnAmountCoefficient rac) {
		this.rac = rac;
	}

	public JsonNode getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(JsonNode productInfo) {
		this.productInfo = productInfo;
	}

	@Override
	public String toString() {
		return "ReturnAmountCoefficientDto [productInfo=" + productInfo
				+ ", rac=" + rac + "]";
	}

}
