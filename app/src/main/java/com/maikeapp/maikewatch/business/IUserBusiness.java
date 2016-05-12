package com.maikeapp.maikewatch.business;

import com.maikeapp.maikewatch.bean.User;




public interface IUserBusiness {


	/**
	 * 用户登录
	 * @param pPhone
	 * @param pPassword
	 * @return
	 * @throws Exception
	 */
	public abstract String getUserLogin(String pPhone, String pPassword) throws Exception;

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
	 * 获取手机短信验证码
	 * @param phone
	 * @return
	 * @throws Exception
	 */
	public abstract String getMobilemsgRegister(String phone) throws Exception;

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
	 * @param mPhone
	 * @param mCode
	 * @param username
	 * @param password
	 * @return
	 */
	public abstract String getUserRegister(String mPhone, String mCode,
										   String username, String password) throws Exception;



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
	public abstract String getUserForgotPasswordTwo(String mPhone,
													String smsCode, String newpwd, String newpwdAgain) throws Exception;

	/**
	 * 获取用户登录后的用户详情：如用户名、头像、创意设计粉丝的数量
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public abstract String getUserDetail(int userid) throws Exception;


	/**
	 * 用户修改密码
	 * @param oldpwd
	 * @param newpwd
	 * @param againpwd
	 * @param mUser
	 * @return
	 * @throws Exception
	 */
	public abstract String updateUserPwd(String oldpwd, String newpwd,
										 String againpwd, User mUser) throws Exception;



	/**
	 * 用户设置-安全中心修改绑定手机
	 * @param mPhone
	 * @param smsCode
	 * @param mUser
	 * @return
	 * @throws Exception
	 */
	public abstract String getUserSettingSecurityPhoneUpdate(String mPhone,
															 String smsCode, User mUser) throws Exception;



	/**
	 * 用户设置-修改用户信息
	 * @param _usersign
	 * @param mUser
	 * @return
	 * @throws Exception
	 */
	public abstract String updateUserSettingPsInfo(
			String _usersign, User mUser) throws Exception;

	public abstract String checkAppUpdate() throws Exception;


}