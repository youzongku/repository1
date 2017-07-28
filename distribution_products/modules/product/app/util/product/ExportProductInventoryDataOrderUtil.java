package util.product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.common.collect.Maps;

import dto.ProdcutInventoryDataExportDto;
import play.Logger;
/**
 * 导出营销单工具类
 * @author huangjc
 * @date 2017年2月21日
 */
public class ExportProductInventoryDataOrderUtil {

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
	 * @param data
	 *            数据
	 * @return
	 */
	public static File export(String fileName, List<ProdcutInventoryDataExportDto> productDtoList) {
		Logger.info("导出时间start：" + new Date());
		HSSFWorkbook workBook = new HSSFWorkbook();// 创建一个工作簿
		HSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		Map<String, HSSFCellStyle> cellStyleMap = getCellStyleMap(workBook);
		
		// sku，商品名称，过期日期，箱规，规格，箱数，数量，总计数   共8列
		
//		CellRangeAddress  对象的构造方法需要传入合并单元格的首行、最后一行、首列、最后一列。
		CellRangeAddress firstTitleCellRangeAddress = new CellRangeAddress(0, 1, 0, 7);
		sheet.addMergedRegion(firstTitleCellRangeAddress);
		HSSFRow headRow = sheet.createRow(0);
		HSSFCell headCell = headRow.createCell(0);
		headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
		// 设置样式
		headCell.setCellStyle(cellStyleMap.get("cellStyle"));
		headCell.setCellValue("商品库存信息");
		
		// 表头
		String[] headers = { "sku", "商品名称", "过期日期", "箱规", "规格", "箱数", "数量","总计数"};
		int[] columnWidths = {3000, 6000, 6000, 6000, 3000, 8000, 3000, 3000};
		HSSFRow titleRow = sheet.createRow(2);
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
		
		insertMarketingOrders(sheet, cellStyleMap, productDtoList);
		
		// 生成文件
		return createExcel(fileName, workBook);
	}

	/**
	 * 插入商品信息
	 * @param sheet
	 * @param cellStyleMap
	 * @param moDtoList
	 */
	private static void insertMarketingOrders(HSSFSheet sheet, Map<String, HSSFCellStyle> cellStyleMap, List<ProdcutInventoryDataExportDto> productList){
		// 从第4行开始是具体数据了
		int rowNum = 3;
		int totalQty=productList.size();
		for (int i = 0; i < productList.size(); i++) {
			ProdcutInventoryDataExportDto productData = productList.get(i);
			
			HSSFCellStyle dataCellStyle = cellStyleMap.get("dataCellStyle");
			insertEachRowData(sheet, dataCellStyle, rowNum+i ,
					productData, totalQty);
		}
		
	}
	
	private static void insertEachRowData(HSSFSheet sheet, HSSFCellStyle dataCellStyle,  int rowNum, 
			ProdcutInventoryDataExportDto productData, int totalQty) {
		// 创建1行数据行
		HSSFRow dataRow = sheet.createRow(rowNum);

		HSSFCell cell1 = dataRow.createCell(0);
		HSSFCell cell2 = dataRow.createCell(1);
		HSSFCell cell3 = dataRow.createCell(2);
		HSSFCell cell4 = dataRow.createCell(3);
		HSSFCell cell5 = dataRow.createCell(4);
		HSSFCell cell6 = dataRow.createCell(5);
		HSSFCell cell7 = dataRow.createCell(6);
		HSSFCell cell8 = dataRow.createCell(7);
	
		
		cell1.setCellStyle(dataCellStyle);
		cell2.setCellStyle(dataCellStyle);
		cell3.setCellStyle(dataCellStyle);
		cell4.setCellStyle(dataCellStyle);
		cell5.setCellStyle(dataCellStyle);
		cell6.setCellStyle(dataCellStyle);
		cell7.setCellStyle(dataCellStyle);
		cell8.setCellStyle(dataCellStyle);
		
		cell1.setCellValue(productData.getSku());
		cell2.setCellValue(productData.getProductName());
		cell3.setCellValue(productData.getExpirationTime());
		cell4.setCellValue(productData.getPackQty());
		cell5.setCellValue(productData.getPlugType());
		cell6.setCellValue(productData.getPackageQty());
		cell7.setCellValue(productData.getExpirationTimeQty());
		cell8.setCellValue(productData.getExpirationTimeSum());
		
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

}
