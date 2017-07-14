package tenant.guardts.house.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.zxing.common.BitMatrix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class ScreenShotUtil {  
	
	public static int mCount = 0;
	public static int mImage1FeatureNum;
	public static int mImage2FeatureNum;
    
    public static void shoot(Activity a, File filePath) {  
        if (filePath == null) {  
            return;  
        }  
        if (!filePath.getParentFile().exists()) {  
            filePath.getParentFile().mkdirs();  
        }  
        FileOutputStream fos = null;  
        try {  
            fos = new FileOutputStream(filePath);  
            if (null != fos) {  
                takeScreenShot(a).compress(Bitmap.CompressFormat.PNG, 100, fos);  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } finally {  
            if (fos != null) {  
                try {  
                    fos.flush();  
                    fos.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
  
	@SuppressLint("NewApi")
	private static Bitmap takeScreenShot(Activity activity) {  
        View view = activity.getWindow().getDecorView();  
        view.setDrawingCacheEnabled(true);  
        view.buildDrawingCache();  
        Bitmap bitmap = view.getDrawingCache();  
        Rect frame = new Rect();  
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
        int statusBarHeight = frame.top;  
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();  
        int height = activity.getWindowManager().getDefaultDisplay()  
                .getHeight();  
        // ȥ��������  
        Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width,  
                height - statusBarHeight);  
        view.destroyDrawingCache();  
        return b;  
    }  
	
	public static Bitmap getScreenshotBitmap(Activity activity){
		return getCompressBitmapImage(activity);
	}
	
	public static String generateScreenshot(Activity activity){
		return GetandSaveCurrentImage(activity, createScreenshotDirectory(activity));
	}
	//������Ƶ��ͼ
	public static String generateScreenshot(Activity activity, int position, String uri){
		return GetandSaveCurrentImage(activity, createScreenshotDirectory(activity), uri, position);
	}
	
	public static String getScreenshotDirectory(Activity context){
		return context.getFilesDir().getPath()+"/screenshot";
	}
	public static void testScreenshot(Context activity) throws IOException{
		 ScreenCapture(activity, createScreenshotDirectory(activity));
	}
	public static String createScreenshotDirectory(Context context) {
		File file = new File(Environment.getExternalStorageDirectory()+"/guardtsimage");
		if (!file.exists()){
			file.mkdir();
		}
		String filename = file.getPath()+"/"+"guardtsuser"+".jpg";
		Log.i("mingguo", "generate  file name   "+filename);
		return filename;
	}
	
	public static void clearScreenShotImage(String path){
    	File mfile = new File(path);
    	 File[] files = mfile.listFiles();

    	 // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
    	 for (int i = 0; i < files.length; i++) {
    		 	File file = files[i];
    		 	if (file != null && file.exists()){
    		 		if (file.delete()){
    		 			Log.i("mingguo", "delete file success "+file.getPath());
    		 		}
    		 	}
    	  }
    }
	
	public static String testGetandSaveCurrentImage(Activity activity, String filePath){
		Bitmap bitmap = myShot(activity);
		try {  
//          File path = new File(SavePath);  
//          //�ļ�  
//          String filepath = SavePath + "/Screen_1.png";  
      	if (!new File(filePath).getParentFile().exists()) {  
      		new File(filePath).getParentFile().mkdirs();  
          } 
          File file = new File(filePath);  
          if (!file.exists()) {  
              file.createNewFile();  
          }  

          FileOutputStream fos = null;  
          fos = new FileOutputStream(file);  
          if (null != fos) {  
        	  bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);  
              fos.flush();  
              fos.close();    

              return filePath;
          }  

      } catch (Exception e) {  
          e.printStackTrace();  
      }  
      return null;
  }
	
	public static Bitmap getCompressBitmapImage(Activity activity){
		WindowManager windowManager = activity.getWindowManager();  
        Display display = windowManager.getDefaultDisplay();  
        int w = display.getWidth();  
        int h = display.getHeight();  

        Bitmap bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888 );      

        //2.��ȡ��Ļ  
        View decorview = activity.getWindow().getDecorView();   
        decorview.setDrawingCacheEnabled(true);   
        bmp = decorview.getDrawingCache();   
		return BMapUtil.compressImage(bmp);
	}
	
	public static String GetandSaveCurrentImage(Activity activity, String filePath)  {  
        //1.����Bitmap  
        WindowManager windowManager = activity.getWindowManager();  
        Display display = windowManager.getDefaultDisplay();  
        int w = display.getWidth();  
        int h = display.getHeight();  

        Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888 );      

        //2.��ȡ��Ļ  
        View decorview = activity.getWindow().getDecorView();   
        decorview.setDrawingCacheEnabled(true);   
        Bmp = decorview.getDrawingCache();   

        //String SavePath = getSDCardPath()+"/AndyDemo/ScreenImage";

        //3.����Bitmap   
        try {  
//            File path = new File(SavePath);  
//            //�ļ�  
//            String filepath = SavePath + "/Screen_1.png";  
        	if (!new File(filePath).getParentFile().exists()) {  
        		new File(filePath).getParentFile().mkdirs();  
            } 
            File file = new File(filePath);  
            if (!file.exists()) {  
                file.createNewFile();  
            }  

            FileOutputStream fos = null;  
            fos = new FileOutputStream(file);  
            if (null != fos) {  
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);  
                fos.flush();  
                fos.close();    

                return filePath;
            }  

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;
    }
	
	public static int getCompareImageResult(){
		return 0;
	}
	
	
	public static String GetandSaveCurrentImage(Activity activity, String filePath, String uri,int position)  {  
        //1.����Bitmap  
        WindowManager windowManager = activity.getWindowManager();  
        Display display = windowManager.getDefaultDisplay();  
        int w = display.getWidth();  
        int h = display.getHeight();  

        Bitmap Bmp = createVideoThumbnail(uri, 640, 360, position);
        
       // Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888 );      

        //2.��ȡ��Ļ  
//        View decorview = activity.getWindow().getDecorView();   
//        decorview.setDrawingCacheEnabled(true);   
//        Bmp = decorview.getDrawingCache();   

        //String SavePath = getSDCardPath()+"/AndyDemo/ScreenImage";

        //3.����Bitmap   
        try {  
//            File path = new File(SavePath);  
//            //�ļ�  
//            String filepath = SavePath + "/Screen_1.png";  
        	if (!new File(filePath).getParentFile().exists()) {  
        		new File(filePath).getParentFile().mkdirs();  
            } 
            File file = new File(filePath);  
            if (!file.exists()) {  
                file.createNewFile();  
            }  

            FileOutputStream fos = null;  
            fos = new FileOutputStream(file);  
            if (null != fos) {  
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);  
                fos.flush();  
                fos.close();    

                return filePath;
            }  

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;
    }  
	
	
	@SuppressLint("NewApi")
	private static Bitmap createVideoThumbnail(String url, int width, int height, int position) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            Log.w("mingguo", "video thumbnail  position   "+position);
            Bitmap originBitmap = retriever.getFrameAtTime(position*1000); //΢��
            bitmap = Bitmap.createScaledBitmap(originBitmap, width, height, true);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }
	
	public static Bitmap myShot(Activity activity) {
//		ActivityManager manager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE) ;
//		RunningTaskInfo info = manager.getRunningTasks(1).get(0);
//		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
		// 获取windows中最顶层的view
		View view = activity.getWindow().getDecorView();
		view.buildDrawingCache();
		// 获取状态栏高度
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		int statusBarHeights = rect.top;
		Display display = activity.getWindowManager().getDefaultDisplay();
		// 获取屏幕宽和高
		int widths = display.getWidth();
		int heights = display.getHeight();
		// 允许当前窗口保存缓存信息
		view.setDrawingCacheEnabled(true);
		// 去掉状态栏
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
		statusBarHeights, widths, heights - statusBarHeights);
		// 销毁缓存信息
		view.destroyDrawingCache();
		return bmp;
	}
  
	
	public static void ScreenCapture(Context context, String filePath) throws IOException {
		ActivityManager mActivityManager;  
		mActivityManager = (ActivityManager) context.getSystemService(
		        Context.ACTIVITY_SERVICE);  
		ComponentName topActivity = mActivityManager.
		        getRunningTasks(1).get(0).topActivity;  
		String packageName = topActivity.getPackageName();
		
		        List<RunningTaskInfo> runningTaskInfos = mActivityManager.getRunningTasks(1) ;


		        if(runningTaskInfos != null){
		        	Log.i("mingguo"  ,"package name  "+ packageName+"  activity  "+topActivity.toString()) ;
		        }

		        	
		DisplayMetrics metrics =new DisplayMetrics();
		WindowManager WM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		
		
		Display display = WM.getDefaultDisplay();
		display.getMetrics(metrics);
		int height = metrics.heightPixels; //屏幕高
		int width = metrics.widthPixels;    //屏幕的宽
		
//			获取显示方式
		int pixelformat = display.getPixelFormat();
		PixelFormat localPixelFormat1 =new PixelFormat();
		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
		int deepth = localPixelFormat1.bytesPerPixel;//位深
			    
		File mydir= new File("/dev/graphics/"); 
		Log.i("mingguo", "mdydir    "+mydir.exists());
		File fbfile = new File(mydir, "fb0");  
		Log.i("mingguo", "fbfile     "+fbfile.exists());
		FileInputStream inStream = new FileInputStream(fbfile);
		
		byte[] piex = new byte[height * width * deepth];
		DataInputStream dStream = new DataInputStream(inStream);
		int[] colors = new int[height * width];
		
		if (dStream.read(piex, 0, height * width * deepth) != -1) {
			for(int m=0;m<piex.length;m++){
				if(m%4 == 0){
					int r = (piex[m] & 0xFF);
					int g = (piex[m+1] & 0xFF);
					int b = (piex[m+2] & 0xFF);
					int a = (piex[m+3] & 0xFF);
					colors[m/4]= (a << 24) + (b <<16) + (g <<8) + r;
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
			Log.i("mingguo", "bitmap   "+bitmap);
			Date nowDate = new Date();
			
			FileOutputStream out;
//			File savedir= new File("/mnt/sdcard/MScreen/");
			try {
//				if(!savedir.exists()){	savedir.mkdir(); }
//				String fileName = "/mnt/sdcard/MScreen/test"+nowDate.getTime()+".jpg";
				if (!new File(filePath).getParentFile().exists()) {  
	        		new File(filePath).getParentFile().mkdirs();  
	            } 
	            File file = new File(filePath);  
	            if (!file.exists()) {  
	                file.createNewFile();  
	            }  
				out = new FileOutputStream(filePath);
				//bitmap.compress(Bitmap.CompressFormat.PNG, 100,out);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100,out);
				out.flush();  
				out.close();    
				//long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启   
				//vibrator.vibrate(pattern,-1); 
				//vibrator.cancel();
				//vibrator.cancel();
			} catch (Exception e) {
				Log.i("mingguo", "exception    "+e);
				e.printStackTrace();
			}
		}
		}
  
}  