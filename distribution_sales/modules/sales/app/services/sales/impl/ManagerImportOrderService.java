package services.sales.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import play.Logger;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;
import services.sales.IHttpService;
import services.sales.IManagerImportOrderService;
import services.sales.IProductExpirationDateService;
import util.sales.ExcelImportUtils;
import util.sales.FileUtils;
import util.sales.RegexUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import dto.sales.SelectedProduct;

public class ManagerImportOrderService implements IManagerImportOrderService {
	@Inject
	private IHttpService httpService;
	@Inject
	IProductExpirationDateService productExpirationDateService;

	@Override
	public Map<String, Object> importSalesOrder(List<FilePart> files,
			Map<String, String[]> params) {
		// 请求返回结果map
		String fileID = params.containsKey("id") ? params.get("id")[0] : null;
		String md5 = params.containsKey(fileID + "_md5") ? params.get(fileID
				+ "_md5")[0] : null;
		Logger.debug("importSalesOrder    params----->{}", params);
		boolean permitted = files != null && files.size() > 0;
		if (!permitted) {
			return createResult(false, "导入失败，没有选择导入文件或系统错误");
		}

		// 导入结果信息列表
		FilePart file = files.get(0);
		String fileName = file.getFilename();
		boolean isExcelFile = FileUtils.isExcelFile(fileName);
		if (!isExcelFile) {
			return createResult(false, "请导入excel文件");
		}

		File origin = file.getFile();
		String fileMD5;
		try {
			fileMD5 = DigestUtils.md5Hex(new FileInputStream(origin));
			Logger.debug("后台录入发货单importSalesOrder，原始md5={}，后台计算的md5={}", md5,
					fileMD5);
		} catch (Exception e) {
			Logger.info("后台导入发货单，获取文件的md5失败，{}", e);
			return createResult(false, "导入失败");
		}

		if (!(fileMD5 != null && fileMD5.equals(md5))) {
			return createResult(false, "excel文件破损，请重新导入");
		}

		// 验证数据完整性
		Map<String, Object> validateResultMap = Maps.newHashMap();
		if (!validateContents(origin, validateResultMap)) {
			return validateResultMap;
		}

		return doImportSalesOrder(origin);
	}

	private Map<String, Object> doImportSalesOrder(File excelFile) {
		Map<String, Object> resultMap = Maps.newLinkedHashMap();
		// 将文件写入流中并创建工作簿
		Workbook workbook = null;
		try (FileInputStream fis = new FileInputStream(excelFile);) {
			workbook = WorkbookFactory.create(fis);// 跳过表头去除数据中包含的双引号
		} catch (Exception e) {
			resultMap.put("suc", false);
			resultMap.put("msg", "导入失败");
			return resultMap;
		}
		
		Sheet sheet = workbook.getSheetAt(0);
		Row firstRow = sheet.getRow(0);
		String email = ExcelImportUtils.readCellByType(firstRow.getCell(1));
		
		List<Map<String,Object>> proList = Lists.newArrayList();
		List<Map<String,Object>> giftList = Lists.newArrayList();
		// 验证商品内容
		int rowCount = sheet.getPhysicalNumberOfRows();
		for (int rowNum = 3; rowNum < rowCount; rowNum++) {
			Row row = sheet.getRow(rowNum);
//			String seq = ExcelImportUtils.readCellByType(row.getCell(0));
			if(row!=null){
				// 正价商品
				String proSku = ExcelImportUtils.readCellByType(row.getCell(1));
				if(StringUtils.isNotEmpty(proSku)){
					Map<String,Object> proMap = Maps.newHashMap();
					//是否选择到期日期
					boolean proNeedExpirationDate = "是".equals(ExcelImportUtils.readCellByType(row.getCell(2)));
					proMap.put("sku", proSku);// 正价商品编号
					proMap.put("needExpirationDate", proNeedExpirationDate);
					proMap.put("isgift", false);
					// 不需要到期日期，需要商品数量
					if (!proNeedExpirationDate) {
						proMap.put("qty", Double.valueOf(ExcelImportUtils.readCellByType(row.getCell(3))).intValue());
					}
					proList.add(proMap);
				}
				
				// 赠品
				String giftSku = ExcelImportUtils.readCellByType(row.getCell(4));
				if(StringUtils.isNotEmpty(giftSku)){
					Map<String,Object> giftMap = Maps.newHashMap();
					//是否选择到期日期
					boolean giftNeedExpirationDate = "是".equals(ExcelImportUtils.readCellByType(row.getCell(5)));
					giftMap.put("sku", giftSku);// 赠品编号
					giftMap.put("needExpirationDate", giftNeedExpirationDate);
					giftMap.put("isgift", true);
					// 不需要到期日期，需要商品数量
					if (!giftNeedExpirationDate) {
						giftMap.put("qty", Double.valueOf(ExcelImportUtils.readCellByType(row.getCell(6))).intValue());
					}
					
					giftList.add(giftMap);
				}
			}
		}
		
		return setProductProperties(email, proList, giftList);
	}
	
	/**
	 * 设置商品的具体属性
	 * @param email 分销商
	 * @param filteredProList 导入的正价商品
	 * @param filteredGiftList 导入的赠品
	 * @return
	 */
	private Map<String, Object> setProductProperties(String email,
			List<Map<String, Object>> proList,
			List<Map<String, Object>> giftList) {
		Map<String, Object> resultMap = Maps.newHashMap();
		
		// 正价商品里不能有重复的sku
		List<Map<String, Object>> filteredProList = filterSameSkus(proList);
		// 赠品里不能有重复的sku
		List<Map<String, Object>> filteredGiftList = filterSameSkus(giftList);
		
		// 1、获取分销商
		JsonNode dismemberNode = null;
		try {
			dismemberNode = httpService.getDismemberByEmail(email);
		} catch (IOException e) {
			Logger.info("获取分销商失败,{}",e);
			return createResult(false, "导入失败");
		}
		// 获取失败
		if(Objects.isNull(dismemberNode) || !dismemberNode.get("suc").asBoolean()){
			String msg = (dismemberNode!=null && dismemberNode.has("result"))?dismemberNode.get("result").asText():"导入失败";
			return createResult(false, msg);
		}
		
		JsonNode dismemberResultNode = dismemberNode.get("result");
		int comsumerType = dismemberResultNode.get("comsumerType").asInt();
		int distributionMode = dismemberResultNode.get("distributionMode").asInt();
		resultMap.put("comsumerType", comsumerType);
		resultMap.put("distributionMode", distributionMode);
		
		// 2、获取商品信息
		Set<String> skuSet = Sets.newHashSet();
		for(Map<String, Object> proMap : filteredProList){
			skuSet.add(proMap.get("sku").toString());
		}
		for(Map<String, Object> giftMap : filteredGiftList){
			skuSet.add(giftMap.get("sku").toString());
		}
		if(skuSet.size()==0){
			return createResult(false, "没有要导入的商品");
		}
		JsonNode productsNode = null;
		try {
			// 只要深圳仓的
			productsNode = httpService.getProducts(email, Lists.newArrayList(skuSet), 2024, distributionMode);
		} catch (IOException e) {
			Logger.info("获取商品信息失败,{}",e);
			return createResult(false, "导入失败");
		}
		if(productsNode==null){
			Logger.info("获取商品信息为null");
			return createResult(false, "导入失败");
		}
		
		JsonNode productResultNode = productsNode.get("data").get("result");
		Map<String,JsonNode> sku2ProductNode = Maps.newHashMap();
		for(Iterator<JsonNode> it = productResultNode.iterator();it.hasNext();){
			JsonNode productNode = it.next();
			sku2ProductNode.put(productNode.get("csku").asText(), productNode);
		}
		
		// 如果所有的sku都不存在
		if(sku2ProductNode.size()==0){
			Logger.info("导入发货单，所有录入的sku在系统中不存在");
			return createResult(false, "所有SKU未匹配到商品，请检查SKU填写是否有误");
		}
		
		// 设置正价商品属性，同时区分哪些要设置到期日期，哪些不要
		Set<String> fakeSkuSet = Sets.newHashSet();
		List<Map<String,Object>> setExpirDateProList = Lists.newArrayList();
		List<Map<String,Object>> notSetExpirDateProList = Lists.newArrayList();
		for(Map<String, Object> proMap : filteredProList){
			String sku = proMap.get("sku").toString();
			JsonNode productNode = sku2ProductNode.get(sku);
			if(productNode!=null){// 可能输入的sku不存在
				proMap.put("batchNumber", productNode.get("batchNumber").asInt());
				proMap.put("title", productNode.get("ctitle").asText());
				proMap.put("interBarCode", productNode.get("interBarCode").asText());
				proMap.put("warehouseName", productNode.get("warehouseName").asText());
				proMap.put("warehouseId", productNode.get("warehouseId").asInt());
				proMap.put("stock", productNode.get("stock").asInt());
				proMap.put("microStock", productNode.get("microStock").asInt());
				boolean needExpirationDate = (boolean) proMap.get("needExpirationDate");
				if(needExpirationDate){// 需要到期日期
					proMap.put("subStock", 0);
					proMap.put("subMicroStock", 0);
					// excel中是没有填数量的，使用起批量作为数量
					proMap.put("qty", productNode.get("batchNumber").asInt());
				}else{
					proMap.put("subStock", productNode.get("stock").asInt());
					proMap.put("subMicroStock", productNode.get("microStock").asInt());
				}
				proMap.put("price", productNode.get("disPrice").asDouble());
				proMap.put("marketPrice", productNode.get("localPrice").asDouble());
				proMap.put("imgUrl", productNode.get("imageUrl").asText());
				
				if(needExpirationDate){
					setExpirDateProList.add(proMap);
				}else{
					notSetExpirDateProList.add(proMap);
				}
			}else{// 不存在的sku
				fakeSkuSet.add(sku);
			}
		}
		
		// 设置赠品属性，同时区分哪些要设置到期日期，哪些不要
		List<Map<String,Object>> setExpirDateGiftList = Lists.newArrayList();
		List<Map<String,Object>> notsetExpirDateGiftList = Lists.newArrayList();
		for(Map<String, Object> giftMap : filteredGiftList){
			String sku = giftMap.get("sku").toString();
			JsonNode productNode = sku2ProductNode.get(sku);
			if(productNode!=null){// 可能输入的sku不存在
				giftMap.put("batchNumber", productNode.get("batchNumber").asInt());
				giftMap.put("title", productNode.get("ctitle").asText());
				giftMap.put("interBarCode", productNode.get("interBarCode").asText());
				giftMap.put("warehouseName", productNode.get("warehouseName").asText());
				giftMap.put("warehouseId", productNode.get("warehouseId").asInt());
				giftMap.put("stock", productNode.get("stock").asInt());
				giftMap.put("microStock", productNode.get("microStock").asInt());
				boolean needExpirationDate = (boolean) giftMap.get("needExpirationDate");
				if(needExpirationDate){// 需要到期日期
					giftMap.put("subStock", 0);
					giftMap.put("subMicroStock", 0);
					// excel中是没有填数量的，使用起批量作为数量
					giftMap.put("qty", productNode.get("batchNumber").asInt());
				}else{
					giftMap.put("subStock", productNode.get("stock").asInt());
					giftMap.put("subMicroStock", productNode.get("microStock").asInt());
				}
				// 赠品的价格为0
				giftMap.put("price", 0.00);
				giftMap.put("marketPrice", productNode.get("localPrice").asDouble());
				giftMap.put("imgUrl", productNode.get("imageUrl").asText());
				
				if(needExpirationDate){
					setExpirDateGiftList.add(giftMap);
				}else{
					notsetExpirDateGiftList.add(giftMap);
				}
			}else{// 不存在的sku
				fakeSkuSet.add(sku);
			}
		}
		
		Logger.info("导入发货单-需要设置到期日期的正价商品--->{}",setExpirDateProList);
		Logger.info("导入发货单-不需要设置到期日期的正价商品--->{}",notSetExpirDateProList);
		Logger.info("导入发货单-需要设置到期日期的赠品--->{}",setExpirDateGiftList);
		Logger.info("导入发货单-不需要设置到期日期的赠品--->{}",notsetExpirDateGiftList);
		// 执行完上面之后，总共分为4个list：
		// 需要设置到期日期的正价商品setExpirDateProList、不需要设置到期日期的正价商品notSetExpirDateProList
		// 需要设置到期日期的赠品setExpirDateGiftList、不需要设置到期日期的赠品notsetExpirDateGiftList
		
		// 设置到期日期
		// 正价商品
		List<SelectedProduct> proSetExpirDateList = setExpirationDates(email, setExpirDateProList);
		// 赠品
		List<SelectedProduct> giftSetExpirDateList = setExpirationDates(email, setExpirDateGiftList);
		
		resultMap.put("suc", true);
		resultMap.put("msg", fakeSkuSet.size()==0?"导入成功":"["+String.join(",", fakeSkuSet)+"]未匹配到商品，系统自动将其过滤，请检查SKU填写是否有误");
		resultMap.put("email", email);
		resultMap.put("comsumerType", comsumerType);
		resultMap.put("distributionMode", distributionMode);
		resultMap.put("proSetExpirDateList", proSetExpirDateList);
		resultMap.put("proNotSetExpirDateList", notSetExpirDateProList);
		resultMap.put("giftSetExpirDateList", giftSetExpirDateList);
		resultMap.put("giftNotSetExpirDateList", notsetExpirDateGiftList);
		resultMap.put("isPackageMail", dismemberNode.get("result").get("isPackageMail").asInt());
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	private List<SelectedProduct> setExpirationDates(String email, List<Map<String, Object>> setExpirDateProductList) {
		if(setExpirDateProductList==null || setExpirDateProductList.size()==0){
			return Lists.newArrayList();
		}
		Map<String,Object> proParams = Maps.newHashMap();
		proParams.put("selectedProducts", setExpirDateProductList);
		proParams.put("email", email);
		Map<String, Object> prosSetExpirDateResultMap = productExpirationDateService.setCloudSelectedProductsExpirationDates(Json.toJson(proParams));
		return (List<SelectedProduct>) prosSetExpirDateResultMap.get("result");
	}

	/**
	 * 过滤重复的
	 * @param list
	 * @return
	 */
	private List<Map<String,Object>> filterSameSkus(List<Map<String, Object>> list){
		Map<String, Map<String, Object>> filteredMap = Maps.newLinkedHashMap();
		for(Map<String, Object> map : list){
			filteredMap.put(map.get("sku").toString(), map);
		}
		return Lists.newArrayList(filteredMap.values());
	}

	/**
	 * 验证内容完整性
	 * @param excelFile
	 * @param validateResultMap
	 * @return
	 */
	private boolean validateContents(File excelFile,
			Map<String, Object> validateResultMap) {
		// 将文件写入流中并创建工作簿
		Workbook workbook = null;
		try (FileInputStream fis = new FileInputStream(excelFile);) {
			workbook = WorkbookFactory.create(fis);// 跳过表头去除数据中包含的双引号
		} catch (Exception e) {
			validateResultMap.put("suc", false);
			validateResultMap.put("msg", "导入失败");
			return false;
		}
		
		if (workbook.getNumberOfSheets() == 0) {
			validateResultMap.put("suc", false);
			validateResultMap.put("msg", "导入内容为空");
			return false;
		}

		Sheet sheet = workbook.getSheetAt(0);
		Row firstRow = sheet.getRow(0);
		String emailTitle = ExcelImportUtils.readCellByType(firstRow.getCell(0));
		String email = ExcelImportUtils.readCellByType(firstRow.getCell(1));
		// 验证email
		if (StringUtils.isEmpty(email)) {
			validateResultMap.put("suc", false);
			validateResultMap.put("msg", emailTitle + "不能为空");
			return false;
		}

		// 验证商品内容
		int rowCount = sheet.getPhysicalNumberOfRows();
		if(rowCount-3 <= 0){
			validateResultMap.put("suc", false);
			validateResultMap.put("msg", "没有要导入的商品");
			return false;
		}
		int validatedRow = 0;
		for (int rowNum = 3; rowNum < rowCount; rowNum++) {
			Row row = sheet.getRow(rowNum);
			if(row!=null){
				String proSku = ExcelImportUtils.readCellByType(row.getCell(1));
				String giftSku = ExcelImportUtils.readCellByType(row.getCell(4));
				if(StringUtils.isEmpty(proSku) && StringUtils.isEmpty(giftSku)){
					validateResultMap.put("suc", false);
					validateResultMap.put("msg", (rowNum + 1) + "行，正价商品编号/赠品编号不能为空");
					return false;
				}
				
				// 正价商品不一定有，判断依据：正价商品sku是否存在
				if(StringUtils.isNotEmpty(proSku)){
					// 是否选择到期日期一定不能为空，是否输入对了值
					String proExpirationDateFlag = ExcelImportUtils.readCellByType(row.getCell(2));
					if(!isExpirationDateValueRight(proExpirationDateFlag)){
						validateResultMap.put("suc", false);
						validateResultMap.put("msg", "模板没有确定商品是否需要选择到期日期，请重新填写后再次导入");
						return false;
					}
					
					// 不需要到期日期，需要商品数量
					if ("否".equals(proExpirationDateFlag)) {
						String proQty = ExcelImportUtils.readCellByType(row.getCell(3));
						int indexOf = proQty.indexOf(".00");
						if(indexOf>=0){
							proQty = proQty.substring(0, indexOf);
						}
						if (!isQtyValueRight(proQty)) {
							validateResultMap.put("suc", false);
							validateResultMap.put("msg", "到期日期为否的商品，请填写商品数量");
							return false;
						}
					}
				}
				
				// 赠品不一定有，判断依据：赠品sku是否存在
				if(StringUtils.isNotEmpty(giftSku)){
					// 是否选择到期日期一定不能为空，是否输入对了值
					String giftExpirationDateFlag = ExcelImportUtils.readCellByType(row.getCell(5));
					if(!isExpirationDateValueRight(giftExpirationDateFlag)){
						validateResultMap.put("suc", false);
						validateResultMap.put("msg", "模板没有确定商品是否需要选择到期日期，请重新填写后再次导入");
						return false;
					}
					
					// 不需要到期日期，需要商品数量
					if ("否".equals(giftExpirationDateFlag)) {
						String giftQty = ExcelImportUtils.readCellByType(row.getCell(6));
						if (StringUtils.isEmpty(giftQty)) {
							validateResultMap.put("suc", false);
							validateResultMap.put("msg", "到期日期为否的商品，请填写商品数量");
							return false;
						}
					}
				}
				validatedRow++;
			}
		}
		if(validatedRow==0){
			validateResultMap.put("suc", false);
			validateResultMap.put("msg", "没有要导入的正价商品");
			return false;
		}
		return true;
	}
	
	/**
	 * 不能为空，且值只能为 是/否
	 * @param str
	 * @return
	 */
	private boolean isExpirationDateValueRight(String str){
		return "是".equals(str) || "否".equals(str);
	}
	
	/**
	 * 不能为空，且为正整数
	 * @param str
	 * @return
	 */
	private boolean isQtyValueRight(String str){
		return StringUtils.isNotEmpty(str) && RegexUtil.IsIntNumber(str);
	}

	private Map<String, Object> createResult(boolean suc, String msg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("suc", suc);
		resultMap.put("msg", msg);
		return resultMap;
	}
}
