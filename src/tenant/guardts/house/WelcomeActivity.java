package tenant.guardts.house;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;

public class WelcomeActivity extends BaseActivity {
	private LinearLayout mLoading_data;
	private Bundle mDataBundle;
	private String mUsername;
	private String mPassword;
	private int mSuccessCount = 0;
	private ImageView mSplashBG;
	private Message m;
	private HoursePresenter mPresenter;
	private String mCommonServiceAction = "http://tempuri.org/GetAreas";
	private String mHelloWordAction = "http://tempuri.org/HelloWord";
	private String mUserInfoAction = "http://tempuri.org/GetUserInfo";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		
		setContentView(R.layout.welcome_main);
		
		SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
		mUsername = sharedata.getString("user_name", "");
		mPassword = sharedata.getString("user_password", "");
		//CommonUtil.mUserHost = sharedata.getString("user_host", "");
		CommonUtil.mUserHost = "http://qxw2332340157.my3w.com/Services.asmx/";
		mPresenter = new HoursePresenter(getApplicationContext(), this);
	    XGPushConfig.enableDebug(this, true);
	    registerXinge();
		mHandler.sendEmptyMessageDelayed(200, 30);
		//testHelloword();
	}
	
	private void commonServiceInterface(){
		
	    	String url = "http://www.guardts.com/commonservice/commonservices.asmx?op=GetAreas";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCommonServiceAction));
			rpc.addProperty("status", "1");
			mPresenter.readyPresentServiceParams(this, url, mCommonServiceAction, rpc);
			mPresenter.startPresentServiceTask(false);
	}
	
	private void getUserInfo() {
		if (mUsername == null || mUsername.equals("")){
			Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
			intent.putExtra("user_name", "");
			intent.putExtra("user_password", "");
			startActivity(intent);
			finish();
			return;
		}
		String url = CommonUtil.mUserHost + "services.asmx?op=GetUserInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUserInfoAction));
		rpc.addProperty("username", mUsername);
		rpc.addProperty("deviceId", CommonUtil.XINGE_TOKEN);
		mPresenter.readyPresentServiceParams(this, url, mUserInfoAction, rpc);
		mPresenter.startPresentServiceTask(false);
	}
	
	private void testHelloword(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
    	String url = "http://qxw2332340157.my3w.com/Services.asmx?op=HelloWord";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mHelloWordAction));
		rpc.addProperty("user", "1");
		rpc.addProperty("pass", "1");
		rpc.addProperty("timestamp", "");
		rpc.addProperty("token", "1");
		mPresenter.readyPresentServiceParams(this, url, mHelloWordAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		XGPushClickedResult click = XGPushManager.onActivityStarted(this);
	}



	private void registerXinge(){
		// 1.获取设备Token
				Handler handler = new HandlerExtension(WelcomeActivity.this);
				m = handler.obtainMessage();
				
		XGPushManager.registerPush(getApplicationContext(),
				new XGIOperateCallback() {
					@Override
					public void onSuccess(Object data, int flag) {
						LogUtil.w("mingguo",
								"+++ register push sucess. token:" + data);
						m.obj = "+++ register push sucess. token:" + data;
						m.sendToTarget();
					}

					@Override
					public void onFail(Object data, int errCode, String msg) {
						LogUtil.w("mingguo",
								"+++ register push fail. token:" + data
										+ ", errCode:" + errCode + ",msg:"
										+ msg);

						m.obj = "+++ register push fail. token:" + data
								+ ", errCode:" + errCode + ",msg:" + msg;
						m.sendToTarget();
					}
				});
	}
	
	private void showSelectAlertDialog(final String title, final List<String[]> data) { 
		if (data != null && data.size()> 0 && data.get(0).length == 1){
			SharedPreferences sharedata = getApplication().getSharedPreferences("user_info", 0);
			SharedPreferences.Editor editor = sharedata.edit();
			editor.putString("area", data.get(0)[0]);
			editor.putString("user_host", data.get(1)[0]);
			editor.commit();
			CommonUtil.mUserArea = data.get(0)[0];
			CommonUtil.mUserHost = data.get(1)[0];
			LogUtil.e("mingguo", "user host  "+CommonUtil.mUserHost);
//			Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
//			intent.putExtra("user_name", mUsername);
//			intent.putExtra("user_password", mPassword);
//			startActivity(intent);
//			finish();
			getUserInfo();
			return;
		}
		
		  AlertDialog.Builder builder =new AlertDialog.Builder(WelcomeActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle(title);
		  //ic_dialog_alert);
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
			    LogUtil.e("mingguo", "user host  "+CommonUtil.mUserHost);
			    getUserInfo();
//			    Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
//				intent.putExtra("user_name", mUsername);
//				intent.putExtra("user_password", mPassword);
//				startActivity(intent);
//				finish();
			}
		});
		builder.setCancelable(false);
		builder.show();
		
	}
	
	private static class HandlerExtension extends Handler {
		WeakReference<WelcomeActivity> mActivity;

		HandlerExtension(WelcomeActivity activity) {
			mActivity = new WeakReference<WelcomeActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			WelcomeActivity theActivity = mActivity.get();
			if (theActivity == null) {
				theActivity = new WelcomeActivity();
			}
			if (msg != null) {
//				TextView textView = (TextView) theActivity
//						.findViewById(R.id.deviceToken);
//				textView.setText(XGPushConfig.getToken(theActivity));
				LogUtil.w("mingguo",  "信鸽注册结果 "+XGPushConfig.getToken(theActivity));
				CommonUtil.XINGE_TOKEN = XGPushConfig.getToken(theActivity);
			}
			
		}
	}

	private  List<String[]> parseCommonService(String value) {
		String [] areaName = null;
		String [] areaHost;
		List<String[]> list = new ArrayList<>();
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				areaName = new String[array.length()];
				areaHost = new String[array.length()];
				for (int item = 0; item < array.length(); item++){
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
	
	
	private void changeAlpha() {
        	FrameLayout alphView = (FrameLayout)findViewById(R.id.id_splash_show_logo_animation);
        	//alphView.setVisibility(View.INVISIBLE);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
            alphView.startAnimation(alphaAnimation);//开始动画
            alphaAnimation.setFillAfter(true);//动画结束后保持状态
            alphaAnimation.setDuration(3500);//动画持续时间，单位为毫秒
           
    }
	
	private void parseUserInfo(String value) {
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
				SharedPreferences sharedata = getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
				editor.putString("user_realname", CommonUtil.mRegisterRealName);
				editor.putString("user_idcard", CommonUtil.mRegisterIdcard);
				editor.commit();
				
				Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
				intent.putExtra("user_name", mUsername);
				intent.putExtra("user_password", mPassword);
				startActivity(intent);
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				LogUtil.e("mingguo", "user host  "+CommonUtil.mUserHost);
				if (CommonUtil.mUserHost == null || CommonUtil.mUserHost.equals("")){
			    	commonServiceInterface();
			    }else{
			    	getUserInfo();
			    }
				break;
			case 200:
				changeAlpha();
				mHandler.sendEmptyMessageDelayed(100, 4000);
				break;
			case 110:
				showSelectAlertDialog("请选择所在区域", parseCommonService((String)msg.obj));
				
				break;
			case 101:
				if (msg.obj != null) {
					parseUserInfo((String) msg.obj);
				}
				break;
			default:
				break;
			}
		}
		
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "onstatus sucess  "+action+" info "+templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mCommonServiceAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 110;
				msg.obj = templateInfo;
				mHandler.sendMessage(msg);
			}else if (action.equals(mUserInfoAction)){
				Message message = mHandler.obtainMessage();
				message.what = 101;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}

	
	
	
}
