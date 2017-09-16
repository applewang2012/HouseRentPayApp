package tenant.guardts.house;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;

public class RegisterUserStep2Activity extends BaseActivity{

	private TextView mTitleBar;
	
	private HoursePresenter mPresenter;
	private String mValidAction = "http://tempuri.org/ValidateLoginName";
	private String mSendVerifyCodeAction = "http://tempuri.org/SendIdentifyCodeMsg";
	private String mCheckVerifyCodeAction = "http://tempuri.org/ValidateIdentifyCode";
	private String mUserName, mPhone, mVerifyCode,mPassword, mPasswordIndentify;
	private TextView mVerifyCodeText;
	private int mTimeCount = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.register_user_step2_layout); 
		ActivityController.addActivity(RegisterUserStep2Activity.this);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("手机认证");
		//mUserName = getIntent().getStringExtra("username");
		//mPassword = getIntent().getStringExtra("password");
//		initHandler();
		initView();
	}
	
	

	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		final EditText inputPhone = (EditText)findViewById(R.id.id_register_step2_input_phone);
		final EditText inputVerify = (EditText)findViewById(R.id.id_register_step2_input_verifycode);
		final EditText password = (EditText)findViewById(R.id.id_register_step1_input_password);
		final EditText passowrdInditfy = (EditText)findViewById(R.id.id_register_step1_input_password_identify);
		mVerifyCodeText = (TextView)findViewById(R.id.id_register_step2_get_verifycode);
		mVerifyCodeText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPhone = inputPhone.getEditableText().toString();
				if (mPhone == null || mPhone.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.phone_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}else if (mPhone.length() < 11){
					GlobalUtil.shortToast(getApplication(), getString(R.string.phone_input_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				Log.w("mingguo", "register step 2  phone  "+mPhone);
				if (mTimeCount < 0){
					mTimeCount = 60;
					sendPhoneVerifyCode(mPhone);
					mHandler.sendEmptyMessage(1000);
				}else{
					return;
				}
				
			}
		});
		Button nextButton = (Button)findViewById(R.id.id_register_user_step2_next);
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPhone = inputPhone.getEditableText().toString();
				mVerifyCode = inputVerify.getEditableText().toString();
				mPassword = password.getEditableText().toString();
				mPasswordIndentify = passowrdInditfy.getEditableText().toString();
				
				if (mPhone == null || mPhone.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.phone_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}else if (mPhone.length() < 11){
					GlobalUtil.shortToast(getApplication(), getString(R.string.phone_input_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (mVerifyCode == null || mVerifyCode.equals("") ){
					GlobalUtil.shortToast(getApplication(), getString(R.string.verify_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}else if (mVerifyCode.length() != 6){
					GlobalUtil.shortToast(getApplication(), getString(R.string.verify_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				
				if (mPassword == null || mPassword.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.pwd_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}else  if (mPassword.length() < 6){
					GlobalUtil.shortToast(getApplication(), getString(R.string.pwd_input_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (mPasswordIndentify == null || mPasswordIndentify.equals("") || !mPassword.equals(mPasswordIndentify)){
					GlobalUtil.shortToast(getApplication(), getString(R.string.pwd_again_not_same), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				
				mUserName = mPhone;
				if (mUserName != null && mUserName.length() > 0){
					
					checkUserNameValid(mUserName);
				}
				
			}
		});
		
		
		
		
	}
	
	private void checkUserNameValid(String username){
		String url = CommonUtil.mUserHost+"services.asmx?op=ValidateLoginName";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mValidAction));
		rpc.addProperty("loginName", username); 
		mPresenter.readyPresentServiceParams(this, url, mValidAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("mingguo", "onActivityResult resultCode  "+resultCode+" requestCode  "+requestCode+"  file  ");
		if (resultCode == RESULT_OK && requestCode == 1) {
			 Log.w("mingguo", "activity result  width data   "+data);
		}
			
	}

	
	
	private void sendPhoneVerifyCode(String phone){
		String url = "http://www.guardts.com/COMMONSERVICE/COMMONSERVICES.ASMX?op=SendIdentifyCodeMsg";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mSendVerifyCodeAction));
		rpc.addProperty("phone", phone); 
		mPresenter.readyPresentServiceParams(this, url, mSendVerifyCodeAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private void checkPhoneVerifyCode(String phone, String code){
		String url = "http://www.guardts.com/COMMONSERVICE/COMMONSERVICES.ASMX?op=ValidateIdentifyCode";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCheckVerifyCodeAction));
		rpc.addProperty("phone", phone); 
		rpc.addProperty("number", code); 
		mPresenter.readyPresentServiceParams(this, url, mCheckVerifyCodeAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if (msg.what == 100){
				GlobalUtil.shortToast(getApplication(), getString(R.string.username_register_again), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}else if (msg.what == 200){
				
				checkPhoneVerifyCode(mPhone, mVerifyCode);
			}else if (msg.what == 102){
				
				if (msg.obj != null){
					JSONObject json;
					try {
						json = new JSONObject((String)msg.obj);
						String ret = json.optString("ret");
//						if (ret != null){
//							if (ret.equals("0")){
								Intent nextIntent = new Intent(RegisterUserStep2Activity.this, RegisterUserStep3Activity.class);
								nextIntent.putExtra("phone", mPhone);
								nextIntent.putExtra("user_name", mPhone);
								nextIntent.putExtra("user_password", mPasswordIndentify);
								startActivity(nextIntent);
//							}else{
//								GlobalUtil.shortToast(getApplication(), getString(R.string.verify_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
//							}
//						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}else if (msg.what == 105){
				GlobalUtil.shortToast(getApplication(), getString(R.string.register_success), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
				Intent intent = new Intent(RegisterUserStep2Activity.this, HomeActivity.class);
				
				startActivity(intent);
				finish();
			}else if (msg.what == 102){
				
				
				
			}else if (msg.what == 200){
				
			}else if (msg.what == 1000){
				if (mTimeCount >= 0){
					mVerifyCodeText.setTextColor(Color.parseColor("#cccccc"));
					mVerifyCodeText.setText(mTimeCount +" 秒");
					mTimeCount--;
					mHandler.sendEmptyMessageDelayed(1000, 1000);
				}else{
					mVerifyCodeText.setTextColor(Color.parseColor("#337ffd"));
					mVerifyCodeText.setText("获取验证码");
				}
				
				
			}
			
		}
		
	};
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}


	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
		mHandler.sendEmptyMessage(200);
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mCheckVerifyCodeAction)){
				Message message = mHandler.obtainMessage();
				message.what = 102;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}else if (action.equals(mSendVerifyCodeAction)){
				
			}else if (action.equals(mValidAction)){
				Log.i("mingguo", "on success  action valid ");
				if (templateInfo.equals("false")){
					mHandler.sendEmptyMessage(100);
				}else{
					mHandler.sendEmptyMessage(200);
				}
			}
		}
	}

}
