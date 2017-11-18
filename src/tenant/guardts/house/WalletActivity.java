package tenant.guardts.house;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;
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
	private HoursePresenter mPresent;
	private String mGetUserInfoAction = "http://tempuri.org/GetUserInfo";

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

		if (CommonUtil.mUserWallet == null || CommonUtil.mUserWallet.equals("")
				|| CommonUtil.mUserWallet.equalsIgnoreCase("null")) {
			mBalance.setText("¥ 0.0");
		} else {
			mBalance.setText("¥ " + CommonUtil.mUserWallet);
		}
		
		getUserInfo();
	}
	
	private void getUserInfo() {
		if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")){
			return;
		}
		String url = CommonUtil.mUserHost + "services.asmx?op=GetUserInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetUserInfoAction));
		rpc.addProperty("username", CommonUtil.mUserLoginName);
		rpc.addProperty("deviceId", CommonUtil.XINGE_TOKEN);
		mPresent.readyPresentServiceParams(WalletActivity.this, url, mGetUserInfoAction, rpc);
		mPresent.startPresentServiceTask(false);
	}

	private void initEvent() {
		mPresent = new HoursePresenter(WalletActivity.this, this);
		
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
					LogUtil.e("",CommonUtil.mUserWallet+"---");
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
	
	private  void parseUserInfo(String value) {
		HashMap<String, String> userInfo = null;
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				LogUtil.w("house", "parse house info " + array.length());
				// for (int item = 0; item < array.length(); item++){
				JSONObject itemJsonObject = array.optJSONObject(0);
				CommonUtil.mUserLoginName = itemJsonObject.optString("LoginName");
				CommonUtil.mRegisterRealName = itemJsonObject.optString("RealName");
				CommonUtil.mRegisterIdcard = itemJsonObject.optString("IDCard");
				CommonUtil.mUserWallet = itemJsonObject.optString("Wallet");
				CommonUtil.mBankName= itemJsonObject.optString("BankName");
				CommonUtil.mCardNo = itemJsonObject.optString("CardNO");
				if (CommonUtil.mUserWallet == null || CommonUtil.mUserWallet.equals("")
						|| CommonUtil.mUserWallet.equalsIgnoreCase("null")) {
					mBalance.setText("¥ 0.0");
				} else {
					mBalance.setText("¥ " + CommonUtil.mUserWallet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				parseUserInfo((String) msg.obj);
			}
		}
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		if (templateInfo != null){
			if (action.equals(mGetUserInfoAction)){
				Message msgMessage = mHandler.obtainMessage();
				msgMessage.what = 100;
				msgMessage.obj = templateInfo;
				msgMessage.sendToTarget();
			}
		}
	}
	
	
}
