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
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		
		setContentView(R.layout.welcome_main);
		
		SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
		mUsername = sharedata.getString("user_name", "");
		mPassword = sharedata.getString("user_password", "");
		CommonUtil.mUserHost = sharedata.getString("user_host", "");
	    
	    XGPushConfig.enableDebug(this, true);
	    registerXinge();
		mHandler.sendEmptyMessageDelayed(200, 300);
		
	}
	
	private void commonServiceInterface(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
	    	String url = "http://www.guardts.com/commonservice/commonservices.asmx?op=GetAreas";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCommonServiceAction));
			rpc.addProperty("status", "1");
			mPresenter.readyPresentServiceParams(this, url, mCommonServiceAction, rpc);
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
						Log.w("mingguo",
								"+++ register push sucess. token:" + data);
						m.obj = "+++ register push sucess. token:" + data;
						m.sendToTarget();
					}

					@Override
					public void onFail(Object data, int errCode, String msg) {
						Log.w("mingguo",
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
		  AlertDialog.Builder builder =new AlertDialog.Builder(WelcomeActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle(title);
		  builder.setIcon(android.R.drawable.ic_dialog_info);
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
			    
			    Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
				intent.putExtra("user_name", mUsername);
				intent.putExtra("user_password", mPassword);
				startActivity(intent);
				finish();
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
				Log.w("mingguo",  "信鸽注册结果 "+XGPushConfig.getToken(theActivity));
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
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				if (CommonUtil.mUserHost == null || CommonUtil.mUserHost.equals("")){
			    	commonServiceInterface();
			    }else{
			    	Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
					intent.putExtra("user_name", mUsername);
					intent.putExtra("user_password", mPassword);
					startActivity(intent);
					finish();
			    }
//				if (mUsername != null && !mUsername.equals("") && mPassword != null && !mPassword.equals("")){
					/*Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
					intent.putExtra("user_name", mUsername);
					intent.putExtra("user_password", mPassword);
					startActivity(intent);*/
//				}else{
//					Intent intent = new Intent(WelcomeActivity.this, RegisterUserActivity.class);
//					startActivity(intent);
//				}
				break;
			case 200:
				changeAlpha();
				mHandler.sendEmptyMessageDelayed(100, 4000);
				break;
			case 110:
				showSelectAlertDialog("请选择所在区域", parseCommonService((String)msg.obj));
				break;
			case 202:
				
			
			default:
				break;
			}
		}
		
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		super.onStatusSuccess(action, templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mCommonServiceAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 110;
				msg.obj = templateInfo;
				mHandler.sendMessage(msg);
			}
		}
	}

	
	
	
}
