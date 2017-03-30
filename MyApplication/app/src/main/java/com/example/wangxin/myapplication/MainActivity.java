package com.example.wangxin.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.tv_four)
    TextView tvFour;
    @BindView(R.id.tv_show)
    TextView tvShow;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    @BindView(R.id.iv_show)
    ImageView ivShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        /*tvOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity","one");
            }
        });*/

        addShortcut(this.getString(R.string.app_name));


        String num = "1111222233334444";
        // tvShow.setText(HideNumber.hideInfo(num));
        //可以绑定 activity fragmentactivity fragment Context(不建议去用)

        //Glide.with(this).load(imageUrl).asBitmap().into(ivShow);//显示gif静态图片
        int resourceId = R.mipmap.timg;

//        Glide
//                .with(this)
//                .load(resourceId)
//                .asGif().into(ivShow);

        //Picasso 加载图片慢，图片大尺寸，但是图片清晰
        String imageUrl = "http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg";
        Glide.with(this)
                .load(imageUrl)
                .into(ivShow);

        ivShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(MainActivity.this,ActivityFlexboxLayout.class);
               startActivity(intent);
            }
        });

       /* diskCacheStrategy(DiskCacheStrategy.ALL);// 让Glide既缓存全尺寸又缓存其他尺寸
         */


       /* //设置加载中以及加载失败图片
        Glide.with(this).load(imageUrl).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivShow);
        //设置跳过内存缓存
        Glide.with(this).load(imageUrl).skipMemoryCache(true).into(ivShow);
        //设置下载优先级
        Glide.with(this).load(imageUrl).priority(Priority.NORMAL).into(ivShow);
        //设置缓存策略
        // all:缓存源资源和转换后的资源
        // none:不作任何磁盘缓存
        //source:缓存源资源
        //result：缓存转换后的资源
        Glide.with(this).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivShow);
        //设置加载动画
        Glide.with(this).load(imageUrl).animate(R.anim.item_alpha_in).into(ivShow);
        //设置缩略图支持
       // 这样会先加载缩略图 然后在加载全图
        Glide.with(this).load(imageUrl).thumbnail(0.1f).into(ivShow);
        //设置加载尺寸
        Glide.with(this).load(imageUrl).override(800, 800).into(ivShow);
        //设置动态转换 fitCenter() centerCrop等函数也可以通过自定义Transformation，举例说明：比如一个人圆角转化器
        Glide.with(this).load(imageUrl).centerCrop().into(ivShow);

        Glide.with(this).load(imageUrl).transform(new GlideRoundTransform(this)).into(ivShow);

        //设置动态GIF加载方式
        Glide.with(this).load(imageUrl).asBitmap().into(ivShow);//显示gif静态图片
        Glide.with(this).load(imageUrl).asGif().into(ivShow);//显示gif动态图片

        //缓存的动态清理
        Glide.get(this).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
        Glide.get(this).clearMemory();//清理内存缓存  可以在UI主线程中进行*/


        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }


    }

    private void addShortcut(String name) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);

        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(MainActivity.this,
                        R.mipmap.ic_launcher));

        // 设置关联程序
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.setClass(MainActivity.this, MainActivity.class);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        addShortcutIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        sendBroadcast(addShortcutIntent);
    }

    private void requestLocation() {

        //定位
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        initLocation();
        boolean isWifi = mLocationClient.requestHotSpotState();
        Log.i("BaiduLocationApiDem", "isWifi" + isWifi);
        if (isWifi) {
            tvTwo.setText("移动热点");

        } else {
            tvTwo.setText("WIFI");
        }


    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);

    }

    @OnClick({R.id.tv_one, R.id.tv_two, R.id.tv_three, R.id.tv_four})
    public void show(View view) {
        Log.i("MainActivity", "view");
        switch (view.getId()) {
            case R.id.tv_one:
                Log.i("MainActivity", "one");
                Toast.makeText(this, "开始定位", Toast.LENGTH_SHORT).show();
                //开始定位
                mLocationClient.start();
                //tvOne.setText("11");
                break;
            case R.id.tv_two:
                //Toast.makeText(this, "two", Toast.LENGTH_SHORT).show();

                // tvTwo.setText("22");
                break;
            case R.id.tv_three:
                // Toast.makeText(this, "three", Toast.LENGTH_SHORT).show();
                // tvThree.setText("33");
                startActivity(new Intent(MainActivity.this,ActivityGoWhere.class));
                break;
            case R.id.tv_four:
                Toast.makeText(this, "关闭定位", Toast.LENGTH_SHORT).show();
                if (null != mLocationClient) {
                    mLocationClient.stop();
                }
                //tvFour.setText("44");
                break;
        }
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            //获取定位结果
            final StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\n国家 : ");
            sb.append(location.getCountry());    //国家

            sb.append("\n省 : ");
            sb.append(location.getProvince());    //省

            sb.append("\n市 : ");
            sb.append(location.getCity());    //市


            sb.append("\n县 : ");
            sb.append(location.getDistrict());    //县

            sb.append("\nlontitude : ");
            sb.append(location.getStreet());    //街道


            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度

            if (location.getLocType() == BDLocation.TypeGpsLocation) {

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                mLocationClient.stop();

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

            }

            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            final String adrress = sb.toString();
            tvShow.post(new Runnable() {
                @Override
                public void run() {
                    tvShow.setText(adrress);
                }
            });
            Log.i("BaiduLocationApiDem", adrress);
        }

        @Override
        public void onConnectHotSpotMessage(String connectWifiMac, int hotSpotState) {
            /*hotSpotState有以下三种情况：

            LocationClient.CONNECT_HPT_SPOT_TRUE - 连接的是移动热点

            LocationClient.CONNECT_HPT_SPOT_FALSE - 连接的非移动热点

            LocationClient.CONNECT_HPT_SPOT_UNKNOWN - 连接状态未知*/
           /* Log.i("BaiduLocationApiDem", "hotSpotState****" + hotSpotState);
            if (hotSpotState == LocationClient.CONNECT_HOT_SPOT_TRUE) {
                tvThree.setText("移动热点");

            } else if (hotSpotState == LocationClient.CONNECT_HOT_SPOT_FALSE) {
                tvThree.setText("非移动热点");

            } else if (hotSpotState == LocationClient.CONNECT_HOT_SPOT_UNKNOWN) {
                tvThree.setText("状态未知 ");

            }
*/

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
