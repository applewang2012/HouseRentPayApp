package tenant.guardts.house;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.gzt.faceid5sdk.DetectionAuthentic;
import com.gzt.faceid5sdk.listener.ResultListener;
import com.oliveapp.face.livenessdetectorsdk.utilities.algorithms.DetectedRect;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.BMapUtil;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.ScreenShotUtil;

public class GetRentAttributeActivity extends BaseActivity{

	private TextView mTitleBar;
	
	private HoursePresenter mPresenter;
	private String mAddRentAction = "http://tempuri.org/AddRentRecord";
	private String mIdentifyUrl = "https://nid.sdtt.com.cn/AppRegSvr/thirdsysauthsvr/houseorder";
	private String mAppIDString = "0000004";
	private String mRandNum = null;
	//ret  1 desc SDT-HOUSE-3435333134303230313730333233313531373331343839
	private String mQueryStatusUrl = null;
//	private Map<String, String> mSelectedMap = new HashMap<>();
//	private Map<String, String> mOriginText = new HashMap<>();
//	private Map<String, String[]> mAllList = new HashMap<>();
	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat df;
	private TextView mStartTime, mEndTime; 
	private String mSetStartData, mSetEndData;
	private String mOriginStartContent, mOriginEndContent, mOriginNationalContent, mOriginProvinceContent;
	private String mHouseNo;
	private String mUsername;
	private String [] mOwnerType = new String[2];
	private String mOriginTypeText, mTypeIndex = null;
	private String mOwnerName;
	private String mOwnerIdcard;
	private String mRentAttributeAction = "http://tempuri.org/GetRentAttribute";
	private String mConfirmRentAttribute = "http://tempuri.org/ConfirmRentAttribute";
	private String mCompleteRentAttribute =  "http://tempuri.org/CompleteRentAttribute";
	private String mOrderId = "";
	private String mfilePath;
	private HandlerThread myHandlerThread ;
	private Handler mSubHandler;
	private String mFaceCaptureString, mCaptureString;
	private String mIdentifyAction = "http://tempuri.org/IdentifyValidateLive";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.get_house_rent_attribute_info); 
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("租房订单");
		initHandler();
		initView();
		mOrderId = getIntent().getStringExtra("order_id");
		getRentAttributeByOrderId(mOrderId);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		mHouseNo = getIntent().getStringExtra("house_id");
//		mUsername = getIntent().getStringExtra("user_name");
//		mOwnerName = getIntent().getStringExtra("owner_name");
//		mOwnerIdcard = getIntent().getStringExtra("owner_id");
//		mHouseId.setText(mHouseNo);
	}

	
	private DatePickerDialog.OnDateSetListener startlistener = new DatePickerDialog.OnDateSetListener(){  //
		@Override 
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) { 
		cal .set(Calendar. YEAR , arg1); 
		cal .set(Calendar. MONTH , arg2); 
		cal .set(Calendar. DAY_OF_MONTH , arg3); 
		updateStartDate();
		} 
	};
	
	private DatePickerDialog.OnDateSetListener endlistener = new DatePickerDialog.OnDateSetListener(){  //
		@Override 
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) { 
		cal .set(Calendar. YEAR , arg1); 
		cal .set(Calendar. MONTH , arg2); 
		cal .set(Calendar. DAY_OF_MONTH , arg3); 
		updateEndDate();
		} 
	};
	private TextView mHouseId;
	private TextView mRentIDcard;
	private TextView mRentName;
	private TextView mRentPrice;
	private TextView mRentPhone;
	private View mQrcodeView;
	private TextView mRentOrderId;
	private DetectionAuthentic authentic;
	
	
	@SuppressLint("SimpleDateFormat")
	private void updateStartDate(){ 
		df = new SimpleDateFormat( "yyyy-MM-dd" ); 
		mSetStartData = df.format(cal.getTime());
		mStartTime.setText(mOriginStartContent + df.format(cal.getTime())); 
	}
	
	private void updateEndDate(){ 
		df = new SimpleDateFormat( "yyyy-MM-dd" ); 
		mSetEndData = df.format(cal.getTime());
		mEndTime.setText(mOriginEndContent + df.format(cal.getTime())); 
	}
	
	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		
		
		mQrcodeView = (View)findViewById(R.id.id_qrcode_layout);
		mQrcodeView.setVisibility(View.INVISIBLE);
		mOwnerType[0] = "日租房";
		mOwnerType[1] = "月租房";
		
		mStartTime = (TextView)findViewById(R.id.id_rent_house_start_date_text);
		mOriginStartContent = (String) mStartTime.getText()+"  ";
		
		
		FrameLayout endTime = (FrameLayout)findViewById(R.id.id_rent_house_end_date);
		mEndTime = (TextView)findViewById(R.id.id_rent_house_end_date_text);
		mOriginEndContent = (String) mEndTime.getText()+"  ";
		
		mHouseId = (TextView)findViewById(R.id.id_rent_house_number);
		mRentIDcard = (TextView)findViewById(R.id.id_rent_house_idcard);
		mRentName = (TextView)findViewById(R.id.id_rent_house_name);
		mRentPhone = (TextView)findViewById(R.id.id_rent_house_phone);
		mRentPrice = (TextView)findViewById(R.id.id_rent_house_price);
		mRentOrderId = (TextView)findViewById(R.id.id_rent_house_order_id);
		Button okButton = (Button)findViewById(R.id.id_add_rent_confirm);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				
//				confirmRentAttributeInfo(mOrderId);
				
				if (CommonUtil.mRegisterIdcard != null && !CommonUtil.mRegisterIdcard.equals("")){
					Log.w("mingguo", "register id card  "+CommonUtil.mRegisterIdcard);
					//if (CommonUtil.mRegisterIdcard.equalsIgnoreCase(mRentIDcard.getText().toString())){
						Intent getPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						mfilePath = ScreenShotUtil.createScreenshotDirectory(GetRentAttributeActivity.this);
						
						File out = new File(mfilePath);
						
						Uri uri = Uri.fromFile(out);
						getPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
						getPhoto.putExtra("return-data", true);
						getPhoto.putExtra("camerasensortype", 2); 
						startActivityForResult(getPhoto, 1);
//					}else{
//						GlobalUtil.shortToast(getApplication(), "抱歉，登录用户身份信息和租房者身份信息不符 ", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
//					}
				}else{
					GlobalUtil.shortToast(getApplication(), "register user idcard  get failed !", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
				}
				
				
			}
		});
	}
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("mingguo", "onActivityResult resultCode  "+resultCode+" requestCode  "+requestCode+"  file  "+mfilePath);
		if (resultCode == RESULT_OK && requestCode == 1) {
			 Log.w("mingguo", "activity result  width data   "+data);
			 mSubHandler.sendEmptyMessage(1000);
			 startLiveIdentifyActivity();
			 
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
                //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                int degree = BMapUtil.readPictureDegree(mfilePath);
                Bitmap rotationBitmap = BMapUtil.rotaingImageView(degree, BitmapFactory.decodeFile(mfilePath, null));
   			 	Log.w("mingguo", "onActivityResult  before compress image  "+rotationBitmap.getWidth()+" height  "+rotationBitmap.getHeight()+"  byte  "+rotationBitmap.getByteCount());
   			 	Bitmap newBitmap = BMapUtil.compressScale(rotationBitmap);
   			 	Log.w("mingguo", "onActivityResult  compress image  "+newBitmap.getWidth()+" height  "+newBitmap.getHeight()+"  byte  "+newBitmap.getByteCount());
   			 	mCaptureString = android.util.Base64.encodeToString(BMapUtil.Bitmap2Bytes(newBitmap), android.util.Base64.NO_WRAP);

            }
        };
    }
	
	private void startLiveIdentifyActivity(){
		authentic = DetectionAuthentic.getInstance(GetRentAttributeActivity.this, new ResultListener() {

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
			
			mFaceCaptureString = android.util.Base64.encodeToString(faceImages, android.util.Base64.NO_WRAP);
			identifyUserInfo(mFaceCaptureString, mCaptureString);
			}
		});
	
		authentic.autenticateToCaptureAction(GetRentAttributeActivity.this, mRentName.getText().toString(), mRentIDcard.getText().toString());
	}
	
	private void identifyUserInfo(String faceStr, String screenshotStr){
		if (faceStr == null || screenshotStr == null){
			return;
		}
		Log.i("mingguo", "mRealNamem  "+mRentName.getText().toString()+"  IdCard  "+mRentIDcard.getText().toString());
		String identifyUrl = "http://www.guardts.com/ValidateService/IdentifyValidateService.asmx?op=IdentifyValidateLive";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mIdentifyAction));
		rpc.addProperty("idcard", mRentIDcard.getText().toString());
		rpc.addProperty("name", mRentName.getText().toString());
		rpc.addProperty("base64Str", faceStr);
		rpc.addProperty("picBase64Str", screenshotStr);
		mPresenter.readyPresentServiceParams(GetRentAttributeActivity.this, identifyUrl, mIdentifyAction, rpc);
		mPresenter.startPresentServiceTask(true);
		
	}

	private void getRentAttributeByOrderId(String id){
		
		
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentAttributeAction));
		rpc.addProperty("id", id); 
		mPresenter.readyPresentServiceParams(GetRentAttributeActivity.this, url, mRentAttributeAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private void confirmRentAttributeInfo(String id){
		
		String url = CommonUtil.mUserHost+"Services.asmx?op=ConfirmRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mConfirmRentAttribute));
		rpc.addProperty("id", id);
		mPresenter.readyPresentServiceParams(GetRentAttributeActivity.this, url, mConfirmRentAttribute, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private void completeHouseRentAttributeInfo(String id){
		
		String url = CommonUtil.mUserHost+"Services.asmx?op=CompleteRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCompleteRentAttribute));
		rpc.addProperty("id", id);
		mPresenter.readyPresentServiceParams(GetRentAttributeActivity.this, url, mCompleteRentAttribute, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				
				jsonAttributeInfoToView((String)msg.obj);
			}else if (msg.what == 101){
				completeHouseRentAttributeInfo(mOrderId);
			}else if (msg.what == 102){
				
				GlobalUtil.shortToast(getApplication(), "订单已确认", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
				finish();
			}else if (msg.what == 103){
				
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					if (object != null){
						String compareResult = object.optString("compareresult");
						if (compareResult == null || compareResult.equals("")){
							GlobalUtil.shortToast(getApplication(), mRentName.getText().toString() + " 身份认证失败 ", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
						}else{
							if (compareResult.equals("0")){
								String similar = object.optString("similar");
								//if (similar != null && similar.length() > 3){
									Double rate = 100 *	Double.parseDouble(similar);
									GlobalUtil.shortToast(getApplication(), mRentName.getText().toString() + " 身份认证成功 ", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
									
									confirmRentAttributeInfo(mOrderId);
									return;
								//}
							}else{
								GlobalUtil.shortToast(getApplication(),  mRentName.getText().toString() + " 身份认证失败  "+compareResult, getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
							}
						}
					}else{
						GlobalUtil.shortToast(getApplication(), "json object is null", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (msg.what == 200){
				
			}
		}
		
	};
	
	private void jsonAttributeInfoToView(String value){
		if (value != null){
			JSONObject object;
			try {
				object = new JSONObject(value);
				if (object != null){
					mHouseId.setText(object.getString("RentNo"));
					mRentIDcard.setText(object.getString("RRAIDCard"));
					mRentName.setText(object.getString("RRAContactName"));
					mRentPhone.setText(object.getString("RRAContactTel"));
					mRentPrice.setText(object.getString("RRentPrice"));
					mRentOrderId.setText(object.getString("RRAID"));
					mStartTime.setText(mOriginStartContent + object.getString("RRAStartDate"));
					mEndTime.setText(mOriginEndContent + object.getString("RRAEndDate")); 
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	

	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return super.onKeyDown(keyCode, event);
		}


	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
		mHandler.sendEmptyMessage(200);
	}


	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mRentAttributeAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mConfirmRentAttribute)){
				mHandler.sendEmptyMessageDelayed(101, 10);
			}else if (action.equals(mCompleteRentAttribute)){
				mHandler.sendEmptyMessageDelayed(102, 1000);
			}else if (action.equals(mIdentifyAction)){
				Message message = mHandler.obtainMessage();
				message.what = 103;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeMessages(102);
	}
	
	

}
