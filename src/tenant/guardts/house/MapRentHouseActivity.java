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
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.map.PoiOverlay;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;

public class MapRentHouseActivity extends BaseActivity implements DataStatusInterface, OnGetPoiSearchResultListener, OnGetSuggestionResultListener{


	private Context mContext;
	//private View mRootView;
	// 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
	MapView mMapView; //模拟器调试用TextureMapView，真机调试MapView
    BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;
    private boolean mShowInfoWindow;
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
    

    //private double mLati, mLongi;
	private LatLng mCurrentLatLng;
	private List<Marker> mMarkList;
	private String mLocationAction = "http://tempuri.org/GetRentsByCoodinates";
	private String mCurrentLocationCity;
	
//	private PoiSearch mPoiSearch = null;
	//private EditText mSearchListener;
//	private AutoCompleteTextView mSearchListener = null;
//    private ArrayAdapter<String> sugAdapter = null;
//	private SuggestionSearch mSuggestionSearch;
//	private TextView mSelectCityText;
	private TextView mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.activity_map_rent_house_layout); 
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("地图租房");
		
		mContext = getApplicationContext();
		mPresenter = new HoursePresenter(mContext, this);
		
		initView();
		
	}
	
	
	
	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		
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
//        mPoiSearch.destroy();
//        mSuggestionSearch.destroy();
        super.onDestroy();
     // 回收 bitmap 资源
        icon_blue.recycle();
        icon_red.recycle();
        icon_yellow.recycle();
        
    }

	private void initView(){

//		LinearLayout button = (LinearLayout)findViewById(R.id.id_home_select_city_content);
//		button.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(mContext, SelectShowCityActivity.class);
//				intent.putExtra("current_city", mCurrentLocationCity);
//				startActivityForResult(intent, CommonUtil.mSelectCityRequestCode);
//				
//			}
//		});
//		mSelectCityText = (TextView)findViewById(R.id.id_home_show_city_view);
//		 mPoiSearch = PoiSearch.newInstance();
//		 mPoiSearch.setOnGetPoiSearchResultListener(this);
//		 mSearchListener = (AutoCompleteTextView)findViewById(R.id.button_search);  
//		 sugAdapter = new ArrayAdapter<String>(MapRentHouseActivity.this,
//	                android.R.layout.simple_dropdown_item_1line);
//	     mSearchListener.setAdapter(sugAdapter);
//	     mSearchListener.setThreshold(1);
	        
	  // 初始化建议搜索模块，注册建议搜索事件监听
//	     mSuggestionSearch = SuggestionSearch.newInstance();
//	     mSuggestionSearch.setOnGetSuggestionResultListener(this);   
		 // 地图初始化
        mMapView = (MapView)findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        //option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
//        mSearchListener.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1,
//                                          int arg2, int arg3) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence cs, int arg1, int arg2,
//                                      int arg3) {
//                if (cs.length() <= 0) {
//                    return;
//                }
//
//                /**
//                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
//                 */
//                Log.i("mingguo", "house fragment  onTextChanged   "+mCurrentLocationCity);
//                mSuggestionSearch
//                        .requestSuggestion((new SuggestionSearchOption())
//                                .keyword(cs.toString()).city(mCurrentLocationCity));
//            }
//        });
//        
//        mSearchListener.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				
//				searchNearbyProcess(mSearchListener.getText().toString());
//			}
//        	
//		});
        
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			public boolean onMarkerClick(final Marker marker) {
				
                final int index = getCurrentMarkerIndex(marker);
                if (index == -1){
                	return true;
                }
                View detailView = LayoutInflater.from(mContext).inflate(R.layout.map_marker_layout, null);
				TextView contact = (TextView) detailView.findViewById(R.id.id_house_contacts);
				TextView location = (TextView) detailView.findViewById(R.id.id_house_location);
				TextView status = (TextView)detailView.findViewById(R.id.id_house_status);
				TextView direction = (TextView)detailView.findViewById(R.id.id_house_direction);
				TextView floor = (TextView)detailView.findViewById(R.id.id_house_floor);
				//TextView checkoutTime = (TextView)detailView.findViewById(R.id.id_house_check_out_time);
				TextView area = (TextView)detailView.findViewById(R.id.id_house_area);
				TextView owner = (TextView)detailView.findViewById(R.id.id_house_owner);
				
//            	Button button = new Button(mContext);
//                button.setBackgroundResource(R.drawable.popup);
//                button.setTextColor(Color.parseColor("#000000"));
//                OnInfoWindowClickListener listener = null;
//                desp.setText(mHouserList.get(index).get("rroomtypedesc")+"  |  "+mHouserList.get(index).get("rrentarea")+"平米  |  "+
//                mHouserList.get(index).get("RPropertyDesc")	+"  |  "+mHouserList.get(index).get("rdirectiondesc"));
//                location.setText("地址:"+mHouserList.get(index).get("RAddress"));
//                Log.i("mingguo", "index  "+index+"  owner  "+mHouserList.get(index).get("ROwner"));
//                contact.setText("房主："+mHouserList.get(index).get("ROwner")+"\n"+
//                "电话："+mHouserList.get(index).get("ROwnerTel"));
                LatLng ll = marker.getPosition();
                contact.setText("电话："+mHouserList.get(index).get("ROwnerTel"));
                location.setText(mHouserList.get(index).get("RAddress"));
                //status.setText(mHouserList.get(index).get("Status"));
                direction.setText(mHouserList.get(index).get("rroomtypedesc"));
                floor.setText(mHouserList.get(index).get("rFloor")+"/"+mHouserList.get(index).get("rtotalfloor")+"层");
                //checkoutTime.setText(mHouserList.get(index).get("rroomtypedesc"));
                area.setText(mHouserList.get(index).get("rrentarea")+"平米");
                owner.setText("房主："+mHouserList.get(index).get("ROwner"));
                mInfoWindow = new InfoWindow(detailView, ll, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                
                Button showView = (Button)detailView.findViewById(R.id.id_house_detail_show);
                showView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
						loadIntent.putExtra("url", "http://www.guardts.com/output/html5.html");
						loadIntent.putExtra("tab_name", "全景图");
						startActivity(loadIntent);
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
        
        mBaiduMap.setOnMapClickListener(new OnMapClickListener()  {  
  
            @Override  
            public boolean onMapPoiClick(MapPoi arg0)  {  
                return false;  
            }  
  
            @Override  
            public void onMapClick(LatLng arg0)  {  
                mBaiduMap.hideInfoWindow();  
            }
        });  
        
        Button rentToButton = (Button)findViewById(R.id.id_home_button_chuzu_house);
        rentToButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MapRentHouseActivity.this, RentToHouseActivity.class));
				
			}
		});
        Button rentButton = (Button)findViewById(R.id.id_home_button_rent_house);
        rentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, HouseSearchActivity.class);
				startActivity(intent);
			}
		});
        
	
	}
	
	// 查询周围2000米的某类建筑
		public void  searchNearbyProcess(String searchText) {
	        //searchType = 2;
	        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(searchText
	                .toString()).sortType(PoiSortType.distance_from_near_to_far).location(mCurrentLatLng)
	                .radius(2000);
//	        mPoiSearch.searchNearby(nearbySearchOption);
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
	    	if (mHouserList.size() == 0){
	    		GlobalUtil.shortToast(mContext, "抱歉，该位置周边未搜索到任何房源！", getResources().getDrawable(R.drawable.ic_dialog_no));
	    		return;
	    	}
	    	GlobalUtil.shortToast(mContext, "共搜索到 "+mHouserList.size()+" 套房源！", getResources().getDrawable(R.drawable.ic_dialog_no));
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
	        }

	    }
		
		private void getLocationByCoordinates(){
	    	Log.w("mingguo", "house  fragment  location by coordates lati  "+ mCurrentLatLng.latitude+"  longti  "+mCurrentLatLng.longitude);
			String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentsByCoodinates";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mLocationAction));
			rpc.addProperty("lat", mCurrentLatLng.latitude+""); 
			rpc.addProperty("lon", mCurrentLatLng.longitude+""); 
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
				}else if (msg.what == 300){
					showSelectLocationMap();
				}else if (msg.what == 500){
					//updateLocationCity();
				}
			}
	    };
		private ArrayList<String> suggest;
	    
	    private void updateLocationFromHouse(){
	    	initOverlay();
	   }
	    
	    private class MyPoiOverlay extends PoiOverlay {
			PoiInfo poi;

			public MyPoiOverlay(BaiduMap baiduMap) {
				super(baiduMap);
			}

			@Override
			public boolean onPoiClick(int index) {
				poi = getPoiResult().getAllPoi().get(index);
				AlertDialog.Builder alertDialog=new AlertDialog.Builder(MapRentHouseActivity.this, AlertDialog.THEME_HOLO_LIGHT);
	            // 设置Title的内�?
				alertDialog.setTitle("位置信息");
	            // 设置Content来显示一个信�?
				alertDialog.setMessage(poi.name + "\n" + poi.address);
	            // 设置�?��PositiveButton
				alertDialog.setNegativeButton("确定", new DialogInterface.OnClickListener()
	             {
	                 @Override
	                 public void onClick(DialogInterface dialog, int which)
	                 {
	                 }
	             });
				alertDialog.show();
				return true;
			}
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
						itemHouse.put("rFloor",itemJsonObject.optString("rFloor"));
						itemHouse.put("rtotalfloor",itemJsonObject.optString("rtotalfloor"));
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
	            mCurrentLocationCity = location.getCity();
	            MyLocationData locData = new MyLocationData.Builder()
	                    .accuracy(location.getRadius())
	                            // 此处设置开发者获取到的方向信息，顺时针0-360
	                    .direction(100).latitude(location.getLatitude())
	                    .longitude(location.getLongitude()).build();
	            mBaiduMap.setMyLocationData(locData);
	            if (isFirstLoc) {
	                isFirstLoc = false;
	                CommonUtil.mCurrentLati = location.getLatitude();
	                CommonUtil.mCurrentLongi = location.getLongitude();
	                mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
	                MapStatus.Builder builder = new MapStatus.Builder();
	                builder.target(mCurrentLatLng).zoom(18.0f);
	                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
	               
	            }
	            startGetLocationFromHouse();
	            mHandler.sendEmptyMessageDelayed(500, 200);
	        }

	        public void onReceivePoi(BDLocation poiLocation) {
	        	
	        }

			@Override
			public void onConnectHotSpotMessage(String arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
	    }
	    
	    private void showSelectLocationMap(){
	    	Log.i("mingguo", "select location site  lati  "+mCurrentLatLng.latitude+"  longi  "+mCurrentLatLng.longitude);
	    	MyLocationData locData = new MyLocationData.Builder()
	                .accuracy(0)
	                        // 此处设置开发者获取到的方向信息，顺时针0-360
	                .direction(100).latitude(mCurrentLatLng.latitude)
	                .longitude(mCurrentLatLng.longitude).build();
	        mBaiduMap.setMyLocationData(locData);
	    	MapStatus.Builder builder = new MapStatus.Builder();
	        builder.target(mCurrentLatLng).zoom(18.0f);
	        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
	        
	    }
	    
//	    private void updateLocationCity(){
//	    	mSelectCityText.setText(mCurrentLocationCity);
//	    }

		
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

		@Override
		public void onGetPoiDetailResult(PoiDetailResult arg0) {
			// TODO Auto-generated method stub
			if (arg0.error != SearchResult.ERRORNO.NO_ERROR) {
	            Toast.makeText(mContext, "抱歉，未找到结果", Toast.LENGTH_SHORT)
	                    .show();
	        } else {
	            Toast.makeText(mContext, arg0.getName() + ": " + arg0.getAddress(), Toast.LENGTH_SHORT)
	                    .show();
	        }
		}

		@Override
		public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			
			if (result == null
					|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND || result.getAllPoi() == null) {
				return;
			}
			Log.e("mingguo", "House fragment  onGetPoiResult  "+result.error);
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				if (result.getAllPoi().size() > 0){
					mCurrentLatLng = result.getAllPoi().get(0).location;
				}
				mBaiduMap.clear();
				startGetLocationFromHouse();
				PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
				// 设置overlay可以处理标注点击事件
				//mBaiduMap.setOnMarkerClickListener(overlay);
				// 设置PoiOverlay数据
				overlay.setData(result);
				overlay.addToMap();
				overlay.zoomToSpan();
				return;
			}else{
				GlobalUtil.shortToast(mContext, "抱歉，定位城市失败！", getResources().getDrawable(R.drawable.ic_dialog_no));
			}
			
		}

//		@Override
//		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//			searchNearbyProcess(v.getText().toString());
//			return true;
//		}

		@Override
		public void onGetSuggestionResult(SuggestionResult res) {
			// TODO Auto-generated method stub
			if (res == null || res.getAllSuggestions() == null) {
	            return;
	        }    
	        suggest = new ArrayList<String>();
	        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
	            if (info.key != null) {
	                suggest.add(info.key);
	            }
	        }
//	        sugAdapter = new ArrayAdapter<String>(MapRentHouseActivity.this, android.R.layout.simple_dropdown_item_1line, suggest);
//	        mSearchListener.setAdapter(sugAdapter);
//	        sugAdapter.notifyDataSetChanged();
		}

}