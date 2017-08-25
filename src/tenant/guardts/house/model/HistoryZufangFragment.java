package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.baidu.location.Poi;

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
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import tenant.guardts.house.HouseDetailInfoActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class HistoryZufangFragment extends Fragment implements DataStatusInterface, OnItemClickListener{
	

	
	private Context mContext;
	private View mRootView;
	private ListView mlistView;
	private View mLoadingView;
	private UniversalAdapter mAdapter;
	private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private HoursePresenter mPresent;
	//private LinearLayout mContentLayout;
	private TextView mNoContent;
	private String mUserName = null;
	private String mRentHistoryAction = "http://tempuri.org/GetRentHistory";
	//private String mIdCard;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mPresent = new HoursePresenter(mContext, HistoryZufangFragment.this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.house_history_zufang_fragment, container, false);
		initView();
		initData();
		return mRootView;
	}
	
	private void initView(){
		mlistView = (ListView)mRootView.findViewById(R.id.id_fragment_house_listview);
		mNoContent = (TextView)mRootView.findViewById(R.id.id_frament_house_no_cotent);
		mLoadingView = (View)mRootView.findViewById(R.id.id_data_loading);
		//mContentLayout.setVisibility(View.INVISIBLE);
		initAdapter();
		mlistView.setAdapter(mAdapter);
		mlistView.setOnItemClickListener(this);
	}


	private void initAdapter(){
		mAdapter = new UniversalAdapter<HouseInfoModel>(mContext, R.layout.house_fragment_zufang_list_item, mHouseInfoList) {

			@Override
			public void convert(UniversalViewHolder holder, HouseInfoModel info) {
				View holderView = holder.getConvertView();
				TextView addressText = (TextView)holderView.findViewById(R.id.id_history_address);
				TextView status = (TextView)holderView.findViewById(R.id.id_zufang_item_status);
				TextView contactText = (TextView)holderView.findViewById(R.id.id_order_end_time);
				TextView timeText = (TextView)holderView.findViewById(R.id.id_order_monkey_input);
				Button button1 = (Button)holderView.findViewById(R.id.id_order_button_status1);
				Button button2 = (Button)holderView.findViewById(R.id.id_order_button_status2);
				Button button3 = (Button)holderView.findViewById(R.id.id_order_button_status3);
				addressText.setText(info.getHouseAddress());
				//areaText.setText(info.getHouseArea()+" 平米");
				//contactText.setText(info.getHouseOwnerName()+" "+info.getHousePhone());
				//timeText.setText(info.getHouseStartTime()+"至"+info.getHouseEndTime());
				if (holder.getPosition() == 0){
					status.setText("待确认");
					status.setTextColor(Color.parseColor("#de6262"));
					button1.setText("查看详情");
					button1.setVisibility(View.INVISIBLE);
					button2.setTextColor(Color.parseColor("#337ffd"));
					button2.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
					button2.setText("查看详情");
					button3.setText("取消订单");
				}else if (holder.getPosition() == 1){
					status.setText("待支付");
					status.setTextColor(Color.parseColor("#de6262"));
					button2.setText("立即付款");
					button2.setTextColor(Color.parseColor("#337ffd"));
					button2.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
					button3.setText("取消订单");
				}else if (holder.getPosition() == 2){
					status.setText("已支付");
					status.setTextColor(Color.parseColor("#de6262"));
					button1.setText("查看详情");
					button1.setVisibility(View.INVISIBLE);
					button2.setText("查看详情");
					button3.setText("取消订单");
					button2.setTextColor(Color.parseColor("#337ffd"));
					button2.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
				}else if (holder.getPosition() == 3){
					status.setText("待评价");
					status.setTextColor(Color.parseColor("#8be487"));
					button1.setText("查看详情");
					button1.setVisibility(View.INVISIBLE);
					button2.setText("查看详情");
					button3.setText("立即评价");
					button3.setTextColor(Color.parseColor("#337ffd"));
					button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
				}
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
					mNoContent.setText("暂无租房历史");
					mNoContent.setVisibility(View.VISIBLE);
				}else{
					//mContentLayout.setVisibility(View.VISIBLE);
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
