package com.yhy.drawtracedemo.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.yhy.drawtracedemo.R;
import com.yhy.drawtracedemo.util.LogUtil;
import com.yhy.drawtracedemo.util.WalkUtil;

import java.util.ArrayList;

/**
 * Created by yhy on 2016/5/4.
 */
public class DrawTraceActivity extends BaseActivity implements LocationSource, AMapLocationListener {


    private static final int LOCATION_TIME_INTERVAL = 4000;

    Bundle mSavedInstanceState;
    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;
    private OnLocationChangedListener mLocationLinstener;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;


    private ArrayList<LatLng> mLocationList = new ArrayList<LatLng>();

    private boolean mIsFirstLocation = true;
    private Marker mMarkMyLocation;
    private double mLocatinLat;
    private double mLocationLon;
    private double mBestLat;
    private double mBestLon;
    private double currLength;
    private long lastTime=0;
    private long currTime=0;
    private int errorCnt = 0;
    private long minusTime;
    //当前经纬度
    private LatLng mCurrentLatLng;
    //上次经纬度
    private LatLng mLastLatLng;
    private LatLng currLa;
    private LatLng lastLa = new LatLng(0,0);
    private LatLng overLa = new LatLng(0,0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_trace);
//        mapView = (MapView) findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mSavedInstanceState = savedInstanceState;

    }



    @Override
    protected void initView() {
        super.initView();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(mSavedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    aMap.setMapType(AMap.MAP_TYPE_NAVI);
                    setMyLocationStyleIcon();
//                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(laQuick, loQuick), 17));
                }
            });

        }
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setLogoPosition(2);//设置高德地图logo位置
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setTiltGesturesEnabled(false);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);
        initLocation();

    }


    private void setMyLocationStyleIcon() {
//		 自定义系统定位小蓝点

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        ImageView iv = new ImageView(this);
        FrameLayout.LayoutParams fmIv = new FrameLayout.LayoutParams(1, 1);
        iv.setImageResource(R.mipmap.location);
        iv.setLayoutParams(fmIv);
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(iv);
        myLocationStyle.myLocationIcon(markerIcon);// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
//				myLocationStyle.
        myLocationStyle.anchor(0.5f, 0.9f);
        aMap.setMyLocationStyle(myLocationStyle);

//        aMap.setMyLocationEnabled(true);
////				// 设置定位的类型为 跟随模式
//        aMap.setMyLocationType(AMap.MAP_TYPE_NORMAL);

    }


    private void initLocation() {

        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = new AMapLocationClientOption();

        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        locationOption.setNeedAddress(true);
        // 设置定位监听
        locationClient.setLocationListener(this);
        //每两秒定位一次
        locationOption.setInterval(LOCATION_TIME_INTERVAL);
        locationOption.setOnceLocation(false);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationLinstener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mLocationLinstener = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {

                if (mLocationLinstener != null) {
                    mLocationLinstener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                }
                mLocatinLat = aMapLocation.getLatitude();
                mLocationLon = aMapLocation.getLongitude();

                if (mIsFirstLocation) {
                    mIsFirstLocation = false;
//                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocatinLat, mLocationLon), 17));
                    setMyStopLoca(new LatLng(mLocatinLat, mLocationLon));
////                    setMyLocationStyleIcon();
////                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude, longitude)));
//                    setMyStopLoca(new LatLng(latitude, longitude));
//
                    mLocationList.add(new LatLng(mLocatinLat, mLocationLon));
//                    mTrueLat = latitude;
//                    mTrueLon = longitude;
//
//                    mSendLat = latitude;
//                    mSendLon = longitude;
//
//
//                    mCenterLat = latitude;
//                    mCenterLon = longitude;
                } else {
//
                    if (mLastLatLng == null) {
                        mLastLatLng = new LatLng(mLocatinLat, mLocationLon);
                    }else {
                        findBest();
                    }
                }

            }
        } else {
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//            LogUtil.d("location Error, ErrCode:"
//                    + aMapLocation.getErrorCode() + ", errInfo:"
//                    + aMapLocation.getErrorInfo());
        }
    }


    private void setMyStopLoca(final LatLng latlng) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17f));

        if(mMarkMyLocation!=null){
            mMarkMyLocation.destroy();
            mMarkMyLocation = null;
        }


        if (mMarkMyLocation == null) {
            final MarkerOptions markerOptions = new MarkerOptions();
            //markerOptions.snippet(dogId);
            // 设置Marker点击之后显示的标题
            markerOptions.setFlat(false);
            markerOptions.anchor(0.5f, 0.7f);
            markerOptions.zIndex(25);
            markerOptions.zIndex(90);
            ImageView iv = new ImageView(this);
            FrameLayout.LayoutParams fmIv = new FrameLayout.LayoutParams(100, 100);
            iv.setImageResource(R.mipmap.location);
            iv.setLayoutParams(fmIv);
            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(iv);
            markerOptions.icon(markerIcon);
            markerOptions.position(latlng);
            mMarkMyLocation = aMap.addMarker(markerOptions);

        }else {
            mMarkMyLocation.setPosition(latlng);
        }
    }


    private Polyline TotalLine;
    private void DrawRideTraceTotal(){
        if(TotalLine!=null){
            TotalLine.remove();
            TotalLine = null;
        }
        PolylineOptions polylineOptions=new PolylineOptions();
        polylineOptions.addAll(mLocationList);
        polylineOptions.visible(true).width(30).zIndex(200);
//        加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
        polylineOptions.colorValues(WalkUtil.getColorList(mLocationList.size()/144+1,this));
        //加上这个属性，表示使用渐变线
//        polylineOptions.useGradient(true);
        TotalLine = aMap.addPolyline(polylineOptions);
    }
    private boolean mOver = false;
    private void findBest(){

        currLa = new LatLng(mLocatinLat,mLocationLon);

        currTime = System.currentTimeMillis();
        LogUtil.d("test walk la is"+currLa+"");
        LogUtil.d("test walk last is"+lastLa+"");
        String move;
//        if(mIsMove){
//            move = "move中";
//        }else {
//            move = "臭不要脸的静止中";
//        }

//        LogUtil.d("yhy 位置来啦，给不给轩哥发？位置是"+ currLa.toString()+"但是"+move);
//        MyToast.showMsg(MainActivity.this, "位置来啦，给不给轩哥发？位置是" + currLa.toString());
        currLength = AMapUtils.calculateLineDistance(
                lastLa, currLa);
        //TODO 传感器检测
//        if(mIsMove){
            if(!lastLa.equals(currLa)){
//            LogUtil.d("yhy 发给轩哥了");
//
                minusTime = currTime-lastTime;
//            if(minusTime>=20000&&!firstOverTime){
//                firstOverTime = true;
//                overTimeLat = currLa;
////                        if(overLength>((minusTime+1)/1000)*5){
////                            mOver = true;
////                        }
//            }else if(minusTime>=20000){
//                overLength = AMapUtils.calculateLineDistance(
//                        overTimeLat, currLa);
//                if(overLength<=20){
//                    mOver = true;
//                }else {
//                    lastTime = currTime;
//                }
//                firstOverTime = false;
//            }

                LogUtil.d("yhy time testzzz"+minusTime);
//            if(currLength<(errorCnt+1)*5||errorCnt>=20){
                if(currLength<((minusTime+1)/1000)*5){
//                if(currLength<((minusTime+1)/1000)*5||mOver){
                    errorCnt = 0;
                    lastLa = currLa;
                    lastTime = currTime;

                    mBestLat = mLocatinLat;
                    mBestLon = mLocationLon;

                    mCurrentLatLng = new LatLng(mBestLat, mBestLon);
                    LogUtil.d("yhy 发给轩哥了>>>>>>>>>" + currLength);
                    mLocationList.add(mCurrentLatLng);
                    mMarkMyLocation.setPosition(mCurrentLatLng);
                    DrawRideTraceTotal();
//                lastTime = currTime;
//                    everyTime = currTime;
                }else if(minusTime>=20000){


                    if(mOver){

                        if(!overLa.equals(currLa)){
//                   if(mOverCnt!=mLocationCnt){
                            errorCnt = 0;
                            lastLa = currLa;
                            lastTime = currTime;
                            LogUtil.d("yhy 确定大于距离，发给轩哥>>>>>>>>>" + currLength);
//                            Toast.makeText(MainActivity.this,"确定大于距离，发给轩哥>>>>>>>>>" + currLength,Toast.LENGTH_SHORT).show();


                            mBestLat = mLocatinLat;
                            mBestLon = mLocationLon;

                            mCurrentLatLng = new LatLng(mBestLat, mBestLon);
                            mLocationList.add(mCurrentLatLng);
                            mMarkMyLocation.setPosition(mCurrentLatLng);
                            DrawRideTraceTotal();
//                lastTime = currTime;
//                            everyTime = currTime;
                            mOver = false;
                        }else {
                            errorCnt = 0;
//                       lastLa = currLa;
                            lastTime = currTime;
                            mOver = false;
                        }
                    }else {
                        if(currLength>((minusTime+1)/1000)*5){
                            mOver = true;
                            overLa = currLa;
                            LogUtil.d("yhy 第一次大于距离"+currLength);
//                            mOverCnt = mLocationCnt;
                        }
                    }



                } else {
                    errorCnt++;
                    LogUtil.d("yhy +++++++++++++++++++++++++++++++++++++++++++++距离太大，是漂移，不发"+currLength+"定位是"+mLocatinLat+"^^^"+mLocationLon);
//                    Toast.makeText(MainActivity.this,"++++距离太大，是漂移，不发"+currLength,Toast.LENGTH_SHORT).show();
//                everyTime = currTime;
//                lastLa = currLa;
//                lastTime = currTime;
//                  currLa = lastLa;
                }

//            LogUtil.d("test walk last change is"+lastLa+"");


            }else {
                LogUtil.d("yhy -------------------------------------------------距离太小，没有移动，不发");
//                Toast.makeText(MainActivity.this,"------距离太小，没有移动，不发",Toast.LENGTH_SHORT).show();
                lastTime = currTime;
            }
//        }




    }




    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null !=locationClient ){
            locationClient.onDestroy();
        }
    }


}
