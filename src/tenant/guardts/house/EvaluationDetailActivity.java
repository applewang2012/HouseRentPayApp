package tenant.guardts.house;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import tenant.guardts.house.model.EvaluationItem;
import tenant.guardts.house.model.EvaluationResult;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.UtilTool;
import tenant.guardts.house.view.CustomRatingBar2;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EvaluationDetailActivity extends BaseActivity {

	private TextView mTitleBar;
	private ListView mListView;
	private String mGetEvaluationListAction = "http://tempuri.org/GetEvaluationList";// 获得评论列表
	private Context mContext;
	private HoursePresenter mPresent;
	ArrayList<EvaluationItem> list = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_evaluation_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("查看评价");
		mContext = getApplicationContext();
		mPresent = new HoursePresenter(mContext, this);
		initData();
		initView();
		initEvent();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 100) {
				String value = (String) msg.obj;

				Gson gson = new Gson();
				EvaluationResult result = gson.fromJson(value, EvaluationResult.class);
				Type type = new TypeToken<ArrayList<EvaluationItem>>() {
				}.getType();
				list = gson.fromJson(result.msg, type);
				mListView.setAdapter(new UniversalAdapter<EvaluationItem>(EvaluationDetailActivity.this,
						R.layout.evaluation_detail_item, list) {
					@Override
					public void convert(UniversalViewHolder holder, EvaluationItem info) {
						View convertView = holder.getConvertView();
						TextView user = (TextView) convertView.findViewById(R.id.detail_user);
						TextView date = (TextView) convertView.findViewById(R.id.detail_date);
						CustomRatingBar2 serviceBar = (CustomRatingBar2) convertView
								.findViewById(R.id.evaluation_service_rating);
						CustomRatingBar2 environmentBar = (CustomRatingBar2) convertView
								.findViewById(R.id.evaluation_environmental_rating);
						CustomRatingBar2 pirceBar = (CustomRatingBar2) convertView
								.findViewById(R.id.evaluation_price_rating);
						user.setText(info.EvaluatePerson.substring(0, 3) + "****" + info.EvaluatePerson.substring(8));
						date.setText(UtilTool.stampToNormalDate(info.EvaluateDate.substring(6,
								info.EvaluateDate.length() - 2)));
						serviceBar.setScore(convert2Integer(info.EvaluateItem0));
						environmentBar.setScore(convert2Integer(info.EvaluateItem1));
						pirceBar.setScore(convert2Integer(info.EvaluateItem2));
					}
				});

			}
		};
	};

	private int convert2Integer(String str) {
		int idx = str.lastIndexOf(".");// 查找小数点的位置
		String strNum = str.substring(0, idx);
		return Integer.valueOf(strNum);
	}

	/**
	 * 获得评论列表
	 * 
	 * @param obj
	 *            评价对象的id RRAID：房屋 idcard：房客身份证
	 */
	private void getEvaluationList(String obj) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetEvaluationList";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetEvaluationListAction));
		rpc.addProperty("obj", obj);
		mPresent.readyPresentServiceParams(this, url, mGetEvaluationListAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.e("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mGetEvaluationListAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}

	private void initData() {

		String rraid = getIntent().getStringExtra("rraid");

		getEvaluationList(rraid);

	}

	private void initEvent() {

	}

	private void initView() {

		mListView = (ListView) findViewById(R.id.evaluation_detail_listview);

	}
}
