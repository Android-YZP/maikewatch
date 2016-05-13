package com.maikeapp.maikewatch.config;

public class CommonConstants {
	public static final String LOGCAT_APP_NAME = "maikewatch"  ;
	public static final String LOGCAT_TAG_NAME = "jlj"  ;
	

	public static final int FLAG_GET_REG_MOBILEMGS_REGISTER_SUCCESS = 8;
	public static final int FLAG_GET_REG_MOBILEMGS_VALIDATE_SUCCESS = 9;
	public static final int FLAG_GET_REG_MOBILEMGS_VALIDATE_CAN_GET_AGAIN_SUCCESS = 10;
	public static final int FLAG_GET_REG_USER_REGISTER_SUCCESS = 11;
	public static final int FLAG_GET_REG_USER_LOGIN_SUCCESS = 12;
	
	public static final int FLAG_GET_USER_FORGOT_PWD_ONE_SUCCESS = 13;
	public static final int FLAG_GET_USER_FORGOT_SMS_CAN_GET_AGAIN_SUCCESS = 14;
	public static final int FLAG_GET_USER_FORGOT_PWD_UPDATE_SUCCESS = 15;
	public static final int FLAG_GET_MAC_ADDRESS_SUCCESS = 16;
	public static final int FLAG_BIND_MAC_ADDRESS_SUCCESS = 17;
	public static final int FLAG_UNBIND_MAC_ADDRESS_SUCCESS = 18;
	public static final int FLAG_SYNC_COMPLETED_SUCCESS = 19;
	public static final int FLAG_HOME_SYNC_SUCCESS = 20;
	public static final int FLAG_SET_TARGET_SUCCESS = 21;

	
	public static final String MSG_GET_ERROR = "服务端数据异常";
	public static final String MSG_GET_SUCCESS = "获取成功";
	public static final String MSG_SERVER_ERROR = "请求服务器错误";
	public static final String MSG_REQUEST_TIMEOUT = "请求服务器超时";
	public static final String MSG_SERVER_RESPONSE_TIMEOUT = "服务器响应超时";
	/**
	 * 测试地址
	 */
//	public static final String TEST_ADDRESS = "http://192.168.0.77:8016/";
	public static final String TEST_ADDRESS = "http://192.168.0.203:8085/";
	public static final String TRUE_ADDRESS = "#";
	public static final String NOW_ADDRESS = TEST_ADDRESS;
	

	/**
	 * 注册登录
	 */
	//获取tokenID
	public static final String GET_TOKEN_ID = NOW_ADDRESS + "WebServices/WatchServices/GetTokenID";

	//登录
	public static final String USER_LOGIN = NOW_ADDRESS + "WebServices/WatchServices/Login";

	public static final String SET_SPORTS_TARGET = NOW_ADDRESS + "WebServices/WatchServices/SetSportsTarget";

	//发送注册短信
	public static final String MOBILEMSG_REGISTER = NOW_ADDRESS + "mobilemsg/register.json";
	//验证注册短信
	public static final String MOBILEMSG_VALIDATE = NOW_ADDRESS + "mobilemsg/validate.json";
	//注册
	public static final String USER_REGISTER = NOW_ADDRESS + "user/register.json";

	//忘记密码第一步
	public static final String USER_FORGOT_PASSWORD_ONE = NOW_ADDRESS + "user/forgot_password_one.json";
	//忘记密码第二步
	public static final String USER_FORGOT_PASSWORD_TWO = NOW_ADDRESS + "user/forgot_password_two.json";


	//个人资料修改确认
	public static final String MEMBER_PROFILE_UPDATE = NOW_ADDRESS + "member/profile/update.json";

	//安全中心修改密码member/pwd.json
	public static final String MEMBER_PWD = NOW_ADDRESS + "member/pwd.json";

	//安全中心修改绑定手机//获取的验证码与需要绑定的手机匹配成功则绑定手机成功member/security/mobile/bind.json
	public static final String MEMBER_SECURITY_MOBILE_BIND = NOW_ADDRESS + "member/security/mobile/bind.json";

	//用户详情
	public static final String USER_DETAIL = NOW_ADDRESS + "user/detail.json";
	//检查更新
	public static final String CHECK_APP_VERSION = NOW_ADDRESS + "version.json";



}
