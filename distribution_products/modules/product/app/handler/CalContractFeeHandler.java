package handler;

import java.io.IOException;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import dto.contract.fee.ContractFeeItemDto;
import event.CalContractFeeEvent;
import play.Logger;
import services.product.IContractFeeItemMgrService;
/**
 * 计算合同费用
 * @author huangjc
 */
import services.product.IHttpService;
public class CalContractFeeHandler {
	
	@Inject
	private IHttpService httpService;
	@Inject
	private IContractFeeItemMgrService contractFeeItemMgrService;
	
	
	@Subscribe
	public void calContractFee(CalContractFeeEvent dto) {
		//TODO 修改更新逻辑，不做全量刷新，只做局部刷新，即某个费用项更新时，做更新操作
		ContractFeeItemDto feeItem = contractFeeItemMgrService.getContractFeeItemDto(dto.getFeeItemId());
		try {
			httpService.notifyCalContractFee(feeItem);
		} catch (IOException e) {
			Logger.info("异步更新合同费用异常:{}",e);
		}
	}

}
