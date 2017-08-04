package tenant.guardts.house;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.gzt.faceid5sdk.DetectionAuthentic;
import com.gzt.faceid5sdk.listener.ResultListener;
import com.oliveapp.face.livenessdetectorsdk.utilities.algorithms.DetectedRect;

import tenant.guardts.house.R;
import tenant.guardts.house.model.ActivityController;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.BMapUtil;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.ScreenShotUtil;

public class RegisterUserStep3Activity extends BaseActivity{

	private TextView mTitleBar;
	private View mLoadingView;
	private HoursePresenter mPresenter;
	private String mIdentifyAction = "http://tempuri.org/IdentifyValidateLive";
	private String mRegisterAction = "http://tempuri.org/AddUserInfo";
	private String mRealName, mIdCard;
	private DetectionAuthentic authentic;
//	private Bitmap mPressBitmap;
	private HandlerThread myHandlerThread ;
	private Handler mSubHandler;
	private String mFaceCaptureString, mCaptureString;
	private String file;
	
	private String mUserName;
	private String mPassword;
	private String mPhone;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.register_user_step3_layout); 
		ActivityController.addActivity(RegisterUserStep3Activity.this);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("实名认证");
		mUserName = getIntent().getStringExtra("username");
		mPassword = getIntent().getStringExtra("password");
		mPhone = getIntent().getStringExtra("phone");
//		mUserName = "wmg123";
//		mPassword = "1234567890";
//		mPhone = "13333333333";
		initView();
		initHandler();
	}
	
	

	private void initView(){
		
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);
		
		final EditText realName = (EditText)findViewById(R.id.id_register_step3_input_name);
		final EditText idCard = (EditText)findViewById(R.id.id_register_step3_input_idcard);
		
		Button registerButton = (Button)findViewById(R.id.id_register_user_step3_next);
		registerButton.setOnClickListener(new OnClickListener() {
			
			

			@Override
			public void onClick(View v) {
				mRealName = realName.getEditableText().toString();
				mIdCard = idCard.getEditableText().toString();
				if (mRealName == null || mRealName.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.surface_name_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (mIdCard == null || mIdCard.equals("")){
					GlobalUtil.shortToast(getApplication(),getString(R.string.id_card_not_null) , getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}else if (mIdCard.length() < 18){
					GlobalUtil.shortToast(getApplication(),getString(R.string.id_card_input_error) , getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				Intent getPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				file = ScreenShotUtil.createScreenshotDirectory(RegisterUserStep3Activity.this);
				File out = new File(file);
				Uri uri = Uri.fromFile(out);
				getPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);//鏍规嵁uri淇濆瓨鐓х墖
				getPhoto.putExtra("return-data", true);
				getPhoto.putExtra("camerasensortype", 2); // 调用前置摄像头
				startActivityForResult(getPhoto, 1);//鍚姩鐩告満鎷嶇収
			
			}
		});
	}
	
	private void startLiveIdentifyActivity(){
		authentic = DetectionAuthentic.getInstance(RegisterUserStep3Activity.this, new ResultListener() {

		@Override
		public void onSDKUsingFail(String errorMessage, String errorCode) {
			// TODO Auto-generated method stub
			GlobalUtil.shortToast(getApplication(), errorMessage, getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			
		}
		
		@Override
		public void onIDCardImageCaptured(byte[] faceImages, DetectedRect arg1) {
			if(faceImages == null){
				GlobalUtil.shortToast(getApplication(), "image capture  无人脸", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}
			
			//TextView textView = (TextView)findViewById(R.id.show_text);
			//textView.setText("onFaceImageCaptured-->"+imgStr);
//			identifyUserInfo(imgStr, mCaptureString);
//			ImageView img = (ImageView) this.findViewById(R.id.imageView1);
//			Bitmap bm = BitmapFactory.decodeByteArray(faceImages, 0, faceImages.length);
//			img.setImageBitmap(bm);
			
		}
		
		@Override
		public void onFaceImageCaptured(byte[] faceImages) {
			if(faceImages == null){
				GlobalUtil.shortToast(getApplication(), "image capture  无人脸", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}
			showLoadingView();
			mFaceCaptureString = android.util.Base64.encodeToString(faceImages, android.util.Base64.NO_WRAP);
			identifyUserInfo(mFaceCaptureString, mCaptureString);
		}
		});
	
		authentic.autenticateToCaptureAction(RegisterUserStep3Activity.this, mRealName, mIdCard);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("mingguo", "onActivityResult resultCode  "+resultCode+" requestCode  "+requestCode+"  file  "+file);
		if (resultCode == RESULT_OK && requestCode == 1) {
			 Log.w("mingguo", "activity result  width data   "+data);
			 mSubHandler.sendEmptyMessage(1000);
			 startLiveIdentifyActivity();
			 
			 //			   if(data != null){
//			    if(data.hasExtra("data")){
//			     Bitmap thunbnail = data.getParcelableExtra("data");
//			     Log.w("mingguo", "activity result  width  "+thunbnail.getWidth()+"  height  "+thunbnail.getHeight());
//			     ImageView shot = (ImageView)findViewById(R.id.id_user_shot);
//					shot.setImageBitmap(thunbnail);
//			    }
//			   }
			
			
//			ImageView shot = (ImageView)findViewById(R.id.id_user_shot);
//			shot.setImageBitmap(newBitmap);
//			int scale = 0;
//				scale = getZoomScale(imageFile);//寰楀埌缂╂斁鍊嶆暟
//				Log.i(TAG, "scale = "+scale);
//				BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inSampleSize = scale;
//				photoImageView.setImageBitmap(BitmapFactory.decodeFile(strImgPath,options));//鎸夋寚瀹歰ptions鏄剧ず鍥剧墖闃叉OOM
//			}else {
//				Toast.makeText(MainActivity.this, R.string.failed, Toast.LENGTH_LONG).show();
//			}
		}else{
			GlobalUtil.shortToast(getApplication(), "头像采集失败", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
		}
	}


	private void initHandler(){
    	//创建一个线程,线程名字：handler-thread
        myHandlerThread = new HandlerThread( "handler-thread") ;
        myHandlerThread.start();
        
        mSubHandler = new Handler(myHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int degree = BMapUtil.readPictureDegree(file);
                Bitmap rotationBitmap = BMapUtil.rotaingImageView(degree, BitmapFactory.decodeFile(file, null));
   			 	Log.w("mingguo", "onActivityResult  before compress image  "+rotationBitmap.getWidth()+" height  "+rotationBitmap.getHeight()+"  byte  "+rotationBitmap.getByteCount());
   			 	Bitmap newBitmap = BMapUtil.compressScale(rotationBitmap);
   			 	Log.w("mingguo", "onActivityResult  compress image  "+newBitmap.getWidth()+" height  "+newBitmap.getHeight()+"  byte  "+newBitmap.getByteCount());
   			 	mCaptureString = android.util.Base64.encodeToString(BMapUtil.Bitmap2Bytes(newBitmap), android.util.Base64.NO_WRAP);
                
            }
        };
        
    }
	
	private void identifyUserInfo(String faceStr, String screenshotStr){
		if (faceStr == null || screenshotStr == null){
			return;
		}
		Log.i("mingguo", "register interface  faceStr  "+faceStr.length()+"  screenshot   "+screenshotStr.length());
		Log.i("mingguo", "register interface  mIdCard  "+mIdCard+"  mRealName  "+mRealName);
		String identifyUrl = "http://www.guardts.com/ValidateService/IdentifyValidateService.asmx?op=IdentifyValidateLive";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mIdentifyAction));
		rpc.addProperty("idcard", mIdCard);
		rpc.addProperty("name", mRealName);
		rpc.addProperty("base64Str", faceStr);
		rpc.addProperty("picBase64Str", screenshotStr);
		mPresenter.readyPresentServiceParams(getApplicationContext(), identifyUrl, mIdentifyAction, rpc);
		mPresenter.startPresentServiceTask();
		
	}
	
	
	private void registerUserName(){
		String url = CommonUtil.mUserHost+"services.asmx?op=AddUserInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRegisterAction));
		rpc.addProperty("loginName", mUserName);
		rpc.addProperty("password", mPassword);
		rpc.addProperty("userType", "0");
		rpc.addProperty("realName", mRealName);
		rpc.addProperty("title", "title");
		rpc.addProperty("sex", "male");
		rpc.addProperty("phone", mPhone);
		rpc.addProperty("fax", "fax");
		rpc.addProperty("email", "email");
		rpc.addProperty("idcard", mIdCard);
		rpc.addProperty("nickName", "nick");
		rpc.addProperty("address", "address");
		rpc.addProperty("status", "0"); //
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mRegisterAction, rpc);
		mPresenter.startPresentServiceTask();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				GlobalUtil.shortToast(getApplication(), getString(R.string.username_register_again), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}else if (msg.what == 101){
				dismissLoadingView();
				SharedPreferences sharedata = getApplicationContext().getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
			    editor.putString("user_name", mUserName);
			    editor.putString("user_password", mPassword);
			    editor.commit();
			    mHandler.sendEmptyMessageDelayed(105, 200);
			}else if (msg.what == 105){
				ActivityController.finishAll();
				GlobalUtil.shortToast(getApplication(), getString(R.string.register_success), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
				Intent intent = new Intent(RegisterUserStep3Activity.this, RegisterUserFinishActivity.class);
				intent.putExtra("user_name", mUserName);
				intent.putExtra("user_password", mPassword);
				startActivity(intent);
				finish();
			}else if (msg.what == 110){
				GlobalUtil.shortToast(getApplication(), "登录失败！", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			}else if (msg.what == 102){
				dismissLoadingView();
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					if (object != null){
						String compareResult = object.optString("compareresult");
						if (compareResult == null || compareResult.equals("")){
							GlobalUtil.shortToast(getApplication(), mRealName + " 身份认证失败 ", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
						}else{
							if (compareResult.equals("0")){
//								String similar = object.optString("similar");
//								if (similar != null && similar.length() > 3){
//									Double rate = 100 *	Double.parseDouble(similar);
									GlobalUtil.shortToast(getApplication(), mRealName + " 身份认证成功 ", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
									registerUserName();
									return;
//								}
							}else{
								GlobalUtil.shortToast(getApplication(), mRealName + " 身份认证失败  "+compareResult , getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if (msg.what == 200){
				dismissLoadingView();
			}
			
		}
		
	};
	
	private void showLoadingView(){
		
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}
	
	
	/**
	 * 自动对焦的回调方法，用来处理对焦成功/不成功后的事件
	 */
	private AutoFocusCallback mAutoFocus =  new AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			//TODO:空实现
		}
	}; 
	

	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE){
					mLoadingView.setVisibility(View.INVISIBLE);
					return false;
				}
			}
			return super.onKeyDown(keyCode, event);
		}
	 
	 

	@Override
	public void onStatusStart() {
		
		
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
		mHandler.sendEmptyMessage(200);
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mRegisterAction)){
				if (templateInfo.equals("true")){
					mHandler.sendEmptyMessage(101);
				}else{
					mHandler.sendEmptyMessage(110);
				}
			}else if (action.equals(mIdentifyAction)){
				Message message = mHandler.obtainMessage();
				message.what = 102;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}

}
