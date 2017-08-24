package tenant.guardts.house;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

/**个人信息
 * @author Zhou
 *
 */
public class PersonalInfoActivity extends BaseActivity {
	private LinearLayout mAvator;//头像
	private LinearLayout mNickName;//昵称
	private LinearLayout mIDCard;//身份证
	private LinearLayout mPhone;//手机号
	private LinearLayout mAbout;//关于

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_personal_info);
		initView();
		initEvent();
		
	}

	private void initEvent() {
		mAvator.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		mNickName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		mPhone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		mIDCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		mAbout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		
	}

	private void initView() {
		mAvator = (LinearLayout) findViewById(R.id.linear_layout_avator);
		mNickName = (LinearLayout) findViewById(R.id.linear_layout_nickname);
		mIDCard = (LinearLayout) findViewById(R.id.linear_layout_idcard);
		mPhone = (LinearLayout) findViewById(R.id.linear_layout_phone);
		mAbout = (LinearLayout) findViewById(R.id.linear_layout_about);
		
	}

}
