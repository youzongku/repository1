package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期/时间工具类
 * 
 * @description 提供有关日期/时间的常用静态操作方法
 * @author luwj
 */

public class DateUtils {

	/**
	 * 日期格式:数据库日期格式(yyyyMMdd)
	 */
	public static String FORMAT_DATE_DB = "yyyyMMdd";

	/**
	 * 日期格式:时间格式(HHmmss)
	 */
	public static String FORMAT_TIME = "HHmmss";

	/**
	 * 日期格式:小时分钟格式(HHmm)
	 */
	public static String FORMAT_HOUR_MINUTE = "HHmm";

	/**
	 * 日期格式：时间格式(HH:mm:ss)
	 */
	public static String FORMAT_TIME_PAGE = "HH:mm:ss";

	/**
	 * 日期格式:页面日期格式(yyyy-MM-dd)
	 */
	public static String FORMAT_DATE_PAGE = "yyyy-MM-dd";

	/**
	 * 日期格式:银行日期时间格式(yyyyMMddHHmmss)
	 */
	public static String FORMAT_DATETIME_BACKEND = "yyyyMMddHHmmss";

	/**
	 * 日期格式:本地日期明码格式(yyyy年MM月dd HH:mm:ss)
	 */
	public static String FORMAT_LOCAL = "yyyy年MM月dd HH:mm:ss";

	/**
	 * 日期格式:本地日期明码格式(yyyy-MM-dd HH:mm:ss)
	 */
	public static String FORMAT_FULL_DATETIME = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 日期格式:完整日期/时间格式
	 */
	public static String EXAC_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss,S";

	
	/**
	 * 时间转字符窜
	 * @param date
	 * @param format
	 * @return String 返回类型
	 */
	public static String date2string(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		String result = df.format(date);
		return result;
	}
	
	/**
	 * 字符窜转时间
	 * @param param
	 * @param format
	 * @return Date 返回类型
	 * @throws ParseException
	 */
	public static Date string2date(String param, String format) throws ParseException {
		DateFormat df = new SimpleDateFormat(format);
		Date date = df.parse(param);
		return date;
	}
	
}
