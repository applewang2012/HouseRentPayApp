package tenant.guardts.house.model;

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

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import tenant.guardts.house.HouseDetailInfoActivity;
import tenant.guardts.house.LoadUrlTestActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.Constants;

//��Ӱ�ʱ���import android.support.v4.app.Fragment; 
@SuppressLint("NewApi")
public class HouseFragment extends Fragment implements DataStatusInterface{
	

	
	private Context mContext;
	private View mRootView;
	// 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
	MapView mMapView; //模拟器调试用TextureMapView，真机调试MapView
    BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;
    // UI相关
    OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
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
	private String mUserName;
	
	public HouseFragment(String name){
		mUserName = name;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mPresenter = new HoursePresenter(mContext, HouseFragment.this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mRootView = inflater.inflate(R.layout.activity_location, container, false);
		initView();
		//initData();
		return mRootView;
	}
	
	
	
	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
//		showLoadingView();
//		mContentLayout.setVisibility(View.INVISIBLE);
//		initData();
//		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onPause() {
        mMapView.onPause();
        super.onPause();
    }
	
	@Override
	public void onDestroy() {
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

	private void initView(){

		 // 地图初始化
        mMapView = (MapView)mRootView.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        //option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			public boolean onMarkerClick(final Marker marker) {
				View detailView = LayoutInflater.from(mContext).inflate(R.layout.map_marker_layout, null);
				TextView contact = (TextView) detailView.findViewById(R.id.id_house_contacts);
				TextView location = (TextView) detailView.findViewById(R.id.id_house_location);
				TextView desp = (TextView)detailView.findViewById(R.id.id_house_desp);
            	Button button = new Button(mContext);
                button.setBackgroundResource(R.drawable.popup);
                button.setTextColor(Color.parseColor("#000000"));
                OnInfoWindowClickListener listener = null;
                final int index = getCurrentMarkerIndex(marker);
                desp.setText(mHouserList.get(index).get("rroomtypedesc")+"  |  "+mHouserList.get(index).get("rrentarea")+"平米  |  "+
                mHouserList.get(index).get("RPropertyDesc")	+"  |  "+mHouserList.get(index).get("rdirectiondesc"));
                location.setText("地址:"+mHouserList.get(index).get("RAddress"));
                Log.i("mingguo", "index  "+index+"  owner  "+mHouserList.get(index).get("ROwner"));
                contact.setText("房主："+mHouserList.get(index).get("ROwner")+"\n"+
                "电话："+mHouserList.get(index).get("ROwnerTel"));
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(detailView, ll, -47);
                
                mBaiduMap.showInfoWindow(mInfoWindow);
                Button showView = (Button)detailView.findViewById(R.id.id_house_detail_show);
                showView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						startActivity(new Intent(mContext, LoadUrlTestActivity.class));
					}
				});
                Button houseSearch = (Button)detailView.findViewById(R.id.id_house_detail_search);
                houseSearch.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
						detailIntent.putExtra("rentNo", mHouserList.get(index).get("rentno"));
						startActivity(detailIntent);
					}
				});
				return true;
            	
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
        
        }

    }
	
	private void getLocationByCoordinates(){
    	Log.w("mingguo", "location by coordates lati  "+mLati+"  longti  "+mLongi);
		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=GetRentsByCoodinates";
		SoapObject rpc = new SoapObject(Constants.NAMESPACE, Constants.getSoapName(mLocationAction));
		rpc.addProperty("lat", mLati+""); 
		rpc.addProperty("lon", mLongi+""); 
		rpc.addProperty("distance", "15000"); 
		mPresenter.readyPresentServiceParams(mContext, url, mLocationAction, rpc);
		mPresenter.startPresentServiceTask();
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
				Log.i("mingguo", "house  location  num   "+array.length());
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
					itemHouse.put("rroomtypedesc",itemJsonObject.optString("rroomtypedesc"));
					itemHouse.put("rdirectiondesc",itemJsonObject.optString("rdirectiondesc"));
					itemHouse.put("rrentarea",itemJsonObject.optString("rrentarea"));
					itemHouse.put("RPropertyDesc",itemJsonObject.optString("RPropertyDesc"));
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
    }

	
	
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on map view status success  action  "+action+"  info  "+templateInfo);
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
	public void onStatusStart() {
		// TODO Auto-generated method stub
		Log.e("housefragment", "on start  ");
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		Log.e("housefragment", "error   "+error);
	}
	
}
