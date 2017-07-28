package utils.purchase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
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
	
	public static int getDateSpace(Date smallDate, Date bigDate) {
		Calendar calst = Calendar.getInstance();
		Calendar caled = Calendar.getInstance();
		calst.setTime(smallDate);
		caled.setTime(bigDate);

		// 设置时间为0时
		calst.set(Calendar.HOUR_OF_DAY, 0);
		calst.set(Calendar.MINUTE, 0);
		calst.set(Calendar.SECOND, 0);
		caled.set(Calendar.HOUR_OF_DAY, 0);
		caled.set(Calendar.MINUTE, 0);
		caled.set(Calendar.SECOND, 0);
		// 得到两个日期相差的天数
		int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst
				.getTime().getTime() / 1000)) / 3600 / 24;

		return days;
	}
	
	public static int getMonthSpace(Date smallDate, Date bigDate) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(smallDate);
        c2.setTime(bigDate);
        int result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        return result == 0 ? 1 : Math.abs(result);
    }

	/**
	 * 解析时间字符串
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date parse(String dateStr, String format){
		DateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 转为yyyy-MM-dd HH:mm:ss字符串
	 * @param date
	 * @return
	 */
	public static String date2FullDateTimeString(Date date){
		return date2string(date,FORMAT_FULL_DATETIME);
	}
	
	/**
	 * 时间转字符窜
	 * @param date
	 * @param format
	 * @return String 返回类型
	 */
	public static String date2string(Date date, String format) {
		String result = null;
		if(date != null){
			DateFormat df = new SimpleDateFormat(format);
			result = df.format(date);
		}
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
