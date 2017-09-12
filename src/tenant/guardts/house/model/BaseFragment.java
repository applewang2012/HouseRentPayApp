package tenant.guardts.house.model;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.AddHouseInfoActivity;
import tenant.guardts.house.LoginUserActivity;
import tenant.guardts.house.ModifyPasswordActivity;
import tenant.guardts.house.PersonalInfoActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.WalletActivity;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
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
		
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		
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
