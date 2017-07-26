package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.model.SurroundInfo;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.search.ExpandMenuView;
import tenant.guardts.house.search.Item;
import tenant.guardts.house.util.CommonUtil;

/**
 * 	Copyright	2016	CoderDream's Eclipse
 * 
 * 	All right reserved.
 * 	
 * 	Created on 2016年3月17日 下午4:59:44
 * 	
 * 	Update on 2016年3月17日 下午4:59:44
 * 
 * 	@author xiaoming
 *	
 * 	@mail wangfeng.wf@warmdoc.com
 * 
 * 	@tags An overview of this file: 可扩展的条件筛选菜单Demo主页
 * 
 */
public class SurroundResultActivity extends BaseActivity implements OnGetPoiSearchResultListener{

	
	private Context mContext;
	private HoursePresenter mPresent;
	private PoiSearch mPoiSearch;
	private View mLoadingView;
	private TextView mNoContent;
	private UniversalAdapter<SurroundInfo> mAdapter;
	private ListView mSurroundListview;
	private List<SurroundInfo> mDataList = new ArrayList<>();
	private String mSearchTag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.surround_result_layout);
		mSearchTag = getIntent().getStringExtra("search_text");
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText(mSearchTag);
		showLoadingView();
		initData();
		initView();
		
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mContext = getApplicationContext();
		mPresent = new HoursePresenter(mContext, SurroundResultActivity.this);
		mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
		searchNearbyProcess(mSearchTag);
	}
	
	private void showLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}
	

	public void  searchNearbyProcess(String text ) {
		Log.i("mingguo", " search near by  lati  "+CommonUtil.mCurrentLati+"  longi  "+CommonUtil.mCurrentLongi+"  text  "+text);
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(text)
        		.sortType(PoiSortType.distance_from_near_to_far).location(new LatLng(CommonUtil.mCurrentLati, CommonUtil.mCurrentLongi))
                .radius(2000).pageCapacity(50);
        mPoiSearch.searchNearby(nearbySearchOption);
    }
	
	private void searchPoiDetailProcess(String uid){
		mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()  
                 .poiUid(uid));  
	}
	
	private void initAdapter(){
		mAdapter = new UniversalAdapter<SurroundInfo>(mContext, R.layout.house_search_list_item, mDataList) {

			@Override
			public void convert(UniversalViewHolder holder, SurroundInfo info) {
//				View holderView = holder.getConvertView();
//				TextView addressTextView = (TextView)holderView.findViewById(R.id.id_house_address);
//				TextView typeTextView = (TextView)holderView.findViewById(R.id.id_house_type);
//				TextView directionTextView = (TextView)holderView.findViewById(R.id.id_house_direction);
//				TextView floorTextView = (TextView)holderView.findViewById(R.id.id_house_floor);
//				TextView statusTextView = (TextView)holderView.findViewById(R.id.id_house_status);
//				addressTextView.setText(info.getHouseAddress());
//				typeTextView.setText(info.getHouseType());
//				directionTextView.setText(info.getHouseDirection());
//				floorTextView.setText(info.getHouseCurrentFloor()+"/"+info.getHouseTotalFloor()+"层");
//				statusTextView.setText(info.getHouseStatus());
			}
		};
	}

	private void initView() {
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		//mContentLayout = (LinearLayout)findViewById(R.id.id_frament_house_cotent);
		mNoContent = (TextView)findViewById(R.id.id_frament_house_no_cotent);
		mLoadingView.setVisibility(View.INVISIBLE);
		mNoContent.setVisibility(View.VISIBLE);
		
		mSurroundListview = (ListView)findViewById(R.id.id_surround_listview);
		initAdapter();
		mSurroundListview.setAdapter(mAdapter);
		
	}
	
	

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(mContext, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        Log.i("mingguo", "poi  result  all  poi   "+result.getAllPoi().size()+"  address size  ");
       for (int index = 0; index < result.getAllPoi().size(); index++){
    	   PoiInfo info = result.getAllPoi().get(index);
    	   SurroundInfo surroundInfo = new SurroundInfo();
    	   surroundInfo.setNearUid(info.uid);
    	   searchPoiDetailProcess(info.uid);
    	   mDataList.add(surroundInfo);
       }
       if (mDataList.size() > 0){
    	   mAdapter.notifyDataSetChanged();
       }
       
	}
	
	

	
}
