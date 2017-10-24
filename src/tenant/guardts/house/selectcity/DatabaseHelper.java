package tenant.guardts.house.selectcity;

import tenant.guardts.house.util.LogUtil;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	// ��û��ʵ��,�ǲ����������๹�����Ĳ���,��������Ϊ��̬
	private static final String name = "city"; // ��ݿ����
	private static final int version = 1; // ��ݿ�汾

	public DatabaseHelper(Context context) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtil.e("info", "create table");
		db.execSQL("CREATE TABLE IF NOT EXISTS recentcity (id integer primary key autoincrement, name varchar(40), date INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
