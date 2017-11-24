package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.AddHouseInfoActivity;
import tenant.guardts.house.GetRentAttributeActivity;
import tenant.guardts.house.HomeSearchActivity;
import tenant.guardts.house.HouseDetailInfoActivity;
import tenant.guardts.house.LocationDemo;
import tenant.guardts.house.LoginUserActivity;
import tenant.guardts.house.MapRentHouseActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.bannerview.CircleFlowIndicator;
import tenant.guardts.house.bannerview.ImagePagerAdapter;
import tenant.guardts.house.bannerview.ViewFlow;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.view.HomeFragmentListView;

public class HouseFragment extends BaseFragment implements OnGetPoiSearchResultListener,
		OnGetSuggestionResultListener, OnItemClickListener {

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
	private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
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
	// private AutoCompleteTextView mSearchListener = null;
	// private ArrayAdapter<String> sugAdapter = null;
	private SuggestionSearch mSuggestionSearch;
	private TextView mSelectCityText;
	// private HomeCustomView mBtnShareRight;
	private LinearLayout mShareHouseLayout;
	private LinearLayout mHouseOwnerLayout;
	private String mUserName;
	private String mUserInfoAction = "http://tempuri.org/GetUserInfo";
	private SwipeRefreshLayout mSwipeLayout;
	//private FrameLayout mTitlebarContent;
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mFlowIndicator;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
//		getActivity().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_titlebar_home);
//		mTitlebarContent  = (FrameLayout) getActivity().getWindow().findViewById(R.id.id_title_bar_home_content);
		Bundle bundle = getArguments();
		
		mPresenter = new HoursePresenter(mContext, HouseFragment.this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.activity_location, container, false);
		initView();
		initEvent();
		// initData();
		initBanner();
		return mRootView;
	}
	
	private void initBanner() {
		final List<Integer> localImage = new ArrayList<>();
		localImage.add(R.drawable.home_fragment_bg2);
		localImage.add(R.drawable.home_fragment_bg);
		localImage.add(R.drawable.home_fragment_bg3);
		mViewFlow = (ViewFlow) mRootView.findViewById(R.id.id_fragment_home_viewflow);
		mFlowIndicator = (CircleFlowIndicator) mRootView.findViewById(R.id.id_fragment_home_indicator);
		mViewFlow.setAdapter(new ImagePagerAdapter(getActivity(), localImage,
				null, null).setInfiniteLoop(true));
//		UniversalAdapter<Integer> adapter = new UniversalAdapter<Integer>(getActivity(), R.layout.house_fragment_banner_item_view, localImage) {
//			
//			@Override
//			public void convert(UniversalViewHolder holder, Integer info) {
//				View itemview = holder.getConvertView();
//				ImageView image = (ImageView)itemview.findViewById(R.id.banner_image);
//				image.setBackgroundResource(localImage.get(holder.getPosition()));
//			}
//		};
//		mViewFlow.setAdapter(adapter);
		mViewFlow.setmSideBuffer(localImage.size()); 
		mFlowIndicator.setIndicatorCount(localImage.size());
		
		mViewFlow.setFlowIndicator(mFlowIndicator);
		mViewFlow.setTimeSpan(2000);
		mViewFlow.setSelection(localImage.size() * 1000); // 设置初始位置
		mViewFlow.startAutoFlowTimer(); // 启动自动播放
		mFlowIndicator.requestLayout();
		mFlowIndicator.invalidate();
	}

	private void initEvent() {

		// 登录
		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Intent loginIntent = new Intent(mContext, LoginUserActivity.class);
					startActivity(loginIntent);
				}

			}
		});
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.home_radio_share:
					mShareHouseLayout.setVisibility(View.VISIBLE);
					mHouseOwnerLayout.setVisibility(View.GONE);
					break;
				case R.id.home_radio_owner:
					mShareHouseLayout.setVisibility(View.GONE);
					mHouseOwnerLayout.setVisibility(View.VISIBLE);
					break;
				}
			}
		});
		
		TextView careButton = (TextView)mRootView.findViewById(R.id.id_home_rent_care_button);
		careButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent careIntent = new Intent(mContext, GetRentAttributeActivity.class);
				startActivity(careIntent);
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.w("mingguo", "house fragment  on resume  login name   "+CommonUtil.mUserLoginName);
		if (CommonUtil.mUserLoginName != null && !CommonUtil.mUserLoginName.equals("")){
			mLogin.setVisibility(View.GONE);
		}else{
			mLogin.setVisibility(View.VISIBLE);
			SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
			mUserName = sharedata.getString("user_name", "");
			LogUtil.w("mingguo", "HouseFragment  username   "+mUserName+"  password  ");
			mHandler.sendEmptyMessageDelayed(6000, 5);
		}
	}
	
	private void getUserInfo() {
		if (mUserName == null || mUserName.equals("")){
			return;
		}
		String url = CommonUtil.mUserHost + "services.asmx?op=GetUserInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUserInfoAction));
		rpc.addProperty("username", mUserName);
		rpc.addProperty("deviceId", CommonUtil.XINGE_TOKEN);
		mPresenter.readyPresentServiceParams(getActivity(), url, mUserInfoAction, rpc);
		mPresenter.startPresentServiceTask(false);
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
		
//		ImageView homeTopImage = (ImageView)mRootView.findViewById(R.id.id_home_image);
//		homeTopImage.setFocusable(true);  
//		homeTopImage.setFocusableInTouchMode(true);  
//		homeTopImage.requestFocus();  
		
		mLogin = (TextView) mRootView.findViewById(R.id.textview_login);
		if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {

		} else {
			mLogin.setVisibility(View.GONE);
		}

		TextView mGovernmentService = (TextView) mRootView.findViewById(R.id.tv_government_service);// 政府服务
		TextView mHeXiPower = (TextView) mRootView.findViewById(R.id.tv_hexi_power);// 河西力量
		TextView mSafeguardRights = (TextView) mRootView.findViewById(R.id.tv_safeguard_rights);// 维权常识
		TextView mMedicalInsurance = (TextView) mRootView.findViewById(R.id.tv_medical_insurance);// 医疗保险
		TextView mUseFulNumber = (TextView) mRootView.findViewById(R.id.tv_phone);// 常用电话

		TextView traffic = (TextView) mRootView.findViewById(R.id.tv_triffic);// 常用电话
		TextView surround = (TextView) mRootView.findViewById(R.id.tv_surrounds);// 常用电话
		TextView more = (TextView) mRootView.findViewById(R.id.tv_more);// 常用电话
		
		mGovernmentService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", CommonUtil.URL_ZHENGFU_FUWU);
//				loadIntent.putExtra("tab_name", "政府服务");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse(CommonUtil.URL_ZHENGFU_FUWU);   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		mHeXiPower.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					GlobalUtil.launcherAppWithPackageName(mContext, CommonUtil.HEXI_POWER_PACKAGE_NAME);
				} catch (Exception e) {
					try {
		                Intent i = new Intent(Intent.ACTION_VIEW);
		                i.setData(Uri.parse("market://details?id="+CommonUtil.HEXI_POWER_PACKAGE_NAME));
		                startActivity(i);
		            } catch (Exception e2) {
		                Toast.makeText(mContext, "您的手机上没有安装Android应用市场", Toast.LENGTH_SHORT).show();
		                e.printStackTrace();
		            }
				}
				
			}
		});
		mSafeguardRights.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", CommonUtil.URL_WEIQUAN_CHANGSHI);
//				loadIntent.putExtra("tab_name", "维权常识");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse(CommonUtil.URL_WEIQUAN_CHANGSHI);   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		mMedicalInsurance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", CommonUtil.URL_YILIAO_BAOXIAN);
//				loadIntent.putExtra("tab_name", "医疗保险");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse(CommonUtil.URL_YILIAO_BAOXIAN);   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		mUseFulNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", CommonUtil.URL_COMMON_PHONE);
//				loadIntent.putExtra("tab_name", "常用电话");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse(CommonUtil.URL_COMMON_PHONE);   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		
		traffic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", CommonUtil.URL_JIAOTONG_CHUXING);
//				loadIntent.putExtra("tab_name", "交通出行");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse(CommonUtil.URL_JIAOTONG_CHUXING);   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		
		surround.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", CommonUtil.URL_ZHOUBIAN_SHANGQU);
//				loadIntent.putExtra("tab_name", "周边商区");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse(CommonUtil.URL_ZHOUBIAN_SHANGQU);   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		
		more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", CommonUtil.URL_MORE_CONTENT);
//				loadIntent.putExtra("tab_name", "更多");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse(CommonUtil.URL_MORE_CONTENT);   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});

		mRbShare = (RadioButton) mRootView.findViewById(R.id.home_radio_share);
		mRbOwner = (RadioButton) mRootView.findViewById(R.id.home_radio_owner);
		mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.home_radiogrouop);
		mShareHouseLayout = (LinearLayout) mRootView.findViewById(R.id.id_share_house_content);
		mHouseOwnerLayout = (LinearLayout) mRootView.findViewById(R.id.id_house_owner_content);
		mShareHouseLayout.setVisibility(View.VISIBLE);
		mHouseOwnerLayout.setVisibility(View.GONE);

		LinearLayout fabuLayout = (LinearLayout) mRootView.findViewById(R.id.id_home_house_owner_dengji);
		LinearLayout weixiuLayout = (LinearLayout) mRootView.findViewById(R.id.id_home_house_owner_weixiu);
		LinearLayout gaizhaoLayout = (LinearLayout) mRootView.findViewById(R.id.id_home_house_owner_gaizhao);
		fabuLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 先判断是否登录
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					Intent loginIntent = new Intent(mContext, LoginUserActivity.class);
					startActivity(loginIntent);
				} else {
					// 发布房屋
					Intent intent = new Intent(mContext, AddHouseInfoActivity.class);
					intent.putExtra("user_name", CommonUtil.mUserLoginName);
					startActivity(intent);
				}
			}
		});

		weixiuLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GlobalUtil.shortToast(mContext, "该模块正在开发中，敬请期待！！", getResources().getDrawable(R.drawable.ic_dialog_no));
			}
		});

		gaizhaoLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GlobalUtil.shortToast(mContext, "该模块正在开发中，敬请期待！！", getResources().getDrawable(R.drawable.ic_dialog_no));
			}
		});

		LinearLayout duanzuShareHouse = (LinearLayout) mShareHouseLayout.findViewById(R.id.id_share_house_duanzu);
		duanzuShareHouse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 房屋检索
				startActivity(new Intent(mContext, HomeSearchActivity.class));
			}
		});
		LinearLayout mapRentHouse = (LinearLayout) mRootView.findViewById(R.id.id_share_house_ditu);
		mapRentHouse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
//					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
//					startActivity(new Intent(mContext, LoginUserActivity.class));
//				} else {
					// 地图租房
					startActivity(new Intent(mContext, MapRentHouseActivity.class));
//				}

			}
		});
		
		mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_ly);
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				
				//mHandler.sendEmptyMessageDelayed(4000, 5000);
				startGetLocationFromHouse();
			}
		});  
		
		mSwipeLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE); 
        
		mListView = (HomeFragmentListView) mRootView.findViewById(R.id.id_home_house_fragment_listview);
		// mListView.setAdapter(new );
		// ///////////////////////////////////////////////////////////////////////////////

		LinearLayout button = (LinearLayout) mRootView.findViewById(R.id.id_home_select_city_content);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(mContext,
				// SelectShowCityActivity.class);
				// intent.putExtra("current_city", mCurrentLocationCity);
				// getActivity().startActivityForResult(intent,
				// CommonUtil.mSelectCityRequestCode);

			}
		});
		mSelectCityText = (TextView) mRootView.findViewById(R.id.id_home_show_city_view);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		//TextView searchText = (TextView) mRootView.findViewById(R.id.button_search);
		// sugAdapter = new ArrayAdapter<String>(getActivity(),
		// android.R.layout.simple_dropdown_item_1line);
		// mSearchListener.setAdapter(sugAdapter);
		// mSearchListener.setThreshold(1);
		FrameLayout searchLayout = (FrameLayout) mRootView.findViewById(R.id.home_search_container);
		searchLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, HomeSearchActivity.class));
			}
		});

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
		// mSearchListener.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		//
		// }
		//
		// @Override
		// public void onTextChanged(CharSequence cs, int arg1, int arg2, int
		// arg3) {
		// if (cs.length() <= 0) {
		// return;
		// }
		//
		// /**
		// * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
		// */
		// LogUtil.w("mingguo", "house fragment onTextChanged " +
		// mCurrentLocationCity);
		// mSuggestionSearch.requestSuggestion(
		// (new
		// SuggestionSearchOption()).keyword(cs.toString()).city(mCurrentLocationCity));
		// }
		// });

		initAdapter();

		mListView.setAdapter(mAdapter);
		TextView empty = new TextView(mContext);
		empty.setText("未搜索到任何信息！");
		mListView.setEmptyView(empty);
		mListView.setOnItemClickListener(this);

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
		if (mCurrentLatLng == null){
			return;
		}
		LogUtil.w("mingguo", "house  fragment  location by coordates lati  " + mCurrentLatLng.latitude + "  longti  "
				+ mCurrentLatLng.longitude);
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetRentsByCoodinates";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mLocationAction));
		rpc.addProperty("lat", mCurrentLatLng.latitude + "");
		rpc.addProperty("lon", mCurrentLatLng.longitude + "");
		rpc.addProperty("distance", "10000");
		mPresenter.readyPresentServiceParams(getActivity(), url, mLocationAction, rpc);
		mPresenter.startPresentServiceTask(false);
	}

	private void startGetLocationFromHouse() {
		getLocationByCoordinates();
	}
	
	private void parseUserInfo(String value) {
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				LogUtil.w("house", "parse house info " + array.length());
				// for (int item = 0; item < array.length(); item++){
				JSONObject itemJsonObject = array.optJSONObject(0);
				
				CommonUtil.mUserLoginName = itemJsonObject.optString("LoginName");
				CommonUtil.mRegisterRealName = itemJsonObject.optString("RealName");
				CommonUtil.mRegisterIdcard = itemJsonObject.optString("IDCard");
				CommonUtil.mUserWallet = itemJsonObject.optString("Wallet");
				CommonUtil.mBankName= itemJsonObject.optString("BankName");
				CommonUtil.mCardNo = itemJsonObject.optString("CardNO");
				SharedPreferences sharedata = getActivity().getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
				editor.putString("user_realname", CommonUtil.mRegisterRealName);
				editor.putString("user_idcard", CommonUtil.mRegisterIdcard);
				editor.commit();
				mLogin.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100) {
				mSwipeLayout.setRefreshing(false);
				parseLocationInfo((String) msg.obj);
				mAdapter.notifyDataSetChanged();
			} else if (msg.what == 300) {
				showSelectLocationMap();
			} else if (msg.what == 5000) {
				updateLocationCity();
				startGetLocationFromHouse();
			}else if (msg.what == 6000){
				getUserInfo();
			}else if (msg.what == 101){
				if (msg.obj != null) {
					parseUserInfo((String) msg.obj);
				}
			}else if (msg.what == 4000){
				mSwipeLayout.setRefreshing(false);
			}
		}
	};
	private ArrayList<String> suggest;
	private RadioButton mRbShare;
	private RadioButton mRbOwner;
	private RadioGroup mRadioGroup;
	private HomeFragmentListView mListView;
	private UniversalAdapter<HouseInfoModel> mAdapter;
	private TextView mLogin;

	private void parseLocationInfo(String obj) {
		try {
			JSONArray array = new JSONArray(obj);
			if (array != null) {
				mHouseInfoList.clear();
				for (int item = 0; item < array.length(); item++) {
					JSONObject itemJsonObject = array.optJSONObject(item);
					HouseInfoModel houseModel = new HouseInfoModel();
					houseModel.setHouseAddress(itemJsonObject.optString("RAddress"));
					houseModel.setHouseDirection(itemJsonObject.optString("rdirectiondesc"));
					houseModel.setHouseTotalFloor(itemJsonObject.optString("rtotalfloor"));
					houseModel.setHouseCurrentFloor(itemJsonObject.optString("rFloor"));
					houseModel.setHouseType(itemJsonObject.optString("rroomtypedesc"));
					houseModel.setHouseStatus(itemJsonObject.optString("IsAvailable"));
					houseModel.setHouseAvailable(itemJsonObject.optBoolean("Available"));
					houseModel.setHouseId(itemJsonObject.optString("rentno"));
					houseModel.setHouseOwnerName(itemJsonObject.optString("ROwner"));
					houseModel.setHouseOwnerIdcard(itemJsonObject.optString("RIDCard"));
					houseModel.setHouseArea(itemJsonObject.optString("rrentarea"));
					mHouseInfoList.add(houseModel);
				}
			}
			LogUtil.w("mingguo", "search  result  mHouseInfoList  " + mHouseInfoList.size());
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
				LogUtil.w("mingguo", "loaction current city  " + mCurrentLocationCity + "  CommonUtil.mCurrentLati  "
						+ CommonUtil.mCurrentLati + "  CommonUtil.mCurrentLongi  " + CommonUtil.mCurrentLongi);
			}
			
			mHandler.sendEmptyMessageDelayed(5000, 10);
		}

		public void onReceivePoi(BDLocation poiLocation) {
			

		}

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}
	}

	private void initAdapter() {
		mAdapter = new UniversalAdapter<HouseInfoModel>(mContext, R.layout.house_search_list_item, mHouseInfoList) {

			@Override
			public void convert(UniversalViewHolder holder, HouseInfoModel info) {
				View holderView = holder.getConvertView();
				TextView addressTextView = (TextView) holderView.findViewById(R.id.id_house_address);
				TextView typeTextView = (TextView) holderView.findViewById(R.id.id_house_type);
				TextView directionTextView = (TextView) holderView.findViewById(R.id.id_house_direction);
				TextView floorTextView = (TextView) holderView.findViewById(R.id.id_house_floor);
				TextView areaTextView = (TextView) holderView.findViewById(R.id.id_house_area);
				addressTextView.setText(info.getHouseAddress());
				typeTextView.setText(info.getHouseType());
				directionTextView.setText(info.getHouseDirection());
				floorTextView.setText(info.getHouseCurrentFloor() + "/" + info.getHouseTotalFloor() + "层");
				areaTextView.setText(info.getHouseArea() + "平米");
			}
		};
	}

	private void showSelectLocationMap() {
		LogUtil.w("mingguo",
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
		LogUtil.e("mingguo", "on status success action  "+action+"  return value "+templateInfo);
		super.onStatusSuccess(action, templateInfo);
		if (action != null && templateInfo != null){
			if (action.equalsIgnoreCase(mLocationAction)) {
				Message message = mHandler.obtainMessage();
				message.what = 100;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}else if (action.equals(mUserInfoAction)) {
				Message message = mHandler.obtainMessage();
				message.what = 101;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
			Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
			Intent loginIntent = new Intent(mContext, LoginUserActivity.class);
			startActivity(loginIntent);
		} else {
			if (mHouseInfoList.get(position).getHouseId() != null
					&& !mHouseInfoList.get(position).getHouseId().equals("")) {
				Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
				detailIntent.putExtra("rentNo", mHouseInfoList.get(position).getHouseId());
				startActivity(detailIntent);
			}
		}
		
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
		LogUtil.e("mingguo", "House fragment  onGetPoiResult  " + result.error);
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			if (result.getAllPoi().size() > 0) {
				mCurrentLatLng = result.getAllPoi().get(0).location;
			}
//			startGetLocationFromHouse();
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
		// sugAdapter = new ArrayAdapter<String>(getActivity(),
		// android.R.layout.simple_dropdown_item_1line, suggest);
		// mSearchListener.setAdapter(sugAdapter);
		// sugAdapter.notifyDataSetChanged();
	}


}
