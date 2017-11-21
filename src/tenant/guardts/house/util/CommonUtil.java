package tenant.guardts.house.util;

import java.io.File;
import java.util.List;
import java.util.Locale;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
/**
 * @author shenxiaolei
 *
 */
public class CommonUtil {
    /**
     * 记录播放位置
     */
    public static int playPosition=-1;
    
    public static String DOWLOAD_URL = null; //"http://acj2.pc6.com/pc6_soure/2017-6/com.dybag_25.apk";
    public static String GURADTS_DOWNLOAD_DIR = "guardtsdownload";
    public static final String NAMESPACE = "http://tempuri.org/";
    
    public static final String UPDATE_VERSION_HOST = "http://www.guardts.com/";
    
    public static String mRegisterRealName = null;
    public static String mRegisterIdcard = null;
    public static String mUserLoginName = null;
    public static String mUserWallet = null;
    public static String mBankName = null;
    public static String mCardNo = null;
    //public static String mUserHost = "http://219.150.56.178:8082/";
    public static String mUserHost = ""; //"http://qxw2332340157.my3w.com/Services.asmx/";
    public static String mUserArea;
    public static double mCurrentLati, mCurrentLongi;
    public static int mScanCodeRequestCode = 1;
    public static int mLockNumberRequestCode = 5;
    public static int mIndentifyUserRequestCode = 10;
    public static final int SELECT_CITY_REQEUST_CODE = 2;
    public static final int MAP_SUGGEST_REQEUST_CODE = 3;
    public static String mPayHouseOrderId = null;
    //weixin
    public static final String APP_ID = "wxae25cb3fefdc75ae";
    public static final String WX_PARTNER_ID = "1481965242";
    public static final String SIGN_KEY = "413ac6a2651c14455ec7a0cd498ab6d8"; //api key和商户签名为同一个
    public static String XINGE_TOKEN = null;
    public static final String APP_SECRET = "989e87b179368c9fbc0bff841b75941e";
    
    public static final String URL_ZHENGFU_FUWU = "http://www.tjgaj.gov.cn/site/default.aspx";
    public static final String URL_WEIQUAN_CHANGSHI = "http://ms.enorth.com.cn/xfwq/";
    public static final String URL_YILIAO_BAOXIAN = "http://m.haodf.com/touch/hospital/DE4roiYGYZwmj5uuYe-Bqregr.htm?from=alading";
    public static final String URL_COMMON_PHONE = "http://www.jihaoba.com/changyongdianhua/tianjin.htm";
    public static final String URL_JIAOTONG_CHUXING = "http://tianjin.gongjiao.com/";
    public static final String URL_ZHOUBIAN_SHANGQU = "http://m.dianping.com/tianjin?from=city_hot";
    public static final String URL_MORE_CONTENT = "http://4g.enorth.com.cn/";
    public static final String HEXI_POWER_PACKAGE_NAME = "com.esint.pahx.messenger";
    
    public static boolean version_pay_test = false; //测试版本支付一分钱改成true
    public static boolean verify_code_test = false; //测试版本验证码改成true
    public static boolean mIsForceIdentifyUser = false;
    public static boolean mIsCancelRentIdentifyTest = false; //测试版本取消实名该成true
    
    public static  String ORDER_TIME = null;
    public static  String ORDER_NO = null;
    public static  String ORDER_MONKEY = null;
    public static String ORDER_PREPAY_ID = null;
    public static String OWNER_IDCARD = null;
    public static final long TIME_STAMP_10_MINUTES = 600000L;
    public static String ORDER_STATUS_NEED_EVALUATION="3";//待评价
    public static String ORDER_STATUS_SUBMITT = "0";    //待确认
    public static String ORDER_STATUS_NEED_PAY = "1";   //待支付
    public static String ORDER_STATUS_HAS_PAYED = "2";  //已支付
    public static String ORDER_STATUS_REJECTED = "9";  //已拒绝
    public static String ORDER_STATUS_CANCELED = "8"; //已取消
    public static String ORDER_STATUS_NEED_CHECKOUT = "6"; //退房待确认
    public static String ORDER_STATUS_EXPIRED = "7"; //已过期
    public static String ORDER_STATUS_CHECKOUTED = "5"; //已退房
    public static String ORDER_STATUS_COMPLETE = "11"; //已完成
    
	public static String getDefaultDownloadPath(String downloadUrl,Context context) {
		String path = null;

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 存在获取外部文件路径
			File root = Environment.getExternalStorageDirectory();
			File base = new File(root.getPath() + "/" + GURADTS_DOWNLOAD_DIR);
			if (!base.exists()) {
				base.mkdir();
			}

			// File[] files = base.listFiles();
			// for (int i = 0; i < files.length; i++) {
			// String filename = files[i].getName();
			// if (filename != null && downloadUrl != null){
			// if (downloadUrl.endsWith(filename)){
			// path = base.getPath()+File.separator+filename;
			// break;
			// }
			// }
			// }
			if (path == null) {
				path = base.getPath();
			}
		} else {
			// 不存在获取内部存
			Toast.makeText(context, "外部存储卡未挂载", Toast.LENGTH_SHORT).show();
			return null;
		}
		LogUtil.w("mingguo", "common util get default download path  " + path);
		return path;
	}

	public static String getDownloadFilename(String url) {
		if (url != null) {
			int lastIndex = url.lastIndexOf("/");
			if (lastIndex > 0) {
				return url.substring(lastIndex);
			}
		}
		return "defaultName.apk";
	}

	public static String getDownloadPathWithName(String downloadUrl,Context context) {
		return (getDefaultDownloadPath(downloadUrl,context) + "/" + getDownloadFilename(downloadUrl));
	}

	public static boolean deleteInstalledApkFile(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File root = Environment.getExternalStorageDirectory();
			File base = new File(root.getPath() + "/" + GURADTS_DOWNLOAD_DIR);
			if (!base.exists()) {
				base.mkdir();
			}

			File[] files = base.listFiles();
			for (int i = 0; i < files.length; i++) {
				String filename = files[i].getName();
				if (filename.endsWith(".apk") || filename.endsWith(".APK")) {
					return new File(base.getPath() + File.separator + filename).delete();
				}
			}
		}else{
			Toast.makeText(context, "外部存储卡未挂载", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	/* 安装apk */
	public static void installApk(Context context, String fileName) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + fileName), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/* 卸载apk */
	public static void uninstallApk(Context context, String packageName) {
		Uri uri = Uri.parse("package:" + packageName);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(intent);
	}

	public static String getSoapName(String action) {
		if (action == null || action.equals("")) {
			return null;
		}
		int index = action.lastIndexOf("/");
		return action.substring(index + 1);
	}

	private static double dEARTH_RADIUS = 6378.137; // 地球半径

	private static double Rad(double d) {
		return (d * Math.PI / 180.0);
	}

	// / <summary>
	// / 计算两个坐标的距离
	// / </summary>
	// / <param >第一个坐标纬度</param>
	// / <param >第一个坐标经度</param>
	// / <param >第二个坐标纬度</param>
	// / <param >第二个坐标经度</param>
	// / <returns>两个坐标间的距离</returns>
	public static double GetDistance(double dLngPre, double dLatPre, double dLngNext, double dLatNext) {
		LogUtil.e("mingguo", "lng " + dLngPre + "  " + dLatPre + "  " + dLngNext + "  " + dLatNext);
		double dRadLatPre = Rad(dLatPre);
		double dRadLatNext = Rad(dLatNext);
		double a = dRadLatPre - dRadLatNext;
		double b = Rad(dLngPre) - Rad(dLngNext);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(dRadLatPre) * Math.cos(dRadLatNext)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * dEARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		LogUtil.w("mingguo", "ss " + s);
		return s;
	}

	public static GeoPoint getGeoPointBystr(Context context, String str) {
		GeoPoint gpGeoPoint = null;
		if (str != null) {
			Geocoder gc = new Geocoder(context, Locale.CHINA);
			List<Address> addressList = null;
			try {
				addressList = gc.getFromLocationName(str, 1);
				if (!addressList.isEmpty()) {
					Address address_temp = addressList.get(0);
					// 计算经纬度
					double Latitude = address_temp.getLatitude();
					double Longitude = address_temp.getLongitude();
					LogUtil.w("mingguo", "经度：" + Latitude);
					LogUtil.w("mingguo", "纬度：" + Longitude);
					// 生产GeoPoint
					gpGeoPoint = new GeoPoint((int) Latitude, (int) Longitude);
				}
			} catch (Exception e) {
				LogUtil.e("mingguo", "get getPoint By str  " + e);
				e.printStackTrace();
			}
		}
		return gpGeoPoint;

	}

	public static LatLng getLatLngBystr(Context context, String str) {
		GeoPoint gpGeoPoint = null;
		double Latitude = 0, Longitude = 0;
		if (str != null) {
			Geocoder gc = new Geocoder(context, Locale.CHINA);
			List<Address> addressList = null;
			try {
				addressList = gc.getFromLocationName(str, 1);
				if (!addressList.isEmpty()) {
					if (addressList.size() > 0) {
						Address address_temp = addressList.get(0);
						// 计算经纬度
						Latitude = address_temp.getLatitude();
						Longitude = address_temp.getLongitude();
						LogUtil.w("mingguo", "经度：" + Latitude);
						LogUtil.w("mingguo", "纬度：" + Longitude);
						// 生产GeoPoint
					}
				}
			} catch (Exception e) {
				LogUtil.e("mingguo", "get getPoint By str  " + e);
				e.printStackTrace();
			}
		}
		return new LatLng(Latitude, Longitude);

	}

}
