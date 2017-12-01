package tenant.guardts.house;

import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public class PermissionActivity extends BaseActivity implements OnRequestPermissionsResultCallback {
	private static final int CAMERA_CODE = 123;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_main);
		if (Build.VERSION.SDK_INT >= 23) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
				startActivityForResult(new Intent(this, CaptureActivity.class), CommonUtil.mScanCodeRequestCode);
				//Toast.makeText(this, "check", Toast.LENGTH_LONG).show();
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_CODE);
			}
		}else{
			startActivityForResult(new Intent(this, CaptureActivity.class), CommonUtil.mScanCodeRequestCode);
		}
		
		/*
		 * if(!ActivityCompat.shouldShowRequestPermissionRationale(this,
		 * Manifest.permission.CAMERA)){ AlertDialog.Builder build=new
		 * AlertDialog.Builder(this);
		 * build.setMessage("请在\"设置-应用-连心锁-权限管理\"中开启相机权限，开通后你才可以使用扫描功能");
		 * build.setPositiveButton("去设置", new DialogInterface.OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * Intent intent = new Intent(Settings.ACTION_SETTINGS);
		 * startActivity(intent); } }); build.create().show(); }
		 */
	}

	@Override
	public void onRequestPermissionsResult(int arg0, String[] arg1, int[] arg2) {
		if (arg0 == CAMERA_CODE) {
			if (arg2[0] == PackageManager.PERMISSION_GRANTED) {
				startActivityForResult(new Intent(this, CaptureActivity.class), CommonUtil.mScanCodeRequestCode);
				finish();
				
			} else {

				if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
					AlertDialog.Builder build = new AlertDialog.Builder(this);
					build.setMessage("请在\"设置-应用-连心锁-权限管理\"中开启相机权限，开通后你才可以使用扫码，闪光灯功能");
					build.setPositiveButton("去设置", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Settings.ACTION_SETTINGS);
							startActivity(intent);
							finish();
						}
					});
					AlertDialog dialog = build.create();
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				} else {
					finish();
				}

			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == CommonUtil.mScanCodeRequestCode) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			Intent resultIntent = new Intent();
			Bundle bundle2 = new Bundle();
			bundle2.putString("result", scanResult);
			resultIntent.putExtras(bundle2);
			setResult(RESULT_OK, resultIntent);
			finish();
		}
		if (resultCode == RESULT_OK && requestCode == CommonUtil.mLockNumberRequestCode) {
			if (data != null)
			this.setResult(RESULT_OK, data);
			finish();
		}
	}

}
