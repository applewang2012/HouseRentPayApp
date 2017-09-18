package tenant.guardts.house;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;
import com.gzt.faceid5sdk.DetectionAuthentic;
import com.gzt.faceid5sdk.listener.ResultListener;
import com.oliveapp.face.livenessdetectorsdk.utilities.algorithms.DetectedRect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.model.ServiceCharge;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.BMapUtil;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.ScreenShotUtil;
import tenant.guardts.house.util.UtilTool;

public class AddRentAttributeActivity extends BaseActivity implements DataStatusInterface{

	private TextView mTitleBar;
	
	private HoursePresenter mPresenter;
	private String mGetPayRateDesc = "http://tempuri.org/GetPayRateDesc";//扣费提醒
	private String mAddRentAction = "http://tempuri.org/AddRentRecord";
	private String mQueryStatusAction = "http://tempuri.org/IsOrderConfirmed";
	private String mSendMessageAction = "http://tempuri.org/SendMessageToPlice";
	private String mCanRentHouseListAction = "http://tempuri.org/CanRentTheHouse";
	private String mIdentifyUrl = "https://nid.sdtt.com.cn/AppRegSvr/thirdsysauthsvr/houseorder";
	private String mRandNum = null;
	//	private Map<String, String> mSelectedMap = new HashMap<>();
//	private Map<String, String> mOriginText = new HashMap<>();
//	private Map<String, String[]> mAllList = new HashMap<>();
	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat df;
	private TextView mStartTime, mEndTime;
	private long mStartTimeClipse, mEndTimeClipse;
	private String mSetStartData, mSetEndData;
	private String mOriginStartContent, mOriginEndContent;
	private String mIdentifyAction = "http://tempuri.org/IdentifyValidateLive";
	private String mSendVerifyCodeAction = "http://tempuri.org/SendIdentifyCodeMsg";
	private String mCheckVerifyCodeAction = "http://tempuri.org/ValidateIdentifyCode";
	private String mHouseNo;
	private String mUsername;
	private String [] mOwnerType = new String[3];
	private String mOriginTypeText, mTypeIndex = null;
	private String mOwnerName;
	private String mOwnerIdcard;
	private String mOrderId;
	private String file;
	private DetectionAuthentic authentic;
	private String mFaceCaptureString, mCaptureString;
	private String mRealName, mIdCard;
	private HandlerThread myHandlerThread ;
	private Handler mSubHandler;
	private TextView commission;
	private TextView explanation;
	private EditText password;
	private LinearLayout mCommissionContent, mExplannationContent;
	private boolean mShowRentHouseDialog = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.add_house_rent_attribute_info); 
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("房客信息");
		mHouseNo = getIntent().getStringExtra("house_id");
		mUsername = getIntent().getStringExtra("user_name");
		mOwnerName = getIntent().getStringExtra("owner_name");
		mOwnerIdcard = getIntent().getStringExtra("owner_id");
		initView();
		initHandler();
		
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	/**获取服务费信息
	 * @param price
	 */
	private void getPayRateDesc(String price){
//		
		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=GetPayRateDesc";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetPayRateDesc));
		rpc.addProperty("fee", price);
		mPresenter.readyPresentServiceParams(AddRentAttributeActivity.this, url, mGetPayRateDesc, rpc);
		mPresenter.startPresentServiceTask(false);
		
	}
	
	private void checkCanRentHouseTime(String houseno){
		Log.i("mingguo0", "add rent attribute check can rent house time house no  "+houseno+"  startTime "+mSetStartData+"  endTime  "+mSetEndData);
		String url = CommonUtil.mUserHost+"Services.asmx?op=CanRentTheHouse";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCanRentHouseListAction));
		rpc.addProperty("rentNo", houseno); 
		rpc.addProperty("startdate", mSetStartData); 
		rpc.addProperty("enddate", mSetEndData); 
		mPresenter.readyPresentServiceParams(this, url, mCanRentHouseListAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private void sendPhoneVerifyCode(String phone){
		String url = "http://www.guardts.com/COMMONSERVICE/COMMONSERVICES.ASMX?op=SendIdentifyCodeMsg";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mSendVerifyCodeAction));
		rpc.addProperty("phone", phone); 
		mPresenter.readyPresentServiceParams(this, url, mSendVerifyCodeAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private void checkPhoneVerifyCode(String phone, String code){
		String url = "http://www.guardts.com/COMMONSERVICE/COMMONSERVICES.ASMX?op=ValidateIdentifyCode";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCheckVerifyCodeAction));
		rpc.addProperty("phone", phone); 
		rpc.addProperty("number", code); 
		mPresenter.readyPresentServiceParams(this, url, mCheckVerifyCodeAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	
	private void showAlertDialog(final TextView text,final String[] items) {  
		  AlertDialog.Builder builder =new AlertDialog.Builder(AddRentAttributeActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTypeIndex = which+"";
				text.setText(mOriginTypeText +"   "+items[which]);
				mSetStartData = "";
				mSetEndData = "";
				mStartTime.setText(mOriginStartContent +mSetStartData);
				mEndTime.setText(mOriginEndContent+mSetEndData);
			}
		});
		builder.show();
	}
	
	private String[] parseAlreadyRentHouseTime(String value){
		String [] list = null;
		Log.w("mingguo", " list item json  value  "+value);
		try {
			JSONArray array = new JSONArray(value);
			Log.w("mingguo", " list item json  array  "+array);
			if (array != null) {
				list = new String[array.length()];
				for (int item = 0; item < array.length(); item++){
					JSONObject itemJsonObject = array.optJSONObject(item);
					String startTime = itemJsonObject.optString("RRAStartDate");
					String endTime = itemJsonObject.optString("RRAEndDate");
					String newStartTime = UtilTool.stampToDateTime(startTime.substring(6,startTime.length()-2));
					String newEndTime = UtilTool.stampToDateTime(endTime.substring(6,endTime.length()-2));
					list[item] = newStartTime +"至"+newEndTime;
					Log.w("mingguo", " list item  "+list[item]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private void showAlreadyRentHouseTime(String[] items) {  
		AlertDialog.Builder builder =new AlertDialog.Builder(AddRentAttributeActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("以下时间段，该房屋已出租");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		builder.show();
		
//		 AlertDialog.Builder builder2 = new AlertDialog.Builder(AddRentAttributeActivity.this, R.style.AlertDialog);
//		  View view = LayoutInflater.from(AddRentAttributeActivity.this).inflate(R.layout.dialog_style_layout, null);
//		  builder2.setView(view);
//
//		builder2.show();
	}

	private void getStartDateAndTime(){
		new DatePickerDialog(AddRentAttributeActivity.this , 
				startlistener , 
				cal.get(Calendar.YEAR ), 
				cal .get(Calendar.MONTH ), 
				cal .get(Calendar.DAY_OF_MONTH ) 
				).show(); 
	}
	
	private void getStartTime(){
		new TimePickerDialog(AddRentAttributeActivity.this, starttimeListener, 
				cal.get(Calendar.HOUR_OF_DAY), 0, true)
			.show();
	}
	
	private void getEndTime(){
		new TimePickerDialog(AddRentAttributeActivity.this, endtimeListener, 
				cal.get(Calendar.HOUR_OF_DAY), 0, true)
			.show();
	}
	
	private void getEndDateAndTime(){
		new DatePickerDialog(AddRentAttributeActivity.this , 
				endlistener , 
				cal.get(Calendar.YEAR ), 
				cal .get(Calendar.MONTH ), 
				cal .get(Calendar.DAY_OF_MONTH ) 
				).show(); 
	}
	
	private void showIndentifySuccessDialog(){
		new AlertDialog.Builder(AddRentAttributeActivity.this, AlertDialog.THEME_HOLO_LIGHT).setTitle(getString(R.string.identify_success_title)) 
		  
	     .setMessage(getString(R.string.identify_success_content))  
	  
	     .setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() { 
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {
	        	 finish();
	         }  
	  
	     }).setCancelable(false)
	     .show(); 
	}
	
	private DatePickerDialog.OnDateSetListener startlistener = new DatePickerDialog.OnDateSetListener(){  //
		@Override 
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) { 
		cal .set(Calendar. YEAR , arg1); 
		cal .set(Calendar. MONTH , arg2); 
		cal .set(Calendar. DAY_OF_MONTH , arg3);
		
		if (mTypeIndex != null && mTypeIndex.equals("2")){ //时租{
			cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, 00);
			getStartTime();
		}else{
			cal.set(Calendar.HOUR_OF_DAY, 00);
			cal.set(Calendar.MINUTE, 00);
			updateStartDate();
			}
		} 
	};
	
	private OnTimeSetListener starttimeListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, 0);
			updateStartDate();
		}
	};
	
	
	
	
	private DatePickerDialog.OnDateSetListener endlistener = new DatePickerDialog.OnDateSetListener(){  //
		@Override 
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) { 
		cal .set(Calendar. YEAR , arg1); 
		cal .set(Calendar. MONTH , arg2); 
		cal .set(Calendar. DAY_OF_MONTH , arg3);
		
		if (mTypeIndex != null && mTypeIndex.equals("2")){ //时租{
			getEndTime();
		}else{
			cal.set(Calendar.HOUR_OF_DAY, 00);
			cal.set(Calendar.MINUTE, 00);
			updateEndDate();
			}
		 
		} 
	};
	
	private OnTimeSetListener endtimeListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, 0);
			updateEndDate();
		}
	};
	
	
	//private TextView mHouseId;
	private EditText mRentIDcard;
	private EditText mRentName;
	private EditText mRentPrice;
	
	private TextView mRentPhone;
	private View mQrcodeView;
	private TextView mTypeTextView;

	private int mTimeCount = -1;

	private TextView mGetVerifyCodeText;

	private EditText mVerifyCodeText;

	private String mVerifyCode;
	
	
	@SuppressLint("SimpleDateFormat")
	private void updateStartDate(){ 
		df = new SimpleDateFormat( "yyyy-MM-dd HH:mm"); 
		mSetStartData = df.format(cal.getTime());
		mStartTime.setText(mOriginStartContent + df.format(cal.getTime())); 
		mStartTimeClipse = cal.getTimeInMillis();
	}
	
	private void updateEndDate(){ 
		df = new SimpleDateFormat( "yyyy-MM-dd HH:mm" ); 
		mSetEndData = df.format(cal.getTime());
		mEndTime.setText(mOriginEndContent + df.format(cal.getTime())); 
		mEndTimeClipse = cal.getTimeInMillis();
	}
	
	private void initView(){
		password = (EditText) findViewById(R.id.door_password);//门锁密码
		commission = (TextView) findViewById(R.id.id_commission);//手续费
		explanation = (TextView) findViewById(R.id.id_explanation);//手续费描述
		mCommissionContent = (LinearLayout)findViewById(R.id.id_commission_content);
		mExplannationContent = (LinearLayout)findViewById(R.id.id_explanation_content);
		mCommissionContent.setVisibility(View.GONE);
		mExplannationContent.setVisibility(View.GONE);
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mQrcodeView = (View)findViewById(R.id.id_qrcode_layout);
		mQrcodeView.setVisibility(View.INVISIBLE);
		mOwnerType[0] = "日租房";
		mOwnerType[1] = "月租房";
		mOwnerType[2] = "时租房";
		FrameLayout typeFrameLayout = (FrameLayout)findViewById(R.id.id_rent_house_type);
		mTypeTextView = (TextView)findViewById(R.id.id_rent_house_type_text);
		mOriginTypeText = mTypeTextView.getText().toString();
		typeFrameLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAlertDialog(mTypeTextView, mOwnerType);
			}
		});
		
		FrameLayout startTime = (FrameLayout)findViewById(R.id.id_rent_house_start_date);
		mStartTime = (TextView)findViewById(R.id.id_rent_house_start_date_text);
		mOriginStartContent = (String) mStartTime.getText()+"  ";
		startTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mTypeIndex == null || mTypeIndex.equals("")){
					Toast.makeText(getApplicationContext(), "请选择租赁类型", Toast.LENGTH_SHORT).show();
					return;
				}
				getStartDateAndTime();
				 
			}
		});
		
		FrameLayout endTime = (FrameLayout)findViewById(R.id.id_rent_house_end_date);
		mEndTime = (TextView)findViewById(R.id.id_rent_house_end_date_text);
		mOriginEndContent = (String) mEndTime.getText()+"  ";
		endTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mTypeIndex == null || mTypeIndex.equals("")){
					Toast.makeText(getApplicationContext(), "请选择租赁类型", Toast.LENGTH_SHORT).show();
					return;
				}
				getEndDateAndTime();
			}
		});
		
		//mHouseId = (TextView)findViewById(R.id.id_rent_house_number);
		mRentIDcard = (EditText)findViewById(R.id.id_rent_house_idcard);
		mRentName = (EditText)findViewById(R.id.id_rent_house_name);
		mRentPhone = (EditText)findViewById(R.id.id_rent_house_phone);
		mRentName.setText(CommonUtil.mRegisterRealName);
		mRentPhone.setText(CommonUtil.mUserLoginName);
		mRentIDcard.setText(CommonUtil.mRegisterIdcard);
		mRentPrice = (EditText)findViewById(R.id.id_rent_house_price);
		mRentPrice.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()>0){
					getPayRateDesc(s+"");
					
				}else{
					mCommissionContent.setVisibility(View.GONE);
					mExplannationContent.setVisibility(View.GONE);
					commission.setText("");
					explanation.setText("");
				}
				
			}
		});
		
		mVerifyCodeText = (EditText)findViewById(R.id.id_rent_house_phone_verify);
		
		
		mGetVerifyCodeText = (TextView)findViewById(R.id.id_rent_house_get_verifycode);
		mGetVerifyCodeText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phone = mRentPhone.getEditableText().toString();
				if (phone == null || phone.equals("")){
					GlobalUtil.shortToast(getApplication(), getString(R.string.phone_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}else if (phone.length() < 11){
					GlobalUtil.shortToast(getApplication(), getString(R.string.phone_input_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				if (mTimeCount < 0){
					mTimeCount = 60;
					sendPhoneVerifyCode(phone);
					mHandler.sendEmptyMessage(2000);
				}else{
					return;
				}
				
			}
		});
		
		Button checkHouseTime = (Button)findViewById(R.id.id_check_can_rent_house);
		checkHouseTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkInputTimeContent()){
					mShowRentHouseDialog = true;
					checkCanRentHouseTime(mHouseNo);
				}
			}
		});
		
		
		Button okButton = (Button)findViewById(R.id.id_add_rent_confirm);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkInputContent()){
					mShowRentHouseDialog = false;
					checkCanRentHouseTime(mHouseNo);
				}
			}
		});
	}
	
	private void startIndentifyProcess(){
		mRealName = mRentName.getEditableText().toString();
		mIdCard = mRentIDcard.getEditableText().toString();
		
		GlobalUtil.longToast(getApplication(),"拍照认证！");
		Intent getPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		file = ScreenShotUtil.createScreenshotDirectory(AddRentAttributeActivity.this);
		File out = new File(file);
		Uri uri = Uri.fromFile(out);
		getPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		getPhoto.putExtra("return-data", true);
		getPhoto.putExtra("camerasensortype", 2); 
		startActivityForResult(getPhoto, 1);
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
	
	private boolean checkInputTimeContent(){
		if (mTypeIndex == null || mTypeIndex.equals("")){
			Toast.makeText(getApplicationContext(), "请选择租赁类型", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mSetStartData == null || mSetStartData.equals("")) {
			Toast.makeText(getApplicationContext(), "请输入租房开始时间", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mSetEndData == null || mSetEndData.equals("")) {
			Toast.makeText(getApplicationContext(), "请输入租房结束时间", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mEndTimeClipse <= mStartTimeClipse){
			Toast.makeText(getApplicationContext(), "租房起止时间选择有误！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private boolean checkInputContent(){
		
		if (mRentIDcard.getText().toString() == null || mRentIDcard.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入身份证信息", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mRentIDcard.getText().toString().length()<18){
			Toast.makeText(getApplicationContext(), "身份证信息输入有误", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mRentIDcard.getText().toString().equals(CommonUtil.mRegisterIdcard)){
			Toast.makeText(getApplicationContext(), "您无法租赁自己发布的房屋", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mRentName.getText().toString() == null || mRentName.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入姓名", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mRentPhone.getText().toString() == null || mRentPhone.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入手机号码,保证手机号真实有效", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mRentPhone.getText().toString().length()<11){
			Toast.makeText(getApplicationContext(), "手机号码输入有误", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		mVerifyCode = mVerifyCodeText.getText().toString();
		
		if (mVerifyCode == null || mVerifyCode.equals("") ){
			GlobalUtil.shortToast(getApplication(), getString(R.string.verify_not_null), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			return false;
		}else if (mVerifyCode.length() != 6){
			GlobalUtil.shortToast(getApplication(), getString(R.string.verify_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
			return false;
		}
		
		if (mRentPrice.getText().toString() == null || mRentPrice.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入租金", Toast.LENGTH_SHORT).show();
			return false;
		}
		 String string = mRentPrice.getText().toString();
		 
		if(string.substring(0, 1).contains("0")){
			Toast.makeText(getApplicationContext(), "价格第一位不能为0", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mRentPrice.getText().toString().equals("0")){
			Toast.makeText(getApplicationContext(), "租金不能为0", Toast.LENGTH_SHORT).show();
			return false;
		}
		

		// 判断密码
		if (password.getText().toString() == null || password.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请设置门锁密码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.getText().toString().length() < 6) {
			Toast.makeText(getApplicationContext(), "密码长度为6位", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
		
		return true;
	}
	
	private void startAddRentInfo(){
			Log.w("mingguo", "house no  "+mHouseNo+"  mRentName "+mRentName.getText()+" mRentPhone "+mRentPhone.getText()+" mRentIDcard.getText() "+mRentIDcard.getText()+" mRentPrice "+mRentPrice.getText()+
					"mSetStartData "+mSetStartData+" mSetEndData "+mSetEndData+" mRentReadMe "+" password  "+password.getEditableText().toString()+" username  "+mUsername);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=AddRentRecord";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mAddRentAction));
			rpc.addProperty("RentNo", mHouseNo);   
			rpc.addProperty("RRAContactName", mRentName.getText().toString());      
			rpc.addProperty("RRAContactTel", mRentPhone.getText().toString());  
			rpc.addProperty("RRAIDCard", mRentIDcard.getText().toString());  
			rpc.addProperty("RRentPrice", mRentPrice.getText().toString());     
			rpc.addProperty("RRAStartDate", mSetStartData);  
			rpc.addProperty("RRAEndDate", mSetEndData); 
			rpc.addProperty("RRADescription", "meiyou"); 
			rpc.addProperty("password", password.getEditableText().toString()); 
			rpc.addProperty("createdBy", mUsername);
			mPresenter.readyPresentServiceParams(AddRentAttributeActivity.this, url, mAddRentAction, rpc);
			mPresenter.startPresentServiceTask(true);
	}
	
	private void queryIdentifyStatus(String orderId){
		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=IsOrderConfirmed";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mQueryStatusAction));
		rpc.addProperty("id", orderId);
		mPresenter.readyPresentServiceParams(AddRentAttributeActivity.this, url, mQueryStatusAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	private void sendMessageToFinish(){
//		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=SendMessageToPlice";
//		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mSendMessageAction));
//		rpc.addProperty("rentNo", mHouseId.getText().toString());
//		rpc.addProperty("sign", "0");
//		mPresenter.readyPresentServiceParams(AddRentAttributeActivity.this, url, mSendMessageAction, rpc);
//		mPresenter.startPresentServiceTask(true);
	}
	
	private void startLiveIdentifyActivity(){
		authentic = DetectionAuthentic.getInstance(AddRentAttributeActivity.this, new ResultListener() {

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
	
		authentic.autenticateToCaptureAction(AddRentAttributeActivity.this, mRealName, mIdCard);
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
		}else{
			GlobalUtil.shortToast(getApplication(), "头像采集失败", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
		}
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
		mPresenter.readyPresentServiceParams(AddRentAttributeActivity.this, identifyUrl, mIdentifyAction, rpc);
		mPresenter.startPresentServiceTask(true);
		
	}
	
	private void startHttpService(){
		try {
			JSONObject obj = new JSONObject(); 
			HashMap<String, String> hashMap = new HashMap<>();
			obj.put("AppID", "0000004");
			obj.put("FunID", "000");
			obj.put("OrderID", System.currentTimeMillis()+"");
			obj.put("HouseID", mHouseNo);
			obj.put("LessorID", mOwnerIdcard);
			obj.put("LessorName", mOwnerName);
			obj.put("LesseeID", mRentIDcard.getText().toString());
			obj.put("LesseeName", mRentName.getText().toString());
			obj.put("Rent", mRentPrice.getText().toString());
			obj.put("RentType", mTypeIndex);  //0，日租，1，月租，2，时租
			obj.put("StartTime",mSetStartData);
			obj.put("EndTime", mSetEndData);
			
			obj.put("AgentFlag", "0");
			obj.put("AgentID", "");
			obj.put("AgentName", "");
			
//			String data = "{'StartTime':'2017-02-01','HouseID':'test002',"
//					+ "'Rent':'1000','AgentID':'110101109892837271','EndTime':'2017-03-01',"
//					+ "'LesseeID':'110101198729838272','RentType':'1','LesseeName':'fangduoduo',"
//					+ "'LessorName':'guolili','AgentName':'fangdongdong','AgentFlag':'1','LessorID':'110101109892837271',"
//					+ "'AppID':'0000004','FunID':'000','OrderID':'02212121212'}";	
			
			hashMap.put("strData", obj.toString());
			mPresenter.readyPresentHttpServiceParams(AddRentAttributeActivity.this, mIdentifyUrl, hashMap);
			mPresenter.startPresentHttpServiceTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
		
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				
				String value = (String)msg.obj;
				if (value != null && value.equals("true")){
					mQrcodeView.setVisibility(View.INVISIBLE);
					Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_SHORT).show();
					Intent intent= new Intent();
			        setResult(Activity.RESULT_OK, intent);
			        finish();
//					
//					startHttpService();
				}else{
					Toast.makeText(getApplicationContext(), "添加租赁信息失败", Toast.LENGTH_SHORT).show();
				}
			}else if (msg.what == 101){
				
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					if (object != null){
						String ret = object.optString("ret");
						if (ret != null){
							if (ret.equals("0")){
								ActivityController.finishAll();
								showIndentifySuccessDialog();
							}else{
								GlobalUtil.shortToast(getApplication(), "抱歉，提交订单失败！", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
							}
						}
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
				
				//getIndentifyInfo((String)msg.obj);
			}else if (msg.what == 102){
				parseQueryStatus((String)msg.obj);
			}else if (msg.what == 103){
				finish();
			}else if (msg.what == 105){
				try {
					Log.e("mingguo", "msg .obj   "+(String)msg.obj);
					JSONObject object = new JSONObject((String)msg.obj);
					if (object != null){
						String ret = object.optString("ret");
						if (ret != null){
							Log.e("mingguo", "ret  "+ret);
							if (ret.equals("0")){
								Toast.makeText(getApplicationContext(), "该时间段房屋空闲，请放心租住！", Toast.LENGTH_SHORT).show();
								if (!mShowRentHouseDialog){
									checkPhoneVerifyCode(mRentPhone.getText().toString(), mVerifyCode);
								}
							}else if (ret.equals("1")){
								Toast.makeText(getApplicationContext(), "该时间段房屋已出租，请选择其他时间", Toast.LENGTH_SHORT).show();
								if (mShowRentHouseDialog){
									showAlreadyRentHouseTime(parseAlreadyRentHouseTime(object.optString("RentRecord")));
								}
							}
						}
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (msg.what == 110){
				
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					if (object != null){
						String compareResult = object.optString("verify_result");
						String result = object.optString("result");
						if (result != null && result.equals("0")){
							if (compareResult != null && compareResult.equals("0")){
								GlobalUtil.shortToast(getApplication(), mRealName + " 身份认证成功 ,请等待房主确认！", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
								startAddRentInfo();
								//GlobalUtil.shortToast(getApplication(), mRealName + " 身份认证失败 ", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
							}else{
								
								GlobalUtil.shortToast(getApplication(), mRealName + " 身份认证失败  "+compareResult , getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
							}
						}else{
							GlobalUtil.shortToast(getApplication(), mRealName + " 身份认证失败，请重试 ", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
						}
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(msg.what==818){
				String value = (String)msg.obj;
				Log.e("", value+"----------");
				//显示服务费信息
				///////////////////////////////////////////////////////////////////////////////
				Gson gson=new Gson();
				ServiceCharge serviceCharge = gson.fromJson(value, ServiceCharge.class);
				mCommissionContent.setVisibility(View.VISIBLE);
				mExplannationContent.setVisibility(View.VISIBLE);
				if(serviceCharge.fee.startsWith("00")){
					commission.setText(serviceCharge.fee.substring(1)+"元（手续费）");
					
				}else{
					commission.setText(serviceCharge.fee+"元（手续费）");
				}
				explanation.setText(serviceCharge.msg);
			}else if (msg.what == 1002){
				if (msg.obj != null){
					JSONObject json;
					try {
						json = new JSONObject((String)msg.obj);
						String ret = json.optString("ret");
//						if (ret != null){
//							if (ret.equals("0")){
						startIndentifyProcess();
//							}else{
//								GlobalUtil.shortToast(getApplication(), getString(R.string.verify_error), getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_yes));
//							}
//						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else if (msg.what == 2000){
				if (mTimeCount >= 0){
					mGetVerifyCodeText.setTextColor(Color.parseColor("#cccccc"));
					mGetVerifyCodeText.setText(mTimeCount +" 秒重新发送");
					mTimeCount--;
					mHandler.sendEmptyMessageDelayed(2000, 1000);
				}else{
					mGetVerifyCodeText.setTextColor(Color.parseColor("#337ffd"));
					mGetVerifyCodeText.setText("获取验证码");
				}
				
				
			}
		}
		
	};

	
	private void parseQueryStatus(String value){
		if (value != null){
			try {
				JSONObject object = new JSONObject(value);
				if (object != null){
					String ret = object.optString("ret");
					if (ret != null && ret.equals("0")){
						Toast.makeText(getApplicationContext(), "租户已确认完成 ", Toast.LENGTH_SHORT).show();
						sendMessageToFinish();
					}else{
						queryIdentifyStatus(mOrderId);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void getIndentifyInfo(String value){
		if (value != null){
			try {
				JSONObject object = new JSONObject(value);
				if (object != null){
					object.optString("ret");
					mOrderId = object.optString("Id");
					if (mOrderId != null && !mOrderId.equals("")){
						mQrcodeView.setVisibility(View.VISIBLE);
						ImageView qrImageView = (ImageView) mQrcodeView.findViewById(R.id.id_qrcode_view);
						Log.e("mingguo", "  ret == 0  "+qrImageView.getWidth()+"  height  "+qrImageView.getWidth());
						Bitmap qrBitmap = BMapUtil.createQRImage(mOrderId, qrImageView, qrImageView.getWidth(), qrImageView.getHeight());
						qrImageView.setImageBitmap(qrBitmap);
						queryIdentifyStatus(mOrderId);
					}else{
						Toast.makeText(getApplicationContext(), "failed  return "+value, Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void getQueryStatusInfo(String value){
		if (value == null){
			return;
		}
		JSONObject object;
		try {
			object = new JSONObject(value);
			if (object != null){
				String ret = object.optString("ret");
				String desc = object.optString("desc");
				if (ret != null){
					if (ret.equals("1")){
						startAddRentInfo();
						
					}else if (ret.equals("0")){
						queryIdentifyStatus(mRandNum);
					}else {
						Toast.makeText(getApplicationContext(), ""+desc, Toast.LENGTH_SHORT).show();
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public class IdentifyModel {
		
		private String status;
		private String idtfInfo;
		
		public IdentifyModel(){
			
		}
		public String getIdentifyStatus() {
			return status;
		}

		public void setIdentifyStatus(String sts) {
			this.status = sts;
		}

		public String getIdentifyInfo() {
			return idtfInfo;
		}

		public void setIdentifyInfo(String info) {
			this.idtfInfo = info;
		}


	}

	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return super.onKeyDown(keyCode, event);
		}
	 
//	 public static IdentifyModel parseIdentifyInfo(String value) {
//			IdentifyModel model = new IdentifyModel();
//			try{
//				JSONObject object = new JSONObject(value);
//				Log.e("house", "  object  ");
//				if (object != null){
//						String ret = object.optString("ret");
//						String desc = object.optString("desc");
//						model.setIdentifyStatus(ret);
//						model.setIdentifyInfo(desc);
//						Log.e("house", "  ret  "+ret+" desc "+desc);
//				}
//				return model;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return model;
//			}
//		}


	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mAddRentAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 101;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mIdentifyUrl)){
				Log.e("mingguo", "identify url "+mIdentifyUrl);
//				Message msg = mHandler.obtainMessage();
//				msg.what = 101;
//				msg.obj = templateInfo;
//				msg.sendToTarget();
			}else if (action.equals(mQueryStatusAction)){
				Message msg = new Message();
				msg.what = 102;
				msg.obj = templateInfo;
				mHandler.sendMessageDelayed(msg, 3000);
			}else if (action.equals(mSendMessageAction)){
				Message msg = new Message();
				msg.what = 103;
				msg.obj = templateInfo;
				mHandler.sendMessage(msg);
			}else if (action.equals(mIdentifyAction)){
				Message message = mHandler.obtainMessage();
				message.what = 110;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}else if(action.equals(mGetPayRateDesc)){
				Message msg = mHandler.obtainMessage();
				msg.what = 818;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mCheckVerifyCodeAction)){
				Message message = mHandler.obtainMessage();
				message.what = 1002;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}else if (action.equals(mSendVerifyCodeAction)){
				
			}else if (action.equals(mCanRentHouseListAction)){
				Message message = mHandler.obtainMessage();
				message.what = 105;
				//message.obj = templateInfo.replace("\\", "");
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}
	
	

}
