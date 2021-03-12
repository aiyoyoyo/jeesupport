package com.jees.tool.joda;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static String DATE_YYYY_MM_DD = "yyyy-MM-dd";

	public static String DATE_Y_M_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 返回时间距离当前时间多久, 正数为未来几天，负数为过去几天
	 * @param _dt 时间
	 * @return [yy, MM, ww, d, h, m, s]
	 */
	public static int[] dateBeforeNow( DateTime _dt ) {
		DateTime now = new DateTime( System.currentTimeMillis() );
		int yy = Years.yearsBetween( now , _dt ).getYears();
		int MM = Months.monthsBetween( now , _dt ).getMonths();
		int ww = Weeks.weeksBetween( now , _dt ).getWeeks();
		int d = Days.daysBetween( now , _dt ).getDays();
		int h = Hours.hoursBetween( now , _dt ).getHours();
		int m = Minutes.minutesBetween( now , _dt ).getMinutes();
		int s = Seconds.secondsBetween( now , _dt ).getSeconds();
		return new int[] { yy , MM , ww , d , h , m , s };
	}

	/**
	 * 和当前时间相差多少天, 正数为未来几天，负数为过去几天
	 * @param _dt 时间
	 * @return 相差天数
	 */
	public static int dateDiffDay( DateTime _dt ){
		DateTime now = new DateTime( System.currentTimeMillis() );

		LocalDate start = new LocalDate( now.getYear(), now.getMonthOfYear(), now.getDayOfMonth() );
		LocalDate end = new LocalDate( _dt.getYear(), _dt.getMonthOfYear(), _dt.getDayOfMonth() );
	 	int days = Days.daysBetween( start , end ).getDays();
		return days;
	}

	/**
	 * 字符串日期转成11位long型
	 * @param date    日期
	 * @param format  日期格式
	 * @return
	 */
	public static long convert2long(String date, String format) {
		try {
			if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(format)) {
				SimpleDateFormat sf = new SimpleDateFormat(format);
				return sf.parse(date).getTime() / 1000;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0l;
	}

	/**
	 * long型日期转成string类型日期
	 * @param time   日期
	 * @param format  日期格式
	 * @return
	 */
	public static String convert2String(long time, String format) {
		if (time > 0l && StringUtils.isNotBlank(format)) {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			Date date = new Date(time);
			return sf.format(date);
		}
		return "";
	}

	/**
	 * 获取当前时间
	 * @param format
	 * @param date
	 * @return
	 */
	public static final String getTimeNow(final String format, final Date date)
	{
		return new SimpleDateFormat(format).format(date);
	}

}
