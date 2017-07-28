package dto.marketing.promotion;

import java.util.List;

import entity.marketing.promotion.PrivilegeInstance;

public class OrderPageReturnsDto {
	private Integer id;
	private String name;
	private List<PrivilegeInstance> prvlInst;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PrivilegeInstance> getPrvlInst() {
		return prvlInst;
	}

	public void setPrvlInst(List<PrivilegeInstance> prvlInst) {
		this.prvlInst = prvlInst;
	}

}
