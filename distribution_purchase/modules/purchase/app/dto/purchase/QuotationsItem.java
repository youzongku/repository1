package dto.purchase;

import java.util.List;

import entity.purchase.DisQuotation;

/**
 * 报价单记录列表
 * Created by luwj on 2016/3/4.
 */
public class QuotationsItem {

    private ReturnMess returnMess;

    private List<DisQuotation> quos;

    private int totalCount;

    public ReturnMess getReturnMess() {
        return returnMess;
    }

    public void setReturnMess(ReturnMess returnMess) {
        this.returnMess = returnMess;
    }

    public List<DisQuotation> getQuos() {
        return quos;
    }

    public void setQuos(List<DisQuotation> quos) {
        this.quos = quos;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
