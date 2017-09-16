package tenant.guardts.house;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.adapter.TransactionDetailAdapter;
import tenant.guardts.house.bean.Detail;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class TransactionDetailActivity extends BaseActivity implements DataStatusInterface {

	private ListView mListView;
	private String mGetBillLog = "http://tempuri.org/GetBillLog";// 交易明细
	private HoursePresenter mPresenter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.activity_transaction_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("交易明细");
		
		initView();
		
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		
		initData();
	}



	private void initData() {
		if(!TextUtils.isEmpty(CommonUtil.mRegisterIdcard))
		getBillLog(CommonUtil.mRegisterIdcard);
	}
	

	private void getBillLog(String IDCard) {
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=GetBillLog";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetBillLog));
		rpc.addProperty("idCard", IDCard);
		mPresenter.readyPresentServiceParams(this, url, mGetBillLog, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.transaction_listview);
		//無交易信息时显示空
		mListView.setEmptyView(findViewById(R.id.transaction_detail_empty_layout));
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			if (msg.what == 818) {
				String value = (String) msg.obj;
				Log.e("", value+"---交易明细---");
				Gson gson = new Gson();
				ArrayList<Detail> result = gson.fromJson(value, new TypeToken<ArrayList<Detail>>() {
				}.getType());
		
				mListView.setAdapter(new TransactionDetailAdapter(TransactionDetailActivity.this,result));

			}

		};
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		Log.i("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mGetBillLog)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 818;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}

	}

	@Override
	public void onStatusError(String action, String error) {

	}
}
