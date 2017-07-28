package dto.sales;

/**
 * @author longhuashen
 * @since 2017/3/20
 */
public class ErpStatusDto {

    /**
     * 单号
     */
    private String orderNo;

    /**
     * 状态
     */
    private Integer status;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ErpStatusDto{" +
                "orderNo='" + orderNo + '\'' +
                ", status=" + status +
                '}';
    }
}
