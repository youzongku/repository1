package util.product;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

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
	 * 日期格式:数据库日期格式(yyyyMMdd HH:mm)
	 */
	public static String TB_CSV_FORMAT_DATE_DB = "yyyy/MM/dd HH:mm";
	
	/**
	 * 订单导入
	 */
	public static String XLSX_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss"; 
	
	/**
	 * 时间转字符窜
	 * @param date
	 * @param format
	 * @return date和format，有一个为null，就返回null
	 */
	public static String date2string(Date date, String format) {
		if (date==null || StringUtils.isEmpty(format)) {
			return null;
		}
		try {
			DateFormat df = new SimpleDateFormat(format);
			String result = df.format(date);
			return result;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 字符窜转时间
	 * @param param
	 * @param format
	 * @return Date 返回类型 param或format为空，就返回null
	 * @throws ParseException
	 */
	public static Date string2date(String param, String format) throws ParseException {
		if (StringUtils.isEmpty(param) || StringUtils.isEmpty(format)) {
			return null;
		}
		DateFormat df = new SimpleDateFormat(format);
		Date date = df.parse(param);
		return date;
	}
	
	/**
	 * 当前时间字符串格式：yyyyMMddHHmmss
	 * @return
	 */
	public static String nowStr(){
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_DATETIME_BACKEND));
	}
	
	public static Date dateAddDays(Date date,int days){
		return dateAdd(date,days,Calendar.DATE);
	}
	private static Date dateAdd(Date date,int days,int field){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, days);
		return cal.getTime();
	}
	
	/**
	 * java.time.LocalDateTime --> java.util.Date
	 * @param localDateTime
	 * @return localDateTime为null，返回null
	 */
	public static Date toDate(LocalDateTime localDateTime) {
		if (localDateTime==null) {
			return null;
		}
	    ZoneId zone = ZoneId.systemDefault();
	    Instant instant = localDateTime.atZone(zone).toInstant();
	    Date date = Date.from(instant);
	    return date;
	}
	
	/**
	 * java.time.LocalDate --> java.util.Date
	 * @param localDate
	 * @return localDate为null，返回null
	 */
	public static Date toDate(LocalDate localDate) {
		if (localDate==null) {
			return null;
		}
	    ZoneId zone = ZoneId.systemDefault();
	    Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
	    Date date = Date.from(instant);
	    return date;
	}
	
	/**
	 * 
	 * @param localDate
	 * @param localTime
	 * @return localDate或localTime为null，返回null
	 */
	public static Date toDate(LocalDate localDate, LocalTime localTime) {
		if (localDate==null || localTime==null) {
			return null;
		}
		LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
	    ZoneId zone = ZoneId.systemDefault();
	    Instant instant = localDateTime.atZone(zone).toInstant();
	    Date date = Date.from(instant);
	    return date;
	}
	
	/**
	 * java.util.Date --> java.time.LocalDateTime
	 * @param date
	 * @return date为null，返回null
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		if (date==null) {
			return null;
		}
	    Instant instant = date.toInstant();
	    ZoneId zone = ZoneId.systemDefault();
	    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
	    return localDateTime;
	}
	
	/**
	 * java.util.Date --> java.time.LocalDate
	 * @param date
	 * @return date为null，返回null
	 */
	public static LocalDate toLocalDate(Date date){
		if (date==null) {
			return null;
		}
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		LocalDate localDate = localDateTime.toLocalDate();
		return localDate;
	}
	
	/**
	 * java.util.Date --> java.time.LocalTime
	 * @param date
	 * @return date为null，返回null
	 */
	public static LocalTime toLocalTime(Date date) {
		if (date==null) {
			return null;
		}
	    Instant instant = date.toInstant();
	    ZoneId zone = ZoneId.systemDefault();
	    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
	    LocalTime localTime = localDateTime.toLocalTime();
	    return localTime;
	}
	
	/**
	 * 计算两个日期的间隔天数
	 * @param fDate 开始时间
	 * @param oDate 结束时间
	 * @return
	 */
	public static int daysInterval(Date fDate, Date oDate) {
		return daysInterval(toLocalDate(fDate), toLocalDate(oDate));
	}
	
	/**
	 * jdk8的：计算两个日期的间隔天数
	 * @param startDate 开始，不能为null
	 * @param endDate 结束，不能为null
	 * @return 如果endDate在startDate之前，那么返回的会是负数
	 */
	public static int daysInterval(LocalDate startDate, LocalDate endDate){
		if (startDate==null || endDate==null) {
			throw new RuntimeException("daysInterval: parameters[startDate, endDate] can not be null");
		}
		int interval = new Long(startDate.until(endDate, ChronoUnit.DAYS)).intValue();
		return interval;
	}
}
