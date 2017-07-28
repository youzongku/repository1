package services.sales.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import dto.sales.TaoBaoOrderForm;
import events.sales.ImportOrderSyncEvent;
import liquibase.util.csv.opencsv.CSVReader;
import mapper.sales.ImportOrderTemplateFieldMapper;
import mapper.sales.TaoBaoOrderGoodsMapper;
import mapper.sales.TaoBaoOrderMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import play.Logger;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;
import services.sales.IHttpService;
import services.sales.IImportOrderService;
import util.sales.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.sales.ImportResultInfo;
import entity.platform.order.template.TaoBaoOrder;
import entity.platform.order.template.TaoBaoOrderGoods;
import entity.sales.ImportOrderTemplateField;

public class ImportOrderService implements IImportOrderService {
	@Inject private ImportOrderTemplateFieldMapper importOrderTemplateFieldMapper;
	@Inject private TaoBaoOrderMapper orderMapper;
	@Inject private TaoBaoOrderGoodsMapper orderGoodsMapper;

	@Inject
	private TaoBaoOrderMapper taoBaoOrderMapper;

	@Inject
	private TaoBaoOrderGoodsMapper taoBaoOrderGoodsMapper;

	@Inject
	private IHttpService httpService;

	@Inject
	private EventBus eventBus;
	
	@Override
	public Map<String, Object> importOrder(List<FilePart> files, Map<String, String[]> params) throws IOException {
		//请求返回结果map
		Map<String, Object> resultMap = new HashMap<String,Object>();
		if(files ==null || files.size()==0){
			resultMap.put("flag", false);
			resultMap.put("msg", "导入失败，没有选择导入文件");
			return resultMap;
		}

		String email = params.containsKey("email") ? params.get("email")[0] : null;
		String templateType = params.containsKey("templateType") ? params.get("templateType")[0] : null;
		String fileID = params.containsKey("id") ? params.get("id")[0] : null;
		String md5 = params.containsKey(fileID + "_md5") ? params.get(fileID + "_md5")[0] : null;
		//导入结果信息列表
		List<ImportResultInfo> resultInfos = new ArrayList<ImportResultInfo>();
		Logger.debug("importOrder    params----->" + Json.toJson(params).toString());
		File origin = null;
		String fileMD5 = null;
		for (FilePart filePart : files) {
			String fileName = filePart.getFilename();
			ImportResultInfo resultInfo = new ImportResultInfo(fileName, 0, 0);
			origin = filePart.getFile();
			fileMD5 = DigestUtils.md5Hex(new FileInputStream(origin));
			Logger.debug("importOrder    fileMD5----->" + fileMD5);
			if (fileMD5 != null && fileMD5.equals(md5)) {
				if (CsvUtils.isCsvFile(fileName)) {//导入csv文件数据
					importCsvFile(fileName,filePart.getFile(),resultInfo,email,Integer.valueOf(templateType));
				} else if(FileUtils.isExcelFile(fileName)){//导入excel文件数据
					importExcelFile(fileName,filePart.getFile(), resultInfo,email,Integer.valueOf(templateType));
				} else{
					resultInfo.getMessages().add("文件导入失败,文件格式不对");
				}
			} else {
				resultInfo.getMessages().add("文件MD5值校验不通过");
			}
			resultInfos.add(resultInfo);
		}
		resultMap.put("flag", true);
		resultMap.put("resultInfos", resultInfos);
		return resultMap;
	}
	
	@Override
	public File getExportModel(String fileName) {
		//模板标题
		String[] array = getExportTemplateTitleByFileName(fileName);;
		return ExportUtil.export(fileName + ".xls", array);
	}
	
	@Override
	public Map<String, Object> importOtherOrder(List<FilePart> files, Map<String, String[]> params) throws IOException {
		// 请求返回结果map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String email = params.containsKey("email") ? params.get("email")[0] : null;
		String templateType = params.containsKey("templateType") ? params.get("templateType")[0] : null;
		String fileID = params.containsKey("id") ? params.get("id")[0] : null;
		String md5 = params.containsKey(fileID + "_md5") ? params.get(fileID + "_md5")[0] : null;
		Logger.debug("importOtherOrder    参数params----->" + Json.toJson(params).toString());
		boolean flag = files != null && files.size() > 0 && !Strings.isNullOrEmpty(templateType);
		if (!flag) {
			resultMap.put("flag", false);
			resultMap.put("msg", "导入失败，没有选择导入文件或系统错误");
			return resultMap;
		}
		
		// 导入结果信息列表
		List<ImportResultInfo> resultInfos = new ArrayList<ImportResultInfo>();
		File origin;
		String fileMD5;
		for (FilePart filePart : files) {
			String fileName = filePart.getFilename();
			ImportResultInfo resultInfo = new ImportResultInfo(fileName, 0, 0);
			origin = filePart.getFile();
			fileMD5 = DigestUtils.md5Hex(new FileInputStream(origin));
			Logger.debug("importOrder    fileMD5----->" + fileMD5);
			// 比较md5值，确保文件的完整性
			if (fileMD5 != null && fileMD5.equals(md5)) {
				if ("8".equals(templateType)) {// 人人店
					Logger.info("准备导入人人店的订单");
					importRRDExcelFile(fileName, filePart.getFile(), resultInfo, email, Integer.valueOf(templateType));
				} else {
					if (CsvUtils.isCsvFile(fileName)) {
						// 导入csv文件数据
						importOtherOrderCsvFile(fileName, filePart.getFile(), resultInfo, email, Integer.valueOf(templateType));
					} else if (FileUtils.isExcelFile(fileName)) {
						// 导入excel文件数据
						importOtherOrderExcelFile(fileName, filePart.getFile(), resultInfo, email, Integer.valueOf(templateType));
					} else {
						resultInfo.getMessages().add("文件导入失败,文件格式不对");
					}
				}
			} else {
				resultInfo.getMessages().add("文件MD5值校验不通过");
			}
			resultInfos.add(resultInfo);
		}
		resultMap.put("flag", true);
		resultMap.put("resultInfos", resultInfos);
		return resultMap;
	}

	@Override
	public void completionOrderInfo(String email) {
		TaoBaoOrderForm taoBaoOrderForm = new TaoBaoOrderForm();
		taoBaoOrderForm.setIsComplete(2);
		taoBaoOrderForm.setEmail(email);
		// 查询订单
		List<TaoBaoOrder> allOrders = taoBaoOrderMapper.getAllOrders(taoBaoOrderForm);
		// 根据订单查询商品
		if (CollectionUtils.isNotEmpty(allOrders)) {
			for (TaoBaoOrder taoBaoOrder : allOrders) {

				JsonNode memberNode = null;
				try {
					// 获取用户详情
					memberNode = httpService.getMemberInfo(email);
					if (memberNode == null || !memberNode.has("comsumerType") || !memberNode.has("distributionMode")) {
						continue;
					}
					Integer distributionMode = memberNode.get("distributionMode").asInt();


						List<TaoBaoOrderGoods> allGoods = taoBaoOrderGoodsMapper.goodsLists(Lists.newArrayList(taoBaoOrder.getOrderNo()), email);
						if (CollectionUtils.isNotEmpty(allGoods)) {
							Set<String> skus = Sets.newHashSet();
							skus.addAll(Lists.transform(allGoods, good -> good.getSku()));
							JsonNode productStrNode = null;
							JsonNode result = null;
							if (skus.size() > 0) {
								try {
									productStrNode = httpService.getProducts(taoBaoOrderForm.getEmail(), Lists.newArrayList(skus), null,
											distributionMode);
									if (productStrNode.get("data") != null && productStrNode.get("data").get("result") != null) {
										result = productStrNode.get("data").get("result");
									}
								} catch (Exception e) {
									Logger.error("getProducts:" + e);
								}
							}

							List<Map<String, Object>> warehouseNameIds = null;
							Map<String, Object> warehouseNameId = null;
							for (TaoBaoOrderGoods good : allGoods) {
								warehouseNameIds = new ArrayList<Map<String, Object>>();
								for (JsonNode product : result) {
									warehouseNameId = Maps.newHashMap();
									if (good.getSku().equals(JsonCaseUtil.getStringValue(product, "csku"))) {
										warehouseNameId.put("warehouseId", product.get("warehouseId").asText());
										warehouseNameId.put("warehouseName", product.get("warehouseName").asText());
										good.setCtitle(product.get("ctitle").asText());
										good.setImageUrl(product.get("imageUrl").asText());
										good.setBatchNumber(product.get("batchNumber").asText());
										warehouseNameIds.add(warehouseNameId);
									}
								}
								good.setWarehouseNameId(warehouseNameIds);
							}
							// key = email_orderno value = List<TaoBaoOrderGoods>
							Map<String, List<TaoBaoOrderGoods>> ordersMap = allGoods.stream().collect(Collectors.groupingBy(e -> {
								return e.getEmail() + "_" + e.getOrderNo();
							}));
							for (TaoBaoOrder order : allOrders) {
								order.setGoods(ordersMap.get(order.getEmail() + "_" + order.getOrderNo()));
							}
						}

						List<TaoBaoOrderGoods> targetTaobaoOrderGoods = taoBaoOrder.getGoods();
						if (CollectionUtils.isNotEmpty(targetTaobaoOrderGoods)) {

							int size = targetTaobaoOrderGoods.size();
							int count = (int) targetTaobaoOrderGoods.stream().filter(d -> d.getWarehouseNameId().size() == 1).count();

							boolean flag = true;
							if (size == count) {
								int warehouseId = Integer.parseInt(String.valueOf(taoBaoOrder.getGoods().get(0).getWarehouseNameId().get(0).get("warehouseId")));
								String warehouseName = String.valueOf(taoBaoOrder.getGoods().get(0).getWarehouseNameId().get(0).get("warehouseName"));
								for (int i = 0; i < targetTaobaoOrderGoods.size(); i++) {

									if (i == 0) {
										continue;
									}
									TaoBaoOrderGoods taoBaoOrderGoods = targetTaobaoOrderGoods.get(i);

									int otherWarehouseId = Integer.parseInt(String.valueOf(taoBaoOrderGoods.getWarehouseNameId().get(0).get("warehouseId")));
									if (otherWarehouseId != warehouseId) {
										flag = false;
									}
								}

								if (flag) {//表明只能是属于某个个仓库的
									for (TaoBaoOrderGoods taoBaoOrderGoods : targetTaobaoOrderGoods) {
										if (StringUtils.isBlankOrNull(taoBaoOrderGoods.getWarehouseId())) {
											taoBaoOrderGoods.setWarehouseId(String.valueOf(warehouseId));
											taoBaoOrderGoods.setWarehouseName(warehouseName);
											taoBaoOrderGoodsMapper.saveGoodsInfo(taoBaoOrderGoods);
										}
									}

									if (taoBaoOrder.getIsComplete() == 2) {
										taoBaoOrder.setIsComplete(1);
										taoBaoOrder.setUpdateDate(new Date());


										if (StringUtils.isBlankOrNull(taoBaoOrder.getLogisticsTypeCode())) {

											JsonNode node = getShippingMethod(warehouseId);
											if (node != null) {
												String code = node.get("methodCode").asText();
												String methodName = node.get("methodName").asText();

												taoBaoOrder.setLogisticsTypeCode(code);
												taoBaoOrder.setLogisticsTypeName(methodName);
											}


											//省-市-区是否匹配
											boolean addressFlag = AddressUtils.isAdjustAddress(taoBaoOrder.getAddress());

											if (addressFlag) {
												Logger.info(">>>>>>>>>>>executeSupplementImportOrderInfo 补全订单信息：{}", taoBaoOrder.toString());
												taoBaoOrderMapper.updateByPrimaryKeySelective(taoBaoOrder);
											}
										}
									}
								}
							}
						}
				} catch (Exception e) {
					Logger.error(">>>>>>>>>>>>executeSupplementImportOrderInfo getMemberInfo:" + e);
				}
			}
		}
	}

	/**
	 * 获取默认物流方式
	 *
	 * @param warehouseId
	 * @return
	 */
	private JsonNode getShippingMethod(int warehouseId) {
		//获取物流方式
		JsonNode methodNode = null;
		try {
			methodNode = httpService.getShoppingMethod(warehouseId);
		} catch (IOException e) {
			Logger.error(">>>>>>>>>>>>>>>>>>>获取物流方式错误：{}", e);
		}
		if (methodNode == null) {
			return null;
		}

		Iterator<JsonNode> it = methodNode.iterator();
		JsonNode node = null;
		while (it.hasNext()) {
			node = (JsonNode) it.next();
			if (node.get("default").asBoolean()) {
				break;
			}
		}

		return node == null ? (methodNode.get(0)) : node;
	}

	/**
	 * 描述：导入csv文件
	 * 2016年5月16日
	 * @param fileName 文件名
	 * @param csvFile csv文件
	 * @param resultInfo 返回信息实体
	 * @param email 用户邮箱
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	private void importCsvFile(String fileName,File csvFile,ImportResultInfo resultInfo,String email,Integer tempalateType) throws IOException{
		Map<String, Object> resultMap = Maps.newHashMap();
		//对应的模板数据
		List<ImportOrderTemplateField> templateFieldList = new ArrayList<ImportOrderTemplateField>();
		Map<String, Object> returnMap = getCsvFileDataAndValidateTemplate(csvFile, resultInfo, templateFieldList, tempalateType);
		if (returnMap == null || returnMap.isEmpty()) {
			return;
		}
		
		//是否为订单(true:订单，false:商品)
		boolean isOrder = (boolean) returnMap.get("isOrder");
		//模板是否正确
		boolean isRight = (boolean) returnMap.get("isRight");
		List<String[]> csvFileData = (List<String[]>) returnMap.get("csvFileData");
		//数据为空（不包括标题）或者模板不正确则结束导入
		if(csvFileData == null || csvFileData.size()<=1 || !isRight){
			return;
		}
		
		//校验并筛选导入数据(除开标题)
		List<TaoBaoOrder> orders = new ArrayList<TaoBaoOrder>();
		List<TaoBaoOrderGoods> goods = new ArrayList<TaoBaoOrderGoods>();
		Logger.info("开始校验并筛选" + (isOrder ? "订单" : "订单商品") + "导入数据==========");
		for (int i = 1,len=csvFileData.size(); i <len; i++) {
			//实体属性名称，实体属性值映射map(例如{"orderNo":"xs1225000"})
			Map<String,String> fieldValueMap = new HashMap<String,String>();
			//校验并筛选订单数据
			if (isOrder) {
				TaoBaoOrder order = new TaoBaoOrder(0,0,email);
				if (tempalateType == 1){
					order.setPlatformid(1);
				} else if(tempalateType == 9) {//针对天猫
					order.setPlatformid(2);
				} else {
					order.setPlatformid(12);
				}

				order.setUpdateDate(new Date());
				//校验订单空值
				boolean isRightForNull = ImportOrderUtils.validateNullForCsv(i, csvFileData.get(i), templateFieldList, resultInfo,fieldValueMap);
				//将订单map映射值转换至订单实体
				order.parseOrderDataFromFieldAndValueMap(fieldValueMap,i,null);
				//针对有省市区三个字段的模板，address重设
				String address = address(order.getProvince(), order.getCity(), order.getArea(), order.getAddress());
				order.setAddress(address);
				boolean existForOrderNo = isExistForOrderNo(orders,order.getOrderNo(), resultInfo,email);
				//校验店铺是否存在  12表示在平台店铺表中为线下店铺
				boolean existForShopNameOfOrder = isExistForShopNameOfOrder(order.getShopName(), email, resultInfo, order.getPlatformid());
				//是否为可添加至数据库的状态（等待卖家发货）
				boolean isRightStatus = (order.getOrderStatus()!=null && order.getOrderStatus().contains("等待卖家发货"));
				if (!isRightStatus) {
					resultInfo.getMessages().add(order.getOrderNo() + "订单状态错误");
				}
				//当店铺存在，空值判断通过，订单不存在于现有系统中时才添加数据
				if (existForShopNameOfOrder && !existForOrderNo && isRightForNull && isRightStatus) {
					orders.add(order);
				}

				if(StringUtils.isNotBlankOrNull(order.getOrderNo())) {
					Pattern p = Pattern.compile("\\s*|\t|\r|\n");
					Matcher m = p.matcher(order.getOrderNo());
					String newOrderNo = m.replaceAll("").replace("=", "");
					order.setOrderNo(newOrderNo.trim());
				}
			}else{//校验并筛选订单商品数据
				TaoBaoOrderGoods good = new TaoBaoOrderGoods(0);
				good.setEmail(email);
				//校验订单商品空值
				boolean isRightForNull = ImportOrderUtils.validateNullForCsv(i, csvFileData.get(i), templateFieldList, resultInfo,fieldValueMap);
				//将map映射值转换至商品实体
				good.parseOrderGoodsDataFromFieldAndValueMap(fieldValueMap);
				//校验是否已经有商品存在于同一订单中（在数据库中或在导入数据中重复）
				boolean existSameSKUInOrder = this.isExistSameSkuInOrder(goods,good,email, resultInfo);

				if(StringUtils.isNotBlankOrNull(good.getOrderNo())) {
					Pattern p = Pattern.compile("\\s*|\t|\r|\n");
					Matcher m = p.matcher(good.getOrderNo());
					String newOrderNo = m.replaceAll("").replace("=", "");
					good.setOrderNo(newOrderNo.trim());
				}


				//当为空校验通过,商品不存在于统一订单中则添加商品数据
				if (isRightForNull && !existSameSKUInOrder) {
					goods.add(good);
				}
			}
		}
		
		Logger.info("校验并筛选" + (isOrder ? "订单" : "订单商品") + "导入数据结束==========");
		//将正确订单数据写入数据库中
		if (isOrder) {
			this.orderMapper.batchInsert(orders);
			Logger.info("将订单数据写入数据库中，共有"+orders.size()+"条数据");
			resultInfo.setFailCount(csvFileData.size()-orders.size());
			resultInfo.setSuccessCount(orders.size());

		}else{
			//将正确的订单商品数据写入到数据库中
			this.orderGoodsMapper.batchInsert(goods);
			Logger.info("将订单商品数据写入数据库中，共有" + goods.size() + "条数据");
			resultInfo.setFailCount(csvFileData.size() - goods.size());
			resultInfo.setSuccessCount(goods.size());
		}

		if (CollectionUtils.isNotEmpty(orders)) {
			eventBus.post(new ImportOrderSyncEvent(orders.get(0).getEmail()));
		}
	}
	
	/**
	 * 描述：
	 * 2016年5月23日
	 * @param csvFile
	 * @param resultInfo
	 * @param templateFieldList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> getCsvFileDataAndValidateTemplate(File csvFile, ImportResultInfo resultInfo,
			List<ImportOrderTemplateField> templateFieldList, Integer tempalateType) {
		//返回数据
		Map<String,Object> returnMap = new HashMap<String,Object>();
		//将文件写入流中并读取所有数据
		CSVReader csvReader = null;
		List<String[]> csvFileData = null;
		try(InputStreamReader isr = new InputStreamReader(new FileInputStream(csvFile), "GBK")) {
			if (isr != null) {
				csvReader = new CSVReader(isr, ',', '\"', 0);// 不跳过表头去除数据中包含的双引号
			}
			if (csvReader != null) {
				csvFileData = csvReader.readAll();
			}
		} catch (Exception e) {
			Logger.info("将文件["+csvFile.getName()+"]写入流失败");
			Logger.info(e.getMessage());
			resultInfo.getMessages().add("导入失败,文件流的读取异常");
			return null;
		} finally {
			if(csvReader!=null){
				try {
					csvReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (csvFileData == null || csvFileData.size()<=0) {
			resultInfo.getMessages().add("导入失败，导入数据为空");
			return null;
		}
		
		//校验csv文件模板标题及模板的正确性
		String[] title = csvFileData.get(0);
		boolean isRight = false;//模板是否正确
		boolean isOrder = true;//是否为订单数据（默认是订单数据）
		List<ImportOrderTemplateField> orderTemplateFieldList = null;
		List<ImportOrderTemplateField> goodTemplateFieldList = null;
		String typeMessage = "";
		//淘宝店铺订单和商品数据字典
		if (tempalateType == 1) {
			orderTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(1);
			goodTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(2);
			typeMessage = "淘宝";
		} else if(tempalateType == 9) {//天猫订单信息和天猫商品信息
			orderTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(9);
			goodTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(10);
			typeMessage = "天猫";
		} else {//线下订单和店铺商品数据字典
			orderTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(3);
			goodTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(4);
			typeMessage = "线下";
		}
		//是否为正确的订单模板(默认正确)
		boolean isRightOrder = true;
		//是否为正确的商品模板（默认正确）
		boolean isRightGood = true;
		if (orderTemplateFieldList!=null&&!orderTemplateFieldList.isEmpty()) {
			Logger.info("开始校验是否为"+typeMessage+"店铺订单模板及标题的正确性，校验信息如下：");
			if (title==null||title.length<orderTemplateFieldList.size()){
				isRightOrder = false;
				resultInfo.getMessages().add(typeMessage+"店铺订单模板不正确,模板中的标题数量不对应");
				Logger.info(typeMessage+"店铺订单模板不正确,模板中的标题数量不对应");
			} else {
				for (ImportOrderTemplateField templateField:orderTemplateFieldList) {
					int position = templateField.getPosition();
					//标题不对应
					if(!templateField.isRightForTemplateName(title[position])){
						isRightOrder = false;
						resultInfo.getMessages().add(typeMessage+"店铺订单模板不正确,模板标题["+title[position]+"]不存在或位置不对");
						Logger.info(typeMessage+"店铺订单模板不正确,模板标题["+title[position]+"]不存在");
						continue;
					}
				}
			}
			
			Logger.info("校验"+typeMessage+"店铺订单模板及标题的正确性结束=======校验结果："+(isRightOrder?"模板正确":"模板不正确"));
			if (isRightOrder) {
				templateFieldList.addAll(orderTemplateFieldList);
				isRight = true;
				Logger.info("当前为订单模板数据");
			}
		}
		
		if (goodTemplateFieldList!=null && !goodTemplateFieldList.isEmpty() && templateFieldList.isEmpty()) {
			Logger.info("开始校验"+typeMessage+"店铺商品模板及标题的正确性==============");
			if (title==null||title.length<goodTemplateFieldList.size()) {
				isRightGood = false;
				resultInfo.getMessages().add(typeMessage+"店铺商品模板不正确,模板中的标题数量不对应");
				Logger.info(typeMessage+"店铺商品模板不正确,模板中的标题数量不对应");
			} else {
				for (ImportOrderTemplateField templateField:goodTemplateFieldList) {
					int position = templateField.getPosition();
					//标题不对应
					if(!templateField.isRightForTemplateName(title[position])){
						isRightGood = false;
						resultInfo.getMessages().add(typeMessage+"店铺商品模板不正确,模板标题["+title[position]+"]不存在");
						Logger.info(typeMessage+"店铺商品模板不正确,模板标题["+title[position]+"]不存在");
						continue;
					}
				}
				Logger.info("校验"+typeMessage+"店铺商品模板及标题的正确性结束=======校验结果："+(isRight?"模板正确":"模板不正确"));
			}
			
			if (isRightGood) {
				resultInfo.getMessages().clear();//这里判断为商品模板正确，上面判断为订单肯定是不符合要求的，所以要清除数据
				templateFieldList.addAll(goodTemplateFieldList);
				isRight = true;
				isOrder = false;
				Logger.info("当前数据为商品模板数据");
			}
		}
		
		//模板是否正确
		returnMap.put("isRight", isRight);
		//模板类型
		returnMap.put("isOrder", isOrder);
		//模板数据
		returnMap.put("csvFileData", csvFileData);
		return returnMap;
	}
	
	@SuppressWarnings("unchecked")
	private List<String[]> getCsvFileDataOfOtherOrder(File csvFile, ImportResultInfo resultInfo,List<ImportOrderTemplateField> templateFieldList){
		// 将文件写入流中并读取所有数据
		CSVReader csvReader = null;
		List<String[]> csvFileData = null;
		try(InputStreamReader isr = new InputStreamReader(new FileInputStream(csvFile), "GBK")) {
			if (isr != null) {
				csvReader = new CSVReader(isr, ',', '\"', 0);// 不跳过表头去除数据中包含的双引号
			}
			if (csvReader != null) {
				csvFileData = csvReader.readAll();
			}
		} catch (Exception e) {
			Logger.info("将文件[" + csvFile.getName() + "]写入流失败");
			Logger.info(e.getMessage());
			resultInfo.getMessages().add("导入失败,文件流的读取异常");
			return null;
		} finally {
			if(csvReader!=null){
				try {
					csvReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (csvFileData == null || csvFileData.size() <= 0) {
			resultInfo.getMessages().add("导入失败，导入数据为空");
			return null;
		}
		
		// 校验csv文件模板标题及模板的正确性
		String[] title = csvFileData.get(0);
		boolean isRight = true;// 模板是否正确
		Logger.info("开始校验订单模板及标题的正确性==============");
		for (ImportOrderTemplateField templateField : templateFieldList) {
			int position = templateField.getPosition();
			// 标题长度小于位置，模板不正确
			if (title == null || title.length < (position + 1)) {
				isRight = false;
				resultInfo.getMessages().add("订单模板不正确,模板中的标题数量不对应");
				break;
			}
			// 标题不对应
			if (!templateField.isRightForTemplateName(title[position])) {
				isRight = false;
				resultInfo.getMessages().add(
						"订单模板不正确,模板标题[" + title[position] + "]不存在");
				continue;
			}
		}
		
		Logger.info("校验线订单模板及标题的正确性结束=======校验结果："
				+ (isRight ? "模板正确" : "模板不正确"));
		if (isRight) {
			return csvFileData;
		}
		return null;
	}
	
	/**
	 * 描述：导入excel文件
	 * 2016年5月16日
	 * @param fileName 文件名
	 * @param excelFile excel文件
	 * @param resultInfo  返回信息实体
	 * @param email 用户邮箱
	 */
	private void importExcelFile(String fileName,File excelFile,ImportResultInfo resultInfo,String email,Integer tempalateType){
		//数据模板字典列表
		List<ImportOrderTemplateField> templateFieldList = new ArrayList<ImportOrderTemplateField>();
		//校验成功，可导入的sheet
		List<Sheet> rightSheetList = new ArrayList<Sheet>();
		Map<String, Object> returnMap = checkExcelTemplate(excelFile, resultInfo, templateFieldList,rightSheetList,tempalateType);
		if (returnMap == null) {
			//结束导入操作
			return;
		}
		
		//是否为订单(true:订单，false:商品)
		boolean isOrder = (boolean) returnMap.get("isOrder");
		boolean isRight = (boolean) returnMap.get("isRight");
		//模板校验失败则结束导入
	    if(!isRight || rightSheetList.isEmpty() || templateFieldList.isEmpty()){
	    	return;
	    }
	    
		//校验并筛选导入数据(除开标题)
		List<TaoBaoOrder> orders = new ArrayList<TaoBaoOrder>();
		List<TaoBaoOrderGoods> goods = new ArrayList<TaoBaoOrderGoods>();
		//总数据量
		int dataCount = 0;
		Logger.info("开始校验并筛选"+(isOrder?"订单":"订单商品")+"导入数据==========");
		for (Sheet rightSheet : rightSheetList) {
			int rowCount = rightSheet.getPhysicalNumberOfRows();
			//统计数据总数
			dataCount += rowCount;
			for(int i = 1; i < rowCount; i++){
				//实体属性名称，实体属性值映射map(例如{"orderNo":"xs1225000"})
				Map<String,String> fieldValueMap = new HashMap<String,String>();
				//校验并筛选订单数据
				if (isOrder) {
					TaoBaoOrder order = new TaoBaoOrder(0,0,email);
					if (tempalateType == 1) {
						order.setPlatformid(1);
					} else if(tempalateType == 9) {//针对天猫
						order.setPlatformid(2);
					}else {
						order.setPlatformid(12);
					}
					order.setUpdateDate(new Date());
					//校验所有属性的空值
					boolean isRightForNull = ImportOrderUtils.validateNullForExcel(i, rightSheet.getRow(i), templateFieldList, resultInfo, fieldValueMap);
					//将订单map映射值转换至订单实体
					order.parseOrderDataFromFieldAndValueMap(fieldValueMap,i,null);
					//针对有省市区三个字段的模板，address重设
					String address = address(order.getProvince(), order.getCity(), order.getArea(), order.getAddress());
					order.setAddress(address);
					boolean existForOrderNo = this.isExistForOrderNo(orders,order.getOrderNo(), resultInfo,email);
					//校验店铺是否存在
					boolean existForShopNameOfOrder = this.isExistForShopNameOfOrder(order.getShopName(), email, resultInfo, order.getPlatformid());
					//是否为可添加至数据库的订单状态（等待卖家发货）
					boolean isRightStatus = (order.getOrderStatus()!=null && order.getOrderStatus().contains("等待卖家发货"));
					if (!isRightStatus) {
						resultInfo.getMessages().add(order.getOrderNo() + "订单状态错误");
					}
					//当店铺存在，空值判断通过，订单不存在于现有系统中时才添加数据
					if (existForShopNameOfOrder && !existForOrderNo && isRightForNull && isRightStatus) {
						orders.add(order);
					}
					if(StringUtils.isNotBlankOrNull(order.getOrderNo())) {
						Pattern p = Pattern.compile("\\s*|\t|\r|\n");
						Matcher m = p.matcher(order.getOrderNo());
						String newOrderNo = m.replaceAll("").replace("=", "");
						order.setOrderNo(newOrderNo.trim());
					}
				}else{
					//校验并筛选订单商品数据
					TaoBaoOrderGoods good = new TaoBaoOrderGoods(0);
					good.setEmail(email);
					boolean isRightForNull = ImportOrderUtils.validateNullForExcel(i, rightSheet.getRow(i), templateFieldList, resultInfo, fieldValueMap);
					//将map映射值转换至商品实体
					good.parseOrderGoodsDataFromFieldAndValueMap(fieldValueMap);
					//校验是否已经有商品存在于同一订单中（在数据库中或在导入数据中重复）
					boolean existSameSKUInOrder = this.isExistSameSkuInOrder(goods,good, email, resultInfo);

					if(StringUtils.isNotBlankOrNull(good.getOrderNo())) {
						Pattern p = Pattern.compile("\\s*|\t|\r|\n");
						Matcher m = p.matcher(good.getOrderNo());
						String newOrderNo = m.replaceAll("").replace("=", "");
						good.setOrderNo(newOrderNo.trim());
					}


					//当为空校验通过,商品不存在于统一订单中则添加商品数据
					if (isRightForNull && !existSameSKUInOrder) {
						goods.add(good);
					}
				}
			}
		}
		
		Logger.info("校验并筛选"+(isOrder?"订单":"订单商品")+"导入数据结束==========");
		//将正确订单数据写入数据库中
		if (isOrder) {
			this.orderMapper.batchInsert(orders);
			Logger.info("将订单数据写入数据库中，共有"+orders.size()+"条数据");
			int failCount = (dataCount==0?0:dataCount-orders.size()-1);
			resultInfo.setFailCount(failCount);
			resultInfo.setSuccessCount(orders.size());

		}else{
			//将正确的订单商品数据写入到数据库中
			this.orderGoodsMapper.batchInsert(goods);
			Logger.info("将订单商品数据写入数据库中，共有"+goods.size()+"条数据");
			int failCount = (dataCount==0?0:dataCount-goods.size()-1);
			resultInfo.setFailCount(failCount);
			resultInfo.setSuccessCount(goods.size());
		}

		if (CollectionUtils.isNotEmpty(orders)) {
			eventBus.post(new ImportOrderSyncEvent(orders.get(0).getEmail()));
		}
	}
	
	/**
	 * 描述：导入其他平台（阿里巴巴，京东，有赞）订单csv文件
	 * 2016年5月16日
	 * @param fileName 文件名
	 * @param csvFile csv文件
	 * @param resultInfo 返回信息实体
	 * @param email 用户邮箱
	 * @param templateType 模板类型
	 * @throws IOException 
	 */
	private void importOtherOrderCsvFile(String fileName, File csvFile, ImportResultInfo resultInfo, String email,Integer tempalateType)
			throws IOException {
		// 数据模板字典列表
		List<ImportOrderTemplateField> templateFieldList = importOrderTemplateFieldMapper.selectByType(tempalateType);
		if (templateFieldList == null || templateFieldList.isEmpty()) {
			resultInfo.getMessages().add("导入失败,系统不存在对应的模板数据字典！");
			return;
		}
		
		List<String[]> csvFileData = getCsvFileDataOfOtherOrder(csvFile, resultInfo, templateFieldList);
		//返回数据为空或者是数据为空（不包括标题）则结束导入
		if(csvFileData == null || csvFileData.size()<=1){
			return;
		}

		// 校验并筛选导入数据(除开标题)
		List<TaoBaoOrder> orders = new ArrayList<TaoBaoOrder>();
		//订单编号，商品列表mapList集合
		Map<String,List<TaoBaoOrderGoods>> orderNo2Goods = new HashMap<String,List<TaoBaoOrderGoods>>();
		Logger.info("开始校验并筛选导入数据==========");
		for (int position = 1,len=csvFileData.size(); position <len; position++) {
			//实体属性名称，实体属性值映射map(例如{"orderNo":"xs1225000"})
			Map<String,String> fieldValueMap = new HashMap<String,String>();
			TaoBaoOrder order = new TaoBaoOrder(0, 0, email);
			order.setPlatformid(tempalateType);
			order.setUpdateDate(new Date());
			//校验订单空值
			boolean isRightForNull = ImportOrderUtils.validateNullForCsv(position, csvFileData.get(position), templateFieldList, resultInfo,fieldValueMap);
			//将订单map映射值转换至订单实体
			order.parseOrderDataFromFieldAndValueMap(fieldValueMap,position,tempalateType);
			//针对有省市区三个字段的模板，address重设
			String address = address(order.getProvince(), order.getCity(),
					order.getArea(), order.getAddress());
			order.setAddress(address);
			if (tempalateType == 5) {//京东的支付交易号默认为平台订单号
				order.setPaymentNo(order.getOrderNo());
			}
			boolean isRightStatus = false;
			String orderStatus = order.getOrderStatus();
			isRightStatus = waiting4Delivery4OtherOrder(tempalateType,orderStatus);

			if(StringUtils.isNotBlankOrNull(order.getOrderNo())) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(order.getOrderNo());
				String newOrderNo = m.replaceAll("").replace("=", "");
				order.setOrderNo(newOrderNo.trim());
			}

			if (isRightStatus) {
				//校验并筛选订单数据
				filterOrderOfOtherOrder(resultInfo, email, orders, order, isRightForNull);
				//校验并筛选订单商品数据
				filterGoodOfOtherOrder(resultInfo, orderNo2Goods, fieldValueMap, isRightForNull,email);
			}
		}
		Logger.info("校验并筛选导入数据结束==========");
		//将正确订单,商品数据写入数据库中
		saveOrderAndGoodOfOtherOrder(resultInfo, orders, orderNo2Goods, csvFileData.size());
	}
	
	/**
	 * 描述：校验京东，阿里巴巴，有赞订单状态，是否处于待发货状态
	 * 2016年5月27日
	 * @param tempalateType
	 * @param orderStatus
	 * @return
	 */
	private boolean waiting4Delivery4OtherOrder(Integer tempalateType,String orderStatus) {
		// 校验京东订单状态是否为"等待发货"
		if (tempalateType.equals(5)&&orderStatus!=null && (orderStatus.contains("等待发货") || orderStatus.contains("等待出库"))) {
			return true;
		}
		// 校验阿里巴巴订单状态是否为"待发货"
		if (tempalateType.equals(6)&&orderStatus!=null && orderStatus.contains("待发货")) {
			return true;
		}
		// 校验有赞订单状态是否为"等待卖家发货"
		if (tempalateType.equals(7)&&orderStatus!=null && orderStatus.contains("等待卖家发货")) {
			return true;
		}
		// 人人店订单
		if (tempalateType.equals(8)&&orderStatus!=null && orderStatus.contains("待发货")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 描述：导入人人店订单excel文件
	 * @param fileName 文件名
	 * @param excelFile excel文件
	 * @param resultInfo  返回信息实体
	 * @param templateType 模板类型
	 * @param email 用户邮箱
	 */
	private void importRRDExcelFile(String fileName, File excelFile, ImportResultInfo resultInfo, String email,Integer tempalateType) {
		// 数据模板字典列表
		List<ImportOrderTemplateField> templateFieldList = importOrderTemplateFieldMapper.selectByType(tempalateType);
		if (templateFieldList == null || templateFieldList.isEmpty()) {
			resultInfo.getMessages().add("导入失败,系统不存在对应的模板数据字典！");
			return;
		}
		
		// 校验excel模板与配置的模板对应得上（位置和标题）
		List<Sheet> rightSheetList = new ArrayList<Sheet>();
		if (!checkExcelTemplateForOtherOrder(excelFile, resultInfo, templateFieldList, rightSheetList)) {
			return;
		}
		
		// tempalateType==8
		// 在配置中的所有字段都可为空
		Logger.info("校验人人店excel的数据-开始");
		for (Sheet rightSheet : rightSheetList) {
			int rowCount = rightSheet.getPhysicalNumberOfRows();
			Row row;
			for (int rowNum = 1; rowNum < rowCount; rowNum++) {
				row = rightSheet.getRow(rowNum);
				String orderNo = ExcelImportUtils.readCellByType(row.getCell(1));
				if (orderNo != null)
					orderNo = orderNo.trim();
				if (StringUtils.isBlankOrNull(orderNo)) {
					// 订单详情
					checkNull4RRD(resultInfo, rowNum, 7, "支付单号", ExcelImportUtils.readCellByType(row.getCell(7)));
					checkNull4RRD(resultInfo, rowNum, 23, "货号", ExcelImportUtils.readCellByType(row.getCell(23)));
					checkNull4RRD(resultInfo, rowNum, 26, "数量", ExcelImportUtils.readCellByType(row.getCell(26)));
					checkNull4RRD(resultInfo, rowNum, 27, "商品单价", ExcelImportUtils.readCellByType(row.getCell(27)));
				} else {
					// 订单主信息
					checkNull4RRD(resultInfo, rowNum, 0, "序号", ExcelImportUtils.readCellByType(row.getCell(0)));
					checkNull4RRD(resultInfo, rowNum, 1, "订单编号", ExcelImportUtils.readCellByType(row.getCell(1)));
					checkNull4RRD(resultInfo, rowNum, 3, "订单金额", ExcelImportUtils.readCellByType(row.getCell(3)));
					checkNull4RRD(resultInfo, rowNum, 4, "支付方式", ExcelImportUtils.readCellByType(row.getCell(4)));
					checkNull4RRD(resultInfo, rowNum, 7, "支付单号", ExcelImportUtils.readCellByType(row.getCell(7)));
					checkNull4RRD(resultInfo, rowNum, 8, "订单状态", ExcelImportUtils.readCellByType(row.getCell(8)));
					checkNull4RRD(resultInfo, rowNum, 13, "买家", ExcelImportUtils.readCellByType(row.getCell(13)));
					checkNull4RRD(resultInfo, rowNum, 15, "收货人", ExcelImportUtils.readCellByType(row.getCell(15)));
					checkNull4RRD(resultInfo, rowNum, 16, "联系电话", ExcelImportUtils.readCellByType(row.getCell(16)));
					checkNull4RRD(resultInfo, rowNum, 18, "省", ExcelImportUtils.readCellByType(row.getCell(18)));
					checkNull4RRD(resultInfo, rowNum, 19, "市", ExcelImportUtils.readCellByType(row.getCell(19)));
					checkNull4RRD(resultInfo, rowNum, 20, "区", ExcelImportUtils.readCellByType(row.getCell(20)));
					checkNull4RRD(resultInfo, rowNum, 21, "地址", ExcelImportUtils.readCellByType(row.getCell(21)));
				}
			}
		}
		if(resultInfo.getMessages().size()>0){
			Logger.info("校验人人店excel的数据-校验不通过，{}",resultInfo);
			return;
		}
		Logger.info("校验人人店excel的数据-校验通过");
		
		
		// 校验并筛选导入数据(除开标题)
		List<TaoBaoOrder> orders = new ArrayList<TaoBaoOrder>();
		// 支付单号=商品列表
		Map<String,List<TaoBaoOrderGoods>> paymentNo2Goods = Maps.newHashMap();
		//总数据量
		int dataCount = 0;
		
		Logger.info("人人店-开始校验并筛选导入数据==========");
		for (Sheet rightSheet : rightSheetList) {
			int rowCount = rightSheet.getPhysicalNumberOfRows();
			// 统计数据总数
			dataCount += rowCount;
			for (int rowNum = 1; rowNum < rowCount; rowNum++) {
				// 实体属性名称，实体属性值映射map(例如{"orderNo":"xs1225000"})
				Map<String, String> fieldValueMap = new HashMap<String, String>();
				TaoBaoOrder order = new TaoBaoOrder(0, 0, email);
				order.setPlatformid(tempalateType);
				order.setUpdateDate(new Date());
				// 校验所有属性的空值：读取一行，变成fieldValueMap里的一条记录
				boolean isRightForNull = ImportOrderUtils.validateNullForExcel(rowNum, rightSheet.getRow(rowNum),
						templateFieldList, resultInfo, fieldValueMap);
				
				// 处理paymentNo数据使用单引号开头问题
				String paymentNo = fieldValueMap.get("paymentNo");
				if(paymentNo!=null){
					fieldValueMap.put("paymentNo", paymentNo.replaceAll("'", ""));
				}
					
				// 将订单map映射值转换至订单实体
				order.parseOrderDataFromFieldAndValueMap(fieldValueMap, rowNum, tempalateType);
				// 针对有省市区三个字段的模板，address重设
				String address = address(order.getProvince(), order.getCity(),
						order.getArea(), order.getAddress());
				order.setAddress(address);
				String orderStatus = order.getOrderStatus();

				if(StringUtils.isNotBlankOrNull(order.getOrderNo())) {
					Pattern p = Pattern.compile("\\s*|\t|\r|\n");
					Matcher m = p.matcher(order.getOrderNo());
					String newOrderNo = m.replaceAll("").replace("=", "");
					order.setOrderNo(newOrderNo.trim());
				}
				// 校验状态
				boolean isRightStatus = waiting4Delivery4OtherOrder(tempalateType,orderStatus);
				if (isRightStatus) {
					// 校验并筛选订单数据
					boolean filterRRDOrderOfOtherOrderResult = filterRRDOrderOfOtherOrder(resultInfo, email, orders, order, isRightForNull);
					if(filterRRDOrderOfOtherOrderResult){
						// 校验并筛选商品数据
						filterGoodOfRRDOrder(resultInfo, paymentNo2Goods, fieldValueMap, isRightForNull, email);
					}
				}
			}
			Logger.info("人人店-校验并筛选导入数据结束==========");
			
			if(orders.size()>0){
				// 将【支付单号=商品列表】转变为【订单编号=商品列表】
				// 订单转为【订单编号=订单】
				Map<String, TaoBaoOrder> paymentNo2Order = orders.stream().collect(Collectors.toMap(TaoBaoOrder::getPaymentNo, Function.identity()));
				Set<TaoBaoOrder> allOrders = new TreeSet<TaoBaoOrder>((o1,o2)->{
					int result1 = o1.getOrderNo().compareTo(o2.getOrderNo());
					if(result1==0){
						return o1.getPaymentNo().compareTo(o2.getPaymentNo());
					}else{
						return result1;
					}
				});
				List<TaoBaoOrderGoods> allGoods = Lists.newArrayList();
				// 给每个商品设置orderNo：商品的支付单号和订单的支付单号一致
				for(Map.Entry<String,List<TaoBaoOrderGoods>> paymentNo2GoodsEntry : paymentNo2Goods.entrySet()){
					List<TaoBaoOrderGoods> goods = paymentNo2GoodsEntry.getValue();
					for(TaoBaoOrderGoods aGood : goods){
						TaoBaoOrder order = paymentNo2Order.get(paymentNo2GoodsEntry.getKey());
						if(order!=null){
							// 给详情设置订单号
							aGood.setOrderNo(order.getOrderNo());
							allOrders.add(order);
							allGoods.add(aGood);// 设置了订单号的才要
						}
					}
				}
				
				// 订单编号=商品列表
				Map<String,List<TaoBaoOrderGoods>> orderNo2Goods = allGoods.stream().collect(Collectors.groupingBy(TaoBaoOrderGoods::getOrderNo));
				
				// 将正确订单,商品数据写入数据库中
				Logger.info("导入人人店的订单数据如下：");
				Logger.info("resultInfo={}",resultInfo);
				Logger.info("orders={}",orders);
				Logger.info("orderNo2Goods={}",orderNo2Goods);
				Logger.info("dataCount={}",dataCount);
				saveOrderAndGoodOfOtherOrder(resultInfo, Lists.newArrayList(allOrders), orderNo2Goods, dataCount);
			}else{
				Logger.info("导入人人店的订单数量为0");
			}
		}
	}
	
	private static void checkNull4RRD(ImportResultInfo resultInfo, int rowNum, int colNum, String cellTitle, String cellVal){
		if(cellVal!=null){
			cellVal = cellVal.trim();
		}
		if(StringUtils.isBlankOrNull(cellVal)){// 支付单号
			resultInfo.getMessages().add("第["+rowNum+"]行["+(colNum+1)+"]列"+cellTitle+"为空");
		}
	}
	
	/**
	 * 描述：导入其他平台（阿里巴巴，有赞，京东）订单excel文件
	 * 2016年5月16日
	 * @param fileName 文件名
	 * @param excelFile excel文件
	 * @param resultInfo  返回信息实体
	 * @param templateType 模板类型
	 * @param email 用户邮箱
	 */
	private void importOtherOrderExcelFile(String fileName, File excelFile, ImportResultInfo resultInfo, String email,Integer tempalateType) {
		// 数据模板字典列表
		List<ImportOrderTemplateField> templateFieldList = importOrderTemplateFieldMapper.selectByType(tempalateType);
		if (templateFieldList == null || templateFieldList.isEmpty()) {
			resultInfo.getMessages().add("导入失败,系统不存在对应的模板数据字典！");
			return;
		}
		
		// 校验
		List<Sheet> rightSheetList = new ArrayList<Sheet>();
		if (!checkExcelTemplateForOtherOrder(excelFile, resultInfo, templateFieldList, rightSheetList)) {
			return;
		}
		
		// 校验并筛选导入数据(除开标题)
		List<TaoBaoOrder> orders = new ArrayList<TaoBaoOrder>();
		// 订单编号=商品列表
		Map<String,List<TaoBaoOrderGoods>> orderNo2Goods = new HashMap<String,List<TaoBaoOrderGoods>>();
		//总数据量
		int dataCount = 0;
		
		Logger.info("开始校验并筛选导入数据==========");
		for (Sheet rightSheet : rightSheetList) {
			int rowCount = rightSheet.getPhysicalNumberOfRows();
			// 统计数据总数
			dataCount += rowCount;
			for (int position = 1; position < rowCount; position++) {
				// 实体属性名称，实体属性值映射map(例如{"orderNo":"xs1225000"})
				Map<String, String> fieldValueMap = new HashMap<String, String>();
				TaoBaoOrder order = new TaoBaoOrder(0, 0, email);
				order.setPlatformid(tempalateType);
				order.setUpdateDate(new Date());
				// 校验所有属性的空值
				boolean isRightForNull = ImportOrderUtils.validateNullForExcel(position, rightSheet.getRow(position),
						templateFieldList, resultInfo, fieldValueMap);
					
				// 将订单map映射值转换至订单实体
				order.parseOrderDataFromFieldAndValueMap(fieldValueMap, position, tempalateType);
				// 针对有省市区三个字段的模板，address重设
				String address = address(order.getProvince(), order.getCity(),
						order.getArea(), order.getAddress());
				order.setAddress(address);
				if (tempalateType == 5) {//京东的支付交易号默认为平台订单号
					order.setPaymentNo(order.getOrderNo());
				}
				String orderStatus = order.getOrderStatus();
				
				// 校验状态
				boolean isRightStatus = waiting4Delivery4OtherOrder(tempalateType,orderStatus);

				if(StringUtils.isNotBlankOrNull(order.getOrderNo())) {
					Pattern p = Pattern.compile("\\s*|\t|\r|\n");
					Matcher m = p.matcher(order.getOrderNo());
					String newOrderNo = m.replaceAll("").replace("=", "");
					order.setOrderNo(newOrderNo.trim());
				}

				if (isRightStatus) {
					// 校验并筛选订单数据
					filterOrderOfOtherOrder(resultInfo, email, orders, order, isRightForNull);
					// 校验并筛选商品数据
					filterGoodOfOtherOrder(resultInfo, orderNo2Goods, fieldValueMap, isRightForNull, email);
				}
			}
			Logger.info("校验并筛选导入数据结束==========");
			// 将正确订单,商品数据写入数据库中
			saveOrderAndGoodOfOtherOrder(resultInfo, orders, orderNo2Goods, dataCount);
		}
	}
	
	private String address(String province, String city, String area, String addressDetail){
		StringBuilder address = new StringBuilder();
		address.append((province == null ? "" : (province + " ")));
		address.append((city == null ? "" : (city + " ")));
		address.append((area == null ? "" : (area + " ")));
		address.append(addressDetail);
		return address.toString();
	}
	
	/**
	 * 校验excel模板与配置的模板对应得上（位置和标题）
	 * 2016年5月23日
	 * @param excelFile
	 * @param resultInfo
	 * @param templateFieldList
	 * @return
	 */
	private boolean checkExcelTemplateForOtherOrder(File excelFile, ImportResultInfo resultInfo,
			List<ImportOrderTemplateField> templateFieldList,List<Sheet> rightSheetList) {
		//将文件写入流中并创建工作簿
		Workbook workbook = null;
		int sheetCount = 0;
		try(FileInputStream fis = new FileInputStream(excelFile)) {
			if(fis !=null){
				workbook = WorkbookFactory.create(fis);// 跳过表头去除数据中包含的双引号
			}
			if (workbook != null) {
				sheetCount = workbook.getNumberOfSheets();
			}
		} catch (Exception e) {
			Logger.info("将文件["+excelFile.getName()+"]写入流失败");
			Logger.info(e.getMessage());
			resultInfo.getMessages().add("导入失败,文件流的读取异常");
			return false;
		}
		
		if (sheetCount <= 0) {
			resultInfo.getMessages().add("导入失败，导入数据为空");
			return false;
		}
		
		// 可能会存在多个sheet
		for (int i = 0; i < sheetCount; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			if(ImportOrderUtils.isRightSheetByFirstRow(sheet, templateFieldList, true)){
				rightSheetList.add(sheet);
			}else{
				resultInfo.getMessages().add("excel sheet["+sheet.getSheetName()+"]不符合模板要求,不导入此数据");
			}
		}
		
		Logger.info("校验模板及标题的正确性结束=======校验结果："+(rightSheetList == null || rightSheetList.isEmpty()?"模板不正确":"模板正确"));
		//没有任何可导入的sheet则结束导入操作，判定为excel模板错误
		if (rightSheetList == null || rightSheetList.isEmpty()) {
			resultInfo.getMessages().add("导入失败，没有符合要求的sheet，模板不正确");
			return false;
		}
		return true;
	}
	
	/**
	 * 描述：线下店铺商品或订单的校验
	 * 2016年5月26日
	 * @param excelFile
	 * @param resultInfo
	 * @param templateFieldList
	 * @param rightSheetList
	 * @return
	 */
	private Map<String,Object> checkExcelTemplate(File excelFile,ImportResultInfo resultInfo,List<ImportOrderTemplateField> templateFieldList,List<Sheet> rightSheetList,Integer tempalateType){
		//返回数据
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Workbook workbook = null;
		int sheetCount = 0;
		try(FileInputStream fis = new FileInputStream(excelFile)) {
			if(fis !=null){
				workbook = WorkbookFactory.create(fis);// 跳过表头去除数据中包含的双引号
			}
			if (workbook != null) {
				sheetCount = workbook.getNumberOfSheets();
			}
		} catch (Exception e) {
			Logger.info("将文件["+excelFile.getName()+"]写入流失败");
			Logger.info(e.getMessage());
			resultInfo.getMessages().add("导入失败,文件流的读取异常");
			return null;
		}
		
		if (sheetCount <= 0) {
			resultInfo.getMessages().add("导入失败，导入数据为空");
			return null;
		}
		
		List<ImportOrderTemplateField> orderTemplateFieldList = null;
		List<ImportOrderTemplateField> goodTemplateFieldList = null;
		//线下或淘宝店铺订单模板
		if (tempalateType == 1) {
			orderTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(1);
			goodTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(2);
		} else if(tempalateType == 9) {//天猫订单信息和商品信息模板
			orderTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(9);
			goodTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(10);
		} else {//线下店铺或淘宝商品模板
			orderTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(3);
			goodTemplateFieldList = this.importOrderTemplateFieldMapper.selectByType(4);
		}
		
		//模板是否正确(默认正确)
		boolean isRight = true;
		// 是否为订单数据(默认为订单)
		boolean isOrder = true;
		for (int i = 0; i < sheetCount; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			if(ImportOrderUtils.isRightSheetByFirstRow(sheet, orderTemplateFieldList,true)){
				//校验是否为订单模板
				rightSheetList.add(sheet);
				templateFieldList.addAll(orderTemplateFieldList);
				isOrder =true;
				Logger.info("当前为订单模板数据");
				continue;
			} else if(ImportOrderUtils.isRightSheetByFirstRow(sheet, goodTemplateFieldList,false)){
				//校验是否为商品模板
				rightSheetList.add(sheet);
				templateFieldList.addAll(goodTemplateFieldList);
				isOrder = false;
				Logger.info("当前为商品模板数据");
				continue;
			} else{
				resultInfo.getMessages().add("excel sheet["+sheet.getSheetName()+"]不符合模板要求,不导入此数据");
			}
		}
		
		Logger.info("校验模板及标题的正确性结束=======校验结果："+(rightSheetList == null || rightSheetList.isEmpty()?"模板不正确":"模板正确"));
		//没有任何可导入的sheet则结束导入操作，判定为excel模板错误
		if (rightSheetList == null || rightSheetList.isEmpty()) {
			resultInfo.getMessages().add("导入失败，没有符合要求的sheet，模板不正确");
			isRight = false;
		}
	    returnMap.put("isRight", isRight);
	    returnMap.put("isOrder", isOrder);
		return returnMap;
	}
	
	/**
	 * 描述：获取模板标题
	 * 2016年5月26日
	 * @param fileName
	 * @return
	 */
	private String[] getExportTemplateTitleByFileName(String fileName){
		if(Strings.isNullOrEmpty(fileName)){
			return new String[]{};
		}
		
		Map<String, Integer> map = Maps.newHashMap();
		map.put("淘宝订单信息模板", 1);
		map.put("淘宝商品信息模板", 2);
		map.put("线下店铺订单模板", 3);
		map.put("线下店铺商品模板", 4);
		map.put("京东订单模板", 5);
		map.put("阿里巴巴订单模板", 6);
		map.put("有赞订单模板", 7);
		map.put("人人店订单模板", 8);
		map.put("天猫订单信息模版", 9);
		map.put("天猫商品信息模版", 10);

		Integer type = map.get(fileName);
		
		if(type==null) {
			return new String[] {};
		}

		return ImportOrderUtils.getTemplateTitleByTemplateField(importOrderTemplateFieldMapper.selectByType(type));
	}
	
	/**
	 * 描述：校验店铺在用户中是否存在
	 * 2016年5月17日
	 * @param shopName 店铺名称
	 * @param email 用户邮箱
	 * @param platformId 店铺平台id
	 * @return
	 */
	private boolean isExistForShopNameOfOrder(String shopName, String email,ImportResultInfo resultInfo,Integer platformId) {
		if (Strings.isNullOrEmpty(email)) {
			resultInfo.getMessages().add("校验店铺数据中传入邮箱有误!");
			return false;
		}
		
		boolean isExistFlag = false;
		if (!Strings.isNullOrEmpty(shopName)) {
			String resultString = HttpUtil.post("{\"email\":\"" + email + "\",\"type\":\"" + platformId + "\",\"shopName\":\"" + shopName + "\",\"remoteFlag\":\"" + IDUtils.getUUID().substring(0, 6) + "\"}", HttpUtil.B2BBASEURL+"/member/checkShopName");
			JsonNode platformShopJson = null;
			if (resultString != null && !"".equals(resultString)) {
				platformShopJson = Json.parse(resultString);
				Logger.info("检验店铺的标识1-------->" + Json.toJson(platformShopJson));
			}
			if (platformShopJson != null && platformShopJson.get("suc") != null){
				isExistFlag = platformShopJson.get("suc").asBoolean();
			} else {
				isExistFlag = false;
			}
		}else{
			isExistFlag = true;
		}
		if (!isExistFlag) {
			resultInfo.getMessages().add("该店铺["+shopName+"]不存在!");
		}
		return isExistFlag;
	}
	
	/**
	 * 描述：平台订单号是否已经存在(在数据库或在导入数据中)
	 * 2016年5月17日
	 * @param orders 待添加的所有导入订单
	 * @param orderNo 平台订单编号
	 * @param email 当前用户邮箱（当传入邮箱为空时校验（系统中所有订单）是否存在，不为空时校验（当前用户的所有订单）是否存在
	 * 2016/6/24  改为email不可能为空，所有的订单（线上线下）都要验证email这个字段的唯一性
	 * @return
	 */
	private boolean isExistForOrderNo(List<TaoBaoOrder> orders,String orderNo,ImportResultInfo resultInfo,String email){
		if (Strings.isNullOrEmpty(orderNo)) {
			resultInfo.getMessages().add("校验平台订单号时传入订单号有误!");
			return false;
		}
		
		List<TaoBaoOrder> ordersByOrderNo = this.orderMapper.selectByOrderNoAndEmail(orderNo, email);
		boolean isExistOrderNo = false;
		if (ordersByOrderNo!=null && ordersByOrderNo.size()>0) {
			for (TaoBaoOrder tb : ordersByOrderNo) {
				//平台订单号已经存在于数据库中且未生成订单则不能导入
				isExistOrderNo = tb.getIsDeleted().equals(0);
				if (isExistOrderNo) {
					resultInfo.getMessages().add("平台订单号在系统中["+orderNo+"]已经存在!");
					break;
				}
			}
		}
		
		//订单号是否重复
		if (orders!=null && orders.size()>0) {
			for (TaoBaoOrder order:orders) {
				if(orderNo.equals(order.getOrderNo())){
					isExistOrderNo = true;
					break;
				}
			}
		}
		return isExistOrderNo;
	}
	
	/**
	 * 描述：是否有相同的sku存在于同一平台订单中(在数据库或在导入数据中)
	 * 2016年5月17日
	 * @param goods 待导入的订单商品
	 * @param orderNo
	 * @param sku
	 * @return
	 */
	private boolean isExistSameSkuInOrder(List<TaoBaoOrderGoods> goods,TaoBaoOrderGoods orderGoods,String email,ImportResultInfo resultInfo){
		if (orderGoods==null || (Strings.isNullOrEmpty(orderGoods.getOrderNo())||Strings.isNullOrEmpty(orderGoods.getSku()))) {
			resultInfo.getMessages().add("校验商品sku时传入订单号或sku有误!");
			return false;
		}
		
		String orderNo = orderGoods.getOrderNo().replace("=", "");
		String sku = orderGoods.getSku();
		//在导入数据中的同一订单中商品是否重复,重复的就合并操作
		Integer firstAmount = orderGoods.getAmount();
		//获取系统中已经存在的商品
		List<TaoBaoOrderGoods> orderGoodsList = this.orderGoodsMapper.queryGoodsByCondition(orderNo, sku,email);
		boolean isExistSku = (orderGoodsList!=null && orderGoodsList.size()>0); 
		if (isExistSku) {
			resultInfo.getMessages().add("商品sku["+sku+"]在系统订单["+orderNo+"]中已存在!");
		}
		if (goods!=null && goods.size()>0) {
			for (TaoBaoOrderGoods good:goods) {
				if (orderNo.equals(good.getOrderNo())&&sku.equals(good.getSku())) {
					isExistSku = true;
					Logger.info("商品sku["+sku+"]在订单["+orderNo+"]中重复,已合并!");
					good.setAmount((Integer)(firstAmount.intValue()+good.getAmount().intValue()));
					break;
				}
			}
		}
		return isExistSku;
	}
	
	/**
	 * 描述：
	 * 2016年5月24日
	 * @param resultInfo
	 * @param email
	 * @param orders
	 * @param order
	 * @param isRightForNull
	 */
	private void filterOrderOfOtherOrder(ImportResultInfo resultInfo, String email, List<TaoBaoOrder> orders,
			TaoBaoOrder order, boolean isRightForNull) {
		//校验订单号是否已经存在（在数据库或在数据中重复）
		boolean existForOrderNo = isExistForOrderNo(orders,order.getOrderNo(), resultInfo,email);
		//当店铺存在，空值判断通过，订单不存在于现有系统中时才添加数据
		if (!existForOrderNo && isRightForNull) {
			orders.add(order);
		}
	}
	
	/**
	 * 描述：
	 * 2016年5月24日
	 * @param resultInfo
	 * @param goodMapList
	 * @param fieldValueMap
	 * @param isRightForNull
	 */
	private void filterGoodOfOtherOrder(ImportResultInfo resultInfo, Map<String, List<TaoBaoOrderGoods>> goodMapList,
			Map<String, String> fieldValueMap, boolean isRightForNull,String email) {
		TaoBaoOrderGoods good = new TaoBaoOrderGoods(0);
		//将map映射值转换至商品实体
		good.parseOrderGoodsDataFromFieldAndValueMap(fieldValueMap);
		good.setEmail(email);

		if(StringUtils.isNotBlankOrNull(good.getOrderNo())) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(good.getOrderNo());
			String newOrderNo = m.replaceAll("").replace("=","");
			good.setOrderNo(newOrderNo.trim());
		}

		//校验是否已经有商品存在于同一订单中（在数据库中或在导入数据中重复）
		boolean existSameSKUInOrder = isExistSameSkuInOrder(goodMapList.get(good.getOrderNo()),good, email, resultInfo);
		//当为空校验通过,商品不存在于统一订单中则添加商品数据
		if (isRightForNull && !existSameSKUInOrder) {
			List<TaoBaoOrderGoods> goods = goodMapList.get(good.getOrderNo());
			if (goods == null) {
				goods = new ArrayList<TaoBaoOrderGoods>();
				goodMapList.put(good.getOrderNo(), goods);
			}
			goods.add(good);
		}
	}
	
	/**
	 * 是否存在相同订单号的
	 * @param resultInfo
	 * @param email
	 * @param orders
	 * @param order
	 * @param isRightForNull
	 * @return 为false时，说明已存在此order，添加进orders里失败
	 */
	private boolean filterRRDOrderOfOtherOrder(ImportResultInfo resultInfo, String email, List<TaoBaoOrder> orders,
			TaoBaoOrder order, boolean isRightForNull) {
		// 订单编号不为空的才是订单主信息，否则就是订单详情
		if(StringUtils.isNotBlankOrNull(order.getOrderNo())){
			List<TaoBaoOrder> orderExists = orderMapper.selectByOrderNoAndEmail(order.getOrderNo(), email);
			if(orderExists.size()>0){
				resultInfo.getMessages().add("已存在订单["+order.getOrderNo()+"]");
				return false;
			}
			orders.add(order);
			return true;
		}
		return true;
	}
	
	/**
	 * 描述：
	 * 2016年5月24日
	 * @param resultInfo
	 * @param paymentNo2Goods
	 * @param fieldValueMap
	 * @param isRightForNull
	 */
	private void filterGoodOfRRDOrder(ImportResultInfo resultInfo, Map<String, List<TaoBaoOrderGoods>> paymentNo2Goods,
			Map<String, String> fieldValueMap, boolean isRightForNull,String email) {
		if(fieldValueMap.get("orderNo")==null){// orderNo为空的才是订单详情
			TaoBaoOrderGoods good = new TaoBaoOrderGoods(0);
			//将map映射值转换至商品实体
			good.parseOrderGoodsDataFromFieldAndValueMap(fieldValueMap);
			good.setEmail(email);
			String paymentNo = fieldValueMap.get("paymentNo");
			//当为空校验通过,商品不存在于统一订单中则添加商品数据
			if (isRightForNull) {
				List<TaoBaoOrderGoods> goods = paymentNo2Goods.get(paymentNo);
				if (goods == null) {
					goods = new ArrayList<TaoBaoOrderGoods>();
					paymentNo2Goods.put(paymentNo, goods);
				}
				goods.add(good);
			}
		}
	}
	
	/**
	 * 描述：
	 * 2016年5月24日
	 * @param resultInfo
	 * @param orders
	 * @param goodMapList
	 * @param dataCount
	 */
	private void saveOrderAndGoodOfOtherOrder(ImportResultInfo resultInfo, List<TaoBaoOrder> orders,
			Map<String, List<TaoBaoOrderGoods>> goodMapList, int dataCount) {
		if (orders!=null && !orders.isEmpty()) {
			orderMapper.batchInsert(orders);
			Logger.info("将订单数据写入数据库中，共有"+orders.size()+"条数据");
		}
		
		//所有的订单商品列表集合
		Collection<List<TaoBaoOrderGoods>> goodsCollection = goodMapList.values();
		if(goodsCollection!=null && !goodsCollection.isEmpty()){
			List<TaoBaoOrderGoods> goods = new ArrayList<TaoBaoOrderGoods>();
			for (Iterator<List<TaoBaoOrderGoods>> it = goodsCollection.iterator(); it.hasNext();) {
				goods.addAll(it.next());
			}
			//将正确的订单商品数据写入到数据库中
			orderGoodsMapper.batchInsert(goods);
			Logger.info("将订单商品数据写入数据库中，共有" + goods.size() + "条数据");
		}


		if (CollectionUtils.isNotEmpty(orders)) {
			eventBus.post(new ImportOrderSyncEvent(orders.get(0).getEmail()));
		}

		int failCount = (dataCount==0?0:dataCount-orders.size()-1);
		resultInfo.setFailCount(failCount);
		resultInfo.setSuccessCount(CollectionUtils.isEmpty(orders)?0:orders.size());
	}
	
}
