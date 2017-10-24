package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterUserStep1Activity extends BaseActivity{

	private TextView mTitleBar;
	private HoursePresenter mPresenter;
	private String mUserName;
	private String mPassword, mPasswordIndentify;
	private String mValidAction = "http://tempuri.org/ValidateLoginName";
	//private boolean mUsernameValid;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.register_user_step1_layout); 
		ActivityController.addActivity(RegisterUserStep1Activity.this);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("用户设置");
//		ImageView backImage = (ImageView)findViewById(R.id.id_titlebar_back);
//		backImage.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//		initHandler();
		initView();
	}
	

	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		
		final EditText password = (EditText)findViewById(R.id.id_register_step1_input_password);
		final EditText passowrdInditfy = (EditText)findViewById(R.id.id_register_step1_input_password_identify);
		
		final EditText userName = (EditText)findViewById(R.id.id_register_step1_input_username);
		userName.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				/*if (!hasFocus){
					mUserName = userName.getEditableText().toString();
					if (mUserName != null && mUserName.length() > 0){
						checkUserNameValid(mUserName);
					}
				}*/
			}
		});
		
		Button nextButton = (Button)findViewById(R.id.id_register_user_step1_next);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUserName = userName.getEditableText().toString();
				mPassword = password.getEditableText().toString();
				mPasswordIndentify = passowrdInditfy.getEditableText().toString();
				
				LogUtil.w("mingguo", "user name  "+mUserName);
				if (mUserName == null || mUserName.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.user_name_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
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
//				if (!mUsernameValid){
//					GlobalUtil.shortToast(getApplication(), getString(R.string.username_register_again), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
//					return;
//				}
				
				mUserName = userName.getEditableText().toString();
				if (mUserName != null && mUserName.length() > 0){
					
					checkUserNameValid(mUserName);
				}
				
				
			}
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.w("mingguo", "onActivityResult resultCode  "+resultCode+" requestCode  "+requestCode+"  file  ");
		if (resultCode == RESULT_OK && requestCode == 1) {
			 LogUtil.w("mingguo", "activity result  width data   "+data);
		}
	}

    	
	private void checkUserNameValid(String username){
		String url = CommonUtil.mUserHost+"services.asmx?op=ValidateLoginName";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mValidAction));
		rpc.addProperty("loginName", username); 
		mPresenter.readyPresentServiceParams(this, url, mValidAction, rpc);
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
				Intent nextIntent = new Intent(RegisterUserStep1Activity.this, RegisterUserStep2Activity.class);
				nextIntent.putExtra("username", mUserName);
				nextIntent.putExtra("password", mPassword);
				startActivity(nextIntent);
			}
			
		}
		
	};
	
	

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
		//mHandler.sendEmptyMessage(200);
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mValidAction)){
				LogUtil.w("mingguo", "on success  action valid ");
				if (templateInfo.equals("false")){
					mHandler.sendEmptyMessage(100);
					
				}else{
					mHandler.sendEmptyMessage(200);
				}
			}
		}
	}

}
