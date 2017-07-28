package utils.discart;

public class StringUtils {
	/**
	 * @author hanfs
	 * @param obj
	 * @param isNull
	 *            (true:对象为空时返回null false:对象为空时返回"")
	 * @return
	 */
	public static String getString(Object obj, boolean isNull) {
		if (obj != null) {
			return obj.toString();
		}
		if (isNull) {
			return null;
		}
		return "";
	}

	public static boolean isBlankOrNull(Object obj) {
		if (obj == null || "".equals(obj.toString())) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlankOrNull(Object obj) {
		return !isBlankOrNull(obj);
	}

	public static String getStringBlank(Object obj, boolean isBlank) {
		if (obj != null && !"".equals(obj.toString())) {
			return obj.toString();
		}
		if (isBlank) {
			return null;
		}
		return "";
	}
	/**
	 * 判断字符串是否包含数字，大写字母和小写字母
	 * @param str
	 * @return
	 * @author huchuyin
	 * @date 2016年10月6日 上午11:36:28
	 */
	public static boolean containsLetterNum(String str) {
		if(str == null) {
			return false;
		}
		//数字标识
		boolean numberFlag = false;
		//大写字母标识
		boolean upperFlag = false;
		//小写字母标识
		boolean lowerFlag = false;
		for(int i=0;i<str.length();i++) {
			char c = str.charAt(i);
			if(Character.isDigit(c)) {
				//若字符串中包含数字，则数字标识设置为true
				numberFlag = true;
			} else if(Character.isUpperCase(c)) {
				//若字符串中包含大写字母，则大写字母标识设置为true
				upperFlag = true;
			} else if(Character.isLowerCase(c)) {
				//若字符串中包含小写字母，则小写字母标识设置为true
				lowerFlag = true;
			}
		}
		//当三个标识都为true时，字符串才算包含有数字和大小写字母
		return numberFlag && upperFlag && lowerFlag;
	}
}
