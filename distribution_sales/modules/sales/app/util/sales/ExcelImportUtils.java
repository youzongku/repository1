package util.sales;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import play.Logger;

public class ExcelImportUtils {
	
	/**
	 * 读取工作薄表头和实体表字段映射关系
	 * @param sheetAt
	 * @return
	 */
	public static Map<Integer, String> readExcelHeaderToMap(Sheet sheetAt, String flag) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		Row row = sheetAt.getRow(0);
		if(row == null){
			return map;
		}
		int cellCount = row.getPhysicalNumberOfCells();
		Cell cell = null;
		for (int c = 0; c < cellCount; c++) {
			cell = row.getCell(c);
			String cellValue = readCellByType(cell);
			if(cellValue == null){
				continue;
			}
			if("1".equals(flag)){//淘宝订单映射map
				if (TitleUtils.tbOrderTitleToEntity().containsKey(cellValue.trim())) {
					map.put(c, TitleUtils.tbOrderTitleToEntity().get(cellValue.trim()));
				}
			}
			if("2".equals(flag)){//淘宝商品映射map
				if (TitleUtils.tbOrderGoodsTitleToEntity().containsKey(cellValue.trim())) {
					map.put(c, TitleUtils.tbOrderGoodsTitleToEntity().get(cellValue.trim()));
				}
			}
		}
		return map;
	}
	
	/**
	 * 读取每个工作薄的表头对应的实体
	 * @param sheetAt
	 * @return
	 */
	public static String readExcelHeader(Sheet sheetAt) {
		String filename = null;
		List<String> header = new ArrayList<String>();
		Row row = sheetAt.getRow(0);//表头
		if(row == null){
			return filename;
		}
		int cellCount = row.getPhysicalNumberOfCells();//单元格
		Cell cell = null;
		for (int c = 0; c < cellCount; c++) {
			cell = row.getCell(c);
			String cellValue = readCellByType(cell);
			if(cellValue == null){
				continue;
			}
			header.add(cellValue.trim());
		}
		Map<String, String> goodTitle = TitleUtils.tbOrderGoodsTitleToEntity();
		Map<String, String> orderTitle = TitleUtils.tbOrderTitleToEntity();
		Set<String> goodSet = goodTitle.keySet();
		Set<String> orderSet = orderTitle.keySet();
		boolean isContain1 = true;
		boolean isContain2 = true;
		for (String string : orderSet) {
			if (!header.contains(string.trim())) {
				Logger.info("淘宝订单表不包含的表头字段》》》》" + string);
				isContain1 = false;
				break;
			}
		}
		for (String string : goodSet) {
			if (!header.contains(string.trim())) {
				Logger.info("淘宝商品表不包含的表头字段》》》》" + string);
				isContain2 = false;
				break;
			}
		}
		if (isContain1) {// 淘宝订单信息模板
			filename = "1";
		}
		if (isContain2) {// 淘宝商品信息模板
			filename = "2";
		}
		return filename;
	}
	
	
	public static String readCellByType(Cell cell) {
		if (cell == null) {
			return null;
		} else {
			int cellType = cell.getCellType();
			String cellValue = null;
			switch (cellType) {
			case Cell.CELL_TYPE_STRING:// 文本
				cellValue = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:// 数字和日期
				if (DateUtil.isCellDateFormatted(cell)) {
					cellValue = DateUtils.date2string(cell.getDateCellValue(), DateUtils.XLSX_DATE_TIME_FORMAT);  
				} else {
					if (String.valueOf(cell.getNumericCellValue()).indexOf("E") > -1) {//判断是否科学计算数据
						cellValue = new DecimalFormat("0").format(cell.getNumericCellValue());
					} else {
						cellValue = new DecimalFormat("0.00").format(cell.getNumericCellValue());
					}
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:// 布尔型
				cellValue = String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_BLANK:// 空白
				cellValue = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:// 公式
				cellValue = String.valueOf(cell.getRichStringCellValue());
				break;
			case Cell.CELL_TYPE_ERROR:// 错误
				cellValue = "error";
				break;
			default:
				cellValue = "error";
				break;
			}
			return cellValue;
		}
	}
}
