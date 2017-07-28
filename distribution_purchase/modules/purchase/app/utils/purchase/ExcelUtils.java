package utils.purchase;

import java.text.DecimalFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.joda.time.DateTime;

public final class ExcelUtils {
	/**
	 * 获取Excel表格单元格中数据
	 * 
	 * @return
	 */
	public static String gainCellText(Cell cell) {
		String cellText;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:// 数值型
			if (DateUtil.isCellDateFormatted(cell)) {
				cellText = new DateTime(cell.getDateCellValue())
						.toString("yyyy-MM-dd HH:mm:ss");
			} else {
				if (String.valueOf(cell.getNumericCellValue()).indexOf("E") > -1) {// 判断是否科学计算数据
					cellText = new DecimalFormat("0").format(cell
							.getNumericCellValue());
				} else {
					cellText = new DecimalFormat("0.00").format(cell
							.getNumericCellValue());
				}
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
		return cellText;
	}
}
