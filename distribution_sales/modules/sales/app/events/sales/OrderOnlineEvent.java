package events.sales;

import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;

import java.util.List;

/**
 * @author longhuashen
 * @since 2017/5/17
 */
public class OrderOnlineEvent {

    private SaleBase saleBase;

    private SaleMain saleMain;

    private List<SaleDetail> saleDetails;

    public OrderOnlineEvent(SaleBase saleBase, SaleMain saleMain, List<SaleDetail> saleDetails) {
        this.saleBase = saleBase;
        this.saleMain = saleMain;
        this.saleDetails = saleDetails;
    }

    public SaleBase getSaleBase() {
        return saleBase;
    }

    public void setSaleBase(SaleBase saleBase) {
        this.saleBase = saleBase;
    }

    public SaleMain getSaleMain() {
        return saleMain;
    }

    public void setSaleMain(SaleMain saleMain) {
        this.saleMain = saleMain;
    }

    public List<SaleDetail> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetail> saleDetails) {
        this.saleDetails = saleDetails;
    }
}
