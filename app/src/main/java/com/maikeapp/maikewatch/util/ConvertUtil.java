package com.maikeapp.maikewatch.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ConvertUtil {
	/**
	 * 把该字符串转换成MD5字符串，并转换成大写
	 * @param info
	 * @return
	 */
	public static String getMD5(String info)
	{
	  try
	  {
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    md5.update(info.getBytes("UTF-8"));
	    byte[] encryption = md5.digest();
	      
	    StringBuffer strBuf = new StringBuffer();
	    for (int i = 0; i < encryption.length; i++)
	    {
	      if (Integer.toHexString(0xff & encryption[i]).length() == 1)
	      {
	        strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
	      }
	      else
	      {
	        strBuf.append(Integer.toHexString(0xff & encryption[i]));
	      }
	    }
	      
	    return strBuf.toString().toUpperCase();//大写
	  }
	  catch (NoSuchAlgorithmException e)
	  {
	    return "";
	  }
	  catch (UnsupportedEncodingException e)
	  {
	    return "";
	  }
	}
	
	/**
	 * 截取敏感信息
	 * @param oldString
	 * @return
	 */
	public static String getSubStringForHideInfo(String oldString){
		if(oldString!=null&&!oldString.equals("")){
			if(oldString.length()>=9){
				StringBuffer _sb = new StringBuffer();
				String _sbtemp1 = oldString.substring(0, 3);
				String _sbtemp2 = oldString.substring(oldString.length()-3, oldString.length());
				_sb.append(_sbtemp1);
				_sb.append("***");
				_sb.append(_sbtemp2);
				return _sb.toString();
			}else if(oldString.length()>=7&&oldString.length()<9){
				StringBuffer _sb = new StringBuffer();
				String _sbtemp1 = oldString.substring(0, 3);
				String _sbtemp2 = oldString.substring(oldString.length()-3, oldString.length());
				_sb.append(_sbtemp1);
				switch (oldString.length()) {
				case 7:
					_sb.append("*");
					break;
				case 8:
					_sb.append("**");
				default:
					break;
				}
				
				_sb.append(_sbtemp2);
				return _sb.toString();
			}
			else{
				return oldString;
			}
		}else{
			return "";
		}
		
		
	}
	
}
