package tenant.guardts.house;

import tenant.guardts.house.camera.CameraManager;
import tenant.guardts.house.model.BaseFragment;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.ViewUtil;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class DoorNumberLockFragment extends BaseFragment {

	private String mUpdateAction="http://tempuri.org/CheckUpgrade";
	private HoursePresenter mPresenter;
	private int mVersionCode;
	private String mVersinName;
	private Camera camera;
	private View mRootView;
	
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//		setContentView(R.layout.activity_lock_number_layout);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
//		FrameLayout titlebarBg = (FrameLayout)findViewById(R.id.id_titlebar_bg);
//		titlebarBg.setBackgroundColor(Color.parseColor("#273032"));
//		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
//		mTitleBar.setText("编码开锁");
//		try {
//			CameraManager.enableFlashlight();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//	}
	
	
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.activity_lock_number_layout, container, false);
		initView();
		return mRootView;
	}






	

//	private void initCamera(SurfaceHolder surfaceHolder) {
//		try {
//			CameraManager.get().openDriver(surfaceHolder);
//		} catch (IOException ioe) {
//			return;
//		} catch (RuntimeException e) {
//			return;
//		}
//		if (handler == null) {
//			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
//		}
//	}
	

	private void initView(){
		final EditText inputNumber = (EditText)mRootView.findViewById(R.id.id_input_lock_number);
		inputNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//if (count == 1){
			        
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				int length = s.toString().length();
//		        
//		        if (length > 1 && (length == 4 || length % 5 == 0)){
//		        	inputNumber.setText(s.toString() + " ");
//		        	inputNumber.setSelection(inputNumber.getText().toString().length());
//		        }
			}
		});
		
		Button openButton = (Button)mRootView.findViewById(R.id.id_open_number_lock_button);
		openButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = inputNumber.getEditableText().toString();
				if (text == null || text.equals("")){
					Toast.makeText(getActivity(), "请输入门锁编码", Toast.LENGTH_LONG).show();
					return;
				}
				ViewUtil.forceCloseSoftKeyborad(getActivity());
				String realContent = "http://www.trackbike.cn/SafeCard/servlet/OAuthServlet?r=r&z=0&d="+text.replace(" ", "");
				Intent resultIntent = new Intent();
				Bundle activityBundel = new Bundle();
				activityBundel.putString("result", realContent);
				resultIntent.putExtras(activityBundel);
//				setResult(getActivity().RESULT_OK, resultIntent);
//				DoorNumberLockActivity.this.finish();
			}
		});
		
		final CheckBox openFlash = (CheckBox)mRootView.findViewById(R.id.capture_flash);
		openFlash.setOnClickListener(new OnClickListener() {

//			private Camera mCamera;
//			private Parameters mParameters;

			private Camera mCamera;
			private Parameters mParameters;

			@Override
			public void onClick(View v) {
				mCamera = CameraManager.getCamera();
	            mParameters = mCamera.getParameters();
	           
	            if (isLighting) {
	            	openFlash.setText("关闭手电筒");
	                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
	                mCamera.setParameters(mParameters);
	                isLighting = false;
	            } else { 
	            	openFlash.setText("打开手电筒");
	            	mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
	            	mCamera.setParameters(mParameters);
	                isLighting = true;
	            }
			}
		});
	}
	
	private boolean isLighting = true;


//	private void Openshoudian() {
//        //异常处理一定要加，否则Camera打开失败的话程序会崩溃
//        try {
//            LogUtil.d("smile","camera打开");
//            camera = CameraManager.getCamera();
//        } catch (Exception e) {
//            LogUtil.d("smile","Camera打开有问题");
//            Toast.makeText(getApplicationContext(), "Camera被占用，请先关闭", Toast.LENGTH_SHORT).show();
//        }
//
//        if(camera != null)
//        {
//            //打开闪光灯
//            camera.startPreview();
//            Camera.Parameters parameter = camera.getParameters();
//            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//            camera.setParameters(parameter);
//            LogUtil.d("smile","闪光灯打开");
//
//
//        }
//    }
//	
//	private void Closeshoudian()
//    {
//        if (camera != null)
//        {
//            //关闭闪光灯
//            LogUtil.d("smile", "closeCamera()");
//            camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//            camera.setParameters(camera.getParameters());
//            camera.stopPreview();
////            camera.release();
////            camera = null;
//
//
//        }
//    }
	
	
	
	
	
}
