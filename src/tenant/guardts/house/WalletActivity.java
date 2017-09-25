package tenant.guardts.house;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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

		initView();
		initEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("", (CommonUtil.mUserWallet instanceof String) + "");

		if (CommonUtil.mUserWallet == null || CommonUtil.mUserWallet.equals("")
				|| CommonUtil.mUserWallet.equalsIgnoreCase("null")) {
			mBalance.setText("¥ 0.0");
		} else {
			mBalance.setText("¥ " + CommonUtil.mUserWallet);
		}
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
					Log.e("",CommonUtil.mUserWallet+"---");
				if (CommonUtil.mUserWallet == null || CommonUtil.mUserWallet.equals("")
						|| CommonUtil.mUserWallet.equalsIgnoreCase("null")||CommonUtil.mUserWallet.equals("0.0")) {
					Toast.makeText(WalletActivity.this, "您的钱包余额为0，暂不能使用提现功能", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(WalletActivity.this, WithdrawActivity.class);
					startActivity(intent);
				}

			}
		});
		transactionDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到交易明细
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
		mBalance = (TextView) findViewById(R.id.text_balance);

	}
}
