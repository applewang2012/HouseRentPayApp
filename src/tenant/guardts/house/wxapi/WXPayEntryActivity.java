package tenant.guardts.house.wxapi;






import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import tenant.guardts.house.R;
import tenant.guardts.house.util.CommonUtil;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "mingguo";
	private FrameLayout mSuccessFrameLayout, mFailedFrameLayout;
    private IWXAPI api;
	private TextView mOrderNo;
	private TextView mOrderTime;
	private TextView mOrderMonkey;
	private Button mFinishPay;
	private Button mFailedPay;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        mSuccessFrameLayout = (FrameLayout)findViewById(R.id.id_weixin_paysuccess);
        mFailedFrameLayout = (FrameLayout)findViewById(R.id.id_weixin_payfailed);
    	api = WXAPIFactory.createWXAPI(this, CommonUtil.APP_ID);
        api.handleIntent(getIntent(), this);
        mOrderNo = (TextView)findViewById(R.id.id_pay_order_no);
        mOrderTime = (TextView)findViewById(R.id.id_pay_oder_time);
        mOrderMonkey = (TextView)findViewById(R.id.id_pay_monkey);
        mFinishPay = (Button)findViewById(R.id.id_button_finish_pay);
        mFailedPay = (Button)findViewById(R.id.id_button_retry_pay);
        mFinishPay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        mFailedPay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
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

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0){
				mSuccessFrameLayout.setVisibility(View.VISIBLE);
				mOrderNo.setText(CommonUtil.ORDER_NO);
				mOrderTime.setText(CommonUtil.ORDER_TIME);
				mOrderMonkey.setText(CommonUtil.ORDER_MONKEY);
			}else {
				mFailedFrameLayout.setVisibility(View.VISIBLE);
			}
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
		}
	}
}