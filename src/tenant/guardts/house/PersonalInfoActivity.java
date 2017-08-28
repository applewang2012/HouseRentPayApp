package tenant.guardts.house;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**个人信息
 * 
 *
 */
public class PersonalInfoActivity extends BaseActivity {

	private TextView mRealName;
	private TextView mPhone;
	private TextView mIdCard;
	private LinearLayout mAboutUs;
	private TextView mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.activity_personal_info);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("个人信息");
		initView();
		initData();
		initEvent();
		
	}

	private void initData() {
		Intent intent = getIntent();
		String realName = intent.getStringExtra("RealName");
		String phone = intent.getStringExtra("Phone");
		String idCard = intent.getStringExtra("IDCard");
		idCard=idCard.substring(0, 3)+"*****"+idCard.substring(14);
		mRealName.setText(realName);
		mPhone.setText(phone);
		mIdCard.setText(idCard);
		
		
	}

	private void initEvent() {
		mAboutUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(PersonalInfoActivity.this, AboutUsActivity.class));
				
			}
		});
		
	}

	private void initView() {
		mRealName = (TextView) findViewById(R.id.personal_info_name);
		mPhone = (TextView) findViewById(R.id.personal_info_phone);
		mIdCard = (TextView) findViewById(R.id.personal_info_idcard);
		mAboutUs = (LinearLayout) findViewById(R.id.about_us_linearlayout);
		
		
	}

}
