package services.product;

import java.util.Map;

import dto.contract.fee.ContractFeeItemDto;
import dto.contract.fee.ContractFeeItemPageQeuryParam;
import dto.contract.fee.ContractFeeParam;
import dto.contract.fee.GetContractFeeItemsParam;
import entity.contract.ContractFeeItem;
import util.product.Page;

/**
 * 合同费用项管理
 * 
 * @author Administrator
 *
 */
public interface IContractFeeItemMgrService {
	/**
	 * 已开始/已结束的才可以录入实际费用
	 * @return
	 */
	Map<String, Object> inputRealFee(ContractFeeParam param);
	/**
	 * 未开始的才可以删除
	 * 
	 * @param feeItemId
	 * @return
	 */
	Map<String, Object> deleteContractFeeItem(Integer feeItemId);

	Map<String, Object> getContractFeeItems4Calculation(GetContractFeeItemsParam param);

	Map<String, Object> addContractFeeItem(ContractFeeParam param);

	/**
	 * 未开始的才可以更新
	 * 
	 * @param param
	 * @return
	 */
	Map<String, Object> updateContractFeeItem(ContractFeeParam param);

	Map<String, Object> finishAheadOfTime(Integer feeItemId, String optUser);
	
	
	Page<ContractFeeItem> getContractFeeItemsPage(ContractFeeItemPageQeuryParam param);
	
	Map<String, Object> getLogs(Integer feeItemId);
	
	ContractFeeItemDto getContractFeeItemDto(Integer feeItemId);

}
