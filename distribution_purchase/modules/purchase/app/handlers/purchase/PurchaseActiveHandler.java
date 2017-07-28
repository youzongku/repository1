package handlers.purchase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import dto.purchase.PurchaseOrderDto;
import entity.purchase.PurchaseOrder;
import entity.purchase.PurchaseOrderDetail;
import services.purchase.IPurchaseOrderService;

public class PurchaseActiveHandler {

	@Inject
	IPurchaseOrderService iPurchaseOrderService;
	
	private static DecimalFormat f = new DecimalFormat("###0.00");
	static {
		f.setRoundingMode(RoundingMode.HALF_UP);
	}

	/**
	 * 校验是否参加活动
	 * @param event
	 */
	@Subscribe
	public void execute(PurchaseOrder event) {
		iPurchaseOrderService.markPro(event);
	}
	
	/**
	 * 计算均摊价格
	 * @author zbc
	 * @since 2016年11月14日 下午6:25:40
	 */
	@Subscribe
	public void execute(PurchaseOrderDto event) {
		try {
			if (event != null) {
				List<PurchaseOrderDetail> details = iPurchaseOrderService.caculateCapFee(event);
				iPurchaseOrderService.changeInventoryCafee(event,details);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
