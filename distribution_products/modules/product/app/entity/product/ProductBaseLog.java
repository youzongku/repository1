package entity.product;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import util.product.DateUtils;

public class ProductBaseLog {
	private Integer id;

	private String csku;

	private Integer istatus;

	private Integer salable;

	@JsonIgnore
	private Integer optType;// 操作类型（修改哪个字段）

	private String optUser;

	private Date optDate;

	public ProductBaseLog() {
	}

	public ProductBaseLog(String csku, Integer salable, Integer optType, String optUser) {
		super();
		this.csku = csku;
		this.salable = salable;
		this.optType = optType;
		this.optUser = optUser;
	}

	public Integer getOptType() {
		return optType;
	}

	public void setOptType(Integer optType) {
		this.optType = optType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCsku() {
		return csku;
	}

	public void setCsku(String csku) {
		this.csku = csku;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public Integer getSalable() {
		return salable;
	}

	public void setSalable(Integer salable) {
		this.salable = salable;
	}

	public String getOptUser() {
		return optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	public Date getOptDate() {
		return optDate;
	}
	
	public String getOptDateStr() {
		if (optDate!=null) {
			return DateUtils.date2string(optDate, DateUtils.FORMAT_FULL_DATETIME);
		}
		return "";
	}

	public void setOptDate(Date optDate) {
		this.optDate = optDate;
	}
}