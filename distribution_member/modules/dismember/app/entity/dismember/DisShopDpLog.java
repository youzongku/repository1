package entity.dismember;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import utils.dismember.DateUtils;

/**
 * 设置店铺扣点日志
 * 
 * @author Administrator
 *
 */
public class DisShopDpLog {
	private Integer id;
	private Integer shopId;
	private String shopName;
	private String email;
	private Double deductionPoints;// 店铺扣点，范围：0~1
	private String createUser;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

	public DisShopDpLog(Integer shopId, String shopName, String email, 
			Double deductionPoints, String createUser, Date createTime) {
		this.shopId = shopId;
		this.shopName = shopName;
		this.email = email;
		this.deductionPoints = deductionPoints;
		this.createUser = createUser;
		this.createTime = createTime;
	}

	public DisShopDpLog() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getDeductionPoints() {
		return deductionPoints;
	}

	public void setDeductionPoints(Double deductionPoints) {
		this.deductionPoints = deductionPoints;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getCreateTimeStr() {
		if (createTime != null) {
			DateUtils.date2FullDateTimeString(createTime);
		}
		return null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "DisShopDpLog [id=" + id + ", shopId=" + shopId + ", shopName=" + shopName + ", email=" + email
				+ ", deductionPoints=" + deductionPoints + ", createUser=" + createUser + ", createTime=" + createTime
				+ "]";
	}

}
