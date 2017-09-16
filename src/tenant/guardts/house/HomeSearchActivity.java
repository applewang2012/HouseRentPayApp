package tenant.guardts.house;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import tenant.guardts.house.model.ActivityController;

public class HomeSearchActivity extends BaseActivity {

	private TextView mTitleBar;
	private String [] mOwnerType = new String[3];
	private String mTypeIndex;
	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat df;
	private long mStartTimeClipse, mEndTimeClipse;
	//private String mSetStartData, mSetEndData;
	private TextView mStartTimeText;
	private TextView mEndTimeText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_home_search);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("搜索房屋");
		ActivityController.addActivity(this);
		initView();
		initData();
		
	}
	
	private void initData(){
		mOwnerType[0] = "日租房";
		mOwnerType[1] = "月租房";
		mOwnerType[2] = "时租房";
	}
	
	private void showRentTypeAlertDialog(final TextView text,final String[] items) {  
		  AlertDialog.Builder builder =new AlertDialog.Builder(HomeSearchActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setItems(items, new DialogInterface.OnClickListener() {
			

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTypeIndex = which+"";
				text.setText("   "+items[which]);
				mStartTimeText.setText("请选择入住时间");
				mStartTimeClipse = 0;
				mEndTimeText.setText("请选择退房时间");
				mEndTimeClipse = 0;
			}
		});
		builder.show();
	}
	
	private void initView(){
		LinearLayout selectRentType = (LinearLayout)findViewById(R.id.id_home_seach_type_content);
		final TextView showRentType = (TextView)findViewById(R.id.id_home_seach_type_show);
		selectRentType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showRentTypeAlertDialog(showRentType , mOwnerType);
			}
		});
		mStartTimeText = (TextView)findViewById(R.id.id_home_seach_start_time);
		mEndTimeText = (TextView)findViewById(R.id.id_home_seach_end_time);
		mStartTimeText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getStartDateAndTime();
				
			}
		});
		mEndTimeText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getEndDateAndTime();
			}
		});
		
		final EditText keywordInput = (EditText)findViewById(R.id.id_home_seach_input_keyword);
		Button startSearch = (Button)findViewById(R.id.id_home_start_seach_button);
		startSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkInputContent()){
					Intent intent = new Intent(HomeSearchActivity.this, HouseSearchActivity.class);
					intent.putExtra("search_tag", keywordInput.getEditableText().toString());
					intent.putExtra("start_time", mStartTimeText.getText());
					intent.putExtra("end_time", mEndTimeText.getText());
					startActivity(intent);
				}
			}
		});
	}
	
	
	private void getStartDateAndTime(){
		new DatePickerDialog(HomeSearchActivity.this , 
				startlistener , 
				cal.get(Calendar.YEAR ), 
				cal .get(Calendar.MONTH ), 
				cal .get(Calendar.DAY_OF_MONTH ) 
				).show(); 
	}
	
	private void getStartTime(){
		new TimePickerDialog(HomeSearchActivity.this, starttimeListener, 
				cal.get(Calendar.HOUR_OF_DAY), 0, true)
			.show();
	}
	
	private void getEndTime(){
		new TimePickerDialog(HomeSearchActivity.this, endtimeListener, 
				cal.get(Calendar.HOUR_OF_DAY), 0, true)
			.show();
	}
	
	private void getEndDateAndTime(){
		new DatePickerDialog(HomeSearchActivity.this , 
				endlistener , 
				cal.get(Calendar.YEAR ), 
				cal .get(Calendar.MONTH ), 
				cal .get(Calendar.DAY_OF_MONTH ) 
				).show(); 
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
	
	private void updateStartDate(){ 
		df = new SimpleDateFormat( "yyyy-MM-dd HH:mm"); 
		//mSetStartData = df.format(cal.getTime());
		mStartTimeText.setText(df.format(cal.getTime())); 
		mStartTimeClipse = cal.getTimeInMillis();
	}
	
	private void updateEndDate(){ 
		df = new SimpleDateFormat( "yyyy-MM-dd HH:mm" ); 
		//mSetEndData = df.format(cal.getTime());
		mEndTimeText.setText(df.format(cal.getTime())); 
		mEndTimeClipse = cal.getTimeInMillis();
	}
	
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
	
	private boolean checkInputContent(){
		
		if (mTypeIndex == null || mTypeIndex.equals("")){
			Toast.makeText(getApplicationContext(), "请选择租赁类型", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mStartTimeClipse == 0) {
			Toast.makeText(getApplicationContext(), "请输入租房开始时间", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mEndTimeClipse == 0) {
			Toast.makeText(getApplicationContext(), "请输入租房结束时间", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mEndTimeClipse <= mStartTimeClipse){
			Toast.makeText(getApplicationContext(), "租房起止时间选择有误！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
