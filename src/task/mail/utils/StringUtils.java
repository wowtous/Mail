package task.mail.utils;

import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return boolean
	 */
	public static boolean isNone(String str) {
		if (str == null || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotNone(String str) {
		return !isNone(str);
	}

	/**
	 * 数字 String 转换成 Long
	 * @param str
	 * @return long
	 */
	public static long parseToLong(String str) {
		if (isNone(str)) {
			return 0;
		} else {
			return Long.parseLong(str);
		}
	}

	public static int parseToInt(String str) {
		if (isNone(str)) {
			return 0;
		} else {
			return Integer.parseInt(str);
		}
	}

	public static double parseToDouble(String str) {
		if (isNone(str)) {
			return 0.0;
		} else {
			return Double.parseDouble(str);
		}
	}

	/**
	 * 判断 String[] 是否为空
	 * @param array
	 * @return boolean
	 */
	public static boolean isEmpty(String[] array) {
		if (array == null || array.length == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotEmpty(String[] array) {
		return !isEmpty(array);
	}

	/**
	 * a 占 b 的百分比, a/b
	 * @param a , 分子
	 * @param b  , 分母
	 * @param pattern  , 输出格式 , 如0.00则保留两位小数 , 返回百分比则是0.00%
	 * @return String 0.00%
	 */
	public static String longDivideForPercent(long a, long b, String pattern) {
		DecimalFormat format = new DecimalFormat(pattern);
		if (b == 0) {
			return format.format(0);
		}
		double percent = (double) a / b;
		String result = format.format(percent);
		return result;
	}

	/**
	 * 将 String 按照分隔符转换成 String[]
	 * @param str 源字符串
	 * @param pattern 分隔符
	 * @return String[]
	 */
	public static String[] parseToArray(String str, String pattern) {
		if (isNone(str)) {
			return new String[] {};
		}
		if (str.contains(pattern)) {
			return str.split(pattern);
		} else {
			return new String[] { str };
		}
	}

	/**
	 * 编码转换
	 * @param str 源字符串
	 * @param enc 编码 , GBK/UTF-8
	 * @return String
	 */
	public static String decode(String str, String enc) {
		String result = null;
		try {
			result = URLDecoder.decode(str, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 编码转换
	 * @param str 源字符串
	 * @param enc 编码 , GBK/UTF-8
	 * @return String
	 */
	public static String encode(String str, String enc) {
		String result = null;
		try {
			result = URLEncoder.encode(str, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Description：生成文章ID
	 * @return String
	 */
	public static String getArticleId() {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss0");
		String dateStr = sdf1.format(new Date());
		String randomNum = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
		String fldRecdId = dateStr + randomNum;
		return fldRecdId;
	}

	/**
	 * 去除所有HTML标签
	 * @param htmlString
	 * @param regex
	 * @param flags
	 * @return String
	 */
	public static String removeHtml(String htmlString) {
		if (StringUtils.isNone(htmlString)) {
			return htmlString;
		}
		Pattern pattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(htmlString);
		if (matcher.find()) {
			htmlString = matcher.replaceAll("");
		}
		return htmlString;
	}

	/**
	 * Description：替换字符串
	 * @param content
	 * @param regex
	 * @param replacement
	 * @return String
	 */
	public static String replaceAll(String content, String regex,
			String replacement) {
		if (isNone(content)) {
			return content;
		}
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			content = matcher.replaceAll(replacement);
			matcher = pattern.matcher(content);
		}
		return content;
	}

	/**
	 * Description：替换第一次出现的字符串
	 * @param content
	 * @param regex
	 * @param replacement
	 * @return String
	 */
	public static String replaceFirst(String content, String regex,
			String replacement) {
		if (isNone(content)) {
			return content;
		}
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = matcher.replaceFirst(replacement);
		}
		return content;
	}

	/**
	 * Description：根据分隔符切分字符串并获取其中某个元素
	 * @param content  源字符串
	 * @param pattern  分隔符
	 * @param group 数组中元素位置, 起始位置 0
	 * @return String
	 */
	public static String getElement(String content, String pattern, int group) {
		if (isNone(content)) {
			return null;
		}
		if (!content.contains(pattern)) {
			return content;
		}
		String[] result = parseToArray(content, pattern);
		if (group >= result.length) {
			return null;
		}
		return result[group];
	}

	/**
	 * utf-8 转换成 unicode
	 * @author fisher
	 * @param inStr
	 * @return
	 */
	public static String utf8ToUnicode(String inStr) {
		char[] myBuffer = inStr.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < inStr.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
			if (ub == UnicodeBlock.BASIC_LATIN) {
				// 英文及数字等
				sb.append(myBuffer[i]);
			} else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				// 全角半角字符
				int j = (int) myBuffer[i] - 65248;
				sb.append((char) j);
			} else {
				// 汉字
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s);
				String unicode = "\\u" + hexS;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	/**
	 * unicode 转换成 utf-8
	 * @author fisher
	 * @param theString
	 * @return
	 */
	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	/**
	 * 获取项目路径
	 * System.getProperty("user.dir")
	 */
	public static String getPath(){
		return System.getProperty("user.dir");
	}
}
