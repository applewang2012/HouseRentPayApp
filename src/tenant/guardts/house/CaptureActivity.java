package tenant.guardts.house;

import java.io.IOException;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import tenant.guardts.house.camera.CameraManager;
import tenant.guardts.house.decoding.CaptureActivityHandler;
import tenant.guardts.house.decoding.InactivityTimer;
import tenant.guardts.house.model.ActionOperationInterface;
import tenant.guardts.house.model.ScanQrCodeFragment;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.zxingview.ViewfinderView;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity {

	private CaptureActivityHandler handler;
	// private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button cancelScanButton;
	private boolean isLighting = true;
	private ScanQrCodeFragment mQrFragment;
	private DoorNumberLockFragment mOpenLockNumber;
	private boolean mFlashLightOn = false;
	

	// private ActionOperationInterface mAction;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan_open_door);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.titlebar);
		// TextView titlebar = (TextView) findViewById(R.id.id_titlebar);
		// titlebar.setText("扫一扫开锁");
			

			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			if (mQrFragment == null) {
				mQrFragment = new ScanQrCodeFragment();
				fragmentTransaction.add(R.id.id_scan_qrcode_content, mQrFragment);
				fragmentTransaction.commitAllowingStateLoss();
				mQrFragment.setFlashLightStatus(mFlashLightOn);
				mQrFragment.setFragmentActionListener(new ActionOperationInterface() {

					@Override
					public void onPreFragment() {

					}

					@Override
					public void onNextFragment() {
						FragmentTransaction newTransaction = getFragmentManager().beginTransaction();
						hideAllFragments(newTransaction);
						if (mOpenLockNumber == null) {
							mOpenLockNumber = new DoorNumberLockFragment();
							newTransaction.add(R.id.id_scan_qrcode_content, mOpenLockNumber);
							newTransaction.commitAllowingStateLoss();
							mOpenLockNumber.setFlashLightStatus(mFlashLightOn);
							mOpenLockNumber.setFragmentActionListener(new ActionOperationInterface() {

								@Override
								public void onPreFragment() {
									// TODO Auto-generated method stub
									FragmentTransaction backPreTransction = getFragmentManager().beginTransaction();
									hideAllFragments(backPreTransction);
									if (mQrFragment != null) {
										backPreTransction.show(mQrFragment);
										backPreTransction.commitAllowingStateLoss();
										mQrFragment.setFlashLightStatus(mFlashLightOn);
									}
								}

								@Override
								public void onNextFragment() {
									// TODO Auto-generated method stub

								}
							});
						} else {
							newTransaction.show(mOpenLockNumber);
							newTransaction.commitAllowingStateLoss();
							mOpenLockNumber.setFlashLightStatus(mFlashLightOn);
						}
					}
				});
			} else {
				fragmentTransaction.show(mQrFragment);
				fragmentTransaction.commitAllowingStateLoss();
				mQrFragment.setFlashLightStatus(mFlashLightOn);
			}
		

	}

	private void hideAllFragments(FragmentTransaction transaction) {
		if (mQrFragment != null && !mQrFragment.isHidden()) {
			transaction.hide(mQrFragment);
		}
		if (mOpenLockNumber != null && !mOpenLockNumber.isHidden()) {
			transaction.hide(mOpenLockNumber);
		}

	}

	public boolean setFlashLightOn() {
		mQrFragment.openOrCloseFlashLight();
		mFlashLightOn = !mFlashLightOn;
		return mFlashLightOn;
	}

	public boolean getFlashLightStatus() {
		return mFlashLightOn;
	}

	private boolean checkFlashlight() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			Toast.makeText(this, "当前设备没有闪光灯", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// SurfaceView surfaceView = (SurfaceView)
		// findViewById(R.id.preview_view);
		// SurfaceHolder surfaceHolder = surfaceView.getHolder();
		// if (hasSurface) {
		// initCamera(surfaceHolder);
		// } else {
		// surfaceHolder.addCallback(this);
		// surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// }
		// decodeFormats = null;
		// characterSet = null;
		//
		// playBeep = true;
		// AudioManager audioService = (AudioManager)
		// getSystemService(AUDIO_SERVICE);
		// if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		// {
		// playBeep = false;
		// }
		// initBeepSound();
		// vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		// if (handler != null) {
		// handler.quitSynchronously();
		// handler = null;
		// }
		// CameraManager.get().closeDriver();
		mFlashLightOn = false;
	}

	@Override
	protected void onDestroy() {
		// inactivityTimer.shutdown();

		super.onDestroy();
	}

	/**
	 * Handler scan result
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		mQrFragment.handleDecode(result, barcode);
		// inactivityTimer.onActivity();
		// playBeepSoundAndVibrate();
		// String resultString = result.getText();
		// // FIXME
		// if (resultString.equals("")) {
		// GlobalUtil.shortToast(getApplication(), "Scan failed  !",
		// getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
		// } else {
		// // System.out.println("Result:"+resultString);
		// Intent resultIntent = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.putString("result", resultString);
		// resultIntent.putExtras(bundle);
		// this.setResult(RESULT_OK, resultIntent);
		// }
		// CaptureActivity.this.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == CommonUtil.mLockNumberRequestCode) {
			if (data != null)
				// Bundle bundle = data.getExtras();
				// String scanResult = bundle.getString("result");
				LogUtil.e("mingguo", "capture activity  scan  result  " + data);
			// http://www.trackbike.cn/SafeCard/servlet/OAuthServlet?r=r&z=0&d=020
			// 100 220 010 000 3

			// Intent resultIntent = new Intent();
			// Bundle activityBundel = new Bundle();
			// activityBundel.putString("result", scanResult);
			// resultIntent.putExtras(resultIntent);
			this.setResult(RESULT_OK, data);
			CaptureActivity.this.finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mOpenLockNumber != null && mOpenLockNumber.isVisible()) {
				FragmentTransaction backTransaction = getFragmentManager().beginTransaction();
				hideAllFragments(backTransaction);
				if (mQrFragment != null) {
					backTransaction.show(mQrFragment);
					backTransaction.commitAllowingStateLoss();
					mQrFragment.setFlashLightStatus(mFlashLightOn);
					return false;
				}
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	// @Override
	// public void surfaceChanged(SurfaceHolder holder, int format, int width,
	// int height) {
	//
	// }
	//
	// @Override
	// public void surfaceCreated(SurfaceHolder holder) {
	// if (!hasSurface) {
	// hasSurface = true;
	// initCamera(holder);
	// }
	//
	// }
	//
	// @Override
	// public void surfaceDestroyed(SurfaceHolder holder) {
	// hasSurface = false;
	// if (mCamera != null) {
	// CameraManager.stopPreview();
	// }
	// }

	public ViewfinderView getViewfinderView() {
		return mQrFragment.getViewfinderView();
	}

	public Handler getHandler() {
		return mQrFragment.getHandler();
	}

	public void drawViewfinder() {
		mQrFragment.getViewfinderView().drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	private CheckBox mNumberButton, lighting;
	private Camera mCamera;
	private Parameters mParameters;

	

}