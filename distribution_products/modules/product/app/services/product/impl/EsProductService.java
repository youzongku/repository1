package services.product.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import component.elasticsearch.EsCommonUtil;
import component.elasticsearch.MappingType;
import component.elasticsearch.ProductInfoDoc;
import component.elasticsearch.ProductLiteDoc;
import dto.product.PageResultDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import mapper.product.ProductBaseMapper;
import play.Logger;
import play.libs.Json;
import services.product.IEsProductService;

/**
 * 索引service
 * 
 * @author huangjc
 * @date 2016年11月15日
 */
public class EsProductService implements IEsProductService {

	@Inject
	private ProductBaseMapper productBaseMapper;

	private static final String INDEX_NAME = "b2b_products";
	private static final String TYPE_NAME = "product_lite";

	@Override
	public boolean createProductIndex() {
		Client esclient = EsCommonUtil.getClient();
		if (indexExists(INDEX_NAME)) {
			Logger.debug("索引【{}】已存在，不需要再创建", INDEX_NAME);
			return false;
		}

		// 创建mapping，相当于数据库表列字段类型
		ObjectNode on = generateMapping(ProductInfoDoc.class);

		Logger.info("索引映射是：{}", on);

		CreateIndexRequestBuilder req = esclient.admin().indices().prepareCreate(INDEX_NAME);
		// type和mapping
		req.addMapping(TYPE_NAME, on.toString());

		Logger.debug("{}.{}的Mapping：{}", INDEX_NAME, TYPE_NAME, on);
		CreateIndexResponse resp = req.get();
		Logger.debug(resp.isAcknowledged() ? "创建成功" : "创建失败");

		return resp.isAcknowledged();
	}

	@Override
	public void initProductDatas() {
		Client esclient = EsCommonUtil.getClient();
		
		if(indexRemove(INDEX_NAME)){
			createProductIndex();
		}
		
		//真实类目树
		Map<String, List<ProductLiteDoc>> prodCatTreeMap = buildMap(productBaseMapper.getProductsCatTree());
		//虚拟类目树
		Map<String, List<ProductLiteDoc>> prodVirCatTreeMap = buildMap(productBaseMapper.getProductsVirCatTree());
		
		ProductSearchParamDto searchDto = new ProductSearchParamDto();
		searchDto.setIstatus(1);
		List<ProductLiteDoc> products = productBaseMapper.getProductsInfo();
		
		for (ProductLiteDoc pl : products) {
			packCatTree(prodCatTreeMap, pl);
			packVirCatTree(prodVirCatTreeMap, pl);
		}
		
		// 开启批量插入
		BulkRequestBuilder bulkRequest = esclient.prepareBulk();
		Logger.info("即将往es导入" + products.size() + "条数据");

		for (int i = 0; i < products.size(); i++) {
			ProductLiteDoc doc = products.get(i);
			bulkRequest.add(esclient.prepareIndex(INDEX_NAME, TYPE_NAME)
					.setId(String.valueOf(doc.getIid()))
					.setSource(Json.toJson(doc).toString()));
			// 每一千条提交一次
			if ((i + 1) % 1000 == 0) {
				bulkRequest.get();
				//Logger.info("本次提交了：1000条");
			}
		}

		bulkRequest.get();
		//Logger.info("本次提交了：" + (docs.size() % 1000) + "条");

		Logger.info("添加数据成功");
	}
	
	private Map<String, List<ProductLiteDoc>> buildMap(List<ProductLiteDoc> list){
		Map<String, List<ProductLiteDoc>> prodCatTreeMap = Maps.newHashMap();
		for (ProductLiteDoc doc : list) {
			List<ProductLiteDoc> catTreeList = prodCatTreeMap.get(doc.getCsku());
			if(null != catTreeList){
				catTreeList.add(doc);
			}else{
				List<ProductLiteDoc> tmpList = Lists.newArrayList();
				tmpList.add(doc);
				prodCatTreeMap.put(doc.getCsku(), tmpList);
			}
		}
		return prodCatTreeMap;
	}
	
	/**
	 * 构建类目树
	 * 
	 * @param capMap
	 * @param doc
	 * @author ye_ziran
	 * @since 2017年3月8日 下午3:29:10
	 */
	private void packCatTree( Map<String, List<ProductLiteDoc>> capMap, ProductLiteDoc doc){
		StringBuilder idTreeSb = new StringBuilder(128);
		StringBuilder nameTreeSb = new StringBuilder(128);
		List<ProductLiteDoc> docList = capMap.get(doc.getCsku());
		if(null != docList){
			for (ProductLiteDoc item : docList) {
				idTreeSb.append(item.getCategoryIdTree()).append(",");
				nameTreeSb.append(item.getCategoryNameTree()).append(",");
			}
		}
		doc.setCategoryIdTree(idTreeSb.toString());
		doc.setCategoryNameTree(nameTreeSb.toString());
	}
	
	/**
	 * 对doc填充类目树的数据
	 * 
	 * @param capMap
	 * @param doc
	 * @author ye_ziran
	 * @since 2017年3月8日 下午3:29:19
	 */
	private void packVirCatTree( Map<String, List<ProductLiteDoc>> capMap, ProductLiteDoc doc){
		StringBuilder idTreeSb = new StringBuilder(128);
		StringBuilder nameTreeSb = new StringBuilder(128);
		List<ProductLiteDoc> docList = capMap.get(doc.getCsku());
		if(null != docList){
			for (ProductLiteDoc item : docList) {
				idTreeSb.append(item.getVirCategoryIdTree()).append(",");
				nameTreeSb.append(item.getVirCategoryNameTree()).append(",");
			}
		}
		doc.setVirCategoryIdTree(idTreeSb.toString());
		doc.setVirCategoryNameTree(nameTreeSb.toString());
	}

	//		query与filter
	//		1、区别如下：
	//		query是要相关性评分的，filter不要；
	//		query结果无法缓存，filter可以。
	//		
	//		所以，选择参考：
	//		1、全文搜索、评分排序，使用query；
	//		2、是非过滤，精确匹配，使用filter。
	@Override
	public PageResultDto products(ProductSearchParamDto productSearchDto) {
		
		Client esclient = EsCommonUtil.getClient();
		
		Integer pageSize = productSearchDto.getPageSize();
		Integer currPage = productSearchDto.getCurrPage();
		Integer model = productSearchDto.getModel();
		if(null == productSearchDto.getQueryStr()){
			productSearchDto.setQueryStr("proposalRetailPrice");
		}
		SearchRequestBuilder searchRequestBuilder = esclient.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		if (pageSize != null && currPage != null) {
			if (pageSize > 50) {
				pageSize = 50;// 一页限制最多50，防止pageSize数量过大可能出现问题
			}
			searchRequestBuilder.setFrom((currPage - 1) * pageSize).setSize(pageSize);
		}
		
		AndFilterBuilder filterChain = FilterBuilders.andFilter(FilterBuilders.termFilter("istatus", 1));
		BoolFilterBuilder disPriceFilter = FilterBuilders.boolFilter().must(FilterBuilders.rangeFilter(productSearchDto.getQueryStr()).gt(0.0d));
		filterChain.add(disPriceFilter);//采购价必须>0
		//filterChain.add(FilterBuilders.rangeFilter("totalstock").from(1).to(Integer.MAX_VALUE));
		// 通配符匹配，类似sql：like '%text%'，用*开头，性能会比较低，建议不要使用?或*开头
		if (StringUtils.isNotEmpty(productSearchDto.getTitle())) {
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			//MatchQueryBuilder titleQuery = QueryBuilders.matchQuery("ctitle",productSearchDto.getTitle()).operator(Operator.AND);
			//PrefixQueryBuilder skuQuery = QueryBuilders.prefixQuery("csku", productSearchDto.getTitle().toUpperCase());
			//MatchQueryBuilder brandQuery = QueryBuilders.matchQuery("brand", productSearchDto.getTitle()).operator(Operator.AND);
			//MatchQueryBuilder categoryQuery = QueryBuilders.matchQuery("categoryNameTree", productSearchDto.getTitle()).operator(Operator.AND);
			//MatchQueryBuilder vcategoryQuery = QueryBuilders.matchQuery("virCategoryNameTree", productSearchDto.getTitle()).operator(Operator.AND);
			
			//结构化过滤，没有打分
			/*boolQuery.must(QueryBuilders.filteredQuery(null, FilterBuilders.boolFilter()
					.should(FilterBuilders.queryFilter(skuQuery))
					.should(FilterBuilders.queryFilter(titleQuery))
					.should(FilterBuilders.queryFilter(brandQuery))
					.should(FilterBuilders.queryFilter(categoryQuery))
					.should(FilterBuilders.queryFilter(vcategoryQuery))));*/
			//结构化查询，有打分，按分数高低排序
			boolQuery.should(QueryBuilders.matchQuery("ctitle", productSearchDto.getTitle())
					.operator(Operator.AND))
					.should(QueryBuilders.prefixQuery("csku", productSearchDto.getTitle().toUpperCase()))
					.should(QueryBuilders.matchQuery("brand", productSearchDto.getTitle()).operator(Operator.AND))
					.should(QueryBuilders.matchQuery("categoryNameTree", productSearchDto.getTitle()).operator(Operator.AND))
					.should(QueryBuilders.matchQuery("virCategoryNameTree", productSearchDto.getTitle()).operator(Operator.AND));
			searchRequestBuilder = searchRequestBuilder.setQuery(boolQuery);
		}
		if(productSearchDto.getCategoryId()!=null){//根据大类过滤
			filterChain.add(FilterBuilders.termFilter("categoryId", productSearchDto.getCategoryId()));
		}
		if(productSearchDto.getWarehouseId()!=null){
			filterChain.add(FilterBuilders.termFilter("warehouseId", productSearchDto.getWarehouseId()));
		}
		if(productSearchDto.getSkuList()!=null && productSearchDto.getSkuList().size()>0){
			Logger.info("设置skuList");
			filterChain.add(FilterBuilders.inFilter("csku", productSearchDto.getSkuList()));
		}
		// 只能查询可卖的商品
		filterChain.add(FilterBuilders.termFilter("salable", 1));
		
		/*if(StringUtils.isNotEmpty(productSearchDto.getTitle())){
			searchRequestBuilder.addSort("ctitle", SortOrder.DESC);
		}else{
			searchRequestBuilder.addSort("csku", SortOrder.DESC);
		}*/
		
		
		// 价格区间查询
		if(null != productSearchDto.getMinPrice() && null != productSearchDto.getMaxPrice()){
			RangeFilterBuilder rangeFilterBuilder = FilterBuilders.rangeFilter(productSearchDto.getQueryStr())
					.from(productSearchDto.getMinPrice())
					.to(productSearchDto.getMaxPrice());
			filterChain.add(rangeFilterBuilder);
		}
		
		// 过滤链
		searchRequestBuilder = searchRequestBuilder.setPostFilter(filterChain);
		
		// 排序
		// 得分降序、库存降序
		// 优先排序字段放前面	stock->score->ctitle->csku
		searchRequestBuilder.addSort(SortBuilders.fieldSort("stock").order(SortOrder.DESC));
		searchRequestBuilder.addSort(SortBuilders.scoreSort().order(SortOrder.DESC));
		searchRequestBuilder.addSort("typeId", SortOrder.ASC);
		if(StringUtils.isNotEmpty(productSearchDto.getTitle())){
			searchRequestBuilder.addSort("ctitle", SortOrder.DESC);
		}else{
			searchRequestBuilder.addSort("csku", SortOrder.DESC);
		}
		SearchResponse response = searchRequestBuilder.get();

		// 获取命中的结果
		SearchHits hits = response.getHits();

		// 拿到匹配结果的数量
		Logger.info("查询到的总记录数：{}", String.valueOf(hits.getHits().length));
		
		//结果转换
		List<ProductLiteDoc> plList = new ArrayList<>();
		SearchHit[] hitArr = hits.getHits();
		int len = Math.min(hitArr.length, null == pageSize ? 50 : pageSize);
		for (int i=0;i<len;i++) {
			//Logger.debug("hitArr[i].getSourceAsString() == "+hitArr[i].getSourceAsString());
			ProductLiteDoc prod = Json.fromJson(Json.parse(hitArr[i].getSourceAsString()), ProductLiteDoc.class);
			if(null != model){
				switch (model) {//根据模式显示不同的disPrice
				case ProductSearchParamDto.VIP_PRICE:
					prod.setDisPrice(prod.getVipPrice());
					break;
				case ProductSearchParamDto.FTZ_PRICE:
					prod.setDisPrice(prod.getFtzPrice());
					break;
				case ProductSearchParamDto.SUPERMARKET_PRICE:
					prod.setDisPrice(prod.getSupermarketPrice());
					break;
				case ProductSearchParamDto.DISTRIBUTOR_PRICE:
					prod.setDisPrice(prod.getDistributorPrice());
					break;
				case ProductSearchParamDto.ELETRICITY_PRICE:
					prod.setDisPrice(prod.getElectricityPrices());
					break;
				}
			}else{
				prod.setDisPrice(prod.getProposalRetailPrice());
			}
			plList.add(prod);
		}
		PageResultDto page = new PageResultDto(pageSize,(int)hits.getTotalHits(),currPage, plList);
		Logger.info("elastic search的查询结果是：{}",page);
		return page;
	}

	

	@Override
	public boolean update(List<ProductLiteDoc> prodLites) {
		boolean res = false;
		Client client = EsCommonUtil.getClient();
		BulkRequestBuilder bulkRequest =  EsCommonUtil.getClient().prepareBulk();
		for (ProductLiteDoc lite : prodLites) {
			IndexRequestBuilder indexRequestBuilder = client.prepareIndex(INDEX_NAME, TYPE_NAME);
			//ProductLiteDoc prod = getProdDocFromEs(lite);
			//BeanUtils.copyProperties(lite, prod);
			indexRequestBuilder.setId(String.valueOf(lite.getIid())).setSource(Json.toJson(lite).toString());
			bulkRequest.add(indexRequestBuilder);
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			Map<String, String> failedDocuments = new HashMap<String, String>();
			for (BulkItemResponse item : bulkResponse.getItems()) {
				if (item.isFailed())
					failedDocuments.put(item.getId(), item.getFailureMessage());
			}
			Logger.error("Bulk indexing has failures. Use EsProductService.update() for detailed messages [{}]", failedDocuments);
		}else{
			res = true;
			Logger.info("es更新成功，更新数量：{}", prodLites.size());
		}
		return res;
	}
	

	@Override
	public boolean update(ProductLiteDoc prodLite) {
		List<ProductLiteDoc> prodLites = new ArrayList<>();
		prodLites.add(prodLite);
		return update(prodLites);
	}
	
	/**
	 * 
	 * 
	 * @param lite
	 * @param searchRequestBuilder
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月2日 下午6:30:55
	 */
	public ProductLiteDoc getProdDocFromEs(ProductLite lite){
		ProductLiteDoc prod = null;
		SearchRequestBuilder searchRequestBuilder = EsCommonUtil.getClient().prepareSearch(INDEX_NAME).setTypes(TYPE_NAME)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termQuery("csku", lite.getCsku())).must(QueryBuilders.termQuery("warehouseId", lite.getWarehouseId()));
		searchRequestBuilder.setQuery(boolQuery);
		SearchResponse response = searchRequestBuilder.get();
		SearchHits hits = response.getHits();
		SearchHit[] hitArr = hits.getHits();
		if(hitArr.length > 0){
			prod = Json.fromJson(Json.parse(hitArr[0].getSourceAsString()), ProductLiteDoc.class);
		}
		
		return prod;
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
	
	/**
	 * 判断指定的索引名是否存在
	 * 
	 * @param indexName
	 *            索引名
	 * @return 存在：true; 不存在：false;
	 */
	private boolean indexExists(String indexName) {
		return EsCommonUtil.getClient().admin().indices().prepareExists(indexName).get()
				.isExists();
	}
	
	private boolean indexRemove(String indexName) {
		Logger.info("移除索引:{}", INDEX_NAME);
		DeleteIndexResponse resp = EsCommonUtil.getClient().admin().indices().prepareDelete(indexName).get();
		return resp.isAcknowledged();
	}

	@Override
	public boolean delete(String id) {
		DeleteResponse response = EsCommonUtil.getClient().prepareDelete().setRefresh(true)
			.setIndex(INDEX_NAME).setType(TYPE_NAME).setId(id)
			.execute().actionGet();
		return response.isFound();
	}
	
}
