package mapper.purchase;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.purchase.PurchaseOrderInputGift;

public interface PurchaseOrderInputGiftMapper {
	int insert(PurchaseOrderInputGift record);

	int deleteByIdList(List<Integer> idList);

	int deleteByInputId(@Param("inputId") int inputId);

	int insertSelective(PurchaseOrderInputGift record);

	int insertBatch(List<PurchaseOrderInputGift> records);

	List<PurchaseOrderInputGift> selectByInputId(Integer inputId);

	List<PurchaseOrderInputGift> selectByIdList(List<Integer> idList);

	@Deprecated
	List<PurchaseOrderInputGift> selectByProIds(String proIds);

	PurchaseOrderInputGift selectByPrimaryKey(Integer id);

	int updateGiftQty(@Param("id") int id, @Param("qty") int qty);

	int batchUpdateGiftQtyAndNeedExpirationDate(List<PurchaseOrderInputGift> giftList);

	int batchUpdateWarehouse(List<PurchaseOrderInputGift> list);

	int updateByPrimaryKeySelective(PurchaseOrderInputGift gift);
}