package com.jees.tool.joda;

import org.joda.time.*;

public class DateUtils {
	/**
	 * 返回时间距离当前时间多久
	 * 
	 * @param _dt
	 * @return [yy, MM, ww, d, h, m, s]
	 */
	public static int[] dateBeforeNow( DateTime _dt ) {
		DateTime now = new DateTime( System.currentTimeMillis() );
		int yy = Years.yearsBetween( _dt , now ).getYears();
		int MM = Months.monthsBetween( _dt , now ).getMonths();
		int ww = Weeks.weeksBetween( _dt , now ).getWeeks();
		int d = Days.daysBetween( _dt , now ).getDays();
		int h = Hours.hoursBetween( _dt , now ).getHours();
		int m = Minutes.minutesBetween( _dt , now ).getMinutes();
		int s = Seconds.secondsBetween( _dt , now ).getSeconds();
		return new int[] { yy , MM , ww , d , h , m , s };
	}
}
