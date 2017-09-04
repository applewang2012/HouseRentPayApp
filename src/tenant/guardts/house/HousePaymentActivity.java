package tenant.guardts.house;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class HousePaymentActivity extends BaseActivity {

	private LinearLayout mWallet;
	private LinearLayout mWechat;
	private CheckBox mCheckBoxWallet;
	private CheckBox mCheckBoxWechat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_house_payment);
		initView();
		initEvent();
	}

	private void initEvent() {
		mWallet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setCheckBoxStatus(mCheckBoxWallet, mCheckBoxWechat);
				
			}
		});
		mWechat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setCheckBoxStatus(mCheckBoxWechat, mCheckBoxWallet);
				
			}
		});
		
	}
	
	private void setCheckBoxStatus(CheckBox box1,CheckBox box2){
		box1.setChecked(true);
		box2.setChecked(false);
	}

	private void initView() {
		mWallet = (LinearLayout) findViewById(R.id.payment_linearlayout_wallet);
		mWechat = (LinearLayout) findViewById(R.id.payment_linearlayout_wechat);
		mCheckBoxWallet = (CheckBox) findViewById(R.id.checkbox_wallet);
		mCheckBoxWechat = (CheckBox) findViewById(R.id.checkbox_wechat);
				

	}

}
