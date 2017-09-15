package tenant.guardts.house.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import tenant.guardts.house.R;

public class ViewUtil {
	private static View mLoadingView;
	public static void showLoadingView(Context context, View loadingView){
		mLoadingView = loadingView;
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	public static void dismissLoadingView(){
		if (mLoadingView != null) {
			
		}
	}
	
	public static void forceCloseSoftKeyborad(Activity activity){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);            
		 if(imm.isActive()&&activity.getCurrentFocus()!=null){
		    if (activity.getCurrentFocus().getWindowToken()!=null) {
		    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		    }             
		 }
	}
	
}
