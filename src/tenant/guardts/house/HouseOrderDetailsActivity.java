package tenant.guardts.house;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.model.AddIDCardResult;
import tenant.guardts.house.model.ConfirmCheckout;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.model.ServiceCharge;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.view.PriceEditText;

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
	private String mExpireOrderAction = "http://tempuri.org/ExpiredOrder";
	private String mAddIDCardToDevice="http://tempuri.org/AddIDCardToDevice";
	private long mEnterTimeStamp;
	private TextView mOrderPriceTextView;
	private String mOrderModifyPrice;
	private Button mModifyPriceButton;

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
		initData();
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

	private void initData() {
		if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_SUBMITT)
				|| mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_PAY)) {
			mEnterTimeStamp = mOrderDetail.getCurrentdDate();
			updateTimeHandler.sendEmptyMessage(800);
		}
	}

	private void initView() {
		// 取消和拒绝，隐藏
		priceLinearLayout = (FrameLayout) findViewById(R.id.order_detail_ll);
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
		address.setSelected(true);
		TextView contactName = (TextView) findViewById(R.id.id_order_detail_contacn_name);
		contactPhone = (TextView) findViewById(R.id.id_order_detail_contacn_phone);
		TextView startTime = (TextView) findViewById(R.id.id_order_detail_start_name);
		TextView endTime = (TextView) findViewById(R.id.id_order_detail_end_name);
		status = (TextView) findViewById(R.id.id_order_detail_status);
		TextView ownerName = (TextView) findViewById(R.id.id_order_detail_owner_name);
		ownerPhone = (TextView) findViewById(R.id.id_order_detail_owner_phone);
		mOrderPriceTextView = (TextView) findViewById(R.id.id_order_detail_pay_monkey);
		address.setText(mOrderDetail.getHouseAddress());
		contactName.setText(mOrderDetail.getHouseContactName());
		contactPhone.setText(mOrderDetail.getHouseContactPhone());
		startTime.setText(mOrderDetail.getHouseStartTime());
		endTime.setText(mOrderDetail.getHouseEndTime());
		status.setText(mOrderDetail.getHouseStatus());
		ownerName.setText(mOrderDetail.getHouseOwnerName());
		ownerPhone.setText(mOrderDetail.getHouseOwnerPhone());
		mOrderPriceTextView.setText("¥ " + mOrderDetail.getHousePrice());
		mOrderModifyPrice = mOrderDetail.getHousePrice();
		mModifyPriceButton = (Button)findViewById(R.id.id_order_detail_modify_button);
		mModifyPriceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showModifyPriceDialog();
				
			}
		});
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
		TextView inputIDCard = (TextView) findViewById(R.id.input_idcard);
		inputIDCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initScanPupopWindow();
			}
		});
	}
	
	private void showModifyPriceDialog(){
		AlertDialog.Builder builder = new Builder(HouseOrderDetailsActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("修改订单价格"); // 设置对话框标题
		builder.setMessage("输入订单价格(单位:元)"); // 设置对话框标题前的图标
//		final EditText edit = new EditText(getApplicationContext());
		View editView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.order_input_price_editview, null);
		final PriceEditText edit = (PriceEditText)editView.findViewById(R.id.id_input_price);
//		edit.setHint("请输入与房客商定订单价格(单位元)");
//		edit.setTextColor(Color.parseColor("#337ffd"));
//		edit.setHintTextColor(Color.parseColor("#777777"));
//		edit.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(editView);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (edit.getText() != null && edit.getText().length() > 0){
					mOrderModifyPrice = edit.getText().toString();
					mOrderPriceTextView.setText("¥ " + edit.getText().toString());
				}
				//Toast.makeText(getApplicationContext(), "你输入的是: " + edit.getText().toString(), Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Toast.makeText(getApplicationContext(), "你点了取消", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setCancelable(true); // 设置按钮是否可以按返回键取消,false则不可以取消
		AlertDialog dialog = builder.create(); // 创建对话框
		dialog.setCanceledOnTouchOutside(true); // 设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
		dialog.show();
	}

	
	protected void initScanPupopWindow() {
		setBackgroundAlpha(0.2f);

		View scanView = View.inflate(this, R.layout.scan_popupwindow, null);
		popupWindow = new PopupWindow(scanView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		Button scan=(Button)scanView. findViewById(R.id.btn_scan);
		Button  cancel=(Button)scanView. findViewById(R.id.btn_cancel);
		
		
		popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				setBackgroundAlpha(1f);

			}
		});
		scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HouseOrderDetailsActivity.this, CaptureActivity.class);
				intent.putExtra("flag", "0");//是否显示扫描页面下边图标，0，不显示
				
				startActivityForResult(intent,CommonUtil.mScanCodeRequestCode);
				popupWindow.dismiss();
				
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

	private void expireHouseRequest(String id) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=ExpiredOrder";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mExpireOrderAction));
		rpc.addProperty("rraId", id);
		mPresent.readyPresentServiceParams(this, url, mExpireOrderAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	/****
	 * 刷新倒计时控件
	 */
	public String updateTimeTextView(long times_remain, String orderId) {
		if (times_remain <= 0) {
			expireHouseRequest(orderId);
			return "00:00";
		}
		// 秒钟
		long time_second = (times_remain / 1000) % 60;
		String str_second;
		if (time_second < 10) {
			str_second = "0" + time_second;
		} else {
			str_second = "" + time_second;
		}

		long time_temp = ((times_remain / 1000) - time_second) / 60;
		// 分钟
		long time_minute = time_temp % 60;
		String str_minute;
		if (time_minute < 10) {
			str_minute = "0" + time_minute;
		} else {
			str_minute = "" + time_minute;
		}

		time_temp = (time_temp - time_minute) / 60;
		// 小时
		long time_hour = time_temp;
		String str_hour;
		if (time_hour < 10) {
			str_hour = "0" + time_hour;
		} else {
			str_hour = "" + time_hour;
		}
		return (str_minute + ":" + str_second + "");

	}

	private Handler updateTimeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			mEnterTimeStamp = mEnterTimeStamp + 1000L;
			updateShowTimeDown(updateTimeTextView(mOrderDetail.getOrderExpiredDate() - mEnterTimeStamp,
					mOrderDetail.getHouseOrderId()));
			if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_SUBMITT)
					|| mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_PAY)) {
				updateTimeHandler.sendEmptyMessageDelayed(800, 1000);
			}

		}
	};

	private void updateShowTimeDown(String timeContent) {
		if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_SUBMITT)) {
			if (mDetailType.equals("owner")) {
				button1.setTextColor(Color.WHITE);
				button1.setText("确认 " + timeContent);
				
			}
		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_PAY)) {
			if (mDetailType.equals("renter")) {
				button1.setTextColor(Color.WHITE);
				button1.setText("支付 " + timeContent);
			}
		}
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
		mModifyPriceButton.setVisibility(View.GONE);
		if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_SUBMITT)) {
			status.setText("待确认");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("确认订单");
			button1.setVisibility(View.GONE);
			
			//button2.setBackgroundResource(R.drawable.order_detail_btn_pressed);
			if (mDetailType != null) {
				if (mDetailType.equals("owner")) {
					button1.setVisibility(View.VISIBLE);
					mModifyPriceButton.setVisibility(View.VISIBLE);
					//button1.setBackgroundResource(R.drawable.order_detail_btn_pressed);
					//button1.setTextColor(Color.parseColor("#ffffff"));
					button1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							showConfirmOrderDialog(mOrderModifyPrice, mOrderDetail.getHouseContactName(), mOrderDetail.getHouseOrderId());
						}
					});
					button2.setText("拒绝订单");
					//button2.setBackgroundResource(R.drawable.item_shape_no_solid_corner);
					button2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							showRejectOrderDialog(mOrderDetail.getHouseOrderId());
						}
					});
				} else if (mDetailType.equals("renter")) {
					button2.setText("取消订单");
					//button2.setBackgroundResource(R.drawable.order_detail_btn_pressed);
					//button2.setTextColor(Color.parseColor("#ffffff"));
					button2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							showCancelOrderDialog(0, mOrderDetail.getHouseOrderId());
						}
					});
				}
			}

		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_PAY)) {
			status.setText("待支付");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("立即付款");
			//button1.setTextColor(Color.parseColor("#ffffff"));
			//button1.setBackgroundResource(R.drawable.order_detail_btn_pressed);
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
							LogUtil.e("", mOrderDetail.getRenterIdcard() + "kkk");
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
		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_HAS_PAYED)) {
			status.setText("已支付");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.GONE);
			button2.setText("申请退房");
			button2.setVisibility(View.VISIBLE);
			priceLinearLayout.setVisibility(View.VISIBLE);
			tvServiceFee.setVisibility(View.VISIBLE);
			btnContact.setTextColor(Color.parseColor("#ffffff"));
			btnContact.setBackgroundResource(R.drawable.order_detail_btn_pressed);
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
					startActivityForResult(intent, 888);

				}
			});
		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_EVALUATION)) {
			status.setText("待评价");
			status.setTextColor(Color.parseColor("#8be487"));
			button1.setText("查看详情");
			button1.setVisibility(View.GONE);
			button2.setText("查看详情");
			button2.setVisibility(View.GONE);
			// button3.setText("立即评价");
			// button3.setTextColor(Color.parseColor("#ffffff"));
			// button3.setBackgroundResource(R.drawable.order_detail_btn_pressed);
		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_CANCELED)) {
			status.setText("已取消");// ///////////////////////////////////////////////////////////////////////////
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.GONE);
			button2.setText("查看详情");
			button2.setVisibility(View.GONE);
			priceLinearLayout.setVisibility(View.GONE);
			tvServiceFee.setVisibility(View.GONE);
			// button3.setText("查看详情");
			// button3.setTextColor(Color.parseColor("#ffffff"));
			// button3.setBackgroundResource(R.drawable.order_detail_btn_pressed);
		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_REJECTED)) {
			status.setText("已拒绝");// ///////////////////////////////////////////////////////////////////////////////////
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
			priceLinearLayout.setVisibility(View.GONE);
			tvServiceFee.setVisibility(View.GONE);
			// button3.setText("查看详情");
			// button3.setTextColor(Color.parseColor("#ffffff"));
			// button3.setBackgroundResource(R.drawable.order_detail_btn_pressed);
		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_CHECKOUT)) {
			status.setText("待退房");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setVisibility(View.GONE);
			String checkOutPerson = mOrderDetail.getCheckOutPerson();// 退房申请人
			if (!TextUtils.isEmpty(checkOutPerson)) {
				if (checkOutPerson.equals(CommonUtil.mUserLoginName)) {
					button2.setVisibility(View.GONE);
				} else {
					button2.setText("确认退房");
					button2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							initAlertDialog();// 确认退房
						}
					});
				}
			}

		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_CHECKOUTED)) {
			status.setText("已退房");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.VISIBLE);
			button2.setText("立即评价");
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(HouseOrderDetailsActivity.this, ApplyForCheckoutActivity.class);
//					if (mDetailType.equals("owner")) {
//						intent.putExtra("type_phone", mOrderDetail.getHouseContactPhone());
//
//					} else if (mDetailType.equals("renter")) {
//						intent.putExtra("type_phone", mOrderDetail.getHouseOwnerPhone());
//
//					}
//					intent.putExtra("detail_type", mDetailType);
//					intent.putExtra("RRAID", mOrderDetail.getHouseOrderId());
//					startActivityForResult(intent, 888);
					Intent intent = new Intent(HouseOrderDetailsActivity.this, EvaluationActivity.class);
					intent.putExtra("order_detail", mOrderDetail);
					intent.putExtra("detail_type", mDetailType);
					startActivity(intent);
				}
			});
		} else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_EXPIRED)) {
			status.setText("已过期");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
		}else if (mOrderDetail.getHouseStatus().equals(CommonUtil.ORDER_STATUS_COMPLETE)){
			status.setText("已完成");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.VISIBLE);
			button2.setText("查看评价");
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HouseOrderDetailsActivity.this,EvaluationDetailActivity.class);
					if (mDetailType.equals("renter")) {
						intent.putExtra("rraid", mOrderDetail.getHouseId());
						intent.putExtra("detail_type", "renter");
					}else if(mDetailType.equals("owner")){
						intent.putExtra("rraid", mOrderDetail.getRenterIdcard());
						intent.putExtra("detail_type", "owner");
					}
					startActivity(intent);
				}
				
			});
		}
		if (mDetailType.equals("renter")) {
			tvServiceFee.setVisibility(View.GONE);
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
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				cancelRentAttributeInfo(houseId);

			}

		});
		builder.setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				LogUtil.w("alertdialog", " �뱣�����ݣ�");

			}
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	private void showConfirmOrderDialog(final String orderPrice, final String renter,  final String houseId) {  
		
		  AlertDialog.Builder builder =new AlertDialog.Builder(HouseOrderDetailsActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("确认订单");
		  builder.setMessage("订单价格:￥"+orderPrice+"\n房客:"+renter);
		  builder.setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {
		         @Override  
		  
		         public void onClick(DialogInterface dialog, int which) {
					//rejectRentAttributeInfo(houseId);
					confirmRentAttributeInfo(houseId, orderPrice);
		         }  
			
		});
		builder.setNegativeButton(getString(R.string.button_cancel),new DialogInterface.OnClickListener() {
			  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {
	  
	             LogUtil.w("alertdialog"," �뱣�����ݣ�");  
	  
	         }
		});
		builder.setCancelable(false);
		builder.show();
	}

	private void confirmRentAttributeInfo(String id, String fee) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=ConfirmRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mConfirmRentAttribute));
		rpc.addProperty("id", id);
		rpc.addProperty("fee", fee);
		mPresent.readyPresentServiceParams(HouseOrderDetailsActivity.this, url, mConfirmRentAttribute, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void showRejectOrderDialog(final String houseId) {

		AlertDialog.Builder builder = new AlertDialog.Builder(HouseOrderDetailsActivity.this,
				AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("拒绝订单");
		builder.setMessage("您确认要拒绝该订单吗？");
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

				LogUtil.w("alertdialog", " �뱣�����ݣ�");

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		updateTimeHandler.removeCallbacksAndMessages(null);
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
			} else if (msg.what == 103) {
				try {
					JSONObject object = new JSONObject((String) msg.obj);
					String ret = object.optString("ret");
					if (ret != null) {
						if (ret.equals("0")) {
							updateTimeHandler.removeMessages(800);
							mOrderDetail.setHouseStatus(CommonUtil.ORDER_STATUS_EXPIRED);
							updateStatus(status, button1, button2);
						} else {
							GlobalUtil.shortToast(HouseOrderDetailsActivity.this, "订单更新失败！", getResources()
									.getDrawable(R.drawable.ic_dialog_no));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			}else if(msg.what==300){
				String value = (String) msg.obj;
				Gson gson = new Gson();
				AddIDCardResult result = gson.fromJson(value, AddIDCardResult.class);
				if(result.ret!=null){
					if(result.ret.equals("0")){
						Toast.makeText(HouseOrderDetailsActivity.this, "请在滴声后按图示完成身份证绑定", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(HouseOrderDetailsActivity.this, result.msg, Toast.LENGTH_LONG).show();
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
	private FrameLayout priceLinearLayout;
	private TextView status;
	private Button button1;
	private Button button2;
	private String deviceID;

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
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
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
			} else if (action.equals(mExpireOrderAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 103;
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
			}else if(action.equals(mAddIDCardToDevice)){
				Message msg = mHandler.obtainMessage();
				msg.what = 300;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}

	/**
	 * 录入身份证
	 */
	protected void intputIDCard() {
		setBackgroundAlpha(0.3f);
		
		View view = View.inflate(this, R.layout.popupwindow_input_identity_info, null);
		popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
		Button btnConfirm = (Button) view.findViewById(R.id.btn_input_idcard_confirm);
		Button btnCancel = (Button) view.findViewById(R.id.btn_input_idcard_cancel);
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String renterIdcard = mOrderDetail.getRenterIdcard();
				if(!TextUtils.isEmpty(renterIdcard)&&!TextUtils.isEmpty(deviceID)){
					
					addIDCardToDevice(renterIdcard, deviceID);
				}

			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();

			}
		});
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				setBackgroundAlpha(1f);

			}
		});
	}
	
	/**录入身份证
	 * @param idcard 身份证
	 * @param deviceId 锁设备id
	 */
	private void addIDCardToDevice(String idcard,String deviceId) {
		//
		String url = CommonUtil.mUserHost + "Services.asmx?op=AddIDCardToDevice";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mAddIDCardToDevice));
		rpc.addProperty("idcard", idcard);
		rpc.addProperty("deviceId", deviceId);
		mPresent.readyPresentServiceParams(this, url, mAddIDCardToDevice, rpc);
		mPresent.startPresentServiceTask(true);

	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 888) {
				boolean flag = data.getBooleanExtra("flag", false);
				if (flag) {
					status.setText("待退房");
					status.setTextColor(Color.parseColor("#de6262"));
					button1.setVisibility(View.GONE);
					button2.setVisibility(View.GONE);
				}
			}
			if(requestCode==CommonUtil.mScanCodeRequestCode){
				String result = data.getStringExtra("result");
				Log.e("mingguo", result);
				String[] split = result.split("=");
				deviceID = split[3];
				if(!TextUtils.isEmpty(deviceID)){
					intputIDCard();
				}
				
				
			}
		}
	}

}
