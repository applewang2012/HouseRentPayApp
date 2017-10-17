package tenant.guardts.house;

import java.io.File;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.gzt.faceid5sdk.DetectionAuthentic;
import com.gzt.faceid5sdk.listener.ResultListener;
import com.oliveapp.face.livenessdetectorsdk.utilities.algorithms.DetectedRect;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import tenant.guardts.house.util.BMapUtil;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.ScreenShotUtil;
import tenant.guardts.house.util.ViewUtil;

public class HomeActivity extends BaseActivity {

	private HoursePresenter mPresenter;
	// private String mLoginAction = "http://tempuri.org/ValidateLogin";
	private String mUpdateAction = "http://tempuri.org/CheckUpgrade";
	private String mOpenDoorAction = "http://tempuri.org/OpenDoor";
	private String mCanOpenDoorAction = "http://tempuri.org/CanOpenDoor";
	private String mXingeTokenAction = "http://tempuri.org/UpdateDeviceID";
	private String mIdentifyAction = "http://tempuri.org/IdentifyValidateLive";
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
	private HandlerThread myHandlerThread ;
	private Handler mSubHandler;
	private long exitTime;
	private String mLockNumber;
	private DetectionAuthentic authentic;
	private String mFaceCaptureString, mCaptureString;
	private String file;

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
		initData();
		initView();
		initHandler();
		mHandler.sendEmptyMessageDelayed(2000, 20);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	//解决重叠
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		
		if (mHouseFrament == null && fragment instanceof HouseFragment) {
			mHouseFrament = (HouseFragment) fragment;
		} else if (mMyFragment == null && fragment instanceof MyFragment) {
			mMyFragment = (MyFragment) fragment;
		} else if (mSurroundFragment == null && fragment instanceof SurroundFragment) {
			mSurroundFragment = (SurroundFragment) fragment;
		} else if (mHistoryFragment == null && fragment instanceof OrderFragment) {
			mHistoryFragment = (OrderFragment) fragment;
		}
	}


	private void initData(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
		mUserName = sharedata.getString("user_name", "");
		mPassword = sharedata.getString("user_password", "");
		bundle = new Bundle();
		bundle.putString("user_name", mUserName);
	}
	

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		initData();
	}

	
	
	private void startIndentifyProcess(){
		
		GlobalUtil.longToast(getApplication(),"开锁前需要实名认证，开始拍照！");
		Intent getPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		file = ScreenShotUtil.createScreenshotDirectory(HomeActivity.this);
		File out = new File(file);
		Uri uri = Uri.fromFile(out);
		getPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		getPhoto.putExtra("return-data", true);
		getPhoto.putExtra("camerasensortype", 2); 
		startActivityForResult(getPhoto, CommonUtil.mIndentifyUserRequestCode);
	}


	private void initView() {
		mOpenLockLoadingView = (View)findViewById(R.id.id_open_lock_layout);
		mOpenLockLoadingView.setVisibility(View.INVISIBLE);
		
		Button scanButton = (Button) findViewById(R.id.id_scan_rent_house);
		scanButton.setVisibility(View.VISIBLE);
		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mRegisterIdcard == null || CommonUtil.mRegisterIdcard.equals("")){
					Toast.makeText(HomeActivity.this, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(HomeActivity.this, LoginUserActivity.class));
					return;
				}
				if (!CommonUtil.mIsForceIdentifyUser){
					Intent openCameraIntent = new Intent(HomeActivity.this, CaptureActivity.class);
					startActivityForResult(openCameraIntent, CommonUtil.mScanCodeRequestCode);
				}else{
					startIndentifyProcess();
				}
				//强制活体
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
				if (CommonUtil.mRegisterIdcard == null || CommonUtil.mRegisterIdcard.equals("")) {
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
		mPresenter.readyPresentServiceParams(HomeActivity.this, url, mOpenDoorAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private void canOpenDoorRequest(String lockId, String idcard) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=CanOpenDoor";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCanOpenDoorAction));
		rpc.addProperty("lockId", lockId);
		rpc.addProperty("idCard", idcard);
		mPresenter.readyPresentServiceParams(HomeActivity.this, url, mCanOpenDoorAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void checkVersionUpdate() {
		mVersionCode = GlobalUtil.getVersionCode(getApplicationContext());
		String url = "http://www.guardts.com/UpgradeService/SystemUpgradeService.asmx?op=CheckUpgrade";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUpdateAction));
		rpc.addProperty("packageName", GlobalUtil.getPackageName(getApplicationContext()));
		rpc.addProperty("versionId", GlobalUtil.getVersionCode(getApplicationContext()));
		mPresenter.readyPresentServiceParams(HomeActivity.this, url, mUpdateAction, rpc);
		mPresenter.startPresentServiceTask(false);
	}

//	private void uploadXingeToken() {
//		if (CommonUtil.XINGE_TOKEN == null || CommonUtil.XINGE_TOKEN.equals("")){
//			return;
//		}
//		String url = CommonUtil.mUserHost + "services.asmx?op=UpdateDeviceID";
//		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mXingeTokenAction));
//		rpc.addProperty("userId", mUserName);
//		rpc.addProperty("deviceId", CommonUtil.XINGE_TOKEN);
//		mPresenter.readyPresentServiceParams(HomeActivity.this, url, mXingeTokenAction, rpc);
//		mPresenter.startPresentServiceTask(false);
//	}

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
			}else if (msg.what == 2000){
				checkVersionUpdate();
			}else if (msg.what == 110){
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					if (object != null){
						String compareResult = object.optString("verify_result");
						String result = object.optString("result");
						if (result != null && result.equals("0")){
							if (compareResult != null && compareResult.equals("0")){
								GlobalUtil.shortToast(getApplication(), CommonUtil.mRegisterRealName + " 身份认证成功 ,您可以扫描开锁入住了！", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
								Intent openCameraIntent = new Intent(HomeActivity.this, CaptureActivity.class);
								startActivityForResult(openCameraIntent, CommonUtil.mScanCodeRequestCode);
							}else{
								GlobalUtil.shortToast(getApplication(), CommonUtil.mRegisterRealName + " 身份认证失败  "+compareResult , getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
							}
						}else{
							GlobalUtil.shortToast(getApplication(), CommonUtil.mRegisterRealName + " 身份认证失败，请重试 ", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
						}
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (msg.what == 200) {
				if (msg.obj != null) {
					parseUpdateVersion((String) msg.obj);
					showUpdateVersionAlertDialog();
				}
//				uploadXingeToken();
			}else if (msg.what == 300){
				dimissOpenDoorLoading();
				JSONObject itemJsonObject;
				try {
					itemJsonObject = new JSONObject((String)msg.obj);
					String ret = itemJsonObject.optString("ret");
					if (ret != null) {
						if (ret.equals("0")){
							Toast.makeText(getApplicationContext(), ""+itemJsonObject.optString("msg"), Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), itemJsonObject.optString("msg"), Toast.LENGTH_LONG).show();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (msg.what == 301){
				dimissOpenDoorLoading();
				JSONObject itemJsonObject;
				try {
					itemJsonObject = new JSONObject((String)msg.obj);
					String ret = itemJsonObject.optString("ret");
					if (ret != null) {
						if (ret.equals("0")){
							showOpenDoorLoadingView();
							showOpenDoorAlertDialog(mLockNumber);
						}else{
							Toast.makeText(getApplicationContext(), itemJsonObject.optString("msg"), Toast.LENGTH_LONG).show();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (msg.what == 1000){
				dimissOpenDoorLoading();
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
		builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
			@Override

			public void onClick(DialogInterface dialog, int which) {
				
			}

		});
		builder.setCancelable(true);
		builder.show();
	}
	
	private void showOpenDoorLoadingView(){
		mOpenLockLoadingView.setVisibility(View.VISIBLE);
		ImageView circleImage = (ImageView)mOpenLockLoadingView.findViewById(R.id.imageView2);
		ObjectAnimator animator = ObjectAnimator.ofFloat(circleImage, "rotation", 0.0F,720.0F).setDuration(12000); //时针
		
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setInterpolator(new LinearInterpolator());;
		animator.start();
		
		ImageView circleImage2 = (ImageView)mOpenLockLoadingView.findViewById(R.id.imageView3);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(circleImage2, "rotation", 0.0F,720.0F).setDuration(5000);  //分针
		
		animator2.setRepeatCount(ObjectAnimator.INFINITE);
		animator2.setInterpolator(new LinearInterpolator());;
		animator2.start();
	}
	
	private void dimissOpenDoorLoading(){
		if (mOpenLockLoadingView != null){
			mOpenLockLoadingView.setVisibility(View.GONE);
		}
		
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
				Log.w("alertdialog", " �뱣�����ݣ�");
			}
		});
		builder.setCancelable(false);
		builder.show();
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
				CommonUtil.mBankName= itemJsonObject.optString("BankName");
				CommonUtil.mCardNo = itemJsonObject.optString("CardNO");
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
				Log.w("house", "parse house info " + array.length());
				JSONObject itemJsonObject = array.optJSONObject(0);
				return itemJsonObject.optString("IDCard");
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	

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
			mLockNumber = scanResult.substring(pos + 1);
			Log.e("mingguo", "scan  result pos " + pos + " lockNo  " + mLockNumber);
			if (mLockNumber != null && mLockNumber.length() > 2) {
				canOpenDoorRequest(mLockNumber, CommonUtil.mRegisterIdcard);
				//showOpenDoorAlertDialog(mLockNumber + "");
			}
		}else if (resultCode == RESULT_OK && requestCode == CommonUtil.mIndentifyUserRequestCode) {
			 Log.w("mingguo", "activity result  width data   "+data);
			 mSubHandler.sendEmptyMessage(1000);
			 startLiveIdentifyActivity();
		}
	}
	
	private void startLiveIdentifyActivity(){
		authentic = DetectionAuthentic.getInstance(HomeActivity.this, new ResultListener() {

		@Override
		public void onSDKUsingFail(String errorMessage, String errorCode) {
			// TODO Auto-generated method stub
			GlobalUtil.shortToast(getApplication(), errorMessage, getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			
		}
		
		@Override
		public void onIDCardImageCaptured(byte[] faceImages, DetectedRect arg1) {
			if(faceImages == null){
				GlobalUtil.shortToast(getApplication(), "image capture  无人脸", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}
		}
		
		@Override
		public void onFaceImageCaptured(byte[] faceImages) {
			if(faceImages == null){
				GlobalUtil.shortToast(getApplication(), "image capture  无人脸", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}
			
			mFaceCaptureString = android.util.Base64.encodeToString(faceImages, android.util.Base64.NO_WRAP);
			identifyUserInfo(mFaceCaptureString, mCaptureString);
		}
		});
	
		authentic.autenticateToCaptureAction(HomeActivity.this, CommonUtil.mRegisterRealName, CommonUtil.mRegisterIdcard);
	}
	
	private void identifyUserInfo(String faceStr, String screenshotStr){
		if (faceStr == null || screenshotStr == null){
			return;
		}
		Log.w("mingguo", "register interface  faceStr  "+faceStr.length()+"  screenshot   "+screenshotStr.length());
		Log.w("mingguo", "register interface  CommonUtil.mRegisterIdcard  "+CommonUtil.mRegisterIdcard+"  CommonUtil.mRegisterRealName  "+CommonUtil.mRegisterRealName);
		String identifyUrl = "http://www.guardts.com/ValidateService/IdentifyValidateService.asmx?op=IdentifyValidateLive";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mIdentifyAction));
		rpc.addProperty("idcard", CommonUtil.mRegisterIdcard);
		rpc.addProperty("name", CommonUtil.mRegisterRealName);
		rpc.addProperty("base64Str", faceStr);
		rpc.addProperty("picBase64Str", screenshotStr);
		mPresenter.readyPresentServiceParams(HomeActivity.this, identifyUrl, mIdentifyAction, rpc);
		mPresenter.startPresentServiceTask(true);
		
	}
	
	private void initHandler(){
    	//创建一个线程,线程名字：handler-thread
        myHandlerThread = new HandlerThread( "handler-thread") ;
        myHandlerThread.start();
        
        mSubHandler = new Handler(myHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int degree = BMapUtil.readPictureDegree(file);
                Bitmap rotationBitmap = BMapUtil.rotaingImageView(degree, BitmapFactory.decodeFile(file, null));
   			 	Log.w("mingguo", "onActivityResult  before compress image  "+rotationBitmap.getWidth()+" height  "+rotationBitmap.getHeight()+"  byte  "+rotationBitmap.getByteCount());
   			 	Bitmap newBitmap = BMapUtil.compressScale(rotationBitmap);
   			 	Log.w("mingguo", "onActivityResult  compress image  "+newBitmap.getWidth()+" height  "+newBitmap.getHeight()+"  byte  "+newBitmap.getByteCount());
   			 	mCaptureString = android.util.Base64.encodeToString(BMapUtil.Bitmap2Bytes(newBitmap), android.util.Base64.NO_WRAP);
                
            }
        };
        
    }

	public void setSelectedCity(String city) {
		mCity = city;
	}

	public String getSelectedCity() {
		return mCity;
	}


	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
		mHandler.sendEmptyMessageDelayed(1000, 50);
	}


	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mUpdateAction)) {
				Message message = mHandler.obtainMessage();
				message.what = 200;
				message.obj = templateInfo;
				mHandler.sendMessageDelayed(message, 50);
			}else if (action.equals(mOpenDoorAction)){
				Message message = mHandler.obtainMessage();
				message.what = 300;
				message.obj = templateInfo;
				mHandler.sendMessageDelayed(message, 50);
			}else if (action.equals(mCanOpenDoorAction)){
				Message message = mHandler.obtainMessage();
				message.what = 301;
				message.obj = templateInfo;
				mHandler.sendMessageDelayed(message, 10);
			}else if (action.equals(mIdentifyAction)){
				Message message = mHandler.obtainMessage();
				message.what = 110;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
			
		}
		
	}

}
