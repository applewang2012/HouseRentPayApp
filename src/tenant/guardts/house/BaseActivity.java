package tenant.guardts.house;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.util.GlobalUtil;

public class BaseActivity extends Activity implements DataStatusInterface{
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			GlobalUtil.shortToast(getApplication(), "网络异常，请检查网络！"+msg.obj, getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
		}
		
	};
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusError(String action, String error) {
		Message msg = mHandler.obtainMessage();
		msg.what = 100;
		msg.obj = error;
		mHandler.sendMessage(msg);
	}
	
	public void backFinish(View view){
		finish();
	}

}
