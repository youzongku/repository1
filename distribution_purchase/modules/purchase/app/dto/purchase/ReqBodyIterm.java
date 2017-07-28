package dto.purchase;

import java.util.List;
import java.util.Map;

/**
 * Created by luwj on 2016/3/8.
 */
public class ReqBodyIterm {

    private String[] header;

    private String[] skuList;

    private String discountRate;

    private String excelName;

    private String madeUser;

    private int id;

    private String disEmail;

    private String remark;

    private String[] iidList;

    /**
     * [
     *      {"sku": "", "warehouseId": ""},
     *      {"sku": "", "warehouseId": ""},...,
     *      {"sku": "", "warehouseId": ""}
     * ]
     */
    private List<Map<String, String>> skuawhid;

    public String[] getHeader() {
        return header;
    }

    public void setHeader(String[] header) {
        this.header = header;
    }

    public String[] getSkuList() {
        return skuList;
    }

    public void setSkuList(String[] skuList) {
        this.skuList = skuList;
    }

    public String getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(String discountRate) {
        this.discountRate = discountRate;
    }

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    public String getMadeUser() {
        return madeUser;
    }

    public void setMadeUser(String madeUser) {
        this.madeUser = madeUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisEmail() {
        return disEmail;
    }

    public void setDisEmail(String disEmail) {
        this.disEmail = disEmail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String[] getIidList() {
        return iidList;
    }

    public void setIidList(String[] iidList) {
        this.iidList = iidList;
    }

    public List<Map<String, String>> getSkuawhid() {
        return skuawhid;
    }

    public void setSkuawhid(List<Map<String, String>> skuawhid) {
        this.skuawhid = skuawhid;
    }
}
