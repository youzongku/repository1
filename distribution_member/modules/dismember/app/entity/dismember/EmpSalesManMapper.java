package entity.dismember;

import java.io.Serializable;

/**
 * 员工与业务员映射设实体
 * @author zbc
 * 2016年11月25日 下午2:44:39
 */
public class EmpSalesManMapper implements Serializable {
	private static final long serialVersionUID = -7448021841041015975L;

	private Integer id;

    private Integer empId;

    private Integer salesmanId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public Integer getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(Integer salesmanId) {
        this.salesmanId = salesmanId;
    }
}