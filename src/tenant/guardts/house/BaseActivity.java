package tenant.guardts.house;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.util.GlobalUtil;

public class BaseActivity extends Activity implements DataStatusInterface{
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			GlobalUtil.shortToast(getApplication(), "网络异常，请检查网络！", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
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
		mHandler.sendEmptyMessage(100);
	}
	
	

}
