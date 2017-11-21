package tenant.guardts.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.map.PoiOverlay;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.LogUtil;


public class MapRentHouseActivity extends BaseActivity
		implements DataStatusInterface, OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

	private Context mContext;
	// private View mRootView;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	MapView mMapView; // 模拟器调试用TextureMapView，真机调试MapView
	BaiduMap mBaiduMap;
	private InfoWindow mInfoWindow;
	private boolean mShowInfoWindow;
	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	boolean isFirstLoc = true; // 是否首次定位
	private HoursePresenter mPresenter;
	private ArrayList<Map<String, String>> mHouserList;
	private HashMap <String,ArrayList<Map<String, String>>> mLocationMap;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor icon_red = BitmapDescriptorFactory.fromResource(R.drawable.blue);
	BitmapDescriptor icon_yellow = null;

	// private double mLati, mLongi;
	private LatLng mCurrentLatLng;
	private List<Marker> mMarkList;
	private String mLocationAction = "http://tempuri.org/GetRentsByCoodinates";
	private String mCurrentLocationCity;

	// private PoiSearch mPoiSearch = null;
	// private EditText mSearchListener;
	// private AutoCompleteTextView mSearchListener = null;
	// private ArrayAdapter<String> sugAdapter = null;
	// private SuggestionSearch mSuggestionSearch;
	// private TextView mSelectCityText;
	private TextView mTitleBar;
	private Button mSelectCityText;
	private PoiSearch mPoiSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_map_rent_house_layout);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.titlebar);
		// mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		// mTitleBar.setText("地图租房");

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
		mPoiSearch.destroy();
		if (icon_red != null){
			icon_red.recycle();
		}
		if (icon_yellow != null){
			icon_yellow.recycle();
		}
		// mSuggestionSearch.destroy();
		super.onDestroy();
		// 回收 bitmap 资源
		

	}

	private void initView() {

		mSelectCityText = (Button) findViewById(R.id.id_home_show_city_view);
		mSelectCityText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SelectShowCityActivity.class);
				intent.putExtra("current_city", mCurrentLocationCity);
				startActivityForResult(intent, CommonUtil.SELECT_CITY_REQEUST_CODE);

			}
		});
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		// mSearchListener =
		// (AutoCompleteTextView)findViewById(R.id.button_search);
		// sugAdapter = new ArrayAdapter<String>(MapRentHouseActivity.this,
		// android.R.layout.simple_dropdown_item_1line);
		// mSearchListener.setAdapter(sugAdapter);
		// mSearchListener.setThreshold(1);

		// 初始化建议搜索模块，注册建议搜索事件监听
		// mSuggestionSearch = SuggestionSearch.newInstance();
		// mSuggestionSearch.setOnGetSuggestionResultListener(this);
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
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
		// option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		// mSearchListener.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1,
		// int arg2, int arg3) {
		//
		// }
		//
		// @Override
		// public void onTextChanged(CharSequence cs, int arg1, int arg2,
		// int arg3) {
		// if (cs.length() <= 0) {
		// return;
		// }
		//
		// /**
		// * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
		// */
		// LogUtil.w("mingguo", "house fragment onTextChanged
		// "+mCurrentLocationCity);
		// mSuggestionSearch
		// .requestSuggestion((new SuggestionSearchOption())
		// .keyword(cs.toString()).city(mCurrentLocationCity));
		// }
		// });
		//
		// mSearchListener.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		//
		// searchNearbyProcess(mSearchListener.getText().toString());
		// }
		//
		// });

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			public boolean onMarkerClick(final Marker marker) {

//				final int index = getCurrentMarkerIndex(marker);
//				if (index == -1) {
//					return true;
//				}
				showClickMarkerView(marker);
//				int num = getSameLocationHouseSize(marker);
//				if (num <= 0){
//					return true;
//				}
//				if (num )
//				View detailView = LayoutInflater.from(mContext).inflate(R.layout.map_marker_layout, null);
//				TextView contact = (TextView) detailView.findViewById(R.id.id_house_contacts);
//				TextView location = (TextView) detailView.findViewById(R.id.id_house_location);
//				TextView status = (TextView) detailView.findViewById(R.id.id_house_status);
//				TextView direction = (TextView) detailView.findViewById(R.id.id_house_direction);
//				TextView floor = (TextView) detailView.findViewById(R.id.id_house_floor);
//				// TextView checkoutTime =
//				// (TextView)detailView.findViewById(R.id.id_house_check_out_time);
//				TextView area = (TextView) detailView.findViewById(R.id.id_house_area);
//				TextView owner = (TextView) detailView.findViewById(R.id.id_house_owner);
//
//				// Button button = new Button(mContext);
//				// button.setBackgroundResource(R.drawable.popup);
//				// button.setTextColor(Color.parseColor("#000000"));
//				// OnInfoWindowClickListener listener = null;
//				// desp.setText(mHouserList.get(index).get("rroomtypedesc")+" |
//				// "+mHouserList.get(index).get("rrentarea")+"平米 | "+
//				// mHouserList.get(index).get("RPropertyDesc") +" |
//				// "+mHouserList.get(index).get("rdirectiondesc"));
//				// location.setText("地址:"+mHouserList.get(index).get("RAddress"));
//				// LogUtil.w("mingguo", "index "+index+" owner
//				// "+mHouserList.get(index).get("ROwner"));
//				// contact.setText("房主："+mHouserList.get(index).get("ROwner")+"\n"+
//				// "电话："+mHouserList.get(index).get("ROwnerTel"));
//				LatLng ll = marker.getPosition();
//				String phone = mHouserList.get(index).get("ROwnerTel");
//				String ownerName = mHouserList.get(index).get("ROwner");
//				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
//					if (phone.length() > 5) {
//						contact.setText("电话：" + phone.substring(0, 3) + "********");
//					}
//					if (ownerName.length() > 1) {
//						owner.setText("房主：" + ownerName.substring(0, 1) + "**");
//					}
//				} else {
//					contact.setText("电话：" + phone);
//				}
//				location.setText(mHouserList.get(index).get("RAddress"));
//				// status.setText(mHouserList.get(index).get("Status"));
//				direction.setText(mHouserList.get(index).get("rroomtypedesc"));
//				floor.setText(
//						mHouserList.get(index).get("rFloor") + "/" + mHouserList.get(index).get("rtotalfloor") + "层");
//				// checkoutTime.setText(mHouserList.get(index).get("rroomtypedesc"));
//				area.setText(mHouserList.get(index).get("rrentarea") + "平米");
//
//				mInfoWindow = new InfoWindow(detailView, ll, -47);
//				mBaiduMap.showInfoWindow(mInfoWindow);
//
//				Button showView = (Button) detailView.findViewById(R.id.id_house_detail_show);
//				showView.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//						loadIntent.putExtra("url", "http://www.guardts.com/output/html5.html");
//						loadIntent.putExtra("tab_name", "全景图");
//						startActivity(loadIntent);
//					}
//				});
//				Button houseSearch = (Button) detailView.findViewById(R.id.id_house_detail_search);
//				houseSearch.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
//							Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
//							Intent loginIntent = new Intent(mContext, LoginUserActivity.class);
//							loginIntent.putExtra("intent_status", true);
//							startActivity(loginIntent);
//						} else {
//							if (mHouserList.get(index).get("rentno") != null
//									&& !mHouserList.get(index).get("rentno").equals("")) {
//								Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
//								detailIntent.putExtra("rentNo", mHouserList.get(index).get("rentno"));
//								startActivity(detailIntent);
//							}
//						}
//					}
//				});
				return true;

			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mBaiduMap.hideInfoWindow();
			}
		});

		FrameLayout suggestButton = (FrameLayout) findViewById(R.id.map_suggest_search);
		suggestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent searchIntent = new Intent(MapRentHouseActivity.this, MapSuggestSearchActivity.class);
				searchIntent.putExtra("select_city", mCurrentLocationCity);
				startActivityForResult(searchIntent, CommonUtil.MAP_SUGGEST_REQEUST_CODE);
			}
		});

	}
	
	private void showMarkerDetail(LatLng ll, final Map<String, String> itemMap){
		View detailView = LayoutInflater.from(mContext).inflate(R.layout.map_marker_layout, null);
		TextView contact = (TextView) detailView.findViewById(R.id.id_house_contacts);
		TextView location = (TextView) detailView.findViewById(R.id.id_house_location);
		TextView status = (TextView) detailView.findViewById(R.id.id_house_status);
		TextView direction = (TextView) detailView.findViewById(R.id.id_house_direction);
		TextView floor = (TextView) detailView.findViewById(R.id.id_house_floor);
		TextView area = (TextView) detailView.findViewById(R.id.id_house_area);
		TextView owner = (TextView) detailView.findViewById(R.id.id_house_owner);
		String phone = itemMap.get("ROwnerTel");
		String ownerName = itemMap.get("ROwner");
		if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
			if (phone.length() > 5) {
				contact.setText("电话：" + phone.substring(0, 3) + "********");
			}
			if (ownerName.length() > 1) {
				owner.setText("房主：" + ownerName.substring(0, 1) + "**");
			}
		} else {
			contact.setText("电话：" + phone);
			if (ownerName.length() > 1) {
				owner.setText("房主：" + ownerName);
			}
		}
		location.setText(itemMap.get("RAddress"));
		// status.setText(itemMap.get("Status"));
		direction.setText(itemMap.get("rroomtypedesc"));
		floor.setText(
				itemMap.get("rFloor") + "/" + itemMap.get("rtotalfloor") + "层");
		area.setText(itemMap.get("rrentarea") + "平米");

		mInfoWindow = new InfoWindow(detailView, ll, -47);
		mBaiduMap.showInfoWindow(mInfoWindow);

		Button showView = (Button) detailView.findViewById(R.id.id_house_detail_show);
		showView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", "http://www.guardts.com/output/html5.html");
//				loadIntent.putExtra("tab_name", "全景图");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse("http://www.guardts.com/output/html5.html");   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		Button houseSearch = (Button) detailView.findViewById(R.id.id_house_detail_search);
		houseSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					Intent loginIntent = new Intent(mContext, LoginUserActivity.class);
					loginIntent.putExtra("intent_status", true);
					startActivity(loginIntent);
				} else {
					if (itemMap.get("rentno") != null
							&& !itemMap.get("rentno").equals("")) {
						Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
						detailIntent.putExtra("rentNo", itemMap.get("rentno"));
						startActivity(detailIntent);
					}
				}
			}
		});
	}
	
	private void showClickMarkerView(Marker marker){
		
		LatLng ll = marker.getPosition();
		
		String key = ll.latitude + "-" + ll.longitude;
		LogUtil.w("mingguo", "show click marker view  "+key);
		ArrayList<Map<String, String>> child = mLocationMap.get(key);
		
		if (child != null){
			LogUtil.w("mingguo", "show click marker view  "+child.size());
			if (child.size() == 1){
				showMarkerDetail(ll, child.get(0));
			}else if (child.size() > 1){
				String[] listItem = new String[child.size()];
				for (int i = 0; i < child.size(); i++){
					String showInfo = null;
					String phone = child.get(i).get("ROwnerTel");
					String ownerName = child.get(i).get("ROwner");
					if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
						if (phone.length() > 5) {
							showInfo = "楼层：" +  child.get(i).get("rFloor") + "/" + child.get(i).get("rtotalfloor") + "层"+ " 房主：" + ownerName.substring(0, 1) + "**";
						}
						
					}else{
						showInfo = "楼层：" +  child.get(i).get("rFloor") + "/" + child.get(i).get("rtotalfloor") + "层" +  " 房主：" + ownerName;
//						showInfo = "房主：" + ownerName  + " 电话：" + phone;////////////////////
					}
					listItem[i] = showInfo; 
				}
				showAlertDialog(listItem, ll, child);
			}
			
		}
		
		
		
		
	}
	
	
	private void showAlertDialog(String[] items, final LatLng ll, final ArrayList<Map<String, String>> child){
		AlertDialog.Builder builder =new AlertDialog.Builder(MapRentHouseActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//showMarkerDetail(ll, child.get(which));
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					Intent loginIntent = new Intent(mContext, LoginUserActivity.class);
					loginIntent.putExtra("intent_status", true);
					startActivity(loginIntent);
				} else {
					if (child.get(which).get("rentno") != null
							&& !child.get(which).get("rentno").equals("")) {
						Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
						detailIntent.putExtra("rentNo", child.get(which).get("rentno"));
						startActivity(detailIntent);
					}
				}
			}
		});
		builder.show();
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.w("mingguo", "Map rent activity  onActivityResult   " + resultCode + "  resquestCode   " + requestCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == CommonUtil.SELECT_CITY_REQEUST_CODE) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					String selectedCity = bundle.getString("city");
					LogUtil.e("mingguo", "homeActivity  onActivity  selected city  " + selectedCity);
					if (!TextUtils.isEmpty(selectedCity)) {
						if (selectedCity != null && selectedCity.equalsIgnoreCase(mCurrentLocationCity)) {
							return;
						}
						mCurrentLocationCity = selectedCity;
						LogUtil.w("mingguo", "house fragment on resume change currentCity " + mCurrentLocationCity);
						mSelectCityText.setText(mCurrentLocationCity);
						searchButtonProcess();
					}
				}
			} else if (requestCode == CommonUtil.MAP_SUGGEST_REQEUST_CODE) {
				Bundle searchBundle = data.getExtras();
				LogUtil.w("mingguo", "Map rent onActivity Result  " + searchBundle.getString("search_tag"));
				searchNearbyProcess(searchBundle.getString("search_tag"));
			}

		}
	}

	private void searchButtonProcess() {
		String citystr = mCurrentLocationCity;
		String keystr = "市政府";
		mPoiSearch.searchInCity((new PoiCitySearchOption()).city(citystr).keyword(keystr).pageCapacity(1));
	}

	// 查询周围2000米的某类建筑
	private void searchNearbyProcess(String searchText) {
		// searchType = 2;
		PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(searchText.toString())
				.sortType(PoiSortType.distance_from_near_to_far).location(mCurrentLatLng).radius(2000);
		mPoiSearch.searchNearby(nearbySearchOption);
	}

	private int getCurrentMarkerIndex(Marker marker) {
		for (int index = 0; index < mMarkList.size(); index++) {
			if (marker == mMarkList.get(index)) {
				return index;
			}
		}
		return -1;
	}
	
	private int getSameLocationHouseSize(Marker marker){
		for (int i = 0; i < mMarkList.size(); i++){
			if (marker == mMarkList.get(i)) {
				String key = marker.getPosition().latitude + "-" + marker.getPosition().longitude;
				ArrayList<Map<String, String>> child = mLocationMap.get(key);
				if (child != null){
					return child.size();
				}
			}
		}
		return 0;
	}

	/**
	 * 获取相同经纬度的房屋集合并设置overlay
	 */
	public void initOverlay() {
		//key：经纬度   value:相对应的集合
		//mLocationMap =new HashMap<String,ArrayList<Map<String, String>>>();
		for(Map<String, String> child : mHouserList){
			ArrayList<Map<String, String>> list=new ArrayList<>();
			mLocationMap.put(child.get("Latitude")+"-"+child.get("Longitude"), list);
		}
		
		for(Map<String, String> child : mHouserList){
			for(String keyname:mLocationMap.keySet()){
				if((child.get("Latitude")+"-"+child.get("Longitude")).equals(keyname)){
					mLocationMap.get(keyname).add(child);
				}
			}
		}
		
		Iterator<Entry<String, ArrayList<Map<String, String>>>> iterator = mLocationMap.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry entry = (Map.Entry) iterator.next();
			//获得key
			String key=(String) entry.getKey();
			LogUtil.e("mingguo", "key  "+key);
			String[] split = key.split("-");
			LatLng llA = new LatLng(Double.parseDouble(split[0]),
					Double.parseDouble(split[1]));
			ArrayList<Map<String, String>> itemLocationList = (ArrayList<Map<String, String>>)entry.getValue();
			MarkerOptions options = null;
			if (itemLocationList != null){
				if (itemLocationList.size() == 1){
					options = new MarkerOptions().position(llA).icon(icon_red).zIndex(9).draggable(false);
				}else{
					View mHouseMore = getLayoutInflater().inflate(R.layout.map_more_house_icon, null);
					TextView houseNum = (TextView)mHouseMore.findViewById(R.id.textView1);
					houseNum.setText(itemLocationList.size()+"");
					icon_yellow = BitmapDescriptorFactory.fromView(mHouseMore);
					options = new MarkerOptions().position(llA).icon(icon_yellow).zIndex(9).draggable(false);
					
				}
			}
			
			mMarkList.add((Marker) (mBaiduMap.addOverlay(options)));
		}
		LogUtil.e("mingguo", "list size  "+mHouserList.size()+"  market size  "+mMarkList.size()+"  locationmap size   "+mLocationMap.size());

		if (mHouserList.size() == 0) {
			GlobalUtil.shortToast(mContext, "抱歉，该位置周边未搜索到任何房源！", getResources().getDrawable(R.drawable.ic_dialog_no));
			return;
		}
		GlobalUtil.shortToast(mContext, "共搜索到 " + mHouserList.size() + " 套房源！",
				getResources().getDrawable(R.drawable.ic_dialog_no));
		

	}

	private void getLocationByCoordinates() {
		LogUtil.w("mingguo", "house  fragment  location by coordates lati  " + mCurrentLatLng.latitude + "  longti  "
				+ mCurrentLatLng.longitude);
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetRentsByCoodinates";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mLocationAction));
		rpc.addProperty("lat", mCurrentLatLng.latitude + "");
		rpc.addProperty("lon", mCurrentLatLng.longitude + "");
		rpc.addProperty("distance", "10000");
		mPresenter.readyPresentServiceParams(this, url, mLocationAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void startGetLocationFromHouse() {
		getLocationByCoordinates();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100) {
				parseLocationInfo((String) msg.obj);
				updateLocationFromHouse();
			} else if (msg.what == 300) {
				showSelectLocationMap();
			} else if (msg.what == 500) {
				updateLocationCity();
			}
		}
	};
	private ArrayList<String> suggest;

	private void updateLocationFromHouse() {
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
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapRentHouseActivity.this,
					AlertDialog.THEME_HOLO_LIGHT);
			// 设置Title的内�?
			alertDialog.setTitle("位置信息");
			// 设置Content来显示一个信�?
			alertDialog.setMessage(poi.name + "\n" + poi.address);
			// 设置�?��PositiveButton
			alertDialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.show();
			return true;
		}
	}
	/**
	 * 设置背景透明度
	 * 
	 * @param alpha
	 */
	public void setBackgroundAlpha(float alpha) {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = alpha;
		getWindow().setAttributes(params);
	}
//	private void initPopupWindow(){
//		setBackgroundAlpha(0.2f);
//		view = View.inflate(this, R.layout.popupwindow_contact_owner, null);
//		popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		popupWindow.setFocusable(true);
//		if (title != null && phone != null) {
//			
//		}
//		popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
//		popupWindow.setOnDismissListener(new OnDismissListener() {
//
//			@Override
//			public void onDismiss() {
//				setBackgroundAlpha(1f);
//
//			}
//		});
//	}

	private void parseLocationInfo(String value) {
		mHouserList = new ArrayList<>();
		mMarkList = new ArrayList<>();
		mLocationMap = new HashMap<>();
		try {
			if (mHouserList != null) {
				mHouserList.clear();
			}
			if (mMarkList != null) {
				mMarkList.clear();
			}
			if (mLocationMap != null){
				mLocationMap.clear();
			}
			JSONArray array = new JSONArray(value);
			if (array != null) {
				LogUtil.w("mingguo", "house  location  num   " + array.length());
				for (int item = 0; item < array.length(); item++) {
					JSONObject itemJsonObject = array.optJSONObject(item);
					String lati = itemJsonObject.optString("Latitude");
					String longi = itemJsonObject.optString("Longitude");
					if (lati == null || lati.equals("") || lati.equalsIgnoreCase("null")){
						continue;
					}
					if (longi == null || longi.equals("") || longi.equalsIgnoreCase("null")){
						continue;
					} 
					Map<String, String> itemHouse = new HashMap<>();
					itemHouse.put("ROwnerTel", itemJsonObject.optString("ROwnerTel"));
					itemHouse.put("Latitude", Double.parseDouble(itemJsonObject.optString("Latitude"))+"");
					itemHouse.put("Longitude", Double.parseDouble(itemJsonObject.optString("Longitude"))+"");
					
					itemHouse.put("rid", itemJsonObject.optString("rid"));
					itemHouse.put("ROwner", itemJsonObject.optString("ROwner"));
					itemHouse.put("RIDCard", itemJsonObject.optString("RIDCard"));
					itemHouse.put("RAddress", itemJsonObject.optString("RAddress"));
					itemHouse.put("rentno", itemJsonObject.optString("rentno"));
					itemHouse.put("Status", itemJsonObject.optString("Status"));
					itemHouse.put("rroomtypedesc", itemJsonObject.optString("rroomtypedesc"));
					itemHouse.put("rdirectiondesc", itemJsonObject.optString("rdirectiondesc"));
					itemHouse.put("rrentarea", itemJsonObject.optString("rrentarea"));
					itemHouse.put("RPropertyDesc", itemJsonObject.optString("RPropertyDesc"));
					itemHouse.put("rFloor", itemJsonObject.optString("rFloor"));
					itemHouse.put("rtotalfloor", itemJsonObject.optString("rtotalfloor"));
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
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
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

	private void showSelectLocationMap() {
		LogUtil.w("mingguo",
				"select location site  lati  " + mCurrentLatLng.latitude + "  longi  " + mCurrentLatLng.longitude);
		MyLocationData locData = new MyLocationData.Builder().accuracy(0)
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(mCurrentLatLng.latitude).longitude(mCurrentLatLng.longitude).build();
		mBaiduMap.setMyLocationData(locData);
		MapStatus.Builder builder = new MapStatus.Builder();
		builder.target(mCurrentLatLng).zoom(18.0f);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

	}

	private void updateLocationCity() {
		mSelectCityText.setText(mCurrentLocationCity);
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equalsIgnoreCase(mLocationAction)) {
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
		LogUtil.e("housefragment", "error   " + error);
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub
		if (arg0.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(mContext, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, arg0.getName() + ": " + arg0.getAddress(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {

		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND || result.getAllPoi() == null) {
			return;
		}
		LogUtil.e("mingguo", "House fragment  onGetPoiResult  " + result.error+" poi size  "+result.getAllPoi().size());
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			if (result.getAllPoi().get(0).location == null){
				Toast.makeText(MapRentHouseActivity.this, "抱歉，未搜索到相关信息", Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.getAllPoi().size() > 0) {
				mCurrentLatLng = result.getAllPoi().get(0).location;
			}
			
			mBaiduMap.clear();
			startGetLocationFromHouse();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			// 设置overlay可以处理标注点击事件
			// mBaiduMap.setOnMarkerClickListener(overlay);
			// 设置PoiOverlay数据
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		} else {
			GlobalUtil.shortToast(mContext, "抱歉，定位城市失败！", getResources().getDrawable(R.drawable.ic_dialog_no));
		}

	}

	// @Override
	// public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	// InputMethodManager imm = (InputMethodManager)
	// getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	// imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	// searchNearbyProcess(v.getText().toString());
	// return true;
	// }

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		// TODO Auto-generated method stub
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		// suggest = new ArrayList<String>();
		// for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions())
		// {
		// if (info.key != null) {
		// suggest.add(info.key);
		// }
		// }
		// sugAdapter = new ArrayAdapter<String>(MapRentHouseActivity.this,
		// android.R.layout.simple_dropdown_item_1line, suggest);
		// mSearchListener.setAdapter(sugAdapter);
		// sugAdapter.notifyDataSetChanged();
	}

}
