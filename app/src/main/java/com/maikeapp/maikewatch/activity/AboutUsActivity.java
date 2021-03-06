package com.maikeapp.maikewatch.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.AppVersion;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.DownUtil;
import com.maikeapp.maikewatch.util.JsonUtils;
import com.maikeapp.maikewatch.util.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 关于我们
 */
public class AboutUsActivity extends AppCompatActivity {

    private static final int UPDATE_APP = 45;
    private static final int NO_UPDATA_APP = 46;
    private static final int UPDATE_COMPLETE = 47;
    private static final int UPDATE_PROGRESS = 48;
    private static final int ISVALID = 49;
    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private String m_title = "关于软件";
    private User mUser;//用户信息
    private static ProgressDialog mProgressDialog = null;
    private ProgressDialog progressDialog;
    private TextView mTvWatchVersion;//固件版本
    private TextView mTvAppVersion;//当前app版本
    private LinearLayout mLineCheckNewVersion;//检查新版本
    private CheckBox mCBIsAutoCheck;//是否检查新版本
    // 是否终止下载
    private boolean isInterceptDownload = false;
    //进度条显示数值
    private int progress = 0;
    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private String mApkPath;
    private String mVersionName;
    private int mVersionCode;
    private ProgressDialog dialog;
    private DownUtil mDownUtils;
    private ProgressBar mProgressBar;
    private SharedPreferences mSP;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mProgressDialog!=null){
                mProgressDialog.dismiss();
            }
            int flag = msg.what;
            if (flag == 0) {
                String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                try {
                    Toast.makeText(AboutUsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (flag == UPDATE_APP) {
                showUpDateDialog();
            } else if (flag == NO_UPDATA_APP) {
                showNoUpDateTip();
            } else if (flag == UPDATE_PROGRESS) {
                // 更新进度情况
                mProgressBar.setProgress(progress);
            } else if (flag == UPDATE_COMPLETE) {
                mProgressBar.setVisibility(View.INVISIBLE);
                // 安装apk文件
                installApk();
            }else if (flag == ISVALID){
                Toast.makeText(AboutUsActivity.this, "当前版本不可用请尽快更新", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private String mSize;
    private String mVersionName1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        mSP = getSharedPreferences("config", MODE_PRIVATE);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView) findViewById(R.id.iv_common_back);
        mTvCommonTitle = (TextView) findViewById(R.id.tv_common_title);
        mTvWatchVersion = (TextView) findViewById(R.id.tv_about_us_watch_version);
        mTvAppVersion = (TextView) findViewById(R.id.tv_app_version);
        mLineCheckNewVersion = (LinearLayout) findViewById(R.id.line_about_us_check_new_version);
        mCBIsAutoCheck = (CheckBox) findViewById(R.id.cb_about_us_isauto_check_update);
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        mUser = CommonUtil.getUserInfo(this);
        if (mUser != null) {
            String _watchVersion = mUser.getWatchVersion();
            mTvWatchVersion.setText(_watchVersion);
        }
        //获取版本信息
        mVersionName = "v" + CommonUtil.getAppVersion(this).getVersionName();
        mVersionCode = CommonUtil.getAppVersion(this).getVersionCode();
        mTvAppVersion.setText(mVersionName);
        //初始化CheckBok
        boolean _autoUpdate = mSP.getBoolean("autoUpdate", true);
        if (_autoUpdate) {
            mCBIsAutoCheck.setChecked(true);
        } else {
            mCBIsAutoCheck.setChecked(false);
        }
    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsActivity.this.finish();
            }
        });
        mLineCheckNewVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出加载进度条
                getVersionFromService(mVersionCode + "", mVersionName);
                mProgressDialog = ProgressDialog.show(AboutUsActivity.this, "请稍等", "正在玩命检查中...", true, true);
            }
        });
        //设置是否自动更新
        mCBIsAutoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSP.edit().putBoolean("autoUpdate", true).apply();
                    mCBIsAutoCheck.setChecked(true);
                    Log.e("isChecked", isChecked + "");
                } else {
                    mSP.edit().putBoolean("autoUpdate", false).apply();
                    Log.e("isChecked", isChecked + "");
                    mCBIsAutoCheck.setChecked(false);
                }
            }
        });
    }

    private void showNoUpDateTip() {
        Toast.makeText(AboutUsActivity.this, "当前为最新版本,无需更新", Toast.LENGTH_SHORT).show();
    }

    private void showUpDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutUsActivity.this);

        builder.setTitle("版本更新");
        builder.setMessage("新版本:"+mVersionName1+" , 文件大小:"+mSize);
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 弹出下载框
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 弹出下载框
     */
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutUsActivity.this);
        builder.setCancelable(false);//设置点击外部不消失
        builder.setTitle("版本更新中...");
        final LayoutInflater inflater = LayoutInflater.from(AboutUsActivity.this);
        View v = inflater.inflate(R.layout.progress_dialog, null);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pb_update_progress);
        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //终止下载
                isInterceptDownload = true;
            }
        });
        builder.create().show();
        //下载apk
        downloadApk();
    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        //开启另一线程下载
        Thread downLoadThread = new Thread(downApkRunnable);
        downLoadThread.start();
    }

    /**
     * 从服务器下载新版apk的线程
     */
    private Runnable downApkRunnable = new Runnable() {
        @Override
        public void run() {
            if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                //如果没有SD卡
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutUsActivity.this);
                builder.setTitle("提示");
                builder.setMessage("当前设备无SD卡，数据无法下载");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return;
            } else {
                try {
                    //服务器上新版apk地址
                    URL url = new URL(mApkPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                    if (!file.exists()) {
                        //如果文件夹不存在,则创建
                        file.mkdir();
                    }
                    //下载服务器中新版本软件（写文件）
                    String apkFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/new.apk";
                    File ApkFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(ApkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int numRead = is.read(buf);
                        count += numRead;
                        //更新进度条
                        progress = (int) (((float) count / length) * 100);
                        handler.sendEmptyMessage(UPDATE_PROGRESS);
                        if (numRead <= 0) {
                            //下载完成通知安装
                            handler.sendEmptyMessage(UPDATE_COMPLETE);
                            break;
                        }
                        fos.write(buf, 0, numRead);
                        //当点击取消时，则停止下载
                    } while (!isInterceptDownload);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 安装apk
     */
    private void installApk() {
        // 获取当前sdcard存储路径
        File apkfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/new.apk");
//        if (!apkfile.exists()) {
//            return;
//        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        // 安装，如果签名不一致，可能出现程序未安装提示
        i.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())), "application/vnd.android.package-archive");
        AboutUsActivity.this.startActivity(i);
    }

    /**
     * 得到服务器的版本
     *
     * @return 返回appCode
     */
    private void getVersionFromService(final String VersionCode, final String VersionName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String _serverResult = mUserBusiness.getUpdateFromServer(VersionName, VersionCode);
                    if (_serverResult == null){
                        handler.sendEmptyMessage(NO_UPDATA_APP);
                    }
                    JSONObject _object = new JSONObject(_serverResult);
                    Boolean success = JsonUtils.getBoolean(_object,"Success");
//                    Boolean Message = JsonUtils.getBoolean(_object, "Message");

                    if (success) {//判断有版本更新
                        String _datas = JsonUtils.getString(_object,"Datas");
                        JSONObject _dataJson = new JSONObject(_datas);

//                        //判断当前版本是否可用
//                        String IsValid = JsonUtils.getString(_dataJson,"IsValid");
//                        if (IsValid.contains("0")){
//                            handler.sendEmptyMessage(ISVALID);
//                        }
                        mApkPath = "https://" + JsonUtils.getString(_dataJson,"Path");
                        mSize = JsonUtils.getString(_dataJson,"FileSize");
                        mVersionName1 = JsonUtils.getString(_dataJson,"AppVersionName");
                        Log.e("mApkPath", mApkPath + "YZP");
                        handler.sendEmptyMessage(UPDATE_APP);
                    } else {
                        handler.sendEmptyMessage(NO_UPDATA_APP);
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(), handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
