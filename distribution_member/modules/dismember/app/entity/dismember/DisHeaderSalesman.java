package entity.dismember;
/**
 * 业务人员与负责人映射实体类
 * @author Lzl
 *
 */
public class DisHeaderSalesman {
    private Integer id;

    private Integer headerid;

    private Integer salesmanid;

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

    public Integer getSalesmanid() {
        return salesmanid;
    }

    public void setSalesmanid(Integer salesmanid) {
        this.salesmanid = salesmanid;
    }
}