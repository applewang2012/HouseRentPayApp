package tenant.guardts.house;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.gzt.faceid5sdk.DetectionAuthentic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.Constants;

public class HouseDetailInfoActivity extends BaseActivity{

	private TextView mTitleBar;
	private View mLoadingView;
	private HoursePresenter mPresenter;
	private String mAddRentAction = "http://tempuri.org/AddRentRecord";
	private String mIdentifyUrl = "https://nid.sdtt.com.cn/AppRegSvr/thirdsysauthsvr/houseorder";
	private String mAppIDString = "0000004";
	private String mRandNum = null;
	//ret  1 desc SDT-HOUSE-3435333134303230313730333233313531373331343839
	private String mQueryStatusUrl = null;
//	private Map<String, String> mSelectedMap = new HashMap<>();
//	private Map<String, String> mOriginText = new HashMap<>();
//	private Map<String, String[]> mAllList = new HashMap<>();
	private String mHouseNo;
	private String mUsername;
	private String mOriginTypeText, mTypeIndex = null;
	private String mOwnerName;
	private String mOwnerIdcard;
	private String mHouseDetailAction = "http://tempuri.org/GetHouseDetailInfo";
	private String mRentNo = "";
	private String mfilePath;
	private HandlerThread myHandlerThread ;
	private Handler mSubHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.house_detail_info_layout);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("房屋详情");
		initView();
		mRentNo = getIntent().getStringExtra("rentNo");
		Log.e("mingguo", "rent no  "+mRentNo);
		getHouseDetailInfoByHouseId(mRentNo);
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

	
	private void showAlertDialog(final TextView text,final String[] items) {  
		  AlertDialog.Builder builder =new AlertDialog.Builder(HouseDetailInfoActivity.this);
		  builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTypeIndex = which+"";
				text.setText(mOriginTypeText +"   "+items[which]);
			}
		});
		builder.show();
}

	
	private TextView mHouseId;
	private TextView mRentName;
	private TextView mRentProperty;
	private TextView mRentReadMe;
	private TextView mRentPhone;
	private TextView mRentOrderId;
	private DetectionAuthentic authentic;
	private TextView mRentArea;
	private TextView mRentType;
	private TextView mRentDirection;
	private TextView mRentFloor;
	private TextView mRentAddress;
	private TextView mRentStatus;
	
	
	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);
		
		
		mHouseId = (TextView)findViewById(R.id.id_rent_house_number);
		mRentArea = (TextView)findViewById(R.id.id_rent_house_area);
		mRentName = (TextView)findViewById(R.id.id_rent_house_name);
		mRentPhone = (TextView)findViewById(R.id.id_rent_house_phone);
		mRentProperty = (TextView)findViewById(R.id.id_rent_house_property);
		mRentType = (TextView)findViewById(R.id.id_rent_house_type);
		mRentDirection = (TextView)findViewById(R.id.id_rent_house_direction);
		mRentFloor = (TextView)findViewById(R.id.id_rent_house_floor);
		mRentAddress = (TextView)findViewById(R.id.id_rent_house_address);
		mRentStatus = (TextView)findViewById(R.id.id_rent_house_status);
	}
	
	
	
	
	

	private void getHouseDetailInfoByHouseId(String rentNo){
		showLoadingView();
		mLoadingView.setVisibility(View.VISIBLE);
		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=GetHouseDetailInfo";
		SoapObject rpc = new SoapObject(Constants.NAMESPACE, Constants.getSoapName(mHouseDetailAction));
		rpc.addProperty("rentNo", rentNo); 
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mHouseDetailAction, rpc);
		mPresenter.startPresentServiceTask();
	}
	

	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.i("mingguo", "on success  action handle  message  ");
			if (msg.what == 100){
				dismissLoadingView();
				jsonHouseInfoToView((String)msg.obj);
			}else if (msg.what == 101){
			
				
			}
		}
		
	};
	
	private void jsonHouseInfoToView(String value){
		if (value != null){
			JSONArray array;
			try {
				array = new JSONArray(value);
				JSONObject object = array.optJSONObject(0);
				Log.i("mingguo", "json house info  "+object);
				if (object != null){
					Log.i("mingguo", "json house info  "+object);
					mHouseId.setText(object.getString("RentNO"));
					Log.i("mingguo", "json house info  "+object.getString("RentNO"));
					mRentName.setText(object.getString("ROwner"));
					mRentPhone.setText(object.getString("ROwnerTel"));
					mRentArea.setText(object.getString("RRentArea")+" 平米");
					mRentProperty.setText(object.getString("RPropertyDesc"));
					mRentType.setText(object.getString("RRoomTypeDesc"));
					mRentDirection.setText(object.getString("RDirectionDesc"));
					mRentFloor.setText(object.getString("RFloor")+"/"+object.getString("RTotalFloor")+"层");
					mRentAddress.setText(object.getString("RAddress"));
					mRentStatus.setText(object.getString("IsAvailable"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	private void showLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}

	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE){
					mLoadingView.setVisibility(View.INVISIBLE);
					return false;
				}
			}
			return super.onKeyDown(keyCode, event);
		}

	@Override
	public void onStatusStart() {
		
		
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mHouseDetailAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
//			}else if (action.equals(mConfirmRentAttribute)){
//				mHandler.sendEmptyMessageDelayed(101, 10);
//			}else if (action.equals(mCompleteRentAttribute)){
//				mHandler.sendEmptyMessageDelayed(102, 1000);
//			}else if (action.equals(mIdentifyAction)){
//				Message message = mHandler.obtainMessage();
//				message.what = 103;
//				message.obj = templateInfo;
//				mHandler.sendMessage(message);
			}
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeMessages(100);
	}
	
	

}
