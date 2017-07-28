package controllers.purchase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.purchase.IDisQuotationService;
import services.purchase.IUserService;
import utils.purchase.ExportUtil;
import utils.purchase.HttpUtil;
import annotation.ALogin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.purchase.Constant;
import dto.purchase.QuotationToExcelDto;
import dto.purchase.QuotationToExcelIterm;
import dto.purchase.ReqBodyIterm;
import dto.purchase.ReturnMess;
import entity.purchase.DisQuotation;

/**
 * 报价单导出 Created by luwj on 2016/3/4.
 */
public class DisQuotationController extends Controller {

	@Inject private IDisQuotationService disQuotationService;
	@Inject private IUserService userService;

	/**
	 * 报价单记录列表展示
	 * 
	 * @return
	 */
	public Result getRecord() {
		JsonNode node = request().body().asJson();
		Logger.debug(">>>getRecord>>>node>>" + node.toString());
		return ok(disQuotationService.getRecord(node));
	}

	/**
	 * 生成记录
	 * 
	 * @return
	 */
	@ALogin
	public Result saveRecord() {
		Logger.debug(">>>>>>>>>>>>" + request().body().toString());
		JsonNode node = request().body().asJson();
		String madeUser = userService.getAdminAccount();

		((ObjectNode) node).put("madeUser", madeUser);
		Logger.debug(">>>saveRecord>>>node>>" + node.toString());
		return ok(disQuotationService.saveRecord(node));
	}

	/**
	 * 下载分销商报价单
	 * 
	 * @return
	 */
	@ALogin
	public Result exportQuotation() {
		String id = request().getQueryString("id");
		if (StringUtils.isBlank(id)) {
			Logger.info("请求参数不存在或格式错误");
			return ok(Json.toJson(new ReturnMess("1", "请求参数不存在或格式错误")));
		}
		
		DisQuotation disQuotation = disQuotationService.getDisQuotationById(Integer.parseInt(id));
		if (disQuotation == null) {
			Logger.info("查无记录");
			return ok(Json.toJson(new ReturnMess("1", "查无记录")));
		}
		try {
			ObjectMapper obj = new ObjectMapper();
			String[] header = null;
			String[] iidList = null;
			List<Map<String, String>> skuawhid = null;
			String excelName = "";
			String discountRate = "";// 折扣
			
			String reqBody = disQuotation.getReqBody();
			Logger.debug(">>exportQuotation>>>reqBody>>>" + reqBody);
			if (StringUtils.isNotBlank(reqBody)) {
				ReqBodyIterm reqIterm = obj.readValue(reqBody,ReqBodyIterm.class);
				header = reqIterm.getHeader();
				iidList = reqIterm.getIidList();
				skuawhid = reqIterm.getSkuawhid();
				excelName = reqIterm.getExcelName();
				discountRate = reqIterm.getDiscountRate();
				Logger.debug("exportQuotation    skuawhid--->" + Json.toJson(skuawhid).toString());
			}
			
			if (header == null || iidList == null
					|| StringUtils.isBlank(discountRate)
					|| StringUtils.isBlank(excelName)) {
				Logger.info("请求参数不存在或格式错误");
				return ok(Json.toJson(new ReturnMess("1", "请求参数不存在或格式错误")));
			}
			
			Logger.info("下载分销商报价单   header--->"
					+ Json.toJson(header).toString());
			response().setContentType("application/vnd.ms-excel;charset=utf-8");
			response().setHeader(
					"Content-disposition",
					"attachment;filename="
							+ new String(excelName.getBytes(), "ISO8859-1")
							+ ".xls");

			// 查询商品
			String reqStr = "{\"data\":{\"iids\":"
					+ Json.toJson(iidList).toString() + "}}";
			Logger.debug(">>>>>exportQuotation>>>reqStr>>>" + reqStr);
			String returnStr = HttpUtil.post(reqStr, HttpUtil.B2BBASEURL
					+ "/product/api/getProducts");

			// 解析
			String itermStr = obj.readTree(returnStr).get("data").toString();
			Logger.debug(">>exportQuotation>>>>itermStr>>>" + itermStr);
			QuotationToExcelIterm iterm = obj.readValue(itermStr,
					QuotationToExcelIterm.class);

			// 更新excel内容
			List<QuotationToExcelDto> dtos = iterm.getResult();
			// sku+仓库ID为key，数量为value
			Map<String, Integer> skuQty = Maps.newHashMap();
			for (Map<String, String> skuMap : skuawhid) {
				skuQty.put(skuMap.get("sku") + "|" + skuMap.get("warehouseId"),
						Integer.valueOf(skuMap.get("qty")));
			}
			// add by zbc 导出内容
			List<QuotationToExcelDto> exlDto = Lists.newArrayList();
			Integer qty = 0;
			for (QuotationToExcelDto dto : dtos) {
				qty = skuQty.get(dto.getCsku() + "|" + dto.getWarehouseId());
				if (qty != null && qty > 0) {
					Double disPriceCalculated = dto.calculateDisPrice(Double
							.parseDouble(discountRate));
					Logger.info("{},{}计算出来的分销价是{}",dto.getCsku(),dto.getWarehouseId(),String.valueOf(disPriceCalculated));
					if (disPriceCalculated != null) {
						dto.setDisPrice(disPriceCalculated);
						dto.setQty(qty);
						exlDto.add(dto);
					}
				}
			}
			
			disQuotation.setExcelInfo(Json.toJson(exlDto).toString());

			disQuotationService.updateByIdSelective(disQuotation);
			
			return ok(ExportUtil.export(excelName + ".xls", header,
					Constant.EXPORT_QUOTATION_MAP, exlDto));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			Logger.error(">>exportQuotation>>JsonProcessingException>>", e);
			return ok(Json.toJson(new ReturnMess("1", "系统异常")));
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(">>exportQuotation>>Exception>>", e);
			return ok(Json.toJson(new ReturnMess("1", "系统异常")));
		}
	}

	/**
	 * 复制操作
	 * 
	 * @return
	 */
	@ALogin
	public Result copyDisQuo() {
		JsonNode reqBody = request().body().asJson();
		boolean condition = reqBody.has("id")
				&& reqBody.has("excelName")
				&& StringUtils.isNotBlank(reqBody.get("id").asText())
				&& StringUtils.isNotBlank(reqBody.get("excelName").asText());
		if (!condition) {
			return ok(Json.toJson(new ReturnMess("1", "请求参数不存在或格式错误")).toString());
		}
		
		String id = reqBody.get("id").asText();
		String excelName = reqBody.get("excelName").asText();
		DisQuotation disQuotation = disQuotationService.getDisQuotationById(Integer.parseInt(id));
		if (disQuotation == null) {
			return ok(Json.toJson(new ReturnMess("1", "请求数据不存在")).toString());
		}
		
		String reqStr = disQuotation.getReqBody();
		reqStr = reqStr.replace(disQuotation.getExcelName(), excelName);
		disQuotation.setReqBody(reqStr);
		disQuotation.setId(null);
		disQuotation.setExcelName(excelName);
		disQuotation.setDisEmail(null);
		disQuotation.setDisname(null);
		disQuotation.setCreateDate(new Date());
		disQuotation.setMadeUser(userService.getAdminAccount());
		disQuotation.setIsBuildOrder(false);
		disQuotationService.addDisQuotation(disQuotation);
		return ok(Json.toJson(new ReturnMess("0",
				String.valueOf(disQuotation.getId()))).toString());
	}

	/**
	 * 报价单Excel(xls、xlsx)文件上传
	 * 
	 * @return
	 */
	public Result uploadQuotationExcelFile() {
		String xls_content_type = "application/vnd.ms-excel";
		String xlsx_content_type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		MultipartFormData mFD = request().body().asMultipartFormData();
		if (mFD == null){
			return ok("false");
		}
		
		Logger.info("uploadQuotationExcelFile   mFD--->" + Json.toJson(mFD).toString());
		List<FilePart> fileParts = mFD.getFiles();
		ArrayNode nodes = JsonNodeFactory.instance.arrayNode();
		if (fileParts != null && fileParts.size() > 0) {
			for (FilePart filePart : fileParts) {
				String contentType = filePart.getContentType();
				String fileName = filePart.getFilename();
				File fileBody = filePart.getFile();
				if (xls_content_type.equals(contentType)
						|| xlsx_content_type.equals(contentType)) {
					String suffix = fileName.endsWith(".xls") ? "xls"
							: fileName.endsWith(".xlsx") ? "xlsx" : null;
					if (suffix == null) {
						nodes = this.readQuotationExcelFile(fileBody, suffix);
					} else {
						Logger.info("uploadQuotationExcel：只允许上传后缀为xls或xlsx的Excel文件，当前文件名"
								+ fileName);
					}
				} else {
					Logger.info("uploadQuotationExcel：当前上传文件不是Excel文件，当前文件名"
							+ fileName);
				}
			}
		}
		
		Logger.info("uploadQuotationExcelFile   nodes--->" + nodes.toString());
		return ok(nodes);
	}

	/**
	 * 读取Excel文件中的目标数据
	 * 
	 * @return
	 */
	private ArrayNode readQuotationExcelFile(File file, String fileSuffix) {
		ArrayNode nodes = JsonNodeFactory.instance.arrayNode();
		
		if("xls".equals(fileSuffix)){
			nodes = readXLS(file);
		}else if("xlsx".equals(fileSuffix)){
			nodes = readXLSX(file);
		}
		return nodes;
	}

	private ArrayNode readXLSX(File file) {
		ArrayNode nodes = JsonNodeFactory.instance.arrayNode();
		
		try (InputStream is = new FileInputStream(file)) {
			XSSFWorkbook workbook2007 = new XSSFWorkbook(is);
			if (workbook2007 != null
					&& workbook2007.getNumberOfSheets() > 0) {
				for (int m = 0; m < workbook2007.getNumberOfSheets(); m++) {
					XSSFSheet sheet = workbook2007.getSheetAt(m);
					if (sheet == null || sheet.getLastRowNum() == 0)
						continue;
					// XSSFSheet.getLastRowNum()返回值为从0开始的最大行号
					Logger.info("readQuotationExcelFile XSSFSheet--->"
							+ sheet.getLastRowNum());
					for (int n = 0; n <= sheet.getLastRowNum(); n++) {
						XSSFRow row = sheet.getRow(n);
						// 过滤掉表格标题行和空行
						if (n == 0 || row == null
								|| row.getLastCellNum() == 0)
							continue;
						Logger.info("readQuotationExcelFile XSSFRow--->"
								+ row.getLastCellNum());
						ObjectNode node = Json.newObject();
						int key = 0;
						for (int i = 0; i < row.getLastCellNum(); i++) {
							XSSFCell cell = row.getCell(i);
							if (cell == null)
								continue;
							key++;
							node.put("" + key, this.gainCellText(cell));
						}
						nodes.add(node);
					}
				}
			}
			if(workbook2007 != null){
				workbook2007.close();
			}
		} catch (IOException e) {
			Logger.info("读取Excel文件中的目标数据失败");
			e.printStackTrace();
		}
		
		return nodes;
	}

	private ArrayNode readXLS(File file) {
		ArrayNode nodes = JsonNodeFactory.instance.arrayNode();
		
		try (InputStream is = new FileInputStream(file)) {
			HSSFWorkbook workbook2003 = new HSSFWorkbook(is);
			if (workbook2003 != null
					&& workbook2003.getNumberOfSheets() > 0) {
				for (int m = 0; m < workbook2003.getNumberOfSheets(); m++) {
					HSSFSheet sheet = workbook2003.getSheetAt(m);
					if (sheet == null || sheet.getLastRowNum() == 0)
						continue;
					// HSSFSheet.getLastRowNum()返回值为从0开始的最大行号
					Logger.info("readQuotationExcelFile HSSFSheet--->"
							+ sheet.getLastRowNum());
					for (int n = 0; n <= sheet.getLastRowNum(); n++) {
						HSSFRow row = sheet.getRow(n);
						// 过滤掉表格标题行和空行
						if (n == 0 || row == null
								|| row.getLastCellNum() == 0)
							continue;
						Logger.info("readQuotationExcelFile HSSFRow--->"
								+ row.getLastCellNum());
						ObjectNode node = Json.newObject();
						int key = 0;
						for (int i = 0; i < row.getLastCellNum(); i++) {
							HSSFCell cell = row.getCell(i);
							if (cell == null)
								continue;
							// 当前行的最后一格为目标数据
							key++;
							node.put("" + key, this.gainCellText(cell));
						}
						nodes.add(node);
					}
				}
			}
			if(workbook2003 != null){
				workbook2003.close();
			}
		} catch (IOException e) {
			Logger.info("读取Excel文件中的目标数据失败");
			e.printStackTrace();
		}
		
		return nodes;
	}

	/**
	 * 获取Excel表格单元格中数据
	 * 
	 * @return
	 */
	private String gainCellText(Cell cell) {
		String cellText;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:// 数值型
			if (DateUtil.isCellDateFormatted(cell)) {
				cellText = new DateTime(cell.getDateCellValue())
						.toString("yyyy-MM-dd HH:mm:ss");
			} else {
				cellText = String.valueOf(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:// 字符串型
			cellText = cell.getRichStringCellValue().toString();
			break;
		case Cell.CELL_TYPE_FORMULA:// 公式型
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
					.getCreationHelper().createFormulaEvaluator();
			cellText = evaluator.evaluateInCell(cell).getStringCellValue();
			break;
		case Cell.CELL_TYPE_BLANK:// 空值
			cellText = "";
			break;
		case Cell.CELL_TYPE_BOOLEAN:// 布尔型
			cellText = String.valueOf(cell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:// 错误
			cellText = null;
			break;
		default:
			cellText = null;
			break;
		}
		Logger.info("gainCellText cellText--->"
				+ Json.toJson("" + cellText).toString());
		return cellText;
	}

	/**
	 * 分销商生成采购单 更新报价单状态
	 * 
	 * @return
	 */
	@ALogin
	public Result buildOrder() {
		JsonNode main = request().body().asJson();
		if (main == null || !main.has("id")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disQuotationService.buildOrder(main)));
	}

}
