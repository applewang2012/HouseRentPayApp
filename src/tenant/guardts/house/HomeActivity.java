package tenant.guardts.house;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.model.HouseFragment;
import tenant.guardts.house.model.MyFragment;
import tenant.guardts.house.model.OrderFragment;
import tenant.guardts.house.model.SurroundFragment;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;

public class HomeActivity extends BaseActivity {

	private HoursePresenter mPresenter;
	// private String mLoginAction = "http://tempuri.org/ValidateLogin";
	private String mUpdateAction = "http://tempuri.org/CheckUpgrade";
	private String mUserInfoAction = "http://tempuri.org/GetUserInfo";
	private String mOpenDoorAction = "http://tempuri.org/OpenDoor";
	private String mXingeTokenAction = "http://tempuri.org/UpdateDeviceID";
	private String mUserName, mPassword;
	private HouseFragment mHouseFrament;
	private MyFragment mMyFragment;
	private SurroundFragment mSurroundFragment;
	private OrderFragment mHistoryFragment;
	private String mUserInfoString = null;
	private String mCity = null;
	private int mVersionCode = -1;
	private Bundle bundle;
	private View mOpenLockLoadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否从推送通知栏打开的
	    XGPushClickedResult click = XGPushManager.onActivityStarted(this);
	    if (click != null) {
	        //从推送通知栏打开-Service打开Activity会重新执行Laucher流程
	        //查看是不是全新打开的面板
	        if (isTaskRoot()) {
	            return;
	        }
	        //如果有面板存在则关闭当前的面板
	        finish();
	    }
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.home_layout);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
//		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
//		mTitleBar.setText("手机注册");
		initData();
		initView();
		getUserInfo();
	}
	
	
	private void initData(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
		mUserName = sharedata.getString("user_name", "");
		mPassword = sharedata.getString("user_password", "");
		bundle = new Bundle();
		bundle.putString("user_name", mUserName);
		CommonUtil.mUserLoginName = mUserName;
	}
	

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		initData();
		getUserInfo();
	}



	private void initView() {
		mOpenLockLoadingView = (View)findViewById(R.id.id_open_lock_layout);
		mOpenLockLoadingView.setVisibility(View.INVISIBLE);
		
		Button scanButton = (Button) findViewById(R.id.id_scan_rent_house);
		scanButton.setVisibility(View.VISIBLE);
		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent openCameraIntent = new Intent(HomeActivity.this, CaptureActivity.class);
				startActivityForResult(openCameraIntent, CommonUtil.mScanCodeRequestCode);
			}
		});

		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		if (mHouseFrament == null) {
			mHouseFrament = new HouseFragment();
			mHouseFrament.setArguments(bundle);// 向fragment传值
			fragmentTransaction.add(R.id.id_home_content, mHouseFrament);
			fragmentTransaction.commitAllowingStateLoss();
		} else {
			fragmentTransaction.show(mHouseFrament);
			fragmentTransaction.commitAllowingStateLoss();
		}

		// if (mSurroundFragment == null){
		// mSurroundFragment = new SurroundFragment();
		// fragmentTransaction.add(R.id.id_home_content, mSurroundFragment);
		// fragmentTransaction.commitAllowingStateLoss();
		// }else{
		// fragmentTransaction.show(mSurroundFragment);
		// fragmentTransaction.commitAllowingStateLoss();
		// }

		final LinearLayout houseLayout = (LinearLayout) findViewById(R.id.id_home_tab_home);
		final LinearLayout myLayout = (LinearLayout) findViewById(R.id.id_home_tab_my);
		final ImageView houseIcon = (ImageView) findViewById(R.id.id_home_tab_home_icon);
		final ImageView myIcon = (ImageView) findViewById(R.id.id_home_tab_my_icon);
		final TextView houseText = (TextView) findViewById(R.id.id_home_tab_home_text);
		final TextView myText = (TextView) findViewById(R.id.id_home_tab_my_text);
		final LinearLayout surroundlayout = (LinearLayout) findViewById(R.id.id_home_tab_surround);
		final LinearLayout historylayout = (LinearLayout) findViewById(R.id.id_home_tab_history);
		final ImageView surroundicon = (ImageView) findViewById(R.id.id_home_tab_surround_icon);
		final ImageView historyicon = (ImageView) findViewById(R.id.id_home_tab_history_icon);
		final TextView surroundtext = (TextView) findViewById(R.id.id_home_tab_surround_text);
		final TextView historytext = (TextView) findViewById(R.id.id_home_tab_history_text);
		houseLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// mTitleBar.setText(getString(R.string.home_tab_house));
				houseIcon.setBackgroundResource(R.drawable.home_icon);
				houseText.setTextColor(Color.parseColor("#337ffd"));
				myIcon.setBackgroundResource(R.drawable.my_icon_default);
				myText.setTextColor(Color.parseColor("#b2b2b2"));
				surroundicon.setBackgroundResource(R.drawable.surround_icon_default);
				surroundtext.setTextColor(Color.parseColor("#b2b2b2"));
				historyicon.setBackgroundResource(R.drawable.history_icon_default);
				historytext.setTextColor(Color.parseColor("#b2b2b2"));
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				hideAllFragments(fragmentTransaction);
				if (mHouseFrament == null) {
					mHouseFrament = new HouseFragment();
					fragmentTransaction.add(R.id.id_home_content, mHouseFrament);
					fragmentTransaction.commitAllowingStateLoss();
				} else {
					fragmentTransaction.show(mHouseFrament);
					fragmentTransaction.commitAllowingStateLoss();
				}
			}
		});

		myLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mTitleBar.setText(getString(R.string.home_tab_my));
				houseIcon.setBackgroundResource(R.drawable.home_icon_default);
				houseText.setTextColor(Color.parseColor("#b2b2b2"));
				myIcon.setBackgroundResource(R.drawable.my_icon);
				myText.setTextColor(Color.parseColor("#337ffd"));
				surroundicon.setBackgroundResource(R.drawable.surround_icon_default);
				surroundtext.setTextColor(Color.parseColor("#b2b2b2"));
				historyicon.setBackgroundResource(R.drawable.history_icon_default);
				historytext.setTextColor(Color.parseColor("#b2b2b2"));
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				hideAllFragments(fragmentTransaction);
				if (mMyFragment == null) {
					mMyFragment = new MyFragment();
					mMyFragment.setArguments(bundle);// 向fragment传值
					fragmentTransaction.add(R.id.id_home_content, mMyFragment);
					fragmentTransaction.commitAllowingStateLoss();
				} else {
					fragmentTransaction.show(mMyFragment);
					fragmentTransaction.commitAllowingStateLoss();
				}
			}
		});

		surroundlayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("mingguo", "surround   onclick  ");
				houseIcon.setBackgroundResource(R.drawable.home_icon_default);
				houseText.setTextColor(Color.parseColor("#b2b2b2"));
				myIcon.setBackgroundResource(R.drawable.my_icon_default);
				myText.setTextColor(Color.parseColor("#b2b2b2"));
				surroundicon.setBackgroundResource(R.drawable.surround_icon);
				surroundtext.setTextColor(Color.parseColor("#337ffd"));
				historyicon.setBackgroundResource(R.drawable.history_icon_default);
				historytext.setTextColor(Color.parseColor("#b2b2b2"));
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				hideAllFragments(fragmentTransaction);
				if (mSurroundFragment == null) {
					mSurroundFragment = new SurroundFragment();
					fragmentTransaction.add(R.id.id_home_content, mSurroundFragment);
					fragmentTransaction.commitAllowingStateLoss();
				} else {
					fragmentTransaction.show(mSurroundFragment);
					fragmentTransaction.commitAllowingStateLoss();
				}
			}
		});
		historylayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mTitleBar.setText(getString(R.string.home_tab_history));
				houseIcon.setBackgroundResource(R.drawable.home_icon_default);
				houseText.setTextColor(Color.parseColor("#b2b2b2"));
				myIcon.setBackgroundResource(R.drawable.my_icon_default);
				myText.setTextColor(Color.parseColor("#b2b2b2"));
				surroundicon.setBackgroundResource(R.drawable.surround_icon_default);
				surroundtext.setTextColor(Color.parseColor("#b2b2b2"));
				historyicon.setBackgroundResource(R.drawable.history_icon);
				historytext.setTextColor(Color.parseColor("#337ffd"));
				if (mUserName.equals("") || mUserName == null) {
					Toast.makeText(HomeActivity.this, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(HomeActivity.this, LoginUserActivity.class));
				} else {

					FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
					hideAllFragments(fragmentTransaction);
					if (mHistoryFragment == null) {
						mHistoryFragment = new OrderFragment();
						fragmentTransaction.add(R.id.id_home_content, mHistoryFragment);
						fragmentTransaction.commitAllowingStateLoss();
					} else {
						fragmentTransaction.show(mHistoryFragment);
						fragmentTransaction.commitAllowingStateLoss();
					}
				}

			}
		});

	}

	private void getOpenDoorRequest(String lockId) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=OpenDoor";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mOpenDoorAction));
		rpc.addProperty("lockId", lockId);
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mOpenDoorAction, rpc);
		mPresenter.startPresentServiceTask();
	}

	private void checkVersionUpdate() {
		mVersionCode = GlobalUtil.getVersionCode(getApplicationContext());
		String url = "http://www.guardts.com/UpgradeService/SystemUpgradeService.asmx?op=CheckUpgrade";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUpdateAction));
		rpc.addProperty("packageName", GlobalUtil.getPackageName(getApplicationContext()));
		rpc.addProperty("versionId", GlobalUtil.getVersionCode(getApplicationContext()));
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mUpdateAction, rpc);
		mPresenter.startPresentServiceTask();
	}

	private void getUserInfo() {
		String url = CommonUtil.mUserHost + "services.asmx?op=GetUserInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUserInfoAction));
		rpc.addProperty("username", mUserName);
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mUserInfoAction, rpc);
		mPresenter.startPresentServiceTask();
	}
	
	private void uploadXingeToken() {
		if (CommonUtil.XINGE_TOKEN == null || CommonUtil.XINGE_TOKEN.equals("")){
			return;
		}
		String url = CommonUtil.mUserHost + "services.asmx?op=UpdateDeviceID";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mXingeTokenAction));
		rpc.addProperty("userId", mUserName);
		rpc.addProperty("deviceId", CommonUtil.XINGE_TOKEN);
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mXingeTokenAction, rpc);
		mPresenter.startPresentServiceTask();
	}

	private void hideAllFragments(FragmentTransaction transaction) {
		if (mHouseFrament != null && !mHouseFrament.isHidden()) {
			transaction.hide(mHouseFrament);
		}
		if (mMyFragment != null && !mMyFragment.isHidden()) {
			transaction.hide(mMyFragment);
		}
		if (mSurroundFragment != null && !mSurroundFragment.isHidden()) {
			transaction.hide(mSurroundFragment);
		}
		if (mHistoryFragment != null && !mHistoryFragment.isHidden()) {
			transaction.hide(mHistoryFragment);
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100) {
				if (msg.obj != null) {
					parseUserInfo((String) msg.obj);
				}
				checkVersionUpdate();
			} else if (msg.what == 101) {
				Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 200) {
				if (msg.obj != null) {
					parseUpdateVersion((String) msg.obj);
					showUpdateVersionAlertDialog();
				}
				uploadXingeToken();
			}else if (msg.what == 300){
				dimissOpenDoorLoading();
				JSONObject itemJsonObject;
				try {
					itemJsonObject = new JSONObject((String)msg.obj);
					String ret = itemJsonObject.optString("ret");
					if (ret != null) {
						if (ret.equals("0")){
							Toast.makeText(getApplicationContext(), "开锁成功"+itemJsonObject.optString("msg"), Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), itemJsonObject.optString("msg"), Toast.LENGTH_LONG).show();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	};

	private void showUpdateVersionAlertDialog() {
		if (CommonUtil.DOWLOAD_URL == null || CommonUtil.DOWLOAD_URL.equals("")) {
			Log.w("mingguo", "home activity  delete installed file  " + CommonUtil.deleteInstalledApkFile());
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("升级云上之家");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override

			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(HomeActivity.this, DownloadAppActivity.class));
				finish();
			}

		});
		builder.setCancelable(false);
		builder.show();
	}
	
	private void showOpenDoorLoadingView(){
		mOpenLockLoadingView.setVisibility(View.VISIBLE);
		ImageView circleImage = (ImageView)mOpenLockLoadingView.findViewById(R.id.imageView2);
		ObjectAnimator animator = ObjectAnimator.ofFloat(circleImage, "rotation", 0.0F,720.0F).setDuration(2000);
		
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setInterpolator(new LinearInterpolator());;
		animator.start();
	}
	
	private void dimissOpenDoorLoading(){
		mOpenLockLoadingView.setVisibility(View.GONE);
	}

	private void showOpenDoorAlertDialog(final String lockId) {

		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("智能锁编号：\n" + lockId);
		builder.setMessage("您确认要开锁吗？");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override

			public void onClick(DialogInterface dialog, int which) {
				showOpenDoorLoadingView();
				getOpenDoorRequest(lockId);
			}

		});
		builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

			@Override

			public void onClick(DialogInterface dialog, int which) {
				Log.i("alertdialog", " �뱣�����ݣ�");
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	private void parseUserInfo(String value) {
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				// for (int item = 0; item < array.length(); item++){

				JSONObject itemJsonObject = array.optJSONObject(0);
				
				CommonUtil.mUserLoginName = itemJsonObject.optString("LoginName");
				CommonUtil.mRegisterRealName = itemJsonObject.optString("RealName");
				CommonUtil.mRegisterIdcard = itemJsonObject.optString("IDCard");
				SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
				editor.putString("user_realname", CommonUtil.mRegisterRealName);
				editor.putString("user_idcard", CommonUtil.mRegisterIdcard);
				editor.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseUpdateVersion(String value) {
		try {
			if (value != null) {
				// {"Result":"1","AppId":"0","PackageName":"tenant.guardts.house","VersionID":"2","MSG":"Success","IsEnforced":"True",
				// "APKUrl":"UpgradeFolder\\APK20170731135631.apk","IOSUrl":"","CreatedDate":"2017-07-31
				// 13:56:32"}
				JSONObject itemJsonObject = new JSONObject(value);
				String versionId = itemJsonObject.optString("VersionID");
				if (versionId != null) {
					int versionCode = Integer.parseInt(versionId);
					if (versionCode > mVersionCode) {
						String downloadUrl = itemJsonObject.optString("APKUrl");
						if (downloadUrl != null && downloadUrl.length() > 5) {
							CommonUtil.DOWLOAD_URL = CommonUtil.UPDATE_VERSION_HOST + downloadUrl;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getUserIdNo(String value) {
		HashMap<String, String> userInfo = null;
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				JSONObject itemJsonObject = array.optJSONObject(0);
				return itemJsonObject.optString("IDCard");
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private long exitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				GlobalUtil.shortToast(getApplication(), getString(R.string.press_again_to_exit),
						getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return false;

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.w("mingguo", "HomeActivity  onActivityResult result code  " + resultCode + "   requestcode  " + requestCode
				+ " data  " + data);
		// 处理扫描结果（在界面上显示）
		if (resultCode == RESULT_OK && requestCode == CommonUtil.mScanCodeRequestCode) {
			// getOpenDoorRequest("0201002200100002");
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			Log.e("mingguo", "scan  result  " + scanResult);
			// http://www.trackbike.cn/SafeCard/servlet/OAuthServlet?r=r&z=0&d=0201002200100003
			int pos = scanResult.lastIndexOf("=");
			String lockNo = scanResult.substring(pos + 1);
			Log.e("mingguo", "scan  result pos " + pos + " lockNo  " + lockNo);
			if (lockNo != null && lockNo.length() > 2) {
				showOpenDoorAlertDialog(lockNo + "");
			}
		} 
//		else if (resultCode == RESULT_OK && requestCode == CommonUtil.mSelectCityRequestCode) {
//			Bundle bundle = data.getExtras();
//			if (bundle != null) {
//				String selectedCity = bundle.getString("city");
//				Log.e("mingguo", "homeActivity  onActivity  selected city  " + selectedCity);
//				if (!TextUtils.isEmpty(selectedCity)) {
//					setSelectedCity(selectedCity);
//				}
//			}
//
//		}
	}

	public void setSelectedCity(String city) {
		mCity = city;
	}

	public String getSelectedCity() {
		return mCity;
	}

	@Override
	public void onStatusStart() {

	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
		}
		if (action.equals(mUserInfoAction)) {
			Message message = mHandler.obtainMessage();
			message.what = 100;
			message.obj = templateInfo;
			mHandler.sendMessage(message);
		} else if (action.equals(mUpdateAction)) {
			Message message = mHandler.obtainMessage();
			message.what = 200;
			message.obj = templateInfo;
			mHandler.sendMessageDelayed(message, 500);
		}else if (action.equals(mOpenDoorAction)){
			Message message = mHandler.obtainMessage();
			message.what = 300;
			message.obj = templateInfo;
			mHandler.sendMessageDelayed(message, 500);
		}
	}

}
