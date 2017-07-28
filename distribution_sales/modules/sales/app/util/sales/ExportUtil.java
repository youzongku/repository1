package util.sales;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.google.common.collect.Maps;

import dto.sales.AsyncExportDto;
import play.Logger;

public class ExportUtil {

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
	public static File export(String fileName, String[] header, Map<String, String> headerMapping, List<?> data) {
		Logger.info("导出时间start：" + new Date());
		HSSFWorkbook workBook = new HSSFWorkbook();// 创建一个工作簿
		HSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		HSSFRow headRow = sheet.createRow(0); // 创建第一栏，抬头栏
		HSSFCell cell = null;
		for (int j = 0; j < header.length; j++) {
			cell = headRow.createCell(j);// 创建抬头栏单元格
			// 设置单元格格式
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			sheet.setColumnWidth(j, 6000);
			HSSFCellStyle style = workBook.createCellStyle();
			HSSFFont font = workBook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			short color = HSSFColor.RED.index;
			font.setColor(color);
			style.setFont(font);
			// 将数据填入单元格
			cell.setCellStyle(style);
			if (header[j] != null) {
				cell.setCellValue(headerMapping.get(header[j]));
			} else {
				cell.setCellValue("-");
			}
		}
		// 创建数据行
		insertData(header, data, sheet);
		// 生成文件
		return createExcel(fileName, workBook);
	}

	public static void insertData(String[] header, List<?> data, HSSFSheet sheet) {
		HSSFCell cell;
		HSSFRow row;
		for (int i = 0; i < data.size(); i++) {
			row = sheet.createRow(i + 1);
			// 单元格
			for (int j = 0; j < header.length; j++) {
				cell = row.createCell(j);
				Object value = parseData(header[j], data.get(i));
				if (null == value) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(value + "");
				}
			}
		}
	}
	
	public static int insertData(String[] header, List<?> data, Sheet sheet,int rowNum) {
		Cell cell;
		Row row;
		for (int i = 0; i < data.size(); i++) {
			rowNum++;
			row = sheet.createRow(rowNum);
			// 单元格
			for (int j = 0; j < header.length; j++) {
				cell = row.createCell(j);
				Object value = parseData(header[j], data.get(i));
				if (null == value) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(value + "");
				}
			}
		}
		return rowNum;
	}
	
	/**
	 * 导出云仓发货导入的excel模板
	 * @param fileName
	 * @return
	 */
	public static File exportCloudSaleTemplate(String fileName) {
		HSSFWorkbook workBook = new HSSFWorkbook();// 创建一个工作簿
		HSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		
		HSSFCellStyle style = workBook.createCellStyle();
		HSSFFont font = workBook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFont(font);
		
		HSSFRow headRow = sheet.createRow(0); // 创建第一栏，抬头栏
		HSSFCell emailTitleCell = headRow.createCell(0);
		HSSFCell emailValueCell = headRow.createCell(1);
		emailTitleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
		emailTitleCell.setCellStyle(style);
		emailTitleCell.setCellValue("分销商：");
		emailValueCell.setCellValue("");
		
		HSSFRow secondRow = sheet.createRow(1);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));
		HSSFCell warningCell = secondRow.createCell(1);
		warningCell.setCellType(HSSFCell.CELL_TYPE_STRING);
		HSSFCellStyle styleRed = workBook.createCellStyle();
		HSSFFont fontRed = workBook.createFont();
		fontRed.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		styleRed.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		short colorRed = HSSFColor.RED.index;
		fontRed.setColor(colorRed);
		styleRed.setFont(fontRed);
		warningCell.setCellStyle(styleRed);
		warningCell.setCellValue("注意：是否选择到期日期栏目，请输入是或者否。如果需要选择商品到期日期，输入商品SKU即可，无需输入商品数量。");
		
		String[] titles = {"序号","正价商品编号","是否选择到期日期","正价商品数量","赠品编号","是否选择到期日期","赠品数量"};
		LinkedHashMap<String, Integer> title2Width = Maps.newLinkedHashMap();
		title2Width.put("序号",2000);
		title2Width.put("正价商品编号", 6000);
		title2Width.put("是否选择到期日期", 6000);
		title2Width.put("正价商品数量", 6000);
		title2Width.put("赠品编号", 6000);
		title2Width.put("是否选择到期日期", 6000);
		title2Width.put("赠品数量", 6000);
		HSSFRow titleRow = sheet.createRow(2);
		for (int j = 0; j < titles.length; j++) {
			HSSFCell cell = titleRow.createCell(j);// 创建抬头栏单元格
			// 设置单元格格式
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			sheet.setColumnWidth(j, title2Width.get(titles[j]));
			
			// 将数据填入单元格
			cell.setCellStyle(style);
			if (titles[j] != null) {
				cell.setCellValue(titles[j]);
			} else {
				cell.setCellValue("-");
			}
		}

		// 生成文件
		return createExcel(fileName, workBook);
	}
	
	public static File export(String fileName, String[] header) {
		HSSFWorkbook workBook = new HSSFWorkbook();// 创建一个工作簿
		HSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		HSSFRow headRow = sheet.createRow(0); // 创建第一栏，抬头栏
		for (int j = 0; j < header.length; j++) {
			HSSFCell cell = headRow.createCell(j);// 创建抬头栏单元格
			// 设置单元格格式
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			sheet.setColumnWidth(j, 6000);
			HSSFCellStyle style = workBook.createCellStyle();
			HSSFFont font = workBook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			short color = HSSFColor.RED.index;
			font.setColor(color);
			style.setFont(font);
			// 将数据填入单元格
			cell.setCellStyle(style);
			if (header[j] != null) {
				cell.setCellValue(header[j]);
			} else {
				cell.setCellValue("-");
			}
		}

		// 生成文件
		return createExcel(fileName, workBook);
	}

	/**
	 * 
	 * @param fileName
	 * @param workBook
	 * @return
	 */
	public static File createExcel(String fileName, HSSFWorkbook workBook) {
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
	 * @param field
	 * @param object
	 * @return
	 */
	public static Object parseData(String field, Object object) {
		try {
			String firstLetter = field.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + field.substring(1);
			Method method = object.getClass().getMethod(getter);
			Object value = method.invoke(object);
			return value;
		} catch (Exception e) {
			Logger.error("字段不存在：" + field);
			return null;
		}
	}
	
	public static File  writeExcel(AsyncExportDto asyncExportDto,Workbook workBook){
		FileOutputStream out = null;
		File file = null;
		try {
			String filepath = filePath+File.separator + asyncExportDto.getAdmin();
			File path = new File(filepath);
			if (!path.isDirectory() && !path.exists()) {
				path.mkdirs();
			}
			asyncExportDto.setPath(filepath);
			String name = filepath + File.separator+ asyncExportDto.getFilename();
			Logger.info("execl导出临时路径:"+name);
			file = new File(name);
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			out =new FileOutputStream(file,true);
			workBook.write(out);
			Logger.info("-----生成Excel成功-----");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (out!=null) {
					out.flush();
					out.close();
					workBook.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return file;
	}
	
	public static void writeExcel(String  filePath,SXSSFWorkbook workBook){
		FileOutputStream out = null;
		try {
			out =new FileOutputStream(filePath);
			workBook.write(out);
			Logger.info("-----填充数据-----");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (out!=null) {
					out.flush();
					out.close();
					workBook.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static File  readExcel(String filePath,String fileName){
		File path = new File(filePath);
		if (!path.isDirectory() && !path.exists()) {
			return null;
		}
		String name = filePath + File.separator+ fileName;
		File file = new File(name);
		if(!file.exists()){
			return null;
		}
		return file;
	}
	

}
