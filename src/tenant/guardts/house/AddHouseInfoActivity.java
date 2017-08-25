package tenant.guardts.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.model.HouseSelectorModel;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.view.AddressSelector;
import tenant.guardts.house.view.CityInterface;
import tenant.guardts.house.view.OnItemClickListener;

public class AddHouseInfoActivity extends BaseActivity {

	private TextView mTitleBar;
	private View mLoadingView;
	private HoursePresenter mPresenter;
	private String mPropertyAction = "http://tempuri.org/GetHouseProperty";
	private String mTypeAction = "http://tempuri.org/GetHouseType";
	private String mDirectionAction = "http://tempuri.org/GetHouseDirection";
	private String mStuctureAction = "http://tempuri.org/GetHouseStructure";
	private String mBuildingStuctureAction = "http://tempuri.org/GetBuildingStructure";
	private String mDistrictAction = "http://tempuri.org/GetDistrictList";
	private String mStreetAction = "http://tempuri.org/GetStreetList";
	private String mRoadAction = "http://tempuri.org/GetRoadList";
	private String mFenjuAction = "http://tempuri.org/GetPoliceStationList";
	private String mPoliceAction = "http://tempuri.org/GetLocalPoliceStationList";
	private String mRentTypeAction = "http://tempuri.org/GetHouseRentType";
	private String mOwnerTypeAction = "http://tempuri.org/GetHouseOwnType";
	private String mAddHouseAction = "http://tempuri.org/AddRentInfo";
	private String mValidHouseIDAction = "http://tempuri.org/ValidateHouseID";
	// private Map<String, String> mSelectedMap = new HashMap<>();
	// private Map<String, String> mOriginText = new HashMap<>();
	// private Map<String, String[]> mAllList = new HashMap<>();
	private Map<String, HouseSelectorModel> mSelectorInfo = new HashMap<>();
	private TextView mPropertryTextView, mTypeTextView;
	private TextView mDirectionTextView;
	private TextView mStructureTextView;
	private TextView mDistrictTextView;
	private TextView mStrictTextView;
	private TextView mStreetTextView;
	private TextView mRoadTextView;
	private TextView mFenjuTextView;
	private TextView mPoliceTextView;
	//private TextView mRentTypeTextView;
	private TextView mHouseTypeTextView;
	private TextView mOwnerTypeTextView;
	private String mRentNo;
	private String mRDName;
	private String mRSName;
	private String mRRName;
	private String mRPSName;
	private String mRAddress;
	private String mRDoor;
	private String mRTotalDoor;
	private String mRRoomType;
	private String mRDirection;
	private String mRStructure;
	private String mRTotalFloor;
	private String mRFloor;
	private String mRHouseAge;
	private String mRRentArea;
	private String mRProperty;
	private String mROwner;
	private String mROwnerTel;
	private String mRIDCard;
	private String mRPSParentName;
	private String mrentType, mownType;
	private TextView mBuildingStructureTextView;
	private String mRBuildingType;
	private String mUserName;
	private boolean mValidHouseId = false;
	private EditText mHouseNo;

	private ArrayList<City> cities1 = new ArrayList<>();
	private ArrayList<City> cities2 = new ArrayList<>();
	private ArrayList<City> cities3 = new ArrayList<>();
	private AddressSelector addressSelector;
	private LinearLayout linearLayout;
	private FrameLayout regionFrameLayout;
	private PopupWindow popupWindow;
	private String district;// 区
	private String street;// 街
	private String road;// 道
	private ImageView mImagView;
	private TextView mRentalModeTextView;// 出租方式
	private TextView mLeaseTypeTextView;// 租赁类型
	private TextView mConfigTextView;// 房源配置
	private AlertDialog mAlertDialog;
	private EditText mHousePrice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.add_house_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("发布房屋");
		initData();
		initView();
	}

	/**
	 * 初始化区域数据
	 */
	private void initData() {
		cities1.add(new City("虹桥区"));
		cities1.add(new City("河西区"));
		cities1.add(new City("河东区"));
		cities1.add(new City("和平区"));
		cities1.add(new City("海港区"));

		cities2.add(new City("邵公庄街"));
		cities2.add(new City("咸阳北路街"));
		cities2.add(new City("芥园道街"));
		cities2.add(new City("丁字沽街"));

		cities3.add(new City("道1"));
		cities3.add(new City("道2"));
		cities3.add(new City("道3"));
		cities3.add(new City("道4"));
		cities3.add(new City("道5"));
		cities3.add(new City("道6"));
		cities3.add(new City("道7"));

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mUserName = getIntent().getStringExtra("user_name");
	}

	private void initView() {
		linearLayout = (LinearLayout) findViewById(R.id.ll);
		mImagView = (ImageView) findViewById(R.id.iv);
		regionFrameLayout = (FrameLayout) findViewById(R.id.id_add_house_quyu);
		regionFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 弹出地址选择器
				initPopupWindow();

			}
		});

		mHousePrice = (EditText)findViewById(R.id.id_add_house_price);
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mLoadingView = (View) findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);

		// 出租方式
//		FrameLayout rentalModeFrameLayout = (FrameLayout) findViewById(R.id.id_add_house_rental_mode);
//		mRentalModeTextView = (TextView) findViewById(R.id.id_add_house_rental_mode_text);
//		rentalModeFrameLayout.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(AddHouseInfoActivity.this, "出租方式", Toast.LENGTH_SHORT).show();
//				getHouseProperty();
//			}
//		});

		// 租赁类型
		FrameLayout leaseTypeFrameLayout = (FrameLayout) findViewById(R.id.id_add_house_lease_type);
		mLeaseTypeTextView = (TextView) findViewById(R.id.id_add_house_lease_type_text);
		leaseTypeFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getHouseRentType();
			}
		});

		// 房型
		FrameLayout typeFrameLayout = (FrameLayout) findViewById(R.id.id_add_house_type);
		mTypeTextView = (TextView) findViewById(R.id.id_add_house_type_text);
		typeFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getHouseType();
			}
		});

		// 朝向
		FrameLayout directionFrameLayout = (FrameLayout) findViewById(R.id.id_add_house_direction);
		mDirectionTextView = (TextView) findViewById(R.id.id_add_house_direction_text);
		directionFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getHouseDirection();
			}
		});

		// 房屋配置
		FrameLayout configurationFrameLayout = (FrameLayout) findViewById(R.id.id_add_house_configuration);
		mConfigTextView = (TextView) findViewById(R.id.id_add_house_configuration_text);
		configurationFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getHouseConfiguration();
			}
		});

		// 发布房屋信息
		Button okButton = (Button) findViewById(R.id.id_add_house_info_confirm);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (checkInputContent()) {
					// 跳转
					startAddHouseInfo();
				}
			}
		});
	}

	/**
	 * 获得房屋配置
	 */
	protected void getHouseConfiguration() {
		final ArrayList<String> list = new ArrayList<>();
		final String[] items = { "空调", "电视", "网络", "热水", "冰箱", "燃气" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setMultiChoiceItems(items, null, new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {

				if (isChecked) {
					list.add(items[which]);
				} else {
					list.remove(items[which]);
				}

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				mAlertDialog.dismiss();
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(AddHouseInfoActivity.this, list.toString(), Toast.LENGTH_SHORT).show();
				mAlertDialog.dismiss();
			}
		});

		mAlertDialog = builder.create();
		mAlertDialog.show();

	}

	/**
	 * 初始化PopupWindow
	 */
	protected void initPopupWindow() {
		// 产生背景变暗效果
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.4f;
		getWindow().setAttributes(lp);
		View view = View.inflate(this, R.layout.popupwindow_layout, null);
		addressSelector = (AddressSelector) view.findViewById(R.id.address);
		addressSelector.setCities(cities1);
		addressSelector.setListTextSelectedColor(Color.parseColor("#2894ff"));
		popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		Drawable drawable = getResources().getDrawable(R.drawable.popupwindow_bg);
		popupWindow.setBackgroundDrawable(drawable);
		popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int popHeight = popupWindow.getContentView().getMeasuredHeight();
		EditText area = (EditText) findViewById(R.id.id_add_house_area);
		popupWindow.showAsDropDown(area, 0, -popHeight);
		initEvent();

	}

	private void initEvent() {
		addressSelector.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void itemClick(AddressSelector addressSelector, CityInterface city, int tabPosition) {
				String name = city.getCityName();
				switch (tabPosition) {
				case 0:
					addressSelector.setCities(cities2);
					district = city.getCityName();
					break;
				case 1:
					addressSelector.setCities(cities3);
					street = city.getCityName();
					break;
				case 2:
					road = name;
					Toast.makeText(AddHouseInfoActivity.this, district + street + road, Toast.LENGTH_SHORT).show();
					popupWindow.dismiss();
					break;
				}
			}
		});
		addressSelector.setOnTabSelectedListener(new AddressSelector.OnTabSelectedListener() {
			@Override
			public void onTabSelected(AddressSelector addressSelector, AddressSelector.Tab tab) {
				switch (tab.getIndex()) {
				case 0:
					addressSelector.setCities(cities1);
					break;
				case 1:
					addressSelector.setCities(cities2);
					break;
				case 2:
					addressSelector.setCities(cities3);
					break;
				}
			}

			@Override
			public void onTabReselected(AddressSelector addressSelector, AddressSelector.Tab tab) {

			}
		});

		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});

	}

	/**
	 * 检测输入内容
	 * 
	 * @return
	 */
	private boolean checkInputContent() {

		if (mSelectorInfo.get("type") == null || mSelectorInfo.get("type").getHouseSelectId() == null) {
			Toast.makeText(getApplicationContext(), "请选择房型", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			mRRoomType = mSelectorInfo.get("type").getHouseSelectId();
		}

		if (mSelectorInfo.get("direction") == null || mSelectorInfo.get("direction").getHouseSelectId() == null) {
			Toast.makeText(getApplicationContext(), "请选择朝向", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			mRDirection = mSelectorInfo.get("direction").getHouseSelectId();
		}

		// if (mSelectorInfo.get("district") == null ||
		// mSelectorInfo.get("district").getHouseSelectId() == null) {
		// Toast.makeText(getApplicationContext(), "请选择区域",
		// Toast.LENGTH_SHORT).show();
		// return false;
		// } else {
		// mRDName = mSelectorInfo.get("district").getHouseSelectId();
		// }

		// if (mSelectorInfo.get("renttype") == null ||
		// mSelectorInfo.get("renttype").getHouseSelectId() == null) {
		// Toast.makeText(getApplicationContext(), "请选择租赁类型",
		// Toast.LENGTH_SHORT).show();
		// return false;
		// } else {
		// mrentType = mSelectorInfo.get("renttype").getHouseSelectId();
		// }

		EditText area = (EditText) findViewById(R.id.id_add_house_area);
		area.setHintTextColor(Color.parseColor("#b2b2b2"));
		if (area.getText().toString() == null || area.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入房屋面积", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			mRRentArea = area.getText().toString();
		}

		EditText houseNum = (EditText) findViewById(R.id.id_add_house_current_num);
		houseNum.setHintTextColor(Color.parseColor("#b2b2b2"));
		if (houseNum.getText().toString() == null || houseNum.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入门牌号", Toast.LENGTH_SHORT).show();
			return false;
		} else {

		}

		EditText address = (EditText) findViewById(R.id.id_add_house_address);
		address.setHintTextColor(Color.parseColor("#b2b2b2"));
		if (address.getText().toString() == null || address.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入详细地址", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			mRAddress = address.getText().toString();
		}

		EditText owner_name = (EditText) findViewById(R.id.id_add_house_owner_name);
		owner_name.setHintTextColor(Color.parseColor("#b2b2b2"));
		if (owner_name.getText().toString() == null || owner_name.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入房主姓名", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			mROwner = owner_name.getText().toString();
		}
		EditText phone = (EditText) findViewById(R.id.id_add_house_owner_phone);
		phone.setHintTextColor(Color.parseColor("#b2b2b2"));
		if (phone.getText().toString() == null || phone.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入房主手机", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			mROwnerTel = phone.getText().toString();
		}

		return true;

	}

	private void getHouseValidId() {
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=ValidateHouseID";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mValidHouseIDAction));
		rpc.addProperty("houseID", mHouseNo.getText().toString());
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mValidHouseIDAction, rpc);
		mPresenter.startPresentServiceTask();
	}

	private void getHouseProperty() {
		if (!mSelectorInfo.containsKey("property")) {
			showLoadingView();
			HouseSelectorModel property = new HouseSelectorModel();
			property.setHouseOrginText((String) 	mRentalModeTextView.getText());
			mSelectorInfo.put("property", property);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetHouseProperty";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mPropertyAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mPropertyAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(	mRentalModeTextView, "property", mSelectorInfo.get("property").getHouseAllContent());
		}

		// [{"RSOUrl":"0","IsVisible":null,"RSOName":"私产","RSOParentNo":8,"RSOID":15,"RSONo":0,"RSOOrder":1},{"RSOUrl":"0","IsVisible":null,"RSOName":"公产","RSOParentNo":8,"RSOID":16,"RSONo":0,"RSOOrder":2}]
	}

	private void getHouseType() {
		if (!mSelectorInfo.containsKey("type")) {
			showLoadingView();
			HouseSelectorModel type = new HouseSelectorModel();
			type.setHouseOrginText((String) mTypeTextView.getText());
			mSelectorInfo.put("type", type);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetHouseType";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mTypeAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mTypeAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(mTypeTextView, "type", mSelectorInfo.get("type").getHouseAllContent());
		}
	}

	private void getHouseDirection() {
		if (!mSelectorInfo.containsKey("direction")) {
			showLoadingView();
			HouseSelectorModel direction = new HouseSelectorModel();
			direction.setHouseOrginText((String) mDirectionTextView.getText());
			mSelectorInfo.put("direction", direction);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetHouseDirection";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mDirectionAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mDirectionAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(mDirectionTextView, "direction", mSelectorInfo.get("direction").getHouseAllContent());
		}
	}

	private void getHouseStructure() {
		if (!mSelectorInfo.containsKey("structure")) {
			showLoadingView();
			HouseSelectorModel direction = new HouseSelectorModel();
			direction.setHouseOrginText((String) mStructureTextView.getText());
			mSelectorInfo.put("structure", direction);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetHouseStructure";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mStuctureAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mStuctureAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(mStructureTextView, "structure", mSelectorInfo.get("structure").getHouseAllContent());
		}

	}

	private void getHouseBuildingStructure() {
		if (!mSelectorInfo.containsKey("buildingstructure")) {
			showLoadingView();
			HouseSelectorModel direction = new HouseSelectorModel();
			direction.setHouseOrginText((String) mBuildingStructureTextView.getText());
			mSelectorInfo.put("buildingstructure", direction);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetBuildingStructure";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mBuildingStuctureAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mBuildingStuctureAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(mBuildingStructureTextView, "buildingstructure",
					mSelectorInfo.get("buildingstructure").getHouseAllContent());
		}

	}

	private void getHouseRentType() {
		if (!mSelectorInfo.containsKey("renttype")) {
			showLoadingView();
			HouseSelectorModel direction = new HouseSelectorModel();
			direction.setHouseOrginText((String) mLeaseTypeTextView.getText());
			mSelectorInfo.put("renttype", direction);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetHouseRentType";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentTypeAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mRentTypeAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(mLeaseTypeTextView, "renttype", mSelectorInfo.get("renttype").getHouseAllContent());
		}
	}

	private void getHouseOwnersType() {
		if (!mSelectorInfo.containsKey("ownertype")) {
			showLoadingView();
			HouseSelectorModel direction = new HouseSelectorModel();
			direction.setHouseOrginText((String) mOwnerTypeTextView.getText());
			mSelectorInfo.put("ownertype", direction);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetHouseOwnType";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mOwnerTypeAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mOwnerTypeAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(mOwnerTypeTextView, "ownertype", mSelectorInfo.get("ownertype").getHouseAllContent());
		}

	}

	private void getHouseDistrict() {
		if (mSelectorInfo.containsKey("street")) {
			mStreetTextView.setText(mSelectorInfo.get("street").getHouseOrginText());
		}
		if (mSelectorInfo.containsKey("road")) {
			mRoadTextView.setText(mSelectorInfo.get("road").getHouseOrginText());
		}
		if (!mSelectorInfo.containsKey("district")) {
			showLoadingView();
			HouseSelectorModel direction = new HouseSelectorModel();
			direction.setHouseOrginText((String) mDistrictTextView.getText());
			mSelectorInfo.put("district", direction);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetDistrictList";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mDistrictAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mDistrictAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(mDistrictTextView, "district", mSelectorInfo.get("district").getHouseAllContent());
		}

	}

	private void getHouseRoad() {
		showLoadingView();
		HouseSelectorModel direction = new HouseSelectorModel();
		direction.setHouseOrginText((String) mRoadTextView.getText());
		mSelectorInfo.put("road", direction);
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetRoadList";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRoadAction));
		if (mSelectorInfo.get("street").getHouseSelectId() != null
				&& !mSelectorInfo.get("street").getHouseSelectId().equals("")) {
			rpc.addProperty("district", mSelectorInfo.get("district").getHouseSelectId());
			rpc.addProperty("street", mSelectorInfo.get("street").getHouseSelectId());
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mRoadAction, rpc);
			mPresenter.startPresentServiceTask();
		}
	}

	private void startAddHouseInfo() {
		Log.w("mingguo", "add house info mRentNo " + mRentNo + " mRDName " + mRDName + " mRSName " + mRSName
				+ " mRRName " + mRRName + " mRPSName " + mRPSName + " mRAddress " + mRAddress + " mRDoor " + mRDoor
				+ " mRTotalDoor " + mRTotalDoor + " mRRoomType " + mRRoomType + " mRDirection " + mRDirection
				+ " mRStructure " + mRStructure + " mRFloor " + mRFloor + " mRTotalFloor " + mRTotalFloor
				+ " mRHouseAge " + mRHouseAge + " mRRentArea " + mRRentArea + " mRProperty " + mRProperty + " mROwner "
				+ mROwner + " mROwnerTel " + mROwnerTel + " mRIDCard " + mRIDCard + " mRPSParentName " + mRPSParentName
				+ " createdBy " + "mnz" + " mrentType " + mrentType + " mowntype " + mownType + "  RBuildingType "
				+ mRBuildingType + "  RLocationDescription " + " desp ");
		showLoadingView();
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=AddRentInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mAddHouseAction));
		rpc.addProperty("RentNo", "111");
		rpc.addProperty("RDName", "111");
		rpc.addProperty("RSName", "111");
		rpc.addProperty("RRName", "111");
		rpc.addProperty("RPSName", "111");
		rpc.addProperty("RAddress", mRAddress);
		rpc.addProperty("RDoor", "111");
		rpc.addProperty("RTotalDoor", "111");

		rpc.addProperty("RRoomType", mRRoomType);
		rpc.addProperty("RDirection", mRDirection);
		rpc.addProperty("RStructure", "111");
		rpc.addProperty("RBuildingType", "111");
		rpc.addProperty("RFloor", "111");
		rpc.addProperty("RTotalFloor", "111");
		rpc.addProperty("RHouseAge", "111");
		rpc.addProperty("RRentArea", mRRentArea);

		rpc.addProperty("RProperty", "111");
		rpc.addProperty("ROwner", mROwner);
		rpc.addProperty("ROwnerTel", mROwnerTel);
		rpc.addProperty("RIDCard", "111");
		rpc.addProperty("RLocationDescription", mHousePrice.getText().toString());
		rpc.addProperty("RPSParentName", "111");
		rpc.addProperty("createdBy", mUserName);
		rpc.addProperty("rentType", "111");
		rpc.addProperty("ownType", "111");

		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mAddHouseAction, rpc);
		mPresenter.startPresentServiceTask();
	}

	private void getHouseStreet() {
		if (mSelectorInfo.containsKey("road")) {
			mRoadTextView.setText(mSelectorInfo.get("road").getHouseOrginText());
		}
		showLoadingView();
		HouseSelectorModel direction = new HouseSelectorModel();
		direction.setHouseOrginText((String) mStreetTextView.getText());
		mSelectorInfo.put("street", direction);
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetStreetList";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mStreetAction));
		if (mSelectorInfo.get("district").getHouseSelectId() != null
				&& !mSelectorInfo.get("district").getHouseSelectId().equals("")) {
			rpc.addProperty("district", mSelectorInfo.get("district").getHouseSelectId());
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mStreetAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {

		}
	}

	private void getHouseFenju() {
		if (mSelectorInfo.containsKey("police")) {
			mPoliceTextView.setText(mSelectorInfo.get("police").getHouseOrginText());
		}

		if (!mSelectorInfo.containsKey("fenju")) {
			showLoadingView();
			HouseSelectorModel direction = new HouseSelectorModel();
			direction.setHouseOrginText((String) mFenjuTextView.getText());
			mSelectorInfo.put("fenju", direction);
			String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetPoliceStationList";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mFenjuAction));
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mFenjuAction, rpc);
			mPresenter.startPresentServiceTask();
		} else {
			showAlertDialog(mFenjuTextView, "fenju", mSelectorInfo.get("fenju").getHouseAllContent());
		}
	}

	private void getHousePolice() {
		showLoadingView();
		HouseSelectorModel direction = new HouseSelectorModel();
		direction.setHouseOrginText((String) mPoliceTextView.getText());
		mSelectorInfo.put("police", direction);
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetLocalPoliceStationList";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mPoliceAction));
		if (mSelectorInfo.get("fenju").getHouseSelectId() != null
				&& !mSelectorInfo.get("fenju").getHouseSelectId().equals("")) {
			rpc.addProperty("parentstationId", mSelectorInfo.get("fenju").getHouseSelectId());
			mPresenter.readyPresentServiceParams(getApplicationContext(), url, mPoliceAction, rpc);
			mPresenter.startPresentServiceTask();
		}
	}

	private void showAlertDialog(final TextView text, final String tag, final String[] items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(AddHouseInfoActivity.this, AlertDialog.THEME_HOLO_LIGHT);

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mSelectorInfo.get(tag).setHouseSelectValue(items[which]);
				if (mSelectorInfo.get(tag).getHouseAllId() != null
						&& mSelectorInfo.get(tag).getHouseAllId().length > 0) {
					mSelectorInfo.get(tag).setHouseSelectId(mSelectorInfo.get(tag).getHouseAllId()[which]);
				}

				text.setText(mSelectorInfo.get(tag).getHouseOrginText() + "   " + items[which]);
			}
		});
		builder.show();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100) {
				dismissLoadingView();
				mSelectorInfo.get("property").setHouseAllContent(parseHouseProperty((String) msg.obj).get(1));
				mSelectorInfo.get("property").setHouseAllId(parseHouseProperty((String) msg.obj).get(0));
				showAlertDialog(mPropertryTextView, "property", parseHouseProperty((String) msg.obj).get(1));
			} else if (msg.what == 101) {
				dismissLoadingView();
				mSelectorInfo.get("type").setHouseAllContent(parseHouseProperty((String) msg.obj).get(1));
				mSelectorInfo.get("type").setHouseAllId(parseHouseProperty((String) msg.obj).get(0));
				showAlertDialog(mTypeTextView, "type", parseHouseProperty((String) msg.obj).get(1));
			} else if (msg.what == 102) {
				dismissLoadingView();
				mSelectorInfo.get("direction").setHouseAllContent(parseHouseProperty((String) msg.obj).get(1));
				mSelectorInfo.get("direction").setHouseAllId(parseHouseProperty((String) msg.obj).get(0));
				showAlertDialog(mDirectionTextView, "direction", parseHouseProperty((String) msg.obj).get(1));
			} else if (msg.what == 103) {
				dismissLoadingView();
				mSelectorInfo.get("structure").setHouseAllContent(parseHouseProperty((String) msg.obj).get(1));
				mSelectorInfo.get("structure").setHouseAllId(parseHouseProperty((String) msg.obj).get(0));
				showAlertDialog(mStructureTextView, "structure", parseHouseProperty((String) msg.obj).get(1));
			} else if (msg.what == 104) {
				dismissLoadingView();
				mSelectorInfo.get("district").setHouseAllContent(parseHouseDistrict((String) msg.obj).get(1));
				mSelectorInfo.get("district").setHouseAllId(parseHouseDistrict((String) msg.obj).get(0));
				showAlertDialog(mDistrictTextView, "district", parseHouseDistrict((String) msg.obj).get(1));
			} else if (msg.what == 105) {
				dismissLoadingView();
				mSelectorInfo.get("street").setHouseAllContent(parseHouseStreet((String) msg.obj).get(1));
				mSelectorInfo.get("street").setHouseAllId(parseHouseStreet((String) msg.obj).get(0));
				showAlertDialog(mStreetTextView, "street", parseHouseStreet((String) msg.obj).get(1));
			} else if (msg.what == 106) {
				dismissLoadingView();
				mSelectorInfo.get("road").setHouseAllContent(parseHouseRoad((String) msg.obj).get(1));
				mSelectorInfo.get("road").setHouseAllId(parseHouseRoad((String) msg.obj).get(0));
				showAlertDialog(mRoadTextView, "road", parseHouseRoad((String) msg.obj).get(1));
			} else if (msg.what == 107) {
				dismissLoadingView();
				mSelectorInfo.get("fenju").setHouseAllContent(parseHouseFenju((String) msg.obj).get(1));
				mSelectorInfo.get("fenju").setHouseAllId(parseHouseFenju((String) msg.obj).get(0));
				showAlertDialog(mFenjuTextView, "fenju", parseHouseFenju((String) msg.obj).get(1));
			} else if (msg.what == 108) {
				dismissLoadingView();
				mSelectorInfo.get("police").setHouseAllContent(parseHouseFenju((String) msg.obj).get(1));
				mSelectorInfo.get("police").setHouseAllId(parseHouseFenju((String) msg.obj).get(0));
				showAlertDialog(mPoliceTextView, "police", parseHouseFenju((String) msg.obj).get(1));
			} else if (msg.what == 109) {
				dismissLoadingView();
				mSelectorInfo.get("renttype").setHouseAllContent(parseHouseProperty((String) msg.obj).get(1));
				mSelectorInfo.get("renttype").setHouseAllId(parseHouseProperty((String) msg.obj).get(0));
				showAlertDialog(mLeaseTypeTextView, "renttype", parseHouseProperty((String) msg.obj).get(1));
			} else if (msg.what == 110) {
				dismissLoadingView();
				mSelectorInfo.get("ownertype").setHouseAllContent(parseHouseProperty((String) msg.obj).get(1));
				mSelectorInfo.get("ownertype").setHouseAllId(parseHouseProperty((String) msg.obj).get(0));
				showAlertDialog(mOwnerTypeTextView, "ownertype", parseHouseProperty((String) msg.obj).get(1));

			} else if (msg.what == 111) {
				dismissLoadingView();
				mSelectorInfo.get("buildingstructure").setHouseAllContent(parseHouseProperty((String) msg.obj).get(1));
				mSelectorInfo.get("buildingstructure").setHouseAllId(parseHouseProperty((String) msg.obj).get(0));
				showAlertDialog(mBuildingStructureTextView, "buildingstructure",
						parseHouseProperty((String) msg.obj).get(1));
			} else if (msg.what == 888) {
				String value = (String) msg.obj;

				if (value != null && value.equals("true")) {
					Log.e("mingguo", "add rent info success ");
					Toast.makeText(getApplicationContext(), "添加租房信息成功", Toast.LENGTH_SHORT).show();
					Intent photoIntent = new Intent(AddHouseInfoActivity.this, SelectPhotoActivity.class);
					photoIntent.putExtra("rentNo", mRentNo);
					startActivity(photoIntent);
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "添加租房信息失败", Toast.LENGTH_SHORT).show();
				}
			} else if (msg.what == 112) {
				String value = (String) msg.obj;
				if (value != null && value.equals("true")) {
					mValidHouseId = true;
				} else {
					mValidHouseId = false;
					Toast.makeText(getApplicationContext(), "房产证编号输入有误", Toast.LENGTH_SHORT).show();
				}
			}
		}

	};

	private void showLoadingView() {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
			ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
			if (imageView != null) {
				RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
				imageView.startAnimation(rotate);
			}
		}
	}

	private void dismissLoadingView() {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE) {
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
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mPropertyAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mTypeAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 101;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mDirectionAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 102;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mStuctureAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 103;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mDistrictAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 104;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mStreetAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 105;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mRoadAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 106;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mFenjuAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 107;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mPoliceAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 108;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mRentTypeAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 109;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mOwnerTypeAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 110;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mBuildingStuctureAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 111;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mAddHouseAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 888;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mValidHouseIDAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 112;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}

	}

	public static List<String[]> parseHouseProperty(String value) {
		String[] property = null;
		String[] nameId;
		List<String[]> list = new ArrayList<>();
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				property = new String[array.length()];
				nameId = new String[array.length()];
				for (int item = 0; item < array.length(); item++) {
					JSONObject itemJsonObject = array.optJSONObject(item);
					property[item] = itemJsonObject.optString("RSOName");
					nameId[item] = itemJsonObject.optString("RSONo");
				}
				list.add(nameId);
				list.add(property);
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	public static String[] parseHouseType(String value) {
		String[] property = null;
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				property = new String[array.length()];
				for (int item = 0; item < array.length(); item++) {

					JSONObject itemJsonObject = array.optJSONObject(item);
					property[item] = itemJsonObject.optString("RSOName");
				}
			}

			return property;
		} catch (Exception e) {
			e.printStackTrace();
			return property;
		}
	}

	public static List<String[]> parseHouseDistrict(String value) {
		String[] name;
		String[] nameId;
		List<String[]> list = new ArrayList<>();
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				name = new String[array.length()];
				nameId = new String[array.length()];
				for (int item = 0; item < array.length(); item++) {

					JSONObject itemJsonObject = array.optJSONObject(item);
					name[item] = itemJsonObject.optString("LDName");
					nameId[item] = itemJsonObject.optString("LDID");
				}
				list.add(nameId);
				list.add(name);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	public static List<String[]> parseHouseStreet(String value) {
		String[] name;
		String[] nameId;
		List<String[]> list = new ArrayList<>();
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				name = new String[array.length()];
				nameId = new String[array.length()];
				for (int item = 0; item < array.length(); item++) {

					JSONObject itemJsonObject = array.optJSONObject(item);
					name[item] = itemJsonObject.optString("LSName");
					nameId[item] = itemJsonObject.optString("LSID");
				}
				list.add(nameId);
				list.add(name);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	public static List<String[]> parseHouseRoad(String value) {
		String[] name;
		String[] nameId;
		List<String[]> list = new ArrayList<>();
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				name = new String[array.length()];
				nameId = new String[array.length()];
				for (int item = 0; item < array.length(); item++) {

					JSONObject itemJsonObject = array.optJSONObject(item);
					name[item] = itemJsonObject.optString("LRName");
					nameId[item] = itemJsonObject.optString("LRID");
				}
				list.add(nameId);
				list.add(name);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	public static List<String[]> parseHouseFenju(String value) {
		String[] name;
		String[] nameId;
		List<String[]> list = new ArrayList<>();
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				name = new String[array.length()];
				nameId = new String[array.length()];
				for (int item = 0; item < array.length(); item++) {

					JSONObject itemJsonObject = array.optJSONObject(item);
					name[item] = itemJsonObject.optString("PSName");
					nameId[item] = itemJsonObject.optString("PSID");
				}
				list.add(nameId);
				list.add(name);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	public static String[] parseHousePolice(String value) {
		String[] property = null;
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
				property = new String[array.length()];
				for (int item = 0; item < array.length(); item++) {

					JSONObject itemJsonObject = array.optJSONObject(item);
					property[item] = itemJsonObject.optString("PSName");
				}
			}
			return property;
		} catch (Exception e) {
			e.printStackTrace();
			return property;
		}
	}

}
