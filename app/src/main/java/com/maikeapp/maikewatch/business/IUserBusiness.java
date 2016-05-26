package com.maikeapp.maikewatch.business;

import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.bean.User;

import java.util.List;


public interface IUserBusiness {
	/**
	 * 上传个人信息到服务端
	 * @return
	 * @throws Exception
	 */
	public abstract String setInfoToServer(User mUser) throws Exception;
	/**
	 * 获取版本号
	 * @return
	 * @throws Exception
	 */
	public abstract String getUpdateFromServer(String AppVersionName ,String AppVersionCode) throws Exception;
	/**
	 * 验证手机号码是否有效
	 * @param Phone 需要验证的手机号吗
	 * @return
	 * @throws Exception
	 */
	public abstract String getMobileNumberIsVaild(String Phone) throws Exception;
	/**
	 * 用户登录
	 * @param pPhone
	 * @param pPassword
	 * @return
	 * @throws Exception
	 */
	public abstract String getUserLogin(String pPhone, String pPassword) throws Exception;

	public abstract String getNumberPic(String token) throws Exception;
	/**
	 * 设置个人目标步数
	 * @param mUser
	 * @throws Exception
     */
	public abstract String setSportsTarget(User mUser) throws Exception;


	/**
	 * 获取tokenID
	 * @return
	 * @throws Exception
	 */
	public abstract String getTokenID() throws Exception;

	/**
	 * 保存今日的运动数据
	 * @param mUser
	 * @param calories
	 * @param distance
	 * @return
     * @throws Exception
     */
	public abstract String syncSportsDataToday(User mUser, String calories, String distance) throws Exception;

	/**
	 * 上传最近7天的数据
	 * @param allDayData
	 * @return
	 * @throws Exception
     */
	public abstract String uploadRecentWeekData(List<OneDayData> allDayData) throws Exception;

	/**
	 * 查询某个日期的当天运动数据，包括小时数的步数
	 * @param mUser 用户信息
	 * @param day_time 日期
	 * @return json字符串
	 * @throws Exception
     */
	public abstract String querySportsDataByDay(User mUser, String day_time) throws Exception;

	/**
	 * 查询最近的几天的运动数据
	 * @param mUser
	 * @param i
	 * @return
	 * @throws Exception
     */
	public abstract String queryRecentlySportsData(User mUser, int days) throws Exception;


	/**
	 * 获取手机短信验证码
	 * @return
	 * @throws Exception
	 */
	public abstract String getMobilemsgRegister(String sMobileNumber, String sTokenID,String sPicCode) throws Exception;

	/**
	 * 获取手机短信验证码验证信息
	 * @param phone 手机号
	 * @param smsCode 验证码
	 * @return
	 * @throws Exception
	 */
	public abstract String getMobilemsgValidate(String phone, String smsCode) throws Exception;

	/**
	 * 用户注册
	 * @return
	 */
	public abstract String getUserRegister(String LoginName, String Mobile,
										   String VerifyCode, String Password) throws Exception;



	/**
	 * 用户忘记密码第一步
	 * @param phone
	 * @return
	 * @throws Exception
	 */
	public abstract String getUserForgotPasswordOne(String phone) throws Exception;

	/**
	 * 用户忘记密码第二步，重置密码
	 * @param mPhone
	 * @param smsCode
	 * @param newpwd
	 * @param newpwdAgain
	 * @return
	 * @throws Exception
	 */
	public abstract String getUserForgotPasswordTwo(String Mobile,
													String NewPassword, String VerifyCode) throws Exception;


	/**
	 * 检查mac地址
	 * @param macAddress
	 * @return
	 * @throws Exception
     */
	public abstract String checkMacAddress(String macAddress) throws Exception;

	/**
	 * 删除当天的数据
	 * @param mUser
	 * @param today_date_str
	 * @return
	 * @throws Exception
     */
	public abstract String deleteSportDataToday(User mUser,String mac_address, String today_date_str) throws Exception;
}