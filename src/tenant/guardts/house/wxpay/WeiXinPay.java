package tenant.guardts.house.wxpay;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.UtilTool;
/**
 * 
 * @作者：lsq
 * @时间：2015-12-29 下午12:42:56
 * 微信支付统一下单接口调用方法
 */
public class WeiXinPay {
	/** APP_ID 应用从官方网站申请到的合法appId */
	//public static final String WX_APP_ID = "APP_ID";
	/** 商户号 */
	//public static final String WX_PARTNER_ID = "商户号";
	/** 统一下单接口链接 */
	public static final String url="https://api.mch.weixin.qq.com/pay/unifiedorder";
	/** 商户平台和开发平台约定的API密钥，在商户平台设置 */
	//public static final String key="API密钥";

	public String submitOrder(String realPayPrice, String  showInfo, String order_no, String ip)
			throws UnsupportedEncodingException {
		//int realpayPrice = (int) (realPayPrice);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// 调用统一下单接口必需传的参数,可以查看微信支付统一下单接口api查看每个参数的意思
		nvps.add(new BasicNameValuePair("appid", CommonUtil.APP_ID));
		nvps.add(new BasicNameValuePair("body", showInfo));
		nvps.add(new BasicNameValuePair("mch_id",CommonUtil.WX_PARTNER_ID));
		nvps.add(new BasicNameValuePair("nonce_str", UUID.randomUUID().toString()
				.replace("-", "")));
		nvps.add(new BasicNameValuePair("notify_url",
				"http://www.weixin.qq.com/wxpay/pay.php")); //回调地址需要根据实际项目做修改
		nvps.add(new BasicNameValuePair("out_trade_no", order_no));
		nvps.add(new BasicNameValuePair("spbill_create_ip", ip));//ip地址需要根据实际项目做修改
		nvps.add(new BasicNameValuePair("total_fee", realPayPrice));
		nvps.add(new BasicNameValuePair("trade_type", "APP"));
		CommonUtil.ORDER_NO = order_no;
		CommonUtil.ORDER_MONKEY = realPayPrice;
		
		CommonUtil.ORDER_TIME = UtilTool.stampToNormalDate(System.currentTimeMillis()+"");
		StringBuffer sb = new StringBuffer();

		for (NameValuePair nvp : nvps) {
			sb.append(nvp.getName() + "=" + nvp.getValue() + "&");
		}
		String signA = sb.toString(); // 根据签名格式组装数据，详见微信支付api
		//String signA = "appid=wxae25cb3fefdc75ae&body=body&mch_id=1481965242&nonce_str=23afad3ca5ce4ae388279d43ef600411&notify_url=http://www.weixin.qq.com/wxpay/pay.php&out_trade_no=100&spbill_create_ip=127.0.0.1&total_fee=100&trade_type=APP";
//		int dotIndex = signA.lastIndexOf("&");
//		signA = (String) signA.subSequence(0, dotIndex);
		String stringSignTemp = signA + "key=" + CommonUtil.SIGN_KEY; // 根据签名格式组装数据，详见微信支付api
		Log.i("mingguo","signA=" + signA);
		Log.i("mingguo","stringSignTemp=" + stringSignTemp);
//		String sign = DigestUtils.md5Hex(
//				getContentBytes(stringSignTemp, "UTF-8")).toUpperCase(); // 把组装好的签名数据md5之后字母都转换为大写，详见微信支付api
		String sign = UtilTool.MD5Encode(stringSignTemp).toUpperCase();
		Log.i("mingguo","sign=" + sign);
		nvps.add(new BasicNameValuePair("sign", sign)); // 把签名后的数据组装成参数
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setReqContent(url);
		String resContent = "";
		if (httpClient.callHttpPost(url, toXml(nvps))) {
			resContent = httpClient.getResContent();
			String result = new String(resContent.getBytes("GBK"), "UTF-8");
			
			Log.i("mingguo","请求返回的结果=" + result);
			return result;
		}
		return null;
	}

	// 转换成xml格式
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");
			sb.append((params.get(i)).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");
		return sb.toString();
	}

	// 编码转换
	public byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:"
					+ charset);
		}
	}

	public static String startPay(String price, String orderNo, String ip){
		String back;
		WeiXinPay wx = new WeiXinPay();
		try {
			back = wx.submitOrder(price, orderNo, orderNo, ip);
			return back;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}