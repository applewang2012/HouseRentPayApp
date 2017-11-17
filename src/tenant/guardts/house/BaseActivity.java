package tenant.guardts.house;

import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.ViewUtil;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class BaseActivity extends Activity implements DataStatusInterface{
	View mBaseLoadingView ;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 100){
				dismissActivityLoadingView(mBaseLoadingView);
				GlobalUtil.shortToast(getApplication(), "网络异常，请检查网络！"+msg.obj, getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}else if (msg.what == 1000){
				Activity parentActivity = (Activity)msg.obj;
				mBaseLoadingView = parentActivity.findViewById(R.id.id_data_loading);
				showActivityLoadingView(parentActivity, mBaseLoadingView);
			}else if (msg.what == 200){
				dismissActivityLoadingView(mBaseLoadingView);
			}
			
		}
	};
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Message msg = mHandler.obtainMessage();
		msg.what = 200;
		mHandler.sendMessage(msg);
		if (isFinishing()){
			templateInfo = null;
		}
	}

	@Override
	public void onStatusStart(Activity loading) {
		Message msg = mHandler.obtainMessage();
		msg.what = 1000;
		msg.obj = loading;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onStatusError(String action, String error) {
		Message msg = mHandler.obtainMessage();
		msg.what = 100;
		msg.obj = action + " " + error;
		mHandler.sendMessage(msg);
	}
	
	public void backFinish(View view){
		//ViewUtil.forceCloseSoftKeyborad(BaseActivity.this);
		finish();
	}
	
	private void showActivityLoadingView(Context ctx, View loadingView){
		if (loadingView != null) {
			loadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) loadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(ctx, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissActivityLoadingView(View loadingView){
		if (loadingView != null) {
			loadingView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		ViewUtil.forceCloseSoftKeyborad(BaseActivity.this);
		super.onDestroy();
		
	}
	
	

}
