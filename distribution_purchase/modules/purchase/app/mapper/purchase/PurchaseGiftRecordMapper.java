package mapper.purchase;

import java.util.List;

import entity.purchase.PurchaseGiftRecord;

public interface PurchaseGiftRecordMapper extends BaseMapper<PurchaseGiftRecord> {

    PurchaseGiftRecord selectByPrimaryKey(Integer id);

	int batchInsert(List<PurchaseGiftRecord> giftrecords);

	List<PurchaseGiftRecord> selectByPurchaseId(Integer purchaseId);
}