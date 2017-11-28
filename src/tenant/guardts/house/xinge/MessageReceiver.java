package tenant.guardts.house.xinge;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.android.tpush.XGCustomPushNotificationBuilder;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import tenant.guardts.house.HomeActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.util.LogUtil;

public class MessageReceiver extends XGPushBaseReceiver {
	private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
	private static Ringtone mRingtone;
	public static final String LogTag = "TPushReceiver";

	private void show(Context context, String text) {
//		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	// 通知展示
	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
		if (context == null || notifiShowedRlt == null) {
			return;
		}
		XGNotification notific = new XGNotification();
		notific.setMsg_id(notifiShowedRlt.getMsgId());
		notific.setTitle(notifiShowedRlt.getTitle());
		notific.setContent(notifiShowedRlt.getContent());
		// notificationActionType==1为Activity，2为url，3为intent
		notific.setNotificationActionType(notifiShowedRlt
				.getNotificationActionType());
		// Activity,url,intent都可以通过getActivity()获得
		notific.setActivity(notifiShowedRlt.getActivity());
		notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(Calendar.getInstance().getTime()));
//		NotificationService.getInstance(context).save(notific);
//		context.sendBroadcast(intent);
//		show(context, "您有1条新消息, " + "通知被展示 ， " + notifiShowedRlt.toString());
//		LogUtil.e("mingguo", "收到通知--播放声音  "+notifiShowedRlt.toString());
//		XGCustomPushNotificationBuilder build = new XGCustomPushNotificationBuilder();
////      build.setSound(
////              RingtoneManager.getActualDefaultRingtoneUri(
////                      context, RingtoneManager.TYPE_ALARM)) // 设置声音
//		build.setSound(
//               Uri.parse("android.resource://" + context.getPackageName()
//               + "/" + R.raw.order_remind)) //设定Raw下指定声音文件
//              .setDefaults(Notification.DEFAULT_VIBRATE) // 振动
//              .setFlags(Notification.FLAG_NO_CLEAR); // 是否可清除
//      // 设置自定义通知layout,通知背景等可以在layout里设置
//      //build.setLayoutId(R.layout.layout_notification);
//      // 设置自定义通知标题id
//      //build.setLayoutTitleId(R.id.title);
//      // 设置自定义通知图片id
//      //build.setLayoutIconId(R.id.icon);
//      // 设置自定义通知图片资源
//      //build.setLayoutIconDrawableId(R.drawable.ic_launcher);
//      // 设置状态栏的通知小图标
//      //build.setIcon(R.drawable.ic_launcher);
//      // 设置时间id
//      //build.setLayoutTimeId(R.id.time);
//      // 若不设定以上自定义layout，又想简单指定通知栏图片资源
//      //build.setNotificationLargeIcon(R.drawable.tenda_icon);
//      // 客户端保存build_id
//      XGPushManager.setDefaultNotificationBuilder(context, build);
		playSound(context);
	}
	
    private synchronized void playSound(Context context) {  
         
        if (mRingtone == null) {  
            LogUtil.v("mingguo","----------初始化铃声----------");  
            String uri = "android.resource://" + context.getPackageName() + "/" + R.raw.order_remind;  
            Uri no = Uri.parse(uri);  
            mRingtone = RingtoneManager.getRingtone(context.getApplicationContext(), no);  
        }  
        if (!mRingtone.isPlaying()) {  
            LogUtil.v("mingguo","--------------播放铃声---------------" + mRingtone.isPlaying());  
            mRingtone.play();  
        }  
    }  

	@Override
	public void onUnregisterResult(Context context, int errorCode) {
		if (context == null) {
			return;
		}
		String text = "";
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "反注册成功";
		} else {
			text = "反注册失败" + errorCode;
		}
		LogUtil.d(LogTag, text);
		show(context, text);

	}

	@Override
	public void onSetTagResult(Context context, int errorCode, String tagName) {
		if (context == null) {
			return;
		}
		String text = "";
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "\"" + tagName + "\"设置成功";
		} else {
			text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
		}
		LogUtil.d(LogTag, text);
		show(context, text);

	}

	@Override
	public void onDeleteTagResult(Context context, int errorCode, String tagName) {
		if (context == null) {
			return;
		}
		String text = "";
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "\"" + tagName + "\"删除成功";
		} else {
			text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
		}
		LogUtil.d(LogTag, text);
		show(context, text);

	}

	// 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
		if (context == null || message == null) {
			return;
		}
//		String text = "";
//		if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
//			// 通知在通知栏被点击啦。。。。。
//			// APP自己处理点击的相关动作
//			// 这个动作可以在activity的onResume也能监听，请看第3点相关内容
//			text = "通知被打开 :" + message;
//		} else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
//			// 通知被清除啦。。。。
//			// APP自己处理通知被清除后的相关动作
//			text = "通知被清除 :" + message;
//		}
//		Toast.makeText(context, "广播接收到通知被点击:" + message.toString(),
//				Toast.LENGTH_SHORT).show();
//		// 获取自定义key-value
		String customContent = message.getCustomContent();
		LogUtil.w("mingguo", "xinge click notification   "+message.toString());
		if (customContent != null && customContent.length() != 0) {
			Intent intent = new Intent(context, HomeActivity.class);
	        //intent.putExtra("data", id.intValue());
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(intent);
//			try {
//				JSONObject obj = new JSONObject(customContent);
//				// key1为前台配置的key
//				if (!obj.isNull("key")) {
//					String value = obj.getString("key");
//					LogUtil.d(LogTag, "get custom value:" + value);
//				}
//				// ...
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
		}
		// APP自主处理的过程。。。
//		LogUtil.w(LogTag, text);
//		show(context, text);
		//打开通知 by wangmingguo
	}

	@Override
	public void onRegisterResult(Context context, int errorCode,
			XGPushRegisterResult message) {
		// TODO Auto-generated method stub
		if (context == null || message == null) {
			return;
		}
		String text = "";
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = message + "注册成功";
			// 在这里拿token
			String token = message.getToken();
		} else {
			text = message + "注册失败，错误码：" + errorCode;
		}
		LogUtil.d(LogTag, text);
		show(context, text);
	}

	// 消息透传
	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		// TODO Auto-generated method stub
		String text = "收到消息:" + message.toString();
		// 获取自定义key-value
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				JSONObject obj = new JSONObject(customContent);
				// key1为前台配置的key
				if (!obj.isNull("key")) {
					String value = obj.getString("key");
					LogUtil.d(LogTag, "get custom value:" + value);
				}
				// ...
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// APP自主处理消息的过程...
		LogUtil.d(LogTag, text);
		
		
        
		show(context, text);
	}

}
