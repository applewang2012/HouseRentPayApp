package tenant.guardts.house;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tenant.guardts.house.model.RetinuesResult;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class RetinueDetailActivity extends BaseActivity {

	private TextView mTitleBar;
	private HoursePresenter mPresenter;
	private ListView listview;
	private String mGetRetinues = "http://tempuri.org/GetRetinues";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_retinue_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("随行人员详情");
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		initView();
		initData();

	}

	private void initData() {
		String rraid = getIntent().getStringExtra("rraid");
		Log.e("mingguo", rraid + "-----------");
		getRetinuesc(rraid);

	}

	/**
	 * 获取随行人员列表
	 * 
	 * @param rraid
	 *            订单id
	 */
	private void getRetinuesc(String rraid) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetRetinues";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetRetinues));
		rpc.addProperty("rraId", rraid);
		mPresenter.readyPresentServiceParams(this, url, mGetRetinues, rpc);
		mPresenter.startPresentServiceTask(true);

	}

	private void initView() {
		listview = (ListView) findViewById(R.id.listview);
		title = (TextView) findViewById(R.id.detail_title);// 无随行

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 100) {
				String value = (String) msg.obj;
				Log.e("mingguo", value + "pppp");
				if (value.equals("[]")) {
					title.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				} else {
					title.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
					if (!TextUtils.isEmpty(value)) {
						Gson gson = new Gson();
						Type type = new TypeToken<ArrayList<RetinuesResult>>() {
						}.getType();
						ArrayList<RetinuesResult> list = gson.fromJson(value, type);

						listview.setAdapter(new UniversalAdapter<RetinuesResult>(RetinueDetailActivity.this,
								R.layout.retinue_detail_item, list) {

							@Override
							public void convert(UniversalViewHolder holder, RetinuesResult info) {
								View convertView = holder.getConvertView();
								TextView name = (TextView) convertView.findViewById(R.id.name);
								TextView idcard = (TextView) convertView.findViewById(R.id.idcard);
								name.setText(info.name);
								idcard.setText(info.idcard);

							}
						});
					}
				}

			}
		};
	};
	private TextView title;

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.e("mingguo", "action" + action + "   templateInfo;" + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mGetRetinues)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}

}
