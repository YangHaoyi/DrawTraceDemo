# DrawTraceDemo
高德地图轨迹回放带定位纠偏加彩虹渐变线


由于高德自身持续定位存在偏移状况，故摒弃了高德自身的定位点，改用自定义marker点作为定位点

加注轨迹纠偏函数，根据时间判定此次移动是否合乎标准

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
                            LogUtil.d("确定大于距离，发给服务端>>>>>>>>>" + currLength);
//                            Toast.makeText(MainActivity.this,"确定大于距离，发给服务端>>>>>>>>>" + currLength,Toast.LENGTH_SHORT).show();


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
        }
        
    如若连续定位时间超过20秒，则判定为移动，或者连续两次较大偏移，则判断为确定移动
    
    
    画线采取彩红线条，用户体验更加美观，如果选用高德自身渐变接口，效果并不是很理想，所以自行写渐变函数实现
    
    渐变线效果。
    
    
    程序实现定位实时记录，实时画线，捋通程序，你会发现悦跑圈，咕咚运动，不过如此。
    
    我的上架项目遛狗圈地址:http://android.myapp.com/myapp/detail.htm?apkName=com.lbt.staffy.walkthedog
    
  
  欢迎互相探讨学习。
  
  ![image](https://github.com/qweyhy/DrawTraceDemo/blob/master/screenshot/draw.jpg)
        
   
    
