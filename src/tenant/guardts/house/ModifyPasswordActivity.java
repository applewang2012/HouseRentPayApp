package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.ViewUtil;

public class ModifyPasswordActivity extends BaseActivity{

	private TextView mTitleBar;
	
	private HoursePresenter mPresenter;
	private String mModifyAction = "http://tempuri.org/ChangePassword";
	private String mUserName, mOldPassword, mNewPassword, mNewPasswordConfirm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.modify_user_password); 
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText(getString(R.string.modify_password));
		
		initView();
	}
	
	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		
		final TextView userName = (TextView)findViewById(R.id.id_password_username);
		mUserName = CommonUtil.mUserLoginName;
		userName.setText(mUserName);
		final EditText oldpassword = (EditText)findViewById(R.id.id_old_password);
		final EditText newpassword = (EditText)findViewById(R.id.id_new_password);
		final EditText newpasswordConfirm = (EditText)findViewById(R.id.id_new_password_confirml);
		Button mofifyButton = (Button)findViewById(R.id.id_modify_password_confirm);
		mofifyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOldPassword = oldpassword.getEditableText().toString();
				mNewPassword = newpassword.getEditableText().toString();
				mNewPasswordConfirm = newpasswordConfirm.getEditableText().toString();
				if (mUserName == null || mUserName.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.user_name_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (mOldPassword == null || mOldPassword.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.old_pwd_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (mNewPassword == null || mNewPassword.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.new_pwd_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if(!TextUtils.isEmpty(mNewPassword)){
					if(mNewPassword.length()<6){
						GlobalUtil.shortToast(getApplication(), "新密码不能小于6位", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
						return ;
					}
				}
				
				if (mNewPasswordConfirm == null || mNewPasswordConfirm.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.new_again_pwd_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				
				if(!TextUtils.isEmpty(mNewPasswordConfirm)){
					if(mNewPasswordConfirm.length()<6){
						GlobalUtil.shortToast(getApplication(), "确认密码不能小于6位", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
						return ;
					}
				}
				
				if (!mNewPassword.equals(mNewPasswordConfirm)){
					GlobalUtil.shortToast(getApplication(), getString(R.string.twice_pwd_not_same), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				ViewUtil.forceCloseSoftKeyborad(ModifyPasswordActivity.this);
				modifyUserPassword();
			}
		});
		
	}
	
	private void modifyUserPassword(){
		String url = CommonUtil.mUserHost+"services.asmx?op=ChangePassword";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mModifyAction));
		rpc.addProperty("username", mUserName);
		rpc.addProperty("oldPassword", mOldPassword);
		rpc.addProperty("newPassword", mNewPassword);
		rpc.addProperty("userType", "0");
		mPresenter.readyPresentServiceParams(this, url, mModifyAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				
				SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
			    editor.putString("user_password", mNewPassword);
			    editor.commit();
			    GlobalUtil.shortToast(getApplication(), getString(R.string.modify_pwd_success), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
			    Intent passwordIntent = new Intent();
			    passwordIntent.putExtra("new_password", mNewPassword);
			    setResult(RESULT_OK, passwordIntent);
				finish();
			}else if (msg.what == 101){
				
				GlobalUtil.shortToast(getApplication(), getString(R.string.modify_pwd_failed), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}
			
		}
		
	};
	

	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return super.onKeyDown(keyCode, event);
		}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mModifyAction)){
				if (templateInfo.equals("false")){
					mHandler.sendEmptyMessage(101);
				}else if (templateInfo.equals("true")){
					mHandler.sendEmptyMessage(100);
				}
			}
		}
		
	}

	@Override
	public void onStatusError(String action, String error) {
		
	}
	
	

}
