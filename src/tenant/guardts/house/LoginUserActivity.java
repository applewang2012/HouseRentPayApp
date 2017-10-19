package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.ViewUtil;

public class LoginUserActivity extends BaseActivity {

	private TextView mTitleBar;
	//
	private HoursePresenter mPresenter;
	private String mLoginAction = "http://tempuri.org/ValidateLogin";
	private String mCommonServiceAction = "http://tempuri.org/GetAreas";
	private String mUserInfoAction = "http://tempuri.org/GetUserInfo";
	private String mValidateLoginNameAction = "http://tempuri.org/ValidateLoginName";// 是否已经注册
	private String mUserName, mPassword;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private boolean mIntentStatus = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login_user);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("登 录");
		
		ActivityController.addActivity(LoginUserActivity.this);
		mIntentStatus = getIntent().getBooleanExtra("intent_status", false);
		initView();
		commonServiceInterface();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * 检测该用户是否注册
	 * 
	 * @param loginName
	 */
	private void haveRegisted(String loginName) {
		String url = CommonUtil.mUserHost + "services.asmx?op=ValidateLoginName";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mValidateLoginNameAction));
		rpc.addProperty("loginName", loginName);
		mPresenter.readyPresentServiceParams(this, url, mValidateLoginNameAction, rpc);
		mPresenter.startPresentServiceTask(false);

	}

	private void initView() {
		forgetPassword = (TextView) findViewById(R.id.forget_password);
		userAgreement = (TextView) findViewById(R.id.tv_user_agreement);// 用户协议
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		userNameEditText = (EditText) findViewById(R.id.id_login_username);
		passwordEditText = (EditText) findViewById(R.id.id_login_password);
		// mUserName = getIntent().getStringExtra("user_name");
		// mPassword = getIntent().getStringExtra("user_password");
		// if (mUserName != null && !mUserName.equals("")){
		// userNameEditText.setText(mUserName);
		// }
		// if (mPassword != null && !mPassword.equals("")){
		// passwordEditText.setText(mPassword);
		// }
		userNameEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!TextUtils.isEmpty(s)){
					if(s.length()==11){
						haveRegisted(s.toString());
					}
				}
				
			}
		});
		
		
		// 忘记密码
		forgetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mUserName = userNameEditText.getEditableText().toString();
				if (mUserName == null || mUserName.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入手机号码", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mUserName.length() < 11) {
					Toast.makeText(getApplicationContext(), "手机号码输入有误", Toast.LENGTH_SHORT).show();
					return;
				}

				ViewUtil.forceCloseSoftKeyborad(LoginUserActivity.this);
				Intent resetIntent = new Intent(LoginUserActivity.this, ResetActivity.class);
				resetIntent.putExtra("user_name", mUserName);
				resetIntent.putExtra("intent_status", mIntentStatus);
				startActivity(resetIntent);
			}
		});

		userAgreement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// startActivity(new Intent(LoginUserActivity.this,
				// UserAgreementActivity.class));
				Intent loadIntent = new Intent(LoginUserActivity.this, LoadUrlTestActivity.class);
				loadIntent.putExtra("url", "http://mg.weiyuzhu.cn/");
				loadIntent.putExtra("tab_name", "用户服务协议");
				startActivity(loadIntent);
			}
		});
		Button login = (Button) findViewById(R.id.id_login_user_button);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUserName = userNameEditText.getEditableText().toString();
				mPassword = passwordEditText.getEditableText().toString();
				if (mUserName == null || mUserName.equals("")) {
					GlobalUtil.shortToast(getApplication(), "请输入手机号码",
							getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					
					return;
				}
				if (mPassword == null || mPassword.equals("")) {
					GlobalUtil.shortToast(getApplication(), "请输入密码",
							getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (CommonUtil.mUserHost == null || CommonUtil.mUserHost.equals("")) {
					GlobalUtil.shortToast(getApplication(), "您尚未选择所在区域",
							getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				ViewUtil.forceCloseSoftKeyborad(LoginUserActivity.this);
				loginUser();
			}
		});

		Button registerButton = (Button) findViewById(R.id.id_login_user_register);
		Button modifyButton = (Button) findViewById(R.id.id_login_user_modify_password);
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserHost == null || CommonUtil.mUserHost.equals("")) {
					GlobalUtil.shortToast(getApplication(), "您尚未选择所在区域",
							getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}

				startActivity(new Intent(LoginUserActivity.this, RegisterUserStep2Activity.class));
			}
		});
		modifyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserHost == null || CommonUtil.mUserHost.equals("")) {
					GlobalUtil.shortToast(getApplication(), "您尚未选择所在区域",
							getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				startActivityForResult(new Intent(LoginUserActivity.this, ModifyPasswordActivity.class), 1);
			}
		});
	}

	private void loginUser() {
		String url = CommonUtil.mUserHost + "services.asmx?op=ValidateLogin";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mLoginAction));
		rpc.addProperty("username", mUserName);
		rpc.addProperty("password", mPassword);
		rpc.addProperty("userType", "0");
		mPresenter.readyPresentServiceParams(this, url, mLoginAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void commonServiceInterface() {
		SharedPreferences sharedata = getApplication().getSharedPreferences("user_info", 0);
		String host = sharedata.getString("user_host", "");
		Log.w("mingguo", "common service preference host  " + host);
		if (host == null || host.equals("")) {
			String url = "http://www.guardts.com/commonservice/commonservices.asmx?op=GetAreas";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCommonServiceAction));
			rpc.addProperty("status", "1");
			mPresenter.readyPresentServiceParams(this, url, mCommonServiceAction, rpc);
			mPresenter.startPresentServiceTask(false);
		} else {
			CommonUtil.mUserHost = host;
		}
	}

	private void showLoadingView() {
		// if (mLoadingView != null) {
		// mLoadingView.setVisibility(View.VISIBLE);
		// ImageView imageView = (ImageView)
		// mLoadingView.findViewById(R.id.id_progressbar_img);
		// if (imageView != null) {
		// RotateAnimation rotate = (RotateAnimation)
		// AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
		// imageView.startAnimation(rotate);
		// }
		// }
	}

	private void dismissLoadingView() {
		// if (mLoadingView != null) {
		//
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			if (data != null) {
				mPassword = data.getStringExtra("new_password");
				passwordEditText.setText(mPassword);
				Log.w("mingguo", "login activity  onActivityResult password  " + mPassword);
			}
		}
	}

	private void showSelectAlertDialog(final String title, final List<String[]> data) {
		if (data != null && data.size() == 1){
			SharedPreferences sharedata = getApplication().getSharedPreferences("user_info", 0);
			SharedPreferences.Editor editor = sharedata.edit();
			editor.putString("area", data.get(0)[0]);
			editor.putString("user_host", data.get(1)[0]);
			editor.commit();
			CommonUtil.mUserArea = data.get(0)[0];
			CommonUtil.mUserHost = data.get(1)[0];
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginUserActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(title);
		//ic_);
		builder.setItems(data.get(0), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences sharedata = getApplication().getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
				editor.putString("area", data.get(0)[which]);
				editor.putString("user_host", data.get(1)[which]);
				editor.commit();
				CommonUtil.mUserArea = data.get(0)[which];
				CommonUtil.mUserHost = data.get(1)[which];
				Log.w("mingguo", "CommonUtil.mUserArea  " + CommonUtil.mUserArea + "  CommonUtil.mUserHost  "
						+ CommonUtil.mUserHost);
			}
		});
		builder.setCancelable(false);
		builder.show();

	}

	private List<String[]> parseCommonService(String value) {
		String[] areaName = null;
		String[] areaHost;
		List<String[]> list = new ArrayList<>();
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				areaName = new String[array.length()];
				areaHost = new String[array.length()];
				for (int item = 0; item < array.length(); item++) {
					JSONObject itemJsonObject = array.optJSONObject(item);
					areaName[item] = itemJsonObject.optString("AreaName");
					areaHost[item] = itemJsonObject.optString("RentHost");
				}
				list.add(areaName);
				list.add(areaHost);
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	private void parseUserInfo(String value) {
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.w("house", "parse house info " + array.length());
				// for (int item = 0; item < array.length(); item++){
				JSONObject itemJsonObject = array.optJSONObject(0);

				CommonUtil.mUserLoginName = itemJsonObject.optString("LoginName");
				CommonUtil.mRegisterRealName = itemJsonObject.optString("RealName");
				CommonUtil.mRegisterIdcard = itemJsonObject.optString("IDCard");
				CommonUtil.mUserWallet = itemJsonObject.optString("Wallet");
				CommonUtil.mBankName = itemJsonObject.optString("BankName");
				CommonUtil.mCardNo = itemJsonObject.optString("CardNO");
				SharedPreferences sharedata = getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
				editor.putString("user_realname", CommonUtil.mRegisterRealName);
				editor.putString("user_idcard", CommonUtil.mRegisterIdcard);
				editor.putString("user_name", mUserName);
				editor.putString("user_password", mPassword);
				editor.putString("user_host", CommonUtil.mUserHost);
				editor.commit();
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
			if (msg.what == 100) {
				Log.w("mingguo", "loginUserActivity  username   " + mUserName + "  password  " + mPassword);
				GlobalUtil.shortToast(getApplication(), getString(R.string.login_success),
						getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
				getUserInfo(mUserName);
			} else if (msg.what == 101) {

				GlobalUtil.shortToast(getApplication(), getString(R.string.login_failed),
						getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			} else if (msg.what == 110) {
				showSelectAlertDialog("请选择所在区域", parseCommonService((String) msg.obj));
			} else if (msg.what == 200) {
				parseUserInfo((String) msg.obj);
				if (!mIntentStatus) {
					Intent intent = new Intent(LoginUserActivity.this, HomeActivity.class);
					intent.putExtra("user_name", mUserName);
					intent.putExtra("user_password", mPassword);
					startActivity(intent);
				}

				finish();
			} else if (msg.what == 300) {
				String value = (String) msg.obj;
				if (!TextUtils.isEmpty(value)) {
					if (value.equals("true")) {
						Toast.makeText(LoginUserActivity.this, "检测到您尚未注册，请先注册！", Toast.LENGTH_LONG).show();
						forgetPassword.setClickable(false);
					} else if (value.equals("false")) {
						forgetPassword.setClickable(true);
					}
				}
			}
		}
	};
	private TextView userAgreement;
	private TextView forgetPassword;

	private void getUserInfo(String username) {
		if (username == null || username.equals("")) {
			return;
		}
		String url = CommonUtil.mUserHost + "services.asmx?op=GetUserInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUserInfoAction));
		rpc.addProperty("username", username);
		rpc.addProperty("deviceId", CommonUtil.XINGE_TOKEN);
		mPresenter.readyPresentServiceParams(LoginUserActivity.this, url, mUserInfoAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if (mLoadingView != null && mLoadingView.getVisibility() ==
			// View.VISIBLE){
			//
			// return false;
			// }
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		super.onStatusSuccess(action, templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mLoginAction)) {
				if (templateInfo.equalsIgnoreCase("false")) {
					mHandler.sendEmptyMessage(101);
				} else if (templateInfo.equalsIgnoreCase("true")) {
					mHandler.sendEmptyMessage(100);
				}
			} else if (action.equals(mCommonServiceAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 110;
				msg.obj = templateInfo;
				mHandler.sendMessage(msg);
			} else if (action.equals(mUserInfoAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 200;
				msg.obj = templateInfo;
				mHandler.sendMessage(msg);
			} else if (action.equals(mValidateLoginNameAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 300;
				msg.obj = templateInfo;
				mHandler.sendMessage(msg);
			}
		}

	}

}
