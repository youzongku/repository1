package dto.purchase;

/**
 * 采购单查询参数实体
 * Created by luwj on 2015/12/29.
 */
public class ViewParam {

    private int pageSize;//每页条目

    private int pageCount;//当前页

    private int pageNow;//起始条目

//    private String email;//邮箱

    private String orderDate;//下单时间

    private String status;//状态

    private String seachFlag;//搜索条件：订单编号、分销商等

    private String sorderDate;//后台下单时间区间--起始时间

    private String eorderDate;//后台下单时间区间--结束时间

    private String spaydate;//后台支付时间区间--起始时间

    private String epaydate;//后台支付时间区间--结束时间

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageNow() {
        return pageNow;
    }

    public void setPageNow(int pageNow) {
        this.pageNow = pageNow;
    }

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeachFlag() {
        return seachFlag;
    }

    public void setSeachFlag(String seachFlag) {
        this.seachFlag = seachFlag;
    }

    public String getSorderDate() {
        return sorderDate;
    }

    public void setSorderDate(String sorderDate) {
        this.sorderDate = sorderDate;
    }

    public String getEorderDate() {
        return eorderDate;
    }

    public void setEorderDate(String eorderDate) {
        this.eorderDate = eorderDate;
    }

    public String getSpaydate() {
        return spaydate;
    }

    public void setSpaydate(String spaydate) {
        this.spaydate = spaydate;
    }

    public String getEpaydate() {
        return epaydate;
    }

    public void setEpaydate(String epaydate) {
        this.epaydate = epaydate;
    }
}
