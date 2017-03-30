package com.example.wangxin.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.BD09LL;
import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.BD09_MC;
import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.GCJ02;
import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.WGS84;
import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;

/**
 * Created by wAngxIn on 2017/3/15.
 */

public class ActivityGoWhere extends AppCompatActivity {


    private static final String APP_FOLDER_NAME = "MYSimpleDemo";

    private Button mWgsNaviBtn = null;
    /*private Button mGcjNaviBtn = null;
    private Button mBdmcNaviBtn = null;
    private Button mDb06ll = null;*/
    private String mSDCardPath = null;

    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";

    private final static String authBaseArr[] =
            { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION };
    private final static String authComArr[] = { Manifest.permission.READ_PHONE_STATE };
    private final static int authBaseRequestCode = 1;
    private final static int authComRequestCode = 2;

    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gowhere);
        BNOuterLogUtil.setLogSwitcher(true);
        mWgsNaviBtn = (Button) findViewById(R.id.wgsNaviBtn);
//        mGcjNaviBtn = (Button) findViewById(R.id.gcjNaviBtn);
//        mBdmcNaviBtn = (Button) findViewById(R.id.bdmcNaviBtn);
//        mDb06ll = (Button) findViewById(R.id.mDb06llNaviBtn);
        BNOuterLogUtil.setLogSwitcher(true);


        if (initDirs()) {
            initNavi();
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initListener() {
        if (mWgsNaviBtn != null) {
            mWgsNaviBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Log.i("ActivityGoWhere",""+hasInitSuccess);
                    if (!hasInitSuccess) {
                        Toast.makeText(ActivityGoWhere.this, "还未初始化!", Toast.LENGTH_SHORT).show();
                    }
                    // 权限申请
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        // 保证导航功能完备
                        Log.i("ActivityGoWhere",hasCompletePhoneAuth()+"+"+hasRequestComAuth);
                        if (!hasCompletePhoneAuth()) {
                            if (!hasRequestComAuth) {
                                hasRequestComAuth = true;
                                ActivityGoWhere.this.requestPermissions(authComArr, authComRequestCode);
                                Toast.makeText(ActivityGoWhere.this, "完备的权限!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ActivityGoWhere.this,ActivityPathPlanning.class));
                                return;
                            } else {
                                Toast.makeText(ActivityGoWhere.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();


                            }
                        }

                    }else{
                        startActivity(new Intent(ActivityGoWhere.this,ActivityPathPlanning.class));
                    }
                }

            });
        }
        /*if (mGcjNaviBtn != null) {
            mGcjNaviBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManager.isNaviInited()) {
                        routeplanToNavi(GCJ02);
                    }
                }

            });
        }
        if (mBdmcNaviBtn != null) {
            mBdmcNaviBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (BaiduNaviManager.isNaviInited()) {
                        routeplanToNavi(BD09_MC);
                    }
                }
            });
        }

        if (mDb06ll != null) {
            mDb06ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManager.isNaviInited()) {
                        routeplanToNavi(BD09LL);
                    }
                }
            });
        }*/

    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    String authinfo = null;

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    // showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    // showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            // showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            // showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

    public void showToastMsg(final String msg) {
        ActivityGoWhere.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ActivityGoWhere.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                ActivityGoWhere.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ActivityGoWhere.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                Toast.makeText(ActivityGoWhere.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                hasInitSuccess = true;
                initSetting();
                initListener();

            }

            public void initStart() {
                Toast.makeText(ActivityGoWhere.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(ActivityGoWhere.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }

        }, null, ttsHandler, ttsPlayStateListener);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private BNRoutePlanNode.CoordinateType mCoordinateType = null;





    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(ActivityGoWhere.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavi();
        } else if (requestCode == authComRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                }
            }
            initNavi();
        }

    }
}
