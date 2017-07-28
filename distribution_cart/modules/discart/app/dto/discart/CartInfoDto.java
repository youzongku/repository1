package dto.discart;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zbc 2017年3月23日 下午2:25:34
 */
public class CartInfoDto implements Serializable {

	private static final long serialVersionUID = 7591930240460498390L;

	private Boolean result;

	private Integer cartId;

	// 购物车里的商品
	private List<DisCartDto> cartData;

	private Integer cartQty = 0;

	private Integer totalQty = 0;

	private Double totalPrice;

	private Map<String, Object> activeInfo;

	private String msg;

	public CartInfoDto() {

	}

	public CartInfoDto(Boolean result, String msg) {
		this.result = result;
		this.msg = msg;
	}

	public CartInfoDto(Boolean result, Integer cartId, List<DisCartDto> cartData, Integer cartQty, Integer totalQty,
			Double totalPrice, Map<String, Object> activeInfo) {
		super();
		this.result = result;
		this.cartId = cartId;
		this.cartData = cartData;
		this.cartQty = cartQty;
		this.totalQty = totalQty;
		this.totalPrice = totalPrice;
		this.activeInfo = activeInfo;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public List<DisCartDto> getCartData() {
		return cartData;
	}

	public void setCartData(List<DisCartDto> cartData) {
		this.cartData = cartData;
	}

	public Integer getCartQty() {
		return cartQty;
	}

	public void setCartQty(Integer cartQty) {
		this.cartQty = cartQty;
	}

	public Integer getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(Integer totalQty) {
		this.totalQty = totalQty;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Map<String, Object> getActiveInfo() {
		return activeInfo;
	}

	public void setActiveInfo(Map<String, Object> activeInfo) {
		this.activeInfo = activeInfo;
	}
}
