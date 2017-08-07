package tenant.guardts.house;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.gzt.faceid5sdk.DetectionAuthentic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

public class RegisterUserStep2Activity extends BaseActivity{

	private TextView mTitleBar;
	private View mLoadingView;
	private HoursePresenter mPresenter;
	private String mSendVerifyCodeAction = "http://tempuri.org/SendIdentifyCodeMsg";
	private String mCheckVerifyCodeAction = "http://tempuri.org/ValidateIdentifyCode";
	private String mUserName, mPassword, mPhone, mVerifyCode;
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
		mTitleBar.setText("手机绑定");
		mUserName = getIntent().getStringExtra("username");
		mPassword = getIntent().getStringExtra("password");
//		initHandler();
		initView();
	}
	
	

	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		final EditText inputPhone = (EditText)findViewById(R.id.id_register_step2_input_phone);
		final EditText inputVerify = (EditText)findViewById(R.id.id_register_step2_input_verifycode);
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
				showLoadingView();
				checkPhoneVerifyCode(mPhone, mVerifyCode);
			}
		});
		
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);
		
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
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mSendVerifyCodeAction, rpc);
		mPresenter.startPresentServiceTask();
	}
	
	private void checkPhoneVerifyCode(String phone, String code){
		String url = "http://www.guardts.com/COMMONSERVICE/COMMONSERVICES.ASMX?op=ValidateIdentifyCode";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCheckVerifyCodeAction));
		rpc.addProperty("phone", phone); 
		rpc.addProperty("number", code); 
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mCheckVerifyCodeAction, rpc);
		mPresenter.startPresentServiceTask();
	}
	
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
			}else if (msg.what == 102){
				dismissLoadingView();
				if (msg.obj != null){
//					JSONObject json;
//					try {
//						json = new JSONObject((String)msg.obj);
//						String ret = json.optString("ret");
//						if (ret != null){
//							if (ret.equals("0")){
								Intent nextIntent = new Intent(RegisterUserStep2Activity.this, RegisterUserStep3Activity.class);
								nextIntent.putExtra("phone", mPhone);
								nextIntent.putExtra("user_name", mUserName);
								nextIntent.putExtra("user_password", mPassword);
								startActivity(nextIntent);
//							}else{
//								GlobalUtil.shortToast(getApplication(), getString(R.string.verify_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
//							}
//						}
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
				}
			}else if (msg.what == 105){
				GlobalUtil.shortToast(getApplication(), getString(R.string.register_success), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
				Intent intent = new Intent(RegisterUserStep2Activity.this, HomeActivity.class);
				
				startActivity(intent);
				finish();
			}else if (msg.what == 102){
				dismissLoadingView();
				
				
			}else if (msg.what == 200){
				dismissLoadingView();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}



	@Override
	public void onStatusStart() {
		
		
	}
	
	

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
		mHandler.sendEmptyMessage(200);
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mCheckVerifyCodeAction)){
				Message message = mHandler.obtainMessage();
				message.what = 102;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}else if (action.equals(mSendVerifyCodeAction)){
				
			}
		}
	}

}
