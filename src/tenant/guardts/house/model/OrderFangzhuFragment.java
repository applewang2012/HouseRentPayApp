package tenant.guardts.house.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import tenant.guardts.house.HouseOrderDetailsActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.GlobalUtil;

public class OrderFangzhuFragment extends BaseFragment{
	
	private Context mContext;
	private View mRootView;
	private ListView mlistView;
	private UniversalAdapter mAdapter;
	private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private HoursePresenter mPresent;
	//private LinearLayout mContentLayout;
	private TextView mNoContent;
	private String mUserName = null;
	private String mRentHistoryAction = "http://tempuri.org/GetRentOwnerHistory";
	private String mConfirmRentAttribute = "http://tempuri.org/ConfirmRentAttribute";
	private String mRejectRentAction = "http://tempuri.org/RejectRentAttribute";
	private int mCurrentPosition = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		
		mPresent = new HoursePresenter(mContext, OrderFangzhuFragment.this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.house_history_zufang_fragment, container, false);
		initView();
		//initData();
		return mRootView;
	}
	
	private void initView(){
		mlistView = (ListView)mRootView.findViewById(R.id.id_fragment_house_listview);
		mNoContent = (TextView)mRootView.findViewById(R.id.id_frament_house_no_cotent);
		initAdapter();
		mlistView.setAdapter(mAdapter);
		mlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
				intent.putExtra("order_detail", mHouseInfoList.get(position));
				intent.putExtra("detail_type", "owner");
				startActivity(intent);
			}
		});
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

	private void initAdapter(){
		mAdapter = new UniversalAdapter<HouseInfoModel>(mContext, R.layout.house_fragment_zufang_list_item, mHouseInfoList) {

			@Override
			public void convert(final UniversalViewHolder holder, final HouseInfoModel info) {
				View holderView = holder.getConvertView();
				TextView addressText = (TextView)holderView.findViewById(R.id.id_history_address);
				TextView status = (TextView)holderView.findViewById(R.id.id_zufang_item_status);
				TextView endTime = (TextView)holderView.findViewById(R.id.id_order_end_time);
				TextView money = (TextView)holderView.findViewById(R.id.id_order_monkey_input);
				Button button1 = (Button)holderView.findViewById(R.id.id_order_button_status1);
				Button button2 = (Button)holderView.findViewById(R.id.id_order_button_status2);
				Button button3 = (Button)holderView.findViewById(R.id.id_order_button_status3);
				addressText.setText(info.getHouseAddress());
				endTime.setText(info.getHouseEndTime());
				money.setText(info.getHousePrice());
				if (info.getHouseStatus().equals("0")){
					status.setText("待确认");
					status.setTextColor(Color.parseColor("#de6262"));
					button1.setText("查看详情");
					button1.setVisibility(View.INVISIBLE);
					button2.setVisibility(View.INVISIBLE);
					button1.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
							intent.putExtra("order_detail", info);
							intent.putExtra("detail_type", "owner");
							startActivity(intent);
						}
					});
					button3.setTextColor(Color.parseColor("#337ffd"));
					button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
					button2.setText("确认订单");
					button3.setText("确认订单");
					button3.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							mCurrentPosition = holder.getPosition();
							confirmRentAttributeInfo(info.getHouseOrderId());
						}
					});
					button3.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//mCurrentPosition = holder.getPosition();
							showPositiveOrderDialog(holder.getPosition(), info.getHouseOrderId());
						}
					});
				}else if (info.getHouseStatus().equals("1")){
					status.setText("待支付");
					status.setTextColor(Color.parseColor("#de6262"));
					button1.setVisibility(View.INVISIBLE);
					button2.setVisibility(View.INVISIBLE);
					button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
					button3.setTextColor(Color.parseColor("#337ffd"));
					button3.setText("查看详情");
					button3.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
							intent.putExtra("order_detail", info);
							intent.putExtra("detail_type", "owner");
							startActivity(intent);
						}
					});
				}else if (info.getHouseStatus().equals("2")){
					status.setText("已支付");
					status.setTextColor(Color.parseColor("#de6262"));
					button1.setVisibility(View.INVISIBLE);
					button2.setVisibility(View.INVISIBLE);
					button3.setBackgroundResource(R.drawable.item_shape_no_solid_corner_press);
					button3.setTextColor(Color.parseColor("#337ffd"));
					button3.setText("查看详情");
					button3.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(), HouseOrderDetailsActivity.class);
							intent.putExtra("order_detail", info);
							intent.putExtra("detail_type", "owner");
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
							intent.putExtra("detail_type", "owner");
							startActivity(intent);
						}
					});
				}else if (info.getHouseStatus().equals("8")){
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
							intent.putExtra("detail_type", "owner");
							startActivity(intent);
						}
					});
				}else if (info.getHouseStatus().equals("9")){
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
							intent.putExtra("detail_type", "owner");
							startActivity(intent);
						}
					});
				}
			}
		};
	}
	
	private void showPositiveOrderDialog(final int id, final String houseId) {  
		
		  AlertDialog.Builder builder =new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("确认订单");
		  builder.setMessage("您确认信息无误，确认该订单吗？");
		  builder.setIcon(android.R.drawable.ic_dialog_info);
		  builder.setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {
		         @Override  
		  
		         public void onClick(DialogInterface dialog, int which) {
		        	 mCurrentPosition = id;
					//rejectRentAttributeInfo(houseId);
					confirmRentAttributeInfo(houseId);
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
	
	private void initData(){
		getHouseHistoryData();
	}
	
	private void getHouseHistoryData(){
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentOwnerHistory";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentHistoryAction));
		rpc.addProperty("idCard", CommonUtil.mRegisterIdcard);
		mPresent.readyPresentServiceParams(getActivity(), url, mRentHistoryAction, rpc);
		mPresent.startPresentServiceTask(true);
	}
	
	private void confirmRentAttributeInfo(String id){
		String url = CommonUtil.mUserHost+"Services.asmx?op=ConfirmRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mConfirmRentAttribute));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(getActivity(), url, mConfirmRentAttribute, rpc);
		mPresent.startPresentServiceTask(true);
	}

	private void rejectRentAttributeInfo(String id){
		String url = CommonUtil.mUserHost+"Services.asmx?op=RejectRentAttribute";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRejectRentAction));
		rpc.addProperty("id", id);
		mPresent.readyPresentServiceParams(getActivity(), url, mRejectRentAction, rpc);
		mPresent.startPresentServiceTask(true);
	}
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 100){
				parseUserHouseInfo((String)msg.obj);
				if (mHouseInfoList.size() == 0){
					mNoContent.setText("暂无出租历史");
					mNoContent.setVisibility(View.VISIBLE);
				}else{
					//mContentLayout.setVisibility(View.VISIBLE);
					mNoContent.setVisibility(View.INVISIBLE);
					Log.w("mingguo", "house list  "+mHouseInfoList.size());
					mAdapter.notifyDataSetChanged();
				}
			}else if (msg.what == 101){
				try {
					JSONObject object = new JSONObject((String)msg.obj);
					String ret = object.optString("ret");
					if (ret != null){
						if (ret.equals("0")){
							mHouseInfoList.get(mCurrentPosition).setHouseStatus("1");
							mAdapter.notifyDataSetChanged();
						}else{
							GlobalUtil.shortToast(mContext, "订单确认失败，请重试！", getResources().getDrawable(R.drawable.ic_dialog_no));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (msg.what == 102){
				parseUserHouseInfo((String)msg.obj);
				if (mHouseInfoList.size() == 0){
					mNoContent.setText("暂无出租历史");
					mNoContent.setVisibility(View.VISIBLE);
				}else{
					//mContentLayout.setVisibility(View.VISIBLE);
					mNoContent.setVisibility(View.INVISIBLE);
					Log.w("mingguo", "house list  "+mHouseInfoList.size());
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	};
	
	private  void parseUserHouseInfo(String value) {
		mHouseInfoList.clear();
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				Log.i("mingguo", "parse house info "+array.length());
				for (int item = 0; item < array.length(); item++){
					
					JSONObject itemJsonObject = array.optJSONObject(item);
					HouseInfoModel houseModel = new HouseInfoModel();
					houseModel.setHouseStatus(itemJsonObject.optString("RRAStatus"));
					houseModel.setHouseAddress(itemJsonObject.optString("RAddress"));
					houseModel.setHousePrice(itemJsonObject.optString("RRentPrice"));
					houseModel.setHouseTotalFloor(itemJsonObject.optString("RTotalFloor"));
					houseModel.setHouseEndTime(itemJsonObject.optString("EndDate"));
					houseModel.setHouseStartTime(itemJsonObject.optString("StartDate"));
					houseModel.setHouseType(itemJsonObject.optString("RRoomTypeDesc"));
					houseModel.setHouseAvailable(itemJsonObject.optBoolean("Available"));
					houseModel.setHouseId(itemJsonObject.optString("RentNO"));
					houseModel.setHouseOwnerName(itemJsonObject.optString("ROwner"));
					houseModel.setHouseOwnerIdcard(itemJsonObject.optString("RIDCard"));//房主身份证
					houseModel.setHouseOrderId(itemJsonObject.optString("RRAID"));
					houseModel.setHouseOwnerPhone(itemJsonObject.optString("ROwnerTel"));
					houseModel.setHouseContactName(itemJsonObject.optString("RRAContactName"));
					houseModel.setHouseContactPhone(itemJsonObject.optString("RRAContactTel"));
					houseModel.setRenterIdcard(itemJsonObject.optString("RRAIDCard"));//租客身份证
					mHouseInfoList.add(houseModel);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		Log.e("mingguo", "on status success action  "+action+"  return value "+templateInfo);
		super.onStatusSuccess(action, templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mRentHistoryAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mConfirmRentAttribute)){
				Message msg = mHandler.obtainMessage();
				msg.what = 101;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}else if (action.equals(mRejectRentAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 102;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}
		
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusStart(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	
}
