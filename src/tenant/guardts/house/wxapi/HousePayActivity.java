package tenant.guardts.house.wxapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.BaseActivity;
import tenant.guardts.house.PaymentStatusActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.model.WalletPayment;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.util.UtilTool;
import tenant.guardts.house.util.ViewUtil;
import tenant.guardts.house.wxpay.WeiXinPay;

public class HousePayActivity extends BaseActivity implements DataStatusInterface {

	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private IWXAPI api;
	
	private String realPrice = null;
	private LinearLayout mWallet;
	private LinearLayout mWechat;
	private CheckBox mCheckBoxWallet;
	private CheckBox mCheckBoxWechat;
	private boolean isPayByWechat = true;// 是否使用微信支付
	private String mPayUseWallet = "http://tempuri.org/PayUseWallet";// 钱包支付
	private String mOrderStatusAction = "http://tempuri.org/GetOrderStatus";// 订单状态
	private HoursePresenter mPresenter;
	private boolean successful;
	private String renterId;
	private String orderID;
	private String orderCreatedDate;
	private View loadingView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_house_payment);
		ActivityController.addActivity(this);
		initView();
		initEvent();
		price = getIntent().getStringExtra("pay_price");
		ownerId = getIntent().getStringExtra("owner_idcard");
		renterId = getIntent().getStringExtra("renter_idcard");
		orderID=getIntent().getStringExtra("orderID");
		rentNO = getIntent().getStringExtra("rentNO");
		orderCreatedDate=getIntent().getStringExtra("orderCreatedDate");//下单时间
		ownerId = getIntent().getStringExtra("owner_idcard");
		renterId = getIntent().getStringExtra("renter_idcard");
		orderID=getIntent().getStringExtra("orderID");

		TextView priceText = (TextView) findViewById(R.id.id_pay_price_show);

		if (price == null || price.equals("null")) {
			priceText.setText("0.0元");
		} else {
			priceText.setText(price + "元");
		}
		CommonUtil.ORDER_MONKEY = price;
		CommonUtil.OWNER_IDCARD = ownerId;
		try {
			Float priceFloat = Float.valueOf(price);
			realPrice = (int)(priceFloat * 100)+"";
			LogUtil.w("mingguo", "pay activity  price int   " + realPrice);
		} catch (Exception e) {
			Toast.makeText(HousePayActivity.this, "价钱有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		loadingView = (View)findViewById(R.id.id_data_loading);
		Button payButton = (Button) findViewById(R.id.id_button_pay_money_button);
		payButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkOrderStatus();
			}
		});
		if (realPrice != null && realPrice.equals("0")){
			payByWallet(renterId, ownerId, price);
		}
		
	}
	
	
	
	private void showWalletPayDialog() {
		new AlertDialog.Builder(HousePayActivity.this, AlertDialog.THEME_HOLO_LIGHT).setTitle("钱包支付")

				.setMessage("您确认要使用钱包余额支付吗？")// ������ʾ������

				.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
					@Override

					public void onClick(DialogInterface dialog, int which) {
						
						payByWallet(renterId, ownerId, price);
						
					}

				}).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

					@Override

					public void onClick(DialogInterface dialog, int which) {
						LogUtil.w("alertdialog", " dialog interface ");
					}

				}).show();
	}
	
	private void checkOrderStatus() {
		if (orderID == null){
			return;
		}
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetOrderStatus";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mOrderStatusAction));
		rpc.addProperty("rraId", orderID);
		mPresenter.readyPresentServiceParams(this, url, mOrderStatusAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void payByWallet(String renterID, String ownerID, String money) {
		String url = CommonUtil.mUserHost+"Services.asmx?op=PayUseWallet";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mPayUseWallet));
		rpc.addProperty("rennteeIDCard", renterID);
		rpc.addProperty("ownerIDCard", ownerID);
		rpc.addProperty("money", money);
		mPresenter.readyPresentServiceParams(this, url, mPayUseWallet, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void initView() {
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mWallet = (LinearLayout) findViewById(R.id.payment_linearlayout_wallet);
		mWechat = (LinearLayout) findViewById(R.id.payment_linearlayout_wechat);
		mCheckBoxWallet = (CheckBox) findViewById(R.id.checkbox_wallet);
		mCheckBoxWechat = (CheckBox) findViewById(R.id.checkbox_wechat);

	}

	private void initEvent() {
		mWallet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isPayByWechat = false;
				setCheckBoxStatus(mCheckBoxWallet, mCheckBoxWechat);

			}
		});
		mWechat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isPayByWechat = true;
				setCheckBoxStatus(mCheckBoxWechat, mCheckBoxWallet);
			}
		});

	}

	private void setCheckBoxStatus(CheckBox box1, CheckBox box2) {
		box1.setChecked(true);
		box2.setChecked(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		ViewUtil.dismissLoadingView();
	}

	private void startPay(final String price, final String orderNo, final String ip) {

		new AsyncTask<Void, Void, Void>() {
			private String weiXinReturn = null;

			@Override
			protected Void doInBackground(Void... params) {
				weiXinReturn = WeiXinPay.startPay(price, orderNo, ip);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				try {
					PayReq req = new PayReq();
					LogUtil.e("mingguo", "onPostExecute  " + weiXinReturn);
					Map<String, String> backMap = UtilTool.decodeXml(weiXinReturn);
					req.appId = CommonUtil.APP_ID;
					req.partnerId = CommonUtil.WX_PARTNER_ID;
					req.prepayId = backMap.get("prepay_id");
					req.nonceStr = UUID.randomUUID().toString().replace("-", "");
					req.timeStamp = System.currentTimeMillis() / 1000 + "";
					req.packageValue = "Sign=WXPay";
					if (req.prepayId == null)
						return;
					CommonUtil.ORDER_PREPAY_ID = req.prepayId;
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					// 调用统一下单接口必需传的参数,可以查看微信支付统一下单接口api查看每个参数的意思
					nvps.add(new BasicNameValuePair("appid", CommonUtil.APP_ID));
					nvps.add(new BasicNameValuePair("noncestr", req.nonceStr));
					nvps.add(new BasicNameValuePair("package", req.packageValue));
					nvps.add(new BasicNameValuePair("partnerid", CommonUtil.WX_PARTNER_ID));
					nvps.add(new BasicNameValuePair("prepayid", req.prepayId));
					nvps.add(new BasicNameValuePair("timestamp", req.timeStamp));

					StringBuffer sb = new StringBuffer();

					for (NameValuePair nvp : nvps) {
						sb.append(nvp.getName() + "=" + nvp.getValue() + "&");
					}
					String signA = sb.toString(); // 根据签名格式组装数据，详见微信支付api
					String stringSignTemp = signA + "key=" + CommonUtil.SIGN_KEY; // 根据签名格式组装数据，详见微信支付api
					LogUtil.w("mingguo", "signA  " + signA);
					LogUtil.w("mingguo", "stringSignTemp  " + stringSignTemp);
					String sign = UtilTool.MD5Encode(stringSignTemp).toUpperCase();
					LogUtil.w("mingguo", "sign=" + sign);
					nvps.add(new BasicNameValuePair("sign", sign));
					req.sign = sign;
					api.sendReq(req);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.execute();
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 818) {
				String value = (String) msg.obj;
				///////////////////////////////////////////////////////////////////////////////
				Gson gson = new Gson();
				WalletPayment payment = gson.fromJson(value, WalletPayment.class);
				if (!TextUtils.isEmpty(payment.ret)){
					if (payment.ret.equals("0")) {
						successful = true;
						Intent intent = new Intent(HousePayActivity.this, PaymentStatusActivity.class);
						intent.putExtra("flag", successful);
						intent.putExtra("orderID", orderID);
						intent.putExtra("rentNO", rentNO);
						intent.putExtra("orderCreatedDate", orderCreatedDate);
						intent.putExtra("pay_price", price);
						startActivity(intent);
						if (!TextUtils.isEmpty(payment.fee)){
							CommonUtil.mUserWallet = payment.fee;
						}
						finish();
					} else{
						successful = false;
						if (payment.msg != null) {
							Toast.makeText(HousePayActivity.this, payment.msg, Toast.LENGTH_LONG).show();
						} else {
							Intent intent = new Intent(HousePayActivity.this, PaymentStatusActivity.class);
							intent.putExtra("flag", successful);
							startActivity(intent);
						}
					}
				}

			}else if (msg.what == 100){
				//{"ret":"0","msg":"success","status":"2"}
				JSONObject obj = null;
				try {
					obj = new JSONObject((String)msg.obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (obj != null){
					String status = obj.optString("status");
					if (status != null){
						if (status.equalsIgnoreCase(CommonUtil.ORDER_STATUS_NEED_PAY)){
							if (isPayByWechat) {
								Toast.makeText(HousePayActivity.this, "微信支付", Toast.LENGTH_SHORT).show();
								ViewUtil.showLoadingView(HousePayActivity.this, loadingView);
								api = WXAPIFactory.createWXAPI(HousePayActivity.this, CommonUtil.APP_ID);
								if (CommonUtil.version_pay_test){
									if (realPrice != null && realPrice.length() > 0){
										//测试支付，取钱的第一位
										startPay(realPrice.subSequence(0, 1)+"", UtilTool.generateOrderNo(), "127.0.0.1");
									}
								}else{
									startPay(realPrice, UtilTool.generateOrderNo(), "127.0.0.1");
								}
								
							} else {
								
								if (ownerId != null && renterId != null) {
									showWalletPayDialog();
								}
							}
						}else{
							ViewUtil.dismissLoadingView();
							Toast.makeText(HousePayActivity.this, "支付订单状态异常，请刷新订单！", Toast.LENGTH_SHORT).show();
						}
					}
				}
				
			}
		};
	};
	private String ownerId;
	private String rentNO;
	private String price;

	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mPayUseWallet)) {
				LogUtil.e("", action + "======" + templateInfo);
				Message msg = mHandler.obtainMessage();
				msg.what = 818;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else	if (action.equals(mOrderStatusAction)) {
					Message msg = mHandler.obtainMessage();
					msg.what = 100;
					msg.obj = templateInfo;
					msg.sendToTarget();
				}
		}
	}

}