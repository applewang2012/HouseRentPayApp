package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import tenant.guardts.house.adapter.ParternerRecordAdapter;
import tenant.guardts.house.adapter.ParternerRecordAdapter.CallBack;
import tenant.guardts.house.model.ParternerRecord;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ParternerRecordActivity extends Activity implements CallBack{

	private TextView mTitleBar;
	private ListView mListView;
	private ArrayList<ParternerRecord> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_parterner_record);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("随行人员");
		initData();
		initView();
		
	}

	private void initData() {
		list = new ArrayList<>();
		for(int i=0;i<5;i++){
			list.add(new ParternerRecord("张三"+i,i+"12312312331233456"));
		}
		
		
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		
		mListView.setAdapter(new ParternerRecordAdapter(this, list, this));

			

		
		
	}

	@Override
	public void click(View v) {
		switch(v.getId()){
		case R.id.parterner_del:
		
			Toast.makeText(this, "删除"+v.getTag(), Toast.LENGTH_SHORT).show();
			
			
			break;
		case R.id.parterner_edit:
			Toast.makeText(this, "编辑"+v.getTag(), Toast.LENGTH_SHORT).show();
			break;
		

		default:
			break;
		}
		
	}
}
