package dto.marketing.promotion.pvlg.value;

import java.util.ArrayList;
import java.util.List;

/**
 * 赠品，可以选择多个
 * 
 * @author huangjc
 * @date 2016年7月29日
 */
public class Donation extends BasePvlgValue {
	// {donations:[{cTitle:"可比克零食",sku:"IM27","wareHouseId":2024,warehouseName:xx,imgUrl:xx,"num":1}],"num":1}
	private List<SingleDonation> donations = new ArrayList<SingleDonation>();
	
	// 跟SingleDonation.num一致，表示 num件/箱
	private int num;// 如果unit为件，则此为件数；如果为箱，则为箱数
	// 箱规
	private String unit;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<SingleDonation> getDonations() {
		return donations;
	}

	public void setDonations(List<SingleDonation> donations) {
		this.donations = donations;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "Donation [donations=" + donations + ", num=" + num + ", unit="
				+ unit + "]";
	}

	// 每一个赠品
	public static class SingleDonation {
		private String cTitle;
		private String sku;
		private Integer warehouseId;
		private String warehouseName;
		private Double realPrice;
		private String imgUrl;
		private Double marketPrice;
		private Integer categoryId;
		private String categoryName;
		private Double clearancePrice;

		public Double getClearancePrice() {
			return clearancePrice;
		}

		public void setClearancePrice(Double clearancePrice) {
			this.clearancePrice = clearancePrice;
		}

		public Integer getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(Integer categoryId) {
			this.categoryId = categoryId;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public Double getRealPrice() {
			return realPrice;
		}

		public void setRealPrice(Double realPrice) {
			this.realPrice = realPrice;
		}

		// 跟Donation.num一致，表示 num件/箱
		private int num;// 如果unit为件，则此为件数；如果为箱，则为箱数
		
		// 箱规
		private String unit;// 件/箱
		// 每一箱的件数
		private int unitNum = 0;// 如果unit为件，则此为0

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public int getUnitNum() {
			return unitNum;
		}

		public void setUnitNum(int unitNum) {
			this.unitNum = unitNum;
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}

		public String getWarehouseName() {
			return warehouseName;
		}

		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public String getcTitle() {
			return cTitle;
		}

		public void setcTitle(String cTitle) {
			this.cTitle = cTitle;
		}

		public String getSku() {
			return sku;
		}

		public void setSku(String sku) {
			this.sku = sku;
		}

		public Integer getWarehouseId() {
			return warehouseId;
		}

		public void setWarehouseId(Integer warehouseId) {
			this.warehouseId = warehouseId;
		}
		
		public Double getMarketPrice() {
			return marketPrice;
		}

		public void setMarketPrice(Double marketPrice) {
			this.marketPrice = marketPrice;
		}

		@Override
		public String toString() {
			return "SingleDonation [cTitle=" + cTitle + ", sku=" + sku
					+ ", warehouseId=" + warehouseId + ", warehouseName="
					+ warehouseName + ", imgUrl=" + imgUrl + ", num=" + num
					+ "]";
		}

	}

}
