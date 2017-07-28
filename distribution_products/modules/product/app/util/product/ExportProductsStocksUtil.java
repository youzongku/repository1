package util.product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.common.collect.Maps;

import dto.product.ProductLite;
import play.Logger;
/**
 * 导出商品库存工具类
 * @author huangjc
 * @date 2017年2月21日
 */
public class ExportProductsStocksUtil {

	/**
	 * 服务器临时目录
	 */
	private static String filePath = "/tmp/";

	
	/**
	 * @param fileName
	 *            文件名
	 * @param header
	 *            excel列头
	 * @param headerMapping
	 *            列头与显示的中文映射
	 * @param skuWarehouseId2ProList
	 *            数据
	 * @return
	 */
	public static File export(String fileName, Map<String, List<ProductLite>> skuWarehouseId2ProList) {
		Logger.info("导出时间start：" + new Date());
		HSSFWorkbook workBook = new HSSFWorkbook();// 创建一个工作簿
		HSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		Map<String, HSSFCellStyle> cellStyleMap = getCellStyleMap(workBook);
		
		// 类目、品牌、SKU、商品名称、国际条码、规格、箱规、保质期、到期时间、库存数量、库存箱数   共11列
		
//		CellRangeAddress  对象的构造方法需要传入合并单元格的首行、最后一行、首列、最后一列。
		CellRangeAddress firstTitleCellRangeAddress = new CellRangeAddress(0, 0, 0, 2);
		sheet.addMergedRegion(firstTitleCellRangeAddress);
		HSSFRow headRow = sheet.createRow(0);
		HSSFCell headCell = headRow.createCell(0);
		headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
		// 设置样式
		headCell.setCellStyle(cellStyleMap.get("cellStyle"));
		headCell.setCellValue("商品库存表");
		
		// 表头
		String[] headers = {"类目", "品牌", "SKU", "商品名称", "国际条码", "规格", "箱规", "保质期", "到期时间", "库存数量", "库存箱数"};
		int[] columnWidths = {4000, 6000, 3000, 12000, 4000, 4000, 4000, 3000, 4000, 3000, 3000};
		HSSFRow titleRow = sheet.createRow(1);
		HSSFCellStyle titleCellstyle = cellStyleMap.get("titleCellstyle");
		for (int j = 0; j < headers.length; j++) {
			HSSFCell titleCell = titleRow.createCell(j);
			// 设置单元格格式
			sheet.setColumnWidth(j, columnWidths[j]);
			titleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			titleCell.setCellStyle(titleCellstyle);
			// 将数据填入单元格
			titleCell.setCellValue(headers[j]);
		}
		
		insertContent(sheet, cellStyleMap, skuWarehouseId2ProList);
		
		// 生成文件
		return createExcel(fileName, workBook);
	}

	/**
	 * 插入商品信息
	 * @param sheet
	 * @param cellStyleMap
	 * @param moDtoList
	 */
	private static void insertContent(HSSFSheet sheet, Map<String, HSSFCellStyle> cellStyleMap, Map<String, List<ProductLite>> skuWarehouseId2ProList){
		HSSFCellStyle dataCellStyle = cellStyleMap.get("dataCellStyle");
		
		// 没数据的
		if (skuWarehouseId2ProList==null || skuWarehouseId2ProList.size()==0) {
			insertLastRow4SubTotal(sheet, 2, skuWarehouseId2ProList, dataCellStyle);
			return;
		}
		
		// 先按商品分类进行分组
		Map<String, List<ProductLite>> listGroupByCategory = skuWarehouseId2ProList.values().stream()
				.flatMap(plList -> plList.stream()).collect(Collectors.groupingBy(e -> {
					String groupStr = "";
					if (e.getCategoryId()!=null) {
						groupStr = String.valueOf(e.getCategoryId());
					}
					return groupStr;
				}));
		
		// 从第4行开始是具体数据了
		int rowNum = 2;
		for (Map.Entry<String, List<ProductLite>> entry : listGroupByCategory.entrySet()) {
			List<ProductLite> sameCategoryProList = entry.getValue();
			// 再按品牌进行分组
			Map<String, List<ProductLite>> listGroupByBrand = sameCategoryProList.stream().collect(Collectors.groupingBy(e -> {
						String groupStr = "";
						if (StringUtils.isNotBlank(e.getBrand())) {
							groupStr = e.getBrand();
						}
						return groupStr;
					}));
			
			for (Map.Entry<String, List<ProductLite>> subentry : listGroupByBrand.entrySet()) {
				List<ProductLite> sameBrandProList = subentry.getValue();
				if (sameBrandProList!=null) {
					// 再根据sku&warehouseId进行分组
					Map<String, List<ProductLite>> skuWarehouseId2Pl = sameBrandProList.stream().collect(Collectors.groupingBy(pl->{
						return String.join("_", pl.getCsku(), String.valueOf(pl.getWarehouseId()));
					}));
					
					for (Map.Entry<String, List<ProductLite>> entry_skuWarehouseId2Pl : skuWarehouseId2Pl.entrySet()) {
						List<ProductLite> list = entry_skuWarehouseId2Pl.getValue();
						addMergedRegions(sheet, rowNum, list);
						
						for (ProductLite productLite : list) {
							insertOneProWithExpirationDate(sheet, dataCellStyle, rowNum, productLite);
							rowNum++;
						}
					}
				}
			}
		}
		
		insertLastRow4SubTotal(sheet, rowNum, skuWarehouseId2ProList, dataCellStyle);
	}

	private static void insertOneProWithExpirationDate(HSSFSheet sheet, HSSFCellStyle dataCellStyle, int rowNum,
			ProductLite productLite) {
		// 创建1行数据行
		HSSFRow dataRow = sheet.createRow(rowNum);
		// 类目、品牌、SKU、商品名称、国际条码、规格、箱规、保质期、到期时间、库存数量、库存箱数
		HSSFCell cellCname = setCellStyle(dataRow.createCell(0), dataCellStyle);
		HSSFCell cellBrand = setCellStyle(dataRow.createCell(1), dataCellStyle);
		HSSFCell cellSku = setCellStyle(dataRow.createCell(2), dataCellStyle);
		HSSFCell cellCtitle = setCellStyle(dataRow.createCell(3), dataCellStyle);
		HSSFCell cellInterBarCode = setCellStyle(dataRow.createCell(4), dataCellStyle);
		HSSFCell cellPlugType = setCellStyle(dataRow.createCell(5), dataCellStyle);
		HSSFCell cellPackQty = setCellStyle(dataRow.createCell(6), dataCellStyle);
		HSSFCell cellExpirationDays = setCellStyle(dataRow.createCell(7), dataCellStyle);
		HSSFCell cellExpirationDate = setCellStyle(dataRow.createCell(8), dataCellStyle);
		HSSFCell cellStock = setCellStyle(dataRow.createCell(9), dataCellStyle);
		HSSFCell cellBoxes = setCellStyle(dataRow.createCell(10), dataCellStyle);

		cellCname.setCellValue(getStringValue(productLite.getCname()));
		cellBrand.setCellValue(getStringValue(productLite.getBrand()));
		cellSku.setCellValue(getStringValue(productLite.getCsku()));
		cellCtitle.setCellValue(getStringValue(productLite.getCtitle()));
		cellInterBarCode.setCellValue(getStringValue(productLite.getInterBarCode()));
		cellPlugType.setCellValue(getStringValue(productLite.getPlugType()));
		Integer packQty = productLite.getPackQty();// 箱规
		// 不正常箱规数据处理
		if (packQty == null || packQty.intValue() == 0) {
			packQty = 1;
		}
		cellPackQty.setCellValue(packQty);
		Integer expirationDays = productLite.getExpirationDays() == null ? 0 : productLite.getExpirationDays();
		cellExpirationDays.setCellValue(expirationDays);// 保质期
		cellExpirationDate.setCellValue(getStringValue(productLite.getExpirationDate()));
		int stock = productLite.getStock() == null ? 0 : productLite.getStock();
		cellStock.setCellValue(stock);
		// 计算库存箱数
		// 四舍五入
		int boxes = 0;
		if (stock>0) {
			boxes = new BigDecimal(stock).divide(new BigDecimal(packQty),0,BigDecimal.ROUND_HALF_UP).intValue();
		}
		cellBoxes.setCellValue(boxes);// 库存箱数
	}

	/**
	 * 合并单元格
	 * @param sheet
	 * @param rowNum
	 * @param proList
	 */
	private static void addMergedRegions(HSSFSheet sheet, int rowNum, List<ProductLite> proList) {
		int lastRow = proList.size()+rowNum-1;
		int firstRow = rowNum;
//		Logger.info("firstRow={}, lastRow={}",firstRow,lastRow);
		CellRangeAddress cellRangeAddress1 = new CellRangeAddress(firstRow, lastRow, 0, 0);
		CellRangeAddress cellRangeAddress2 = new CellRangeAddress(firstRow, lastRow, 1, 1);
		CellRangeAddress cellRangeAddress3 = new CellRangeAddress(firstRow, lastRow, 2, 2);
		CellRangeAddress cellRangeAddress4 = new CellRangeAddress(firstRow, lastRow, 3, 3);
		CellRangeAddress cellRangeAddress5 = new CellRangeAddress(firstRow, lastRow, 4, 4);
		CellRangeAddress cellRangeAddress6 = new CellRangeAddress(firstRow, lastRow, 5, 5);
		CellRangeAddress cellRangeAddress7 = new CellRangeAddress(firstRow, lastRow, 6, 6);
		CellRangeAddress cellRangeAddress8 = new CellRangeAddress(firstRow, lastRow, 7, 7);
		sheet.addMergedRegion(cellRangeAddress1);
		sheet.addMergedRegion(cellRangeAddress2);
		sheet.addMergedRegion(cellRangeAddress3);
		sheet.addMergedRegion(cellRangeAddress4);
		sheet.addMergedRegion(cellRangeAddress5);
		sheet.addMergedRegion(cellRangeAddress6);
		sheet.addMergedRegion(cellRangeAddress7);
		sheet.addMergedRegion(cellRangeAddress8);
	}
	
	/**
	 * 最后一行是统计
	 * @param sheet
	 * @param dataCellStyle
	 * @param totalStock
	 * @param totalBoxes
	 * @param rowNum
	 */
	private static void insertLastRow4SubTotal(HSSFSheet sheet, int rowNum,
			Map<String, List<ProductLite>> skuWarehouseId2ProList, HSSFCellStyle dataCellStyle) {
		// 商品库存 汇总
		HSSFRow dataRow = sheet.createRow(rowNum);
		HSSFCell cell1 = setCellStyle(dataRow.createCell(0), dataCellStyle);
		HSSFCell cell2 = setCellStyle(dataRow.createCell(1), dataCellStyle);
		HSSFCell cell3 = setCellStyle(dataRow.createCell(2), dataCellStyle);
		HSSFCell cell4 = setCellStyle(dataRow.createCell(3), dataCellStyle);
		HSSFCell cell5 = setCellStyle(dataRow.createCell(4), dataCellStyle);
		HSSFCell cell6 = setCellStyle(dataRow.createCell(5), dataCellStyle);
		HSSFCell cell7 = setCellStyle(dataRow.createCell(6), dataCellStyle);
		HSSFCell cell8 = setCellStyle(dataRow.createCell(7), dataCellStyle);
		HSSFCell cell9 = setCellStyle(dataRow.createCell(8), dataCellStyle);
		HSSFCell cellTotalStock = setCellStyle(dataRow.createCell(9), dataCellStyle);
		HSSFCell cellTotalBoxes = setCellStyle(dataRow.createCell(10), dataCellStyle);
		cell1.setCellValue("商品库存 汇总");
		cellTotalStock.setCellValue(getTotalStock(skuWarehouseId2ProList));
		cellTotalBoxes.setCellValue(getTotalBoxes(skuWarehouseId2ProList));
	}
	
	/**
	 * 总云仓库存
	 * @param skuWarehouseId2ProList
	 * @return 返回大于等于0的数字
	 */
	private static int getTotalStock(Map<String, List<ProductLite>> skuWarehouseId2ProList){
		if(skuWarehouseId2ProList==null || skuWarehouseId2ProList.size()==0){
			return 0;
		}
		int totalStock = 0;
		for (Map.Entry<String, List<ProductLite>> entry : skuWarehouseId2ProList.entrySet()) {
			List<ProductLite> proList = entry.getValue();
			if (proList!=null) {
				for (ProductLite productLite : proList) {
					int stock = productLite.getStock()==null ? 0 : productLite.getStock();
					totalStock += stock;
				}
			}
		}
		return totalStock;
	}
	
	/**
	 * 总箱数
	 * @param skuWarehouseId2ProList
	 * @return 返回大于等于0的数字
	 */
	private static int getTotalBoxes(Map<String, List<ProductLite>> skuWarehouseId2ProList){
		if(skuWarehouseId2ProList==null || skuWarehouseId2ProList.size()==0){
			return 0;
		}
		int totalBoxes = 0;
		for (Map.Entry<String, List<ProductLite>> entry : skuWarehouseId2ProList.entrySet()) {
			List<ProductLite> proList = entry.getValue();
			if (proList!=null) {
				for (ProductLite productLite : proList) {
					Integer packQty = productLite.getPackQty();
					int stock = productLite.getStock()==null ? 0 : productLite.getStock();
					// 不正常箱规数据处理
					if (packQty==null || packQty.intValue()==0) {
						packQty = 1;
					}
					// 计算库存箱数
					int boxes = new BigDecimal(stock).divide(new BigDecimal(packQty),0,BigDecimal.ROUND_HALF_UP).intValue();
					totalBoxes += boxes;
				}
			}
		}
		return totalBoxes;
	}
	
	private static HSSFCell setCellStyle(HSSFCell cell, HSSFCellStyle dataCellStyle){
		cell.setCellStyle(dataCellStyle);
		return cell;
	}
	
	/**
	 * 样式，最上边的标题是cellStyle，表头的是titleCellstyle，具体数据的是dataCellStyle
	 * @param workBook
	 * @return
	 */
	private static Map<String,HSSFCellStyle> getCellStyleMap(HSSFWorkbook workBook){
		Map<String,HSSFCellStyle> map = Maps.newHashMap();
		
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		HSSFFont font = workBook.createFont();
		font.setColor(HSSFColor.RED.index);
		cellStyle.setFont(font);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中  
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		map.put("cellStyle", cellStyle);
		
		// 表头样式
		HSSFCellStyle titleCellstyle = workBook.createCellStyle();
		HSSFFont titleCellFont = workBook.createFont();
		titleCellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		titleCellstyle.setFont(titleCellFont);
		titleCellstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中  
		titleCellstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直 
		map.put("titleCellstyle", titleCellstyle);
		
		// 具体数据样式
		HSSFCellStyle dataCellStyle = workBook.createCellStyle();
		HSSFFont dataCellFont = workBook.createFont();
		dataCellStyle.setFont(dataCellFont);
		dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中  
		dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直 
		dataCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		dataCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
		dataCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		dataCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		map.put("dataCellStyle", dataCellStyle);
		return map;
	}
	
	/**
	 * 
	 * @param fileName
	 * @param workBook
	 * @return
	 */
	private static File createExcel(String fileName, HSSFWorkbook workBook) {
		FileOutputStream fos = null;
		File file = null;
		try {
			File path = new File(filePath);
			if (!path.isDirectory() && !path.exists()) {
				path.mkdir();
			}
			String name = filePath + "Export-" + new Date().getTime() + "-" + fileName;
			Logger.info("execl导出临时路径:"+name);
			file = new File(name);
			file.createNewFile();
			fos = new FileOutputStream(file);
			workBook.write(fos);
			Logger.info("-----生成Excel成功-----");
		} catch (Exception e) {
			Logger.error(e + "");
			return null;
		} finally {
			try {
				if (fos!=null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				Logger.error(e + "");
				return null;
			}
		}
		Logger.info("导出时间end：" + new Date());
		return file;
	}
	
	/**
	 * 
	 * @param value
	 * @return 如果value为null，就返回""
	 */
	private static String getStringValue(String value){
		return StringUtils.isNotEmpty(value)?value:"";
	}

}
