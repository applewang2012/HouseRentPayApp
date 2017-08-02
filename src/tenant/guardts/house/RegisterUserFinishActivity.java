package tenant.guardts.house;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import tenant.guardts.house.presenter.HoursePresenter;

public class RegisterUserFinishActivity extends BaseActivity{

	private TextView mTitleBar;
	private HoursePresenter mPresenter;
	private String mUserName;
	private String mPassword, mPasswordIndentify;
	private String mValidAction = "http://tempuri.org/ValidateLoginName";
	private boolean mUsernameValid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.register_user_finish_layout); 
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("注册完成");
		Button back = (Button)findViewById(R.id.id_titlebar_back);
		back.setVisibility(View.INVISIBLE);
		mUserName = getIntent().getStringExtra("user_name");
		mPassword = getIntent().getStringExtra("user_password");
		initView();
	}
	
	

	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		
		Button interButton = (Button)findViewById(R.id.id_register_user_enter_app);
		interButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterUserFinishActivity.this, HomeActivity.class);
				intent.putExtra("user_name", mUserName);
				intent.putExtra("user_password", mPassword);
				startActivity(intent);
				finish();
			}
		});
		
	}
}
