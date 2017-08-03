package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.bannerview.CircleFlowIndicator;
import tenant.guardts.house.bannerview.ImagePagerAdapter;
import tenant.guardts.house.bannerview.ViewFlow;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
//��Ӱ�ʱ���import android.support.v4.app.Fragment; 
public class RentToHouseActivity extends BaseActivity implements DataStatusInterface, OnItemClickListener, OnItemLongClickListener{
	

	
	private Context mContext;
	private ListView mlistView;
	private View mLoadingView;
	private UniversalAdapter mAdapter;
	private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private HoursePresenter mPresent;
	private LinearLayout mContentLayout;
	private TextView mNoContent;
	private String mUserName = null;
	private String mGetHouseInfoAction = "http://tempuri.org/GetHouseInfoByLoginName";
	private String mDeleteHouseInfoAction = "http://tempuri.org/DeleteHouseInfo";
	private int mDeleteIndex = 0;
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mFlowIndicator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_rent_to_house_list_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView titlebar = (TextView)findViewById(R.id.id_titlebar);
		titlebar.setText("我要出租");
		mContext = getApplicationContext();
		mUserName = CommonUtil.mUserLoginName;
		initView();
		initData();
	}

	
	private void initView(){
		mViewFlow = (ViewFlow) findViewById(R.id.id_rent_to_viewflow);
		mFlowIndicator = (CircleFlowIndicator) findViewById(R.id.id_rent_to_viewflow_indicator);
		mlistView = (ListView)findViewById(R.id.id_fragment_house_listview);
		mContentLayout = (LinearLayout)findViewById(R.id.id_frament_house_cotent);
		mNoContent = (TextView)findViewById(R.id.id_frament_house_no_cotent);
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		mContentLayout.setVisibility(View.INVISIBLE);
		initAdapter();
		mlistView.setAdapter(mAdapter);
		mlistView.setOnItemClickListener(this);
		mlistView.setOnItemLongClickListener(this);
		
		initBanner();
	}
	
	private void initBanner() {
		List<Integer> localImage = new ArrayList<>();
		localImage.add(R.drawable.house_rent_to_house_banner1);
		localImage.add(R.drawable.house_rent_to_house_banner2);
		localImage.add(R.drawable.house_rent_to_house_banner3);
		
		
		mViewFlow.setAdapter(new ImagePagerAdapter(RentToHouseActivity.this, localImage,
				null, null).setInfiniteLoop(true));
		mViewFlow.setmSideBuffer(localImage.size()); // 实际图片张数，
														// 我的ImageAdapter实际图片张数为3
		mFlowIndicator.setIndicatorCount(localImage.size());
		
		mViewFlow.setFlowIndicator(mFlowIndicator);
		mViewFlow.setTimeSpan(2000);
		mViewFlow.setSelection(localImage.size() * 1000); // 设置初始位置
		mViewFlow.startAutoFlowTimer(); // 启动自动播放
		mFlowIndicator.requestLayout();
		mFlowIndicator.invalidate();
	}
	
	private void initData(){
		mPresent = new HoursePresenter(mContext, RentToHouseActivity.this);
		getHouseInfo();
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		showLoadingView();
//		mContentLayout.setVisibility(View.INVISIBLE);
//		mAdapter.notifyDataSetChanged();
	}

	
	private void initAdapter(){
		mAdapter = new UniversalAdapter<HouseInfoModel>(mContext, R.layout.rent_to_house_list_item_layout, mHouseInfoList) {

			@Override
			public void convert(UniversalViewHolder holder, HouseInfoModel info) {
				View holderView = holder.getConvertView();
				TextView addressTextView = (TextView)holderView.findViewById(R.id.id_rent_to_house_item_address);
				TextView typeTextView = (TextView)holderView.findViewById(R.id.id_rent_to_house_item_type);
				TextView directionTextView = (TextView)holderView.findViewById(R.id.id_rent_to_house_item_direction);
				TextView floorTextView = (TextView)holderView.findViewById(R.id.id_rent_to_house_item_floor);
				//TextView statusTextView = (TextView)holderView.findViewById(R.id.id_house_status);
				addressTextView.setText(info.getHouseAddress());
				typeTextView.setText(info.getHouseType());
				directionTextView.setText(info.getHouseDirection());
				floorTextView.setText(info.getHouseCurrentFloor()+"/"+info.getHouseTotalFloor()+getString(R.string.house_floor));
//				statusTextView.setText(info.getHouseStatus());
//				if (info.getHouseAvailable()){
//					statusTextView.setTextColor(Color.parseColor("#0b6cfe"));
//				}else{
//					
//				}
			}
		};
	}
	
	private void showPublicAttributeDialog(final int position){
		new AlertDialog.Builder(mContext).setTitle(getString(R.string.arribute_house))//���öԻ������  
		  
	     .setMessage(getString(R.string.arribute_house_whether))//������ʾ������  
	  
	     .setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {//���ȷ����ť  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
//	        	 Intent intent = new Intent(mContext, AddRentAttributeActivity.class);
//	        	 intent.putExtra("house_id", mHouseInfoList.get(position).getHouseId());
//	        	 intent.putExtra("user_name", mUserName);
//	        	 intent.putExtra("owner_name", mHouseInfoList.get(position).getHouseOwnerName());
//	        	 intent.putExtra("owner_id", mHouseInfoList.get(position).getHouseOwnerIdcard());
//	        	 startActivityForResult(intent, 100);
	         }  
	  
	     }).setNegativeButton(getString(R.string.button_cancel),new DialogInterface.OnClickListener() {//��ӷ��ذ�ť  
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�  
	  
	             // TODO Auto-generated method stub  
	  
	             Log.i("alertdialog"," �뱣�����ݣ�");  
	  
	         }  
	  
	     }).show(); 
	}
	
//	private void showDeleteHouseInfoDialog(final int position){
//		new AlertDialog.Builder(mContext).setTitle(getString(R.string.delete_house))//���öԻ������  
//		  
//	     .setMessage(getString(R.string.arribute_house_whether))//������ʾ������  
//	  
//	     .setPositiveButton(getString(R.string.button_ok),new DialogInterface.OnClickListener() {//���ȷ����ť  
//	         @Override  
//	  
//	         public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
//	        	 showLoadingView();
//	        	 deleteHouseInfo(mHouseInfoList.get(position).getHouseId());
//	         }  
//	  
//	     }).setNegativeButton(getString(R.string.button_cancel),new DialogInterface.OnClickListener() {//��ӷ��ذ�ť  
//	         @Override  
//	         public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�  
//	             Log.i("alertdialog"," �뱣�����ݣ�");  
//	         }  
//	     }).show();
//	}
	
	private void getHouseInfo(){
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=GetHouseInfoByLoginName";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetHouseInfoAction));
		rpc.addProperty("loginName", "apple");
		mPresent.readyPresentServiceParams(mContext, url, mGetHouseInfoAction, rpc);
		mPresent.startPresentServiceTask();
	}
	
	private void deleteHouseInfo(String houseNo){
		String url = "http://qxw2332340157.my3w.com/services.asmx?op=DeleteHouseInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mDeleteHouseInfoAction));
		rpc.addProperty("houseNo", houseNo);
		mPresent.readyPresentServiceParams(mContext, url, mDeleteHouseInfoAction, rpc);
		mPresent.startPresentServiceTask();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100){
				getAdapterListData(parseUserHouseInfo((String)msg.obj));
				dismissLoadingView();
				if (mHouseInfoList.size() == 0){
					mContentLayout.setVisibility(View.GONE);
					mNoContent.setVisibility(View.VISIBLE);
				}else{
					mContentLayout.setVisibility(View.VISIBLE);
					mNoContent.setVisibility(View.INVISIBLE);
					Log.w("housefragment", "house list  "+mHouseInfoList.size());
					mAdapter.notifyDataSetChanged();
				}
			}else if (msg.what == 101){
				dismissLoadingView();
				if ("true".equals((String)msg.obj)){
					mHouseInfoList.remove(mDeleteIndex);
					Log.w("housefragment", "delete house   "+mHouseInfoList.size());
					mAdapter.notifyDataSetChanged();
				}else{
					Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	public static List<HouseInfoModel> parseUserHouseInfo(String value) {
		List<HouseInfoModel> list = new ArrayList<>();
		try{
			JSONArray array = new JSONArray(value);
			if (array != null){
				Log.i("house", "parse house info "+array.length());
				for (int item = 0; item < array.length(); item++){
					
					JSONObject itemJsonObject = array.optJSONObject(item);
					HouseInfoModel houseModel = new HouseInfoModel();
					houseModel.setHouseAddress(itemJsonObject.optString("RAddress"));
					houseModel.setHouseDirection(itemJsonObject.optString("RDirectionDesc"));
					houseModel.setHouseTotalFloor(itemJsonObject.optString("RTotalFloor"));
					houseModel.setHouseCurrentFloor(itemJsonObject.optString("RFloor"));
					houseModel.setHouseType(itemJsonObject.optString("RRoomTypeDesc"));
					houseModel.setHouseStatus(itemJsonObject.optString("IsAvailable"));
					houseModel.setHouseAvailable(itemJsonObject.optBoolean("Available"));
					houseModel.setHouseId(itemJsonObject.optString("RentNO"));
					houseModel.setHouseOwnerName(itemJsonObject.optString("ROwner"));
					houseModel.setHouseOwnerIdcard(itemJsonObject.optString("RIDCard"));
					list.add(houseModel);
				}
			}
			Log.i("house", "for item  "+list.size());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}
	
	private void getAdapterListData(List<HouseInfoModel> list){
		if (list == null){
			return;
		}
		mHouseInfoList.clear();
		for (int index = 0; index < list.size(); index++){
			HouseInfoModel infoModel = new HouseInfoModel();
			infoModel.setHouseAddress(list.get(index).getHouseAddress());
			infoModel.setHouseDirection(list.get(index).getHouseDirection());
			infoModel.setHouseTotalFloor(list.get(index).getHouseTotalFloor());
			infoModel.setHouseCurrentFloor(list.get(index).getHouseCurrentFloor());
			infoModel.setHouseType(list.get(index).getHouseType());
			infoModel.setHouseStatus(list.get(index).getHouseStatus());
			infoModel.setHouseId(list.get(index).getHouseId());
			infoModel.setHouseAvailable(list.get(index).getHouseAvailable());
			infoModel.setHouseOwnerName(list.get(index).getHouseOwnerName());
			infoModel.setHouseOwnerIdcard(list.get(index).getHouseOwnerIdcard());
			mHouseInfoList.add(infoModel);
		}
	}

	private void showLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
        	if (imageView != null) {
        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate);
        		imageView.startAnimation(rotate);
        	}
		}
	}
	private void dismissLoadingView(){
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.INVISIBLE);
		}
	}

	
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		Log.v("mingguo", "on status success  action  "+action+"  info  "+templateInfo);
		if (action.equals(mGetHouseInfoAction)){
			Message msgMessage = mHandler.obtainMessage();
			msgMessage.what = 100;
			msgMessage.obj = templateInfo;
			msgMessage.sendToTarget();
		}else if (action.equals(mDeleteHouseInfoAction)){
			Message msgMessage = mHandler.obtainMessage();
			msgMessage.what = 101;
			msgMessage.obj = templateInfo;
			msgMessage.sendToTarget();
		}
		
	}

	@Override
	public void onStatusStart() {
		// TODO Auto-generated method stub
		Log.e("housefragment", "on start  ");
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		Log.e("housefragment", "error   "+error);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.e("housefragment", "item click position    "+position+"  list  "+mHouseInfoList.get(position).getHouseAvailable());
//		if (mHouseInfoList.get(position).getHouseAvailable()){
//			Toast.makeText(mContext, "已出租", Toast.LENGTH_SHORT).show();
//		}else{
//			showPublicAttributeDialog(position);
//		}
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Log.e("housefragment", "item long click position    "+position+"  list  "+mHouseInfoList.get(position).getHouseAvailable());
//		if (mHouseInfoList.get(position).getHouseAvailable()){
//			Toast.makeText(mContext, "已出租，无法删除", Toast.LENGTH_SHORT).show();
//		}else{
//			
//			//showDeleteHouseInfoDialog(position);
//			
//		}
		return true;
	}
	
}
