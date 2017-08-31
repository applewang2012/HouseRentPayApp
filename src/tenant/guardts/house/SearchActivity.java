package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.adapter.HistoryRecordsAdapter;
import tenant.guardts.house.helper.RecordSQLiteOpenHelper;
import tenant.guardts.house.view.AddressSelector;
import tenant.guardts.house.view.CityInterface;
import tenant.guardts.house.view.HistoryRecordsView;

public class SearchActivity extends Activity{

	private AutoCompleteTextView mSearchText;
	private RecordSQLiteOpenHelper helper;
	private TextView tvClear;
	private HistoryRecordsAdapter adapter;
	private HistoryRecordsView listView;
	private SQLiteDatabase db;
	private TextView tvSearch;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		init();
	}

	private void initEvent() {

		/**
		 * 搜索框的文本变化实时监听
		 */
		mSearchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 每次输入后，模糊查询数据库 & 显示
				// 注：若搜索框为空,则模糊搜索空字符 = 显示所有的搜索历史
				String string = mSearchText.getText().toString();
				queryData(string); 

			}
		});
		
		
		/**
		 * "清空搜索历史"按钮
		 */
		tvClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 清空数据库
				deleteData();
				// 模糊搜索空字符 = 显示所有的搜索历史（此时是没有搜索记录的）
				queryData("");
			}
		});
		
		tvSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean hasData = hasData(mSearchText.getText().toString().trim());
				// 3. 若存在，则不保存；若不存在，则将该搜索字段保存（插入）到数据库，并作为历史搜索记录
				if (!hasData) {
					insertData(mSearchText.getText().toString().trim());
					queryData("");
				}
				
//				Intent resultIntent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putString("search_tag", mSearchText.getText().toString().trim());
//				resultIntent.putExtras(bundle);
//				setResult(RESULT_OK, resultIntent);
				
				}
			
			
			
		});

	}

	/**
	 * 检索是否有某条记录
	 * 
	 * @param records
	 * @return
	 */
	protected boolean hasData(String records) {
		// 从数据库中Record表里找到name=records的id
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{records});
        //  判断是否有下一个
        return cursor.moveToNext();
		
	}

	/**
	 * 插入检索记录
	 * 
	 * @param records
	 */
	protected void insertData(String records) {
		db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + records + "')");
        db.close();
	}

	private void init() {
		// 初始化View
		initView();

		// 实例化数据库SQLiteOpenHelper子类对象
		helper = new RecordSQLiteOpenHelper(this);

		// 首次进入时查询所有的历史搜索记录
		queryData("");

		
		//点击事件
		initEvent();

	}

	/**
	 * 清空历史数据
	 */
	protected void deleteData() {
		db = helper.getWritableDatabase();
		db.execSQL("delete from records");
		db.close();
		tvClear.setVisibility(View.INVISIBLE);

	}

	/**
	 * 查询历史数据方法
	 * 
	 * @param string
	 */
	private void queryData(String string) {
		String sql = "select id as _id,name from records where name like '%" + string + "%' order by id desc ";
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		list = new ArrayList<>();
		while(cursor.moveToNext()){
			String name=cursor.getString(cursor.getColumnIndex("name"));
			list.add(name);
		}
		adapter = new HistoryRecordsAdapter(this,list);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		// 当输入框为空 & 数据库中有搜索记录时，显示 "删除搜索记录"按钮
		if (string.equals("") && cursor.getCount() != 0) {
			tvClear.setVisibility(View.VISIBLE);
		} else {
			tvClear.setVisibility(View.INVISIBLE);
		}

	}

	private void initView() {
		mSearchText = (AutoCompleteTextView) findViewById(R.id.id_search_auto_tv);
		mSearchText.setHintTextColor(Color.parseColor("#e7e7e7"));
		listView = (HistoryRecordsView) findViewById(R.id.history_listview);
		tvClear=(TextView) findViewById(R.id.tv_clear);
		tvSearch=(TextView) findViewById(R.id.tv_search);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("search_tag", list.get(position));
				resultIntent.putExtras(bundle);
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});

	}

}