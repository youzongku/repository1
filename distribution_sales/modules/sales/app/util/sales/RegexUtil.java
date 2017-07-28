package util.sales;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具
 * @author huangjc
 * @date 2017年3月24日
 */
public final class RegexUtil {
	private RegexUtil() {
	}

	/**
	 * 是否是正整数（不包含0）
	 * @param str
	 * @return
	 */
	public static boolean IsIntNumber(String str) {
		String regex = "^\\+?[1-9][0-9]*$";
		return match(regex, str);
	}

	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
