package util.marketing.promotion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 条件属性util
 * 
 * @author huangjc
 * @date 2016年7月26日
 */
public class CondtUtil {
	/**
	 * 是否是条件属性1~3
	 * @param str
	 * @return
	 */
	public static boolean isAttrNumber(String str) {
		Pattern pattern = Pattern.compile("[1-3]{1}+");
		Matcher isAttrNumber = pattern.matcher(str);
		if (!isAttrNumber.matches()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 是否处于条件属性范围
	 * @param attr
	 * @return
	 */
	public static boolean isAttrRange(short attr) {
		if(1<=attr && attr <=3){
			return true;
		}
		return false;
	}
}
