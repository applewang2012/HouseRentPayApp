package tenant.guardts.house;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import tenant.guardts.house.view.CustomRatingBar;

public class EvaluationActivity extends Activity {

	private TextView mHouseLocation;//房屋所在地
	private CustomRatingBar mOverallRating;//综合评价
	private CustomRatingBar mServiceRating;//服务评价
	private CustomRatingBar mEnvironmentalRating;//环境评价
	private CustomRatingBar mPriceRating;//价格评价
	private EditText mExplanation;//补充说明
	private Button mBtnSubmit;//提交

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_evaluation);
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
