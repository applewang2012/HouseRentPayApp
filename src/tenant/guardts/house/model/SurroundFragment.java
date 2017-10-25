package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import tenant.guardts.house.LoadUrlTestActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.SurroundMoreActivity;
import tenant.guardts.house.SurroundResultActivity;
import tenant.guardts.house.bannerview.CircleFlowIndicator;
import tenant.guardts.house.bannerview.ImagePagerAdapter;
import tenant.guardts.house.bannerview.ViewFlow;
import tenant.guardts.house.headergridview.StickyGridHeadersGridView;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.view.HomeFragmentListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

public class SurroundFragment extends BaseFragment implements DataStatusInterface, OnGetPoiSearchResultListener, OnItemClickListener{
	

	
	private Context mContext;
	private View mRootView;
	
	private StickyGridHeadersGridView mHeadersGridView;
	private HoursePresenter mPresent;
	private List<String> mTitleList = new ArrayList<>();
	private Map<Integer, String[]> mContentMap = new HashMap<>();
	private List<String> mContentList = new ArrayList<>();
	private PoiSearch mPoiSearch;
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mFlowIndicator;
	private List<SurroundInfo> mDataList = new ArrayList<>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mPresent = new HoursePresenter(mContext, SurroundFragment.this);
//		getActivity().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
//		TextView bartext  = (TextView) getActivity().getWindow().findViewById(R.id.id_titlebar);
//		bartext.setText("周边生活");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LogUtil.w("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.house_surround_fragment, container, false);
		initTitleBar();
		initAdapter();
		initSurroundHot();
		initData();
		initBanner();
		initSearchData();
		
		return mRootView;
	}
	
	private void initTitleBar(){
		
		View titlebarView = (View)mRootView.findViewById(R.id.id_common_title_bar);
		TextView titleText = (TextView) titlebarView.findViewById(R.id.id_titlebar);
		titleText.setText("周边生活");
		FrameLayout backButton = (FrameLayout)titlebarView.findViewById(R.id.id_titlebar_back);
		backButton.setVisibility(View.INVISIBLE);
	}
	
	private void initBanner() {
		List<Integer> localImage = new ArrayList<>();
		localImage.add(R.drawable.house_surround_life_viewflow1);
		localImage.add(R.drawable.house_surround_life_viewflow2);
		localImage.add(R.drawable.house_surround_life_viewflow3);
		mViewFlow = (ViewFlow) mRootView.findViewById(R.id.id_fragment_surround_life_viewflow);
		mFlowIndicator = (CircleFlowIndicator) mRootView.findViewById(R.id.id_fragment_surround_life_viewflow_indicator);
		mViewFlow.setAdapter(new ImagePagerAdapter(getActivity(), localImage,
				null, null).setInfiniteLoop(true));
		mViewFlow.setmSideBuffer(localImage.size()); // 实际图片张数，
														// 我的ImageAdapter实际图片张数为3
		mFlowIndicator.setIndicatorCount(localImage.size());
		
		mViewFlow.setFlowIndicator(mFlowIndicator);
		mViewFlow.setTimeSpan(2000);
		mViewFlow.setSelection(localImage.size() * 1000); // 设置初始位置
		mViewFlow.startAutoFlowTimer(); // 启动自动播放
		mFlowIndicator.requestLayout();
		mFlowIndicator.invalidate();
	}
	
	private void initSurroundHot(){
		LinearLayout eatbutton = (LinearLayout)mRootView.findViewById(R.id.id_fragment_surround_eat);
		eatbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
				surroundIntent.putExtra("search_text", "美食");
				startActivity(surroundIntent);
				
			}
		});
		LinearLayout hotelbutton = (LinearLayout)mRootView.findViewById(R.id.id_fragment_surround_hotle);
		hotelbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
				surroundIntent.putExtra("search_text", "酒店");
				startActivity(surroundIntent);
				
			}
		});
		LinearLayout bankbutton = (LinearLayout)mRootView.findViewById(R.id.id_fragment_surround_bank);
		bankbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
				surroundIntent.putExtra("search_text", "银行");
				startActivity(surroundIntent);
				
			}
		});
		LinearLayout jingdianbutton = (LinearLayout)mRootView.findViewById(R.id.id_fragment_surround_jingdian);
		jingdianbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
				surroundIntent.putExtra("search_text", "景点");
				startActivity(surroundIntent);
				
			}
		});
		LinearLayout trafficbutton = (LinearLayout)mRootView.findViewById(R.id.id_fragment_surround_traffic);
		trafficbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
				surroundIntent.putExtra("search_text", "交通");
				startActivity(surroundIntent);
				
			}
		});
		LinearLayout parkbutton = (LinearLayout)mRootView.findViewById(R.id.id_fragment_surround_park);
		parkbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
				surroundIntent.putExtra("search_text", "停车场");
				startActivity(surroundIntent);
				
			}
		});
		LinearLayout oilbutton = (LinearLayout)mRootView.findViewById(R.id.id_fragment_surround_oil);
		oilbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
				surroundIntent.putExtra("search_text", "加油站");
				startActivity(surroundIntent);
				
			}
		});
		LinearLayout morebutton = (LinearLayout)mRootView.findViewById(R.id.id_fragment_surround_more);
		morebutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent surroundIntent = new Intent(mContext, SurroundMoreActivity.class);
				startActivity(surroundIntent);
				
			}
		});
		
	}
	
	public void  searchNearbyProcess(String text ) {
		LogUtil.w("mingguo", " search near by  lati  "+CommonUtil.mCurrentLati+"  longi  "+CommonUtil.mCurrentLongi+"  text  "+text);
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(text)
        		.sortType(PoiSortType.distance_from_near_to_far).location(new LatLng(CommonUtil.mCurrentLati, CommonUtil.mCurrentLongi))
                .radius(2000).pageCapacity(20);
        mPoiSearch.searchNearby(nearbySearchOption);
    }
	
	
	public  class TitleViewHolder {
        public TextView textView;
    }
	
	public  class ContentViewHolder {
        public Button button;
    }
	
	private void initData(){
		mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

	     
		String [] title = getResources().getStringArray(R.array.surround_title);
		for (int titleIndex = 0; titleIndex < 8; titleIndex++){
			mTitleList.add(title[titleIndex]);
		}
		String [] childHot = getResources().getStringArray(R.array.surround_content_hot);
		String [] childEat = getResources().getStringArray(R.array.surround_content_eat);
		String [] childHotel = getResources().getStringArray(R.array.surround_content_hotel);
		String [] childTravel = getResources().getStringArray(R.array.surround_content_travel);
		String [] childLeasure = getResources().getStringArray(R.array.surround_content_leasure);
		String [] childLive = getResources().getStringArray(R.array.surround_content_live);
		String [] childJiandian = getResources().getStringArray(R.array.surround_content_jingdian);
		String [] childBuy = getResources().getStringArray(R.array.surround_content_buy);
		for (int contentIndex = 0; contentIndex < 8; contentIndex++){
			if (contentIndex == 0){
				mContentMap.put(contentIndex, childHot);
			}else if (contentIndex == 1){
				mContentMap.put(contentIndex, childEat);
			}else if (contentIndex == 2){
				mContentMap.put(contentIndex, childHotel);
			}else if (contentIndex == 3){
				mContentMap.put(contentIndex, childTravel);
			}else if (contentIndex == 4){
				mContentMap.put(contentIndex, childLeasure);
			}else if (contentIndex == 5){
				mContentMap.put(contentIndex, childLive);
			}else if (contentIndex == 6){
				mContentMap.put(contentIndex, childJiandian);
			}else if (contentIndex == 7){
				mContentMap.put(contentIndex, childBuy);
			}
		}
		
		for (int index = 0; index < mContentMap.size(); index++){
			String [] childContent = mContentMap.get(index);
			for (int childIndex = 0; childIndex < childContent.length; childIndex++){
				mContentList.add(childContent[childIndex]);
			}
		}
		
	}
	
	private void initSearchData() {
		mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
		searchNearbyProcess("美食");
	}
	
	private int getContentCount(){
		int num = 0;
		for (int i =0; i < mContentMap.size(); i++){
			int childCount = mContentMap.get(i).length;
			num = num + childCount;
		}
		return num;
	}
	
	
	
	
	@Override
	public void onDestroy() {
		mPoiSearch.destroy();
		super.onDestroy();
	}

	
	private void searchPoiDetailProcess(String uid){
		mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()  
                 .poiUid(uid));  
	}


	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			HashMap<String,String> infoModel = parseUserInfo((String)msg.obj);
//			
//			if (infoModel != null){
//				mUserContainer.setVisibility(View.VISIBLE);
//				
//				mUserAddress.setText(infoModel.get("Phone"));
//				mUserId.setText(infoModel.get("LoginName"));
//			}
		}
	};
	private UniversalAdapter<SurroundInfo> mAdapter;
	private HomeFragmentListView mSurroundListview;
	
	public static HashMap<String,String> parseUserInfo(String value) {
		HashMap<String,String> userInfo = null;
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				LogUtil.w("house", "parse house info "+array.length());
				//for (int item = 0; item < array.length(); item++){
					
					JSONObject itemJsonObject = array.optJSONObject(0);
					userInfo = new HashMap<>();
					userInfo.put("NickName", itemJsonObject.optString("NickName"));
					userInfo.put("LoginName", itemJsonObject.optString("LoginName"));
					userInfo.put("Address", itemJsonObject.optString("Address"));
					userInfo.put("IDCard", itemJsonObject.optString("IDCard"));
					userInfo.put("Phone", itemJsonObject.optString("Phone"));
					
			}
			return userInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return userInfo;
		}
	}
	
	

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		LogUtil.e("mingguo", "success "+templateInfo);
		Message msgMessage = mHandler.obtainMessage();
		msgMessage.obj = templateInfo;
		msgMessage.sendToTarget();
	}


	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		LogUtil.w("mingguo", "surround fragment  onGetPoiDetailResult  result "+result.error);
		
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
        	for (int i = 0; i < mDataList.size(); i++){
    			if (result.getUid().equalsIgnoreCase(mDataList.get(i).getNearUid())){
    				mDataList.get(i).setNearDetailUrl(result.getDetailUrl());
    			}
    		}
        }
	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND || result.getAllPoi() == null) {
            Toast.makeText(mContext, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        LogUtil.w("mingguo", "poi  result  all  poi   "+result.getAllPoi().size()+"  address size  ");
        for (int index = 0; index < result.getAllPoi().size(); index++){
     	   PoiInfo info = result.getAllPoi().get(index);
     	   SurroundInfo surroundInfo = new SurroundInfo();
     	   surroundInfo.setNearUid(info.uid);
     	   surroundInfo.setNearAddress(info.address);
     	   surroundInfo.setNearName(info.name);
     	   surroundInfo.setNearPhone(info.phoneNum);
     	   searchPoiDetailProcess(info.uid);
     	   mDataList.add(surroundInfo);
        }
        if (mDataList.size() > 0){
     	   mAdapter.notifyDataSetChanged();
        }else{
     	  // mNoContent.setVisibility(View.VISIBLE);
        }
	}
	
	private void initAdapter(){
		mSurroundListview = (HomeFragmentListView)mRootView.findViewById(R.id.id_fragment_surround_life_listview);
		mAdapter = new UniversalAdapter<SurroundInfo>(mContext, R.layout.surround_fragment_list_item, mDataList) {

			@Override
			public void convert(UniversalViewHolder holder, SurroundInfo info) {
				View holderView = holder.getConvertView();
				TextView surroundname = (TextView)holderView.findViewById(R.id.id_surround_fragment_item_hot_name);
				TextView surroundaddress = (TextView)holderView.findViewById(R.id.id_surround_fragment_item_hot_address);
				TextView surroundphone = (TextView)holderView.findViewById(R.id.id_surround_fragment_item_hot_phone);
				surroundname.setText(info.getNearName());
				surroundaddress.setText(info.getNearAddress());
				surroundphone.setText(info.getNearPhone());
			}
		};
		mSurroundListview.setAdapter(mAdapter);
		mSurroundListview.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (mDataList.get(position).getNearDetailUrl()== null || mDataList.get(position).getNearDetailUrl().equals("")){
			GlobalUtil.shortToast(mContext, "抱歉，未获取到详细信息！", mContext.getResources().getDrawable(R.drawable.ic_dialog_no));
			return;
		}else{
//			Intent loadIntent = new Intent(mContext, LoadUrlTestActivity.class);
//			loadIntent.putExtra("url", mDataList.get(position).getNearDetailUrl());
//			loadIntent.putExtra("tab_name", mDataList.get(position).getNearName());
//			startActivity(loadIntent);
			
			Intent intent = new Intent();        
			intent.setAction("android.intent.action.VIEW");    
			Uri content_url = Uri.parse(mDataList.get(position).getNearDetailUrl());   
			intent.setData(content_url);  
			startActivity(intent);
		}
	}

	@Override
	public void onStatusStart(Activity activity) {
		// TODO Auto-generated method stub
		
	}
	
}
