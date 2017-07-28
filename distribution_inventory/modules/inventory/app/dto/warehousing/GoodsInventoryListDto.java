package dto.warehousing;

import entity.warehousing.GoodsInventory;

/**
 * 真实商品库存列表数据dto
 * 
 * @author ouyangyaxiong
 * @date 2016年3月8日
 */
public class GoodsInventoryListDto extends GoodsInventory {

	private static final long serialVersionUID = 1L;

	private int mTotalStock;// 该商品在微仓总库存
	private int cTotalStock;// 云仓总库存
	private String cProductTitle;
	private String cCategoryName;// 大类名
	private int iCategoryId; // 大类id

	public int getmTotalStock() {
		return mTotalStock;
	}

	public void setmTotalStock(int mTotalStock) {
		this.mTotalStock = mTotalStock;
	}

	public int getcTotalStock() {
		return this.getTotalStock() - this.getmTotalStock();
	}

	public void setcTotalStock(int cTotalStock) {
		this.cTotalStock = cTotalStock;
	}

	public String getcProductTitle() {
		return cProductTitle;
	}

	public void setcProductTitle(String cProductTitle) {
		this.cProductTitle = cProductTitle;
	}

	public String getcCategoryName() {
		return cCategoryName;
	}

	public void setcCategoryName(String cCategoryName) {
		this.cCategoryName = cCategoryName;
	}

	public int getiCategoryId() {
		return iCategoryId;
	}

	public void setiCategoryId(int iCategoryId) {
		this.iCategoryId = iCategoryId;
	}

}
