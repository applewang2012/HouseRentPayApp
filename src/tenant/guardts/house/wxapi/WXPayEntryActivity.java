package tenant.guardts.house.wxapi;






import org.ksoap2.serialization.SoapObject;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.BaseActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler, DataStatusInterface{
	
	private static final String TAG = "mingguo";
	private View mSuccessFrameLayout, mFailedFrameLayout;
    private IWXAPI api;
	private TextView mOrderNo;
	private TextView mOrderTime;
	private TextView mOrderMonkey;
	private Button mFinishPay;
	private Button mFailedPay;
	private View mLoadingView;
	private HoursePresenter mPresenter;
	private String mCompleteRentAttribute =  "http://tempuri.org/CompleteRentAttribute";
	private String mUpdateOrderAction =  "http://tempuri.org/UpdateOrderInfo";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
    	api = WXAPIFactory.createWXAPI(this, CommonUtil.APP_ID);
        api.handleIntent(getIntent(), this);
        
//        mLoadingView = (View)findViewById(R.id.id_data_loading);
//		mLoadingView.setVisibility(View.INVISIBLE);
        
    }
    
    private void initSuccessView(View v){
    	mOrderNo = (TextView)v.findViewById(R.id.id_pay_order_no);
        mOrderTime = (TextView)v.findViewById(R.id.id_pay_oder_time);
        mOrderMonkey = (TextView)v.findViewById(R.id.id_pay_monkey);
        mOrderNo.setText(CommonUtil.mPayHouseOrderId);
        mOrderTime.setText(CommonUtil.ORDER_TIME);
        mOrderNo.setText(CommonUtil.ORDER_NO);
        mOrderMonkey.setText(CommonUtil.ORDER_MONKEY);
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
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mCompleteRentAttribute, rpc);
		mPresenter.startPresentServiceTask();
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
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mUpdateOrderAction, rpc);
		mPresenter.startPresentServiceTask();
	}
    
    private void showLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
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
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode+" yuanyin  "+resp.errStr+" open  id  "+resp.openId);
		ActivityController.finishAll();
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0){
				View v = getLayoutInflater().inflate(R.layout.activity_successful_payment, null);
				mPresenter = new HoursePresenter(WXPayEntryActivity.this, this);
				mLoadingView = (View) v.findViewById(R.id.id_data_loading);
				setContentView(v);
				initSuccessView(v);
				showLoadingView();
				completeHouseRentAttributeInfo(CommonUtil.mPayHouseOrderId);
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
			dismissLoadingView();
			if (msg.what == 100){
				showLoadingView();
				updateOrderInfo(CommonUtil.mPayHouseOrderId);
			}else if (msg.what == 101){
				
			}
		}
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		Log.i("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mCompleteRentAttribute)){
				mHandler.sendEmptyMessageDelayed(100, 100);
			}else if (action.equals(mUpdateOrderAction)){
				mHandler.sendEmptyMessageDelayed(101, 10);
			}
		}
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