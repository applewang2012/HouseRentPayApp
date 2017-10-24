package tenant.guardts.house;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.baidu.location.LocationClient;

public class MapActivity extends Activity {
	public LocationClient mLocationClient = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);
		initView();
	}

	private void initView() {
		// 初始化LocationClient类
		mLocationClient = new LocationClient(getApplicationContext());
		// 注册监听函数
//		mLocationClient.registerLocationListener(myListener);
		//配置定位SDK参数
		
	}
	
	//实现BDAbstractLocationListener接口

	
	
	@Override
	protected void onStart() {
		super.onStart();
		//开启定位
		mLocationClient.start();
	}
}
