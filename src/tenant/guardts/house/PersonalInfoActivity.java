package tenant.guardts.house;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**个人信息
 * @author Zhou
 *
 */
public class PersonalInfoActivity extends BaseActivity {

	private TextView mRealName;
	private TextView mPhone;
	private TextView mIdCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_personal_info);
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
		
	}

	private void initView() {
		mRealName = (TextView) findViewById(R.id.personal_info_name);
		mPhone = (TextView) findViewById(R.id.personal_info_phone);
		mIdCard = (TextView) findViewById(R.id.personal_info_idcard);
		
		
	}

}
