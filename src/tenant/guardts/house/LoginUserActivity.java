package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;

public class LoginUserActivity extends BaseActivity{

	private TextView mTitleBar;
//	
	private HoursePresenter mPresenter;
	private String mLoginAction = "http://tempuri.org/ValidateLogin";
	private String mCommonServiceAction = "http://tempuri.org/GetAreas";
	private String mUserName, mPassword;
	private EditText userNameEditText;
	private EditText passwordEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.login_user); 
		ActivityController.addActivity(LoginUserActivity.this);
		initView();
		commonServiceInterface();
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initView(){
		userAgreement = (TextView) findViewById(R.id.tv_user_agreement);//用户协议
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		//
		
		userNameEditText = (EditText)findViewById(R.id.id_login_username);
		passwordEditText = (EditText)findViewById(R.id.id_login_password);
		mUserName = getIntent().getStringExtra("user_name");
		mPassword = getIntent().getStringExtra("user_password");
		if (mUserName != null && !mUserName.equals("")){
			userNameEditText.setText(mUserName);
		}
		if (mPassword != null && !mPassword.equals("")){
			passwordEditText.setText(mPassword);
		}
		
		userAgreement.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(LoginUserActivity.this, UserAgreementActivity.class));
				Intent loadIntent = new Intent(LoginUserActivity.this, LoadUrlTestActivity.class);
				loadIntent.putExtra("url", "http://mg.weiyuzhu.cn/");
				loadIntent.putExtra("tab_name", "用户服务协议");
				startActivity(loadIntent);
			}
		});
		Button login = (Button)findViewById(R.id.id_login_user_button);
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mUserName = userNameEditText.getEditableText().toString();
				mPassword = passwordEditText.getEditableText().toString();
				if (mUserName == null || mUserName.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.please_input_username), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (mPassword == null || mPassword.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.please_input_username), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (CommonUtil.mUserHost == null || CommonUtil.mUserHost.equals("")){
					GlobalUtil.shortToast(getApplication(), "您尚未选择所在区域", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				
				loginUser();
			}
		});
		
		Button registerButton = (Button)findViewById(R.id.id_login_user_register);
		Button modifyButton = (Button)findViewById(R.id.id_login_user_modify_password);
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserHost == null || CommonUtil.mUserHost.equals("")){
					GlobalUtil.shortToast(getApplication(), "您尚未选择所在区域", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				
				startActivity(new Intent(LoginUserActivity.this, RegisterUserStep2Activity.class));
			}
		});
		modifyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserHost == null || CommonUtil.mUserHost.equals("")){
					GlobalUtil.shortToast(getApplication(), "您尚未选择所在区域", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				startActivityForResult(new Intent(LoginUserActivity.this, ModifyPasswordActivity.class), 1);
			}
		});
	}
	
	private void loginUser(){
		String url = CommonUtil.mUserHost+"services.asmx?op=ValidateLogin";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mLoginAction));
		rpc.addProperty("username", mUserName);
		rpc.addProperty("password", mPassword);
		rpc.addProperty("userType", "0");
		mPresenter.readyPresentServiceParams(this, url, mLoginAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private void commonServiceInterface(){
		SharedPreferences sharedata = getApplication().getSharedPreferences("user_info", 0);
	    String host = sharedata.getString("host", "");
	    Log.i("mingguo", "common service preference host  "+host);
	    if (host == null || host.equals("")){
	    	
	    	String url = "http://www.guardts.com/commonservice/commonservices.asmx?op=GetAreas";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCommonServiceAction));
			rpc.addProperty("status", "1");
			mPresenter.readyPresentServiceParams(this, url, mCommonServiceAction, rpc);
			mPresenter.startPresentServiceTask(true);
	    }else{
	    	CommonUtil.mUserHost = host;
	    }
		
	}
	
	private void showLoadingView(){
//		if (mLoadingView != null) {
//			mLoadingView.setVisibility(View.VISIBLE);
//        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
//        	if (imageView != null) {
//        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
//        		imageView.startAnimation(rotate);
//        	}
//		}
	}
	private void dismissLoadingView(){
//		if (mLoadingView != null) {
//			
//		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1){
			if (data != null){
				mPassword = data.getStringExtra("new_password");
				passwordEditText.setText(mPassword);
				Log.i("mingguo", "login activity  onActivityResult password  "+mPassword);
			}
		}
	}
	
	private void showSelectAlertDialog(final String title, final List<String[]> data) {  
		  AlertDialog.Builder builder =new AlertDialog.Builder(LoginUserActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle(title);
		  builder.setIcon(android.R.drawable.ic_dialog_info);
		  builder.setItems(data.get(0), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences sharedata = getApplication().getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
			    editor.putString("area", data.get(0)[which]);
			    editor.putString("host", data.get(1)[which]);
			    editor.commit();
			    CommonUtil.mUserArea = data.get(0)[which];
			    CommonUtil.mUserHost = data.get(1)[which];
			    Log.w("mingguo", "CommonUtil.mUserArea  "+CommonUtil.mUserArea+"  CommonUtil.mUserHost  "+CommonUtil.mUserHost);
			}
		});
		builder.setCancelable(false);
		builder.show();
		
	}
	
	public static List<String[]> parseCommonService(String value) {
		String [] areaName = null;
		String [] areaHost;
		List<String[]> list = new ArrayList<>();
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				areaName = new String[array.length()];
				areaHost = new String[array.length()];
				for (int item = 0; item < array.length(); item++){
					JSONObject itemJsonObject = array.optJSONObject(item);
					areaName[item] = itemJsonObject.optString("AreaName");
					areaHost[item] = itemJsonObject.optString("RentHost");
				}
				list.add(areaName);
				list.add(areaHost);
			}
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}



	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				
				SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
			    editor.putString("user_name", mUserName);
			    editor.putString("user_password", mPassword);
			    editor.commit();
				GlobalUtil.shortToast(getApplication(), getString(R.string.login_success), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
				Intent intent = new Intent(LoginUserActivity.this, HomeActivity.class);
				intent.putExtra("user_name", mUserName);
				intent.putExtra("user_password", mPassword);
				startActivity(intent);
//				startActivity(new Intent(LoginUserActivity.this, HomeActivity.class));
				finish();
			}else if (msg.what == 101){
				
				GlobalUtil.shortToast(getApplication(), getString(R.string.login_failed), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}else if (msg.what == 110){
				
				showSelectAlertDialog("请选择所在区域", parseCommonService((String)msg.obj));
			}
		}
	};
	private TextView userAgreement;
	


	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
//				if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE){
//					
//					return false;
//				}
			}
			return super.onKeyDown(keyCode, event);
		}


	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		super.onStatusSuccess(action, templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mLoginAction)){
				if (templateInfo.equals("false")){
					mHandler.sendEmptyMessage(101);
				}else if (templateInfo.equals("true")){
					mHandler.sendEmptyMessage(100);
				}
			}else if (action.equals(mCommonServiceAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 110;
				msg.obj = templateInfo;
				mHandler.sendMessage(msg);
			}
		}
		
	}
	

}
