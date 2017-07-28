package services.purchase.impl;

import java.util.List;

import com.google.inject.Inject;

import entity.purchase.PurchaseOrderDetail;
import mapper.purchase.PurchaseOrderDetailMapper;
import services.purchase.IPurchaseOrderDetailService;

public class PurchaseOrderDetailService implements IPurchaseOrderDetailService {
	
	@Inject private PurchaseOrderDetailMapper purchaseOrderDetailMapper;

	@Override
	public boolean batchSaveDetails(List<PurchaseOrderDetail> details) {
		return purchaseOrderDetailMapper.batchSaveDetails(details) >0;
	}

	@Override
	public boolean updateDetail(PurchaseOrderDetail purchaseOrderDetail) {
		return purchaseOrderDetailMapper.updateByPrimaryKeySelective(purchaseOrderDetail) > 0;
	}

	@Override
	public List<PurchaseOrderDetail> getPurchaseDetailStockInfo(String purOrderNo) {
		return purchaseOrderDetailMapper.getAlldetailsByPurNo(purOrderNo);
	}

	@Override
	public List<PurchaseOrderDetail> getGift(Integer id) {
		return purchaseOrderDetailMapper.getGift(id);
	}

}
