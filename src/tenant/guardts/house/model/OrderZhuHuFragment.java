package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import tenant.guardts.house.ApplyForCheckoutActivity;
import tenant.guardts.house.HouseOrderDetailsActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;
import tenant.guardts.house.util.UtilTool;

public class OrderZhuHuFragment extends BaseFragment{
	
	private Context mContext;
	private View mRootView;
	private ListView mlistView;
	private UniversalAdapter mAdapter;
	private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private HoursePresenter mPresent;
	//private LinearLayout mContentLayout;
	private TextView mNoContent;
	private String mUserName = null;
	private String mRentHistoryAction = "http://tempuri.org/GetRentHistory";
	private String mCancelAttrbuteAction = "http://tempuri.org/CancelRentAttribute";
	private String mExpireOrderAction = "http://tempuri.org/ExpiredOrder";
	private String mConfirmCheckOutAction = "http://tempuri.org/ConfirmCheckOut";// 确认退房
	
	private int mCurrentPosition = 0, mExpirePosition = -1, mCheckoutPosition = -1;
	private long mTimeTag ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		
		mPresent = new HoursePresenter(mContext, OrderZhuHuFragment.this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.w("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.house_history_zufang_fragment, container, false);
		initView();
		//initData();
		return mRootView;
	}
	
	public void refreshData(){
		initData();
	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshData();
	}
	
	@Override  
	 public void onDestroy() {  
	        updateTimeHandler.removeCallbacksAndMessages(null);  
	        super.onDestroy();  
	 }  

	private void initView(){
		mlistView = (ListView)mRootView.findViewById(R.id.id_fragment_house_listview);
		mNoContent = (TextView)mRootView.findViewById(R.id.id_frament_house_no_cotent);
		initAdapter();
		mlistView.setAdapter(mAdapter);
		mlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
				intent.putExtra("order_detail", mHouseInfoList.get(position));
				intent.putExtra("detail_type", "renter");
				Log.w("mingguo", "password  "+mHouseInfoList.get(position).getDoorPassword());
				startActivity(intent);
			}
		});
	}
	
	private Handler updateTimeHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			boolean isUpdate = false;
			for (int i = 0; i < mHouseInfoList.size(); i++){
				if (mHouseInfoList.get(i).getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_PAY)){
					mHouseInfoList.get(i).setCurrentDate(mHouseInfoList.get(i).getCurrentdDate() + 1000L);
					mHouseInfoList.get(i).setShowTimeDownTime(updateTimeTextView(mHouseInfoList.get(i).getOrderExpiredDate() - mHouseInfoList.get(i).getCurrentdDate(),
							mHouseInfoList.get(i).getHouseOrderId(), i));
					isUpdate = true;
				}else if (mHouseInfoList.get(i).getHouseStatus().equals(CommonUtil.ORDER_STATUS_SUBMITT)){
					mHouseInfoList.get(i).setShowTimeDownTime("请联系房主尽快确认订单");
				}else{
					mHouseInfoList.get(i).setShowTimeDownTime("");
				}
			}
			
			if (isUpdate){
				needUpdateTimeDownTime();
				Log.w("mingguo", "update data");
				updateTimeHandler.sendEmptyMessageDelayed(800, 1000);
			}else{
				mAdapter.notifyDataSetChanged();
			}
		}  
    	
    };
	
    private void needUpdateTimeDownTime(){
		int firstShowPositon = mlistView.getFirstVisiblePosition();
		int lastShowPosition = mlistView.getLastVisiblePosition();
		for (int index = firstShowPositon; index <= lastShowPosition; index++){
			if (mHouseInfoList.get(index).getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_PAY)){
				mAdapter.notifyDataSetChanged();
//				timeHandler.sendEmptyMessage(900);
//				mTimeTag = System.currentTimeMillis();
				break;
			}
		}
	}
    
	 
    private String updateTimeTextView(long times_remain, String orderId, int position) {  
    	if (times_remain <= 0) {  
            expireHouseRequest(orderId);
            mExpirePosition = position;
            return "超时未支付订单已过期";  
        }  
        //秒钟  
        long time_second = (times_remain/1000)%60;  
        String str_second;  
        if (time_second < 10) {  
            str_second = "0" + time_second;  
        } else {  
            str_second = "" + time_second;  
        }  
          
        long time_temp = ((times_remain / 1000) - time_second) / 60;  
        //分钟  
        long time_minute = time_temp % 60;  
        String str_minute;  
        if (time_minute < 10) {  
            str_minute = "0" + time_minute;  
        } else {  
            str_minute = "" + time_minute;  
        }  
          
        time_temp = (time_temp - time_minute) / 60;  
        //小时  
        long time_hour = time_temp;  
        String str_hour;  
        if (time_hour < 10) {  
            str_hour = "0" + time_hour;  
        } else {  
            str_hour = "" + time_hour;  
        }  
        return (str_minute+"分"+str_second+"秒 未支付订单自动取消"); 
        
        
    }  

	private void updateOrderStatusView(final UniversalViewHolder holder, final HouseInfoModel info){
		View holderView = holder.getConvertView();
		TextView status = (TextView)holderView.findViewById(R.id.id_zufang_item_status);
		Button button1 = (Button)holderView.findViewById(R.id.id_order_button_status1);
		Button button2 = (Button)holderView.findViewById(R.id.id_order_button_status2);
		Button button3 = (Button)holderView.findViewById(R.id.id_order_button_status3);
		if (info.getHouseStatus().equals(CommonUtil.ORDER_STATUS_SUBMITT)){
			status.setText("待确认");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setVisibility(View.INVISIBLE);
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button2.setText("查看详情");
			button3.setText("取消订单");
			button2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
					intent.putExtra("order_detail", info);
					intent.putExtra("detail_type", "renter");
					
					startActivity(intent);
				}
			});
			button3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showCancelOrderDialog(holder.getPosition(), info.getHouseOrderId());
				}
			});
		}else if (info.getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_PAY)){
			status.setText("待支付");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button2.setText("立即付款");
			button1.setVisibility(View.INVISIBLE);
			button2.setVisibility(View.INVISIBLE);
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button3.setText("立即付款");
			button1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
					intent.putExtra("order_detail", info);
					intent.putExtra("detail_type", "renter");
					startActivity(intent);
				}
			});
			button2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CommonUtil.mPayHouseOrderId = info.getHouseOrderId();
					Intent payIntent = new Intent(mContext, tenant.guardts.house.wxapi.HousePayActivity.class);
					payIntent.putExtra("pay_price", info.getHousePrice());
					payIntent.putExtra("owner_idcard", info.getHouseOwnerIdcard());
					///////////
					payIntent.putExtra("renter_idcard", info.getRenterIdcard());
					payIntent.putExtra("orderID", info.getHouseOrderId());
					payIntent.putExtra("rentNO", info.getHouseId());
					payIntent.putExtra("orderCreatedDate", info.getOrderCreatedDate());
					
					
					
					startActivity(payIntent);
				}
			});
			button3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//showCancelOrderDialog(holder.getPosition(), info.getHouseOrderId());
					CommonUtil.mPayHouseOrderId = info.getHouseOrderId();
					Intent payIntent = new Intent(mContext, tenant.guardts.house.wxapi.HousePayActivity.class);
					payIntent.putExtra("pay_price", info.getHousePrice());
					payIntent.putExtra("owner_idcard", info.getHouseOwnerIdcard());
					payIntent.putExtra("renter_idcard", info.getRenterIdcard());
					payIntent.putExtra("orderID", info.getHouseOrderId());
					payIntent.putExtra("rentNO", info.getHouseId());
					payIntent.putExtra("orderCreatedDate", info.getOrderCreatedDate());
					startActivity(payIntent);
				}
			});
		}else if (info.getHouseStatus().equals(CommonUtil.ORDER_STATUS_HAS_PAYED)){
			status.setText("已支付");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setText("申请退房");
			button2.setVisibility(View.INVISIBLE);
			button2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					
				}
			});
			button3.setText("查看详情");
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
					intent.putExtra("order_detail", info);
					intent.putExtra("detail_type", "renter");
					startActivity(intent);
				}
			});
		}else if (info.getHouseStatus().equals("3")){
			status.setText("待评价");
			status.setTextColor(Color.parseColor("#8be487"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setText("查看详情");
			button3.setText("查看详情");
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
					intent.putExtra("order_detail", info);
					intent.putExtra("detail_type", "renter");
					startActivity(intent);
				}
			});
		}else if (info.getHouseStatus().equals(CommonUtil.ORDER_STATUS_CANCELED)){
			status.setText("已取消");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setText("查看详情");
			button2.setVisibility(View.INVISIBLE);
			button3.setText("查看详情");
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
					intent.putExtra("order_detail", info);
					intent.putExtra("detail_type", "renter");
					startActivity(intent);
				}
			});
		}else if (info.getHouseStatus().equals(CommonUtil.ORDER_STATUS_REJECTED)){
			status.setText("已拒绝");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setVisibility(View.INVISIBLE);
			button3.setText("查看详情");
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
					intent.putExtra("order_detail", info);
					intent.putExtra("detail_type", "renter");
					startActivity(intent);
				}
			});
		}else if (info.getHouseStatus().equals(CommonUtil.ORDER_STATUS_EXPIRED)){
			status.setText("已过期");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setVisibility(View.INVISIBLE);
			button3.setText("查看详情");
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
					intent.putExtra("order_detail", info);
					intent.putExtra("detail_type", "owner");
					startActivity(intent);
				}
			});
		}else if (info.getHouseStatus().equals(CommonUtil.ORDER_STATUS_CHECKOUTED)){
			status.setText("已退房");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setVisibility(View.INVISIBLE);
			button3.setText("立即评价");
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			button3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
//					intent.putExtra("order_detail", info);
//					intent.putExtra("detail_type", "owner");
//					startActivity(intent);
				}
			});
		}else if (info.getHouseStatus().equals(CommonUtil.ORDER_STATUS_NEED_CHECKOUT)){
			status.setText("待退房");
			status.setTextColor(Color.parseColor("#de6262"));
			button1.setText("查看详情");
			button1.setVisibility(View.INVISIBLE);
			button2.setVisibility(View.INVISIBLE);
			button3.setText("查看详情");
			button3.setTextColor(Color.parseColor("#337ffd"));
			button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
			if (!TextUtils.isEmpty(info.getCheckOutPerson())) {
				if (info.getCheckOutPerson().equals(CommonUtil.mUserLoginName)) {
					button3.setText("查看详情");
					Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
					intent.putExtra("order_detail", info);
					intent.putExtra("detail_type", "owner");
					startActivity(intent);
				} else {
					button3.setText("确认退房");
					button3.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mCheckoutPosition = holder.getPosition();
							initAlertDialog(mCheckoutPosition);// 确认退房
						}
					});
				}
			}
		}
	}
	
	protected void initAlertDialog(final int position) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setTitle("是否确认退房？");
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();

			}
		});
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String mRRAID = mHouseInfoList.get(position).getHouseOrderId();
				if (!TextUtils.isEmpty(mRRAID)) {
					confirmCheckOut(mRRAID);
				}

			}
		});

		builder.show();
	}
    
    
    private void confirmCheckOut(String rraID) {
		String url = CommonUtil.mUserHost + "Services.asmx?op=ConfirmCheckOut";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mConfirmCheckOutAction));
		rpc.addProperty("rraId", rraID);
		mPresent.readyPresentServiceParams(getActivity(), url, mConfirmCheckOutAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void initAdapter(){
		mAdapter = new UniversalAdapter<HouseInfoModel>(mContext, R.layout.house_fragment_zufang_list_item, mHouseInfoList) {

			@Override
			public void convert(final UniversalViewHolder holder, final HouseInfoModel info) {
				View holderView = holder.getConvertView();
				TextView addressText = (TextView)holderView.findViewById(R.id.id_history_address);
				TextView endTime = (TextView)holderView.findViewById(R.id.id_order_end_time);
				TextView money = (TextView)holderView.findViewById(R.id.id_order_monkey_input);
				TextView timeDown = (TextView)holderView.findViewById(R.id.id_order_time_down);
				addressText.setText(info.getHouseAddress());
				endTime.setText(info.getHouseEndTime());
				money.setText(info.getHousePrice());
				timeDown.setText(info.getShowTimeDownTime());
				updateOrderStatusView(holder, info);
			}
		};
	}
	
	private void initData(){
		getHouseHistoryData();
	}
	
	private void showCancelOrderDialog(final int id, final String houseId) {  
		
		  AlertDialog.Builder builder =new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("取消订单");
		  builder.setMessage("您确认要取消该订单吗？");
		  builder.setIcon(android.R.drawable.ic_dialog_info);
		  builder.setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {
		         @Override  
		  
		         public void onClick(DialogInterface dialog, int which) {
					 cancelRentAttributeInfo(houseId);
		        	 
		         }  
			
		});
		builder.setNegativeButton(getString(R.string.button_cancel),new DialogInterface.OnClickListener() {
			  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {
	  
	  
	         }
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	private void getHouseHistoryData(){
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentHistory";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentHistoryAction));
		rpc.addProperty("idCard", CommonUtil.mRegisterIdcard);
		mPresent.readyPresentServiceParams(getActivity(), url, mRentHistoryAction, rpc);
		mPresent.startPresentServiceTask(true);
	}
	
	private void cancelRentAttributeInfo(String id){
		String url = CommonUtil.mUserHost+"Services.asmx?op=CancelRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mCancelAttrbuteAction));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(getActivity(), url, mCancelAttrbuteAction, rpc);
		mPresent.startPresentServiceTask(true);
	}
	
	private void expireHouseRequest(String id){
		String url = CommonUtil.mUserHost+"Services.asmx?op=ExpiredOrder";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mExpireOrderAction));
		rpc.addProperty("rraId", id);
		mPresent.readyPresentServiceParams(getActivity(), url, mExpireOrderAction, rpc);
		mPresent.startPresentServiceTask(true);
	}

	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 100){
				parseUserHouseInfo((String)msg.obj);
				if (mHouseInfoList.size() == 0){
					mNoContent.setText("暂无租房历史");
					mNoContent.setVisibility(View.VISIBLE);
				}else{
					//mContentLayout.setVisibility(View.VISIBLE);
					mNoContent.setVisibility(View.INVISIBLE);
					mAdapter.notifyDataSetChanged();
				}
			}else if (msg.what == 101){
				parseUserHouseInfo((String)msg.obj);
				if (mHouseInfoList.size() == 0){
					mNoContent.setText("暂无租房历史");
					mNoContent.setVisibility(View.VISIBLE);
				}else{
					//mContentLayout.setVisibility(View.VISIBLE);
					mNoContent.setVisibility(View.INVISIBLE);
					Log.w("housefragment", "house list  "+mHouseInfoList.size());
					mAdapter.notifyDataSetChanged();
				}
			}else if (msg.what == 103){
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					String ret = object.optString("ret");
					if (ret != null){
						if (ret.equals("0")){
							if (mExpirePosition != -1){
								mHouseInfoList.get(mExpirePosition).setHouseStatus(CommonUtil.ORDER_STATUS_EXPIRED);
							}
							
							mAdapter.notifyDataSetChanged();
						}else{
							GlobalUtil.shortToast(mContext, "订单过期失败，请重试！", getResources().getDrawable(R.drawable.ic_dialog_no));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (msg.what == 104){
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					String ret = object.optString("ret");
					if (ret != null){
						if (ret.equals("0")){
							if (mCheckoutPosition != -1){
								mHouseInfoList.get(mCheckoutPosition).setHouseStatus(CommonUtil.ORDER_STATUS_EXPIRED);
							}
							mAdapter.notifyDataSetChanged();
						}else{
							GlobalUtil.shortToast(mContext, "退房失败，请重试！", getResources().getDrawable(R.drawable.ic_dialog_no));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	private  void parseUserHouseInfo(String value) {
		mHouseInfoList.clear();
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				Log.w("mingguo", "parse house info "+array.length());
				for (int item = 0; item < array.length(); item++){
					
					JSONObject itemJsonObject = array.optJSONObject(item);
					HouseInfoModel houseModel = new HouseInfoModel();
					houseModel.setHouseStatus(itemJsonObject.optString("RRAStatus"));
//					if (!itemJsonObject.optString("RRAStatus").equals(CommonUtil.ORDER_STATUS_EXPIRED)){
//						houseModel.setHouseStatus(CommonUtil.ORDER_STATUS_NEED_PAY);
//					}else{
//						houseModel.setHouseStatus(itemJsonObject.optString("RRAStatus"));
//					}
					houseModel.setHouseAddress(itemJsonObject.optString("RAddress"));
					houseModel.setHousePrice(itemJsonObject.optString("RRentPrice"));
					houseModel.setHouseTotalFloor(itemJsonObject.optString("RTotalFloor"));
					houseModel.setHouseEndTime(itemJsonObject.optString("EndDate"));
					houseModel.setHouseStartTime(itemJsonObject.optString("StartDate"));
					houseModel.setHouseType(itemJsonObject.optString("RRoomTypeDesc"));
					houseModel.setHouseAvailable(itemJsonObject.optBoolean("Available"));
					houseModel.setHouseId(itemJsonObject.optString("RentNO"));
					houseModel.setHouseOwnerName(itemJsonObject.optString("ROwner"));
					houseModel.setHouseOwnerIdcard(itemJsonObject.optString("RIDCard"));
					houseModel.setHouseOrderId(itemJsonObject.optString("RRAID"));
					houseModel.setHouseOwnerPhone(itemJsonObject.optString("ROwnerTel"));
					houseModel.setHouseContactName(itemJsonObject.optString("RRAContactName"));
					houseModel.setHouseContactPhone(itemJsonObject.optString("RRAContactTel"));
					
					houseModel.setDoorPassword(itemJsonObject.optString("RRANationName"));

					houseModel.setRenterIdcard(itemJsonObject.optString("RRAIDCard"));//租客身份证
					houseModel.setOrderCreatedDateStamp(UtilTool.DateTimeToStamp(itemJsonObject.optString("CreatedOn")));
					houseModel.setOrderCreatedDate(itemJsonObject.optString("RRACreatedDate"));//下单时间
					houseModel.setOrderExpiredDate(UtilTool.DateTimeToStamp(itemJsonObject.optString("ExpireDate"))); 
					houseModel.setCurrentDate(UtilTool.DateTimeToStamp(itemJsonObject.optString("SysDate")));  //当前时间
					
//					houseModel.setOrderCreatedDate(1507776010000l);//下单时间
//					houseModel.setOrderExpiredDate(1507777150000l);//过期时间
//					houseModel.setCurrentDate(1507776190000l);  //当前时间

					houseModel.setRenterIdcard(itemJsonObject.optString("RRAIDCard"));//租客身份证
					houseModel.setCheckOutPerson(itemJsonObject.optString("RRACheckOutPerson"));//退房申请人
					mHouseInfoList.add(houseModel);
				}
				if (mHouseInfoList.size() > 0){
					updateTimeHandler.removeMessages(800);
					updateTimeHandler.sendEmptyMessage(800);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.e("mingguo", "on status success action  "+action+"  return value "+templateInfo);
		super.onStatusSuccess(action, templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mRentHistoryAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mCancelAttrbuteAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 101;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mExpireOrderAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 103;
				msg.obj = templateInfo;
				msg.sendToTarget();
			} else if (action.equals(mConfirmCheckOutAction)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 104;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
	}


}
