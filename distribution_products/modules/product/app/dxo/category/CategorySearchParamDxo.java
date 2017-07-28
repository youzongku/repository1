package dxo.category;

import com.fasterxml.jackson.databind.JsonNode;

import dto.category.CategorySearchParamDto;
import services.base.utils.JsonFormatUtils;

/**
 *  CategorySearchParamDto裁剪/翻译工具
 * 
 * @author ye_ziran
 * @since 2015年12月9日 上午9:39:02
 */
public class CategorySearchParamDxo {
	
	/**
	 * Json => dto
	 * 
	 * @param data
	 * @return
	 * @author ye_ziran
	 * @since 2015年12月9日 下午12:07:27
	 */
	public static CategorySearchParamDto json2ParamDto(JsonNode data){
		CategorySearchParamDto dto = JsonFormatUtils.jsonToBean(data.get("data") + "", CategorySearchParamDto.class);
		if(null == dto){
			dto = new CategorySearchParamDto();
		}
		if(data.get("pageSize") != null) {
			dto.setPageSize(data.get("pageSize").asInt());
		}
		if(data.get("pageNo") != null) {
			dto.setPageNo(data.get("pageNo").asInt());
		}
		//trans2ParamDto(dto, data.get("data"));
		return dto;
	}
	
//	private static void trans2ParamDto(CategorySearchParamDto paramDto, JsonNode jsonNode){
//		if(null != jsonNode){
//			paramDto.setCatIds(jsonNode.get("catIds")==null ? null : jsonNode.get("catIds"));
//			paramDto.setParentId(jsonNode.get("parentId")==null ? null : jsonNode.get("parentId").asInt());
//			paramDto.setLevel(jsonNode.get("level")==null ? null : jsonNode.get("level").asInt());			
//		}
//	}
}
