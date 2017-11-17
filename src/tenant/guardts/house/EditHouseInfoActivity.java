package tenant.guardts.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.model.ServiceCharge;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.JsonObjectParse;
import tenant.guardts.house.util.LogUtil;

public class EditHouseInfoActivity extends BaseActivity{

	private TextView mTitleBar;
	private HoursePresenter mPresenter;
	private String mGetPayRateDesc = "http://tempuri.org/GetPayRateDesc";//扣费提醒
//	private String mPropertyAction = "http://tempuri.org/GetHouseProperty";
	private String mTypeAction ="http://tempuri.org/GetHouseType";
	private String mDirectionAction ="http://tempuri.org/GetHouseDirection";
	private String mStuctureAction = "http://tempuri.org/GetHouseStructure";
	private String mBuildingStuctureAction = "http://tempuri.org/GetBuildingStructure";
	private String mDistrictAction = "http://tempuri.org/GetDistrictList";
	private String mStreetAction = "http://tempuri.org/GetStreetList";
	private String mRoadAction = "http://tempuri.org/GetRoadList";
	private String mFenjuAction = "http://tempuri.org/GetPoliceStationList";
	private String mPoliceAction = "http://tempuri.org/GetLocalPoliceStationList";
	private String mRentTypeAction = "http://tempuri.org/GetHouseRentType";
	private String mOwnerTypeAction = "http://tempuri.org/GetHouseOwnType";
	private String mUpdateHouseAction = "http://tempuri.org/UpdateRentInfo";
	private String mValidHouseIDAction ="http://tempuri.org/ValidateHouseID";
//	private Map<String, String> mSelectedMap = new HashMap<>();
//	private Map<String, String> mOriginText = new HashMap<>();
//	private Map<String, String[]> mAllList = new HashMap<>();
//	private Map<String, HouseSelectorModel> mSelectorInfo = new HashMap<>();
	private TextView mPropertryTextView, mTypeTextView;
	private TextView mDirectionTextView;
	//private TextView mStructureTextView;
	private TextView mDistrictTextView;
	private TextView mStrictTextView;
	private TextView mStreetTextView;
	private TextView mRoadTextView;
	private TextView mFenjuTextView;
	private TextView mPoliceTextView;
	private TextView mRentTypeTextView;
	private TextView mHouseTypeTextView;
	//private TextView mOwnerTypeTextView;
	private String mRentNo;
	private String mRDName = "01";
	private String mRSName = "02";
	//private String mRTotalDoor;
	private String mRRoomType;
	private String mRDirection;
	//private String mRStructure;
	//private TextView mBuildingStructureTextView;
	//private String mRBuildingType;
	private String mUserName;
	//private boolean mValidHouseId = false;
	//private EditText mHouseNo;
	private TextView commission;
	private TextView explanation;
	private TextView mOwnerName;
	private TextView mOwnerPhone;
	private TextView mOwnerIdCard;
	
	private String mHouseDetailAction = "http://tempuri.org/GetHouseDetailInfo";
	private HashMap<String, List<String>> mDetailMap = new HashMap<>(); //key 类型，list 0 id， list 1 value
	private EditText mHouseAreaEditText, mTotalFloorEditText, mCurrentFloorEditText, mCurrentDoorEditText,
			mLouHaoEditText, mMenHaoEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_edit_house_info); 
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("编辑房屋");
		mRentNo = getIntent().getStringExtra("rentNo");
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		getHouseDetailInfoByHouseId(mRentNo);
		
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	private void getHouseDetailInfoByHouseId(String rentNo) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetHouseDetailInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mHouseDetailAction));
		rpc.addProperty("rentNo", rentNo);
		mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mHouseDetailAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	
	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mOwnerName = (TextView)findViewById(R.id.id_add_house_owner_name);
		mOwnerPhone = (TextView)findViewById(R.id.id_add_house_owner_phone);
		mOwnerIdCard = (TextView)findViewById(R.id.id_add_house_owner_id_card);
		mOwnerName.setText(CommonUtil.mRegisterRealName);
		mOwnerPhone.setText(CommonUtil.mUserLoginName);
		mOwnerIdCard.setText(CommonUtil.mRegisterIdcard);
		commission = (TextView) findViewById(R.id.commission);//手续费
		explanation = (TextView) findViewById(R.id.explanation);//手续费描述
		mPriceEditText = (EditText)findViewById(R.id.id_add_house_price);
		mPriceEditText.addTextChangedListener(new TextWatcher() {
			
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
					commission.setText("");
					explanation.setText("");
				}
			}
		});
//		mHouseNo = (EditText)findViewById(R.id.id_add_house_id_number);
//		mHouseNo.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if (!hasFocus){
//					getHouseValidId();
//				}
//			}
//		});
//		FrameLayout propertryFrameLayout = (FrameLayout)findViewById(R.id.id_add_house_property);
//		mPropertryTextView = (TextView)findViewById(R.id.id_add_house_property_text);
//		propertryFrameLayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				getHouseProperty();
//			}
//		});
		LinearLayout typeFrameLayout = (LinearLayout)findViewById(R.id.id_add_house_type);
		mTypeTextView = (TextView)findViewById(R.id.id_add_house_type_text);
		typeFrameLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getHouseType();
			}
		});
		LinearLayout directionFrameLayout = (LinearLayout)findViewById(R.id.id_add_house_direction);
		mDirectionTextView = (TextView)findViewById(R.id.id_add_house_direction_text);
		directionFrameLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getHouseDirection();
			}
		});
		
		//分局
		FrameLayout fenjuFrameLayout = (FrameLayout)findViewById(R.id.id_add_house_police_fenju);
		mFenjuTextView = (TextView)findViewById(R.id.id_add_house_police_fenju_text);
		fenjuFrameLayout.setOnClickListener(new OnClickListener() {
					
			@Override
					public void onClick(View v) {
						getHouseFenju();
					}
				});
				//派出所
		FrameLayout policeFrameLayout = (FrameLayout)findViewById(R.id.id_add_house_police);
		mPoliceTextView = (TextView)findViewById(R.id.id_add_house_police_text);
		policeFrameLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						List<String> fenjuList = mDetailMap.get("fenju");
						if (fenjuList != null && fenjuList.size() > 1){
							if (fenjuList.get(1) != null && !fenjuList.get(1).equals("")){
								List<String> roadList = mDetailMap.get("road");
								if (roadList != null && roadList.size() > 1){
									roadList.set(1, "");
								}
								mRoadTextView.setText("");
								getHousePolice(fenjuList.get(0));
							}else{
								Toast.makeText(getApplicationContext(), "请先选择分局", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "请先选择分局", Toast.LENGTH_SHORT).show();
						}
					}
				});
		//小区
		FrameLayout roadFrameLayout = (FrameLayout)findViewById(R.id.id_add_house_road);
		mRoadTextView = (TextView)findViewById(R.id.id_add_house_road_text);
		roadFrameLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<String> policeList = mDetailMap.get("police");
					if (policeList.get(1) != null && !policeList.get(1).equals("")){
						
					}else{
							Toast.makeText(getApplicationContext(), "请先选择派出所", Toast.LENGTH_SHORT).show();
							return;
					}
					List<String> fenjuList = mDetailMap.get("fenju");
					if (fenjuList != null && fenjuList.size() > 1){
							
					}else{
						Toast.makeText(getApplicationContext(), "请先选择分局", Toast.LENGTH_SHORT).show();
							return;
					}
					getHouseRoad(policeList.get(0));
					
			}
		});
		

		//租赁类型
		FrameLayout rentTypeFrameLayout = (FrameLayout)findViewById(R.id.id_add_house_rent_type);
		mRentTypeTextView = (TextView)findViewById(R.id.id_add_house_rent_type_text);
		
		rentTypeFrameLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getHouseRentType();
			}
		});
		
		mHouseAreaEditText = (EditText)findViewById(R.id.id_add_house_area);
		mTotalFloorEditText = (EditText)findViewById(R.id.id_add_house_total_floor);
		mCurrentFloorEditText = (EditText)findViewById(R.id.id_add_house_current_floor);
		mCurrentDoorEditText = (EditText)findViewById(R.id.id_add_house_current_num);
		mLouHaoEditText = (EditText)findViewById(R.id.id_add_house_loudong_number);
		mMenHaoEditText = (EditText)findViewById(R.id.id_add_house_loumen_hao);
		
		Button okButton = (Button)findViewById(R.id.id_add_house_info_confirm);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if (!mValidHouseId){
//					Toast.makeText(getApplicationContext(), "房产证编号输入有误", Toast.LENGTH_SHORT).show();
//					return;
//				}
				if (checkInputContent()){
					editHouseFinishDialog();
				}
			}
		});
	}
	
	private void editHouseFinishDialog(){
		new AlertDialog.Builder(EditHouseInfoActivity.this, AlertDialog.THEME_HOLO_LIGHT)
		.setTitle(getString(R.string.modify_house_title))

		.setMessage(getString(R.string.modify_house_content))

		.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startUpdateHouseInfo();
			}

		}).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť

					@Override
					public void onClick(DialogInterface dialog, int which) {// ��Ӧ�¼�

					}

				}).show();
	}
	
	/**获取服务费信息
	 * @param price
	 */
	private void getPayRateDesc(String price){
//		
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetPayRateDesc";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetPayRateDesc));
		rpc.addProperty("fee", price);
		mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mGetPayRateDesc, rpc);
		mPresenter.startPresentServiceTask(false);
		
	}
	
	private void showDetailView(){
		mRentTypeTextView.setText("   "+mDetailMap.get("rentType").get(1));
		mTypeTextView.setText(mDetailMap.get("roomType").get(1));
		mDirectionTextView.setText(mDetailMap.get("direction").get(1));
		mHouseAreaEditText.setText(mDetailMap.get("area").get(0));
		mTotalFloorEditText.setText(mDetailMap.get("totalFloor").get(0));
		mCurrentFloorEditText.setText(mDetailMap.get("currentFloor").get(0));
		mCurrentDoorEditText.setText(mDetailMap.get("currentDoor").get(0));
		mFenjuTextView.setText(mDetailMap.get("fenju").get(1));
		mPoliceTextView.setText(mDetailMap.get("police").get(1));
		mRoadTextView.setText(mDetailMap.get("road").get(1));
		
		mPriceEditText.setText(mDetailMap.get("RLocationDescription").get(0));
		mOwnerName.setText(mDetailMap.get("ROwner").get(0));
		mOwnerPhone.setText(mDetailMap.get("ROwnerTel").get(0));
		mOwnerIdCard.setText(mDetailMap.get("RIDCard").get(0));
	}
	private boolean checkInputContent(){

		if (mPriceEditText.getText().toString() == null || mPriceEditText.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入房屋价格", Toast.LENGTH_SHORT).show();
			return false;
		}else if(mPriceEditText.getText().toString().substring(0, 1).equals("0")){
			Toast.makeText(getApplicationContext(), "价格输入有误，请重新输入", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			List<String> priceList = mDetailMap.get("RLocationDescription");
			if (priceList != null){
				priceList.set(0, mPriceEditText.getText().toString());
			}
		}
		
		
		if (mHouseAreaEditText.getText().toString() == null || mHouseAreaEditText.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入房屋面积", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			 ;
			List<String> areaList2 = mDetailMap.get("area");
			if (areaList2 != null){
				areaList2.set(0, mHouseAreaEditText.getText().toString());
			}
		}
		
		if (mTotalFloorEditText.getText().toString() == null || mTotalFloorEditText.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入总楼层", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			List<String> totalList = mDetailMap.get("totalFloor");
			if (totalList != null){
				totalList.set(0, mTotalFloorEditText.getText().toString());
			}
		}
		
		if (mCurrentFloorEditText.getText().toString() == null || mCurrentFloorEditText.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入所在楼层", Toast.LENGTH_SHORT).show();
			return false;
		}else{ 
			List<String> currentList = mDetailMap.get("currentFloor");
			if (currentList != null){
				currentList.set(0, mCurrentFloorEditText.getText().toString());
			}
		}

		
		if (mCurrentDoorEditText.getText().toString() == null || mCurrentDoorEditText.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入门牌号", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			List<String> currentList = mDetailMap.get("currentDoor");
			if (currentList != null){
				currentList.set(0, mCurrentDoorEditText.getText().toString());
			}
		}
		
		if (mLouHaoEditText.getText().toString() == null || mLouHaoEditText.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入楼栋号", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			
		}
		
		if (mMenHaoEditText.getText().toString() == null || mMenHaoEditText.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入楼门号", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			
		}
		

		
		if (mOwnerName.getText().toString() == null || mOwnerName.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入房主姓名", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			
		}
		
		if (mOwnerPhone.getText().toString() == null || mOwnerPhone.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入房主手机", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			
		}
		
		if (mOwnerIdCard.getText().toString() == null || mOwnerIdCard.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "请输入房主身份证号码", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			
		}
		
		if (mDetailMap.get("rentType").get(1) == null || mDetailMap.get("roomType").get(1).equals("")){
			Toast.makeText(getApplicationContext(), "请选择租赁类型", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("roomType").get(1) == null || mDetailMap.get("roomType").get(1).equals("")){
			Toast.makeText(getApplicationContext(), "请选择房型", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("direction").get(1) == null || mDetailMap.get("direction").get(1).equals("")){
			Toast.makeText(getApplicationContext(), "请选择房屋朝向", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("currentFloor").get(0) == null || mDetailMap.get("currentFloor").get(0).equals("")){
			Toast.makeText(getApplicationContext(), "请输入楼层", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("totalFloor").get(0) == null || mDetailMap.get("totalFloor").get(0).equals("")){
			Toast.makeText(getApplicationContext(), "请输入总楼层", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("currentDoor").get(0) == null || mDetailMap.get("currentDoor").get(0).equals("")){
			Toast.makeText(getApplicationContext(), "请输入门牌号", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("area").get(0) == null || mDetailMap.get("area").get(0).equals("")){
			Toast.makeText(getApplicationContext(), "请选择房屋面积", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("RLocationDescription").get(0) == null || mDetailMap.get("RLocationDescription").get(0).equals("")){
			Toast.makeText(getApplicationContext(), "请输入房屋价格", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("fenju").get(1) == null || mDetailMap.get("fenju").get(1).equals("")){
			Toast.makeText(getApplicationContext(), "请选择分局", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("police").get(1) == null || mDetailMap.get("police").get(1).equals("")){
			Toast.makeText(getApplicationContext(), "请选择派出所", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (mDetailMap.get("road").get(1) == null || mDetailMap.get("road").get(1).equals("") || mDetailMap.get("road").get(1).equalsIgnoreCase("null")){
			Toast.makeText(getApplicationContext(), "请选择小区", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
		
	}

	
	private void getHouseType(){
			String url = CommonUtil.mUserHost+"services.asmx?op=GetHouseType";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mTypeAction));
			mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mTypeAction, rpc);
			mPresenter.startPresentServiceTask(true);
	}
	
	private void getHouseDirection(){
			String url = CommonUtil.mUserHost+"services.asmx?op=GetHouseDirection";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mDirectionAction));
			mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mDirectionAction, rpc);
			mPresenter.startPresentServiceTask(true);
	}
	
	private void getHouseRentType(){

			String url = CommonUtil.mUserHost+"services.asmx?op=GetHouseRentType";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentTypeAction));
			mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mRentTypeAction, rpc);
			mPresenter.startPresentServiceTask(true);

	}
	
	private void getHouseRoad(String policeId){
		///////////////////////////////////////////////////////////////////////////////////////////
			
			String url = CommonUtil.mUserHost+"services.asmx?op=GetRoadList";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRoadAction));
			//if (mSelectorInfo.get("police").getHouseSelectId() != null && !mSelectorInfo.get("police").getHouseSelectId().equals("")){
//				rpc.addProperty("street", mSelectorInfo.get("fenju").getHouseSelectId());
				rpc.addProperty("district", policeId);
				mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mRoadAction, rpc);
				mPresenter.startPresentServiceTask(true);
			//}
	}
	
	private void startUpdateHouseInfo(){
		String address =  mDetailMap.get("fenju").get(2)+  mDetailMap.get("police").get(2)+  mDetailMap.get("road").get(1) + mLouHaoEditText.getText().toString()+"号楼"+
	mMenHaoEditText.getText().toString()+"门"+mDetailMap.get("currentDoor").get(0);	
		LogUtil.w("mingguo", "add house info mRentNo "+mRentNo+" mRDName "+mRDName+" mRSName "+mRSName+" mRRName "+"小区"+mDetailMap.get("road").get(0)+
				" mRPSName "+"派出所"+mDetailMap.get("police").get(0)
				+" mRAddress "+address+" mRDoor "+"default null"+" mRTotalDoor "+"default null"+" mRRoomType "+mRRoomType +" mRDirection "+mRDirection+
				" mRStructure "+"default null "+" mRFloor "+"楼层"+mCurrentFloorEditText.getText().toString()+" mRTotalFloor "+"总楼层"+mTotalFloorEditText.getText().toString()+
				" mRHousePrice "+mDetailMap.get("RLocationDescription").get(0)+" mRRentArea "+mDetailMap.get("area").get(0)+
				" mRProperty "+"progper"+" mROwner "+mDetailMap.get("ROwner").get(0)+" mROwnerTel "+mDetailMap.get("ROwnerTel").get(0)+" mRIDCard "+
				mDetailMap.get("RIDCard").get(0)+" mRPSParentName "+mDetailMap.get("fenju").get(0)+" createdBy "+ mDetailMap.get("ROwnerTel").get(0)
				+" mrentType "+mDetailMap.get("rentType").get(0)+" mowntype "+"default null"+"  RBuildingType "+"defautl null ");
		
//		Log.e("mingguo", "link address  "+(mSelectorInfo.get("fenju").getHouseAllLinkName()[mSelectorInfo.get("fenju").getHouseSelectPosition()]+
//				mSelectorInfo.get("police").getHouseAllLinkName()[mSelectorInfo.get("police").getHouseSelectPosition()]
//				+mSelectorInfo.get("road").getHouseSelectValue()+mRAddress+mRDoor));
		String url = CommonUtil.mUserHost+"services.asmx?op=AddRentInfo";
		
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUpdateHouseAction));
		rpc.addProperty("RentNo", mRentNo);   
		rpc.addProperty("RDName", mRDName);      
		rpc.addProperty("RSName", mRSName);  
		rpc.addProperty("RRName", mDetailMap.get("road").get(0));     //小区 
		rpc.addProperty("RPSName", mDetailMap.get("police").get(0));   //派出所
		rpc.addProperty("RAddress", address);   
		rpc.addProperty("RDoor", mDetailMap.get("currentDoor").get(0));    
		rpc.addProperty("RTotalDoor", "6"); 
		
		rpc.addProperty("RRoomType", mDetailMap.get("roomType").get(0)); 
		rpc.addProperty("RDirection", mDetailMap.get("direction").get(0));   
		rpc.addProperty("RStructure", "01"); 
		rpc.addProperty("RBuildingType", "02"); 
		rpc.addProperty("RFloor", mDetailMap.get("currentFloor").get(0));     
		rpc.addProperty("RTotalFloor", mDetailMap.get("totalFloor").get(0));   
		rpc.addProperty("RHouseAge", "15");  
		rpc.addProperty("RRentArea", mDetailMap.get("area").get(0));     
		
		rpc.addProperty("RProperty", "fangchanxingzhi");  
		rpc.addProperty("ROwner", mDetailMap.get("ROwner").get(0));        
		rpc.addProperty("ROwnerTel", mDetailMap.get("ROwnerTel").get(0));  
		rpc.addProperty("RIDCard", mDetailMap.get("RIDCard").get(0));       
		rpc.addProperty("RLocationDescription", mDetailMap.get("RLocationDescription").get(0));  
		rpc.addProperty("RPSParentName", mDetailMap.get("fenju").get(0)); //分局           
		rpc.addProperty("createdBy", mDetailMap.get("ROwnerTel").get(0));             
		rpc.addProperty("rentType", mDetailMap.get("rentType").get(0));                
		rpc.addProperty("ownType", "02");       
		
		mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mUpdateHouseAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
	
	private void getHouseFenju(){
		List<String> policeList = mDetailMap.get("police");
		if (policeList != null && policeList.size() > 1){
			policeList.set(1, "");
		}
		mPoliceTextView.setText("");
		List<String> roadList = mDetailMap.get("road");
		if (roadList != null && roadList.size() > 1){
			roadList.set(1, "");
		}
		mRoadTextView.setText("");
		
		String url = CommonUtil.mUserHost+"services.asmx?op=GetPoliceStationList";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mFenjuAction));
		mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mFenjuAction, rpc);
		mPresenter.startPresentServiceTask(true);
		
//		if (!mSelectorInfo.containsKey("fenju")){
//			HouseSelectorModel direction = new HouseSelectorModel();
//			direction.setHouseOrginText((String) mFenjuTextView.getText());
//			mSelectorInfo.put("fenju", direction);
//			String url = CommonUtil.mUserHost+"services.asmx?op=GetPoliceStationList";
//			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mFenjuAction));
//			mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mFenjuAction, rpc);
//			mPresenter.startPresentServiceTask(true);
//		}else{
//			showAlertDialog(mFenjuTextView, "fenju", mSelectorInfo.get("fenju").getHouseAllContent());
//		}
	}
	
	private void getHousePolice(String fenjuId){
			
			String url = CommonUtil.mUserHost+"services.asmx?op=GetLocalPoliceStationList";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mPoliceAction));
//			if (mSelectorInfo.get("fenju").getHouseSelectId() != null && !mSelectorInfo.get("fenju").getHouseSelectId().equals("")){
				rpc.addProperty("parentstationId", fenjuId);
				mPresenter.readyPresentServiceParams(EditHouseInfoActivity.this, url, mPoliceAction, rpc);
				mPresenter.startPresentServiceTask(true);
//			}
	}
	
	private void showAlertDialog(final TextView text, final String tag, final String[] idItems, final String[] valueitems) {  
			  AlertDialog.Builder builder =new AlertDialog.Builder(EditHouseInfoActivity.this);
			  
			  builder.setItems(valueitems, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					text.setText("   "+valueitems[which]);
					List<String> list = mDetailMap.get(tag);
					if (list != null){
						list.set(0, idItems[which]);
						list.set(1, valueitems[which]);
					}
				}
			});
			builder.show();
	}
	
	private void showPoliceAlertDialog(final TextView text, final String tag, final String[] idItems, final String[] valueitems, final String[] roadItems) {  
		  AlertDialog.Builder builder =new AlertDialog.Builder(EditHouseInfoActivity.this);
		  
		  builder.setItems(valueitems, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				text.setText("   "+valueitems[which]);
				List<String> list = mDetailMap.get(tag);
				if (list != null){
					list.set(0, idItems[which]);
					list.set(1, valueitems[which]);
					list.set(2, roadItems[which]);
				}
			}
		});
		builder.show();
	}
	
	private void jsonHouseInfoToView(String value) {
		if (value != null) {
			JSONArray array;
			try {
				array = new JSONArray(value);
				JSONObject object = array.optJSONObject(0);
				if (object != null) {
					List<String> contentRID = new ArrayList<>();
					contentRID.add(object.optString("RID"));
					mDetailMap.put("RID", contentRID);
					List<String> content2 = new ArrayList<>();
					content2.add("01");
					mDetailMap.put("RDName", content2);
					List<String> content3 = new ArrayList<>();
					content3.add("02");
					mDetailMap.put("RSName", content3);
					List<String> roadList = new ArrayList<>();
					roadList.add(object.optString("RRNameID"));
					roadList.add(object.optString("RRName"));
					roadList.add("");
					mDetailMap.put("road", roadList);
					List<String> policeList = new ArrayList<>();
					policeList.add(object.optString("RPSID"));
					policeList.add(object.optString("RPSName"));
					policeList.add(object.optString("PSStreetDesc"));
					mDetailMap.put("police", policeList);
					
					List<String> currentDoorList = new ArrayList<>();
					currentDoorList.add(object.optString("RDoor"));
					mDetailMap.put("currentDoor", currentDoorList);
					List<String> totalDoorList = new ArrayList<>();
					totalDoorList.add("6");
					mDetailMap.put("totalDoor", totalDoorList);
					List<String> roomTypeList = new ArrayList<>();
					roomTypeList.add(object.optString("RRoomType"));
					roomTypeList.add(object.optString("RRoomTypeDesc"));
					mDetailMap.put("roomType", roomTypeList);
					List<String> directionList = new ArrayList<>();
					directionList.add(object.optString("RDirection"));
					directionList.add(object.optString("RDirectionDesc"));
					mDetailMap.put("direction", directionList);
					List<String> strucctureList = new ArrayList<>();
					strucctureList.add("01");
					mDetailMap.put("structure", strucctureList);
					List<String> buildTypeList = new ArrayList<>();
					buildTypeList.add("02");
					mDetailMap.put("RBuildingType", buildTypeList);
					List<String> currentFloorList = new ArrayList<>();
					currentFloorList.add(object.optString("RFloor"));
					mDetailMap.put("currentFloor", currentFloorList);
					
					List<String> totalFloorList = new ArrayList<>();
					totalFloorList.add(object.optString("RTotalFloor"));
					mDetailMap.put("totalFloor", totalFloorList);
					List<String> ageList = new ArrayList<>();
					ageList.add("15");
					mDetailMap.put("RHouseAge", ageList);
					List<String> areaList = new ArrayList<>();
					areaList.add(object.optString("RRentArea"));
					mDetailMap.put("area", areaList);
					List<String> propertyList = new ArrayList<>();
					propertyList.add(object.optString("RProperty"));
					mDetailMap.put("property", propertyList);
					List<String> ownerList = new ArrayList<>();
					ownerList.add(object.optString("ROwner"));
					mDetailMap.put("ROwner", ownerList);
					List<String> telList = new ArrayList<>();
					telList.add(object.optString("ROwnerTel"));
					mDetailMap.put("ROwnerTel", telList);
					List<String> idCardList = new ArrayList<>();
					idCardList.add(object.optString("RIDCard"));
					mDetailMap.put("RIDCard", idCardList);
					List<String> priceList = new ArrayList<>();
					priceList.add(object.optString("RLocationDescription"));
					mDetailMap.put("RLocationDescription", priceList);
					List<String> parentList = new ArrayList<>();
					parentList.add(object.optString("RDParentNO"));
					parentList.add(object.optString("RPSParentName"));
					parentList.add(object.optString("PSDescription"));
					mDetailMap.put("fenju", parentList);
					List<String> createList = new ArrayList<>();
					createList.add(CommonUtil.mUserLoginName);
					mDetailMap.put("createdBy", createList);
					List<String> typeList = new ArrayList<>();
					typeList.add(object.optString("RRentType"));
					typeList.add(object.optString("RRentTypeDesc"));
					mDetailMap.put("rentType", typeList);
					List<String> ownTypeList = new ArrayList<>();
					ownTypeList.add("02");
					mDetailMap.put("ownType", ownTypeList);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
		
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1000){
				jsonHouseInfoToView((String)msg.obj);
				showDetailView();
				getPayRateDesc(mDetailMap.get("RLocationDescription").get(0));
			}
			else if (msg.what == 100){
//				
//				mSelectorInfo.get("property").setHouseAllContent(JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
//				mSelectorInfo.get("property").setHouseAllId(JsonObjectParse.parseHouseProperty((String)msg.obj).get(0));
//				showAlertDialog(mPropertryTextView,"property" , JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
			}else if (msg.what == 101){
				List<String[]> list = JsonObjectParse.parseHouseProperty((String)msg.obj);
				showAlertDialog(mTypeTextView, "roomType", list.get(0), list.get(1));
			}else if (msg.what == 102){				
//				mSelectorInfo.get("direction").setHouseAllContent(JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
//				mSelectorInfo.get("direction").setHouseAllId(JsonObjectParse.parseHouseProperty((String)msg.obj).get(0));
//				showAlertDialog(mDirectionTextView, "direction", JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
				List<String[]> list = JsonObjectParse.parseHouseProperty((String)msg.obj);
				showAlertDialog(mDirectionTextView, "direction", list.get(0), list.get(1));
			}else if (msg.what == 103){
//				
//				mSelectorInfo.get("structure").setHouseAllContent(JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
//				mSelectorInfo.get("structure").setHouseAllId(JsonObjectParse.parseHouseProperty((String)msg.obj).get(0));
//				showAlertDialog(mStructureTextView, "structure", JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
			}else if (msg.what == 104){
//				
//				mSelectorInfo.get("district").setHouseAllContent(JsonObjectParse.parseHouseDistrict((String)msg.obj).get(1));
//				mSelectorInfo.get("district").setHouseAllId(JsonObjectParse.parseHouseDistrict((String)msg.obj).get(0));
//				showAlertDialog(mDistrictTextView, "district", JsonObjectParse.parseHouseDistrict((String)msg.obj).get(1));
			}else if (msg.what == 105){
				
//				mSelectorInfo.get("street").setHouseAllContent(JsonObjectParse.parseHouseStreet((String)msg.obj).get(1));
//				mSelectorInfo.get("street").setHouseAllId(JsonObjectParse.parseHouseStreet((String)msg.obj).get(0));
//				showAlertDialog(mStreetTextView, "street", JsonObjectParse.parseHouseStreet((String)msg.obj).get(1));
			}else if (msg.what == 106){
				List<String[]> list = JsonObjectParse.parseHouseRoad((String)msg.obj);
				showAlertDialog(mRoadTextView, "road", list.get(0), list.get(1));
			}else if (msg.what == 107){
				List<String[]> list = JsonObjectParse.parseHouseFenju((String)msg.obj);
				showPoliceAlertDialog(mFenjuTextView, "fenju", list.get(0), list.get(1), list.get(2));
			}else if (msg.what == 108){
				List<String[]> list = JsonObjectParse.parseHouseFenju((String)msg.obj);
				showPoliceAlertDialog(mPoliceTextView, "police", list.get(0), list.get(1), list.get(2));
			}else if (msg.what == 109){
				
//				mSelectorInfo.get("rentType").setHouseAllContent(JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
//				mSelectorInfo.get("rentType").setHouseAllId(JsonObjectParse.parseHouseProperty((String)msg.obj).get(0));
				List<String[]> list = JsonObjectParse.parseHouseProperty((String)msg.obj);
				showAlertDialog(mRentTypeTextView, "rentType",list.get(0), list.get(1));
			}else if (msg.what == 110){
//				
//				mSelectorInfo.get("ownertype").setHouseAllContent(JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
//				mSelectorInfo.get("ownertype").setHouseAllId(JsonObjectParse.parseHouseProperty((String)msg.obj).get(0));
//				showAlertDialog(mOwnerTypeTextView, "ownertype", JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
				
			}else if (msg.what == 111){
//				
//				mSelectorInfo.get("buildingstructure").setHouseAllContent(JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
//				mSelectorInfo.get("buildingstructure").setHouseAllId(JsonObjectParse.parseHouseProperty((String)msg.obj).get(0));
//				showAlertDialog(mBuildingStructureTextView, "buildingstructure", JsonObjectParse.parseHouseProperty((String)msg.obj).get(1));
			}else if (msg.what == 888){
				String value = (String)msg.obj;
				if (value != null && value.equals("true")){
					LogUtil.e("mingguo", "add rent info success ");
					Toast.makeText(getApplicationContext(), "修改房屋成功", Toast.LENGTH_SHORT).show();
//					Intent photoIntent = new Intent(AddHouseInfoActivity.this, SelectPhotoActivity.class);
//					photoIntent.putExtra("rentNo", mRentNo);
//					startActivity(photoIntent);
					finish();
				}else{
					Toast.makeText(getApplicationContext(), "登记房屋失败", Toast.LENGTH_SHORT).show();
				}
			}else if (msg.what == 112){
				String value = (String)msg.obj;
//				if (value != null && value.equals("true")){
//					mValidHouseId = true;
//				}else{
//					mValidHouseId = false;
//					Toast.makeText(getApplicationContext(), "房产证编号输入有误", Toast.LENGTH_SHORT).show();
//				}
			}else if(msg.what==818){
				String value = (String)msg.obj;
				LogUtil.e("", value+"----------");
				//显示服务费信息
				///////////////////////////////////////////////////////////////////////////////
				Gson gson=new Gson();
				ServiceCharge serviceCharge = gson.fromJson(value, ServiceCharge.class);
				if(serviceCharge.fee.startsWith("0")){
					commission.setText(serviceCharge.fee.substring(1)+"元");
					
				}else{
					commission.setText(serviceCharge.fee+"元");
				}
				explanation.setText(serviceCharge.msg);
			}
		}
		
	};
	private EditText mPriceEditText;
	

	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
//			if (keyCode == KeyEvent.KEYCODE_BACK) {
//				if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE){
//					
//					return false;
//				}
//			}
			return super.onKeyDown(keyCode, event);
		}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mHouseDetailAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 1000;
				msg.obj = templateInfo;
				msg.sendToTarget();
//			}
//			else if (action.equals(mPropertyAction)){
//				Message msg = mHandler.obtainMessage();
//				msg.what = 100;
//				msg.obj = templateInfo;
//				msg.sendToTarget();
			}else if (action.equals(mTypeAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 101;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mDirectionAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 102;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mStuctureAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 103;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mDistrictAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 104;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mStreetAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 105;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mRoadAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 106;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mFenjuAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 107;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mPoliceAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 108;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mRentTypeAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 109;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mOwnerTypeAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 110;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mBuildingStuctureAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 111;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mUpdateHouseAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 888;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mValidHouseIDAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 112;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if(action.equals(mGetPayRateDesc)){
				Message msg = mHandler.obtainMessage();
				msg.what = 818;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
		
	}

}
