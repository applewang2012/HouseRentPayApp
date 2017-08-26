package tenant.guardts.house.wxapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.BaseActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.UtilTool;
import tenant.guardts.house.wxpay.WeiXinPay;

public class HousePayActivity extends BaseActivity{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private IWXAPI api;
	private View mLoadingView;
	private String realPrice = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_house_pay_select);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView titlebar = (TextView)findViewById(R.id.id_titlebar);
		titlebar.setText("支付房款");
        final String price = getIntent().getStringExtra("pay_price");
        TextView priceText = (TextView)findViewById(R.id.id_pay_price_show);
        priceText.setText(price+"元");
        mLoadingView = (View)findViewById(R.id.id_data_loading);
        mLoadingView.setVisibility(View.INVISIBLE);
        try {
        	String priceInt = price.substring(0, price.indexOf("."));
        	realPrice = priceInt+"00";
        	Log.i("mingguo", "pay activity  price int   "+realPrice);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        Button payButton = (Button)findViewById(R.id.id_button_pay_money_button);
        payButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLoadingView();
				api = WXAPIFactory.createWXAPI(HousePayActivity.this, CommonUtil.APP_ID);
				startPay("2", UtilTool.generateOrderNo(), "127.0.0.1");
			}
		});
        
    }
    
    private void showLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(HousePayActivity.this, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
    
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}
    
    private void startPay(final String price, final String orderNo, final String ip ){
		
		new AsyncTask<Void, Void, Void>() {
			private String weiXinReturn = null;
			@Override
			protected Void doInBackground(Void... params) {
				weiXinReturn =   WeiXinPay.startPay(price, orderNo, ip);
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				try {
					PayReq req = new PayReq();
					Log.e("mingguo", "onPostExecute  "+weiXinReturn);
					Map<String, String> backMap = UtilTool.decodeXml(weiXinReturn);
					req.appId			= CommonUtil.APP_ID;
					req.partnerId		= CommonUtil.WX_PARTNER_ID;
					req.prepayId		= backMap.get("prepay_id");
					req.nonceStr		= UUID.randomUUID().toString().replace("-", "");
					req.timeStamp		= System.currentTimeMillis()/1000+"";
					req.packageValue	= "Sign=WXPay";
					if (req.prepayId == null)
						return;
					CommonUtil.ORDER_PREPAY_ID = req.prepayId;
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					// 调用统一下单接口必需传的参数,可以查看微信支付统一下单接口api查看每个参数的意思
					nvps.add(new BasicNameValuePair("appid", CommonUtil.APP_ID));
					nvps.add(new BasicNameValuePair("noncestr", req.nonceStr));
					nvps.add(new BasicNameValuePair("package", req.packageValue));
					nvps.add(new BasicNameValuePair("partnerid", CommonUtil.WX_PARTNER_ID));
					nvps.add(new BasicNameValuePair("prepayid",req.prepayId));
					nvps.add(new BasicNameValuePair("timestamp", req.timeStamp));
					
					StringBuffer sb = new StringBuffer();

					for (NameValuePair nvp : nvps) {
						sb.append(nvp.getName() + "=" + nvp.getValue() + "&");
					}
					String signA = sb.toString(); // 根据签名格式组装数据，详见微信支付api
					String stringSignTemp = signA + "key=" + CommonUtil.SIGN_KEY; // 根据签名格式组装数据，详见微信支付api
					Log.i("mingguo","signA  " + signA);
					Log.i("mingguo","stringSignTemp  " + stringSignTemp);
					String sign = UtilTool.MD5Encode(stringSignTemp).toUpperCase();
					Log.i("mingguo","sign=" + sign);
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