package tenant.guardts.house;

import com.baidu.mapapi.SDKInitializer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class DemoApplication extends Application {
	private static DemoApplication mApp;  
    private static Activity sActivity;  
    @SuppressLint("NewApi")
	@Override
    public void onCreate() {
        super.onCreate();
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
}