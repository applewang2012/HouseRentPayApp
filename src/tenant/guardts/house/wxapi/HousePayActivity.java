package tenant.guardts.house.wxapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.BaseActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.UtilTool;
import tenant.guardts.house.wxpay.WeiXinPay;

@SuppressWarnings("deprecation")
public class HousePayActivity extends BaseActivity implements IWXAPIEventHandler{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_house_pay_select);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView titlebar = (TextView)findViewById(R.id.id_titlebar);
		titlebar.setText("支付房款");
		ImageView searchImage = (ImageView)findViewById(R.id.id_titlebar_right_search);
		searchImage.setVisibility(View.INVISIBLE);
        
        
        Button payButton = (Button)findViewById(R.id.id_button_pay_money_button);
        payButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				api = WXAPIFactory.createWXAPI(HousePayActivity.this, CommonUtil.APP_ID);
				startPay("1", UtilTool.generateOrderNo(), "127.0.0.1");
			}
		});
        
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
	
	
	

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.i("mingguo", "onResp error  code    "+resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("微信支付结果："+String.valueOf(resp.errCode));
			builder.show();
		}
	}

//	@Override
//	public void onReq(BaseReq req) {
//		Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT).show();
//		
//		switch (req.getType()) {
//		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//			goToGetMsg();		
//			break;
//		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//			goToShowMsg((ShowMessageFromWX.Req) req);
//			break;
//		case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
//			Toast.makeText(this, R.string.launch_from_wx, Toast.LENGTH_SHORT).show();
//			break;
//		default:
//			break;
//		}
//	}
//
//	// ����Ӧ�÷��͵�΢�ŵ�����������Ӧ����ص����÷���
//	@Override
//	public void onResp(BaseResp resp) {
//		Toast.makeText(this, "openid = " + resp.openId, Toast.LENGTH_SHORT).show();
//		
//		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//			Toast.makeText(this, "code = " + ((SendAuth.Resp) resp).code, Toast.LENGTH_SHORT).show();
//		}
//		
//		int result = 0;
//		
//		switch (resp.errCode) {
//		case BaseResp.ErrCode.ERR_OK:
//			result = R.string.errcode_success;
//			break;
//		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			result = R.string.errcode_cancel;
//			break;
//		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			result = R.string.errcode_deny;
//			break;
//		default:
//			result = R.string.errcode_unknown;
//			break;
//		}
//		
//		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//	}
//	
//	private void goToGetMsg() {
//		Intent intent = new Intent(this, GetFromWXActivity.class);
//		intent.putExtras(getIntent());
//		startActivity(intent);
//		finish();
//	}
//	
//	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//		WXMediaMessage wxMsg = showReq.message;		
//		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//		
//		StringBuffer msg = new StringBuffer(); // ��֯һ������ʾ����Ϣ����
//		msg.append("description: ");
//		msg.append(wxMsg.description);
//		msg.append("\n");
//		msg.append("extInfo: ");
//		msg.append(obj.extInfo);
//		msg.append("\n");
//		msg.append("filePath: ");
//		msg.append(obj.filePath);
//		
//		Intent intent = new Intent(this, ShowFromWXActivity.class);
//		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//		startActivity(intent);
//		finish();
//	}
}