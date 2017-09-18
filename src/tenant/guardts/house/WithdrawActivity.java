package tenant.guardts.house;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.WithdrawStatus;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;

public class WithdrawActivity extends BaseActivity implements DataStatusInterface {

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.e("", action + "-------" + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mSendVerifyCodeAction)) {

			} else if (action.equals(mCheckVerifyCodeAction)) {
				Message message = mHandler.obtainMessage();
				message.what = 100;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			} else if (action.equals(mWithDrawAction)) {
				Message message = mHandler.obtainMessage();
				message.what = 200;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}

	private TextView mAddBankCard;
	private TextView mSum;// 提现金额
	private String withdrawSum;
	private PopupWindow popupWindow;
	private Button mWithdraw;
	private String idCard;
	private String num;
	private String type;
	private String cardNo;
	private String bankName;
	private String mSendVerifyCodeAction = "http://tempuri.org/SendIdentifyCodeMsg";
	private String mCheckVerifyCodeAction = "http://tempuri.org/ValidateIdentifyCode";
	private String mWithDrawAction = "http://tempuri.org/WithDraw";
	private View view;
	private HoursePresenter mPresenter;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what <= 1) {
				getVerifyNum.setText("获取验证码");
				getVerifyNum.setClickable(true);

			} else {
				getVerifyNum.setText(msg.what + "秒");
				mHandler.sendEmptyMessageDelayed(msg.what - 1, 1000);
				getVerifyNum.setClickable(false);
			}

			if (msg.what == 100) {
				String json = (String) msg.obj;
			}

			if (msg.obj != null) {
				JSONObject json;
				try {
					json = new JSONObject((String) msg.obj);
					String ret = json.optString("ret");
					if (ret != null) {
//						if (ret.equals("0")) {
							Toast.makeText(WithdrawActivity.this, "验证成功！", Toast.LENGTH_SHORT).show();
							// 提现
							// withdraw(CommonUtil.mRegisterRealName,
							// CommonUtil.mBankName, CommonUtil.mCardNo,
							// String.valueOf(Integer.parseInt(CommonUtil.mUserWallet)-1),
							// "云上之家提现");
//						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			if (msg.what == 200) {
				String value = (String) msg.obj;
				Gson gson = new Gson();
				WithdrawStatus status = gson.fromJson(value, WithdrawStatus.class);
				if (status.retCode.equals("0000")) {
					Toast.makeText(WithdrawActivity.this, status.retMsg, Toast.LENGTH_SHORT).show();
				}
			}

		};
	};
	private TextView getVerifyNum;
	private Button checkVerifyNum;
	private Button cancel;
	private EditText verifyNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_withdraw);
		initData();
		initView();
		initEvent();
	}

	private void initData() {
		Intent intent = getIntent();
		withdrawSum = intent.getStringExtra("balance");

	}

	private void initEvent() {
		mAddBankCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mCardNo == null) {
					// 添加银行卡
					Intent intent = new Intent(WithdrawActivity.this, AddBankCardActivity.class);
					intent.putExtra("IDCard", CommonUtil.mRegisterIdcard);
					startActivityForResult(intent, 100);
				} else {

					Intent intent = new Intent(WithdrawActivity.this, BankActivity.class);
					startActivity(intent);
				}
			}
		});

		mWithdraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (TextUtils.isEmpty(CommonUtil.mCardNo)) {
					Toast.makeText(WithdrawActivity.this, "尚未添加银行卡，请先添加银行卡", Toast.LENGTH_SHORT).show();
				} else {
					// 提现验证码
					showPopupWindow();
				}

			}
		});

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// popupwindow消失时还原
				setBackgroundAlpha(1f);

			}
		});

	}

	/**
	 * 设置背景透明度
	 * 
	 * @param alpha
	 */
	public void setBackgroundAlpha(float alpha) {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = alpha;
		getWindow().setAttributes(params);
	}

	/**
	 * 验证码popupwindow
	 * 
	 */
	protected void showPopupWindow() {
		setBackgroundAlpha(0.2f);
		View parent = View.inflate(this, R.layout.activity_withdraw, null);
		popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
		getVerifyNum = (TextView) view.findViewById(R.id.withdraw_getverify_num);
		checkVerifyNum = (Button) view.findViewById(R.id.btn_check_verify_num);
		cancel = (Button) view.findViewById(R.id.btn_cancel);

		verifyNum = (EditText) view.findViewById(R.id.edittext_verify_num);
		getVerifyNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendPhoneVerifyCode(CommonUtil.mUserLoginName);
				mHandler.sendEmptyMessageDelayed(60, 1000);
			}
		});

		// 确定
		checkVerifyNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String num = verifyNum.getText().toString();
				if (num == null || num.equals("")) {
					GlobalUtil.shortToast(getApplication(), getString(R.string.verify_not_null),
							getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				} else if (num.length() != 6) {
					GlobalUtil.shortToast(getApplication(), getString(R.string.verify_error),
							getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				} else {
					checkPhoneVerifyCode(CommonUtil.mUserLoginName, num);
				}

			}
		});
	}

	/**
	 * 提现
	 * 
	 * @param name
	 *            姓名
	 * @param bankName
	 *            银行
	 * @param cardNo
	 *            卡号
	 * @param fee
	 *            金额
	 * @param orderName
	 *            描述
	 */
	private void withdraw(String name, String bankName, String cardNo, String fee, String orderName) {
		String url = "http://www.guardts.com/payment/payservice.asmx?op=WithDraw";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mWithDrawAction));
		rpc.addProperty("name", name);
		rpc.addProperty("bankName", bankName);
		rpc.addProperty("cardNO", cardNo);
		rpc.addProperty("fee", fee);
		rpc.addProperty("orderName", orderName);
		mPresenter.readyPresentServiceParams(this, url, mWithDrawAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	/**
	 * 获取验证码
	 * 
	 * @param phone
	 */
	private void sendPhoneVerifyCode(String phone) {
		String url = "http://www.guardts.com/COMMONSERVICE/COMMONSERVICES.ASMX?op=SendIdentifyCodeMsg";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mSendVerifyCodeAction));
		rpc.addProperty("phone", phone);
		mPresenter.readyPresentServiceParams(this, url, mSendVerifyCodeAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	/**
	 * 验证
	 * 
	 * @param phone
	 * @param code
	 */
	private void checkPhoneVerifyCode(String phone, String code) {
		String url = "http://www.guardts.com/COMMONSERVICE/COMMONSERVICES.ASMX?op=ValidateIdentifyCode";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCheckVerifyCodeAction));
		rpc.addProperty("phone", phone);
		rpc.addProperty("number", code);
		mPresenter.readyPresentServiceParams(this, url, mCheckVerifyCodeAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void initView() {
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mAddBankCard = (TextView) findViewById(R.id.tv_add_bank_card);
		mSum = (TextView) findViewById(R.id.withdraw_tv_sum);
		mSum.setText("¥ " + withdrawSum);
		mWithdraw = (Button) findViewById(R.id.btn_withdraw);
		view = View.inflate(this, R.layout.withdraw_popupwindow, null);
		TextView popupSum = (TextView) view.findViewById(R.id.popup_sum);
		popupSum.setText("转出" + withdrawSum + "元");
		popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(false);
		if (TextUtils.isEmpty(CommonUtil.mCardNo)) {
			mAddBankCard.setText("添加银行卡");
		} else {
			mAddBankCard.setText(CommonUtil.mBankName + "("
					+ CommonUtil.mCardNo.substring(CommonUtil.mCardNo.length() - 4, CommonUtil.mCardNo.length()) + ")");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 回传银行卡类型及卡号后四位
		if (resultCode == RESULT_OK) {
			if (requestCode == 100) {
				num = data.getStringExtra("bankNum");
				type = data.getStringExtra("bankType");
				mAddBankCard.setText(type + "(" + num + ")");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
