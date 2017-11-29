package tenant.guardts.house;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.LogUtil;

public class AboutUsActivity extends BaseActivity {

	private String mUpdateAction="http://tempuri.org/CheckUpgrade";
	private HoursePresenter mPresenter;
	private int mVersionCode;
	private String mVersinName;
	private String mCheckTestUrl = "http://39.106.19.37/check/phone/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_about_us);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView titlebar  = (TextView)findViewById(R.id.id_titlebar);
		titlebar.setText("关于我们");
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
//				Intent loadIntent = new Intent(AboutUsActivity.this, LoadUrlTestActivity.class);
//				loadIntent.putExtra("url", "http://mg.weiyuzhu.cn/");
//				loadIntent.putExtra("tab_name", "用户服务协议");
//				startActivity(loadIntent);
				
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse("http://39.106.19.37:81/file/houseservice.php");   
				intent.setData(content_url);  
				startActivity(intent);
				
			}
		});
		
		ImageView checkTest = (ImageView)findViewById(R.id.id_about_us_check_test_icon);
		checkTest.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if (CommonUtil.mUserLoginName != null && CommonUtil.mUserLoginName.length() == 11){
					requestCheckPhone(mCheckTestUrl+CommonUtil.mUserLoginName);
				}
				return false;
			}
		});
	}
	
	private  void requestCheckPhone(final String checkUrl) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				InputStream inputStream = null;
		    	FileOutputStream fos = null;
		        try {  
		        	
		            URL url = new URL(checkUrl);  
		            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
		            if (null == connection) {  
		                return ;  
		            }  
		            connection.setRequestMethod("GET");
		            connection.setConnectTimeout(8000);
					connection.setInstanceFollowRedirects(true);
					connection.setRequestProperty("Content-type", "text/html");
					connection.setDoInput(true);
					connection.connect();
					
					inputStream = connection.getInputStream();
					if (inputStream == null) {
						return ;
					}
					
					 int responseCode = connection.getResponseCode();  
		             if (responseCode == 200) {  
		            	 String backValue = changeInputStream(inputStream, "UTF-8");
		            	 if (backValue != null){
		            		 try {
		     					JSONObject json = new JSONObject(backValue);
		     					if (json != null){
		     						String ret = json.optString("ret");
		     						if (ret != null){
		     							Message msg = mHandler.obtainMessage();
		     							msg.what = 99;
		     							msg.obj = json.optString("msg");
		     							mHandler.sendMessage(msg);
		     							if (ret.equals("0")){
		     								CommonUtil.verify_code_test = true;
		     								CommonUtil.version_pay_test = true;
		     							}else{
		     								
		     							}
		     						}
		     					}
		     				} catch (JSONException e) {
		     					// TODO Auto-generated catch block
		     					e.printStackTrace();
		     				}
		            	 }
		            	 
		             }
					
		        } catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}

						if (inputStream != null) {
							inputStream.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}).start();
        
    }  
	
	private String changeInputStream(InputStream inputStream,  String encode) {  
  
        // 内存流  
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
        byte[] data = new byte[1024];  
        int len = 0;  
        String result = null;  
        if (inputStream != null) {  
            try {  
                while ((len = inputStream.read(data)) != -1) {  
                    byteArrayOutputStream.write(data, 0, len);  
                }  
                result = new String(byteArrayOutputStream.toByteArray(), encode);  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return result;  
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
			LogUtil.w("mingguo", "home activity  delete installed file  "+CommonUtil.deleteInstalledApkFile(this));
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
			}else if (msg.what == 99){
				Toast.makeText(AboutUsActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		if (action.equals(mUpdateAction)){
			Message message = mHandler.obtainMessage();
			message.what = 200;
			message.obj = templateInfo;
			mHandler.sendMessageDelayed(message, 500);
		}
	}
	
	
	
}
