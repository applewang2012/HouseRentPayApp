package tenant.guardts.house.wxapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.BaseActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.util.UtilTool;
import tenant.guardts.house.util.ViewUtil;
import tenant.guardts.house.wxpay.WeiXinPay;

public class RechargeActivity extends BaseActivity {

	private TextView mTitleBar;
	private CheckBox monkey800;
	private CheckBox monkey500;
	private CheckBox monkey200;
	private CheckBox moneyOther;
	private String mChargePrice;
	
	private IWXAPI api;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_recharge);
		ActivityController.addActivity(this);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("钱包充值");
		initView();
	}
	
	private void initView(){
		monkey800 = (CheckBox)findViewById(R.id.id_rechange_money_800);
		monkey500 = (CheckBox)findViewById(R.id.id_rechange_money_500);
		monkey200 = (CheckBox)findViewById(R.id.id_rechange_money_200);
		moneyOther = (CheckBox)findViewById(R.id.id_rechange_other_money);
		final EditText input = (EditText)findViewById(R.id.id_rechange_input_other_monkey);
		final View loadingView = (View)findViewById(R.id.id_data_loading);
//		input.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				if (hasFocus){
//					monkey800.setChecked(false);
//					monkey200.setChecked(false);
//					monkey500.setChecked(false);
//					moneyOther.setChecked(true);
//				}else{
//					
//				}
//			}
//		});
		input.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				monkey800.setChecked(false);
				monkey200.setChecked(false);
				monkey500.setChecked(false);
				moneyOther.setChecked(true);
			}
		});
		monkey800.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				LogUtil.w("mingguo", "oncheck changed listen  "+isChecked);
				if (isChecked){
					monkey500.setChecked(false);
					monkey200.setChecked(false);
					moneyOther.setChecked(false);
					monkey800.setTextColor(Color.parseColor("#ffffff"));
					monkey500.setTextColor(Color.parseColor("#444444"));
					monkey200.setTextColor(Color.parseColor("#444444"));
					moneyOther.setTextColor(Color.parseColor("#444444"));
					//input.clearFocus();
				}else{
					monkey800.setTextColor(Color.parseColor("#444444"));
				}
			}
		});
		monkey200.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					monkey500.setChecked(false);
					monkey800.setChecked(false);
					moneyOther.setChecked(false);
					monkey200.setTextColor(Color.parseColor("#ffffff"));
					monkey500.setTextColor(Color.parseColor("#444444"));
					monkey800.setTextColor(Color.parseColor("#444444"));
					moneyOther.setTextColor(Color.parseColor("#444444"));
					//input.clearFocus();
				}else{
					monkey200.setTextColor(Color.parseColor("#444444"));
				}
			}
		});
		monkey500.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					monkey800.setChecked(false);
					monkey200.setChecked(false);
					moneyOther.setChecked(false);
					monkey500.setTextColor(Color.parseColor("#ffffff"));
					monkey800.setTextColor(Color.parseColor("#444444"));
					monkey200.setTextColor(Color.parseColor("#444444"));
					moneyOther.setTextColor(Color.parseColor("#444444"));
					//input.clearFocus();
				}else{
					monkey500.setTextColor(Color.parseColor("#444444"));
				}
			}
		});
		moneyOther.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					monkey800.setChecked(false);
					monkey200.setChecked(false);
					monkey500.setChecked(false);
					moneyOther.setTextColor(Color.parseColor("#ffffff"));
					monkey500.setTextColor(Color.parseColor("#444444"));
					monkey800.setTextColor(Color.parseColor("#444444"));
					monkey200.setTextColor(Color.parseColor("#444444"));
					input.setVisibility(View.VISIBLE);
					input.setText("100");
				}else{
					input.setVisibility(View.INVISIBLE);
					moneyOther.setTextColor(Color.parseColor("#444444"));
				}
			}
		});
		
		Button pay = (Button)findViewById(R.id.id_rechange_button);
		pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (monkey800.isChecked()){
					mChargePrice = "800";
				}else if (monkey500.isChecked()){
					mChargePrice = "500";
				}else if (monkey200.isChecked()){
					mChargePrice = "200";
				}else if (moneyOther.isChecked()){
					mChargePrice = input.getEditableText().toString();
				}
				if (mChargePrice != null && mChargePrice.length() > 0){
					if (mChargePrice.subSequence(0, 1).equals("0")){
						Toast.makeText(getApplicationContext(), "充值金额输入有误！", Toast.LENGTH_SHORT).show();
						return;
					}
					if (mChargePrice.length() < 3){
						Toast.makeText(getApplicationContext(), "最低充值金额为100元！", Toast.LENGTH_SHORT).show();
						return;
					}
					CommonUtil.mPayHouseOrderId = null;
					CommonUtil.ORDER_MONKEY = mChargePrice;
					//CommonUtil.ORDER_MONKEY = mChargePrice+"00"; //真实价格
					ViewUtil.showLoadingView(RechargeActivity.this, loadingView);
					api = WXAPIFactory.createWXAPI(RechargeActivity.this, CommonUtil.APP_ID);
					if (CommonUtil.version_test){
						startPay("1", UtilTool.generateOrderNo(), "127.0.0.1");
					}else{
						startPay(mChargePrice+"00", UtilTool.generateOrderNo(), "127.0.0.1");
					}
					
				}else{
					Toast.makeText(getApplicationContext(), "请选择充值金额！", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
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
	
	
}
