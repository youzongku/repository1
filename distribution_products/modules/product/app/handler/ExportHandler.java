package handler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import dto.CommonExportDto;
import entity.product.ExportSyncResult;
import event.ExportEvent;
import mapper.product.ExportSyncResultMapper;
import play.Logger;
import play.libs.Json;
import services.product.IProductBaseService;
import util.product.CommonExportUtils;

public class ExportHandler {
	@Inject
    private IProductBaseService baseService;
	
	@Inject
	private ExportSyncResultMapper exportSyncResultMapper;
	
	@Subscribe
    public void createExportFile(ExportEvent event) {
		try {
			Logger.info("库存信息导出异步触发成功！");
			String reqParam = event.getReqParam();
			JsonNode reqNode = Json.parse(reqParam);
			String operator = reqNode.get("operator").asText();
			ExportSyncResult syncResult = exportSyncResultMapper.selectByOperator(operator);
			String functionId = reqNode.get("functionId").asText();
			CommonExportDto exportModel= baseService.getExportModelByFunctionId(functionId);
			if(exportModel==null){
				if(syncResult !=null){
					syncResult.setExportResult(2);
					syncResult.setUpdateTime(new Date());
					syncResult.setMsg("查询不到导出函数信息");
					Logger.info("库存信息导出查询不到导出函数信息。");
					exportSyncResultMapper.updateByPrimaryKeySelective(syncResult);
					return ;
				}else{
					Logger.info("库存信息导出查询不到导出函数信息，导出记录信息");
					return ;
				}
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
				List<Map> sqlResult = baseService.productInventoryDataExportTest(sql);
				
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
				if(StringUtils.isNotEmpty(rowsMerge)){
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
				syncResult.setExportResult(3);//导出文件已生成
				syncResult.setFileName(tempFileName);
				syncResult.setMsg("导出文件已生成");
				syncResult.setUpdateTime(new Date());
				exportSyncResultMapper.updateByPrimaryKeySelective(syncResult);
				Logger.info("库存信息导出成功！");
			}catch (Exception e) {
				Logger.info("库存信息导出发生异常{}",e);
				syncResult.setExportResult(2);//导出文件已生成
				syncResult.setMsg("导出文件发生异常");
				syncResult.setUpdateTime(new Date());
				exportSyncResultMapper.updateByPrimaryKeySelective(syncResult);
			}
		}catch (Exception e) {
			Logger.info("库存信息导出发生异常{}",e);
		}

	}
}
