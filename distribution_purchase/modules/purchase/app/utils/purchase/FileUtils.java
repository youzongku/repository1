package utils.purchase;

public class FileUtils {
	/**
	 * 判断是否是excel文件（文件名以.xls或.xlsx结尾）
	 * @param filename
	 * @return
	 */
	public static boolean isExcelFile(String filename){
		if(StringUtils.isBlankOrNull(filename)){
			return false;
		}
		
		return filename.endsWith(".xls") || filename.endsWith(".xlsx");
	}
}
