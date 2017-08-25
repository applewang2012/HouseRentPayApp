package tenant.guardts.house;


import tenant.guardts.house.R;
import tenant.guardts.house.util.CommonUtil;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends BaseActivity {
	private LinearLayout mLoading_data;
	private Bundle mDataBundle;
	private String mUsername;
	private String mPassword;
	private int mSuccessCount = 0;
	private ImageView mSplashBG;
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
		mHandler.sendEmptyMessageDelayed(200, 100);
		
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
					Intent intent = new Intent(WelcomeActivity.this, LoginUserActivity.class);
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
