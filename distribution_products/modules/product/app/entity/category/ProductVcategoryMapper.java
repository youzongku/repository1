package entity.category;

import java.util.Date;

public class ProductVcategoryMapper {
    private Integer id;

    private String sku;

    private Integer categoryid;

    private Date createdate;

    private Integer listingid;

    private Integer position;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Integer categoryid) {
        this.categoryid = categoryid;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Integer getListingid() {
        return listingid;
    }

    public void setListingid(Integer listingid) {
        this.listingid = listingid;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}