package mapper.purchase;

import java.util.List;

import entity.purchase.PurchaseRecord;

public interface PurchaseRecordMapper extends BaseMapper<PurchaseRecord> {
    int deleteByPrimaryKey(Integer id);

    int insert(PurchaseRecord record);

    int insertSelective(PurchaseRecord record);

    List<PurchaseRecord> getRecords(Integer orderId);

    int updateByPrimaryKeySelective(PurchaseRecord record);

    int updateByPrimaryKey(PurchaseRecord record);
}