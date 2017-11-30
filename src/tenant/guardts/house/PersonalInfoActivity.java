package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;

/**个人信息
 * 
 *
 */
public class PersonalInfoActivity extends BaseActivity {

	private TextView mRealName;
	private TextView mPhone;
	private TextView mIdCard;
	private LinearLayout mModifyPassword, mLogout;
	private TextView mTitleBar;
	private String mXingeTokenAction = "http://tempuri.org/UpdateDeviceID";
	private HoursePresenter mPresent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.activity_personal_info);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("个人信息");
		initView();
		initData();
		initEvent();
		
	}

	private void initData() {
		mPresent = new HoursePresenter(PersonalInfoActivity.this, this);
		Intent intent = getIntent();
		String realName = intent.getStringExtra("RealName");
		String phone = intent.getStringExtra("Phone");
		String idCard = intent.getStringExtra("IDCard");
		idCard=idCard.substring(0, 3)+"*****"+idCard.substring(14);
		mRealName.setText(realName);
		mPhone.setText(phone);
		mIdCard.setText(idCard);
		
		
	}
	
	private void logoutUserDialog(final int position) {
		new AlertDialog.Builder(PersonalInfoActivity.this, AlertDialog.THEME_HOLO_LIGHT).setTitle(getString(R.string.user_logout))

				.setMessage(getString(R.string.user_logout_remind))

				.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
					@Override

					public void onClick(DialogInterface dialog, int which) {
						uploadXingeToken();
						
					}

				}).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť

					@Override

					public void onClick(DialogInterface dialog, int which) {// ��Ӧ�¼�
						LogUtil.w("alertdialog", " dialog interface ");
					}

				}).show();
	}
	
	private void uploadXingeToken() {

		String url = CommonUtil.mUserHost + "services.asmx?op=UpdateDeviceID";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mXingeTokenAction));
		rpc.addProperty("userId", CommonUtil.mUserLoginName);
		rpc.addProperty("deviceId", "");
		mPresent.readyPresentServiceParams(this, url, mXingeTokenAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void initEvent() {
		TextView avaText = (TextView)findViewById(R.id.id_title_button);
		avaText.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				CommonUtil.verify_code_test = true;
				Toast.makeText(getApplicationContext(), ""+CommonUtil.verify_code_test, Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		
		mModifyPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(PersonalInfoActivity.this, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(PersonalInfoActivity.this, LoginUserActivity.class));
				} else {
					startActivity(new Intent(PersonalInfoActivity.this, ModifyPasswordActivity.class));
				}
			}
		});
		
		mLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(PersonalInfoActivity.this, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(PersonalInfoActivity.this, LoginUserActivity.class));
				} else {
					logoutUserDialog(0);
				}

			}
		});
	}

	private void initView() {
		mRealName = (TextView) findViewById(R.id.personal_info_name);
		mPhone = (TextView) findViewById(R.id.personal_info_phone);
		mIdCard = (TextView) findViewById(R.id.personal_info_idcard);
		mModifyPassword = (LinearLayout) findViewById(R.id.modify_password_layout);
		mLogout = (LinearLayout) findViewById(R.id.id_userinfo_logout);
	}
	
	private void resetUserInfo(){
		SharedPreferences sharedata = getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = sharedata.edit();
		editor.putString("user_name", "");
		editor.putString("user_password", "");
		editor.putString("user_realname", "");
		editor.putString("user_idcard", "");
		//editor.putString("user_host", "");
		editor.commit();
		CommonUtil.mUserLoginName = "";
		CommonUtil.mRegisterRealName = "";
		CommonUtil.mRegisterIdcard = "";
		CommonUtil.mUserWallet = "";
		CommonUtil.mBankName = "";
		CommonUtil.mCardNo = "";
		//CommonUtil.mUserHost = "";
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 101) {
				resetUserInfo();
//				Intent intent = new Intent(PersonalInfoActivity.this, LoginUserActivity.class);
//				startActivity(intent);
				finish();
			}
		}
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		super.onStatusSuccess(action, templateInfo);
		if (action.equals(mXingeTokenAction)){
			Message msgMessage = mHandler.obtainMessage();
			msgMessage.what = 101;
			msgMessage.obj = templateInfo;
			msgMessage.sendToTarget();
		}
		
	}
	
	

}
