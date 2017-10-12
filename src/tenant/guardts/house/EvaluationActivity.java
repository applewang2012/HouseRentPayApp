package tenant.guardts.house;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import tenant.guardts.house.view.CustomRatingBar;

/**评价
 * @author Zhou
 *
 */
public class EvaluationActivity extends BaseActivity {

	private TextView mHouseLocation;//房屋所在地
	private CustomRatingBar mOverallRating;//综合评价
	private CustomRatingBar mServiceRating;//服务评价
	private CustomRatingBar mEnvironmentalRating;//环境评价
	private CustomRatingBar mPriceRating;//价格评价
	private EditText mExplanation;//补充说明
	private Button mBtnSubmit;//提交
	private TextView mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_evaluation);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("评价");
		initView();
		initEvent();
	}

	private void initEvent() {
		mBtnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//提交评论
				
			}
		});
		
	}

	private void initView() {
		mHouseLocation = (TextView) findViewById(R.id.evaluation_textview_house_location);
		mOverallRating = (CustomRatingBar) findViewById(R.id.evaluation_overall_rating);
		mServiceRating = (CustomRatingBar) findViewById(R.id.evaluation_overall_rating);
		mEnvironmentalRating = (CustomRatingBar) findViewById(R.id.evaluation_overall_rating);
		mPriceRating = (CustomRatingBar) findViewById(R.id.evaluation_overall_rating);
		mExplanation = (EditText) findViewById(R.id.evaluation_edittext_explanation);
		mBtnSubmit = (Button) findViewById(R.id.evaluation_btn_submit);
	}
}
