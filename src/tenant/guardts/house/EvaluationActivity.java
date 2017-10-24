package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

import tenant.guardts.house.model.Evaluation;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.view.CustomRatingBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * 评价
 * 
 * @author Zhou
 *
 */
public class EvaluationActivity extends BaseActivity {

	private TextView mHouseLocation;// 房屋所在地
	private CustomRatingBar mOverallRating;// 综合评价
	private CustomRatingBar mServiceRating;// 服务评价
	private CustomRatingBar mEnvironmentalRating;// 环境评价
	private CustomRatingBar mPriceRating;// 价格评价
	private EditText mExplanation;// 补充说明
	private Button mBtnSubmit;// 提交
	private TextView mTitleBar;
	private String mAddEvaluationAction = "http://tempuri.org/AddEvaluation";// 添加评论
	private HoursePresenter mPresent;
	private Context mContext;
	private CheckBox box1;
	private CheckBox box2;
	private CheckBox box3;
	private CheckBox box4;
	private CheckBox box5;
	private CheckBox box6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_evaluation);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("评价");
		initView();
		initData();
		initEvent();
	}

	private void initData() {
		type = getIntent().getStringExtra("detail_type");
		info = (HouseInfoModel) getIntent().getSerializableExtra("order_detail");
		mHouseLocation.setText(info.getHouseAddress());
		if (type.equals("renter")) {
			title1.setText("服务评分");
			title2.setText("环境评分");
			title3.setText("价格评分");
			box1.setText("环境良好");
			box2.setText("交通方便");
			box3.setText("态度好");
			box4.setText("家电齐全");
			box5.setText("价格公道");
			box6.setText("环境较差");
		} else if (type.equals("owner")) {
			title1.setText("环境评分");
			title2.setText("信誉评分");
			title3.setText("态度评分");
			box1.setText("诚实守信");
			box2.setText("环境整洁");
			box3.setText("友好沟通");
			box4.setText("与人为善");
			box5.setText("有破环行为");
			box6.setText("环境较差");
		}
	}

	private void initEvent() {
		mBtnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuilder desc = new StringBuilder();
				if (box1.isChecked()) {
					desc.append(box1.getText().toString()+",");
				}
				if (box2.isChecked()) {
					desc.append(box2.getText().toString()+",");
				}
				if (box3.isChecked()) {
					desc.append(box3.getText().toString()+",");
				}
				if (box4.isChecked()) {
					desc.append(box4.getText().toString()+",");
				}
				if (box5.isChecked()) {
					desc.append(box5.getText().toString()+",");
				}
				if (box6.isChecked()) {
					desc.append(box6.getText().toString()+",");
				}
				// 提交评论
				if (!TextUtils.isEmpty(type) && info != null) {
					int service = mServiceRating.getCount();
					int enviroment = mEnvironmentalRating.getCount();
					int cost = mPriceRating.getCount();
					if (type.equals("renter")) {
						if (service == 0 || enviroment == 0 || cost == 0) {
							Toast.makeText(EvaluationActivity.this, "分数不能为空，请评分", Toast.LENGTH_SHORT).show();
						} else {
							
							addEvaluation(info.getHouseId(), "1", service, enviroment, cost, desc.toString().substring(0,desc.toString().length()-1),
									CommonUtil.mUserLoginName);

						}
					} else if (type.equals("owner")) {
						if (service == 0 || enviroment == 0 || cost == 0) {
							Toast.makeText(EvaluationActivity.this, "分数不能为空，请评分", Toast.LENGTH_SHORT).show();
						} else {
							addEvaluation(info.getRenterIdcard(), "0", service, enviroment, cost, desc.toString().substring(0,desc.toString().length()-1),
									CommonUtil.mUserLoginName);
						}
					}
				}
			}
		});

	}

	/**
	 * 添加评价
	 * 
	 * @param evaObject
	 *            评价对象的id 房屋id，IDCard：房客身份证
	 * @param evaType
	 *            评价对象的类型 1：房屋，0：房客
	 * @param service
	 *            服务评价
	 * @param enviroment
	 *            环境评价
	 * @param cost
	 *            价格评价
	 * @param desc
	 *            描述
	 * @param evaPerson
	 *            评价人用户名（手机号）
	 */
	private void addEvaluation(String evaObject, String evaType, int service, int enviroment, int cost, String desc,
			String evaPerson) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetRentList";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mAddEvaluationAction));
		rpc.addProperty("evaObject", evaObject);
		rpc.addProperty("evaType", evaType);
		rpc.addProperty("service", service);
		rpc.addProperty("enviroment", enviroment);
		rpc.addProperty("cost", cost);
		rpc.addProperty("desc", desc);
		rpc.addProperty("evaPerson", evaPerson);
		mPresent.readyPresentServiceParams(this, url, mAddEvaluationAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void initView() {
		mContext = getApplicationContext();
		mPresent = new HoursePresenter(mContext, EvaluationActivity.this);
		mHouseLocation = (TextView) findViewById(R.id.evaluation_textview_house_location);
		mOverallRating = (CustomRatingBar) findViewById(R.id.evaluation_overall_rating);
		mServiceRating = (CustomRatingBar) findViewById(R.id.evaluation_service_rating);// 服务评分
		mEnvironmentalRating = (CustomRatingBar) findViewById(R.id.evaluation_environmental_rating);// 环境评分
		mPriceRating = (CustomRatingBar) findViewById(R.id.evaluation_price_rating);// 价格评分
		box1 = (CheckBox) findViewById(R.id.eval_checkbox1);
		box2 = (CheckBox) findViewById(R.id.eval_checkbox2);
		box3 = (CheckBox) findViewById(R.id.eval_checkbox3);
		box4 = (CheckBox) findViewById(R.id.eval_checkbox4);
		box5 = (CheckBox) findViewById(R.id.eval_checkbox5);
		box6 = (CheckBox) findViewById(R.id.eval_checkbox6);
		title1 = (TextView) findViewById(R.id.title1);
		title2 = (TextView) findViewById(R.id.title2);
		title3 = (TextView) findViewById(R.id.title3);
		box1.setChecked(true);
		box2.setChecked(true);
		box3.setChecked(true);
		mBtnSubmit = (Button) findViewById(R.id.evaluation_btn_submit);
		
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 100) {
				String value = (String) msg.obj;
				Gson gson = new Gson();
				Evaluation evaluation = gson.fromJson(value, Evaluation.class);
				if (!TextUtils.isEmpty(evaluation.ret)) {
					if (evaluation.ret.equals("0")) {
						Toast.makeText(EvaluationActivity.this, "评价成功", Toast.LENGTH_SHORT).show();
						if (!TextUtils.isEmpty(type) && info != null) {
							if (type.equals("renter")) {
								Intent intent = new Intent(EvaluationActivity.this, EvaluationDetailActivity.class);
								intent.putExtra("rraid", info.getHouseOrderId());
								startActivity(intent);
							}
							finish();
						}

					}else{

						Toast.makeText(EvaluationActivity.this, "评价失败", Toast.LENGTH_SHORT).show();

					}
				}
			}

		};
	};
	private String type;
	private HouseInfoModel info;
	private TextView title1;
	private TextView title2;
	private TextView title3;

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		LogUtil.e("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mAddEvaluationAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}

}
