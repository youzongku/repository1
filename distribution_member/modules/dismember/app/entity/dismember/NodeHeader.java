package entity.dismember;
/**
 * 每个组织节点所对应的直接负责人	
 * @author Administrator
 *
 */
public class NodeHeader {
    private Integer id;

    private Integer headerid;

    private Integer organizationid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHeaderid() {
        return headerid;
    }

    public void setHeaderid(Integer headerid) {
        this.headerid = headerid;
    }

    public Integer getOrganizationid() {
        return organizationid;
    }

    public void setOrganizationid(Integer organizationid) {
        this.organizationid = organizationid;
    }
}