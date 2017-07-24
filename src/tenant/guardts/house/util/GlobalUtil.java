package tenant.guardts.house.util;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.R;


/**
 * Some common global utilities.
 */
public final class GlobalUtil {

	// This class should not be instantiated, hence the private constructor
	private GlobalUtil() {
	}


	/**
	 * Get version code for this application.
	 * 
	 * @param context
	 *            The context to use. Usually your
	 *            {@link android.app.Application} or
	 *            {@link android.app.Activity} object.
	 * @return the version code or -1 if package not found
	 */
	public static int getVersionCode(Context context) {
		int versionCode;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
			versionCode = -1;
		}

		return versionCode;
	}

	/**
	 * Get version code for the application package name.
	 * 
	 * @param context
	 *            The context to use. Usually your
	 *            {@link android.app.Application} or
	 *            {@link android.app.Activity} object.
	 * @param packageName
	 *            application package name
	 * @return the version code or -1 if package not found
	 */
	public static int getVersionCode(Context context, String packageName) {
		int versionCode;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					packageName, 0).versionCode;
		} catch (Exception e) {
			versionCode = -1;
		}

		return versionCode;
	}

	/**
	 * Get version name for this application.
	 * 
	 * @param context
	 *            The context to use. Usually your
	 *            {@link android.app.Application} or
	 *            {@link android.app.Activity} object.
	 * @return the version name or empty string if package not found
	 */
	public static String getVersionName(Context context) {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			//versionName = StringUtil.EMPTY_STRING;
		}

		return versionName;
	}
	
	/**
	 * Get version name for this application.
	 * 
	 * @param context
	 *            The context to use. Usually your
	 *            {@link android.app.Application} or
	 *            {@link android.app.Activity} object.
	 * @return the version name or empty string if package not found
	 */
	public static String getVersionName(Context context, String packageName) {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					packageName, 0).versionName;
		} catch (Exception e) {
			//versionName = StringUtil.EMPTY_STRING;
		}

		return versionName;
	}

	public static void startActivity(Context context, Class<?> class1) {
		Intent intent = new Intent();
		intent.setClass(context, class1);
		context.startActivity(intent);
	}

	public static void startActivity(Context context, Class<?> class1, int flags) {
		Intent intent = new Intent();
		intent.setClass(context, class1);
		intent.addFlags(flags);
		context.startActivity(intent);
	}

	public static void startActivity(Context context, Class<?> class1,
			Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(context, class1);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	public static void shortToast(Context context, int resId) {
		// Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
		newtoast(context, context.getString(resId), null, Toast.LENGTH_SHORT);
	}

	public static void shortToast(Context context, int resId, Drawable icon) {
		newtoast(context, context.getString(resId), icon, Toast.LENGTH_SHORT);
	}

	public static void shortToast(Context context, CharSequence text,
			Drawable icon) {
		newtoast(context, text, icon, Toast.LENGTH_SHORT);
	}

	public static void shortToast(Context context, CharSequence text) {
		newtoast(context, text, null, Toast.LENGTH_SHORT);
	}

	public static void longToast(Context context, int resId) {
		newtoast(context, context.getString(resId), null, Toast.LENGTH_LONG);
	}

	public static void longToast(Context context, int resId, Drawable icon) {
		newtoast(context, context.getString(resId), icon, Toast.LENGTH_LONG);
	}

	public static void longToast(Context context, CharSequence text) {
		newtoast(context, text, null, Toast.LENGTH_LONG);
	}

	/**
	 * Make a toast.
	 * 
	 * @param context
	 *            The context to use. Usually your
	 *            {@link android.app.Application} or
	 *            {@link android.app.Activity} object.
	 * @param text
	 *            The text to show. Can be formatted text.
	 * @param icon
	 *            The icon image to show.
	 * @param duration
	 *            How long to display the message. Either {@link #LENGTH_SHORT}
	 *            or {@link #LENGTH_LONG}
	 * 
	 */
	/*
	public static void pritoast(Context context, CharSequence text, Drawable icon,
			int duration) {
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflate.inflate(R.layout.toast, null);
		((TextView) v.findViewById(android.R.id.message)).setText(text);
		if (icon != null) {
			ImageView imageView = (ImageView) v.findViewById(R.id.icon_image);
			imageView.setImageDrawable(icon);
			imageView.setVisibility(View.VISIBLE);
		}

		NewToast toast = new NewToast(context);
		toast.setDuration(duration);
		toast.setContentView(v);
		toast.show();
	}
	*/
	public static void newtoast(Context context, CharSequence text,
			Drawable icon, int duration) {
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflate.inflate(R.layout.toast, null);
		((TextView) v.findViewById(android.R.id.message)).setText(text);
		if (icon != null) {
			ImageView imageView = (ImageView) v.findViewById(R.id.icon_image);
			imageView.setImageDrawable(icon);
			imageView.setVisibility(View.VISIBLE);
		}
		switch (duration) {
		case Toast.LENGTH_SHORT:
			duration = 1000;
			break;
		case Toast.LENGTH_LONG:
			duration = 3000;
			break;
		}
		NewToast toast = new NewToast(context);
		toast.setContentView(v);
		toast.setDuration(duration);
		toast.show();

	}

	public static void safeShowDialog(Activity activity, int id) {
		try {
			activity.showDialog(id);
		} catch (Throwable e) {
		}
	}

	public static void safeDismissDialog(Activity activity, int id) {
		try {
			activity.dismissDialog(id);
		} catch (Throwable e) {
		}
	}

}
