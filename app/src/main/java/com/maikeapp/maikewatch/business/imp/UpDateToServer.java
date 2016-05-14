package com.maikeapp.maikewatch.business.imp;

import android.util.Log;

import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.util.NetWorkUtil;

import org.json.JSONObject;

/**
 * Created by aaa on 2016/5/13.
 */
public  class UpDateToServer  {
    public  String setInfoToServer(User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_info = new JSONObject();
        _json_info.put("sLoginName", mUser.getLoginName());
        _json_info.put("sSex", ""+mUser.getSex());
        _json_info.put("dtBirthday", ""+mUser.getAge());
        _json_info.put("iHeight", mUser.getHeight());
        _json_info.put("iWeight", mUser.getWeight());
//        Log.e(CommonConstants.LOGCAT_TAG_NAME+"_getUserLogin",_json_info.toString());
        _result = NetWorkUtil.getResultFromUrlConnection(CommonConstants.SET_PERSONAL, _json_info.toString(), "");
        return _result;
    }

}
