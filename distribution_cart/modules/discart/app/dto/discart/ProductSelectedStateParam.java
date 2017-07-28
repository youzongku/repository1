package dto.discart;

public class ProductSelectedStateParam {
	private boolean allInCart;
	private boolean selected;
	private Integer itemId;
	private String email;
	private int distributionMode;

	public ProductSelectedStateParam() {
	}

	public ProductSelectedStateParam(boolean allInCart, boolean selected) {
		super();
		this.allInCart = allInCart;
		this.selected = selected;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(int distributionMode) {
		this.distributionMode = distributionMode;
	}

	public boolean isAllInCart() {
		return allInCart;
	}

	public void setAllInCart(boolean allInCart) {
		this.allInCart = allInCart;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Override
	public String toString() {
		return "ProductSelectedStateParam [allInCart=" + allInCart + ", selected=" + selected + ", itemId=" + itemId
				+ ", email=" + email + ", distributionMode=" + distributionMode + "]";
	}

}
