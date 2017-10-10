package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.gzt.faceid5sdk.DetectionAuthentic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.bannerview.CircleFlowIndicator;
import tenant.guardts.house.bannerview.ImagePagerAdapter;
import tenant.guardts.house.bannerview.ViewFlow;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.model.HouseImageInfo;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class ApplyForCheckoutActivity extends BaseActivity {
	private Button mButtonCall;// 联系房主
	private Button mButtonApply;// 申请租房
	private TextView mTitleBar;

	private HoursePresenter mPresenter;
	private String mAddRentAction = "http://tempuri.org/AddRentRecord";
	private String mIdentifyUrl = "https://nid.sdtt.com.cn/AppRegSvr/thirdsysauthsvr/houseorder";
	private String mAppIDString = "0000004";
	private String mRandNum = null;
	// ret 1 desc SDT-HOUSE-3435333134303230313730333233313531373331343839
	private String mQueryStatusUrl = null;
	// private Map<String, String> mSelectedMap = new HashMap<>();
	// private Map<String, String> mOriginText = new HashMap<>();
	// private Map<String, String[]> mAllList = new HashMap<>();
	private String mHouseNo;
	private String mUsername;
	private String mOriginTypeText, mTypeIndex = null;
	private String mOwnerName;
	private String mOwnerIdcard;
	private String mHouseDetailAction = "http://tempuri.org/GetHouseDetailInfo";
	private String mHouseImageListAction = "http://tempuri.org/GetRentImageList";
	private String mDeleteHouseInfo = "http://tempuri.org/DeleteHouseInfo";
	private String mRentNo = "";
	private String mfilePath;
	private HandlerThread myHandlerThread;
	private Handler mSubHandler;
	private ArrayList<String> imageUrlList = new ArrayList<String>();
	private String mImageUrlPrefix = CommonUtil.mUserHost + "";
	private TextView mLocationPolice;
	private PopupWindow popupWindow;
	private PopupWindow pop;
	private LinearLayout ll_popup;
	private HouseInfoModel mHouseInfo;
	private String mDetailType;
	private String mPhoneNumber;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_apply_checkout_layout);
		ActivityController.addActivity(this);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("申请退房");
		mDetailType = getIntent().getStringExtra("detail_type");
		mPhoneNumber = getIntent().getStringExtra("type_phone");
//		flag = getIntent().getStringExtra("flag");
		initView();
//		// mRentNo = "888888888";
//		Log.e("mingguo", "rent no  " + mRentNo);
//		getHouseDetailInfoByHouseId(mRentNo);
		initPopupWindow();
		initEvent();
	}
	
	

	/**
	 * 点击事件
	 */
	private void initEvent() {
		mButtonCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				backgroundAlpha(0.3f);
				ll_popup.startAnimation(
						AnimationUtils.loadAnimation(ApplyForCheckoutActivity.this, R.anim.activity_translate_in));
				pop.showAtLocation(mButtonCall, Gravity.BOTTOM, 0, 0);

				TextView phone = (TextView) ll_popup.findViewById(R.id.id_button_contact_owner_show_phone);
				phone.setText(mPhoneNumber);
			}
		});

		mButtonApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				
			}
		});

	}

	private void showPublicAttributeDialog() {
		new AlertDialog.Builder(ApplyForCheckoutActivity.this, AlertDialog.THEME_HOLO_LIGHT)
				.setTitle(getString(R.string.rent_house_title))

				.setMessage(getString(R.string.rent_house_content))

				.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
					@Override

					public void onClick(DialogInterface dialog, int which) {
						if (mHouseInfo == null){
							return;
						}
						if (mHouseInfo != null && mHouseInfo.getHouseId() != null && !mHouseInfo.getHouseId().equals("")
								&& mHouseInfo.getHouseOwnerName() != null && !mHouseInfo.getHouseOwnerName().equals("")
								&& mHouseInfo.getHouseOwnerName() != null
								&& !mHouseInfo.getHouseOwnerName().equals("")) {
							Intent intent = new Intent(ApplyForCheckoutActivity.this, AddRentAttributeActivity.class);
							intent.putExtra("house_id", mHouseInfo.getHouseId());
							intent.putExtra("user_name", CommonUtil.mUserLoginName);
							intent.putExtra("owner_name", mHouseInfo.getHouseOwnerName());
							intent.putExtra("owner_id", mHouseInfo.getHouseOwnerIdcard());
							startActivity(intent);
							finish();
						} else {
							Toast.makeText(getApplicationContext(), "获取房屋详情异常，请重试！", Toast.LENGTH_SHORT).show();
						}

					}

				}).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť

					@Override

					public void onClick(DialogInterface dialog, int which) {// ��Ӧ�¼�

						Log.i("alertdialog", " �뱣�����ݣ�");

					}

				}).show();
	}
	
	private void initView() {

		mPresenter = new HoursePresenter(getApplicationContext(), this);

		LinearLayout mReasonContent1 = (LinearLayout) findViewById(R.id.checkout_house_reason_content1);
		LinearLayout mReasonContent2 = (LinearLayout) findViewById(R.id.checkout_house_reason_content2);
		LinearLayout mReasonContent3 = (LinearLayout) findViewById(R.id.checkout_house_reason_content3);
		LinearLayout mReasonContent4 = (LinearLayout) findViewById(R.id.checkout_house_reason_content4);
		
		final ImageView reasonIcon1 = (ImageView)findViewById(R.id.checkout_house_reason_icon1);
		final ImageView reasonIcon2 = (ImageView)findViewById(R.id.checkout_house_reason_icon2);
		final ImageView reasonIcon3 = (ImageView)findViewById(R.id.checkout_house_reason_icon3);
		final ImageView reasonIcon4 = (ImageView)findViewById(R.id.checkout_house_reason_icon4);
		
		final TextView reasonText1 = (TextView) findViewById(R.id.checkout_house_reason_text1);
		final TextView reasonText2 = (TextView) findViewById(R.id.checkout_house_reason_text2);
		final TextView reasonText3 = (TextView) findViewById(R.id.checkout_house_reason_text3);
		final TextView reasonText4 = (TextView) findViewById(R.id.checkout_house_reason_text4);
		mReasonContent1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reasonIcon1.setBackgroundResource(R.drawable.checkout_house_selected);
				reasonIcon2.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon3.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon4.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonText1.setTextColor(Color.parseColor("#337ffd"));
				reasonText2.setTextColor(Color.parseColor("#333333"));
				reasonText3.setTextColor(Color.parseColor("#333333"));
				reasonText4.setTextColor(Color.parseColor("#333333"));
			}
		});
		
		mReasonContent2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reasonIcon1.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon2.setBackgroundResource(R.drawable.checkout_house_selected);
				reasonIcon3.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon4.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonText1.setTextColor(Color.parseColor("#333333"));
				reasonText2.setTextColor(Color.parseColor("#337ffd"));
				reasonText3.setTextColor(Color.parseColor("#333333"));
				reasonText4.setTextColor(Color.parseColor("#333333"));
			}
		});

		mReasonContent3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reasonIcon1.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon2.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon3.setBackgroundResource(R.drawable.checkout_house_selected);
				reasonIcon4.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonText1.setTextColor(Color.parseColor("#333333"));
				reasonText2.setTextColor(Color.parseColor("#333333"));
				reasonText3.setTextColor(Color.parseColor("#337ffd"));
				reasonText4.setTextColor(Color.parseColor("#333333"));
			}
		});

		mReasonContent4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reasonIcon1.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon2.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon3.setBackgroundResource(R.drawable.checkout_house_normal);
				reasonIcon4.setBackgroundResource(R.drawable.checkout_house_selected);
				reasonText1.setTextColor(Color.parseColor("#333333"));
				reasonText2.setTextColor(Color.parseColor("#333333"));
				reasonText3.setTextColor(Color.parseColor("#333333"));
				reasonText4.setTextColor(Color.parseColor("#337ffd"));
			}
		});

		mButtonCall = (Button) findViewById(R.id.checkout_house_contact_phone);
		mButtonApply = (Button) findViewById(R.id.checkout_house_checkout_ok);
		
		if (mDetailType.equals("owner")) {
			mButtonCall.setText("联系房客");
			reasonText1.setText("房客行程有变");
			reasonText2.setText("房屋改造");
			reasonText3.setText("态度恶劣");
			reasonText4.setText("其它原因");
		}else{
			mButtonCall.setText("联系房主");
			reasonText1.setText("行程有变");
			reasonText2.setText("环境一般");
			reasonText3.setText("服务一般");
			reasonText4.setText("其它原因");
		}
	}

	/**
	 * 初始化PopupWindow
	 */
	protected void initPopupWindow() {

		// View view = View.inflate(this, R.layout.popupwindow_contact_owner,
		// null);
		// popupWindow = new PopupWindow(view,
		// ViewGroup.LayoutParams.MATCH_PARENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT);
		// popupWindow.setFocusable(true);
		// popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,
		// View.MeasureSpec.UNSPECIFIED);
		// int popHeight = popupWindow.getContentView().getMeasuredHeight();
		// popupWindow.showAsDropDown(mButtonCall, 0, -popHeight);

		pop = new PopupWindow(ApplyForCheckoutActivity.this);
		View view = getLayoutInflater().inflate(R.layout.popupwindow_contact_owner, null);
		ll_popup = (LinearLayout) view.findViewById(R.id.id_contact_owner_content);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
		TextView contactTitle = (TextView) view.findViewById(R.id.popup_contact_title);
		if (mDetailType.equals("owner")) {
			contactTitle.setText("联系房客");
		}else{
			contactTitle.setText("联系房主");
		}
		Button dialPhoneButton = (Button) view.findViewById(R.id.id_button_contact_owner_dial);
		Button cancelContactButton = (Button) view.findViewById(R.id.id_button_contact_owner_cancel);
		dialPhoneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
				try {
					Intent intent1 = new Intent(Intent.ACTION_CALL,
							Uri.parse("tel:" + mPhoneNumber));
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent1);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		cancelContactButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		pop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				backgroundAlpha(1);

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	/**
	 * 删除房屋
	 * 
	 * @param rentNO
	 */
	private void setDeleteHouseInfo(String rentNO) {
		String url = CommonUtil.mUserHost+"Services.asmx?op=DeleteHouseInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mDeleteHouseInfo));
		rpc.addProperty("rentNO", rentNO);
		mPresenter.readyPresentServiceParams(this, url, mDeleteHouseInfo, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void initDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("是否删除该房屋？").setNegativeButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除房屋
				if (!TextUtils.isEmpty(mRentNo)) {
//					setDeleteHouseInfo(mRentNo);//////////////////////////////////////////
				}
			}
		}).setPositiveButton("否", null).show();

	}

	private void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getWindow().setAttributes(lp);
	}

	private void showAlertDialog(final TextView text, final String[] items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ApplyForCheckoutActivity.this,
				AlertDialog.THEME_HOLO_LIGHT);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTypeIndex = which + "";
				text.setText(mOriginTypeText + "   " + items[which]);
			}
		});
		builder.show();
	}

	private TextView mRentName;

	private TextView mRentReadMe;
	private TextView mRentOrderId;
	private DetectionAuthentic authentic;
	private TextView mRentArea;
	private TextView mRentType;
	private TextView mRentDirection;
	private TextView mRentAddress;

	// private GridView mHouseInfoGridview;
	private List<HouseImageInfo> mDataList = new ArrayList<>();
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mFlowIndicator;
	private TextView mZhulinType;
	private TextView mRoomNum, mHousePrice;
	private Button mDel;

	


	private void getHouseDetailInfoByHouseId(String rentNo) {

		String url = CommonUtil.mUserHost + "Services.asmx?op=GetHouseDetailInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mHouseDetailAction));
		rpc.addProperty("rentNo", rentNo);
		mPresenter.readyPresentServiceParams(ApplyForCheckoutActivity.this, url, mHouseDetailAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void getHouseDetailImageListByHouseId(String rentNo) {

		String url = CommonUtil.mUserHost + "services.asmx?op=GetRentImageList";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mHouseImageListAction));
		rpc.addProperty("rentNo", rentNo);
		mPresenter.readyPresentServiceParams(this, url, mHouseImageListAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 100) {

				jsonHouseInfoToView((String) msg.obj);

				//getHouseDetailImageListByHouseId(mRentNo);
			} else if (msg.what == 101) {

			} else if (msg.what == 200) {

				jsonHouseImageListData((String) msg.obj);
				Log.i("mingguo", "handle message   image url list size  " + imageUrlList.size());
			} else if (msg.what == 300) {
				String value = (String) msg.obj;
				if (value != null) {

					if (value.equals("true")) {
						Toast.makeText(ApplyForCheckoutActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(ApplyForCheckoutActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}

	};
	private String flag;

	private void jsonHouseInfoToView(String value) {
		if (value != null) {
			JSONArray array;
			try {
				array = new JSONArray(value);
				JSONObject object = array.optJSONObject(0);
				if (object != null) {
					mHouseInfo = new HouseInfoModel();
					mRentName.setText(object.getString("ROwner"));
					mRentArea.setText(object.getString("RRentArea") + " 平米");
					mRentType.setText(object.getString("RRoomTypeDesc"));
					mRentDirection.setText(object.getString("RDirectionDesc"));
					mRentAddress.setText(object.getString("RAddress"));
					mZhulinType.setText(object.getString("RRentTypeDesc"));
					mRoomNum.setText(object.getString("RDoor"));
					mLocationPolice.setText(object.getString("RPSName"));
					mHousePrice.setText(object.getString("RLocationDescription") + " 元");

					mHouseInfo.setHouseId(object.optString("RentNO"));
					mHouseInfo.setHouseOwnerName(object.optString("ROwner"));
					mHouseInfo.setHouseOwnerIdcard(object.optString("RIDCard"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void jsonHouseImageListData(String value) {
		if (value != null) {
			try {
				JSONObject object = new JSONObject(value);
				if (object != null) {
					String imageCount = object.optString("count");
					int count = Integer.parseInt(imageCount);
					Log.w("mingguo", "countt  " + count);
					if (count > 0) {
						if (count == 1) {
							String imageUrl1 = object.optString("Image0");
							imageUrlList.add(mImageUrlPrefix + mImageUrlPrefix + imageUrl1);
						} else if (count == 2) {
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							imageUrlList.add(mImageUrlPrefix + imageUrl1);
							imageUrlList.add(mImageUrlPrefix + imageUrl2);
						} else if (count == 3) {
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							String imageUrl3 = object.optString("Image2");
							imageUrlList.add(mImageUrlPrefix + imageUrl1);
							imageUrlList.add(mImageUrlPrefix + imageUrl2);
							imageUrlList.add(mImageUrlPrefix + imageUrl3);
						} else if (count == 4) {
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							String imageUrl3 = object.optString("Image2");
							String imageUrl4 = object.optString("Image3");
							imageUrlList.add(mImageUrlPrefix + imageUrl1);
							imageUrlList.add(mImageUrlPrefix + imageUrl2);
							imageUrlList.add(mImageUrlPrefix + imageUrl3);
							imageUrlList.add(mImageUrlPrefix + imageUrl4);
						} else if (count == 5) {
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							String imageUrl3 = object.optString("Image2");
							String imageUrl4 = object.optString("Image3");
							String imageUrl5 = object.optString("Image4");
							imageUrlList.add(mImageUrlPrefix + imageUrl1);
							imageUrlList.add(mImageUrlPrefix + imageUrl2);
							imageUrlList.add(mImageUrlPrefix + imageUrl3);
							imageUrlList.add(mImageUrlPrefix + imageUrl4);
							imageUrlList.add(mImageUrlPrefix + imageUrl5);
						} else if (count == 6) {
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							String imageUrl3 = object.optString("Image2");
							String imageUrl4 = object.optString("Image3");
							String imageUrl5 = object.optString("Image4");
							String imageUrl6 = object.optString("Image5");
							imageUrlList.add(mImageUrlPrefix + imageUrl1);
							imageUrlList.add(mImageUrlPrefix + imageUrl2);
							imageUrlList.add(mImageUrlPrefix + imageUrl3);
							imageUrlList.add(mImageUrlPrefix + imageUrl4);
							imageUrlList.add(mImageUrlPrefix + imageUrl5);
							imageUrlList.add(mImageUrlPrefix + imageUrl6);
						}
					}

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
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mHouseDetailAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mHouseImageListAction)) {
				Message message = mHandler.obtainMessage();
				message.what = 200;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			} else if (action.equals(mDeleteHouseInfo)) {
				Message message = mHandler.obtainMessage();
				message.what = 300;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}

}
