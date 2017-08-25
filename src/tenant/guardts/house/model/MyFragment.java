package tenant.guardts.house.model;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.AddHouseInfoActivity;
import tenant.guardts.house.LoginUserActivity;
import tenant.guardts.house.ModifyPasswordActivity;
import tenant.guardts.house.PersonalInfoActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.WalletActivity;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class MyFragment extends Fragment implements DataStatusInterface {

	private Context mContext;
	private View mRootView;
	private TextView mUserNickname;
	// private TextView mUserId;
	private View mLoadingView;
	private TextView mUserAddress;
	private HoursePresenter mPresent;
	private FrameLayout mPublishHouse;

	private FrameLayout mPassword;
	private FrameLayout mLogout;
	// private String mUsername;
	private FrameLayout mChangeArea;
	private String wallet;
	private String realName;
	private String phone;
	private String idCard;
	private FrameLayout mWalletFrameLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mPresent = new HoursePresenter(mContext, MyFragment.this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.home_my_fragment, container, false);
		initView();
		initData();
		return mRootView;
	}

	private void initView() {
		mImageAvator = (ImageView) mRootView.findViewById(R.id.img_avator);// 头像
		mWallet = (TextView) mRootView.findViewById(R.id.id_user_wallet);
		mUserNickname = (TextView) mRootView.findViewById(R.id.id_user_nickname);
		// mUserId = (TextView)mRootView.findViewById(R.id.id_user_id);
		mUserAddress = (TextView) mRootView.findViewById(R.id.id_user_address);
		mLoadingView = (View) mRootView.findViewById(R.id.id_data_loading);
		// showLoadingView();
		mPublishHouse = (FrameLayout) mRootView.findViewById(R.id.id_user_publish_house);
		mWalletFrameLayout = (FrameLayout) mRootView.findViewById(R.id.id_user_house_wallet);
		mPassword = (FrameLayout) mRootView.findViewById(R.id.id_userinfo_password_modify);
		mLogout = (FrameLayout) mRootView.findViewById(R.id.id_userinfo_logout);
		mChangeArea = (FrameLayout) mRootView.findViewById(R.id.id_userinfo_change_area);
		// 点击头像跳转
		mImageAvator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (realName != null && phone != null && idCard != null) {
					Intent intent = new Intent(mContext, PersonalInfoActivity.class);
					intent.putExtra("RealName", realName);
					intent.putExtra("Phone", phone);
					intent.putExtra("IDCard", idCard);
					startActivity(intent);
				}

			}
		});

		mPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, ModifyPasswordActivity.class));
			}
		});

		mLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUserDialog(0);

			}
		});
		mPublishHouse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AddHouseInfoActivity.class);
				intent.putExtra("user_name", CommonUtil.mUserLoginName);
				startActivity(intent);
			}
		});
		mChangeArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeUserAreaDialog();
			}
		});
		mWalletFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext, WalletActivity.class);
				if (wallet == null || wallet.equals("null")) {
					intent.putExtra("balance", "0.0");

				} else {
					intent.putExtra("balance", wallet);
				}

				startActivity(intent);

			}
		});
		// showLoadingView();
	}

	private void initData() {
		getUserInfo();
	}

	private void getUserInfo() {
		String url = CommonUtil.mUserHost + "services.asmx?op=GetUserInfo";
		String soapaction = "http://tempuri.org/GetUserInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(soapaction));
		rpc.addProperty("username", CommonUtil.mUserLoginName);
		mPresent.readyPresentServiceParams(mContext, url, soapaction, rpc);
		mPresent.startPresentServiceTask();

	}

	private void showLoadingView() {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
			ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
			if (imageView != null) {
				RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(),
						R.anim.anim_rotate);
				imageView.startAnimation(rotate);
			}
		}
	}

	private void dismissLoadingView() {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			HashMap<String, String> infoModel = parseUserInfo((String) msg.obj);
			dismissLoadingView();
			if (infoModel != null) {
				phone = infoModel.get("Phone");
				mUserAddress.setText(phone);
				realName = infoModel.get("RealName");
				mUserNickname.setText(realName);
				idCard = infoModel.get("IDCard");
				wallet = infoModel.get("Wallet");
				Toast.makeText(mContext, wallet, Toast.LENGTH_LONG).show();
				if (wallet == null || wallet.equals("null")) {
					mWallet.setText("¥0.0");

				} else {
					mWallet.setText("¥" + wallet);
				}

			}
		}
	};
	private ImageView mImageAvator;
	private TextView mWallet;

	public static HashMap<String, String> parseUserInfo(String value) {
		HashMap<String, String> userInfo = null;
		try {
			JSONArray array = new JSONArray(value);
			if (array != null) {
				Log.i("house", "parse house info " + array.length());
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
				// CommonUtil.mRegisterName =
				// itemJsonObject.optString("RealName");
				// CommonUtil.mRegisterIdcard =
				// itemJsonObject.optString("IDCard");
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
						SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
						SharedPreferences.Editor editor = sharedata.edit();
						editor.putString("user_name", "");
						editor.putString("user_password", "");
						editor.commit();
						Intent intent = new Intent(mContext, LoginUserActivity.class);
						startActivity(intent);
						MyFragment.this.getActivity().finish();
					}

				}).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť

					@Override

					public void onClick(DialogInterface dialog, int which) {// ��Ӧ�¼�
						Log.i("alertdialog", " dialog interface ");
					}

				}).show();
	}

	private void changeUserAreaDialog() {
		new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle(getString(R.string.user_logout))

				.setMessage(getString(R.string.user_change_area_title))// ������ʾ������

				.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
					@Override

					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
						SharedPreferences.Editor editor = sharedata.edit();
						editor.putString("user_name", "");
						editor.putString("user_password", "");
						editor.putString("area", "");
						editor.putString("host", "");
						editor.commit();
						Intent intent = new Intent(mContext, LoginUserActivity.class);
						startActivity(intent);
						MyFragment.this.getActivity().finish();
					}

				}).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť

					@Override

					public void onClick(DialogInterface dialog, int which) {// ��Ӧ�¼�
						Log.i("alertdialog", " �뱣�����ݣ�");
					}

				}).show();
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		Log.e("mingguo", "success " + templateInfo);
		Message msgMessage = mHandler.obtainMessage();
		msgMessage.obj = templateInfo;
		msgMessage.sendToTarget();
	}

	@Override
	public void onStatusStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub

	}

}
