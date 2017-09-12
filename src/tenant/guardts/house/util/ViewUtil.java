package tenant.guardts.house.util;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
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
}
