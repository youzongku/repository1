package mapper.dismember;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.dismember.CommonExportDto;

public interface ExportModelMapper {

	CommonExportDto getExportByFunctionId(@Param("functionId")String functionId);

	/**
	 * 获取导出数据信息
	 * @param sql
	 * @return
	 */
	List<Map> getExportDataBySqlFunction(@Param("param")String sql);
}
