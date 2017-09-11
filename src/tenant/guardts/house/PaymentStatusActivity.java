package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.CompleteStatus;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.UtilTool;

public class PaymentStatusActivity extends BaseActivity implements DataStatusInterface {
	private String mCompleteRentAttribute = "http://tempuri.org/CompleteRentAttribute";
	private HoursePresenter mPresenter;
	private View mLoadingView;
	private String rentNO;
	private String orderCreatedDate;
	private String price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		successful = getIntent().getBooleanExtra("flag", false);
		orderID = getIntent().getStringExtra("orderID");
		rentNO = getIntent().getStringExtra("rentNO");
		orderCreatedDate = getIntent().getStringExtra("orderCreatedDate");
		price=getIntent().getStringExtra("pay_price");
		
		Log.e("", orderID + "===============");
		if (successful) {
			setContentView(R.layout.activity_successful_payment);
		} else {
			setContentView(R.layout.activity_payment_failure);
		}
		mPresenter = new HoursePresenter(PaymentStatusActivity.this, this);
		initView();
		initEvent();
	}

	private void initEvent() {
		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (orderID != null)
					showLoadingView();
				completeHouseRentAttributeInfo(orderID);
			}
		});

	}

	private void showLoadingView() {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
			ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
			if (imageView != null) {
				RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(PaymentStatusActivity.this,
						R.anim.anim_rotate);
				imageView.startAnimation(rotate);
			}
		}
	}

	private void dismissLoadingView() {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}

	private void initView() {
		mLoadingView = (View) findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);
		finish = (Button) findViewById(R.id.id_button_finish_pay);
		if(successful){
			TextView tvRentNO=(TextView) findViewById(R.id.id_pay_order_no);
			tvRentNO.setText(rentNO);
			TextView tvOrderCreatedDate=(TextView) findViewById(R.id.id_pay_oder_time);
			String date = UtilTool.stampToNormalDate(orderCreatedDate.substring(6,orderCreatedDate.length()-2));
			tvOrderCreatedDate.setText(date);
			TextView money=(TextView) findViewById(R.id.id_pay_monkey);
			money.setText("¥"+price);
			
		}
	}

	/**
	 * 通知后台更新订单状态
	 * 
	 * @param id
	 *            房屋订单号
	 */
	private void completeHouseRentAttributeInfo(String id) {
		if (id == null || id.equals("")) {
			return;
		}
		String url = CommonUtil.mUserHost + "Services.asmx?op=CompleteRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCompleteRentAttribute));
		rpc.addProperty("id", id);
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mCompleteRentAttribute, rpc);
		mPresenter.startPresentServiceTask();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 818) {
				dismissLoadingView();
				String value = (String) msg.obj;
				Log.e("", value + "--");
				Gson gson = new Gson();
				CompleteStatus completeStatus = gson.fromJson(value, CompleteStatus.class);
				int ret = Integer.parseInt(completeStatus.ret);
				if (ret == 0) {
					finish();
				} else {
					Toast.makeText(PaymentStatusActivity.this, "订单提交失败", Toast.LENGTH_LONG).show();
				}
			}
		};
	};
	private Button finish;
	private String orderID;
	private boolean successful;



	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		if (action.equals(mCompleteRentAttribute)) {
			Log.e("", action + "======" + templateInfo);
			Message msg = mHandler.obtainMessage();
			msg.what = 818;
			msg.obj = templateInfo;
			msg.sendToTarget();
		}
	}

	@Override
	public void onStatusStart() {
		super.onStatusStart();
	}

	@Override
	public void onStatusError(String action, String error) {
		super.onStatusError(action, error);
	}
}