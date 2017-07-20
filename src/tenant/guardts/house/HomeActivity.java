package tenant.guardts.house;


import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.model.HouseFragment;
import tenant.guardts.house.model.MyFragment;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.Constants;

public class HomeActivity extends BaseActivity {

	private TextView mTitleBar;
	private HoursePresenter mPresenter;
	//private String mLoginAction = "http://tempuri.org/ValidateLogin";
	private String mUpdateAction="http://tempuri.org/CheckUpdate";
	private String mUserInfoAction = "http://tempuri.org/GetUserInfo";;
	private String mUserName, mPassword;
	private HouseFragment mHouseFrament;
	private MyFragment mMyFragment;
	private String mUserInfoString = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.home_layout); 
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText(getResources().getString(R.string.home_tab_house));
		
		mUserName = getIntent().getStringExtra("user_name");
		mPassword = getIntent().getStringExtra("user_password");
		initView();
		getUserInfo();
	}
	
	
	
	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		Button scanButton = (Button)findViewById(R.id.id_scan_rent_house);
		scanButton.setVisibility(View.VISIBLE);
		scanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent openCameraIntent = new Intent(HomeActivity.this,CaptureActivity.class);
				startActivityForResult(openCameraIntent, 1);
//				Intent attributeIntent = new Intent(HomeActivity.this, GetRentAttributeActivity.class);
//				attributeIntent.putExtra("order_id", "170");
//				startActivity(attributeIntent);
				
			}
		});
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		if (mHouseFrament == null){
			mHouseFrament = new HouseFragment(mUserName);
			fragmentTransaction.add(R.id.id_home_content, mHouseFrament);
			fragmentTransaction.commitAllowingStateLoss();
		}else{
			fragmentTransaction.show(mHouseFrament);
			fragmentTransaction.commitAllowingStateLoss();
		}
		
		final LinearLayout houseLayout = (LinearLayout)findViewById(R.id.id_home_button_house);
		final LinearLayout myLayout = (LinearLayout)findViewById(R.id.id_home_button_my);
		final ImageView houseIcon = (ImageView)findViewById(R.id.id_home_button_house_icon);
		final ImageView myIcon = (ImageView)findViewById(R.id.id_home_button_my_icon);
		final TextView houseText = (TextView)findViewById(R.id.id_home_button_house_text);
		final TextView myText = (TextView)findViewById(R.id.id_home_button_my_text);
		houseLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTitleBar.setText(getString(R.string.home_tab_house));
				
				houseIcon.setBackgroundResource(R.drawable.chuzu_icon);
				houseText.setTextColor(Color.parseColor("#0b6cfe"));
				myIcon.setBackgroundResource(R.drawable.my_icon_default);
				myText.setTextColor(Color.parseColor("#afaeae"));
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				hideAllFragments(fragmentTransaction);
				if (mHouseFrament == null){
					mHouseFrament = new HouseFragment(mUserName);
					fragmentTransaction.add(R.id.id_home_content, mHouseFrament);
					fragmentTransaction.commitAllowingStateLoss();
				}else{
					fragmentTransaction.show(mHouseFrament);
					fragmentTransaction.commitAllowingStateLoss();
				}
			}
		});
		
		myLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTitleBar.setText(getString(R.string.home_tab_my));
				houseIcon.setBackgroundResource(R.drawable.chuzu_icon_default);
				houseText.setTextColor(Color.parseColor("#afaeae"));
				myIcon.setBackgroundResource(R.drawable.my_icon);
				myText.setTextColor(Color.parseColor("#0b6cfe"));
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				hideAllFragments(fragmentTransaction);
				if (mMyFragment == null){
					mMyFragment = new MyFragment(mUserName);
					fragmentTransaction.add(R.id.id_home_content, mMyFragment);
					fragmentTransaction.commitAllowingStateLoss();
				}else{
					fragmentTransaction.show(mMyFragment);
					fragmentTransaction.commitAllowingStateLoss();
				}
			}
		});
		
//		final EditText userName = (EditText)findViewById(R.id.id_login_username);
//		final EditText password = (EditText)findViewById(R.id.id_login_password);
//		Button login = (Button)findViewById(R.id.id_login_user_button);
//		login.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				mUserName = userName.getEditableText().toString();
//				mPassword = password.getEditableText().toString();
//				if (mUserName == null || mUserName.equals("")){
//					Toast.makeText(getApplicationContext(), "�û�������Ϊ��", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if (mPassword == null || mPassword.equals("")){
//					Toast.makeText(getApplicationContext(), "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				showLoadingView();
//				loginUser();
//			}
//		});
//		
//		Button registerButton = (Button)findViewById(R.id.id_login_user_register);
//		Button modifyButton = (Button)findViewById(R.id.id_login_user_modify_password);
//		registerButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(HomeActivity.this, RegisterUserActivity.class));
//			}
//		});
//		modifyButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(HomeActivity.this, ModifyPasswordActivity.class));
//			}
//		});
	}
	
	
	
//	private void loginUser(){
//		String url = "http://qxw2332340157.my3w.com/services.asmx?op=ValidateLogin";
//		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mLoginAction));
//		rpc.addProperty("username", mUserName);
//		rpc.addProperty("password", mPassword);
//		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mLoginAction, rpc);
//		mPresenter.startPresentServiceTask();
//	}
	
	private void getUserInfo(){
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetUserInfo";
		SoapObject rpc = new SoapObject(Constants.NAMESPACE, Constants.getSoapName(mUserInfoAction));
		rpc.addProperty("username", mUserName);
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mUserInfoAction, rpc);
		mPresenter.startPresentServiceTask();
		
	}
	
	private void hideAllFragments(FragmentTransaction transaction) {
		if (mHouseFrament != null && !mHouseFrament.isHidden()) {
			transaction.hide(mHouseFrament);
		}
		if (mMyFragment != null && !mMyFragment.isHidden()) {
			transaction.hide(mMyFragment);
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				if (msg.obj != null){
					parseUserInfo((String)msg.obj);
				}
			}else if (msg.what == 101){
				
				Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private static void parseUserInfo(String value) {
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				Log.i("house", "parse house info "+array.length());
				//for (int item = 0; item < array.length(); item++){
					
					JSONObject itemJsonObject = array.optJSONObject(0);
//					userInfo = new HashMap<>();
//					userInfo.put("NickName", itemJsonObject.optString("NickName"));
//					userInfo.put("LoginName", itemJsonObject.optString("LoginName"));
//					userInfo.put("Address", itemJsonObject.optString("Address"));
//					userInfo.put("IDCard", itemJsonObject.optString("IDCard"));
					Constants.mRegisterName = itemJsonObject.optString("RealName");
					Constants.mRegisterIdcard = itemJsonObject.optString("IDCard");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private  String getUserIdNo(String value) {
		HashMap<String,String> userInfo = null;
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				Log.i("house", "parse house info "+array.length());
					JSONObject itemJsonObject = array.optJSONObject(0);
					return itemJsonObject.optString("IDCard");
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	 private long exitTime;
	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
					if ((System.currentTimeMillis() - exitTime) > 2000) {
						Toast.makeText(getApplicationContext(), getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
			            exitTime = System.currentTimeMillis();
			        } else {
			            finish();
			            System.exit(0);
			        }
					return false;
				
			}
			return super.onKeyDown(keyCode, event);
		}
	 
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			//处理扫描结果（在界面上显示）
					if (resultCode == RESULT_OK  && requestCode == 1) {
						Bundle bundle = data.getExtras();
						String scanResult = bundle.getString("result");
						Log.e("mingguo", "scan  result  "+scanResult);
						if (!TextUtils.isEmpty(scanResult)){
							Intent attributeIntent = new Intent(HomeActivity.this, GetRentAttributeActivity.class);
							attributeIntent.putExtra("order_id", scanResult);
							startActivity(attributeIntent);
						}
					}
		}

	@Override
	public void onStatusStart() {
		
		
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		if (action != null && templateInfo != null){}
			if (action.equals(mUserInfoAction)){
				Message message = mHandler.obtainMessage();
				message.what = 100;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
	}
	
	


}
