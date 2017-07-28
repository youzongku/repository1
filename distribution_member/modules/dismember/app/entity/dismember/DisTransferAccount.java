package entity.dismember;

import java.io.Serializable;
import java.util.Date;

/**
 * 分销商付款账号表
 *
 */
public class DisTransferAccount implements Serializable {

	private static final long serialVersionUID = 1L;

    private Integer id;
    
    private String bankName; //银行名称
    
    private String transferCard; //银行账号
    
    private Date createDate; //创建时间
    
    private String distributorEmail; //分销商邮箱
    
    private String payerName;  //付款人名称
    
    private Integer customStatus; //自定义状态 (1为自定义 0非自定义)

	public Integer getId() {
		return id;
	}

	public Integer getCustomStatus() {
		return customStatus;
	}

	public void setCustomStatus(Integer customStatus) {
		this.customStatus = customStatus;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getTransferCard() {
		return transferCard;
	}

	public void setTransferCard(String transferCard) {
		this.transferCard = transferCard;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public String getDistributorEmail() {
		return distributorEmail;
	}

	public void setDistributorEmail(String distributorEmail) {
		this.distributorEmail = distributorEmail;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}  
	
}
