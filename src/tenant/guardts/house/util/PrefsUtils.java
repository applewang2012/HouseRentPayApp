package tenant.guardts.house.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsUtils {
	private static String CONFIG_FILE_NAME = "sys";

	/**获取配置信息
	 * @param ctx 上下文
	 * @param key 键
	 * @param defValue 默认值
	 * @return
	 */
	public static boolean getCfg(Context ctx, String key,boolean defValue) {
		SharedPreferences prefs = ctx.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
		return  prefs.getBoolean(key, defValue);
	}
	/**获取配置信息
	 * @param ctx 上下文
	 * @param key 键
	 * @param defValue 默认值
	 * @return
	 */
	public static String getCfg(Context ctx, String key,String defValue) {
		SharedPreferences prefs = ctx.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
		return  prefs.getString(key, defValue);
	}


	/**保存配置信息
	 * @param ctx 上下文
	 * @param key 键
	 * @param value 值
	 */
	public static void saveCfg(Context ctx, String key, boolean value) {
		SharedPreferences sp = ctx.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	/**保存配置信息
	 * @param ctx 上下文
	 * @param key 键
	 * @param value 值
	 */
	public static void saveCfg(Context ctx, String key, String value) {
		SharedPreferences sp = ctx.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	
	/**清除配置信息
	 * @param ctx
	 */
	public static void clearCfg(Context ctx){
		SharedPreferences sp = ctx.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.clear();
		edit.commit();
	}
}
