package util.timer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

import play.Configuration;
import play.Play;

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
	
	public static String FORMAT_LOCAL_DATE = "yyyy年MM月dd";

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
	 * 日期格式:本地日期明码格式(yyyy-MM-dd HH:mm:ss:SSS)
	 */
	public static String FORMAT_FULL_DATETIME_MI = "yyyy-MM-dd HH:mm:ss:SSS";
	
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
	
	public static Date getCancelDate() throws ParseException{
		Configuration config = Play.application().configuration().getConfig("cancel");
		String dateStr = config.getString("date");
		return string2date(dateStr,DateUtils.FORMAT_FULL_DATETIME);
	}
	
	/**
	 * date 的 days 天后 的日期
	 * @param date 指定日期
	 * @param days 长度
	 * @param field 操作类型
	 * @author zbc
	 * @since 2017年2月18日 下午4:11:44
	 */
	public static Date dateAddDays(Date date,int days){
		return dateAdd(date,days,Calendar.DATE);
	}	
	private static Date dateAdd(Date date,int days,int field){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, days);
		return cal.getTime();
	}
	
	public static int daysOfTwo(Date fDate, Date oDate) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(fDate);
		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(oDate);
		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
		return day2 - day1;
	}
	
	public static Date dateAddMonths(Date date,int months){
		return lastDayOfMonth(dateAdd(date,months-1,Calendar.MONTH));
	}
	
	/**
	 * @param date 
	 * 指定日期的当月最后一天
	 * @author zbc
	 * @since 2017年2月25日 下午12:34:17
	 */
	public static Date lastDayOfMonth(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		LocalDate localDate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH));
		// 创建日历
		LocalDate lastDayOfThisMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
		return toDate(lastDayOfThisMonth);
    }
	
	/**
	 * 将LocalDate ->  Date 
	 * @author zbc
	 * @since 2017年2月25日 下午2:51:18
	 */
	private static Date toDate(LocalDate localDate){
		if (localDate == null) {
			return null;
		}
		//时区对象
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Instant instant = localDate.atStartOfDay(defaultZoneId).toInstant();
		return new Date(instant.toEpochMilli());
	}
	
}
