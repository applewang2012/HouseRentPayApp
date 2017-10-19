package tenant.guardts.house;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;

public class AboutUsActivity extends BaseActivity {

	private String mUpdateAction="http://tempuri.org/CheckUpgrade";
	private HoursePresenter mPresenter;
	private int mVersionCode;
	private String mVersinName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_about_us);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("关于我们");
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		TextView currentVersion = (TextView)findViewById(R.id.id_about_us_current_version);
		currentVersion.setText(currentVersion.getText()+GlobalUtil.getVersionName(getApplicationContext()));
		Button checkVersion = (Button)findViewById(R.id.id_about_us_check_version);
		checkVersion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkVersionUpdate();
			}
		});
		Button serviceContent = (Button)findViewById(R.id.id_about_us_service_content);
		serviceContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent loadIntent = new Intent(AboutUsActivity.this, LoadUrlTestActivity.class);
				loadIntent.putExtra("url", "http://mg.weiyuzhu.cn/");
				loadIntent.putExtra("tab_name", "用户服务协议");
				startActivity(loadIntent);
				
			}
		});
	}
	
	private void checkVersionUpdate(){
		mVersionCode = GlobalUtil.getVersionCode(getApplicationContext());
		String url = "http://www.guardts.com/UpgradeService/SystemUpgradeService.asmx?op=CheckUpgrade";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUpdateAction));
		rpc.addProperty("packageName", GlobalUtil.getPackageName(getApplicationContext()));
		rpc.addProperty("versionId", GlobalUtil.getVersionCode(getApplicationContext()));
		mPresenter.readyPresentServiceParams(AboutUsActivity.this, url, mUpdateAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private  void parseUpdateVersion(String value) {
		try{
			if (value != null){
				//{"Result":"1","AppId":"0","PackageName":"tenant.guardts.house","VersionID":"2","MSG":"Success","IsEnforced":"True",
					//"APKUrl":"UpgradeFolder\\APK20170731135631.apk","IOSUrl":"","CreatedDate":"2017-07-31 13:56:32"}
					JSONObject itemJsonObject = new JSONObject(value);
					String versionId = itemJsonObject.optString("VersionID");
					if (versionId != null){
						int versionCode = Integer.parseInt(versionId);
						if (versionCode > mVersionCode){
							String downloadUrl = itemJsonObject.optString("APKUrl");
							if (downloadUrl != null && downloadUrl.length() > 5){
								CommonUtil.DOWLOAD_URL = CommonUtil.UPDATE_VERSION_HOST+downloadUrl;
							}
						}
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showUpdateVersionAlertDialog() {
		if (CommonUtil.DOWLOAD_URL == null || CommonUtil.DOWLOAD_URL.equals("")){
			Log.w("mingguo", "home activity  delete installed file  "+CommonUtil.deleteInstalledApkFile());
			Toast.makeText(AboutUsActivity.this, "当前版本已经最新", Toast.LENGTH_LONG).show();
			return;
		}
		
		  AlertDialog.Builder builder =new AlertDialog.Builder(AboutUsActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("检测到新版本，是否升级？");
		  builder.setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {
		         @Override  
		  
		         public void onClick(DialogInterface dialog, int which) {
		        	 startActivity(new Intent(AboutUsActivity.this, DownloadAppActivity.class));
		        	 finish();
		         }  
			
		});
		builder.setCancelable(true);
		builder.show();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 200){
				if (msg.obj != null){
					parseUpdateVersion((String)msg.obj);
					showUpdateVersionAlertDialog();
				}
			}
		}
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		super.onStatusSuccess(action, templateInfo);
		Log.w("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		if (action.equals(mUpdateAction)){
			Message message = mHandler.obtainMessage();
			message.what = 200;
			message.obj = templateInfo;
			mHandler.sendMessageDelayed(message, 500);
		}
	}
	
	
	
}
