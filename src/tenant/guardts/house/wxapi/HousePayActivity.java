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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.BaseActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.RegisterUserStep1Activity;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.UtilTool;
import tenant.guardts.house.wxpay.WeiXinPay;

public class HousePayActivity extends BaseActivity {

	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private IWXAPI api;
	private View mLoadingView;
	private String realPrice = null;
	private LinearLayout mWallet;
	private LinearLayout mWechat;
	private CheckBox mCheckBoxWallet;
	private CheckBox mCheckBoxWechat;
	private boolean isPayByWechat=true;//是否使用微信支付

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_house_payment);
		initView();
		initEvent();
		final String price = "1.0";//////////////////////////////////
		final String ownerId = getIntent().getStringExtra("owner_idcard");
		TextView priceText = (TextView) findViewById(R.id.id_pay_price_show);
		
		if (price == null || price.equals("null")) {

			priceText.setText("0.0元");
		} else {

			priceText.setText(price + "元");
		}
		mLoadingView = (View) findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);
		CommonUtil.ORDER_MONKEY = price;
		CommonUtil.OWNER_IDCARD = ownerId;
		try {
			int dotInex = price.indexOf(".");
			if (dotInex >= 0) {
				String priceInt = price.substring(0, dotInex);
				realPrice = priceInt + "00";
			} else {
				Toast.makeText(HousePayActivity.this, "价钱有误", Toast.LENGTH_SHORT).show();
				return;
			}

			Log.i("mingguo", "pay activity  price int   " + realPrice);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Button payButton = (Button) findViewById(R.id.id_button_pay_money_button);
		payButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showLoadingView();

				if (isPayByWechat) {
					Toast.makeText(HousePayActivity.this, "微信支付", Toast.LENGTH_SHORT).show();
					api = WXAPIFactory.createWXAPI(HousePayActivity.this, CommonUtil.APP_ID);
					startPay(realPrice, UtilTool.generateOrderNo(), "127.0.0.1");
				}else{
					Toast.makeText(HousePayActivity.this, "钱包支付", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private void initView() {
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

	private void showLoadingView() {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
			ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
			if (imageView != null) {
				RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(HousePayActivity.this,
						R.anim.anim_rotate);
				imageView.startAnimation(rotate);
			}
		}
	}

	private void dismissLoadingView() {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
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
					Log.e("mingguo", "onPostExecute  " + weiXinReturn);
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
					Log.i("mingguo", "signA  " + signA);
					Log.i("mingguo", "stringSignTemp  " + stringSignTemp);
					String sign = UtilTool.MD5Encode(stringSignTemp).toUpperCase();
					Log.i("mingguo", "sign=" + sign);
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