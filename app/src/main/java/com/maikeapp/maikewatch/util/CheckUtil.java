package com.maikeapp.maikewatch.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {
	public static boolean checkMobile(String mobile) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
			Matcher m = p.matcher(mobile);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static boolean checkCertNo(String mobile) {
		boolean flag = false;
		try {
			Pattern p15 = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
			Pattern p18 = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
			Matcher m15 = p15.matcher(mobile);
			boolean flag15 = m15.matches();
			Matcher m18 = p18.matcher(mobile);
			boolean flag18 = m18.matches();
			if(flag15||flag18){
				flag = true;
			}
			
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**
	 * username正则表达式
	 */
	public static final Pattern USERNAME_PATTERN = Pattern
			.compile("^[0-9a-zA-Z\\u4e00-\\u9fa5\\.\\-@_]+$");
	/**
	 * password正则表达式
	 */
	public static final Pattern PASSWORD_PATTERN = Pattern
			.compile("^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]+$");
	
	/**
	 * email正则表达式
	 */
	public static final Pattern EMAIL_PATTERN = Pattern
			.compile("^\\w+(\\.\\w+)*@\\w+(\\.\\w+)+$");
	
	public static boolean checkUsername(String username) {
		boolean flag = false;
		try {
			Matcher m = USERNAME_PATTERN.matcher(username);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static boolean checkPassword(String password) {
		boolean flag = false;
		try {
			Matcher m = PASSWORD_PATTERN.matcher(password);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			Matcher m = EMAIL_PATTERN.matcher(email);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
}
