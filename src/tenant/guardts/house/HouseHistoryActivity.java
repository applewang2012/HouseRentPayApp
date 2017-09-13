package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class HouseHistoryActivity extends BaseActivity implements OnItemClickListener{

	private Context mContext;
	private ListView mlistView;
	
	private UniversalAdapter mAdapter;
	private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private HoursePresenter mPresent;
	private LinearLayout mContentLayout;
	private TextView mNoContent;
	private String mUserName = null;
	private String mRentHistoryAction = "http://tempuri.org/GetRentHistory";
	private String mIdCard;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.house_history_layout);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("房屋记录");
		mIdCard = getIntent().getStringExtra("idcard");
		Log.i("mingguo", "house history  idcard  "+mIdCard);
		initView();
		initData();
	}
	
	private void initView(){
		mlistView = (ListView)findViewById(R.id.id_fragment_house_listview);
		mContentLayout = (LinearLayout)findViewById(R.id.id_frament_house_cotent);
		mNoContent = (TextView)findViewById(R.id.id_frament_house_no_cotent);
		
		mContentLayout.setVisibility(View.INVISIBLE);
		initAdapter();
		mlistView.setAdapter(mAdapter);
		mlistView.setOnItemClickListener(this);
		
	}
	
	private void initAdapter(){
		mAdapter = new UniversalAdapter<HouseInfoModel>(getApplicationContext(), R.layout.house_fragment_zufang_list_item, mHouseInfoList) {

			@Override
			public void convert(UniversalViewHolder holder, HouseInfoModel info) {
				View holderView = holder.getConvertView();
				TextView addressText = (TextView)holderView.findViewById(R.id.id_history_address);
				//TextView areaText = (TextView)holderView.findViewById(R.id.id_history_area);
				TextView contactText = (TextView)holderView.findViewById(R.id.id_order_end_time);
				TextView timeText = (TextView)holderView.findViewById(R.id.id_order_monkey_input);
				addressText.setText(info.getHouseAddress());
				//areaText.setText(info.getHouseArea()+" 平米");
				contactText.setText(info.getHouseOwnerName()+" "+info.getHouseOwnerPhone());
				timeText.setText(info.getHouseStartTime()+"至"+info.getHouseEndTime());
			}
		};
	}
	
	private void initData(){
		mContext = getApplicationContext();
		mPresent = new HoursePresenter(mContext, HouseHistoryActivity.this);
		
		getHouseHistoryData();
	}
	
	private void getHouseHistoryData(){
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentHistory";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentHistoryAction));
		rpc.addProperty("idCard", mIdCard);
		mPresent.readyPresentServiceParams(this, url, mRentHistoryAction, rpc);
		mPresent.startPresentServiceTask(true);
	}
	
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
					infoModel.setHouseOwnerName(itemJsonObject.optString("ROwnerTel"));
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		mHouseNo = getIntent().getStringExtra("house_id");
//		mUsername = getIntent().getStringExtra("user_name");
//		mOwnerName = getIntent().getStringExtra("owner_name");
//		mOwnerIdcard = getIntent().getStringExtra("owner_id");
//		mHouseId.setText(mHouseNo);
	}
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
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

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mRentHistoryAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mHouseInfoList.get(position).getHouseId() != null && !mHouseInfoList.get(position).getHouseId().equals("")){
			Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
			detailIntent.putExtra("rentNo", mHouseInfoList.get(position).getHouseId());
			startActivity(detailIntent);
		}
		
	}
	
	

	
	

}
