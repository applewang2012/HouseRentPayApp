package tenant.guardts.house.wxapi;






import org.ksoap2.serialization.SoapObject;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import tenant.guardts.house.BaseActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, DataStatusInterface{
	
	private static final String TAG = "mingguo";
	private View mSuccessFrameLayout, mFailedFrameLayout;
    private IWXAPI api;
	private TextView mOrderNo;
	private TextView mOrderTime;
	private TextView mOrderMonkey;
	private Button mFinishPay;
	private Button mFailedPay;
	
	private HoursePresenter mPresenter;
	private String mCompleteRentAttribute =  "http://tempuri.org/CompleteRentAttribute";
	private String mUpdateOrderAction =  "http://tempuri.org/UpdateOrderInfo";
	private String mDepositWalletAction = "http://tempuri.org/DepositWallet";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        Log.w("mingguo", "wxpay entry activity  oncreate  ");
    	api = WXAPIFactory.createWXAPI(this, CommonUtil.APP_ID);
        api.handleIntent(getIntent(), this);
        
//        
//		
        
    }
    
    private void initSuccessView(View v){
    	mOrderNo = (TextView)v.findViewById(R.id.id_pay_order_no);
        mOrderTime = (TextView)v.findViewById(R.id.id_pay_oder_time);
        mOrderMonkey = (TextView)v.findViewById(R.id.id_pay_monkey);
        mOrderNo.setText(CommonUtil.mPayHouseOrderId);
        mOrderTime.setText(CommonUtil.ORDER_TIME);
        mOrderNo.setText(CommonUtil.ORDER_NO);
        mOrderMonkey.setText(CommonUtil.ORDER_MONKEY+"　元");
        mFinishPay = (Button)v.findViewById(R.id.id_button_finish_pay);
        Log.i("mingguo", "CommonUtil.mPayHouseOrderId  "+CommonUtil.mPayHouseOrderId+" CommonUtil.ORDER_TIME "+CommonUtil.ORDER_TIME+
        		" CommonUtil.ORDER_NO "+CommonUtil.ORDER_NO+" CommonUtil.ORDER_MONKEY "+CommonUtil.ORDER_MONKEY);
        mFinishPay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }
    
    private void initFailedView(View v){
        
        mFailedPay = (Button)v.findViewById(R.id.id_button_retry_pay);
        
        mFailedPay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    }
    
    private void completeHouseRentAttributeInfo(String id){
    	if (id == null || id.equals("")){
    		return;
    	}
		String url = CommonUtil.mUserHost+"Services.asmx?op=CompleteRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCompleteRentAttribute));
		rpc.addProperty("id", id);
		mPresenter.readyPresentServiceParams(this, url, mCompleteRentAttribute, rpc);
		mPresenter.startPresentServiceTask(true);
	}
    
    private void depositWalletRequestInfo(String fee){
    	if (fee == null || fee.equals("")){
    		return;
    	}
		String url = CommonUtil.mUserHost+"Services.asmx?op=DepositWallet";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mDepositWalletAction));
		rpc.addProperty("idCard", CommonUtil.mRegisterIdcard);
		rpc.addProperty("fee", fee);
		mPresenter.readyPresentServiceParams(this, url, mDepositWalletAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
    
    private void updateOrderInfo(String id){
    	if (id == null || id.equals("")){
    		return;
    	}
		String url = CommonUtil.mUserHost+"Services.asmx?op=UpdateOrderInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mUpdateOrderAction));
		rpc.addProperty("id", id);
		rpc.addProperty("appId", CommonUtil.APP_ID);
		rpc.addProperty("body", CommonUtil.ORDER_NO);
		rpc.addProperty("mchId", CommonUtil.WX_PARTNER_ID);
		rpc.addProperty("tradeNo", CommonUtil.ORDER_NO);
		rpc.addProperty("fee", CommonUtil.ORDER_MONKEY);
		rpc.addProperty("prepayId", CommonUtil.ORDER_PREPAY_ID );
		rpc.addProperty("ownerIdCard", CommonUtil.OWNER_IDCARD );
		mPresenter.readyPresentServiceParams(this, url, mUpdateOrderAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}
    

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	
	 

	@Override
	public void onReq(BaseReq req) {
		
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.i(TAG, "onPayFinish, errCode = " + resp.errCode+" yuanyin  "+resp.errStr);
		ActivityController.finishAll();
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0){
				if (CommonUtil.mPayHouseOrderId != null && !CommonUtil.mPayHouseOrderId.equals("")){
					View v = getLayoutInflater().inflate(R.layout.activity_successful_payment, null);
					mPresenter = new HoursePresenter(WXPayEntryActivity.this, this);
					setContentView(v);
					initSuccessView(v);
					
					completeHouseRentAttributeInfo(CommonUtil.mPayHouseOrderId);
				}else{
					View v = getLayoutInflater().inflate(R.layout.activity_successful_payment, null);
					mPresenter = new HoursePresenter(WXPayEntryActivity.this, this);
					setContentView(v);
					initSuccessView(v);
					
					depositWalletRequestInfo(CommonUtil.ORDER_MONKEY);
				}
				
			}else {
				View v = getLayoutInflater().inflate(R.layout.activity_payment_failure, null);
				setContentView(v);
				initFailedView(v);
			}
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if (msg.what == 100){
				
				updateOrderInfo(CommonUtil.mPayHouseOrderId);
			}else if (msg.what == 101){
				
			}else if (msg.what == 102){
				
			}
		}
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		Log.i("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		super.onStatusSuccess(action, templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mCompleteRentAttribute)){
				mHandler.sendEmptyMessageDelayed(100, 100);
			}else if (action.equals(mUpdateOrderAction)){
				mHandler.sendEmptyMessageDelayed(101, 10);
			}else if (action.equals(mDepositWalletAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 102;
				msg.obj = templateInfo;
				mHandler.sendMessage(msg);
			}
		}
	}


	
}