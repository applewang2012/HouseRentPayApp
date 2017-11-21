package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.AboutUsActivity;
import tenant.guardts.house.AddHouseInfoActivity;
import tenant.guardts.house.LoginUserActivity;
import tenant.guardts.house.MyHouseActivity;
import tenant.guardts.house.PersonalInfoActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.WalletActivity;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;

public class MyFragment extends BaseFragment {

	private Context mContext;
	private View mRootView;
	private TextView mUserNickname;
	// private TextView mUserId;
	
	private TextView mUserAddress;
	private HoursePresenter mPresent;
	private FrameLayout mPublishHouse;
	private String mXingeTokenAction = "http://tempuri.org/UpdateDeviceID";
	private String mGetUserInfoAction = "http://tempuri.org/GetUserInfo";
	private String mCommonServiceAction = "http://tempuri.org/GetAreas";
	private FrameLayout mAboutUs;
	private FrameLayout mLogout;
	// private String mUsername;
	private FrameLayout mChangeArea;
	private String wallet;
	private String realName;
	private String phone;
	private String idCard;
	private FrameLayout mWalletFrameLayout;
	private String mUserName;
	private FrameLayout mHistory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mUserName = bundle.getString("user_name");
		mContext = getActivity().getApplicationContext();
		mPresent = new HoursePresenter(mContext, MyFragment.this);
//		getActivity().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
//		TextView bartext  = (TextView) getActivity().getWindow().findViewById(R.id.id_titlebar);
//		bartext.setText("周边生活");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.w("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.home_my_fragment, container, false);
		initView();

		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	private void initView() {
		mRegistAndLogin = (TextView) mRootView.findViewById(R.id.id_user_log_in);// 登录注册

		mImageAvator = (ImageView) mRootView.findViewById(R.id.img_avator);// 头像
		mWallet = (TextView) mRootView.findViewById(R.id.id_user_wallet);
		mUserNickname = (TextView) mRootView.findViewById(R.id.id_user_nickname);
		// mUserId = (TextView)mRootView.findViewById(R.id.id_user_id);
		mUserAddress = (TextView) mRootView.findViewById(R.id.id_user_address);
		// 
		mHistory = (FrameLayout) mRootView.findViewById(R.id.house_history);// 我的房屋
		mPublishHouse = (FrameLayout) mRootView.findViewById(R.id.id_user_publish_house);// 发布房屋
		mWalletFrameLayout = (FrameLayout) mRootView.findViewById(R.id.id_user_house_wallet);// 我的钱包
		mAboutUs = (FrameLayout) mRootView.findViewById(R.id.id_userinfo_about_us);// 修改密码
		mLogout = (FrameLayout) mRootView.findViewById(R.id.id_userinfo_logout);// 退出登录
		mChangeArea = (FrameLayout) mRootView.findViewById(R.id.id_userinfo_change_area);// 切换城市
		if (mUserName.equals("") || mUserName == null) {
		} else {
			mRegistAndLogin.setVisibility(View.GONE);
			mUserNickname.setVisibility(View.VISIBLE);
			mUserAddress.setVisibility(View.VISIBLE);
		}

		mRegistAndLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, LoginUserActivity.class));
			}
		});

		// 点击头像跳转
		mImageAvator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(mContext, LoginUserActivity.class));
				} else {
					if (CommonUtil.mRegisterRealName != null && CommonUtil.mUserLoginName != null
							&& CommonUtil.mRegisterIdcard != null) {
						Intent intent = new Intent(mContext, PersonalInfoActivity.class);
						intent.putExtra("RealName", CommonUtil.mRegisterRealName);
						intent.putExtra("Phone", CommonUtil.mUserLoginName);
						intent.putExtra("IDCard", CommonUtil.mRegisterIdcard);
						startActivity(intent);
					}
				}
			}
		});
		mHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(mContext, LoginUserActivity.class));
				} else {
						Intent intent = new Intent(mContext, MyHouseActivity.class);
						intent.putExtra("IDCard", CommonUtil.mRegisterIdcard);
						startActivity(intent);
				}
			}
		});
		mAboutUs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, AboutUsActivity.class));
			}
		});

		mLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(mContext, LoginUserActivity.class));
				} else {
					logoutUserDialog(0);
				}

			}
		});
		mPublishHouse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(mContext, LoginUserActivity.class));
				} else {
 
					Intent intent = new Intent(mContext, AddHouseInfoActivity.class);
					intent.putExtra("user_name", CommonUtil.mUserLoginName);
					startActivity(intent);
				}

			}
		});
		mChangeArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				commonServiceInterface();
				//changeUserAreaDialog();
			}
		});
		mWalletFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(mContext, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					startActivity(new Intent(mContext, LoginUserActivity.class));
				} else {
					Intent intent = new Intent(mContext, WalletActivity.class);
					startActivity(intent);
				}

			}
		});
		// 
	}

	private void initData() {
		LogUtil.w("mingguo", "my fragment  onresume  login name  " + CommonUtil.mUserLoginName+"  wallet  "+CommonUtil.mUserWallet);
		if (CommonUtil.mUserLoginName != null && !CommonUtil.mUserLoginName.equals("")){
			mRegistAndLogin.setVisibility(View.GONE);
			mUserNickname.setText(CommonUtil.mRegisterRealName);
			mUserAddress.setText(CommonUtil.mUserLoginName);
			mUserNickname.setVisibility(View.VISIBLE);
			mUserAddress.setVisibility(View.VISIBLE);
			mWallet.setVisibility(View.VISIBLE);
			if (CommonUtil.mUserWallet == null || CommonUtil.mUserWallet.equals("") || CommonUtil.mUserWallet.equalsIgnoreCase("null")) {
				mWallet.setText("¥ 0.0");
			} else {
				mWallet.setText("¥ "+CommonUtil.mUserWallet);
			}
			getUserInfo();
		}else{
			mRegistAndLogin.setVisibility(View.VISIBLE);
			mUserNickname.setVisibility(View.GONE);
			mUserAddress.setVisibility(View.GONE);
			mWallet.setText("¥ 0.0");
			SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
			mUserName = sharedata.getString("user_name", "");
			getUserInfo();
		}
	}

	private void commonServiceInterface(){
		mPresent = new HoursePresenter(mContext, this);
	    	String url = "http://www.guardts.com/commonservice/commonservices.asmx?op=GetAreas";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCommonServiceAction));
			rpc.addProperty("status", "1");
			mPresent.readyPresentServiceParams(getActivity(), url, mCommonServiceAction, rpc);
			mPresent.startPresentServiceTask(true);
	}
	
	private void getUserInfo() {
		if (mUserName == null || mUserName.equals("")){
			return;
		}
		String url = CommonUtil.mUserHost + "services.asmx?op=GetUserInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetUserInfoAction));
		rpc.addProperty("username", mUserName);
		rpc.addProperty("deviceId", CommonUtil.XINGE_TOKEN);
		mPresent.readyPresentServiceParams(getActivity(), url, mGetUserInfoAction, rpc);
		mPresent.startPresentServiceTask(true);
	}


	private String cardNo;
	private String bankName;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				HashMap<String, String> infoModel = parseUserInfo((String) msg.obj);
				if (infoModel != null) {
					phone = infoModel.get("Phone");
					mUserAddress.setText(phone);// 显示手机号
					realName = infoModel.get("RealName");
					mUserNickname.setText(realName);// 显示姓名
					idCard = infoModel.get("IDCard");
					wallet = infoModel.get("Wallet");
					cardNo = infoModel.get("CardNO");
					bankName = infoModel.get("BankName");
					
					if (CommonUtil.mUserWallet == null || CommonUtil.mUserWallet.equals("") || CommonUtil.mUserWallet.equalsIgnoreCase("null")) {
						mWallet.setText("¥ 0.0");
					} else {
						mWallet.setText("¥ "+CommonUtil.mUserWallet);
					}
					
					if (realName != null && !realName.equals("")) {
						mRegistAndLogin.setVisibility(View.GONE);
						mUserNickname.setVisibility(View.VISIBLE);
						mUserAddress.setVisibility(View.VISIBLE);
					} else {
						mRegistAndLogin.setVisibility(View.VISIBLE);
						mUserNickname.setVisibility(View.GONE);
						mUserAddress.setVisibility(View.GONE);
					}
				}
			} else if (msg.what == 101) {
				resetUserInfo();
				Intent intent = new Intent(mContext, LoginUserActivity.class);
				startActivity(intent);
				MyFragment.this.getActivity().finish();
			}else if (msg.what == 110){
				showSelectAlertDialog("请选择所在区域", parseCommonService((String)msg.obj));
			}

		}
	};
	
	private  List<String[]> parseCommonService(String value) {
		String [] areaName = null;
		String [] areaHost;
		List<String[]> list = new ArrayList<>();
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				areaName = new String[array.length()];
				areaHost = new String[array.length()];
				for (int item = 0; item < array.length(); item++){
					JSONObject itemJsonObject = array.optJSONObject(item);
					areaName[item] = itemJsonObject.optString("AreaName");
					areaHost[item] = itemJsonObject.optString("RentHost");
					
				}
				list.add(areaName);
				list.add(areaHost);
			}
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}
	
	private ImageView mImageAvator;
	private TextView mWallet;
	private TextView mRegistAndLogin;

	private  HashMap<String, String> parseUserInfo(String value) {
		HashMap<String, String> userInfo = null;
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				LogUtil.w("house", "parse house info " + array.length());
				// for (int item = 0; item < array.length(); item++){

				JSONObject itemJsonObject = array.optJSONObject(0);
				userInfo = new HashMap<>();
				userInfo.put("NickName", itemJsonObject.optString("NickName"));// 昵称
				userInfo.put("LoginName", itemJsonObject.optString("LoginName"));
				userInfo.put("Address", itemJsonObject.optString("Address"));
				userInfo.put("IDCard", itemJsonObject.optString("IDCard"));// 身份证号
				userInfo.put("Phone", itemJsonObject.optString("Phone"));// 手机
				userInfo.put("Wallet", itemJsonObject.optString("Wallet"));
				userInfo.put("RealName", itemJsonObject.optString("RealName"));
				userInfo.put("BankName", itemJsonObject.optString("BankName"));
				userInfo.put("CardNO", itemJsonObject.optString("CardNO"));
				CommonUtil.mUserLoginName = itemJsonObject.optString("LoginName");
				CommonUtil.mRegisterRealName = itemJsonObject.optString("RealName");
				CommonUtil.mRegisterIdcard = itemJsonObject.optString("IDCard");
				CommonUtil.mUserWallet = itemJsonObject.optString("Wallet");
				CommonUtil.mBankName= itemJsonObject.optString("BankName");
				CommonUtil.mCardNo = itemJsonObject.optString("CardNO");
			}
			return userInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return userInfo;
		}
	}

	private void logoutUserDialog(final int position) {
		new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle(getString(R.string.user_logout))

				.setMessage(getString(R.string.user_logout_remind))// ������ʾ������

				.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
					@Override

					public void onClick(DialogInterface dialog, int which) {
						uploadXingeToken();
						
					}

				}).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť

					@Override

					public void onClick(DialogInterface dialog, int which) {// ��Ӧ�¼�
						LogUtil.w("alertdialog", " dialog interface ");
					}

				}).show();
	}

	private void uploadXingeToken() {
		
		String url = CommonUtil.mUserHost + "services.asmx?op=UpdateDeviceID";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mXingeTokenAction));
		rpc.addProperty("userId", CommonUtil.mUserLoginName);
		rpc.addProperty("deviceId", "");
		mPresent.readyPresentServiceParams(getActivity(), url, mXingeTokenAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void changeUserAreaDialog() {
		new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle(getString(R.string.user_logout))

				.setMessage(getString(R.string.user_change_area_title))// ������ʾ������

				.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
					@Override

					public void onClick(DialogInterface dialog, int which) {
						uploadXingeToken();
					}

				}).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť

					@Override

					public void onClick(DialogInterface dialog, int which) {// ��Ӧ�¼�
						LogUtil.w("alertdialog", " �뱣�����ݣ�");
					}

				}).show();
	}
	
	private void showSelectAlertDialog(final String title, final List<String[]> data) { 
		if (data != null && data.size()> 0 && data.get(0).length == 1){
			SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
			SharedPreferences.Editor editor = sharedata.edit();
			editor.putString("area", data.get(0)[0]);
			editor.putString("user_host", data.get(1)[0]);
			editor.commit();
			CommonUtil.mUserArea = data.get(0)[0];
			CommonUtil.mUserHost = data.get(1)[0];
			return;
		}
		
		  AlertDialog.Builder builder =new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle(title);
		  //ic_dialog_alert);
		  builder.setItems(data.get(0), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sharedata.edit();
			    editor.putString("area", data.get(0)[which]);
			    editor.putString("user_host", data.get(1)[which]);
			    editor.commit();
			    CommonUtil.mUserArea = data.get(0)[which];
			    CommonUtil.mUserHost = data.get(1)[which];
			    
			    if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")){
				    Message msgMessage = mHandler.obtainMessage();
					msgMessage.what = 101;
					msgMessage.sendToTarget();
				}else{
					 uploadXingeToken();
				}
			}
		});
		builder.setCancelable(true);
		builder.show();
		
	}
	
	private void resetUserInfo(){
		mWallet.setText("¥0.0");
		SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = sharedata.edit();
		editor.putString("user_name", "");
		editor.putString("user_password", "");
		editor.putString("user_realname", "");
		editor.putString("user_idcard", "");
		//editor.putString("user_host", "");
		editor.commit();
		mUserAddress.setVisibility(View.GONE);
		mUserNickname.setVisibility(View.GONE);
		CommonUtil.mUserLoginName = "";
		CommonUtil.mRegisterRealName = "";
		CommonUtil.mRegisterIdcard = "";
		CommonUtil.mUserWallet = "";
		CommonUtil.mBankName = "";
		CommonUtil.mCardNo = "";
		CommonUtil.verify_code_test = false;
		CommonUtil.version_pay_test = false;
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		LogUtil.e("mingguo", "on status success-----  "+getClass().getName()+action+ "  success " + templateInfo);
		super.onStatusSuccess(action, templateInfo);
		if (templateInfo != null){
			if (action.equals(mGetUserInfoAction)){
				Message msgMessage = mHandler.obtainMessage();
				msgMessage.what = 100;
				msgMessage.obj = templateInfo;
				msgMessage.sendToTarget();
			}else if (action.equals(mXingeTokenAction)){
				Message msgMessage = mHandler.obtainMessage();
				msgMessage.what = 101;
				msgMessage.obj = templateInfo;
				msgMessage.sendToTarget();
			}else	if (action.equals(mCommonServiceAction)){
					Message msg = mHandler.obtainMessage();
					msg.what = 110;
					msg.obj = templateInfo;
					mHandler.sendMessage(msg);
				}
		}

	}



}
