package com.maikeapp.maikewatch.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.BlueToothController;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.fragment.HomeFragment;
import com.maikeapp.maikewatch.fragment.PscenterFragment;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;
import com.maikeapp.maikewatch.view.IndexTabBarLayout;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends FragmentActivity {
    private BlueToothController mController = new BlueToothController();//蓝牙帮助类
    private Toast mToast;
    public static final int REQUEST_CODE = 2;
    public static final int LOGIN_BACK_CODE = 100;
    private IndexTabBarLayout mIndexTabBarLayout;//底部整个控件
    private final static int FLAG_HOME = 0;
    private final static int FLAG_PSCENTER = 1;

    private Fragment mHomeFragment;
    private Fragment mPscenterFragment;

    //App跟新所用到的
    private static final int UPDATE_COMPLETE = 147;
    private static final int UPDATE_PROGRESS = 148;
    private SharedPreferences mSP;
    private final static int UPDATE_APP = 149;
    private boolean isInterceptDownload = false;  // 是否终止下载
    private int progress = 0;    //进度条显示数值
    private ProgressBar mProgressBar;
    private IUserBusiness mUserBusiness = new UserBusinessImp();

    /**
     * 声明一个handler来跟进进度条
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_APP:
                    showUpdateDialog();
                    break;
                case UPDATE_PROGRESS:
                    // 更新进度情况
                    mProgressBar.setProgress(progress);
                    break;
                case UPDATE_COMPLETE:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    // 安装apk文件
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };
    private String mApkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
        checkNewVersion();
        //1-判断是否开启了蓝牙；未开启蓝牙，弹出是否开启蓝牙；已开启蓝牙，判断用户是否登录
        //2-已登录用户：判断用户是否绑定过手表，未绑定过，跳转到手表绑定界面（绑定之后把绑定的手表mac地址，上传服务端），
        //3-若已绑定手表，在首页直接同步一次数据，并上传服务器
        checkBlueTooth();
    }

    /**
     * 检查是否有新版本更新
     */
    private void checkNewVersion() {
        mSP = getSharedPreferences("config", MODE_PRIVATE);
        boolean isAotoUpdate = mSP.getBoolean("autoUpdate", true);//默认自动跟新版本
        if (isAotoUpdate) {
            getVersionFromService(CommonUtil.getAppVersion(this).getVersionCode()+"","v" + CommonUtil.getAppVersion(this).getVersionName());
        }
    }
    /**
     * 得到服务器的版本
     * @return 返回appCode
     */
    private void getVersionFromService(final String VersionCode, final String VersionName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String _serverResult = mUserBusiness.getUpdateFromServer(VersionName, VersionCode);
                    JSONObject _object = new JSONObject(_serverResult);
                    Log.e("升级程序所用到的返回值", _serverResult);
                    Boolean success = JsonUtils.getBoolean(_object, "Success");
                    if (success) {//判断有版本更新
                        String _datas = JsonUtils.getString(_object, "Datas");
                        JSONObject _dataJson = new JSONObject(_datas);
                        mApkPath = "http://" + JsonUtils.getString(_dataJson, "Path");
                        handler.sendEmptyMessage(UPDATE_APP);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 提示更新对话框
     * <p/>
     * 版本信息对象
     */
    private void showUpdateDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("版本更新");
        builder.setMessage("有新版本更新了");
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
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("版本更新中...");
        final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
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
    private void downloadApk(){
        //开启另一线程下载
        Thread downLoadThread = new Thread(downApkRunnable);
        downLoadThread.start();
    }

    /**
     * 从服务器下载新版apk的线程
     */
    private Runnable downApkRunnable = new Runnable(){
        @Override
        public void run() {
            if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                //如果没有SD卡
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
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
            }else{
                try {
                    //服务器上新版apk地址
                    URL url = new URL(mApkPath);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                    if(!file.exists()){
                        //如果文件夹不存在,则创建
                        file.mkdir();
                    }
                    //下载服务器中新版本软件（写文件）
                    String apkFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/new.apk";
                    File ApkFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(ApkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    do{
                        int numRead = is.read(buf);
                        count += numRead;
                        //更新进度条
                        progress = (int) (((float) count / length) * 100);
                        handler.sendEmptyMessage(UPDATE_PROGRESS);
                        if(numRead <= 0){
                            //下载完成通知安装
                            handler.sendEmptyMessage(UPDATE_COMPLETE);
                            break;
                        }
                        fos.write(buf,0,numRead);
                        //当点击取消时，则停止下载
                    }while(!isInterceptDownload);
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
        File apkfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()  + "/new.apk");
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        // 安装，如果签名不一致，可能出现程序未安装提示
        i.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())), "application/vnd.android.package-archive");
        MainActivity.this.startActivity(i);
    }
    /**
     * 判断蓝牙是否开启
     *
     * @return
     */
    private void checkBlueTooth() {
        boolean isSupportBlueTooth = mController.isSupportBlueTooth();
        if (isSupportBlueTooth) {
            boolean isBlueToothOn = mController.getBlueToothStatus();
            if (isBlueToothOn) {
                //已开启蓝牙，判断用户是否登录和是否绑定手表
                checkUserLoginAndBind();//检查用户是否登录和是否绑定手表
            } else {
                //未开启蓝牙，询问是否开启蓝牙
                new AlertDialog.Builder(this)
                        .setTitle("您未开启蓝牙")
                        .setMessage("是否开启蓝牙？")
                        .setPositiveButton("开启", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //开启蓝牙
                                mController.turnOnBlueTooth(MainActivity.this, REQUEST_CODE);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                        .show();
            }
        } else {
            showToast("不支持蓝牙");
            return;
        }
    }

    /**
     * 检查用户是否登录和是否绑定手表
     */
    private void checkUserLoginAndBind() {
        User mUser = CommonUtil.getUserInfo(this);
        if (mUser != null) {
            //用户已登录，判断该用户是否绑定手表，未绑定，跳转到绑定手表界面；已绑定，直接同步一次数据

            boolean isBindWatch = mUser.isBindWatch();
            if (isBindWatch) {
                //同步手表数据（获取手表端数据，并上传到服务端，并在当前界面展示）
//                syncWatchData();
            } else {
                //去绑定
                Intent _intent = new Intent(MainActivity.this, BindWatchActivity.class);
                startActivity(_intent);
            }
        }

    }

    /**
     * 同步手表数据（获取手表端数据，并上传到服务端，并在当前界面展示）
     */
    private void syncWatchData() {
        showToast("正在同步数据...");
    }

    /**
     * 弹出提示信息
     *
     * @param text
     */
    private void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    private void initView() {
        mHomeFragment = new HomeFragment();
        initPageContent(mHomeFragment);
        findView();
    }

    private void findView() {
        mIndexTabBarLayout = (IndexTabBarLayout) findViewById(R.id.myIndexTabBarLayout);
    }

    private void initData() {

    }

    private void setListener() {
        //点击了某个菜单后
        mIndexTabBarLayout.setOnItemClickListener(new IndexTabBarLayout.IIndexTabBarCallBackListener() {
            @Override
            public void clickItem(int id) {
                switch (id) {
                    case R.id.index_home_item:
                        if (mHomeFragment == null) {
                            Log.d("jlj-maikewatch", "new mHomeFragment");
                            mHomeFragment = new HomeFragment();
                        }
                        initPageContent(mHomeFragment);
                        break;

                    case R.id.index_pscenter_item:
                        if (mPscenterFragment == null) {
                            Log.d("jlj-maikewatch", "new pscenterfragment");
                            mPscenterFragment = new PscenterFragment();
                        }
                        initPageContent(mPscenterFragment);
                        break;
                }
            }
        });
    }

    /**
     * 初始化第一页初始页(动态切换页面)
     */
    private void initPageContent(Fragment fragment) {
        FragmentManager _manager = getSupportFragmentManager();
        FragmentTransaction _ft = _manager.beginTransaction();
        _ft.replace(R.id.layout_main_frag, fragment);
        _ft.commit();
    }

    private long exitTime = 0;//上次系統退出時間

    /**
     * 第二次点击返回，退出
     */
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出麦客watch", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        MainActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //当发起请求蓝牙打开事件时，会告诉你用户选择的结果
            if (resultCode == RESULT_OK) {
                showToast("打开成功");
                checkUserLoginAndBind();//检查用户是否绑定手表
            } else {
                showToast("打开失败");
            }
        }

    }
}
