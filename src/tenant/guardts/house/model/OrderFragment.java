package tenant.guardts.house.model;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import tenant.guardts.house.R;
import tenant.guardts.house.presenter.HoursePresenter;

public class OrderFragment extends BaseFragment implements OnItemClickListener{
	

	
	private Context mContext;
	private View mRootView;
	private ListView mlistView;
	
	//private UniversalAdapter mAdapter;
	//private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private HoursePresenter mPresent;
	//private LinearLayout mContentLayout;
	private TextView mNoContent;
	private String mUserName = null;
	private String mRentHistoryAction = "http://tempuri.org/GetRentHistory";
	private OrderZhuHuFragment mZuFangFrament;
	private OrderFangzhuFragment mChuzuFragment;
	private RadioGroup mRadioGroup;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mPresent = new HoursePresenter(mContext, OrderFragment.this);
//		getActivity().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
//		TextView bartext  = (TextView) getActivity().getWindow().findViewById(R.id.id_titlebar);
//		bartext.setText("订单管理");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("fragmenttest", "homefragment onCreateView ");
		mRootView = inflater.inflate(R.layout.house_history_layout, container, false);
		initTitleBar();
		initView();

		//initData();
		return mRootView;
	}
	
	@SuppressLint("NewApi")
	private void initView(){
//		mlistView = (ListView)mRootView.findViewById(R.id.id_fragment_house_listview);
//		FrameLayout childFragmentContent = (FrameLayout)mRootView.findViewById(R.id.id_house_child_fragment_content);
		//mContentLayout = (LinearLayout)mRootView.findViewById(R.id.id_frament_house_cotent);
//		mNoContent = (TextView)mRootView.findViewById(R.id.id_frament_house_no_cotent);
//		mLoadingView = (View)mRootView.findViewById(R.id.id_data_loading);
		//mContentLayout.setVisibility(View.INVISIBLE);
//		initAdapter();
//		mlistView.setAdapter(mAdapter);
//		mlistView.setOnItemClickListener(this);
		
		mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.id_fragment_history_radiogrouop);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.id_frament_history_zufang_button:
					
					FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
					hideAllFragments(fragmentTransaction);
					if (mZuFangFrament == null){
						mZuFangFrament = new OrderZhuHuFragment();
						fragmentTransaction.add(R.id.id_house_child_fragment_content, mZuFangFrament);
						fragmentTransaction.commitAllowingStateLoss();
					}else{
						fragmentTransaction.show(mZuFangFrament);
						fragmentTransaction.commitAllowingStateLoss();
						mZuFangFrament.refreshData();
					}
					break;
				case R.id.id_frament_history_chuzu_button:
					
					FragmentTransaction chuzufragmentTransaction = getChildFragmentManager().beginTransaction();
					hideAllFragments(chuzufragmentTransaction);
					if (mChuzuFragment == null){
						mChuzuFragment = new OrderFangzhuFragment();
						chuzufragmentTransaction.add(R.id.id_house_child_fragment_content, mChuzuFragment);
						chuzufragmentTransaction.commitAllowingStateLoss();
					}else{
						chuzufragmentTransaction.show(mChuzuFragment);
						chuzufragmentTransaction.commitAllowingStateLoss();
						mChuzuFragment.refreshData();
					}
					break;
				}
			}
		});
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		if (mZuFangFrament == null){
			mZuFangFrament = new OrderZhuHuFragment();
			fragmentTransaction.add(R.id.id_house_child_fragment_content, mZuFangFrament);
			fragmentTransaction.commitAllowingStateLoss();
		}else{
			fragmentTransaction.show(mZuFangFrament);
			fragmentTransaction.commitAllowingStateLoss();
		}
	}
	
	private void hideAllFragments(FragmentTransaction transaction) {
		if (mChuzuFragment != null && !mChuzuFragment.isHidden()) {
			transaction.hide(mChuzuFragment);
		}
		if (mZuFangFrament != null && !mZuFangFrament.isHidden()) {
			transaction.hide(mZuFangFrament);
		}
		
	}

	private void initTitleBar(){
		
		View titlebarView = (View)mRootView.findViewById(R.id.id_common_title_bar);
		TextView titleText = (TextView) titlebarView.findViewById(R.id.id_titlebar);
		titleText.setText("订单管理");
		FrameLayout backButton = (FrameLayout)titlebarView.findViewById(R.id.id_titlebar_back);
		backButton.setVisibility(View.INVISIBLE);
	}
	
//	private void initAdapter(){
//		mAdapter = new UniversalAdapter<HouseInfoModel>(mContext, R.layout.house_fragment_zufang_list_item, mHouseInfoList) {
//
//			@Override
//			public void convert(UniversalViewHolder holder, HouseInfoModel info) {
//				View holderView = holder.getConvertView();
//				TextView addressText = (TextView)holderView.findViewById(R.id.id_history_address);
//				//TextView areaText = (TextView)holderView.findViewById(R.id.id_history_area);
//				TextView contactText = (TextView)holderView.findViewById(R.id.id_history_contacts);
//				TextView timeText = (TextView)holderView.findViewById(R.id.id_history_time);
//				addressText.setText(info.getHouseAddress());
//				//areaText.setText(info.getHouseArea()+" 平米");
//				contactText.setText(info.getHouseOwnerName()+" "+info.getHousePhone());
//				timeText.setText(info.getHouseStartTime()+"至"+info.getHouseEndTime());
//			}
//		};
//	}
	
//	private void initData(){
//		
//		getHouseHistoryData();
//	}
	
//	private void getHouseHistoryData(){
//		String url = CommonUtil.mUserHost+"Services.asmx?op=GetRentHistory";
//		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mRentHistoryAction));
//		rpc.addProperty("idCard", CommonUtil.mRegisterIdcard);
//		mPresent.readyPresentServiceParams(mContext, url, mRentHistoryAction, rpc);
//		mPresent.startPresentServiceTask(true);
//	}
//
//	private void showLoadingView(){
//		if (mLoadingView != null) {
//			mLoadingView.setVisibility(View.VISIBLE);
//        	ImageView imageView = (ImageView) mLoadingView.findViewById(R.id.id_progressbar_img);
//        	if (imageView != null) {
//        		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
//        		imageView.startAnimation(rotate);
//        	}
//		}
//	}
//	private void dismissLoadingView(){
//		if (mLoadingView != null) {
//			
//		}
//	}
	
//	private Handler mHandler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			
//			
//			if (msg.what == 100){
//				getAdapterListData((String)msg.obj);
//				if (mHouseInfoList.size() == 0){
//					//mContentLayout.setVisibility(View.GONE);
//					mNoContent.setVisibility(View.VISIBLE);
//				}else{
//					//mContentLayout.setVisibility(View.VISIBLE);
//					mNoContent.setVisibility(View.INVISIBLE);
//					Log.w("housefragment", "house list  "+mHouseInfoList.size());
//					mAdapter.notifyDataSetChanged();
//				}
//			}
//		}
//	};
//	
//	private void getAdapterListData(String value){
//		if (value == null){
//			return;
//		}
//		mHouseInfoList.clear();
//		JSONArray array;
//		try {
//			array = new JSONArray(value);
//			if (array != null){
//				Log.i("house", "parse house info "+array.length());
//				for (int item = 0; item < array.length(); item++){
//					JSONObject itemJsonObject = array.optJSONObject(item);
//					HouseInfoModel infoModel = new HouseInfoModel();
//					infoModel.setHouseAddress(itemJsonObject.optString("RAddress"));
//					infoModel.setHouseArea(itemJsonObject.optString("RRentArea"));
//					infoModel.setHouseOwnerName(itemJsonObject.optString("ROwner"));
//					infoModel.setHousePhone(itemJsonObject.optString("ROwnerTel"));
//					infoModel.setHouseStartTime(itemJsonObject.optString("StartDate"));
//					infoModel.setHouseEndTime(itemJsonObject.optString("EndDate"));
//					infoModel.setHouseId(itemJsonObject.optString("RentNO"));
//					mHouseInfoList.add(infoModel);
//				}
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	
	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		Log.e("mingguo", "success "+templateInfo);
//		if (action.equals(mRentHistoryAction)){
//			Message msg = mHandler.obtainMessage();
//			msg.what = 100;
//			msg.obj = templateInfo;
//			msg.sendToTarget();
//		}
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
//		Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
//		detailIntent.putExtra("rentNo", mHouseInfoList.get(position).getHouseId());
//		startActivity(detailIntent);
	}

	
}
