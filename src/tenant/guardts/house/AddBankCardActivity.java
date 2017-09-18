package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.AddCardStatus;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class AddBankCardActivity extends BaseActivity implements DataStatusInterface {

	private Button mNext;// 下一步
	private HoursePresenter mPresenter;
	private String mUpdateCreditCard = "http://tempuri.org/UpdateCreditCard";
	private String mGetUserInfoAction = "http://tempuri.org/GetUserInfo";
	private TextView mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_add_bank_card);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("添加银行卡");
		initView();
		initEvent();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 100) {
				String value = (String) msg.obj;
				Gson gson = new Gson();
				AddCardStatus cardStatus = gson.fromJson(value, AddCardStatus.class);
				if (cardStatus.ret.equals("0")) {
					Toast.makeText(AddBankCardActivity.this, "银行卡添加成功！", Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.putExtra("bankNum", bankCardNum.substring(bankCardNum.length() - 4, bankCardNum.length()));
					intent.putExtra("bankType", bankCardType);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(AddBankCardActivity.this, "银行卡添加失败，请重新填写添加信息！", Toast.LENGTH_LONG).show();
				}
			}
		};
	};
	private EditText mBankCardName;
	private EditText mBankCardNum;
	private EditText mBankCardType;
	private String bankCardName;
	private String bankCardNum;
	private String bankCardType;
	private String idCard;
	private String cardNo;
	private String bankName;

	private void initEvent() {
		mNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkInputContent()) {
					updateCreditCard(CommonUtil.mRegisterIdcard, bankCardNum, bankCardType);

				}
			}
		});

	}

	/**
	 * 添加银行卡
	 * 
	 * @param idCard
	 *            身份证号
	 * @param cardNO
	 *            卡号
	 * @param bankName
	 *            姓名
	 */
	private void updateCreditCard(String idCard, String cardNO, String bankName) {
		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=UpdateCreditCard";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUpdateCreditCard));
		rpc.addProperty("idcard", idCard);
		rpc.addProperty("cardNo", cardNO);
		rpc.addProperty("bankName", bankName);
		mPresenter.readyPresentServiceParams(this, url, mUpdateCreditCard, rpc);
		mPresenter.startPresentServiceTask(false);
	}

	private void initView() {
		
		idCard = getIntent().getStringExtra("IDCard");

		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mNext = (Button) findViewById(R.id.add_bank_card_btn_next);
		mBankCardName = (EditText) findViewById(R.id.bank_card_name);
		mBankCardName.setText(CommonUtil.mRegisterRealName);
		mBankCardNum = (EditText) findViewById(R.id.bank_card_num);
		mBankCardType = (EditText) findViewById(R.id.bank_card_type);

	}

	private boolean checkInputContent() {

		if (mBankCardName.getText().toString() == null || mBankCardName.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入持卡人姓名", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			bankCardName = mBankCardName.getText().toString();
		}
		if (mBankCardNum.getText().toString() == null || mBankCardNum.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入银行卡号", Toast.LENGTH_SHORT).show();
			return false;
		} else if (mBankCardNum.getText().toString().length() < 16) {
			Toast.makeText(getApplicationContext(), "银行卡号输入有误", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			bankCardNum = mBankCardNum.getText().toString();
		}
		if (mBankCardType.getText().toString() == null || mBankCardType.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入所属银行", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			bankCardType = mBankCardType.getText().toString();
		}
		return true;
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.e("", action + "===" + templateInfo);
		if (action != null && templateInfo != null) {

			if (action.equals(mUpdateCreditCard)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}

	@Override
	public void onStatusError(String action, String error) {
		super.onStatusError(action, error);
	}

}
