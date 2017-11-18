package tenant.guardts.house;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import tenant.guardts.house.util.CommonUtil;

public class BankActivity extends BaseActivity {

	private TextView mTitleBar;
	private String cardNo;
	private String bankName;
	private Button btnAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_bank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("银行卡");
		initView();
	}


	private void initView() {
		TextView user=(TextView) findViewById(R.id.bank_card_name);
		TextView cardNo=(TextView) findViewById(R.id.bank_card_num);
		TextView bankType=(TextView) findViewById(R.id.bank_card_type);
		user.setText(CommonUtil.mRegisterRealName);
		cardNo.setText(CommonUtil.mCardNo);
		bankType.setText(CommonUtil.mBankName);
	}
}
