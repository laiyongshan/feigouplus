package com.example.youhe.youhecheguanjiaplus.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private final static SimpleDateFormat dateFormatUserId = new SimpleDateFormat("yyMMddkkmmss");

	private static Pattern p = null;
	private static Matcher m = null;

	public static Date toDate(String sdate) {
		try {
			return dateFormater.parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}


	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.format(today);
			String timeDate = dateFormater2.format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	public static String formatSoapDateTime(String soapDateTime) {
		String returnString = soapDateTime.substring(0, 19).replace("T", " ");
		return returnString;
	}

	public static String formatSoapNullString(String anyType) {
		String returnString = anyType.equals("anyType{}") ? "" : anyType;
		return returnString;
	}

	public static boolean checkEmailInput(String email) {
		p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
		m = p.matcher(email);
		return m.matches();
	}

	public static boolean checkUsernameInput(String username) {
		// p = Pattern.compile("^\\w+$");
		// m = p.matcher(username);
		return 1 > 0;
	}

	public static boolean check2Password(String password, String password2) {
		return password.equals(password2);
	}

	public static String date2UserId() {
		String time = dateFormatUserId.format(new Date());
		return time;
	}

	public static Date genCurrentDate() {
		Date date = null;
		try {
			String now = dateFormater.format(new Date());
			// DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = dateFormater.parse(now);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

    public static boolean formatBoolean(String booleanStr) {
        if ("true".equalsIgnoreCase(booleanStr)) {
            return true;
        } else {
            return false;
        }
    }
    
    
    public static boolean checkPassword(String password){
    	 p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{3,11}$");
    	 m = p.matcher(password);
    	return m.matches();
    }
    
    //显示手机号码
    public static String  showNum(String strNum){
    	StringBuilder s=new StringBuilder(strNum);
    	if(strNum.length()>=7){
//    		s.replace(3, 7, "****");
			return s.substring(0,4)+"****"+s.substring(s.length()-4,s.length());
    	}
    	return s.toString();
    }


	//检查输入的手机号码是不是11位
	public static boolean isPhoneNum(String phoneNum){
		if(phoneNum.length()==11){
			return true;
		}else{
			return false;
		}
	}

	public static String subSpanStr(String span){
		Pattern p=Pattern.compile("<span class='text-error'>([\\w/\\.]*)</span>");
		Matcher m=p.matcher(span);
		while(!m.hitEnd()&&m.find()){
			return m.group(1);
		}

		return "";
	}
	public static boolean isChinese(String str) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(str);
		boolean flg = false;
		if (matcher.find())
			flg = true;

		return flg;
	}

}
