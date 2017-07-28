package services.sales;

import java.util.List;
import java.util.Map;

import play.mvc.Http.MultipartFormData.FilePart;

/**
 * 后台导入发货单
 * @author huangjc
 * @date 2017年3月24日
 */
public interface IManagerImportOrderService {
	/**
	 * 导入发货单
	 * @param files
	 * @return
	 */
	Map<String, Object> importSalesOrder(List<FilePart> files, Map<String, String[]> params);
}
