package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Text;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.a.r;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
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
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.HomeActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.SelectShowCityActivity;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.view.HomeCustomView;
import tenant.guardts.house.view.HomeFragmentListView;

public class HouseFragment extends Fragment
		implements DataStatusInterface, OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

	private Context mContext;
	private View mRootView;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;

	private boolean mShowInfoWindow;
	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	boolean isFirstLoc = true; // 是否首次定位
	private HoursePresenter mPresenter;
	private ArrayList<Map<String, String>> mHouserList;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor icon_blue = BitmapDescriptorFactory.fromResource(R.drawable.blue);
	BitmapDescriptor icon_red = BitmapDescriptorFactory.fromResource(R.drawable.red);
	BitmapDescriptor icon_yellow = BitmapDescriptorFactory.fromResource(R.drawable.yellow);

	// private double mLati, mLongi;
	private LatLng mCurrentLatLng;
	private List<Marker> mMarkList;
	private String mLocationAction = "http://tempuri.org/GetRentsByCoodinates";
	private String mCurrentLocationCity;
	private int mRadioSelectedPostion = 0;
	private PoiSearch mPoiSearch = null;
	// private EditText mSearchListener;
	private AutoCompleteTextView mSearchListener = null;
	private ArrayAdapter<String> sugAdapter = null;
	private SuggestionSearch mSuggestionSearch;
	private TextView mSelectCityText;
	private HomeCustomView mBtnRight;

	public HouseFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mPresenter = new HoursePresenter(mContext, HouseFragment.this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.activity_location, container, false);
		initView();
		initEvent();
		// initData();
		return mRootView;
	}

	private void initEvent() {
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.home_radio_share:
					mBtnLeft.setImageAndContent(R.drawable.clock,"短租共享");
					mBtnRight.setImageAndContent(R.drawable.pin_sharp_circle,"地图租房");
					mRadioSelectedPostion = 0;
					break;
				case R.id.home_radio_owner:
					mBtnLeft.setImageAndContent(R.drawable.edit,"登记房屋");
					mBtnRight.setImageAndContent(R.drawable.tap_click_force_touch,"我要出租");
					mRadioSelectedPostion = 1;
					break;
				}
			}
		});
		
		
		

	}
	


	@Override
	public void onResume() {
		super.onResume();
		HomeActivity activity = (HomeActivity) getActivity();
		if (activity.getSelectedCity() != null && !activity.getSelectedCity().equals("")) {
			if (mCurrentLocationCity != null && mCurrentLocationCity.equalsIgnoreCase(activity.getSelectedCity())) {
				return;
			}
			mCurrentLocationCity = activity.getSelectedCity();
			Log.i("mingguo", "house fragment  on resume  change  currentCity  " + mCurrentLocationCity);
			mSelectCityText.setText(mCurrentLocationCity);
			// mCurrentLatLng = CommonUtil.getLatLngBystr(mContext,
			// mCurrentLocationCity);
			// Message message = mHandler.obtainMessage();
			// message.what = 300;
			// message.obj = mCurrentLatLng;
			// message.sendToTarget();
			// startThreadfindLocation(mCurrentLocationCity);
			searchButtonProcess();
		}
	}

	private void startThreadfindLocation(final String locationName) {

		Thread thrd = new Thread() {
			@Override
			public void run() {
				try {
					mCurrentLatLng = CommonUtil.getLatLngBystr(mContext, mCurrentLocationCity);
					Message message = mHandler.obtainMessage();
					message.what = 300;
					message.obj = mCurrentLatLng;
					message.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thrd.start();
	}

	public void searchButtonProcess() {
		String citystr = mCurrentLocationCity;
		String keystr = "市政府";
		mPoiSearch.searchInCity((new PoiCitySearchOption()).city(citystr).keyword(keystr).pageCapacity(1));
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		super.onDestroy();
		// 回收 bitmap 资源
		icon_blue.recycle();
		icon_red.recycle();
		icon_yellow.recycle();

	}

	private void initView() {
		mRbShare = (RadioButton) mRootView.findViewById(R.id.home_radio_share);
		mRbOwner = (RadioButton) mRootView.findViewById(R.id.home_radio_owner);
		mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.home_radiogrouop);
		mBtnLeft = (HomeCustomView) mRootView.findViewById(R.id.id_home_button_left);
		mBtnRight = (HomeCustomView) mRootView.findViewById(R.id.id_home_button_right);
		mBtnLeft.setImageAndContent(R.drawable.clock,"短租共享");
		mBtnRight.setImageAndContent(R.drawable.pin_sharp_circle,"地图租房");
		mBtnLeft.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView name = (TextView)v.findViewById(R.id.textView1);
				ImageView image = (ImageView)v.findViewById(R.id.imageView1);
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					name.setTextColor(Color.parseColor("#ffffff"));
					v.setBackgroundColor(Color.parseColor("#337ffd"));
					Log.w("mingguo", "left  selected position  "+mRadioSelectedPostion);
					if (mRadioSelectedPostion == 0){
						image.setBackgroundResource(R.drawable.clock_press);
					}else if (mRadioSelectedPostion == 1){
						image.setBackgroundResource(R.drawable.edit_press);
					}
				}
				if (event.getAction() == MotionEvent.ACTION_UP){
					name.setTextColor(Color.parseColor("#999999"));
					v.setBackgroundColor(Color.parseColor("#ffffff"));
					Log.w("mingguo", "left up  selected position  "+mRadioSelectedPostion);
					if (mRadioSelectedPostion == 0){
						image.setBackgroundResource(R.drawable.clock);
					}else if (mRadioSelectedPostion == 1){
						image.setBackgroundResource(R.drawable.edit);
					}
				}
				
				return false;
			}
		});
		mBtnRight.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView name = (TextView)v.findViewById(R.id.textView1);
				ImageView image = (ImageView)v.findViewById(R.id.imageView1);
				
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					name.setTextColor(Color.parseColor("#ffffff"));
					v.setBackgroundColor(Color.parseColor("#337ffd"));
					Log.w("mingguo", "right   selected position down "+mRadioSelectedPostion);
					if (mRadioSelectedPostion == 0){
						image.setBackgroundResource(R.drawable.pin_sharp_circle_press);
					}else if (mRadioSelectedPostion == 1){
						image.setBackgroundResource(R.drawable.tap_click_force_touch_press);
					}
				}
				if (event.getAction() == MotionEvent.ACTION_UP){
					name.setTextColor(Color.parseColor("#999999"));
					v.setBackgroundColor(Color.parseColor("#ffffff"));
					Log.w("mingguo", "right   selected position up  "+mRadioSelectedPostion);
					if (mRadioSelectedPostion == 0){
						image.setBackgroundResource(R.drawable.pin_sharp_circle);
					}else if (mRadioSelectedPostion == 1){
						image.setBackgroundResource(R.drawable.tap_click_force_touch);
					}
				}
				return false;
			}
		});
		mBtnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("mingguo", "share  onclick left ");
				
			}
		});
		mBtnRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("mingguo", "share  onclick  right ");
				
			}
		});
		mListView = (HomeFragmentListView) mRootView.findViewById(R.id.home_listview);
//		mListView.setAdapter(new );
//		///////////////////////////////////////////////////////////////////////////////
		
		LinearLayout button = (LinearLayout) mRootView.findViewById(R.id.id_home_select_city_content);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SelectShowCityActivity.class);
				intent.putExtra("current_city", mCurrentLocationCity);
				getActivity().startActivityForResult(intent, CommonUtil.mSelectCityRequestCode);

			}
		});
		mSelectCityText = (TextView) mRootView.findViewById(R.id.id_home_show_city_view);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSearchListener = (AutoCompleteTextView) mRootView.findViewById(R.id.button_search);
		sugAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line);
		mSearchListener.setAdapter(sugAdapter);
		mSearchListener.setThreshold(1);

		// 初始化建议搜索模块，注册建议搜索事件监听
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		// 地图初始化

		// 开启定位图层
		// mBaiduMap.setMyLocationEnabled(true);
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
		mSearchListener.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				if (cs.length() <= 0) {
					return;
				}

				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				Log.i("mingguo", "house fragment  onTextChanged   " + mCurrentLocationCity);
				mSuggestionSearch.requestSuggestion(
						(new SuggestionSearchOption()).keyword(cs.toString()).city(mCurrentLocationCity));
			}
		});

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
		//
		//
		// Button houseSearch =
		// (Button)detailView.findViewById(R.id.id_house_detail_search);
		// houseSearch.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent detailIntent = new Intent(mContext,
		// HouseDetailInfoActivity.class);
		// detailIntent.putExtra("rentNo",
		// mHouserList.get(index).get("rentno"));
		// startActivity(detailIntent);
		// }
		// });
		// return true;
		//
		// }
		// });

		// Button rentToButton =
		// (Button)mRootView.findViewById(R.id.id_home_button_chuzu_house);
		// rentToButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// startActivity(new Intent(getActivity(), RentToHouseActivity.class));
		//
		// }
		// });
		// Button rentButton =
		// (Button)mRootView.findViewById(R.id.id_home_button_rent_house);
		// rentButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// Intent intent = new Intent(mContext, HouseSearchActivity.class);
		// startActivity(intent);
		// }
		// });

	}

	// 查询周围2000米的某类建筑
	public void searchNearbyProcess(String searchText) {
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


	private void getLocationByCoordinates() {
		Log.w("mingguo", "house  fragment  location by coordates lati  " + mCurrentLatLng.latitude + "  longti  "
				+ mCurrentLatLng.longitude);
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetRentsByCoodinates";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mLocationAction));
		rpc.addProperty("lat", mCurrentLatLng.latitude + "");
		rpc.addProperty("lon", mCurrentLatLng.longitude + "");
		rpc.addProperty("distance", "15000");
		mPresenter.readyPresentServiceParams(mContext, url, mLocationAction, rpc);
		mPresenter.startPresentServiceTask();
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
			} else if (msg.what == 300) {
				showSelectLocationMap();
			} else if (msg.what == 500) {
				updateLocationCity();
			}
		}
	};
	private ArrayList<String> suggest;
	private RadioButton mRbShare;
	private RadioButton mRbOwner;
	private RadioGroup mRadioGroup;
	private HomeCustomView mBtnLeft;
	private HomeFragmentListView mListView;



	private void parseLocationInfo(String value) {
		mHouserList = new ArrayList<>();
		mMarkList = new ArrayList<>();
		try {
			if (mHouserList != null) {
				mHouserList.clear();
			}
			if (mMarkList != null) {
				mMarkList.clear();
			}
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("mingguo", "house  location  num   " + array.length());
				for (int item = 0; item < array.length(); item++) {
					Map<String, String> itemHouse = new HashMap<>();
					JSONObject itemJsonObject = array.optJSONObject(item);
					itemHouse.put("ROwnerTel", itemJsonObject.optString("ROwnerTel"));
					itemHouse.put("Latitude", itemJsonObject.optString("Latitude"));
					itemHouse.put("Longitude", itemJsonObject.optString("Longitude"));
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
			if (location == null) {
				return;
			}
			mCurrentLocationCity = location.getCity();
			
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			if (isFirstLoc) {
				isFirstLoc = false;
				CommonUtil.mCurrentLati = location.getLatitude();
				CommonUtil.mCurrentLongi = location.getLongitude();
				mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(mCurrentLatLng).zoom(18.0f);
				Log.i("mingguo", "loaction current city  "+mCurrentLocationCity+"  CommonUtil.mCurrentLati  "+CommonUtil.mCurrentLati+"  CommonUtil.mCurrentLongi  "+CommonUtil.mCurrentLongi);
			}
			//startGetLocationFromHouse();
			mHandler.sendEmptyMessageDelayed(500, 100);
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}
	}

	private void showSelectLocationMap() {
		Log.i("mingguo",
				"select location site  lati  " + mCurrentLatLng.latitude + "  longi  " + mCurrentLatLng.longitude);
		MyLocationData locData = new MyLocationData.Builder().accuracy(0)
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(mCurrentLatLng.latitude).longitude(mCurrentLatLng.longitude).build();
		MapStatus.Builder builder = new MapStatus.Builder();
		builder.target(mCurrentLatLng).zoom(18.0f);

	}

	private void updateLocationCity() {
		mSelectCityText.setText(mCurrentLocationCity);
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on map view status success  action  " + action + "  info  " + templateInfo);
		if (action != null) {
			if (action.equalsIgnoreCase(mLocationAction)) {
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
		Log.e("housefragment", "error   " + error);
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
		Log.e("mingguo", "House fragment  onGetPoiResult  " + result.error);
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			if (result.getAllPoi().size() > 0) {
				mCurrentLatLng = result.getAllPoi().get(0).location;
			}
			startGetLocationFromHouse();
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
		suggest = new ArrayList<String>();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null) {
				suggest.add(info.key);
			}
		}
		sugAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, suggest);
		mSearchListener.setAdapter(sugAdapter);
		sugAdapter.notifyDataSetChanged();
	}

}
