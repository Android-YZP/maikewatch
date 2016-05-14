package com.maikeapp.maikewatch.business.imp;

import org.json.JSONObject;

import android.util.Log;

import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.util.ConvertUtil;
import com.maikeapp.maikewatch.util.NetWorkUtil;


public class UserBusinessImp implements IUserBusiness {




	/**
	 * 用户登录
	 * @param pPhone
	 * @param pPassword
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getUserLogin(String pPhone, String pPassword)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("Mobile", pPhone);
		_json_args.put("Password", pPassword);

		_json_args.put("ClientVersion", "1.0");
		_json_args.put("ClientType", "Phone");

		JSONObject _json_viewModel = new JSONObject();
		_json_viewModel.put("viewModel",_json_args);

		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_getUserLogin",_json_viewModel.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEWATCH");
		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_md5_value",_md5_value.substring(0,8));

		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.USER_LOGIN, _json_viewModel.toString(), _md5_value.substring(0,8));
		return _result;
	}

	/**
	 * 上传个人目标步数
	 * @param mUser
	 * @throws Exception
     */
	@Override
	public String setSportsTarget(User mUser) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("sLoginName", mUser.getLoginName());
		_json_args.put("iSportsTarget", ""+mUser.getSportsTarget());
		_json_args.put("MacAddress", ""+mUser.getMacAddress());
		_json_args.put("ClientVersion", "1.0");
		_json_args.put("ClientType", "Phone");


		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_getUserLogin",_json_args.toString());

		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.SET_SPORTS_TARGET, _json_args.toString(), "");
		return _result;
	}


	/**
	 * 获取tokenID
	 * @return
	 * @throws Exception
	 */
	public String getTokenID() throws Exception {
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEWATCH");
		String _result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.GET_TOKEN_ID,"", _md5_value.substring(0,8));
		return _result;
	}

	@Override
	public String getMobilemsgRegister(String phone) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("mobile", phone);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.MOBILEMSG_REGISTER, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}


	@Override
	public String getMobilemsgValidate(String phone, String smsCode)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("mobile", phone);
		_json_args.put("captcha", smsCode);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.MOBILEMSG_VALIDATE, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}


	@Override
	public String getUserRegister(String mPhone, String mCode, String username,
			String password) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("username", username);
		_json_args.put("mobile", mPhone);
		_json_args.put("password", password);
		_json_args.put("captcha", mCode);
		
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.USER_REGISTER, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}





	@Override
	public String getUserForgotPasswordOne(String phone) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("mobile", phone);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.USER_FORGOT_PASSWORD_ONE, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}


	@Override
	public String getUserForgotPasswordTwo(String mPhone, String smsCode,
			String newpwd, String newpwdAgain) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("mobile", mPhone);
		_json_args.put("newPwd", newpwd);
		_json_args.put("password", newpwdAgain);
		_json_args.put("captcha", smsCode);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.USER_FORGOT_PASSWORD_TWO, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}


	@Override
	public String getUserDetail(int userid) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("id", userid);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = NetWorkUtil.getResultFromUrlConnectionWithGet(CommonConstants.USER_DETAIL, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}


	@Override
	public String updateUserPwd(String oldpwd, String newpwd, String againpwd,
			User mUser) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("oldpwd", oldpwd);
		_json_args.put("pwd", newpwd);
		_json_args.put("repwd", againpwd);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//头信息-verifyCode保存在用户信息中
		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.MEMBER_PWD, _json_args.toString(), null);
		return _result;
	}

	@Override
	public String getUserSettingSecurityPhoneUpdate(String mPhone,
			String smsCode, User mUser) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("mobile", mPhone);
		_json_args.put("captcha", smsCode);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//头信息-verifyCode保存在用户信息中
		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.MEMBER_SECURITY_MOBILE_BIND, _json_args.toString(), null);
		return _result;
	}




	@Override
	public String updateUserSettingPsInfo(String _usersign,
			User mUser) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("userSignature", _usersign);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//头信息-verifyCode保存在用户信息中
		_result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.MEMBER_PROFILE_UPDATE, _json_args.toString(),null);
		return _result;
	}



	@Override
	public String checkAppUpdate() throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = NetWorkUtil.getResultFromUrlConnectionWithGet(CommonConstants.CHECK_APP_VERSION, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}
}
