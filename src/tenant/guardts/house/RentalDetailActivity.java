package tenant.guardts.house;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import tenant.guardts.house.adapter.RentalDetailAdapter;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.RentRecord;
import tenant.guardts.house.model.RentalDetail;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RentalDetailActivity extends BaseActivity implements DataStatusInterface{
	private String mCanRentHouseListAction = "http://tempuri.org/CanRentTheHouse";
	private HoursePresenter mPresenter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_rental_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("租客历史");
		initView();
		
	}
	Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==100){
				try {
					String value=(String)msg.obj;
					JSONObject object = new JSONObject((String)msg.obj);
					if (object != null){
						String ret = object.optString("ret");
						if (ret != null){
							if (ret.equals("0")){
								
								
							}else if (ret.equals("1")){
								Gson gson=new Gson();
								RentalDetail detail = gson.fromJson(value, RentalDetail.class);
								ArrayList<RentRecord> list=gson.fromJson(detail.rentRecord,new TypeToken<ArrayList<RentRecord>>(){}.getType());
								LogUtil.e("", list.size()+"==");
								
								RentalDetailAdapter adapter = new RentalDetailAdapter(RentalDetailActivity.this, list);
								mListView.setAdapter(adapter);
							}
						}
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	};
	private String rentNO;
	private ExpandableListView mListView;
	
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mCanRentHouseListAction)){
				Message message = mHandler.obtainMessage();
				message.what = 100;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}

	private void initView() {
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		rentNO = getIntent().getStringExtra("rentNo");
		mListView = (ExpandableListView) findViewById(R.id.expandable_lv);
		checkCanRentHouseTime(rentNO);
		
	}

	private void checkCanRentHouseTime(String houseno){
		String url = CommonUtil.mUserHost+"Services.asmx?op=CanRentTheHouse";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCanRentHouseListAction));
		rpc.addProperty("rentNo", houseno); 
		rpc.addProperty("startdate", "2017-09-01 00:00:00"); 
		rpc.addProperty("enddate", "2020-09-01 00:00:00"); 
		mPresenter.readyPresentServiceParams(this, url, mCanRentHouseListAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
}
