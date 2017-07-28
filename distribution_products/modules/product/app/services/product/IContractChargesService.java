package services.product;

import java.util.List;
import java.util.Map;

import dto.JsonResult;
import entity.contract.ChargesOprecord;
import entity.contract.ContractCost;
import entity.contract.ContractCostType;

/**
 * @author zbc
 * 2017年3月25日 下午3:25:41
 */
public interface IContractChargesService {

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月25日 下午3:27:17
	 */
	Map<String, Object> create(String string,String admin);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月27日 上午10:09:19
	 */
	List<ContractCostType> getTypes();

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月27日 上午10:22:00
	 */
	Map<String, Object> update(String string, String admin);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月27日 上午10:37:22
	 */
	ContractCost get(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月27日 上午11:38:04
	 */
	List<ChargesOprecord> oprecord(Integer id);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月27日 上午11:50:49
	 */
	Map<String, Object> pageSearch(String string);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月27日 下午5:35:27
	 */
	Map<String, Object> match(String string);

	JsonResult<?> delete(Integer id, String admin);

	JsonResult<?> earlyTermination(Integer id, String admin);
}
