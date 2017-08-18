package tenant.guardts.house;

import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import tenant.guardts.house.helper.RecordSQLiteOpenHelper;

public class SearchActivity extends Activity {

	private AutoCompleteTextView mSearchText;
	private RecordSQLiteOpenHelper helper;
	private TextView tvClear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);

		init();
		initEvent();

	}

	


	private void initEvent() {
		mSearchText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                    // 1. 点击搜索按键后，根据输入的搜索字段进行查询
                    // 注：由于此处需求会根据自身情况不同而不同，所以具体逻辑由开发者自己实现，此处仅留出接口
                    if (!(mCallBack == null)){
                        mCallBack.SearchAciton(mSearchText.getText().toString());
                    }
                    Toast.makeText(context, "需要搜索的是" + mSearchText.getText(), Toast.LENGTH_SHORT).show();

                    // 2. 点击搜索键后，对该搜索字段在数据库是否存在进行检查（查询）
                    boolean hasData = hasData(mSearchText.getText().toString().trim());
                    // 3. 若存在，则不保存；若不存在，则将该搜索字段保存（插入）到数据库，并作为历史搜索记录
                    if (!hasData) {
                        insertData(mSearchText.getText().toString().trim());
                        queryData("");
                    }
                }
                return false;
            }
        });
		
		
		/**
         * 搜索框的文本变化实时监听
         */
//		mSearchText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            // 输入文本后调用该方法
//            @Override
//            public void afterTextChanged(Editable s) {
//                // 每次输入后，模糊查询数据库 & 显示
//                // 注：若搜索框为空,则模糊搜索空字符 = 显示所有的搜索历史
//                String tempName = et_search.getText().toString();
//                queryData(tempName); // ->>关注1
//
//            }
//        });

		
		
	}




	/**检索是否有某条记录
	 * @param records
	 * @return
	 */
	protected boolean hasData(String records) {
		// TODO Auto-generated method stub
		return false;
	}




	/**插入检索记录
	 * @param records
	 */
	protected void insertData(String records) {
		
		
	}




	private void init() {
		//初始化View
        initView();

        // 实例化数据库SQLiteOpenHelper子类对象
        helper = new RecordSQLiteOpenHelper(this);

        // 首次进入时查询所有的历史搜索记录
        queryData("");
        
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

	}




	protected void deleteData() {
		// TODO Auto-generated method stub
		
	}




	/**查询历史数据方法
	 * @param string
	 */
	private void queryData(String string) {
		
	}




	private void initView() {
		mSearchText = (AutoCompleteTextView) findViewById(R.id.id_search_auto_tv);
		mSearchText.setHintTextColor(Color.parseColor("#e7e7e7"));
		
	}

	
}
