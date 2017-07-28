package forms.product;

import play.data.validation.Constraints;

import java.util.List;

/**
 * @author ye_ziran
 * @since 2016/3/23 15:40
 */
public class ProductSearchParamForm {

    @Constraints.Required
    private String memberEmail;
    private Integer pageSize;
    private Integer currPage;
    private Integer istatus;
    private String sku;
    private Integer warehouseId;

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrPage() {
        return currPage;
    }

    public void setCurrPage(Integer currPage) {
        this.currPage = currPage;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String skuList) {
        this.sku = skuList;
    }
}
