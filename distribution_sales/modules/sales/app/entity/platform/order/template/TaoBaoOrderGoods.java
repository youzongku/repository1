package entity.platform.order.template;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import play.Logger;

@SuppressWarnings("serial")
public class TaoBaoOrderGoods implements Serializable {

	private Integer id;
	private String orderNo;// 订单编号
	private String goodsTitle;// 商品标题
	private Integer amount;// 数量
	private String sku;// 商品编码
	private String sku1;// (有赞里优先选择此sku)
	private Double price;// 价格
	private String warehouseId;// 仓库编号
	private String warehouseName;// 仓库名称
	public Integer isDeleted;// 逻辑删除（0 未删除 1 删除）
	public String email;// 订单所对应的email

	// 增加属性,导入进来的产品没有指明仓库，所以以下几个字段通过查询另外的接口添加
	private List<Map<String, Object>> warehouseNameId;
	private String ctitle;// sku所对应的标题名(正规)
	private String imageUrl;// sku所对应的图片路径
	private String batchNumber;// 起批量

	public TaoBaoOrderGoods() {
		super();
	}

	public TaoBaoOrderGoods(Integer isDeleted) {
		super();
		this.isDeleted = isDeleted;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = StringUtils.isBlank(orderNo) ? null : orderNo.replace(
				".00", "");
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public void setAmountStr(String amountStr) {
		if (amountStr != null && !"".equals(amountStr.trim())) {
			if (amountStr.indexOf(".") == -1) {
				this.amount = Integer.valueOf(amountStr);
			} else {
				this.amount = Integer.valueOf(amountStr.substring(0,
						amountStr.indexOf(".")));
			}
		}
	}

	public String getSku() {
		if (!Strings.isNullOrEmpty(this.sku1)) {
			return this.sku1.trim();
		} else if (!Strings.isNullOrEmpty(sku)) {
			return sku.trim();
		} else {
			return sku;
		}
	}

	public void setSku(String sku) {
		if (!Strings.isNullOrEmpty(this.sku1)) {
			this.sku = this.sku1.trim();
			return;
		}
		if (!Strings.isNullOrEmpty(sku)) {
			this.sku = sku.trim();
		}
	}

	public String getSku1() {
		return sku1;
	}

	public void setSku1(String sku1) {
		if (!Strings.isNullOrEmpty(sku1)) {
			this.sku1 = sku1.trim();
		}
	}

	public Double getPrice() {
		return price;
	}

	public void setPriceStr(String priceStr) {
		if (priceStr != null && !"".equals(priceStr.trim())) {
			this.price = Double.valueOf(priceStr);
		}
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public List<Map<String, Object>> getWarehouseNameId() {
		return warehouseNameId;
	}

	public void setWarehouseNameId(List<Map<String, Object>> warehouseNameId) {
		this.warehouseNameId = warehouseNameId;
	}

	public String getCtitle() {
		return ctitle;
	}

	public void setCtitle(String ctitle) {
		this.ctitle = ctitle;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * 描述：通过属性与值得映射map设置商品属性值 2016年5月16日
	 * 
	 * @param fieldValueMap
	 *            属性与值的映射map
	 */
	public void parseOrderGoodsDataFromFieldAndValueMap(
			Map<String, String> fieldValueMap) {
		if (!fieldValueMap.isEmpty()) {
			Set<String> fieldSet = fieldValueMap.keySet();
			for (String field : fieldSet) {
				if (!Strings.isNullOrEmpty(field)
						&& !field.equals("notRegister")) {
					String methodName = "set"
							+ field.substring(0, 1).toUpperCase()
							+ field.substring(1);
					Method setFieldMethod = null;
					try {
						setFieldMethod = super.getClass().getMethod(
								methodName + "Str", String.class);
					} catch (NoSuchMethodException e) {
					} catch (SecurityException e) {
					} catch (Exception e) {
					}
					try {
						if (setFieldMethod == null) {
							setFieldMethod = super.getClass().getMethod(
									methodName, String.class);
						}
						if (setFieldMethod != null) {
							setFieldMethod.invoke(this,
									fieldValueMap.get(field));
						} else {
							Logger.info("获取属性[" + field + "]的set方法失败");
						}
					} catch (Exception e) {
						Logger.error("设置商品属性[" + field + "]的值失败！");
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "TaoBaoOrderGoods [id=" + id + ", orderNo=" + orderNo
				+ ", goodsTitle=" + goodsTitle + ", amount=" + amount
				+ ", sku=" + sku + ", sku1=" + sku1 + ", price=" + price
				+ ", warehouseId=" + warehouseId + ", warehouseName="
				+ warehouseName + ", isDeleted=" + isDeleted + ", email="
				+ email + ", warehouseNameId=" + warehouseNameId + ", ctitle="
				+ ctitle + ", imageUrl=" + imageUrl + ", batchNumber="
				+ batchNumber + "]";
	}

}
