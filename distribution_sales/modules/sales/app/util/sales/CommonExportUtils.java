package util.sales;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import play.Logger;


public class CommonExportUtils {
	/**
	 * 服务器临时目录
	 */
	private static String filePath = "/tmp/";
	
	/**
	 * 
	 * @param sqlResult   sql结果
	 * @param sqlResultKeyMap   sql结果map中的key值和在表中的列数
	 * @param excelRowKeyMap	excel中列名和位置
	 * @param fileName	文件名
	 * @param excelTitle 表格标题
	 * @param excelWidthMap 
	 * @param rowsMergeLists 需要行合并的列
	 * @param mergeKey 根据该字段是否相操作行合并
	 * @return
	 */
	public static String getFile(List<Map> sqlResult, Map<Integer, String> sqlResultKeyMap,
			Map<Integer, String> excelRowKeyMap, String fileName, String excelTitle, Map<Integer, Integer> excelWidthMap, List<Integer> rowsMergeLists, String mergeKey) {
		Logger.info("导出时间start：" + new Date());
		
		HSSFWorkbook workBook = new HSSFWorkbook();// 创建一个工作簿
		HSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		Map<String, HSSFCellStyle> cellStyleMap = getCellStyleMap(workBook);
		
		
		
		//CellRangeAddress  对象的构造方法需要传入合并单元格的首行、最后一行、首列、最后一列。
		CellRangeAddress firstTitleCellRangeAddress = new CellRangeAddress(0, 1, 0, excelRowKeyMap.size()-1);
		sheet.addMergedRegion(firstTitleCellRangeAddress);
		HSSFRow headRow = sheet.createRow(0);
		HSSFCell headCell = headRow.createCell(0);
		headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
		// 设置样式
		headCell.setCellStyle(cellStyleMap.get("cellStyle"));
		headCell.setCellValue(excelTitle);
		
		
		HSSFRow titleRow = sheet.createRow(2);
		HSSFCellStyle titleCellstyle = cellStyleMap.get("titleCellstyle");
		for (int j = 0; j < excelRowKeyMap.size(); j++) {
			HSSFCell titleCell = titleRow.createCell(j);
			// 设置单元格格式
			if(excelWidthMap.size()>0){
				sheet.setColumnWidth(j, excelWidthMap.get(j));
			}else{
				sheet.setColumnWidth(j, 4000);
			}
			titleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			titleCell.setCellStyle(titleCellstyle);
			// 将列名填入单元格
			titleCell.setCellValue(excelRowKeyMap.get(j));
		}
		
		insertDatas(sheet, cellStyleMap, sqlResult,sqlResultKeyMap,excelRowKeyMap,rowsMergeLists,mergeKey);
		
		// 生成文件
		return createExcel(fileName, workBook);
	}

	private static String createExcel(String fileName, HSSFWorkbook workBook) {
		FileOutputStream fos = null;
		File file = null;
		String name="";
		try {
			File path = new File(filePath);
			if (!path.isDirectory() && !path.exists()) {
				path.mkdir();
			}
			name = filePath + "Export-" + new Date().getTime() + "-" + fileName;
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
				if(fos!=null){
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				Logger.error(e + "");
				return null;
			}
		}
		Logger.info("导出时间end：" + new Date());
		return name.replace(filePath, "");
		
	}

	/**
	 * 
	 * @param sheet 表对象
	 * @param cellStyleMap 单元格格式
	 * @param sqlResult 待插入数据
	 * @param sqlResultKeyMap sql结果与列的对应关系
	 * @param excelRowKeyMap 提供有多少列
	 * @param rowsMergeLists 需要行合并的列
	 * @param mergeKey 根据该字段操作行合并
	 */
	private static void insertDatas(HSSFSheet sheet, Map<String, HSSFCellStyle> cellStyleMap,
			List<Map> sqlResult, Map<Integer, String> sqlResultKeyMap, Map<Integer, String> excelRowKeyMap, List<Integer> rowsMergeLists, String mergeKey) {
		// 从第4行开始是具体数据了
		int rowNum = 3;
		for (int i = 0; i < sqlResult.size(); i++) {
			  Map map = sqlResult.get(i);
			  if(mergeKey==null && !rowsMergeLists.isEmpty() && i>0){//无需根据指定字段操作行合并
				  for(int j=0;j<map.size();j++){
					if(rowsMergeLists.contains(j)){//该列要求行合并
						String key = sqlResultKeyMap.get(j);
						Map mapRow1 = sqlResult.get(i-1);
						 String cellData1 = mapRow1.get(key)==null?"":mapRow1.get(key).toString();
						 String cellData2 = map.get(key)==null?"":map.get(key).toString();
						 if(cellData1.equals(cellData2)){//当前行和前一行一样，进行行合并
							 sheet.addMergedRegion(new CellRangeAddress(rowNum+i-1,rowNum+i,j,j));
						 }
					}
				  }
			  }
			  if(mergeKey!=null && !rowsMergeLists.isEmpty() && i>0){//根据指定字段值相同操作行合并
				  String cellValue1= map.get(mergeKey)==null?"":map.get(mergeKey).toString();
				  String cellValue0= sqlResult.get(i-1).get(mergeKey)==null?"":sqlResult.get(i-1).get(mergeKey).toString();
				  if(cellValue1.equals(cellValue0)){//指定字段值相同，操作需要合并的行进行合并
					  for(int line: rowsMergeLists){
						  sheet.addMergedRegion(new CellRangeAddress(rowNum+i-1,rowNum+i,line,line));
					  }
				  }
			  }
			HSSFCellStyle dataCellStyle = cellStyleMap.get("dataCellStyle");
			insertEachRowData(sheet, dataCellStyle, rowNum+i ,
					map,sqlResultKeyMap,excelRowKeyMap);
		}
		
	}
	private static void insertEachRowData(HSSFSheet sheet, HSSFCellStyle dataCellStyle, int rowNum, Map sqlResult,
			Map<Integer, String> sqlResultKeyMap, Map<Integer, String> excelRowKeyMap) {
		// 创建1行数据行
		HSSFRow dataRow = sheet.createRow(rowNum);
		for(int i=0;i<excelRowKeyMap.size();i++){
			HSSFCell cell = dataRow.createCell(i);
			cell.setCellStyle(dataCellStyle);
			String key=sqlResultKeyMap.get(i);
			cell.setCellValue(sqlResult.get(key)==null?"":sqlResult.get(key).toString());
		}
	}

	private static Map<String, HSSFCellStyle> getCellStyleMap(HSSFWorkbook workBook) {
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
}