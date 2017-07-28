package controllers.product;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.CommonExportDto;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.product.IProductBaseService;
import util.product.CommonExportUtils;

public class CommonExportController extends Controller {
	@Inject
	IProductBaseService service;
	public Result export(){
		JsonNode reqNode = request().body().asJson();
		Map<String,String> result=Maps.newHashMap();
		String functionId = reqNode.get("functionId").asText();
		CommonExportDto exportModel= service.getExportModelByFunctionId(functionId);
		if(exportModel==null){
			result.put("result", "1");
			result.put("msg","查询不到导出信息");
			return ok(Json.toJson(result));
		}
		try {
			String sql=" ";
			String function = exportModel.getFunctionId();
			sql=sql+function;
			String functionParam = exportModel.getFunctionParam();
			Map<Integer,String> paramKeyMap=Maps.newHashMap();
			JsonNode paramNode = Json.parse(functionParam);
			if(paramNode!=null && paramNode.size()>0){
				for(JsonNode node: paramNode){
					int index = node.get("index").asInt();
					String key = node.get("key").asText();
					paramKeyMap.put(index, key);
				}
			}
			sql=sql+" (";
			if(paramKeyMap.size()>0){
				for(int i=0;i<paramKeyMap.size();i++){
					String key = paramKeyMap.get(i);
					if(reqNode.get(key)!=null && !"null".equals(reqNode.get(key).asText())){
						sql = sql + "'" + reqNode.get(key).asText() + "'" + ",";
					}else{
						sql=sql+"''"+",";
					}
				}
				sql=sql.substring(0, sql.length()-1);
			}
			sql=sql+")";
			sql=sql.replace("\"", "'");
			List<Map> sqlResult = service.productInventoryDataExportTest(sql);
			
			String functionResult = exportModel.getFunctionResult();
			Map<Integer,String> sqlResultKeyMap=Maps.newHashMap();
			JsonNode resultNode = Json.parse(functionResult);
			if(resultNode!=null && resultNode.size()>0){
				for(JsonNode node: resultNode){
					int index = node.get("index").asInt();
					String key = node.get("key").asText();
					sqlResultKeyMap.put(index, key);
				}
			}
			
			String excelRows = exportModel.getExcelRows();
			Map<Integer,String> excelRowKeyMap=Maps.newHashMap();
			JsonNode excelRowsNode = Json.parse(excelRows);
			if(excelRowsNode!=null && excelRowsNode.size()>0){
				for(JsonNode node: excelRowsNode){
					int index = node.get("index").asInt();
					String rowName = node.get("rowName").asText();
					excelRowKeyMap.put(index, rowName);
				}
			}
			
			String excelWidth=exportModel.getExcelWidth();
			Map<Integer,Integer> excelWidthMap=Maps.newHashMap();
			JsonNode excelWidthNode=null;
			if(excelWidth!=null){
				excelWidthNode=Json.parse(excelWidth);
			}
			
			if(excelWidthNode!=null && excelWidthNode.size()>0){
				for(JsonNode node:excelWidthNode){
					int index=node.get("index").asInt();
					int width=node.get("width").asInt();
					excelWidthMap.put(index, width);
				}
			}
			
			//行合并依赖列
			String mergeKey = exportModel.getMergeKey();
			
			//依赖某一字段进行行合并的列
			String mergeKeyRows = exportModel.getMergeKeyRows();
			List<Integer> mergeKeyRowsLists=Lists.newArrayList();
			JsonNode mergeKeyRowsNode=null;
			if(mergeKeyRows!=null){
				mergeKeyRowsNode=Json.parse(mergeKeyRows);
			}
			if(mergeKeyRowsNode!=null && mergeKeyRowsNode.size()>0){
				for(JsonNode node:mergeKeyRowsNode){
					Integer rowMerge = node.asInt();
					mergeKeyRowsLists.add(rowMerge);
				}
			}
			
			String rowsMerge = exportModel.getRowsMerge();
			List<Integer> rowsMergeLists=Lists.newArrayList();
			JsonNode rowsMergeNode=null;
			if(rowsMerge!=null){
				rowsMergeNode=Json.parse(rowsMerge);
			}
			if(rowsMergeNode!=null && rowsMergeNode.size()>0){
				for(JsonNode node:rowsMergeNode){
					Integer rowMerge = node.asInt();
					rowsMergeLists.add(rowMerge);
				}
			}
			
			String fileName = exportModel.getFileName();
			String excelTitle = exportModel.getExcelTitle();
			
			String tempFileName=CommonExportUtils.getFile(sqlResult,sqlResultKeyMap,excelRowKeyMap,fileName,excelTitle,excelWidthMap,rowsMergeLists,mergeKey,mergeKeyRowsLists);
			result.put("result", "0");
			result.put("fileName", fileName);
			result.put("temFileName",tempFileName);
		} catch (Exception e) {
			result.put("result", "1");
			result.put("msg","导出功能异常!");
			Logger.info("导出功能异常------->{}", e);
		}
		return ok(Json.toJson(result));
	}
	public Result downLoad(){
		String tempFileName = request().getQueryString("tempFileName");
		String fileName = request().getQueryString("fileName");
		File file=new File("/tmp/"+tempFileName);
		if(!file.exists()){
			return ok("downLoadError");
		}
		try {
			response().setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("utf-8"),"ISO8859_1"));
		} catch (UnsupportedEncodingException e) {
			Logger.info("commondownloadError----------->",e);
		}
		return ok(file);
	}
}