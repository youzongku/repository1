package utils.dismember;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
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
				cell.setCellValue(headerMapping.get(header[j]));
			} else {
				cell.setCellValue("-");
			}
		}
		// 创建数据行
		for (int i = 0; i < data.size(); i++) {
			HSSFRow row = sheet.createRow(i + 1);
			// 单元格
			for (int j = 0; j < header.length; j++) {
				HSSFCell cell = row.createCell(j);
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
			/*//要想表单上的字段有%,在此加入过滤，对等级折扣和定制折扣加上%,关键点在此处
			if(getter.equals("getDiscount")||getter.equals("getCustomizeDiscount")){
				return StringUtils.isBlankOrNull(value) ? "" : String.valueOf(value)+"%";
			}*/
			return value;
		} catch (Exception e) {
			Logger.error("字段不存在：" + field);
			return null;
		}
	}

}
