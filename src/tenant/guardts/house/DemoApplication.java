package tenant.guardts.house;

import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class DemoApplication extends Application {
	private static DemoApplication mApp;  
    private static Activity sActivity;  
    @SuppressLint("NewApi")
	@Override
    public void onCreate() {
        super.onCreate();
        
     // 在主进程设置信鸽相关的内容
     		if (isMainProcess()) {
     			// 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
     			// 收到通知时，会调用本回调函数。
     			// 相当于这个回调会拦截在信鸽的弹出通知之前被截取
     			// 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
     			XGPushManager
     					.setNotifactionCallback(new XGPushNotifactionCallback() {

     						@Override
     						public void handleNotify(XGNotifaction xGNotifaction) {
     							Log.w("test", "处理信鸽通知：" + xGNotifaction);
     							// 获取标签、内容、自定义内容
     							String title = xGNotifaction.getTitle();
     							String content = xGNotifaction.getContent();
     							String customContent = xGNotifaction
     									.getCustomContent();
     							// 其它的处理
     							// 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
     							xGNotifaction.doNotify();
     						}
     					});
     		}
     		
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        
        mApp = this;  
//        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {  
//            @Override  
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {  
//                Log.d("mingguo",activity+"onActivityCreated");  
//            }  
//  
//            @Override  
//            public void onActivityStarted(Activity activity) {  
//                Log.d("mingguo",activity+"onActivityStarted");  
//                sActivity=activity;  
//  
//            }  
//  
//            @Override  
//            public void onActivityResumed(Activity activity) {  
//            	Log.d("mingguo",activity+"onActivityResumed");  
//            }  
//  
//            @Override  
//            public void onActivityPaused(Activity activity) {  
//            	Log.d("mingguo",activity+"onActivityPaused");  
//            }  
//  
//            @Override  
//            public void onActivityStopped(Activity activity) {  
//            	Log.d("mingguo",activity+"onActivityStopped");  
//            }  
//  
//            @Override  
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {  
//            	Log.d("mingguo",activity+"onActivitySaveInstanceState");  
//            }  
//  
//            @Override  
//            public void onActivityDestroyed(Activity activity) {  
//            	Log.d("mingguo",activity+"onActivityDestroyed");  
//            }  
//        });  
    }  
//  
//    public static Context getAppContext() {  
//        return mApp;  
//    }  
//  
//    public static Resources getAppResources() {  
//        return mApp.getResources();  
//    }  
//  
//    public static Activity getActivity(){  
//        return sActivity;  
//    }  
    
    public boolean isMainProcess() {
		ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = getPackageName();
		int myPid = android.os.Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}
}