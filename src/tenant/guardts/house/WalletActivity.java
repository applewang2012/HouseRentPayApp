package tenant.guardts.house;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WalletActivity extends BaseActivity {

	private TextView mBalance;
	private Button mTopUp;//充值
	private Button mWithdraw;//提现
	private String balance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		Intent intent = getIntent();
		balance = intent.getStringExtra("balance");
		mBalance = (TextView) findViewById(R.id.text_balance);
		mBalance.setText(balance);
		initView();
		initEvent();
	}

	private void initEvent() {
		mTopUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mWithdraw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转提现
				Intent intent = new Intent(WalletActivity.this, WithdrawActivity.class);
				intent.putExtra("balance", balance);
				startActivity(intent);
				
			}
		});
		
	}

	private void initView() {
	mTopUp = (Button) findViewById(R.id.wallet_btn_top_up);
	mWithdraw = (Button) findViewById(R.id.wallet_btn_withdraw);
		
	}
}
