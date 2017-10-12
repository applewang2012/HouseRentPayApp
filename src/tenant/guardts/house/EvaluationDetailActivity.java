package tenant.guardts.house;

import java.util.ArrayList;

import tenant.guardts.house.adapter.EvaluationDetailAdapter;
import tenant.guardts.house.model.EvaluationDetail;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class EvaluationDetailActivity extends BaseActivity {

	private TextView mTitleBar;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_evaluation_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("评价详情");
		initView();
		initEvent();
	}

	private void initEvent() {
		
		
	}

	private void initView() {
		ArrayList<EvaluationDetail> list=new ArrayList<>();
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "3", "3", "3"));
		mListView = (ListView) findViewById(R.id.evaluation_detail_listview);
		mListView.setAdapter(new EvaluationDetailAdapter(this,list));
		
		
	}
}
