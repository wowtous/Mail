package task.mail.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
	/**
	 * 生成文章ID
	 * @return
	 */
	public static long getArticleId() {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss0");
		String dateStr = sdf1.format(new Date());
		String randomNum = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
		String fldRecdId = dateStr + randomNum;
		return Long.parseLong(fldRecdId);
	}
	
	/**
	 * 时间格式化
	 * @param date
	 * @param pattern	输出格式
	 * @return String
	 */
	public static String dateToString(Date date , String pattern){
		if (date == null) {
			return null;
		}
		SimpleDateFormat dateFormat=new SimpleDateFormat(pattern);
		String newDate = dateFormat.format(date);
		return newDate;
	}
	
	/**
	 * String转Date
	 * @param str	源字符串
	 * @param pattern	输出格式
	 * @return Date
	 */
	public static Date stringToDate(String str, String pattern) {
		if (str == null || "".equals(str)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 获取当前时间
	 * 格式：Fri Sep 14 16:18:35 CST 2012
	 * @return Date
	 */
	public static Date getNow() {
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		return cal.getTime();
	}
	
	public static String getCurrentTime(String pattern) {
		Date date = new Date();
		return dateToString(date, pattern);
	}

	/**
	 * 增加天数
	 * @param date	原日期
	 * @param num	增加的天数 , 负数则为减少
	 * @return Date
	 */
	public static Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
	
	/**
	 * 获取当前时间到明天剩余的毫秒数
	 * @return Long
	 */
	public static Long getSecondToNextDay() {
		SimpleDateFormat sFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		Long between = 0L;
		try {
			String now = sFormat.format(new Date());
			Date begin = sFormat.parse(now);
			Date end = sFormat.parse("23:59:59.999");
			between = end.getTime() - begin.getTime();
		} catch (Exception e) {
			e.getStackTrace();
		}
		return between;
	}
	
	/**
	 * 获取运行时间
	 * @param hour	时
	 * @param minute	分
	 * @param second	秒
	 * @return Date
	 */
	public static Date getRunTime(int hour,int minute,int second){
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		
		Date date=calendar.getTime(); //第一次执行定时任务的时间
		
		// 如果程序启动时间超过了任务定时启动的时间，则将任务定时器推迟一天，避免程序一启动即开始任务
		if (date.before(new Date())) {
			date = addDay(date, 1);
		}
		return date;
	}
	
	/**
	 * 时间字符串(包含"-")转换成 Long
	 * @param date
	 * @return long
	 */
	public static long parseDateToLong(String date){
		if (StringUtils.isNone(date)) {
			return 0;
		}
		String dateString = date.replaceAll("-", "");
		long res = Long.parseLong(dateString);
		return res;
	}
	
	/**
	 *Description：转换 "xxxx年xx月xx日" 格式的日期, 将 "年月日" 替换为 "-"
	 *@author Jadeite.Wang
	 *@param dateStr
	 *@param pattern
	 *@return
	 * @throws ParseException 
	 */
	public static Date convertDateStr(String dateStr, String pattern) {
		dateStr = dateStr.replace("年", "-").replace("月", "-").replace("日", "");
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取当月第一天日期
	 * @return yyyy-MM-dd
	 */
	public static String getCurrentMonthFirstDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		Calendar c = Calendar.getInstance();      
        c.add(Calendar.MONTH, 0);  
        c.set(Calendar.DAY_OF_MONTH,1);	//设置为1号,当前日期既为本月第一天   
        return format.format(c.getTime());  
	}
	
	/**
	 * 获取当月最后一天日期
	 * @return yyyy-MM-dd
	 */
	public static String getCurrentMonthLastDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		Calendar ca = Calendar.getInstance();      
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));    
        return format.format(ca.getTime());  
	}
	
	/**
	 * 获取上一个月日期
	 * @return yyyy-MM
	 */
	public static String getLastMonthBefore(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM"); 
		Calendar ca = Calendar.getInstance();      
        ca.set(Calendar.MONDAY,ca.get(Calendar.MONDAY)-1);
        return format.format(ca.getTime());  
	}
}
