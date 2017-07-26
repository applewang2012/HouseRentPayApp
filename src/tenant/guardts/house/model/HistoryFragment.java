package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import tenant.guardts.house.HouseDetailInfoActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class HistoryFragment extends Fragment implements DataStatusInterface, OnItemClickListener{
	

	
	private Context mContext;
	private View mRootView;
	private ListView mlistView;
	private View mLoadingView;
	private UniversalAdapter mAdapter;
	private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private HoursePresenter mPresent;
	private LinearLayout mContentLayout;
	private TextView mNoContent;
	private String mUserName = null;
	private String mRentHistoryAction = "http://tempuri.org/GetRentHistory";
	//private String mIdCard;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mPresent = new HoursePresenter(mContext, HistoryFragment.this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.house_history_layout, container, false);
		initView();
		initData();
		return mRootView;
	}
	
	private void initView(){
		mlistView = (ListView)mRootView.findViewById(R.id.id_fragment_house_listview);
		mContentLayout = (LinearLayout)mRootView.findViewById(R.id.id_frament_house_cotent);
		mNoContent = (TextView)mRootView.findViewById(R.id.id_frament_house_no_cotent);
		mLoadingView = (View)mRootView.findViewById(R.id.id_data_loading);
		mContentLayout.setVisibility(View.INVISIBLE);
		initAdapter();
		mlistView.setAdapter(mAdapter);
		mlistView.setOnItemClickListener(this);
	}
	
	private void initAdapter(){
		mAdapter = new UniversalAdapter<HouseInfoModel>(mContext, R.layout.house_fragment_list_item, mHouseInfoList) {

			@Override
			public void convert(UniversalViewHolder holder, HouseInfoModel info) {
				View holderView = holder.getConvertView();
				TextView addressText = (TextView)holderView.findViewById(R.id.id_history_address);
				//TextView areaText = (TextView)holderView.findViewById(R.id.id_history_area);
				TextView contactText = (TextView)holderView.findViewById(R.id.id_history_contacts);
				TextView timeText = (TextView)holderView.findViewById(R.id.id_history_time);
				addressText.setText(info.getHouseAddress());
				//areaText.setText(info.getHouseArea()+" 平米");
				contactText.setText(info.getHouseOwnerName()+" "+info.getHousePhone());
				timeText.setText(info.getHouseStartTime()+"至"+info.getHouseEndTime());
			}
		};
	}
	
	private void initData(){
		showLoadingView();
		getHouseHistoryData();
	}
	
	private void getHouseHistoryData(){
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentHistory";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentHistoryAction));
		rpc.addProperty("idCard", CommonUtil.mRegisterIdcard);
		mPresent.readyPresentServiceParams(mContext, url, mRentHistoryAction, rpc);
		mPresent.startPresentServiceTask();
	}

	private void showLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			dismissLoadingView();
			if (msg.what == 100){
				getAdapterListData((String)msg.obj);
				if (mHouseInfoList.size() == 0){
					mContentLayout.setVisibility(View.GONE);
					mNoContent.setVisibility(View.VISIBLE);
				}else{
					mContentLayout.setVisibility(View.VISIBLE);
					mNoContent.setVisibility(View.INVISIBLE);
					Log.w("housefragment", "house list  "+mHouseInfoList.size());
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	};
	
	private void getAdapterListData(String value){
		if (value == null){
			return;
		}
		mHouseInfoList.clear();
		JSONArray array;
		try {
			array = new JSONArray(value);
			if (array != null){
				Log.i("house", "parse house info "+array.length());
				for (int item = 0; item < array.length(); item++){
					JSONObject itemJsonObject = array.optJSONObject(item);
					HouseInfoModel infoModel = new HouseInfoModel();
					infoModel.setHouseAddress(itemJsonObject.optString("RAddress"));
					infoModel.setHouseArea(itemJsonObject.optString("RRentArea"));
					infoModel.setHouseOwnerName(itemJsonObject.optString("ROwner"));
					infoModel.setHousePhone(itemJsonObject.optString("ROwnerTel"));
					infoModel.setHouseStartTime(itemJsonObject.optString("StartDate"));
					infoModel.setHouseEndTime(itemJsonObject.optString("EndDate"));
					infoModel.setHouseId(itemJsonObject.optString("RentNO"));
					mHouseInfoList.add(infoModel);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		Log.e("mingguo", "success "+templateInfo);
		if (action.equals(mRentHistoryAction)){
			Message msg = mHandler.obtainMessage();
			msg.what = 100;
			msg.obj = templateInfo;
			msg.sendToTarget();
		}
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
		detailIntent.putExtra("rentNo", mHouseInfoList.get(position).getHouseId());
		startActivity(detailIntent);
	}
	
}
