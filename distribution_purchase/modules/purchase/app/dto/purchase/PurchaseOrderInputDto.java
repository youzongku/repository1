package dto.purchase;

import java.util.List;

import entity.purchase.PurchaseOrderInputGift;
import entity.purchase.PurchaseOrderInputPro;

/**
 * 手动录入采购单的数据
 * @author huangjc
 * @date 2016年11月30日
 */
public class PurchaseOrderInputDto {
	private Integer inputId;

	private String inputUser;// 录入人

	private String disAccount;// 分销商账号

	private Integer inputType;// 录入类型 1为手动录入 2 为导入

	private Integer disMode;// 分销模式(1.电商 2.经销商3.KA直营4.进口专营)

	List<PurchaseOrderInputPro> proList;
	List<PurchaseOrderInputGift> giftList;

	public Integer getInputId() {
		return inputId;
	}

	public void setInputId(Integer inputId) {
		this.inputId = inputId;
	}

	public String getInputUser() {
		return inputUser;
	}

	public void setInputUser(String inputUser) {
		this.inputUser = inputUser;
	}

	public String getDisAccount() {
		return disAccount;
	}

	public void setDisAccount(String disAccount) {
		this.disAccount = disAccount;
	}

	public Integer getInputType() {
		return inputType;
	}

	public void setInputType(Integer inputType) {
		this.inputType = inputType;
	}

	public Integer getDisMode() {
		return disMode;
	}

	public void setDisMode(Integer disMode) {
		this.disMode = disMode;
	}

	public List<PurchaseOrderInputPro> getProList() {
		return proList;
	}

	public void setProList(List<PurchaseOrderInputPro> proList) {
		this.proList = proList;
	}

	public List<PurchaseOrderInputGift> getGiftList() {
		return giftList;
	}

	public void setGiftList(List<PurchaseOrderInputGift> giftList) {
		this.giftList = giftList;
	}

}
