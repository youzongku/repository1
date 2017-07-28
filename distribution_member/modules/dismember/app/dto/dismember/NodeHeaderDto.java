package dto.dismember;

import java.util.Date;

/**
 * 每个组织节点所对应的直接负责人Dto类
 *
 */
public class NodeHeaderDto {
	
    private Integer organizationId;//组织节点id
	
	private Integer headerId;//负责人id
	
	private String organizationName;//角色名称 
	
	private String headerName;//负责人姓名
	
	private Integer parentId;//组织节点对应的父id
	
	private String headerAccount;//负责人后台账户;
	
	private String headerTel;//负责人手机号码
	
	private Date createDate;//负责人创建时间
	
	private Date updateDate;//负责人更新时间

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Integer getHeaderId() {
		return headerId;
	}

	public void setHeaderId(Integer headerId) {
		this.headerId = headerId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getHeaderAccount() {
		return headerAccount;
	}

	public void setHeaderAccount(String headerAccount) {
		this.headerAccount = headerAccount;
	}

	public String getHeaderTel() {
		return headerTel;
	}

	public void setHeaderTel(String headerTel) {
		this.headerTel = headerTel;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
