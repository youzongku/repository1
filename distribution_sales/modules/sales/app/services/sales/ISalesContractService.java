package services.sales;

import java.util.List;
import java.util.Map;

import dto.JsonResult;
import dto.sales.FeeColumnDto;

/**
 * @author zbc
 * 2017年5月12日 上午10:29:25
 */
public interface ISalesContractService {

	public JsonResult<?> caculate(Integer sid);

	public List<FeeColumnDto> fieldNames(String cno);

	public JsonResult<?> pages(String string);

	public JsonResult<?> refresh(String string);

	public Map<String,Object> contractFee(String cno);

}
