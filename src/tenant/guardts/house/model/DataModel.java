package tenant.guardts.house.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.util.UtilTool;

public class DataModel {


	private HoursePresenter mPresenter;
	private String mUrl;
	private String mSoapAction;
	private SoapObject mSoapObject;
	private String[] mDistrictID;
	private String[] mStreetID;
	private HashMap<String, String> mPostData;
	private Context mContext;
	
	public DataModel(HoursePresenter presenter){
		mPresenter = presenter;
	}
	
	public void setAsyncTaskReady(Context ctx, String url, String action, SoapObject object){
		mContext = ctx;
		mUrl = url;
		mSoapAction = action;
		mSoapObject = object;
	}
	
	public void setHttpTaskReady(Context ctx, String url, HashMap<String, String> postdata){
		mContext = ctx;
		mUrl = url;
		mPostData = postdata;
	}
	
	
	public void startDataRequestTask(){
		if (isNetworkAvailable(mContext)){
			new ServiceAsyncTask().execute();
		}else{
			mPresenter.notifyDataRequestError(mUrl, null);
		}
		
	}
	
	public void startHttpRequestTask(){
		if (isNetworkAvailable(mContext)){
			new HttpAsyncTask().execute();
		}else{
			mPresenter.notifyDataRequestError(mUrl, null);
		}
	}
	
	private class ServiceAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try {
				//修复static被回收的bug
				if (mUrl != null && !mUrl.startsWith("http")){
					SharedPreferences sharedata = mContext.getSharedPreferences("user_info", 0);
					CommonUtil.mUserHost = sharedata.getString("user_host", "");
					mUrl = CommonUtil.mUserHost +mUrl;
				}
				
				 Element[] header = new Element[1]; 
	                header[0] = new Element().createElement(CommonUtil.NAMESPACE, "Authentication"); 
	                
	                Element userName = new Element().createElement(CommonUtil.NAMESPACE, "UserID"); 
	                userName.addChild(Node.TEXT, "admin"); 
	                header[0].addChild(Node.ELEMENT, userName); 
	                
	                Element passwd = new Element().createElement(CommonUtil.NAMESPACE, "PassWord"); 
	                passwd.addChild(Node.TEXT, "Pa$$w0rd780419"); 
	                header[0].addChild(Node.ELEMENT, passwd); 
	                
	                String timestamp = System.currentTimeMillis()+"";
	                Element time = new Element().createElement(CommonUtil.NAMESPACE, "TimeStamp"); 
	                time.addChild(Node.TEXT, timestamp); 
	                header[0].addChild(Node.ELEMENT, time);
	                
	                Element token = new Element().createElement(CommonUtil.NAMESPACE, "Token"); 
	                token.addChild(Node.TEXT, UtilTool.MD5Encode("guardts"+timestamp+"house")); 
	                header[0].addChild(Node.ELEMENT, token);
	                
//	                header[0].setAttribute(CommonUtil.NAMESPACE, "UserID", "admin");
//	                header[0].setAttribute(CommonUtil.NAMESPACE, "PassWord", "Pa$$w0rd780419");
				//添加单用户校验
				if (!TextUtils.isEmpty(CommonUtil.mUserLoginName) && !TextUtils.isEmpty(CommonUtil.XINGE_TOKEN)){
					mSoapObject.addProperty("checkUser", CommonUtil.mUserLoginName);
					mSoapObject.addProperty("checkToken", CommonUtil.XINGE_TOKEN);
				}
				if (!checkOrderStatusBeforeRequest(header, mSoapAction)){
					Log.i("mingguo", "check order status exception  ");
					mPresenter.notifyDataRequestError(null, "获取订单状态异常，请及时刷新订单!");
					return null;
				}
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.headerOut = header; 
				envelope.bodyOut = mSoapObject;
				envelope.dotNet= true;
				envelope.setOutputSoapObject(mSoapObject);
				HttpTransportSE transport = new HttpTransportSE(mUrl,30000);
				transport.call(mSoapAction, envelope);
				SoapObject valueObject = null;
				if(envelope.getResponse()!=null){
					valueObject = (SoapObject)envelope.bodyIn;				
				}
				String resultString = valueObject.getProperty(0).toString();
				if (resultString != null && resultString.contains("headerError")){
					mPresenter.notifyDataRequestError(CommonUtil.getSoapName(mSoapAction), "error header !");
					return null;
				}
				Activity activity = (Activity) mContext;
				if (activity.isFinishing()){
					mPresenter.notifyDataRequestError(CommonUtil.getSoapName(mSoapAction), "error finish ");
				}else{
					mPresenter.notifyDataRequestSuccess(mSoapAction, resultString);
				}
				
			} catch (Exception e) {
				mPresenter.notifyDataRequestError(CommonUtil.getSoapName(mSoapAction), "error exception"+e);
				LogUtil.e("mingguo", "exception  action   "+mSoapAction+"  e  "+e);
			}
			
			return null;
		}
	}
	
	private boolean checkOrderStatusBeforeRequest(Element[] header, String action){
		String actionName = CommonUtil.getSoapName(action);
		String orderId = null;
		if (actionName != null){
			if (actionName.equalsIgnoreCase("ConfirmRentAttribute") || actionName.equalsIgnoreCase("RejectRentAttribute")
					|| actionName.equalsIgnoreCase("CancelRentAttribute")){
				orderId = (String) mSoapObject.getProperty("id");
			}
			if (actionName.equalsIgnoreCase("ExpiredOrder") || actionName.equalsIgnoreCase("ApplyCheckOut")
					|| actionName.equalsIgnoreCase("ConfirmCheckOut")){
				orderId = (String) mSoapObject.getProperty("rraId");
			}
		}
		Log.i("mingguo", "check order status oder id   "+orderId+"  action name  "+actionName);
		if (orderId == null || orderId.equals("")){
			return true;  //订单id有误，无法校验状态，或者其它接口直接放过
		}
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			String statusUrl = "http://qxw2332340157.my3w.com/Services.asmx?op=GetOrderStatus";
			String statusAction = "http://tempuri.org/GetOrderStatus";
			SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(statusAction));
			
			rpc.addProperty("rraId", orderId);
			envelope.headerOut = header; 
			envelope.bodyOut = rpc;
			envelope.dotNet= true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE transport = new HttpTransportSE(statusUrl,30000);
			transport.call(statusAction, envelope);
			SoapObject valueObject = null;
			if(envelope.getResponse()!=null){
				valueObject = (SoapObject)envelope.bodyIn;				
			}
			String resultString = valueObject.getProperty(0).toString(); //{"ret":"0","msg":"success","status":"2"}
			Log.i("mingguo", "check order status order return    "+resultString);
			JSONObject obj = new JSONObject(resultString);
			if (obj != null){
				String status = obj.optString("status");
				if (status != null){
					if (actionName.equalsIgnoreCase("ConfirmRentAttribute")
							|| (actionName.equalsIgnoreCase("RejectRentAttribute"))){
						if (status.equalsIgnoreCase(CommonUtil.ORDER_STATUS_SUBMITT)){
							return true;
						}else {
							return false;
						}
					}else if (actionName.equalsIgnoreCase("CancelRentAttribute") || actionName.equalsIgnoreCase("ExpiredOrder")){
						if (status.equalsIgnoreCase(CommonUtil.ORDER_STATUS_SUBMITT)  || status.equalsIgnoreCase(CommonUtil.ORDER_STATUS_NEED_PAY)){
							return true;
						}else {
							return false;
						}
					}else if (actionName.equalsIgnoreCase("ApplyCheckOut")){
						if (status.equalsIgnoreCase(CommonUtil.ORDER_STATUS_HAS_PAYED)){
							return true;
						}else {
							return false;
						}
						
					}else if (actionName.equalsIgnoreCase("ConfirmCheckOut")){
						if (status.equalsIgnoreCase(CommonUtil.ORDER_STATUS_NEED_CHECKOUT)){
							return true;
						}else {
							return false;
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	
	private class HttpAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try 
	    	{
				int ret = -1;
				if (mContext == null){
					return null;
				}
	    		InputStream ins = null;
//	    		copyAssetFileToFiles(mContext, "nid.crt");
//	    		LogUtil.e("house", " file exsits "+new File(mContext.getFilesDir() + "/"+"nid.crt").exists());
//	    		ins = new  FileInputStream(new File(mContext.getFilesDir() + "/"+"nid.crt"));
	    		ins = mContext.getAssets().open("nid.crt");
	    		CertificateFactory cerFactory = CertificateFactory
                        .getInstance("X.509");  
		        Certificate cer = cerFactory.generateCertificate(ins);
		        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");//("PKCS12", "BC");
		        keyStore.load(null, null);
		        keyStore.setCertificateEntry("trust", cer);
        
	            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
	            Scheme sch = new Scheme("https", socketFactory, 443);
				//HttpClient
				HttpClient client = new DefaultHttpClient(); 
				client.getConnectionManager().getSchemeRegistry().register(sch);
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 30000);   //�������ӳ�ʱֵ30s
	            HttpConnectionParams.setSoTimeout(client.getParams(), 30000);   //���ôӷ���˻�ȡ���ݳ�ʱֵ30s
	            HttpResponse response = null;
	            if (mPostData == null){
	            	HttpGet httpGet = new HttpGet(mUrl);
	            	response = client.execute(httpGet);	
	            	LogUtil.e("house"," get  data  url  "+mUrl);
	            }else{
	            	LogUtil.e("house"," post  data  url  "+mUrl);
	            	HttpPost httpPost = new HttpPost(mUrl);
					// ������������    
		            List<NameValuePair> urlList = new ArrayList<NameValuePair>(); 
		            //���ز���
		            for(Map.Entry<String, String> entry:mPostData.entrySet()){    
		            	urlList.add(new BasicNameValuePair(entry.getKey(), entry.getValue())); 
		            	LogUtil.w("house", "entry.getKey()  "+entry.getKey()+"  entry.getValue()  "+entry.getValue());
		            }   
		            
		            HttpEntity entity = new UrlEncodedFormEntity(urlList, HTTP.UTF_8);
		            httpPost.setEntity(entity);
		            response = client.execute(httpPost);	
	            }
				
				int StatusCode = response.getStatusLine().getStatusCode(); 
				LogUtil.e("house"," get  data  status   "+StatusCode);
				if(StatusCode == HttpStatus.SC_OK) 
				{
					HttpEntity resRet = response.getEntity();  
	                if (resRet != null) 
	                {  
	                    String SvrRet = EntityUtils.toString(response.getEntity(),"UTF-8"); //��ȡ������ֵ 
	                    System.out.println( "  SvrRet  "+SvrRet);
	                    mPresenter.notifyDataRequestSuccess(mUrl, SvrRet);
//	                    JSONObject SvrRet_Json = JSONObject.fromObject(SvrRet);
//	                    System.out.println("����������������ֵ��" + SvrRet);
//	                    if(!SvrRet_Json.getString("ret").equals("1"))
//	        			{
//	        				ret = 0;
//	        				System.out.println("���������������մ���������⣺" + SvrRet_Json.getString("desc"));
//	        			}
//	        			else
//	        			{
//	        				ret = 1;
//	        			}            
	                }
	                else
	                {
	                	mPresenter.notifyDataRequestError(mUrl, "����������������ֵΪnull");
	                	System.out.println( "����������������ֵΪnull");
	                	ret = 0;
	                }
				}
				else
				{
					mPresenter.notifyDataRequestError(mUrl, "����������������Ӧ��֤ƽ̨����������ʧ�ܣ�״̬�룺 status  "+StatusCode);
	            	ret = 0;
				}
	    	}
	    	catch(Exception e)
	    	{
	    		mPresenter.notifyDataRequestError(mUrl, "404:�������������֤������ݳ����쳣��ret=" + e.toString());
	    		System.out.println( "404:�������������֤������ݳ����쳣��ret=" + e.toString());
//	    		ret = 0;
	    	}

			return null;
		}
		
	}
	
	
	
	public static void copyAssetFileToFiles(Context context, String filename) throws IOException {
		InputStream is = context.getAssets().open(filename);
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();

		File of = new File(context.getFilesDir() + "/" + filename);
		of.createNewFile();
		FileOutputStream os = new FileOutputStream(of);
		os.write(buffer);
		os.close();
	}
	
//	public static HttpClient getNewHttpClient() {
//		   try {
//		       KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//		       trustStore.load(null, null);
//		 
//		       SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
//		       sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//		 
//		       HttpParams params = new BasicHttpParams();
//		       HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//		       HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//		 
//		       SchemeRegistry registry = new SchemeRegistry();
//		       registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//		       registry.register(new Scheme("https", sf, 443));
//		 
//		       ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//		 
//		       return new DefaultHttpClient(ccm, params);
//		   } catch (Exception e) {
//		       return new DefaultHttpClient();
//		   }
//	}
	
	private class RelationServiceAsyncTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				String url = CommonUtil.mUserHost+"services.asmx?op=GetHouseInfo";
				String action = "http://tempuri.org/GetHouseInfo";
				SoapObject object = new SoapObject("http://tempuri.org/", getSoapName(mSoapAction));
				
				
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.bodyOut = object;
				envelope.dotNet= true;
				envelope.setOutputSoapObject(object);
				HttpTransportSE transport = new HttpTransportSE(url,30000);
				transport.call(action, envelope);
				SoapObject valueObject = null;
				if(envelope.getResponse()!=null){
					valueObject = (SoapObject)envelope.bodyIn;				
				}
				String resultString = valueObject.getProperty(0).toString();
				
				mPresenter.notifyDataRequestSuccess(mSoapAction, resultString);
			} catch (Exception e) {
				
			}
			return null;
		}
		
	}
	
	
	
	private void jsonStreetData(String data){
		JSONArray dataArray;
		try {
			dataArray = new JSONArray(data);
			String[] streetList = new String[dataArray.length()];
			for (int item = 0; item < dataArray.length(); item++){
				JSONObject districtObject = dataArray.optJSONObject(item);
				streetList[item] = districtObject.optString("LSName");
				mStreetID[item] = districtObject.optString("LDID");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getSoapName(String action){
		if (action == null || action.equals("")){
			return null;
		}
		int index = action.lastIndexOf("/");
		return action.substring(index+1);
	}
	
	private String startRequestData(String url, String action, SoapObject object ){
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.bodyOut = object;
			envelope.dotNet= true;
			envelope.setOutputSoapObject(object);
			HttpTransportSE transport = new HttpTransportSE(url,30000);
			transport.call(action, envelope);
			SoapObject valueObject = null;
			if(envelope.getResponse()!=null){
				valueObject = (SoapObject)envelope.bodyIn;				
			}
			return valueObject.getProperty(0).toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	
	public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity != null) {  
            NetworkInfo info = connectivity.getActiveNetworkInfo();  
            if (info != null && info.isConnected())   
            {  
                // ��ǰ���������ӵ�  
                if (info.getState() == NetworkInfo.State.CONNECTED)   
                {  
                    // ��ǰ�����ӵ��������  
                    return true;  
                }  
            }  
        }  
        return false;  
    }  
	
	
	
}
