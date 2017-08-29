package tenant.guardts.house;

import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class HouseOrderDetailsActivity extends BaseActivity {
	private HouseInfoModel mOrderDetail;
	private String mDetailType;
	private HoursePresenter mPresent;
	private View mLoadingView;
	private String mCancelAttrbuteAction = "http://tempuri.org/CancelRentAttribute";
	private String mRejectRentAction = "http://tempuri.org/RejectRentAttribute";
	private String mConfirmRentAttribute = "http://tempuri.org/ConfirmRentAttribute";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_order_details_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("订单详情");
		mOrderDetail = (HouseInfoModel) getIntent().getSerializableExtra("order_detail");
		mDetailType = getIntent().getStringExtra("detail_type");
		mPresent = new HoursePresenter(HouseOrderDetailsActivity.this, this);
		initView();
	}
	
	private void initView(){
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);
		TextView address = (TextView) findViewById(R.id.id_order_detail_address);
		TextView contactName = (TextView) findViewById(R.id.id_order_detail_contacn_name);
		TextView contactPhone = (TextView) findViewById(R.id.id_order_detail_contacn_phone);
		TextView startTime = (TextView) findViewById(R.id.id_order_detail_start_name);
		TextView endTime = (TextView) findViewById(R.id.id_order_detail_end_name);
		TextView status = (TextView) findViewById(R.id.id_order_detail_status);
		TextView ownerName = (TextView) findViewById(R.id.id_order_detail_owner_name);
		TextView ownerPhone = (TextView) findViewById(R.id.id_order_detail_owner_phone);
		TextView money = (TextView) findViewById(R.id.id_order_detail_pay_monkey);
		address.setText(mOrderDetail.getHouseAddress());
		contactName.setText(mOrderDetail.getHouseContactName());
		contactPhone.setText(mOrderDetail.getHouseContactPhone());
		startTime.setText(mOrderDetail.getHouseStartTime());
		endTime.setText(mOrderDetail.getHouseEndTime());
		status.setText(mOrderDetail.getHouseStatus());
		ownerName.setText(mOrderDetail.getHouseOwnerName());
		ownerPhone.setText(mOrderDetail.getHouseOwnerPhone());
		money.setText(mOrderDetail.getHousePrice());
		Button button1 = (Button)findViewById(R.id.id_order_detail_button1);
		Button button2 = (Button)findViewById(R.id.id_order_detail_button2);
		if (mOrderDetail.getHouseStatus().equals("0")){
			status.setText("待确认");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("确认订单");
			button1.setVisibility(View.INVISIBLE);
			
			button2.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			if (mDetailType != null){
				if (mDetailType.equals("owner")){
					button1.setVisibility(View.VISIBLE);
					button1.setTextColor(Color.parseColor("#337ffd"));
					button1.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showLoadingView();
							confirmRentAttributeInfo(mOrderDetail.getHouseOrderId());
						}
					});
					button2.setText("拒绝订单");
					button2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							showRejectOrderDialog(mOrderDetail.getHouseOrderId());
						}
					});
				}else if (mDetailType.equals("renter")){
					button2.setText("取消订单");
					button2.setTextColor(Color.parseColor("#337ffd"));
					button2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							showCancelOrderDialog(0, mOrderDetail.getHouseOrderId());
						}
					});
				}
			}
			
		}else if (mOrderDetail.getHouseStatus().equals("1")){
			status.setText("待支付");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("立即付款");
			button1.setTextColor(Color.parseColor("#337ffd"));
			button1.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button2.setText("取消订单");
			
			if (mDetailType != null){
				if (mDetailType.equals("owner")){
					button1.setVisibility(View.INVISIBLE);
					button2.setVisibility(View.INVISIBLE);
				}else if (mDetailType.equals("renter")){
					button1.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							CommonUtil.mPayHouseOrderId = mOrderDetail.getHouseOrderId();
							Intent payIntent = new Intent(HouseOrderDetailsActivity.this, tenant.guardts.house.wxapi.HousePayActivity.class);
							payIntent.putExtra("pay_price", mOrderDetail.getHousePrice());
							payIntent.putExtra("owner_idcard", mOrderDetail.getHouseOwnerIdcard());
							startActivity(payIntent);
						}
					});
					button2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							showCancelOrderDialog(0, mOrderDetail.getHouseOrderId());
							
						}
					});
				}
			}
		}else if (mOrderDetail.getHouseStatus().equals("2")){
			status.setText("已支付");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setText("查看详情");
			button2.setVisibility(View.INVISIBLE);
			/*button3.setText("查看详情");
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);*/
		}else if (mOrderDetail.getHouseStatus().equals("3")){
			status.setText("待评价");
			status.setTextColor(Color.parseColor("#8be487"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setText("查看详情");
			button2.setVisibility(View.INVISIBLE);
//			button3.setText("立即评价");
//			button3.setTextColor(Color.parseColor("#337ffd"));
//			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
		}else if (mOrderDetail.getHouseStatus().equals("8")){
			status.setText("已取消");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setText("查看详情");
			button2.setVisibility(View.INVISIBLE);
//			button3.setText("查看详情");
//			button3.setTextColor(Color.parseColor("#337ffd"));
//			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
		}else if (mOrderDetail.getHouseStatus().equals("9")){
			status.setText("已拒绝");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setVisibility(View.INVISIBLE);
//			button3.setText("查看详情");
//			button3.setTextColor(Color.parseColor("#337ffd"));
//			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
		}
		
		Button back = (Button)findViewById(R.id.id_order_detail_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void showLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(HouseOrderDetailsActivity.this, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}
	
	private void showCancelOrderDialog(final int id, final String houseId) {  
		
		  AlertDialog.Builder builder =new AlertDialog.Builder(HouseOrderDetailsActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("取消订单");
		  builder.setMessage("您确认要取消该订单吗？");
		  builder.setIcon(android.R.drawable.ic_dialog_info);
		  builder.setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {
		         @Override  
		  
		         public void onClick(DialogInterface dialog, int which) {
		        	 showLoadingView();
					 cancelRentAttributeInfo(houseId);
		        	 
		         }  
			
		});
		builder.setNegativeButton(getString(R.string.button_cancel),new DialogInterface.OnClickListener() {
			  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {
	  
	             Log.i("alertdialog"," �뱣�����ݣ�");  
	  
	         }
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	private void confirmRentAttributeInfo(String id){
		mLoadingView.setVisibility(View.VISIBLE);
		String url = CommonUtil.mUserHost+"Services.asmx?op=ConfirmRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mConfirmRentAttribute));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(HouseOrderDetailsActivity.this, url, mConfirmRentAttribute, rpc);
		mPresent.startPresentServiceTask();
	}
	
	private void showRejectOrderDialog(final String houseId) {  
		
		  AlertDialog.Builder builder =new AlertDialog.Builder(HouseOrderDetailsActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("拒绝订单");
		  builder.setMessage("您确认要拒绝该订单吗？");
		  builder.setIcon(android.R.drawable.ic_dialog_info);
		  builder.setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {
		         @Override  
		  
		         public void onClick(DialogInterface dialog, int which) {
//		        	 mCurrentPosition = id;
		        	 showLoadingView();
					rejectRentAttributeInfo(houseId);
		         }  
			
		});
		builder.setNegativeButton(getString(R.string.button_cancel),new DialogInterface.OnClickListener() {
			  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {
	  
	             Log.i("alertdialog"," �뱣�����ݣ�");  
	  
	         }
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	private void rejectRentAttributeInfo(String id){
		mLoadingView.setVisibility(View.VISIBLE);
		String url = CommonUtil.mUserHost+"Services.asmx?op=ConfirmRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRejectRentAction));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(HouseOrderDetailsActivity.this, url, mRejectRentAction, rpc);
		mPresent.startPresentServiceTask();
	}
	
	
	private void cancelRentAttributeInfo(String id){
		mLoadingView.setVisibility(View.VISIBLE);
		String url = CommonUtil.mUserHost+"Services.asmx?op=CancelRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCancelAttrbuteAction));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(HouseOrderDetailsActivity.this, url, mCancelAttrbuteAction, rpc);
		mPresent.startPresentServiceTask();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			dismissLoadingView();
			if (msg.what == 100){
				finish();
			}else if (msg.what == 101){
				finish();
			}else if (msg.what == 102){
				finish();
			}
		}
	};
	

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		super.onStatusSuccess(action, templateInfo);
		Log.e("mingguo", "action "+action+" success "+templateInfo);
		if (action.equals(mRejectRentAction)){
			Message msg = mHandler.obtainMessage();
			msg.what = 100;
			msg.obj = templateInfo;
			msg.sendToTarget();
		}else if (action.equals(mCancelAttrbuteAction)){
			Message msg = mHandler.obtainMessage();
			msg.what = 101;
			msg.obj = templateInfo;
			msg.sendToTarget();
		}else if (action.equals(mConfirmRentAttribute)){
			Message msg = mHandler.obtainMessage();
			msg.what = 102;
			msg.obj = templateInfo;
			msg.sendToTarget();
		}
	}
	
	
	
	
}
