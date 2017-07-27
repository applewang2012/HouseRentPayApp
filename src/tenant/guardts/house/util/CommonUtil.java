package tenant.guardts.house.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
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
    
    public static final String DOWLOAD_URL = "http://acj2.pc6.com/pc6_soure/2017-6/com.dybag_25.apk";
    
    public static final String NAMESPACE = "http://tempuri.org/";
    
    public static String mRegisterRealName = null;
    public static String mRegisterIdcard = null;
    public static String mUserLoginName = null;
    public static String mUserHost, mUserArea;
    public static double mCurrentLati, mCurrentLongi;
    
    public static Canvas getCanvas() {
        return canvas;
    }

    public static void setCanvas(Canvas canvas) {
        CommonUtil.canvas = canvas;
    }
    
    public static String getDefaultDownloadPath(String downloadUrl){
    	String path = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 存在获取外部文件路径
            File root = Environment.getExternalStorageDirectory();
            File base = new File(root.getPath() + "/download");
            //base.mkdir();
        	if (!base.isDirectory() && !base.mkdir()) {
        		return null;
        	}else{
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
        	}
        } else {
            // 不存在获取内部存
            return null;
        }
       return path;
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
    
    
}
