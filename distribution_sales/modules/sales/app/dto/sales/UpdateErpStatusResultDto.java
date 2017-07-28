package dto.sales;

/**
 * @author longhuashen
 * @since 2017/3/20
 */
public class UpdateErpStatusResultDto {

    private String orderNo;

    private boolean flag;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "UpdateErpStatusResultDto{" +
                "orderNo='" + orderNo + '\'' +
                ", flag=" + flag +
                '}';
    }
}
