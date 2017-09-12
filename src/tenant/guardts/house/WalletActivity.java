package tenant.guardts.house;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.wxapi.RechargeActivity;

public class WalletActivity extends BaseActivity {

	private TextView mBalance;
	private Button mTopUp;// 充值
	private Button mWithdraw;// 提现
	private String balance;
	private TextView transactionDetail;
	private String idCard;
	private String cardNo;
	private String bankName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		ActivityController.addActivity(this);
		Intent intent = getIntent();
		balance = intent.getStringExtra("balance");
		idCard = intent.getStringExtra("IDCard");
		cardNo = intent.getStringExtra("CardNO");
		bankName = intent.getStringExtra("BankName");
		
		mBalance = (TextView) findViewById(R.id.text_balance);
		mBalance.setText(balance);
		initView();
		initEvent();
	}

	private void initEvent() {
		mTopUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(WalletActivity.this, RechargeActivity.class));
			}
		});

		mWithdraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转提现
				Intent intent = new Intent(WalletActivity.this, WithdrawActivity.class);
				intent.putExtra("balance", balance);
				intent.putExtra("IDCard", idCard);
				intent.putExtra("CardNO",cardNo);
				intent.putExtra("BankName", bankName);
				startActivity(intent);

			}
		});
		transactionDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到交易明细
				Intent intent = new Intent(WalletActivity.this, TransactionDetailActivity.class);
				intent.putExtra("IDCard", idCard);
				startActivity(intent);
				
			}
		});

	}

	private void initView() {
		mTopUp = (Button) findViewById(R.id.wallet_btn_top_up);
		mWithdraw = (Button) findViewById(R.id.wallet_btn_withdraw);
		transactionDetail = (TextView) findViewById(R.id.wallet_details);

	}
}
