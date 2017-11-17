package tenant.guardts.house;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.CompleteStatus;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.util.UtilTool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class PaymentStatusActivity extends BaseActivity implements DataStatusInterface {
	private String mAddBillLog="http://tempuri.org/AddBillLog";
	private String mCompleteRentAttribute = "http://tempuri.org/CompleteRentAttribute";
	private HoursePresenter mPresenter;
	private String mUpdateWalletction = "http://tempuri.org/UpdateUserWallet";
	
	private String rentNO;
	private String orderCreatedDate;
	private String price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		successful = getIntent().getBooleanExtra("flag", false);
		orderID = getIntent().getStringExtra("orderID");
		rentNO = getIntent().getStringExtra("rentNO");
		orderCreatedDate = getIntent().getStringExtra("orderCreatedDate");
		price=getIntent().getStringExtra("pay_price");
		boolean successful = getIntent().getBooleanExtra("flag", false);
		orderID = getIntent().getStringExtra("orderID");
		LogUtil.e("", orderID+"===============");
		if (successful) {
			setContentView(R.layout.activity_successful_payment);
		} else {
			setContentView(R.layout.activity_payment_failure);
		}
		mPresenter = new HoursePresenter(PaymentStatusActivity.this, this);
		initView();
		initEvent();
	}

	private void initEvent() {
		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (orderID != null)
					
				completeHouseRentAttributeInfo(orderID);
			}
		});

	}


	private void initView() {
			

		
		finish = (Button) findViewById(R.id.id_button_finish_pay);
		if(successful){
			TextView tvRentNO=(TextView) findViewById(R.id.id_pay_order_no);
			tvRentNO.setText(orderID);
			TextView tvOrderCreatedDate=(TextView) findViewById(R.id.id_pay_oder_time);
			String date = UtilTool.stampToNormalDate(orderCreatedDate.substring(6,orderCreatedDate.length()-2));
			tvOrderCreatedDate.setText(date);
			TextView money=(TextView) findViewById(R.id.id_pay_monkey);
			money.setText("¥"+price);
			
		}
	}
	
//	private void addBillLog(String rentIDCard,String ownerIDCard,String fee){
//		String url = CommonUtil.mUserHost + "Services.asmx?op=AddBillLog";
//		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mAddBillLog));
//		rpc.addProperty("renteeIDCard", rentIDCard);
//		rpc.addProperty("ownerIDCard", ownerIDCard);
//		rpc.addProperty("fee", fee);
//		mPresenter.readyPresentServiceParams(this, url, mAddBillLog, rpc);
//		mPresenter.startPresentServiceTask(true);
//	}
	
	private void addBillLogRequestInfo(String renterId, String ownerId, String fee, String type){
    	if (fee == null || fee.equals("")){
    		return;
    	}
		String url = CommonUtil.mUserHost+"Services.asmx?op=AddBillLog";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mAddBillLog));
		rpc.addProperty("renteeIDCard", renterId);
		rpc.addProperty("ownerIDCard", ownerId);
		rpc.addProperty("fee", fee);
		rpc.addProperty("type", type);  //0微信，1钱包
		mPresenter.readyPresentServiceParams(this, url, mAddBillLog, rpc);
		mPresenter.startPresentServiceTask(true);
	}
	
//	private void updateWalletRequestInfo(String idcard, String fee){
//    	if (fee == null || fee.equals("")){
//    		return;
//    	}
//		String url = CommonUtil.mUserHost+"Services.asmx?op=UpdateUserWallet";
//		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUpdateWalletction));
//		rpc.addProperty("idCard", idcard);
//		rpc.addProperty("fee", fee);
//		rpc.addProperty("type", "0");  //0增加，1减少
//		mPresenter.readyPresentServiceParams(this, url, mUpdateWalletction, rpc);
//		mPresenter.startPresentServiceTask(true);
//	}
	

	/**
	 * 通知后台更新订单状态
	 * 
	 * @param id
	 *            房屋订单号
	 */
	private void completeHouseRentAttributeInfo(String id) {
		if (id == null || id.equals("")) {
			return;
		}
		String url = CommonUtil.mUserHost + "Services.asmx?op=CompleteRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCompleteRentAttribute));
		rpc.addProperty("id", id);
		mPresenter.readyPresentServiceParams(this, url, mCompleteRentAttribute, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 818) {
				
				String value = (String) msg.obj;
				Gson gson = new Gson();
				CompleteStatus completeStatus = gson.fromJson(value, CompleteStatus.class);
				try {
					int ret = Integer.parseInt(completeStatus.ret);
					if (ret == 0) {
						if(!TextUtils.isEmpty(CommonUtil.OWNER_IDCARD)&&!TextUtils.isEmpty(CommonUtil.mRegisterIdcard)&&!TextUtils.isEmpty(price))
						//updateWalletRequestInfo(CommonUtil.OWNER_IDCARD, CommonUtil.ORDER_MONKEY);
						addBillLogRequestInfo(CommonUtil.mRegisterIdcard, CommonUtil.OWNER_IDCARD, price, "1");
					} else {
						Toast.makeText(PaymentStatusActivity.this, "订单提交失败", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else if (msg.what == 819){
				addBillLogRequestInfo(CommonUtil.mRegisterIdcard, CommonUtil.OWNER_IDCARD, price, "1");
		}else if(msg.what == 100){
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					if (object != null){
						String ret = object.optString("ret");
						if (ret != null){
							if (ret.equals("0")){
//								String walletValue = object.optString("fee");
//								if (walletValue != null && !walletValue.equals("")){
//									CommonUtil.mUserWallet = walletValue;
									Toast.makeText(getApplicationContext(), "恭喜您订单更新成功", Toast.LENGTH_SHORT).show();
									finish();
//								}
							}else{
								GlobalUtil.shortToast(getApplication(), "抱歉，提交订单失败！", getApplicationContext().getResources().getDrawable(R.drawable.ic_dialog_no));
							}
						}
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	};
	private Button finish;
	private String orderID;
	private boolean successful;



	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mCompleteRentAttribute)) {
				LogUtil.e("", action + "======" + templateInfo);
				Message msg = mHandler.obtainMessage();
				msg.what = 818;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if(action.equals(mAddBillLog)){
				LogUtil.e("", action + "======" + templateInfo);
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mUpdateWalletction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 819;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}




	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
	}
}

