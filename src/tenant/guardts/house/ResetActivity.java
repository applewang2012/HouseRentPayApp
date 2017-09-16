package tenant.guardts.house;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.ResetStatus;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;

public class ResetActivity extends BaseActivity implements DataStatusInterface {

	private boolean successsful;// 手机是否验证成功
	private TextView mTitleBar;
	private EditText verifyNum;
	private TextView getVerifyNum;
	private HoursePresenter mPresenter;
	private String mSendVerifyCodeAction = "http://tempuri.org/SendIdentifyCodeMsg";
	private String mCheckVerifyCodeAction = "http://tempuri.org/ValidateIdentifyCode";
	private String mForgotPasswordAction = "http://tempuri.org/ForgotPassword";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_reset);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("重置密码");
		initView();
		initEvent();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what <= 1) {
				getVerifyNum.setText("获取验证码");
				getVerifyNum.setClickable(true);

			} 
			if (msg.what <=60) {
				getVerifyNum.setText(msg.what + "秒");
				mHandler.sendEmptyMessageDelayed(msg.what - 1, 1000);
				getVerifyNum.setClickable(false);
			}

			if (msg.what == 100) {

				if (msg.obj != null) {
					JSONObject json;
					try {
						json = new JSONObject((String) msg.obj);
						String ret = json.optString("ret");
						if (ret != null) {
							if (ret.equals("0")) {
								Toast.makeText(ResetActivity.this, "验证成功！", Toast.LENGTH_SHORT).show();
								psd2nd = newPassword2nd.getText().toString();
								strPhone = phone.getText().toString();
								forgotPassword(strPhone,psd2nd);

							} else {
								Toast.makeText(ResetActivity.this, "验证失败！", Toast.LENGTH_SHORT).show();
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			if (msg.what == 200) {
				String value = (String) msg.obj;
				Gson gson = new Gson();
				ResetStatus status=gson.fromJson(value, ResetStatus.class);
				if(status.ret!=null){
					if(status.ret.equals("0")){
						Toast.makeText(ResetActivity.this, "密码重置成功！", Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			}

		};
	};
	private Button confirm;
	private EditText newPassword;
	private EditText newPassword2nd;
	private String psd2nd;
	private EditText phone;
	private String strPhone;
	private String num;

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
			} else if (action.equals(mForgotPasswordAction)) {
				Message message = mHandler.obtainMessage();
				message.what = 200;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}

	/**
	 * 忘记密码
	 * 
	 * @param phone
	 *            登录名
	 * @param newPasssword
	 *            新密码
	 */
	private void forgotPassword(String phone, String newPasssword) {
		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=ForgotPassword";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mForgotPasswordAction));
		rpc.addProperty("LoginName", phone);
		rpc.addProperty("newPassword", newPasssword);
		mPresenter.readyPresentServiceParams(this, url, mForgotPasswordAction, rpc);
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

	private void initEvent() {
		// 发送验证码
		getVerifyNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strPhone = phone.getText().toString();
				if (!TextUtils.isEmpty(strPhone)) {
					sendPhoneVerifyCode(strPhone);
					mHandler.sendEmptyMessageDelayed(60, 1000);
				}

			}
		});

		// 确认
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkContent()) {
					checkPhoneVerifyCode(strPhone, num);
				}

			}
		});

	}

	private boolean checkContent() {

		strPhone = phone.getText().toString();
		if (TextUtils.isEmpty(strPhone)) {
			GlobalUtil.shortToast(getApplication(), "手机号不能为空",
					getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			return false;
		}
		String psd = newPassword.getText().toString();
		if (TextUtils.isEmpty(psd)) {
			GlobalUtil.shortToast(getApplication(), getString(R.string.new_pwd_not_null),
					getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			return false;
		}
		psd2nd = newPassword2nd.getText().toString();
		if (TextUtils.isEmpty(psd2nd)) {
			GlobalUtil.shortToast(getApplication(), getString(R.string.new_again_pwd_not_null),
					getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			return false;
		}
		if (!psd.equals(psd2nd)) {
			GlobalUtil.shortToast(getApplication(), getString(R.string.twice_pwd_not_same),
					getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			return false;
		}

		num = verifyNum.getText().toString();
		if (TextUtils.isEmpty(num)) {
			GlobalUtil.shortToast(getApplication(), "验证码不能为空",
					getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			return false;
		} else if (num.length() < 6) {
			GlobalUtil.shortToast(getApplication(), "验证码输入错误",
					getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			return false;
		}

		return true;
	}

	private void initView() {
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		phone = (EditText) findViewById(R.id.id_phone);
		verifyNum = (EditText) findViewById(R.id.verify_num);
		getVerifyNum = (TextView) findViewById(R.id.get_verify_num);
		confirm = (Button) findViewById(R.id.btn_confirm);
		newPassword = (EditText) findViewById(R.id.new_password);
		newPassword2nd = (EditText) findViewById(R.id.new_password_second);
	}
}
