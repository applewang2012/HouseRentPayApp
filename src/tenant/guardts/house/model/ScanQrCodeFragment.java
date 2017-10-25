package tenant.guardts.house.model;

import java.io.IOException;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import tenant.guardts.house.CaptureActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.camera.CameraManager;
import tenant.guardts.house.decoding.CaptureActivityHandler;
import tenant.guardts.house.decoding.InactivityTimer;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.zxingview.ViewfinderView;

public class ScanQrCodeFragment extends BaseFragment implements Callback{


	private Context mContext;
	private View mRootView;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private CheckBox mNumberButton;
	private CaptureActivityHandler handler;
	private ActionOperationInterface mAction;
	private ImageView mFlashIcon;
	private TextView mFlashText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
//		getActivity().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_titlebar_home);
//		mTitlebarContent  = (FrameLayout) getActivity().getWindow().findViewById(R.id.id_title_bar_home_content);
		CameraManager.init(getActivity().getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.camera, container, false);
		
		initView();
		initEvent();
		return mRootView;
	}
	
	public void setFlashLightStatus(final boolean status){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (status){
					mFlashIcon.setBackgroundResource(R.drawable.flash_checked);
					mFlashText.setText("关闭手电筒");
					mFlashText.setTextColor(Color.parseColor("#337ffd"));
				}else{
					mFlashIcon.setBackgroundResource(R.drawable.flash_normal);
					mFlashText.setText("打开手电筒");
					mFlashText.setTextColor(Color.parseColor("#ffffff"));
				}
			}
		}, 200);
		
	}
	
	public  void setFragmentActionListener(ActionOperationInterface action) {
		mAction = action;
	}
	
	private void initView(){
		TextView titlebar = (TextView) mRootView.findViewById(R.id.id_titlebar);
		titlebar.setText("扫一扫开锁");
		
		viewfinderView = (ViewfinderView) mRootView.findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(getActivity());
		mNumberButton = (CheckBox)mRootView.findViewById(R.id.id_open_lock_number_button);
		LinearLayout flashButton = (LinearLayout)mRootView.findViewById(R.id.capture_flash_button);
		mFlashIcon = (ImageView)mRootView.findViewById(R.id.capture_flash_icon);
		mFlashText = (TextView)mRootView.findViewById(R.id.capture_flash_text);
		flashButton.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				if (((CaptureActivity) getActivity()).setFlashLightOn()){
					mFlashIcon.setBackgroundResource(R.drawable.flash_checked);
					mFlashText.setTextColor(Color.parseColor("#337ffd"));
					mFlashText.setText("关闭手电筒");
				}else{
					mFlashIcon.setBackgroundResource(R.drawable.flash_normal);
					mFlashText.setTextColor(Color.parseColor("#ffffff"));
					mFlashText.setText("打开手电筒");
				}
			}
		});
		
		
		mNumberButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//startActivityForResult(new Intent(mContext, DoorNumberLockActivity.class), CommonUtil.mLockNumberRequestCode);
				mAction.onNextFragment();
			}
		});
	}
	
	
	

	private void initEvent() {

		

	}

	@Override
	public void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) mRootView.findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}
	
	public boolean openOrCloseFlashLight(){
		mCamera = CameraManager.getCamera();
        mParameters = mCamera.getParameters();
       
        if (mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
            return true;
        } else { 
        	mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        	mCamera.setParameters(mParameters);
        	
        	return false;
        }
	}
	
	public boolean getFlashLightStatus(){
		mCamera = CameraManager.getCamera();
		if (mCamera != null){
			mParameters = mCamera.getParameters();
		       
	        if (mParameters != null && mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
	        	return false;
	        }else{
	        	return true;
	        }
		}
        return false;
	}
	
	
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		LogUtil.e("mingguo", "scan qr code  onAttach ");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LogUtil.w("mingguo", "scan qr code  onPause  ");
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}
	
	

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
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
			handler = new CaptureActivityHandler((CaptureActivity)getActivity(), decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		hasSurface = false;
		if (mCamera != null) {
            CameraManager.stopPreview();
        }
	}
	
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
	
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		// FIXME
		if (resultString.equals("")) {
			GlobalUtil.shortToast(mContext, "Scan failed  !",
					getResources().getDrawable(R.drawable.ic_dialog_no));
		} else {
			// System.out.println("Result:"+resultString);
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			resultIntent.putExtras(bundle);
			getActivity().setResult(getActivity().RESULT_OK, resultIntent);
		}
		getActivity().finish();
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
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
	private Camera mCamera;
	private Parameters mParameters;
	private boolean vibrate;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
}
