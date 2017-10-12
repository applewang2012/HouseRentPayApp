package tenant.guardts.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */
public class LocationDemo extends BaseActivity {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    MapView mMapView;
    BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;
    boolean isFirstLoc = true; // 是否首次定位
	private HoursePresenter mPresenter;
	private ArrayList<Map<String, String>> mHouserList;
	// 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor icon_blue = BitmapDescriptorFactory
            .fromResource(R.drawable.blue);
    BitmapDescriptor icon_red = BitmapDescriptorFactory
            .fromResource(R.drawable.red);
    BitmapDescriptor icon_yellow = BitmapDescriptorFactory
            .fromResource(R.drawable.yellow);
    

    private double mLati, mLongi;
	//private Marker mMarkerA;
	private List<Marker> mMarkList;
	private String mLocationAction = "http://tempuri.org/GetRentsByCoodinates";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_location);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        TextView titleBar = (TextView)findViewById(R.id.id_titlebar);
        titleBar.setText("位置");
        mPresenter = new HoursePresenter(getApplicationContext(), this);
        mCurrentMode = LocationMode.NORMAL;

        // 地图初始化
        mMapView = (MapView)findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        //option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			public boolean onMarkerClick(final Marker marker) {
            	Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                button.setTextColor(Color.parseColor("#000000"));
                OnInfoWindowClickListener listener = null;
                int index = getCurrentMarkerIndex(marker);
                Log.w("mingguo", "index  "+index+"  owner  "+mHouserList.get(index).get("ROwner"));
                button.setText("房主："+mHouserList.get(index).get("ROwner")+"\n"+
                "电话："+mHouserList.get(index).get("ROwnerTel"));
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(button, ll, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
            	
            }
        });
        
        Button scanButton = (Button)findViewById(R.id.id_scan_rent_house);
		scanButton.setVisibility(View.VISIBLE);
		scanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent openCameraIntent = new Intent(LocationDemo.this,CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
//				Intent openCameraIntent = new Intent(LocationDemo.this, GetRentAttributeActivity.class);
//				startActivityForResult(openCameraIntent, 0);
				
			}
		});
    }
    
    private int getCurrentMarkerIndex(Marker marker){
    	for (int index = 0; index < mMarkList.size(); index++){
    		if (marker == mMarkList.get(index)){
    			return index;
    		}
    	}
    	return -1;
    }
    
    public void initOverlay() {
    	
    	//mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka),mMapView);	
        for (int index = 0; index < mHouserList.size(); index++){
       	 Map<String, String> child = (Map<String, String>) mHouserList.get(index);
        LatLng llA = new LatLng(Double.parseDouble(child.get("Latitude")), Double.parseDouble(child.get("Longitude")));
        //LatLng llA = new LatLng(mLati, mLongi+0.0008);
        MarkerOptions options = null;
        String status = child.get("Status");
        if (status != null && status.equals("0")){
        	options = new MarkerOptions().position(llA).icon(icon_blue)
                    .zIndex(9).draggable(false);
        }else if (status != null && status.equals("1")){
        	options = new MarkerOptions().position(llA).icon(icon_red)
                    .zIndex(9).draggable(false);
        }else if (status != null && status.equals("2")){
        	options = new MarkerOptions().position(llA).icon(icon_yellow)
                    .zIndex(9).draggable(false);
        
        }else{
        	options = new MarkerOptions().position(llA).icon(icon_blue)
                    .zIndex(9).draggable(false);
        }
        mMarkList.add((Marker) (mBaiduMap.addOverlay(options)));
        
        //ooA.animateType(MarkerAnimateType.drop);
        
//        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
//        giflist.add(bdA);
        


        mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(
                        LocationDemo.this,
                        "拖拽结束，新位置：" + marker.getPosition().latitude + ", "
                                + marker.getPosition().longitude,
                        Toast.LENGTH_LONG).show();
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
        }

        
    }
    
    private void getLocationByCoordinates(){
    	Log.w("mingguo", "location by coordates lati  "+mLati+"  longti  "+mLongi);
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentsByCoodinates";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mLocationAction));
		rpc.addProperty("lat", mLati+""); 
		rpc.addProperty("lon", mLongi+""); 
		rpc.addProperty("distance", "15000"); 
		mPresenter.readyPresentServiceParams(this, url, mLocationAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
    
    private void startGetLocationFromHouse(){
    	getLocationByCoordinates();
    }
    
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				parseLocationInfo((String)msg.obj);
				updateLocationFromHouse();
			}
		}
    };
    
    private void updateLocationFromHouse(){
    	initOverlay();
   }
	
    
    private void parseLocationInfo(String value){
    	mHouserList = new ArrayList<>();
    	mMarkList = new ArrayList<>();
		try{
			if (mHouserList != null){
	    		mHouserList.clear();
	    	}
			if (mMarkList != null){
				mMarkList.clear();
			}
			JSONArray array = new JSONArray(value);
			if (array != null){
				Log.w("mingguo", "house  location  num   "+array.length());
				for (int item = 0; item < array.length(); item++){
					Map<String, String> itemHouse = new HashMap<>();
					JSONObject itemJsonObject = array.optJSONObject(item);
					itemHouse.put("ROwnerTel", itemJsonObject.optString("ROwnerTel"));
					itemHouse.put("Latitude",itemJsonObject.optString("Latitude"));
					itemHouse.put("Longitude",itemJsonObject.optString("Longitude"));
					itemHouse.put("rid",itemJsonObject.optString("rid"));
					itemHouse.put("ROwner",itemJsonObject.optString("ROwner"));
					itemHouse.put("RIDCard",itemJsonObject.optString("RIDCard"));
					itemHouse.put("RAddress",itemJsonObject.optString("RAddress"));
					itemHouse.put("rentno",itemJsonObject.optString("rentno"));
					itemHouse.put("Status",itemJsonObject.optString("Status"));
					mHouserList.add(itemHouse);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        

		@Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                mLati = location.getLatitude();
                mLongi = location.getLongitude();
                LatLng ll = new LatLng(mLati,
                        mLongi);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
               
            }
            startGetLocationFromHouse();
        }

        public void onReceivePoi(BDLocation poiLocation) {
        	
        }

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
    }
    
    

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//处理扫描结果（在界面上显示）
				if (resultCode == RESULT_OK) {
					Bundle bundle = data.getExtras();
					String scanResult = bundle.getString("result");
					Log.e("mingguo", "scan  result  "+scanResult);
					if (!TextUtils.isEmpty(scanResult)){
						Intent attributeIntent = new Intent(LocationDemo.this, GetRentAttributeActivity.class);
						attributeIntent.putExtra("order_id", scanResult);
						startActivity(attributeIntent);
					}
				}
	}

	@Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
     // 回收 bitmap 资源
        icon_blue.recycle();
        icon_red.recycle();
        icon_yellow.recycle();
        
    }

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.w("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		super.onStatusSuccess(action, templateInfo);
		
		if (action != null){
			if (action.equalsIgnoreCase(mLocationAction)){
				Message message = mHandler.obtainMessage();
				message.what = 100;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}


	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
	}
    
    

}
