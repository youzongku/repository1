package services.sales;

import java.io.File;
import java.util.List;
import java.util.Map;

import dto.sales.AsyncExportDto;


/**
 * @author zbc
 * 2017年6月23日 上午9:54:01
 */
public interface IAsyncExportService {

	String createFile(Map<String, String[]> map,AsyncExportDto asyncExportDto, String[] headerString, Map<String, String> fieldsMap, List<String> accounts);

	File dowloadFile(AsyncExportDto dto);

	void insert(Map<String, String[]> map, AsyncExportDto asyncExportDto, String[] headerString,
			Map<String, String> fieldsMap);

}
