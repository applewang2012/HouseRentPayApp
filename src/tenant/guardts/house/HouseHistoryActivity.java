package tenant.guardts.house;

import java.util.ArrayList;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.adapter.PublishedRecordAdapter;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class HouseHistoryActivity extends BaseActivity implements OnItemClickListener,tenant.guardts.house.adapter.PublishedRecordAdapter.CallBack{

	private Context mContext;
	private ListView mlistView;
	
	private PublishedRecordAdapter mAdapter;
	private ArrayList<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private HoursePresenter mPresent;
	private LinearLayout mContentLayout;
	private TextView mNoContent;
	private String mUserName = null;
	private String mRentHistoryAction = "http://tempuri.org/GetRentOwnerHistory";
	private String mIdCard;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.house_history);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("我的房屋");
		mIdCard = getIntent().getStringExtra("IDCard");
		initView();
		initData();
	}
	
	private void initView(){
		mlistView = (ListView)findViewById(R.id.listview);
		mlistView.setOnItemClickListener(this);
		
	}
	
	
	
	private void initData(){
		mContext = getApplicationContext();
		mPresent = new HoursePresenter(mContext, HouseHistoryActivity.this);
		
		getHouseHistoryData(mIdCard);
	}
	
	private void getHouseHistoryData(String idcard){
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentOwnerHistory";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentHistoryAction));
		rpc.addProperty("idCard", idcard);
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
					infoModel.setHouseCurrentFloor(itemJsonObject.optString("RFloor"));
					infoModel.setHouseTotalFloor(itemJsonObject.optString("RTotalFloor"));
					infoModel.setHouseArea(itemJsonObject.optString("RRentArea"));
					infoModel.setHouseId(itemJsonObject.optString("RentNO"));
					infoModel.setHouseType(itemJsonObject.optString("RoomTypeDesc"));
					infoModel.setHouseDirection(itemJsonObject.optString("RoomDirectoryDesc"));
					boolean isSameHouse = false;
					for (int i = 0; i < mHouseInfoList.size(); i++){
						if (infoModel.getHouseId().equals(mHouseInfoList.get(i).getHouseId())){
							isSameHouse = true;
							break;
						}
					}
					if (!isSameHouse){
						mHouseInfoList.add(infoModel);
					}
				}
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onResume() {
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
			super.handleMessage(msg);
			
			if (msg.what == 100){
				String value=(String) msg.obj;
				getAdapterListData((String)msg.obj);
				Log.e("", mHouseInfoList.size()+"---");
					mAdapter=new PublishedRecordAdapter(mContext,mHouseInfoList,HouseHistoryActivity.this);
					mlistView.setAdapter(mAdapter);
				
			}
		}
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.e("mingguo", "on success  action " + action + "  msg  " + templateInfo);
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
		if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
			Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
			startActivity(new Intent(mContext, LoginUserActivity.class));
		} else {
			
			
			if (mHouseInfoList.get(position).getHouseId() != null && !mHouseInfoList.get(position).getHouseId().equals("")){
				Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
				detailIntent.putExtra("rentNo", mHouseInfoList.get(position).getHouseId());
				startActivity(detailIntent);
			}
		}
	}

	

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.btn_del:
			Toast.makeText(mContext, "del"+v.getTag(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_detail:
			Intent intent = new Intent(HouseHistoryActivity.this, HouseDetailInfoActivity.class);
			intent.putExtra("rentNo", mHouseInfoList.get((Integer)(v.getTag())).getHouseId());
			intent.putExtra("flag", "0");//表示从当前页跳入，详情页按钮会发生改变
			startActivity(intent);
			break;
		case R.id.btn_history:
			Intent intent2 = new Intent(HouseHistoryActivity.this, RentalDetailActivity.class);
			intent2.putExtra("rentNo", mHouseInfoList.get((Integer)(v.getTag())).getHouseId());
			startActivity(intent2);
			break;
			
		default:
			break;
		}
		
	}
	
	

	
	

}
