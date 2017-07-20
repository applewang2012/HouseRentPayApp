package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

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
import android.widget.Toast;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.Constants;

public class LoginUserActivity extends BaseActivity{

	private TextView mTitleBar;
	private View mLoadingView;
	private HoursePresenter mPresenter;
	private String mLoginAction = "http://tempuri.org/ValidateLogin";
	private String mUserName, mPassword;
	private EditText userNameEditText;
	private EditText passwordEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login_user); 
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("登录");
		
		initView();
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}



	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		dismissLoadingView();
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
		Button login = (Button)findViewById(R.id.id_login_user_button);
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mUserName = userNameEditText.getEditableText().toString();
				mPassword = passwordEditText.getEditableText().toString();
				if (mUserName == null || mUserName.equals("")){
					Toast.makeText(getApplicationContext(), getString(R.string.please_input_username), Toast.LENGTH_SHORT).show();
					return;
				}
				if (mPassword == null || mPassword.equals("")){
					Toast.makeText(getApplicationContext(), getString(R.string.please_input_username), Toast.LENGTH_SHORT).show();
					return;
				}
				showLoadingView();
				loginUser();
			}
		});
		
		Button registerButton = (Button)findViewById(R.id.id_login_user_register);
		Button modifyButton = (Button)findViewById(R.id.id_login_user_modify_password);
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginUserActivity.this, RegisterUserActivity.class));
			}
		});
		modifyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(LoginUserActivity.this, ModifyPasswordActivity.class));
				startActivityForResult(new Intent(LoginUserActivity.this, ModifyPasswordActivity.class), 1);
			}
		});
	}
	
	private void loginUser(){
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=ValidateLogin";
		SoapObject rpc = new SoapObject(Constants.NAMESPACE, Constants.getSoapName(mLoginAction));
		rpc.addProperty("username", mUserName);
		rpc.addProperty("password", mPassword);
		rpc.addProperty("userType", "0");
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mLoginAction, rpc);
		mPresenter.startPresentServiceTask();
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



	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				dismissLoadingView();
				SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
			    editor.putString("user_name", mUserName);
			    editor.putString("user_password", mPassword);
			    editor.commit();
				Toast.makeText(LoginUserActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginUserActivity.this, HomeActivity.class);
				intent.putExtra("user_name", mUserName);
				intent.putExtra("user_password", mPassword);
				startActivity(intent);
//				startActivity(new Intent(LoginUserActivity.this, HomeActivity.class));
				finish();
			}else if (msg.what == 101){
				dismissLoadingView();
				Toast.makeText(LoginUserActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
			}
			
		}
		
	};
	


	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE){
					dismissLoadingView();
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
			if (action.equals(mLoginAction)){
				if (templateInfo.equals("false")){
					mHandler.sendEmptyMessage(101);
				}else if (templateInfo.equals("true")){
					mHandler.sendEmptyMessage(100);
				}
			}
		}
		
	}
	

}
