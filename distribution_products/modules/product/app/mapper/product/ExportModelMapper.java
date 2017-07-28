package mapper.product;

import org.apache.ibatis.annotations.Param;

import dto.CommonExportDto;

public interface ExportModelMapper {

	CommonExportDto getExprotByFunctionId(@Param("functionId")String functionId);

}
