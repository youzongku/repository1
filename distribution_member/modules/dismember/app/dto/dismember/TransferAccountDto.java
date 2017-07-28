package dto.dismember;

import java.io.Serializable;

public class TransferAccountDto implements Serializable {
	

	private static final long serialVersionUID = 2903710879769371422L;

	private Integer id;
	
	private String email;
	
	private String bankName;
	
	private String payerName;
	
	private String card;
	
	private Integer customStatus; //自定义状态 (1为自定义 0非自定义)

	public Integer getCustomStatus() {
		return customStatus;
	}

	public void setCustomStatus(Integer customStatus) {
		this.customStatus = customStatus;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}
	
}
