package com.jees.tool.joda;

import org.joda.time.*;

public class DateUtils {
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
}
