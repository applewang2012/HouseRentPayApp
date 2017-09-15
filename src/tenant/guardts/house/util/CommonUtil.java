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

/**
 * @author shenxiaolei
 *
 */
public class CommonUtil {

    /**
     * 记录播放位置
     */
    public static int playPosition=-1;
    
    private static  Canvas canvas;
    
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
    public static String mUserHost = "http://qxw2332340157.my3w.com/";
    public static String mUserArea;
    public static double mCurrentLati, mCurrentLongi;
    public static int mScanCodeRequestCode = 1;
    public static final int SELECT_CITY_REQEUST_CODE = 2;
    public static String mPayHouseOrderId = null;
    //weixin
    public static final String APP_ID = "wxae25cb3fefdc75ae";
    public static final String WX_PARTNER_ID = "1481965242";
    public static final String SIGN_KEY = "413ac6a2651c14455ec7a0cd498ab6d8"; //api key和商户签名为同一个
    public static String XINGE_TOKEN = null;
    
    public static final String URL_ZHENGFU_FUWU = "http://www.tjgaj.gov.cn/site/default.aspx";
    public static final String URL_WEIQUAN_CHANGSHI = "http://ms.enorth.com.cn/xfwq/";
    public static final String URL_YILIAO_BAOXIAN = "http://m.haodf.com/touch/hospital/DE4roiYGYZwmj5uuYe-Bqregr.htm?from=alading";
    public static final String URL_JIAOTONG_CHUXING = "http://tianjin.gongjiao.com/";
    public static final String URL_ZHOUBIAN_SHANGQU = "http://m.dianping.com/tianjin?from=city_hot";
    public static final String URL_MORE_CONTENT = "http://4g.enorth.com.cn/";
    
    public static  String ORDER_TIME = null;
    public static  String ORDER_NO = null;
    public static  String ORDER_MONKEY = null;
    public static String ORDER_PREPAY_ID = null;
    public static String OWNER_IDCARD = null;
    
    public static void setCanvas(Canvas canvas) {
        CommonUtil.canvas = canvas;
    }
    
    public static String getDefaultDownloadPath(String downloadUrl){
    	String path = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 存在获取外部文件路径
            File root = Environment.getExternalStorageDirectory();
            File base = new File(root.getPath() + "/"+GURADTS_DOWNLOAD_DIR);
            if (!base.exists()){
            	base.mkdir();
            }
        	
        	File[] files = base.listFiles();
        	for (int i = 0; i < files.length; i++) {
        			String filename = files[i].getName();
        			if (filename != null && downloadUrl != null){
        				if (downloadUrl.endsWith(filename)){
        					path = base.getPath()+File.separator+filename;
        					break;
        				}
        			}
        	}
        	if (path == null){
        		path = base.getPath();
        	}
        } else {
            // 不存在获取内部存
            return null;
        }
        Log.i("mingguo", "common util get default download path  "+path);
       return path;
    }
    
    public static boolean deleteInstalledApkFile(){
    	 File root = Environment.getExternalStorageDirectory();
         File base = new File(root.getPath() + "/"+GURADTS_DOWNLOAD_DIR);
         if (!base.exists()){
         	base.mkdir();
         }
     	
     	File[] files = base.listFiles();
     	for (int i = 0; i < files.length; i++) {
			String filename = files[i].getName();
			if (filename.endsWith(".apk")){
				return new File(base.getPath()+File.separator+filename).delete();
			}
     	}
     	return false;
    }
    
    
    /* 安装apk */    
    public static void installApk(Context context, String fileName) {    
        Intent intent = new Intent();    
        intent.setAction(Intent.ACTION_VIEW);    
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
        intent.setDataAndType(Uri.parse("file://" + fileName),"application/vnd.android.package-archive");    
        context.startActivity(intent);    
    }    
        
    /* 卸载apk */    
    public static void uninstallApk(Context context, String packageName) {    
        Uri uri = Uri.parse("package:" + packageName);    
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);    
        context.startActivity(intent);    
    }  
    
    
    public static String getSoapName(String action){
		if (action == null || action.equals("")){
			return null;
		}
		int index = action.lastIndexOf("/");
		return action.substring(index+1);
	}
    
    private static double dEARTH_RADIUS = 6378.137;  // 地球半径
    private static double Rad(double d)
    {
        return (d * Math.PI / 180.0);
    }
    /// <summary>
    /// 计算两个坐标的距离
    /// </summary>
    /// <param >第一个坐标纬度</param>
    /// <param >第一个坐标经度</param>
    /// <param >第二个坐标纬度</param>
    /// <param >第二个坐标经度</param>
    /// <returns>两个坐标间的距离</returns>
    public static double GetDistance(double dLngPre, double dLatPre, double dLngNext, double dLatNext){
    	Log.e("mingguo", "lng "+dLngPre+"  "+ dLatPre+"  "+dLngNext+"  "+dLatNext);
        double dRadLatPre = Rad(dLatPre);
        double dRadLatNext = Rad(dLatNext);
        double a = dRadLatPre - dRadLatNext;
        double b = Rad(dLngPre) - Rad(dLngNext);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(dRadLatPre) * Math.cos(dRadLatNext) * Math.pow(Math.sin(b / 2), 2)));
        s = s * dEARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        Log.w("mingguo", "ss "+s);
        return s;
	}
    
    public static GeoPoint getGeoPointBystr(Context context, String str) {
		  GeoPoint gpGeoPoint = null;
		  if (str!=null) {
		    Geocoder gc = new Geocoder(context,Locale.CHINA);
		      List<Address> addressList = null;
		    try {
		    	addressList = gc.getFromLocationName(str, 1);
		        if (!addressList.isEmpty()) {
		      Address address_temp = addressList.get(0);
		      //计算经纬度
		      double Latitude=address_temp.getLatitude();
		      double Longitude=address_temp.getLongitude();
		      Log.i("mingguo", "经度："+Latitude);
		      Log.i("mingguo", "纬度："+Longitude);
		      //生产GeoPoint
		      gpGeoPoint = new GeoPoint((int)Latitude, (int)Longitude);
		    }
		    } catch (Exception e) {
		    	Log.e("mingguo", "get getPoint By str  "+e);
		      e.printStackTrace();
		    }
		  }
		  return gpGeoPoint;
		  
	}
    
    public static LatLng getLatLngBystr(Context context, String str) {
		  GeoPoint gpGeoPoint = null;
		  double Latitude = 0, Longitude = 0;
		  if (str!=null) {
		    Geocoder gc = new Geocoder(context,Locale.CHINA);
		      List<Address> addressList = null;
		    try {
		    	addressList = gc.getFromLocationName(str, 1);
		        if (!addressList.isEmpty()) {
		        	if (addressList.size() > 0){
		        		Address address_temp = addressList.get(0);
					      //计算经纬度
					       Latitude = address_temp.getLatitude();
					       Longitude = address_temp.getLongitude();
					      Log.i("mingguo", "经度："+Latitude);
					      Log.i("mingguo", "纬度："+Longitude);
					      //生产GeoPoint
					    }
		        	}
		    } catch (Exception e) {
		    	Log.e("mingguo", "get getPoint By str  "+e);
		      e.printStackTrace();
		    }
		  }
		  return new LatLng(Latitude, Longitude);
		  
	}
    
    
}
