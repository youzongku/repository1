package services.sales;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import entity.platform.order.template.TaoBaoOrder;
import play.mvc.Http.MultipartFormData.FilePart;

public interface IImportOrderService {
	/**
	 * 描述： 2016年5月14日
	 * 
	 * @param files
	 *            上传的文件
	 * @param params
	 *            上传参数
	 * @return
	 * @throws IOException
	 */
	Map<String, Object> importOrder(List<FilePart> files,
			Map<String, String[]> params) throws IOException;

	/**
	 * 描述：通过模板文件名获取下载的模板文件 2016年5月18日
	 * 
	 * @param fileName
	 *            导出模板文件名
	 * @return
	 */
	File getExportModel(String fileName);

	/**
	 * 描述：导入其他平台订单（阿力巴巴,有赞，京东） 2016年5月23日
	 * 
	 * @param files
	 *            上传的文件
	 * @param params
	 *            上传参数
	 * @return
	 * @throws IOException
	 */
	Map<String, Object> importOtherOrder(List<FilePart> files,
			Map<String, String[]> params) throws IOException;

	/**
	 * 补全订单信息
	 *
	 * @param email
	 */
	void completionOrderInfo(String email);
}
