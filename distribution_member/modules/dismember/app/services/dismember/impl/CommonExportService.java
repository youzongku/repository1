package services.dismember.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dto.dismember.CommonExportDto;
import mapper.dismember.ExportModelMapper;
import play.Logger;
import play.libs.Json;
import services.dismember.ICommonExportService;
import utils.dismember.CommonExportUtils;

public class CommonExportService implements ICommonExportService {

	@Inject
	private ExportModelMapper exportModelMapper;
	
	@Override
	public String commonExport(String reqStr) {
		Map<String, Object> result = Maps.newHashMap();
		JsonNode reqNode = Json.parse(reqStr);
		String functionId = reqNode.get("functionId").asText();
		CommonExportDto exportModel = exportModelMapper.getExportByFunctionId(functionId);
		if (exportModel == null) {
			result.put("result", "1");
			result.put("msg", "查询不到导出信息");
			return Json.toJson(result).toString();
		}
		try {
			String sql = " ";
			String function = exportModel.getFunctionId();
			sql = sql + function;
			String functionParam = exportModel.getFunctionParam();
			Map<Integer, String> paramKeyMap = Maps.newHashMap();
			JsonNode paramNode = Json.parse(functionParam);
			if (paramNode != null && paramNode.size() > 0) {
				for (JsonNode node : paramNode) {
					int index = node.get("index").asInt();
					String key = node.get("key").asText();
					paramKeyMap.put(index, key);
				}
			}
			sql = sql + " (";
			if (paramKeyMap.size() > 0) {
				for (int i = 0; i < paramKeyMap.size(); i++) {
					String key = paramKeyMap.get(i);
					if (reqNode.get(key) != null && !"null".equals(reqNode.get(key).asText())) {
						sql = sql + "'" + reqNode.get(key).asText() + "'" + ",";
					} else {
						sql = sql + "''" + ",";
					}
				}
				sql = sql.substring(0, sql.length() - 1);
			}
			sql = sql + ")";
			List<Map> sqlResult = exportModelMapper.getExportDataBySqlFunction(sql);
			String functionResult = exportModel.getFunctionResult();
			Map<Integer, String> sqlResultKeyMap = Maps.newHashMap();
			JsonNode resultNode = Json.parse(functionResult);
			if (resultNode != null && resultNode.size() > 0) {
				for (JsonNode node : resultNode) {
					int index = node.get("index").asInt();
					String key = node.get("key").asText();
					sqlResultKeyMap.put(index, key);
				}
			}
			String excelRows = exportModel.getExcelRows();
			Map<Integer, String> excelRowKeyMap = Maps.newHashMap();
			JsonNode excelRowsNode = Json.parse(excelRows);
			if (excelRowsNode != null && excelRowsNode.size() > 0) {
				for (JsonNode node : excelRowsNode) {
					int index = node.get("index").asInt();
					String rowName = node.get("rowName").asText();
					excelRowKeyMap.put(index, rowName);
				}
			}

			String excelWidth = exportModel.getExcelWidth();
			Map<Integer, Integer> excelWidthMap = Maps.newHashMap();
			JsonNode excelWidthNode = null;
			if (excelWidth != null) {
				excelWidthNode = Json.parse(excelWidth);
			}

			if (excelWidthNode != null && excelWidthNode.size() > 0) {
				for (JsonNode node : excelWidthNode) {
					int index = node.get("index").asInt();
					int width = node.get("width").asInt();
					excelWidthMap.put(index, width);
				}
			}

			String mergeKey = exportModel.getMergeKey();

			String rowsMerge = exportModel.getRowsMerge();
			List<Integer> rowsMergeLists = Lists.newArrayList();
			JsonNode rowsMergeNode = null;
			if (rowsMerge != null) {
				rowsMergeNode = Json.parse(rowsMerge);
			}
			if (rowsMergeNode != null && rowsMergeNode.size() > 0) {
				for (JsonNode node : rowsMergeNode) {
					Integer rowMerge = node.asInt();
					rowsMergeLists.add(rowMerge);
				}
			}

			String fileName = exportModel.getFileName();
			String excelTitle = exportModel.getExcelTitle();

			String tempFileName = CommonExportUtils.getFile(sqlResult, sqlResultKeyMap, excelRowKeyMap, fileName,
					excelTitle, excelWidthMap, rowsMergeLists, mergeKey);
			result.put("result", "0");
			result.put("fileName", fileName);
			result.put("temFileName", tempFileName);
		} catch (Exception e) {
			Logger.info("导出功能发生异常{}", e);
			result.put("result", "1");
			result.put("msg", "导出功能发生异常");
		}
		return Json.toJson(result).toString();
	}

}
