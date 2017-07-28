package dto.sales;

/**
 * @author longhuashen
 * @since 2017/5/26
 */
public class SaleAuditDto {

    //订单id
    private Integer id;

    //审核结果
    private String result;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "SaleAuditDto{" +
                "id=" + id +
                ", result='" + result + '\'' +
                '}';
    }
}
