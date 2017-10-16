package tenant.guardts.house.model;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import tenant.guardts.house.R;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.util.GlobalUtil;

public class BaseFragment extends Fragment implements DataStatusInterface {
	View mFragmentLoadingView ;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 100){
				dismissLoadingView(mFragmentLoadingView);
				GlobalUtil.shortToast(getActivity().getApplicationContext(), "网络异常，请检查网络！"+msg.obj, getResources().getDrawable(R.drawable.ic_dialog_no));
			}else if (msg.what == 1000){
				Activity parentActivity = (Activity)msg.obj;
				mFragmentLoadingView = parentActivity.findViewById(R.id.id_data_loading);
				showLoadingView(parentActivity, mFragmentLoadingView);
			}else if (msg.what == 200){
				dismissLoadingView(mFragmentLoadingView);
			}
			
		}
	};
	
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		Message msg = mHandler.obtainMessage();
		msg.what = 200;
		mHandler.sendMessage(msg);
		if (getActivity().isFinishing()){
			templateInfo = null;
		}
	}

	@Override
	public void onStatusStart(Activity activity) {
		// TODO Auto-generated method stub
		Message msg = mHandler.obtainMessage();
		msg.what = 1000;
		msg.obj = activity;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		Message msg = mHandler.obtainMessage();
		msg.what = 100;
		msg.obj = action + " " + error;
		mHandler.sendMessage(msg);
	}

	private void showLoadingView(Context ctx, View loadingView){
		if (loadingView != null) {
			loadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) loadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(ctx, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(View loadingView){
		if (loadingView != null) {
			loadingView.setVisibility(View.INVISIBLE);
		}
	}


}
