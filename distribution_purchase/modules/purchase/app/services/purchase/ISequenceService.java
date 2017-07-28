package services.purchase;

/**
 * Created by luwj on 2015/12/11.
 */
public interface ISequenceService {
    /**
     * 获取标识下的当前值
     * @param seqName
     * @return
     */
    public int selectNextval(String seqName);

    /**
     * 获取采购订单号
     * @return
     */
    public String getPurchaseNo(String flag);
    
    /**
     * 获取退货单号
     * @return
     */
    public String getReturnOrderNo();
    
    /**
     * 描述:生成采购商品批次号
     * @return
     */
    public String getBatchNo();
}
