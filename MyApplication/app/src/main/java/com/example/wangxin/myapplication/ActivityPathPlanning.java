package com.example.wangxin.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.WGS84;

/**
 * Created by wAngxIn on 2017/3/15.
 */

public class ActivityPathPlanning extends AppCompatActivity {
    public static List<Activity> activityList = new LinkedList<Activity>();
    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;


    private BNRoutePlanNode.CoordinateType mCoordinateType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_path);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_one, R.id.tv_two, R.id.tv_three})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_one:
                break;
            case R.id.tv_two:
                break;
            case R.id.tv_three:
                if (BaiduNaviManager.isNaviInited()) {
                    routeplanToNavi(WGS84);
                }
                break;
        }
    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {

        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        sNode = new BNRoutePlanNode(116.251855, 39.90869, "瑞达大厦", null, coType);
        eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */
            Log.i("ActivityGoWhere", "" + activityList.size());
            for (Activity ac : activityList) {
                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
                    return;
                }
            }
            Intent intent = new Intent(ActivityPathPlanning.this, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(ActivityPathPlanning.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }
}
