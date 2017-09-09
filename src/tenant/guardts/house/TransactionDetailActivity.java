package tenant.guardts.house;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
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
	private View mLoadingView;

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
		// TODO Auto-generated method stub
		super.onResume();
		showLoadingView();
		initData();
	}



	private void initData() {
		String idcard = getIntent().getStringExtra("IDCard");
		// getBillLog(idcard);
		// getBillLog("12010519780419061X");
		getBillLog("210724197009190016");

	}
	
	private void showLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}

	private void getBillLog(String IDCard) {
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		String url = "http://qxw2332340157.my3w.com/Services.asmx?op=GetBillLog";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetBillLog));
		rpc.addProperty("idCard", IDCard);
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mGetBillLog, rpc);
		mPresenter.startPresentServiceTask();
	}

	private void initView() {
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);
		
		mListView = (ListView) findViewById(R.id.transaction_listview);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dismissLoadingView();
			if (msg.what == 818) {
				String value = (String) msg.obj;
				Gson gson = new Gson();
				ArrayList<Detail> result = gson.fromJson(value, new TypeToken<ArrayList<Detail>>() {
				}.getType());
				
				mListView.setAdapter(new TransactionDetailAdapter(TransactionDetailActivity.this,result));

			}

		};
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.i("mingguo", "tansaction detail action  "+action+" onsucces   "+templateInfo);
		if (action.equals(mGetBillLog)) {
			Message msg = mHandler.obtainMessage();
			msg.what = 818;
			msg.obj = templateInfo;
			msg.sendToTarget();
		}

	}

	@Override
	public void onStatusStart() {

	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub

	}
}
