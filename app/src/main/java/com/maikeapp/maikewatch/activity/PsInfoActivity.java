package com.maikeapp.maikewatch.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;
import com.maikeapp.maikewatch.util.NetWorkUtil;
import com.maikeapp.maikewatch.view.CustomSmartImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * dev-test2
 */
public class PsInfoActivity extends AppCompatActivity {
    //男女性别状态
    private static final int PERSON_MAN = 1;
    private static final int PERSON_WOMAN = 0;
    //头像选择相关变量
    private String mpicName = "touxiang.jpg";
    private String mPicPath = Environment.getExternalStorageDirectory().getPath()+"/";
    private File tempFile = new File(Environment.getExternalStorageDirectory(),
            getPhotoFileName());
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private TextView mTvCommonAction;//编辑
    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private CustomSmartImageView mUserHead;//用户头像
    private TextView mTvLoginName;//用户名
    private String m_title = "个人信息";
    private String m_action = "完成";
    private SharedPreferences sp;
    private User mUser;
    //用户信息
    private TextView mEtAge;
    private TextView mEtHeight;
    private TextView mEtWeight;
    private RadioGroup mRgSax;
    private int mSax = 0;
    private RadioButton mRbMan, mRbWoman;
    private String mPhotoUrl;
    private ProgressDialog mProgressDialog;
    private NumberPicker mNpselect;
    private String mUserimagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_info);
        //判断文件夹是否存在,不存在则创建
        File file = new File(mPicPath);
        if (!file.exists()) {
            file.mkdir();
        }
        initView();
        initData();
        setListener();
    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView) findViewById(R.id.iv_common_back);
        mTvCommonTitle = (TextView) findViewById(R.id.tv_common_title);
        mTvCommonAction = (TextView) findViewById(R.id.tv_common_action);
        //用户信息
        mRgSax = (RadioGroup) findViewById(R.id.rg_ps_info_sax);
        mRbMan = (RadioButton) findViewById(R.id.rb_ps_info_man);
        mRbWoman = (RadioButton) findViewById(R.id.rb_ps_info_woman);
        mUserHead = (CustomSmartImageView) findViewById(R.id.iv_ps_info_userhead);
        mTvLoginName = (TextView) findViewById(R.id.tv_ps_info_loginname);
        mEtAge = (TextView) findViewById(R.id.et_ps_info_age);
        mEtHeight = (TextView) findViewById(R.id.et_ps_info_height);
        mEtWeight = (TextView) findViewById(R.id.et_ps_info_weight);
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        mTvCommonAction.setText(m_action);
        mUser = CommonUtil.getUserInfo(this);
            //设置头像,本地没有就用默认头像
            if (mUser != null) {
                mUserHead.setImageUrl(mUser.getPortraits(), R.drawable.pscenter_userinfo_headpic);
            } else {
                mUserHead.setImageResource(R.drawable.pscenter_userinfo_headpic);
            }

        if (mUser != null) {
            mTvLoginName.setText(mUser.getLoginName());
            //SAX判断
            if (mUser.getSex() == PERSON_MAN) {
                mRbMan.setChecked(true);
                mSax = PERSON_MAN;
            } else {
                mRbWoman.setChecked(true);
                mSax = PERSON_WOMAN;
            }
            //填充其他信息
            mEtAge.setText("" + mUser.getBirthday());
            mEtHeight.setText("" + mUser.getHeight());
            mEtWeight.setText("" + mUser.getWeight());
        }
    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PsInfoActivity.this.finish();
            }
        });
        //完成的点击事件
        mTvCommonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPersonUserData();
                mProgressDialog = ProgressDialog.show(PsInfoActivity.this, "请稍等", "正在玩命设置中...", true, true);
            }
        });
        //男女选择事件
        mRgSax.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_ps_info_man:
                        mSax = PERSON_MAN;
                        break;
                    case R.id.rb_ps_info_woman:
                        mSax = PERSON_WOMAN;
                        break;
                }
            }
        });
        //头像点击事件
        mUserHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog();
            }
        });
        //选择年龄
        mEtAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(18, 120, 8, "岁");
            }
        });
        //选择身高
        mEtHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(170, 250, 80, "厘米");
            }
        });
        //选择体重吧
        mEtWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(50, 150, 30, "千克");
            }
        });
    }

    /**
     * 弹出选择框
     */
    private void showSelectDialog(int CurrentValue, int MaxValue, int MinValue, final String unit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PsInfoActivity.this);
        final LayoutInflater inflater = LayoutInflater.from(PsInfoActivity.this);
        View v = inflater.inflate(R.layout.num_picker_dialog, null);
        mNpselect = (NumberPicker) v.findViewById(R.id.np_select);
        TextView textView = (TextView) v.findViewById(R.id.tv_unit);
        mNpselect.setMinValue(MinValue);//初始化设置属性
        mNpselect.setMaxValue(MaxValue);
        mNpselect.setValue(CurrentValue);
        textView.setText(unit);
        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int value = mNpselect.getValue();
                if (unit.contains("厘米")) {
                    mEtHeight.setText(value+"");
                }else if (unit.contains("岁")){
                    mEtAge.setText(value+"");
                }else if (unit.contains("千克")){
                    mEtWeight.setText(value+"");
                }
            }
        });
        builder.create().show();
    }

    Dialog alertDialog;
    private void showOptionDialog() {
        // 取得自定义View
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View _OptionView = layoutInflater.inflate(R.layout.layout_photo_option, null);
        TextView _camera = (TextView) _OptionView.findViewById(R.id.tv_camera);
        TextView _cancle = (TextView) _OptionView.findViewById(R.id.tv_cancel);
        TextView _photo = (TextView) _OptionView.findViewById(R.id.tv_photo);
        //拍照选取图片的点击事件
        _camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                _cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(tempFile));
                startActivityForResult(_cameraIntent, PHOTO_REQUEST_TAKEPHOTO);
                alertDialog.dismiss();
            }
        });

        //选取图库图片的点击事件
        _photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "Android.intent.action.GET_CONTENT"
                innerIntent.setType("image/*"); // 查看类型
                // StringIMAGE_UNSPECIFIED="image/*";详细的类型在com.google.android.mms.ContentType中
                Intent wrapperIntent = Intent.createChooser(innerIntent, null);
                startActivityForResult(wrapperIntent, PHOTO_REQUEST_GALLERY);
                alertDialog.dismiss();
            }
        });
        //取消事件
        _cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = new AlertDialog.Builder(this).
                setView(_OptionView).
                create();
        alertDialog.show();
    }

    /**
     *  处理图片的剪辑
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
                startPhotoZoom(Uri.fromFile(tempFile));
                break;
            case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
                // 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                if (data != null)
                    startPhotoZoom(data.getData());
                break;
            case PHOTO_REQUEST_CUT:// 返回的结果
                if (data != null)
                    // setPicToView(data);
                    sentPicToNext(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 将进行剪裁后的图片传递到下一个界面上
    private void sentPicToNext(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            saveBitmap(photo);  //保存BitMap到本地
            //上传图片到服务器
            sendPicToServer();
            if (photo == null) {
                mUserHead.setImageResource(R.drawable.pscenter_userinfo_headpic);
            } else {
                mUserHead.setImageBitmap(photo);
            }

            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] photodata = baos.toByteArray();
                System.out.println(photodata.toString());
            } catch (Exception e) {
                e.getStackTrace();
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 上传图片到服务器
     */
    private void sendPicToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(mPicPath + mpicName);
                    String _withPhoto = NetWorkUtil.getResultFromUrlConnectionWithPhoto(CommonConstants.UPLOAD_IMAGE, null, mpicName, mUser.getLoginVerifyCode(), file);
                   //解析出上传图片的地址
                    JSONObject _result =new JSONObject(_withPhoto);
                    String _datas = JsonUtils.getString(_result, "Datas");
                    String _message = JsonUtils.getString(_result, "Message");
                    JSONObject _userimage = new JSONObject(_datas);
                    String _userimagePath =  JsonUtils.getString(_userimage, "userimage");
                    mUser.setPortraits(_userimagePath);
                    CommonUtil.saveUserInfo(mUser,PsInfoActivity.this);//更新本地信息
                    CommonUtil.sendErrorMessage(_message, handler);

                } catch (ServiceException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(), handler);
                }catch (Exception e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage("上传图片失败，数据异常", handler);
                }
            }
        }).start();
    }


    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * 保存bitmap为File文件
     */
    public void saveBitmap(Bitmap bm) {
        File f = new File(mPicPath, mpicName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
//            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 同步数据-设置手表个人信息
     */
    private void setPersonUserData() {
        //用子线程上传数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //判断是否登录
                if (mUser != null) {
                    mUser.setBirthday(Integer.parseInt(mEtAge.getText().toString()));
                    mUser.setSex(mSax);
                    mUser.setHeight(Integer.parseInt(mEtHeight.getText().toString()));
                    mUser.setWeight(Integer.parseInt(mEtWeight.getText().toString()));
                    try {
                        CommonUtil.saveUserInfo(mUser, PsInfoActivity.this);//更新数据
                        Log.e("user数据", mUser.toString() + "");
                        //上传数据到服务器
                        String _set_result = mUserBusiness.setInfoToServer(mUser);
                        //更新本地数据
                        CommonUtil.saveUserInfo(mUser, PsInfoActivity.this);
                        //加载到内存中
                        mUser = CommonUtil.getUserInfo(PsInfoActivity.this);
                        Log.e("上传个人信息返回的数据", _set_result);
                        JSONObject _json_result = new JSONObject(_set_result);
                        boolean _success = JsonUtils.getBoolean(_json_result, "Success");
                        if (_success) {
                            // 同步完成
                            handler.sendEmptyMessage(CommonConstants.FLAG_SET_TARGET_SUCCESS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonUtil.sendErrorMessage("设置个人信息失败，请检查网络", handler);
                    }
                } else {
                    CommonUtil.sendErrorMessage("请先登录", handler);
                }
            }
        }).start();
    }


    //处理消息队列
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    try {
                        Toast.makeText(PsInfoActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommonConstants.FLAG_SET_TARGET_SUCCESS:
                    //   setTargetCompleted();//操作完成
                    mProgressDialog.dismiss();
                    Toast.makeText(PsInfoActivity.this, "操作完成", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }


    };
}
