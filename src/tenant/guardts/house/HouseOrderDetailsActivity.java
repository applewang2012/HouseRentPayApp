package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import tenant.guardts.house.model.ConfirmCheckout;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.model.ServiceCharge;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class HouseOrderDetailsActivity extends BaseActivity {
	private HouseInfoModel mOrderDetail;
	private String mDetailType;
	private HoursePresenter mPresent;
	private String mCancelAttrbuteAction = "http://tempuri.org/CancelRentAttribute";
	private String mConfirmCheckOutAction = "http://tempuri.org/ConfirmCheckOut";// 确认退房
	private String mRejectRentAction = "http://tempuri.org/RejectRentAttribute";
	private String mConfirmRentAttribute = "http://tempuri.org/ConfirmRentAttribute";
	private PopupWindow popupWindow;
	private TextView ownerPhone;// 房主电话
	private TextView contactPhone;// 房客电话
	private String mGetPayRateDesc = "http://tempuri.org/GetPayRateDesc";// 扣费提醒

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_order_details_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("订单详情");
		mOrderDetail = (HouseInfoModel) getIntent().getSerializableExtra("order_detail");
		mDetailType = getIntent().getStringExtra("detail_type");
		mPresent = new HoursePresenter(HouseOrderDetailsActivity.this, this);
		getPayRateDesc(mOrderDetail.getHousePrice());
		initView();

	}

	/**
	 * 获取服务费信息
	 * 
	 * @param price
	 */
	private void getPayRateDesc(String price) {
		//
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetPayRateDesc";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetPayRateDesc));
		rpc.addProperty("fee", price);
		mPresent.readyPresentServiceParams(this, url, mGetPayRateDesc, rpc);
		mPresent.startPresentServiceTask(true);

	}

	private void initView() {
		// 取消和拒绝，隐藏
		priceLinearLayout = (LinearLayout) findViewById(R.id.order_detail_ll);
		// 取消和拒绝，隐藏
		tvServiceFee = (TextView) findViewById(R.id.order_service_fee);
		parent = View.inflate(this, R.layout.activity_order_details_info, null);
		view = View.inflate(this, R.layout.popupwindow_contact_owner, null);
		title = (TextView) view.findViewById(R.id.popup_contact_title);
		phone = (TextView) view.findViewById(R.id.id_button_contact_owner_show_phone);
		contact = (Button) view.findViewById(R.id.id_button_contact_owner_dial);
		cancel = (Button) view.findViewById(R.id.id_button_contact_owner_cancel);
		LinearLayout passwordContent = (LinearLayout) findViewById(R.id.id_door_password_content);
		TextView password = (TextView) findViewById(R.id.door_password);

		TextView address = (TextView) findViewById(R.id.id_order_detail_address);
		TextView contactName = (TextView) findViewById(R.id.id_order_detail_contacn_name);
		contactPhone = (TextView) findViewById(R.id.id_order_detail_contacn_phone);
		TextView startTime = (TextView) findViewById(R.id.id_order_detail_start_name);
		TextView endTime = (TextView) findViewById(R.id.id_order_detail_end_name);
		status = (TextView) findViewById(R.id.id_order_detail_status);
		TextView ownerName = (TextView) findViewById(R.id.id_order_detail_owner_name);
		ownerPhone = (TextView) findViewById(R.id.id_order_detail_owner_phone);
		TextView money = (TextView) findViewById(R.id.id_order_detail_pay_monkey);
		address.setText(mOrderDetail.getHouseAddress());
		contactName.setText(mOrderDetail.getHouseContactName());
		contactPhone.setText(mOrderDetail.getHouseContactPhone());
		startTime.setText(mOrderDetail.getHouseStartTime());
		endTime.setText(mOrderDetail.getHouseEndTime());
		status.setText(mOrderDetail.getHouseStatus());
		ownerName.setText(mOrderDetail.getHouseOwnerName());
		ownerPhone.setText(mOrderDetail.getHouseOwnerPhone());
		money.setText("¥ " + mOrderDetail.getHousePrice());
		button1 = (Button) findViewById(R.id.id_order_detail_button1);
		button2 = (Button) findViewById(R.id.id_order_detail_button2);
		btnContact = (Button) findViewById(R.id.id_order_detail_contact);
		if (mDetailType != null) {
			if (mDetailType.equals("owner")) {
				passwordContent.setVisibility(View.GONE);
			} else {
				passwordContent.setVisibility(View.VISIBLE);
				password.setText(mOrderDetail.getDoorPassword());
			}
		}
		if (status != null && button1 != null && button2 != null) {
			updateStatus(status, button1, button2);
		}

		if (mDetailType.equals("owner")) {
			btnContact.setText("联系房客");

		} else if (mDetailType.equals("renter")) {
			btnContact.setText("联系房主");

		}
		btnContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initPopupWindow();
			}
		});

	}

	/**
	 * 更新订单状态
	 * 
	 * @param status
	 *            状态
	 * @param button1
	 * @param button2
	 */
	public void updateStatus(TextView status, Button button1, Button button2) {
		if (mOrderDetail.getHouseStatus().equals("0")) {
			status.setText("待确认");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("确认订单");
			button1.setVisibility(View.GONE);

			button2.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			if (mDetailType != null) {
				if (mDetailType.equals("owner")) {
					button1.setVisibility(View.VISIBLE);
					button1.setTextColor(Color.parseColor("#337ffd"));
					button1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							confirmRentAttributeInfo(mOrderDetail.getHouseOrderId());
						}
					});
					button2.setText("拒绝订单");
					button2.setBackgroundResource(R.drawable.item_shape_no_solid_corner);
					button2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							showRejectOrderDialog(mOrderDetail.getHouseOrderId());
						}
					});
				} else if (mDetailType.equals("renter")) {
					button2.setText("取消订单");
					button2.setTextColor(Color.parseColor("#337ffd"));
					button2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							showCancelOrderDialog(0, mOrderDetail.getHouseOrderId());
						}
					});
				}
			}

		} else if (mOrderDetail.getHouseStatus().equals("1")) {
			status.setText("待支付");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("立即付款");
			button1.setTextColor(Color.parseColor("#337ffd"));
			button1.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button2.setText("取消订单");

			if (mDetailType != null) {
				if (mDetailType.equals("owner")) {
					button1.setVisibility(View.GONE);
					button2.setVisibility(View.GONE);
				} else if (mDetailType.equals("renter")) {
					button1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							CommonUtil.mPayHouseOrderId = mOrderDetail.getHouseOrderId();
							Intent payIntent = new Intent(HouseOrderDetailsActivity.this,
									tenant.guardts.house.wxapi.HousePayActivity.class);
							payIntent.putExtra("pay_price", mOrderDetail.getHousePrice());
							payIntent.putExtra("owner_idcard", mOrderDetail.getHouseOwnerIdcard());
							payIntent.putExtra("renter_idcard", mOrderDetail.getRenterIdcard());
							Log.e("", mOrderDetail.getRenterIdcard() + "kkk");
							payIntent.putExtra("orderID", mOrderDetail.getHouseOrderId());
							payIntent.putExtra("rentNO", mOrderDetail.getHouseId());
							payIntent.putExtra("orderCreatedDate", mOrderDetail.getOrderCreatedDate());
							startActivity(payIntent);
							finish();
						}
					});
					button2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							showCancelOrderDialog(0, mOrderDetail.getHouseOrderId());

						}
					});
				}
			}
		} else if (mOrderDetail.getHouseStatus().equals("2")) {
			status.setText("已支付");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.GONE);
			button2.setText("申请退房");
			button2.setVisibility(View.VISIBLE);
			priceLinearLayout.setVisibility(View.VISIBLE);
			tvServiceFee.setVisibility(View.VISIBLE);
			btnContact.setTextColor(Color.parseColor("#337ffd"));
			btnContact.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HouseOrderDetailsActivity.this, ApplyForCheckoutActivity.class);
					if (mDetailType.equals("owner")) {
						intent.putExtra("type_phone", mOrderDetail.getHouseContactPhone());

					} else if (mDetailType.equals("renter")) {
						intent.putExtra("type_phone", mOrderDetail.getHouseOwnerPhone());

					}
					intent.putExtra("detail_type", mDetailType);
					intent.putExtra("RRAID", mOrderDetail.getHouseOrderId());
					startActivity(intent);

				}
			});
		} else if (mOrderDetail.getHouseStatus().equals("3")) {
			status.setText("待评价");
			status.setTextColor(Color.parseColor("#8be487"));
			button1.setText("查看详情");
			button1.setVisibility(View.GONE);
			button2.setText("查看详情");
			button2.setVisibility(View.GONE);
			// button3.setText("立即评价");
			// button3.setTextColor(Color.parseColor("#337ffd"));
			// button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
		} else if (mOrderDetail.getHouseStatus().equals("8")) {
			status.setText("已取消");// ///////////////////////////////////////////////////////////////////////////
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.GONE);
			button2.setText("查看详情");
			button2.setVisibility(View.GONE);
			priceLinearLayout.setVisibility(View.GONE);
			tvServiceFee.setVisibility(View.GONE);
			// button3.setText("查看详情");
			// button3.setTextColor(Color.parseColor("#337ffd"));
			// button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
		} else if (mOrderDetail.getHouseStatus().equals("9")) {
			status.setText("已拒绝");// ///////////////////////////////////////////////////////////////////////////////////
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
			priceLinearLayout.setVisibility(View.GONE);
			tvServiceFee.setVisibility(View.GONE);
			// button3.setText("查看详情");
			// button3.setTextColor(Color.parseColor("#337ffd"));
			// button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
		} else if (mOrderDetail.getHouseStatus().equals("6")) {
			status.setText("待退房");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setVisibility(View.GONE);
			button2.setText("确认退房");
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					initAlertDialog();// 确认退房
				}
			});

		} else if (mOrderDetail.getHouseStatus().equals("5")) {
			status.setText("已退房");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
		} else if (mOrderDetail.getHouseStatus().equals("7")) {
			status.setText("已过期");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化popupwindow
	 * 
	 */
	protected void initPopupWindow() {
		setBackgroundAlpha(0.2f);

		popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		if (title != null && phone != null) {
			if (mDetailType.equals("owner")) {
				title.setText("联系房客");
				phone.setText(contactPhone.getText() + "");

			} else if (mDetailType.equals("renter")) {
				title.setText("联系房主");
				phone.setText(ownerPhone.getText() + "");

			}
		}
		popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				setBackgroundAlpha(1f);

			}
		});
		contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CharSequence strPhone = phone.getText();

				startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strPhone)));

			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				setBackgroundAlpha(1f);

			}
		});

	}

	/**
	 * 设置背景透明度
	 * 
	 * @param alpha
	 */
	public void setBackgroundAlpha(float alpha) {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = alpha;
		getWindow().setAttributes(params);
	}

	private void showCancelOrderDialog(final int id, final String houseId) {

		AlertDialog.Builder builder = new AlertDialog.Builder(HouseOrderDetailsActivity.this,
				AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("取消订单");
		builder.setMessage("您确认要取消该订单吗？");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				cancelRentAttributeInfo(houseId);

			}

		});
		builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Log.w("alertdialog", " �뱣�����ݣ�");

			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	private void confirmRentAttributeInfo(String id) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=ConfirmRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mConfirmRentAttribute));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(HouseOrderDetailsActivity.this, url, mConfirmRentAttribute, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void showRejectOrderDialog(final String houseId) {

		AlertDialog.Builder builder = new AlertDialog.Builder(HouseOrderDetailsActivity.this,
				AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("拒绝订单");
		builder.setMessage("您确认要拒绝该订单吗？");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// mCurrentPosition = id;

				rejectRentAttributeInfo(houseId);
			}

		});
		builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Log.w("alertdialog", " �뱣�����ݣ�");

			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	protected void initAlertDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle("是否确认退房？");
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();

			}
		});
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String mRRAID = mOrderDetail.getHouseOrderId();
				if (!TextUtils.isEmpty(mRRAID)) {

					confirmCheckOut(mRRAID);
				}

			}
		});

		builder.show();
	}

	/**
	 * 确认退房
	 * 
	 * @param rraID
	 *            RRAID
	 */
	private void confirmCheckOut(String rraID) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=ConfirmCheckOut";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mConfirmCheckOutAction));
		rpc.addProperty("rraId", rraID);
		mPresent.readyPresentServiceParams(HouseOrderDetailsActivity.this, url, mConfirmCheckOutAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void rejectRentAttributeInfo(String id) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=RejectRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRejectRentAction));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(HouseOrderDetailsActivity.this, url, mRejectRentAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void cancelRentAttributeInfo(String id) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=CancelRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCancelAttrbuteAction));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(HouseOrderDetailsActivity.this, url, mCancelAttrbuteAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 100) {
				finish();
			} else if (msg.what == 101) {
				finish();
			} else if (msg.what == 102) {
				finish();
			} else if (msg.what == 818) {
				String value = (String) msg.obj;
				Gson gson = new Gson();
				ServiceCharge serviceCharge = gson.fromJson(value, ServiceCharge.class);
				if (serviceCharge.fee.startsWith("00")) {
					tvServiceFee.setText("已包含服务费" + serviceCharge.fee.substring(1) + "元");

				} else {
					tvServiceFee.setText("已包含服务费" + serviceCharge.fee + "元");
					;
				}

			} else if (msg.what == 200) {
				String value = (String) msg.obj;
				Gson gson = new Gson();
				ConfirmCheckout checkout = gson.fromJson(value, ConfirmCheckout.class);
				if (checkout.ret != null) {
					if (checkout.ret.equals("0")) {
						Toast.makeText(HouseOrderDetailsActivity.this, "退房成功", Toast.LENGTH_SHORT).show();
						status.setText("已退房");
						status.setTextColor(Color.parseColor("#de6262"));
						button1.setVisibility(View.GONE);
						button2.setVisibility(View.GONE);
					}
				}
			}
		}
	};
	private TextView title;
	private TextView phone;
	private Button contact;
	private Button cancel;
	private View parent;
	private View view;
	private Button btnContact;
	private TextView tvServiceFee;
	private LinearLayout priceLinearLayout;
	private TextView status;
	private Button button1;
	private Button button2;

	@Override
	protected void onResume() {
		super.onResume();

		if (status != null && button1 != null && button2 != null) {
			updateStatus(status, button1, button2);
		}
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		super.onStatusSuccess(action, templateInfo);
		Log.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mRejectRentAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mCancelAttrbuteAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 101;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mConfirmRentAttribute)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 102;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mGetPayRateDesc)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 818;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mConfirmCheckOutAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 200;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}

}
