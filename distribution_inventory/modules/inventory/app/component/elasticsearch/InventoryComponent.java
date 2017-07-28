package component.elasticsearch;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import play.Logger;
import play.libs.Json;
import services.product_inventory.IProductInventoryService;


public class InventoryComponent implements IInventoryComponent{
	
	@Inject
	IProductInventoryService inventoryService;
	
	private static final String INDEX_NAME = "b2b_products";
	private static final String CLOUD_TYPE = "CloudInventory";
	private static final String MICRO_TYPE = "MicroInventory";
	
	public void elasticSearchInit(){
		IndicesAdminClient adminClient = EsCommonUtil.getClient().admin().indices();
		
		//判断节点是否存在
		if(adminClient.prepareExists(INDEX_NAME).get().isExists()){//如果存在，先删除索引
			Logger.debug("索引【{}】已存在，先删除索引", INDEX_NAME);
			DeleteIndexRequestBuilder delReq = adminClient.prepareDelete(INDEX_NAME);
			DeleteIndexResponse res = delReq.get();
			Logger.debug(res.isAcknowledged() ? "删除成功" : "删除失败");
		}
		// 创建mapping，相当于数据库表列字段类型
		ObjectNode cloudNode = generateMapping(CloudInventoryDoc.class);
		ObjectNode microNode = generateMapping(MicroInventoryDoc.class);
		CreateIndexRequestBuilder req = adminClient.prepareCreate(INDEX_NAME);
		req.addMapping(CLOUD_TYPE, cloudNode.toString());
		req.addMapping(MICRO_TYPE, microNode.toString());
		CreateIndexResponse resp = req.get();
		Logger.debug(resp.isAcknowledged() ? "库存索引创建成功" : "库存索引创建失败");
		{//初始化库存数据
			
			List<CloudInventoryDoc> cloudInventoryList =  inventoryService.cloudInventory();
			List<MicroInventoryDoc> microInventoryList =  inventoryService.microInventory(null);
			
			// 开启批量插入
			BulkRequestBuilder bulkRequest = EsCommonUtil.getClient().prepareBulk();
			for (int i = 0; i < cloudInventoryList.size(); i++) {
				CloudInventoryDoc doc = cloudInventoryList.get(i);
				bulkRequest.add(EsCommonUtil.getClient().prepareIndex(INDEX_NAME, CLOUD_TYPE)
						.setId(String.valueOf(doc.getId()))
						.setSource(Json.toJson(doc).toString()));
				// 每一千条提交一次
				if ((i + 1) % 1000 == 0) {
					bulkRequest.get();
				}
			}
			bulkRequest.get();
			Logger.info("es导入云仓库存记录{}行", cloudInventoryList.size());
			
			for (int i = 0; i < microInventoryList.size(); i++) {
				MicroInventoryDoc doc = microInventoryList.get(i);
				bulkRequest.add(EsCommonUtil.getClient().prepareIndex(INDEX_NAME, MICRO_TYPE)
						.setId(String.valueOf(doc.getId()))
						.setSource(Json.toJson(doc).toString()));
				// 每一千条提交一次
				if ((i + 1) % 1000 == 0) {
					bulkRequest.get();
				}
			}
			bulkRequest.get();
			Logger.info("es导入微仓库存记录{}行", microInventoryList.size());
			
		}
		{//初始化商品数据
			
		}
	}
	
	/**
	 * 创建mapping
	 * 
	 * @param clazz
	 * @return
	 */
	private <T> ObjectNode generateMapping(Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		ObjectNode properties = node.putObject("properties");
		for (Field f : fields) {
			String name = f.getName();
			MappingType mt = f.getAnnotation(MappingType.class);
			if (mt != null) {
				ObjectNode fieldNode = properties.putObject(name);
				fieldNode.put("type", mt.type());
				// 要是string才有index
				if (StringUtils.isNotEmpty(mt.index()) && "string".equals(mt.type())) {
					fieldNode.put("index", mt.index());
				}
				// 分析器
				if (StringUtils.isNotEmpty(mt.analyzer())){
					fieldNode.put("analyzer", mt.analyzer());
				}
			}
		}
		return node;
	}
	
}
