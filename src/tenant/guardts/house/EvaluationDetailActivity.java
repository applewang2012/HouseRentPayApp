package tenant.guardts.house;

import java.util.ArrayList;

import tenant.guardts.house.model.EvaluationDetail;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import tenant.guardts.house.view.CustomRatingBar2;
import android.os.Bundle;
import android.view.View;
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
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("评价详情");
		initView();
		initEvent();
	}

	private void initEvent() {

	}

	private void initView() {
		ArrayList<EvaluationDetail> list = new ArrayList<>();
		list.add(new EvaluationDetail("张三", "2019-09-08 12:000", "0", "1", "5"));//
		list.add(new EvaluationDetail("李四", "2019-09-08 12:000", "2", "4", "1"));
		list.add(new EvaluationDetail("王五", "2019-09-08 12:000", "3", "2", "0"));
		list.add(new EvaluationDetail("赵二", "2019-09-08 12:000", "2", "2", "0"));
		list.add(new EvaluationDetail("流弊", "2019-09-08 12:000", "1", "2", "3"));//
		list.add(new EvaluationDetail("阿萨德", "2019-09-08 12:000", "3", "3", "3"));//
		list.add(new EvaluationDetail("欧吉", "2019-09-08 12:000", "1", "0", "0"));//
		list.add(new EvaluationDetail("张哈", "2019-09-08 12:000", "4", "4", "2"));
		list.add(new EvaluationDetail("撒地", "2019-09-08 12:000", "3", "3", "1"));
		list.add(new EvaluationDetail("好玩", "2019-09-08 12:000", "5", "5", "5"));//
		list.add(new EvaluationDetail("还萨迪", "2019-09-08 12:000", "1", "0", "3"));//
		mListView = (ListView) findViewById(R.id.evaluation_detail_listview);
		mListView.setAdapter(new UniversalAdapter<EvaluationDetail>(this, R.layout.evaluation_detail_item, list) {
			@Override
			public void convert(UniversalViewHolder holder, EvaluationDetail info) {
				View convertView = holder.getConvertView();
				TextView user = (TextView) convertView.findViewById(R.id.detail_user);
				TextView date = (TextView) convertView.findViewById(R.id.detail_date);
				CustomRatingBar2 serviceBar = (CustomRatingBar2) convertView.findViewById(R.id.evaluation_service_rating);
				CustomRatingBar2 environmentBar = (CustomRatingBar2) convertView.findViewById(R.id.evaluation_environmental_rating);
				CustomRatingBar2 pirceBar = (CustomRatingBar2) convertView.findViewById(R.id.evaluation_price_rating);
				user.setText(info.getUser());
				date.setText(info.getDate());
				serviceBar.setScore(Integer.valueOf(info.getServiceRating()));
				environmentBar.setScore(Integer.valueOf(info.getEnvironmentalRating()));
				pirceBar.setScore(Integer.valueOf(info.getPriceRating()));
			}
		});

	}
}
