package com.jees.tool.joda;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

@Log4j2
public class DateUtils {
	public static final String DATE_YYYY_MM_DD = "yyyy-MM-dd";
	public static final String DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 返回时间距离当前时间多久, 正数为未来几天，负数为过去几天
	 * @param _dt 时间
	 * @return [yy, MM, ww, d, h, m, s]
	 */
	public static int[] dateBeforeNow( DateTime _dt ) {
		DateTime now = DateTime.now();
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
		DateTime now = DateTime.now();

		LocalDate start = new LocalDate( now.getYear(), now.getMonthOfYear(), now.getDayOfMonth() );
		LocalDate end = new LocalDate( _dt.getYear(), _dt.getMonthOfYear(), _dt.getDayOfMonth() );
	 	int days = Days.daysBetween( start , end ).getDays();
		return days;
	}

	/**
	 * 字符串日期转成11位long型
	 * @param _date    日期
	 * @param _format  日期格式
	 * @return
	 */
	public static long convert2long(String _date, String _format) {
		if (StringUtils.isNotBlank(_date) && StringUtils.isNotBlank(_format)) {
			DateTime date = DateTime.parse(_date, DateTimeFormat.forPattern(_format));
			return date.getMillis() / 1000;
		}
		return 0l;
	}

		/**
		 * long型日期转成string类型日期
		 * @param _time   日期
		 * @param _format  日期格式
		 * @return
		 */
		public static String convert2String(long _time, String _format) {
			DateTime datetime = new DateTime( _time );
			if ( _time > 0l && StringUtils.isNotBlank( _format )) {
				return datetime.toString( _format );
			}
			log.warn( "The param time is zero, will return empty string." );
			return "";
		}

		/**
		 * 获取当前时间
		 * @param _format
		 * @return
		 */
		public static String getTimeNow(String _format) {
			DateTime datetime = DateTime.now();
			if( StringUtils.isNotBlank( _format ) ){
				return datetime.toString( _format );
			}
			return datetime.toString( DATE_YYYY_MM_DD_HH_MM_SS );
		}

		/**
		 * Date型日期转成string类型日期
		 * @param _time   日期
		 * @param _format  日期格式
		 * @return
		 */
		public static String convert2String(Date _time, String _format) {
			DateTime datetime = new DateTime( _time );
			if ( StringUtils.isNotBlank( _format ) ) {
				return datetime.toString( _format );
			}
			return datetime.toString( DATE_YYYY_MM_DD_HH_MM_SS );
		}
}
