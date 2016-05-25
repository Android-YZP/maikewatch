package com.maikeapp.maikewatch.config;

import android.os.Environment;

import java.io.File;

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
	public static final int FLAG_HOME_GET_ONE_DAY_DATA_SUCCESS = 22;
	public static final int FLAG_GET_RECENT_DATAS_FOR_WEEK_SUCCESS = 23;
	public static final int FLAG_GET_RECENT_DATAS_FOR_MONTH_SUCCESS = 24;
	public static final int FLAG_GET_MAC_NONE = 25;
	public static final int FLAG_SET_ALARM_SUCCESS = 26;
	public static final int FLAG_HOME_DATA_EXCEPTION_SUCCESS = 27;

	
	public static final String MSG_GET_ERROR = "服务端数据异常";
	public static final String MSG_GET_SUCCESS = "获取成功";
	public static final String MSG_SERVER_ERROR = "请求服务器错误";
	public static final String MSG_REQUEST_TIMEOUT = "请求服务器超时";
	public static final String MSG_SERVER_RESPONSE_TIMEOUT = "服务器响应超时";
	/**
	 * 测试地址
	 */
//	public static final String TEST_ADDRESS = "http://192.168.0.77:8016/";
//	public static final String TEST_ADDRESS = "http://192.168.0.99:8017/";
//	public static final String TEST_ADDRESS = "http://192.168.0.203:8085/";

	public static final String TEST_ADDRESS = "http://192.168.0.203:8085/";
//	public static final String TEST_ADDRESS = "http://192.168.0.97:8017/";

	public static final String TRUE_ADDRESS = "#";
	public static final String NOW_ADDRESS = TEST_ADDRESS;

	/** 应用缓存目录 */
	public static final String CachePath =Environment.getExternalStorageDirectory().getPath()+ "/smzy-2.4.3.apk";

	/**
	 * 获取tokenID
	 */
	public static final String GET_TOKEN_ID = NOW_ADDRESS + "WebServices/WatchServices/GetTokenID";

	/**
	 * 登录
	 */
	public static final String USER_LOGIN = NOW_ADDRESS + "WebServices/WatchServices/Login";

	/**
	 * 图片验证码
	 */
	public static final String PIC_LOCATION = NOW_ADDRESS ;

	/**
	 * 设置个人目标
	 */
	public static final String SET_SPORTS_TARGET = NOW_ADDRESS + "WebServices/WatchServices/SetSportsTarget";



	/**
	 * 注册
	 */
	public static final String JOIN = NOW_ADDRESS + "WebServices/WatchServices/Join";

	/**
	 * 发送短信验证码
	 */
	public static final String SET_VERIFICATION_CODE = NOW_ADDRESS + "WebServices/WatchServices/SetVerificationCode";

	/**
	 * 切换图片验证码
	 */
	public static final String RELOAD_PIC_CODE = NOW_ADDRESS + "WebServices/WatchServices/ReloadPicCode";

	/**
	 * 验证短信验证码
	 */
	public static final String CHECK_CODE = NOW_ADDRESS + "WebServices/WatchServices/CheckCode";

	/**
	 * 上传图像
	 */
	public static final String UPLOAD_IMAGE = NOW_ADDRESS + "WebServices/WatchServices/UploadImage";

	/**
	 * 获取用户信息
	 */
	public static final String QUERY_WATCH_USER = NOW_ADDRESS + "WebServices/WatchServices/QueryWatchUser";

	/**
	 * 设置用户信息
	 */
	public static final String SET_PERSONAL = NOW_ADDRESS + "WebServices/WatchServices/SetPersonal";

	/**
	 * 新增闹钟
	 */
	public static final String ADD_CLOCK = NOW_ADDRESS + "WebServices/WatchServices/AddClock";

	/**
	 * 查询闹钟
	 */
	public static final String QUERY_CLOCKS = NOW_ADDRESS + "WebServices/WatchServices/QueryClocks";

	/**
	 * 修改闹钟
	 */
	public static final String MODIFY_CLOCK = NOW_ADDRESS + "WebServices/WatchServices/ModifyClock";

	/**
	 * 检查mac地址是否通过
	 */
	public static final String CHECK_MAC_ADDRESS_EXIST = NOW_ADDRESS + "WebServices/WatchServices/CheckMacAddressExist";

	/**
	 * 同步运动数据(操作之前先调用设置运动目标和最新身高体重接口)
	 */
	public static final String SYNC_SPORTS_DATA = NOW_ADDRESS + "WebServices/WatchServices/SyncSportsData";

	/**
	 * 查询运动数据
	 */
	public static final String QUERY_SPORTS_DATA = NOW_ADDRESS + "WebServices/WatchServices/QuerySportsData";

	/**
	 * 查询最近运动数据
	 */
	public static final String QUERY_RECENTLY_SPORTS_DATA = NOW_ADDRESS + "WebServices/WatchServices/QueryRecentlySportsData";

	/**
	 * 设置运动目标和最新身高体重
	 */
	public static final String SET_TARGET_HEIGHT_WEIGHT = NOW_ADDRESS + "WebServices/WatchServices/SetSportsTarget";

	/**
	 * 查询运动目标
	 */
	public static final String QUERY_SPORTS_TARGET = NOW_ADDRESS + "WebServices/WatchServices/QuerySportsTarget";

	/**
	 * 验证登陆名是否有效
	 */
	public static final String LOGIN_NAME_ISVAILD = NOW_ADDRESS + "WebServices/WatchServices/LoginNameIsVaild";

	/**
	 * 验证手机号码是否有效
	 */
	public static final String MOBILE_NUMBER_ISVAILD = NOW_ADDRESS + "WebServices/WatchServices/MobileNumberIsVaild";
	/**
	 * 版本检查更新
	 */
	public static final String UP_DATE_FROM_SERVER = NOW_ADDRESS + "WebServices/WatchServices/CheckWatchVersion";


	/**
	 * 忘记密码
	 */
	public static final String FORGET_PASSWORD = NOW_ADDRESS + "WebServices/WatchServices/ForgetPassword";

	/**
	 * 修改密码
	 */
	public static final String CHANGE_PASSWORD = NOW_ADDRESS + "WebServices/WatchServices/ChangePassword";

	/**
	 * 查询当天运动数据
	 */
	public static final String QUERY_SPORTS_DATA_BYDAY = NOW_ADDRESS + "WebServices/WatchServices/QuerySportsDataByDay";

	/**
	 * 保存今天运动数据
	 */
	public static final String SYNC_SPORTS_DATATODAY = NOW_ADDRESS + "WebServices/WatchServices/SyncSportsDataToday";

	/**
	 * 删除当天的运动数据
	 */
	public static final String DELETE_SPORT_DATA_TODAY = NOW_ADDRESS + "WebServices/WatchServices/DeleteSportDataToday";


	//验证注册短信
	public static final String MOBILEMSG_VALIDATE = NOW_ADDRESS + "WebServices/WatchServices/CheckCode";
	//注册
	public static final String USER_REGISTER = NOW_ADDRESS + "WebServices/WatchServices/Join";

	//忘记密码第一步
	public static final String USER_FORGOT_PASSWORD_ONE = NOW_ADDRESS + "WebServices/WatchServices/ForgetPassword";
	//忘记密码第二步
	public static final String USER_FORGOT_PASSWORD_TWO = NOW_ADDRESS + "WebServices/WatchServices/ForgetPassword";




}
