package util.product;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import play.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
		HSSFRow row = null;
		// 创建数据行
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
	private static File createExcel(String fileName, HSSFWorkbook workBook) {
		FileOutputStream fos = null;
		File file = null;
		try {
			File path = new File(filePath);
			if (!path.isDirectory() && !path.exists()) {
				path.mkdir();
			}
			String name = filePath + "Export-" + new Date().getTime() + "-" + fileName;
			Logger.info("execl导出临时路径:" + name);
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
	private static Object parseData(String field, Object object) {
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

}
