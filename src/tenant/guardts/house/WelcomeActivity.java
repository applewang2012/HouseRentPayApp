package tenant.guardts.house;


import java.lang.ref.WeakReference;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;

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
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends BaseActivity {
	private LinearLayout mLoading_data;
	private Bundle mDataBundle;
	private String mUsername;
	private String mPassword;
	private int mSuccessCount = 0;
	private ImageView mSplashBG;
	private Message m;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		
		setContentView(R.layout.welcome_main);
		
		SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
		mUsername = sharedata.getString("user_name", "");
		mPassword = sharedata.getString("user_password", "");
	    if (mUsername != null && !mUsername.equals("")){
//	    	CommonUtil.mUserLoginName = sharedata.getString("user_name", "");
//	    	CommonUtil.mRegisterRealName = sharedata.getString("user_realname", "");
//	    	CommonUtil.mRegisterIdcard = sharedata.getString("user_idcard", "");
	    }
	    XGPushConfig.enableDebug(this, true);
	    registerXinge();
		mHandler.sendEmptyMessageDelayed(200, 300);
		
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
				Log.w("mingguo", msg.obj.toString());
//				TextView textView = (TextView) theActivity
//						.findViewById(R.id.deviceToken);
//				textView.setText(XGPushConfig.getToken(theActivity));
				Log.w("mingguo",  "信鸽注册结果 "+XGPushConfig.getToken(theActivity));
			}
			// XGPushManager.registerCustomNotification(theActivity,
			// "BACKSTREET", "BOYS", System.currentTimeMillis() + 5000, 0);
		}
	}

	
	public void changeAlpha() {
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
				
//				if (mUsername != null && !mUsername.equals("") && mPassword != null && !mPassword.equals("")){
					Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
					intent.putExtra("user_name", mUsername);
					intent.putExtra("user_password", mPassword);
					startActivity(intent);
//				}else{
//					Intent intent = new Intent(WelcomeActivity.this, RegisterUserActivity.class);
//					startActivity(intent);
//				}
				finish();
				break;
			case 200:
				changeAlpha();
				mHandler.sendEmptyMessageDelayed(100, 5000);
				break;
			case 201:
				
			case 202:
				
			
			default:
				break;
			}
			
		}
		
	};

	
	
	
}
