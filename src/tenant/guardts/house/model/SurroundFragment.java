package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

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
import com.baidu.mapapi.search.sug.SuggestionSearch;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.LoginUserActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.headergridview.StickyGridHeadersBaseAdapter;
import tenant.guardts.house.headergridview.StickyGridHeadersGridView;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
//��Ӱ�ʱ���import android.support.v4.app.Fragment; 
@SuppressLint("NewApi")
public class SurroundFragment extends Fragment implements DataStatusInterface, OnGetPoiSearchResultListener{
	

	
	private Context mContext;
	private View mRootView;
	
	private StickyGridHeadersGridView mHeadersGridView;
	private HoursePresenter mPresent;
	private List<String> mTitleList = new ArrayList<>();
	private Map<Integer, String[]> mContentMap = new HashMap<>();
	private List<String> mContentList = new ArrayList<>();
	private PoiSearch mPoiSearch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mPresent = new HoursePresenter(mContext, SurroundFragment.this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.house_surround_fragment, container, false);
		initView();
		initData();
		return mRootView;
	}
	
	private void initView(){
		mHeadersGridView = (StickyGridHeadersGridView) mRootView.findViewById(R.id.sgv);
		mHeadersGridView.setAdapter(new StickyGridHeadersBaseAdapter() {

			private int mCurrentHeader;

			@Override
			public void unregisterDataSetObserver(DataSetObserver arg0) {

			}

			@Override
			public void registerDataSetObserver(DataSetObserver arg0) {

			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public int getViewTypeCount() {
				return 1;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
	                final Button b = new Button(mContext);
	                arg1 = b;
					b.setWidth(10);
					b.setHeight(50);
					b.setText(mContentList.get(arg0));
					b.setTextSize(15);
					b.setTextColor(Color.parseColor("#000000"));
					//b.setBackgroundColor(Color.parseColor("#0b6cfe"));
					b.setBackgroundColor(Color.parseColor("#00000000"));
					final String searchText = mContentList.get(arg0);
					b.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							searchNearbyProcess(searchText);
						}
					});
	            return b;
			}

			@Override
			public int getItemViewType(int arg0) {
				return 0;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				return getContentCount();
			}

			@Override
			public boolean isEnabled(int position) {
				return false;
			}

			@Override
			public boolean areAllItemsEnabled() {
				return false;
			}

			@Override
			public int getNumHeaders() {
				return mTitleList.size();
			}

			@Override
			public View getHeaderView(int position, View convertView,
					ViewGroup parent) {
//				TitleViewHolder holder = null;
//	            if (convertView == null) {
//	                holder = new TitleViewHolder();
	                TextView b = new TextView(mContext);
	                convertView = b;
					b.setWidth(200);
					b.setHeight(150);
					b.setTextSize(20);
					b.setPadding(20, 0, 0, 0);
					b.setGravity(Gravity.BOTTOM);
					b.setTextColor(Color.parseColor("#0b6cfe"));
					b.setBackgroundColor(Color.parseColor("#00000000"));
					b.setText(mTitleList.get(position));
//					holder.textView = b;
//	                convertView.setTag(holder);
//	            } else {
//	                holder = (TitleViewHolder)convertView.getTag();
//	            }
				return b;
			}

			@Override
			public int getCountForHeader(int header) {
				mCurrentHeader = header;
				return mContentMap.get(header).length;
				
			}
		});
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
	
	private int getContentCount(){
		int num = 0;
		for (int i =0; i < mContentMap.size(); i++){
			int childCount = mContentMap.get(i).length;
			num = num + childCount;
		}
		return num;
	}
	
	
	
	
//	private void getUserInfo(){
//		String url = CommonUtil.mUserHost+"services.asmx?op=GetUserInfo";
//		String soapaction = "http://tempuri.org/GetUserInfo";
//		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(soapaction));
//		rpc.addProperty("username", mUsername);
//		mPresent.readyPresentServiceParams(mContext, url, soapaction, rpc);
//		mPresent.startPresentServiceTask();
//		
//	}
//
//	private void showLoadingView(){
//		if (mLoadingView != null) {
//			mLoadingView.setVisibility(View.VISIBLE);
//        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
//        	if (imageView != null) {
//        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
//        		imageView.startAnimation(rotate);
//        	}
//		}
//	}
//	private void dismissLoadingView(){
//		if (mLoadingView != null) {
//			mLoadingView.setVisibility(View.INVISIBLE);
//		}
//	}
	
	@Override
	public void onDestroy() {
		mPoiSearch.destroy();
		super.onDestroy();
	}

	public void  searchNearbyProcess(String text ) {
		Log.i("mingguo", " search near by  lati  "+CommonUtil.mCurrentLati+"  longi  "+CommonUtil.mCurrentLongi+"  text  "+text);
        int radius = 500;
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(text)
        		.sortType(PoiSortType.distance_from_near_to_far).location(new LatLng(CommonUtil.mCurrentLati, CommonUtil.mCurrentLongi))
                .radius(2000).pageCapacity(50);
        mPoiSearch.searchNearby(nearbySearchOption);
        //mPoiSearch.searchPoiDetail(arg0)
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
//			dismissLoadingView();
//			if (infoModel != null){
//				mUserContainer.setVisibility(View.VISIBLE);
//				
//				mUserAddress.setText(infoModel.get("Phone"));
//				mUserId.setText(infoModel.get("LoginName"));
//			}
		}
	};
	
	public static HashMap<String,String> parseUserInfo(String value) {
		HashMap<String,String> userInfo = null;
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				Log.i("house", "parse house info "+array.length());
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
	
	private void logoutUserDialog(final int position){
		new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.user_logout)) 
		  
	     .setMessage(getString(R.string.user_logout_remind))//������ʾ������  
	  
	     .setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {
	        	 SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
					SharedPreferences.Editor editor = sharedata.edit();
				    editor.putString("user_name", "");
				    editor.putString("user_password", "");
				    editor.commit();
				    Intent intent = new Intent(mContext, LoginUserActivity.class);
		            startActivity(intent);    
	        	 
	         }  
	  
	     }).setNegativeButton(getString(R.string.button_cancel),new DialogInterface.OnClickListener() {//��ӷ��ذ�ť  
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�  
	             Log.i("alertdialog"," �뱣�����ݣ�");  
	         }  
	  
	     }).show();
	}
	
	private void changeUserAreaDialog(){
		new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.user_logout)) 
		  
	     .setMessage(getString(R.string.user_change_area_title))//������ʾ������  
	  
	     .setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {
	        	 SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
					SharedPreferences.Editor editor = sharedata.edit();
				    editor.putString("user_name", "");
				    editor.putString("user_password", "");
				    editor.putString("area", "");
				    editor.putString("host", "");
				    editor.commit();
				    Intent intent = new Intent(mContext, LoginUserActivity.class);
		            startActivity(intent);    
		            
	         }  
	  
	     }).setNegativeButton(getString(R.string.button_cancel),new DialogInterface.OnClickListener() {//��ӷ��ذ�ť  
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�  
	             Log.i("alertdialog"," �뱣�����ݣ�");  
	         }  
	  
	     }).show();
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		Log.e("mingguo", "success "+templateInfo);
		Message msgMessage = mHandler.obtainMessage();
		msgMessage.obj = templateInfo;
		msgMessage.sendToTarget();
	}

	@Override
	public void onStatusStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		Log.i("mingguo", "onGetPoiDetailResult    ");
		
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
        	
            Toast.makeText(mContext, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
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
    	   //mDataList 
       }
	}
	
}
