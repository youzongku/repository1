package entity.dismember;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * 业务人员实体类
 * @author Lzl
 *
 */
public class DisSalesman {
    private Integer id;

    private String name;//业务员姓名

    private String account;//业务员关联的后台账户

    private String tel;//业务员手机号码

    private String erp;//业务员的erp账号
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;//创建日期
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateDate;//更新日期
    
    private String workNo;//工号
    
    private Integer memberCount;
    
    private Integer salesManCount;//关联业务员人数
    
    private Integer nodeType;//业务员类型（1：业务员，2：员工）

    public Integer getSalesManCount() {
		return salesManCount;
	}

	public void setSalesManCount(Integer salesManCount) {
		this.salesManCount = salesManCount;
	}

	public Integer getNodeType() {
		return nodeType;
	}

	public void setNodeType(Integer nodeType) {
		this.nodeType = nodeType;
	}

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

	public String getWorkNo() {
		return workNo;
	}

	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}

	public Integer getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}
}