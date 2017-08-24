package tenant.guardts.house.wxpay;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


/**
 * 璐粯閫歨ttp鎴栬�https缃戠粶閫氫俊瀹㈡埛绔�br/>
 * ========================================================================<br/>
 * api璇存槑锛�br/>
 * setReqContent($reqContent),璁剧疆璇锋眰鍐呭锛屾棤璁簆ost鍜実et锛岄兘鐢╣et鏂瑰紡鎻愪緵<br/>
 * getResContent(), 鑾峰彇搴旂瓟鍐呭<br/>
 * setMethod(method),璁剧疆璇锋眰鏂规硶,post鎴栬�get<br/>
 * getErrInfo(),鑾峰彇閿欒淇℃伅<br/>
 * setCertInfo(certFile, certPasswd),璁剧疆璇佷功锛屽弻鍚慼ttps鏃堕渶瑕佷娇鐢�br/>
 * setCaInfo(caFile), 璁剧疆CA锛屾牸寮忔湭pem锛屼笉璁剧疆鍒欎笉妫�煡<br/>
 * setTimeOut(timeOut)锛�璁剧疆瓒呮椂鏃堕棿锛屽崟浣嶇<br/>
 * getResponseCode(), 鍙栬繑鍥炵殑http鐘舵�鐮�br/>
 * call(),鐪熸璋冪敤鎺ュ彛<br/>
 * getCharset()/setCharset(),瀛楃闆嗙紪鐮�br/>
 * 
 * ========================================================================<br/>
 *
 */
public class TenpayHttpClient {
	
	private static final String USER_AGENT_VALUE = 
		"Mozilla/4.0 (compatible; MSIE 6.0; Windows XP)";
	
	private static final String JKS_CA_FILENAME = 
		"tenpay_cacert.jks";
	
	private static final String JKS_CA_ALIAS = "tenpay";
	
	private static final String JKS_CA_PASSWORD = "";
	
	/** ca璇佷功鏂囦欢 */
	private File caFile;
	
	/** 璇佷功鏂囦欢 */
	private File certFile;
	
	/** 璇佷功瀵嗙爜 */
	private String certPasswd;
	
	/** 璇锋眰鍐呭锛屾棤璁簆ost鍜実et锛岄兘鐢╣et鏂瑰紡鎻愪緵 */
	private String reqContent;
	
	/** 搴旂瓟鍐呭 */
	private String resContent;
	
	/** 璇锋眰鏂规硶 */
	private String method;
	
	/** 閿欒淇℃伅 */
	private String errInfo;
	
	/** 瓒呮椂鏃堕棿,浠ョ涓哄崟浣�*/
	private int timeOut;
	
	/** http搴旂瓟缂栫爜 */
	private int responseCode;
	
	/** 瀛楃缂栫爜 */
	private String charset;
	
	private InputStream inputStream;
	
	public TenpayHttpClient() {
		this.caFile = null;
		this.certFile = null;
		this.certPasswd = "";
		
		this.reqContent = "";
		this.resContent = "";
		this.method = "POST";
		this.errInfo = "";
		this.timeOut = 30;//30绉�
		
		this.responseCode = 0;
		this.charset = "GBK";
		
		this.inputStream = null;
	}

	/**
	 * 璁剧疆璇佷功淇℃伅
	 * @param certFile 璇佷功鏂囦欢
	 * @param certPasswd 璇佷功瀵嗙爜
	 */
	public void setCertInfo(File certFile, String certPasswd) {
		this.certFile = certFile;
		this.certPasswd = certPasswd;
	}
	
	/**
	 * 璁剧疆ca
	 * @param caFile
	 */
	public void setCaInfo(File caFile) {
		this.caFile = caFile;
	}
	
	/**
	 * 璁剧疆璇锋眰鍐呭
	 * @param reqContent 琛ㄦ眰鍐呭
	 */
	public void setReqContent(String reqContent) {
		this.reqContent = reqContent;
	}
	
	/**
	 * 鑾峰彇缁撴灉鍐呭
	 * @return String
	 * @throws IOException 
	 */
	public String getResContent() {
		try {
			this.doResponse();
		} catch (IOException e) {
			this.errInfo = e.getMessage();
			//return "";
		}
		
		return this.resContent;
	}
	
	/**
	 * 璁剧疆璇锋眰鏂规硶post鎴栬�get
	 * @param method 璇锋眰鏂规硶post/get
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * 鑾峰彇閿欒淇℃伅
	 * @return String
	 */
	public String getErrInfo() {
		return this.errInfo;
	}
	
	/**
	 * 璁剧疆瓒呮椂鏃堕棿,浠ョ涓哄崟浣�
	 * @param timeOut 瓒呮椂鏃堕棿,浠ョ涓哄崟浣�
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	/**
	 * 鑾峰彇http鐘舵�鐮�
	 * @return int
	 */
	public int getResponseCode() {
		return this.responseCode;
	}
	
	/**
	 * 鎵цhttp璋冪敤銆倀rue:鎴愬姛 false:澶辫触
	 * @return boolean
	 */
	public boolean call() {
		
		boolean isRet = false;
		
		//http
		if(null == this.caFile && null == this.certFile) {
			try {
				this.callHttp();
				isRet = true;
			} catch (IOException e) {
				this.errInfo = e.getMessage();
			}
			return isRet;
		}
		
		//https
		try {
			this.callHttps();
			isRet = true;
		} catch (UnrecoverableKeyException e) {
			this.errInfo = e.getMessage();
		} catch (KeyManagementException e) {
			this.errInfo = e.getMessage();
		} catch (CertificateException e) {
			this.errInfo = e.getMessage();
		} catch (KeyStoreException e) {
			this.errInfo = e.getMessage();
		} catch (NoSuchAlgorithmException e) {
			this.errInfo = e.getMessage();
		} catch (IOException e) {
			this.errInfo = e.getMessage();
		}
		
		return isRet;
		
	}
	
	protected void callHttp() throws IOException {
		
		if("POST".equals(this.method.toUpperCase())) {
			String url = HttpClientUtil.getURL(this.reqContent);
			String queryString = HttpClientUtil.getQueryString(this.reqContent);
			byte[] postData = queryString.getBytes(this.charset);
			this.httpPostMethod(url, postData);
			
			return ;
		}
		
		this.httpGetMethod(this.reqContent);
		
	} 
	
	protected void callHttps() throws IOException, CertificateException,
			KeyStoreException, NoSuchAlgorithmException,
			UnrecoverableKeyException, KeyManagementException {

		// ca鐩綍
		String caPath = this.caFile.getParent();

		File jksCAFile = new File(caPath + "/"
				+ TenpayHttpClient.JKS_CA_FILENAME);
		if (!jksCAFile.isFile()) {
			X509Certificate cert = (X509Certificate) HttpClientUtil
					.getCertificate(this.caFile);

			FileOutputStream out = new FileOutputStream(jksCAFile);

			// store jks file
			HttpClientUtil.storeCACert(cert, TenpayHttpClient.JKS_CA_ALIAS,
					TenpayHttpClient.JKS_CA_PASSWORD, out);

			out.close();

		}

		FileInputStream trustStream = new FileInputStream(jksCAFile);
		FileInputStream keyStream = new FileInputStream(this.certFile);

		SSLContext sslContext = HttpClientUtil.getSSLContext(trustStream,
				TenpayHttpClient.JKS_CA_PASSWORD, keyStream, this.certPasswd);
		
		//鍏抽棴娴�
		keyStream.close();
		trustStream.close();
		
		if("POST".equals(this.method.toUpperCase())) {
			String url = HttpClientUtil.getURL(this.reqContent);
			String queryString = HttpClientUtil.getQueryString(this.reqContent);
			byte[] postData = queryString.getBytes(this.charset);
			
			this.httpsPostMethod(url, postData, sslContext);
			
			return ;
		}
		
		this.httpsGetMethod(this.reqContent, sslContext);

	}
	
	public boolean callHttpPost(String url, String postdata) {
		boolean flag = false;
		byte[] postData;
		try {
			postData = postdata.getBytes(this.charset);
			this.httpPostMethod(url, postData);
			flag = true;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 浠ttp post鏂瑰紡閫氫俊
	 * @param url
	 * @param postData
	 * @throws IOException
	 */
	protected void httpPostMethod(String url, byte[] postData)
			throws IOException {

		HttpURLConnection conn = HttpClientUtil.getHttpURLConnection(url);

		this.doPost(conn, postData);
	}
	
	/**
	 * 浠ttp get鏂瑰紡閫氫俊
	 * 
	 * @param url
	 * @throws IOException
	 */
	protected void httpGetMethod(String url) throws IOException {
		
		HttpURLConnection httpConnection =
			HttpClientUtil.getHttpURLConnection(url);
		
		this.setHttpRequest(httpConnection);
		
		httpConnection.setRequestMethod("GET");
		
		this.responseCode = httpConnection.getResponseCode();
		
		this.inputStream = httpConnection.getInputStream();
		
	}
	
	/**
	 * 浠ttps get鏂瑰紡閫氫俊
	 * @param url
	 * @param sslContext
	 * @throws IOException
	 */
	protected void httpsGetMethod(String url, SSLContext sslContext)
			throws IOException {

		SSLSocketFactory sf = sslContext.getSocketFactory();

		HttpsURLConnection conn = HttpClientUtil.getHttpsURLConnection(url);

		conn.setSSLSocketFactory(sf);

		this.doGet(conn);

	}
	
	protected void httpsPostMethod(String url, byte[] postData,
			SSLContext sslContext) throws IOException {

		SSLSocketFactory sf = sslContext.getSocketFactory();

		HttpsURLConnection conn = HttpClientUtil.getHttpsURLConnection(url);

		conn.setSSLSocketFactory(sf);

		this.doPost(conn, postData);

	}
	
	/**
	 * 璁剧疆http璇锋眰榛樿灞炴�
	 * @param httpConnection
	 */
	protected void setHttpRequest(HttpURLConnection httpConnection) {
		
		//璁剧疆杩炴帴瓒呮椂鏃堕棿
		httpConnection.setConnectTimeout(this.timeOut * 1000);
		
		//User-Agent
		httpConnection.setRequestProperty("User-Agent", 
				TenpayHttpClient.USER_AGENT_VALUE);
		
		//涓嶄娇鐢ㄧ紦瀛�
		httpConnection.setUseCaches(false);
		
		//鍏佽杈撳叆杈撳嚭
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(true);
		
	}
	
	/**
	 * 澶勭悊搴旂瓟
	 * @throws IOException
	 */
	protected void doResponse() throws IOException {
		
		if(null == this.inputStream) {
			return;
		}

		//鑾峰彇搴旂瓟鍐呭
		this.resContent=HttpClientUtil.InputStreamTOString(this.inputStream,this.charset); 

		//鍏抽棴杈撳叆娴�
		this.inputStream.close();
		
	}
	
	/**
	 * post鏂瑰紡澶勭悊
	 * @param conn
	 * @param postData
	 * @throws IOException
	 */
	protected void doPost(HttpURLConnection conn, byte[] postData)
			throws IOException {

		// 浠ost鏂瑰紡閫氫俊
		conn.setRequestMethod("POST");

		// 璁剧疆璇锋眰榛樿灞炴�
		this.setHttpRequest(conn);

		// Content-Type
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		BufferedOutputStream out = new BufferedOutputStream(conn
				.getOutputStream());

		final int len = 1024; // 1KB
		HttpClientUtil.doOutput(out, postData, len);

		// 鍏抽棴娴�
		out.close();

		// 鑾峰彇鍝嶅簲杩斿洖鐘舵�鐮�
		this.responseCode = conn.getResponseCode();

		// 鑾峰彇搴旂瓟杈撳叆娴�
		this.inputStream = conn.getInputStream();

	}
	
	/**
	 * get鏂瑰紡澶勭悊
	 * @param conn
	 * @throws IOException
	 */
	protected void doGet(HttpURLConnection conn) throws IOException {
		
		//浠ET鏂瑰紡閫氫俊
		conn.setRequestMethod("GET");
		
		//璁剧疆璇锋眰榛樿灞炴�
		this.setHttpRequest(conn);
		
		//鑾峰彇鍝嶅簲杩斿洖鐘舵�鐮�
		this.responseCode = conn.getResponseCode();
		
		//鑾峰彇搴旂瓟杈撳叆娴�
		this.inputStream = conn.getInputStream();
	}

	
}
