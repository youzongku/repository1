package utils.purchase;

public class CsvUtils {
	/**
	 * 判断是否是csv文件（文件名以.csv结尾）
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isCsvFile(String filename) {
		if (StringUtils.isBlankOrNull(filename)) {
			return false;
		}

		return filename.endsWith(".csv");
	}

}
