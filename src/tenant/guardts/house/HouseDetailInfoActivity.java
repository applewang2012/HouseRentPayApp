package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.gzt.faceid5sdk.DetectionAuthentic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.bannerview.CircleFlowIndicator;
import tenant.guardts.house.bannerview.ImagePagerAdapter;
import tenant.guardts.house.bannerview.ViewFlow;
import tenant.guardts.house.model.HouseImageInfo;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;

public class HouseDetailInfoActivity extends BaseActivity{

	private TextView mTitleBar;
	private View mLoadingView;
	private HoursePresenter mPresenter;
	private String mAddRentAction = "http://tempuri.org/AddRentRecord";
	private String mIdentifyUrl = "https://nid.sdtt.com.cn/AppRegSvr/thirdsysauthsvr/houseorder";
	private String mAppIDString = "0000004";
	private String mRandNum = null;
	//ret  1 desc SDT-HOUSE-3435333134303230313730333233313531373331343839
	private String mQueryStatusUrl = null;
//	private Map<String, String> mSelectedMap = new HashMap<>();
//	private Map<String, String> mOriginText = new HashMap<>();
//	private Map<String, String[]> mAllList = new HashMap<>();
	private String mHouseNo;
	private String mUsername;
	private String mOriginTypeText, mTypeIndex = null;
	private String mOwnerName;
	private String mOwnerIdcard;
	private String mHouseDetailAction = "http://tempuri.org/GetHouseDetailInfo";
	private String mHouseImageListAction = "http://tempuri.org/GetRentImageList";
	private String mRentNo = "";
	private String mfilePath;
	private HandlerThread myHandlerThread ;
	private Handler mSubHandler;
	private ArrayList<String> imageUrlList = new ArrayList<String>();
	private String mImageUrlPrefix = CommonUtil.mUserHost+"";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.house_detail_info_layout);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("房屋详情");
		initView();
		mRentNo = getIntent().getStringExtra("rentNo");
		//mRentNo = "888888888";
		Log.e("mingguo", "rent no  "+mRentNo);
		getHouseDetailInfoByHouseId(mRentNo);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		mHouseNo = getIntent().getStringExtra("house_id");
//		mUsername = getIntent().getStringExtra("user_name");
//		mOwnerName = getIntent().getStringExtra("owner_name");
//		mOwnerIdcard = getIntent().getStringExtra("owner_id");
//		mHouseId.setText(mHouseNo);
	}

	
	private void showAlertDialog(final TextView text,final String[] items) {  
		  AlertDialog.Builder builder =new AlertDialog.Builder(HouseDetailInfoActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		  builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTypeIndex = which+"";
				text.setText(mOriginTypeText +"   "+items[which]);
			}
		});
		builder.show();
}

	
	private TextView mHouseId;
	private TextView mRentName;
	private TextView mRentProperty;
	private TextView mRentReadMe;
	private TextView mRentPhone;
	private TextView mRentOrderId;
	private DetectionAuthentic authentic;
	private TextView mRentArea;
	private TextView mRentType;
	private TextView mRentDirection;
	private TextView mRentFloor;
	private TextView mRentAddress;
	private TextView mRentStatus;
	//private GridView mHouseInfoGridview;
	private List<HouseImageInfo> mDataList = new ArrayList<>();
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mFlowIndicator;
	
	private void initView(){
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		mLoadingView = (View)findViewById(R.id.id_data_loading);
		mLoadingView.setVisibility(View.INVISIBLE);
		mHouseId = (TextView)findViewById(R.id.id_rent_house_number);
		mRentArea = (TextView)findViewById(R.id.id_rent_house_area);
		mRentName = (TextView)findViewById(R.id.id_rent_house_name);
		mRentPhone = (TextView)findViewById(R.id.id_rent_house_phone);
		mRentProperty = (TextView)findViewById(R.id.id_rent_house_property);
		mRentType = (TextView)findViewById(R.id.id_rent_house_type);
		mRentDirection = (TextView)findViewById(R.id.id_rent_house_direction);
		mRentFloor = (TextView)findViewById(R.id.id_rent_house_floor);
		mRentAddress = (TextView)findViewById(R.id.id_rent_house_address);
		mRentStatus = (TextView)findViewById(R.id.id_rent_house_status);
		
//		mHouseInfoGridview = (GridView) findViewById(R.id.id_house_detail_info_image);	
//		mHouseInfoGridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mFlowIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
//		UniversalAdapter adapter = new UniversalAdapter<HouseImageInfo>(getApplicationContext(), R.layout.house_detail_gridview_item_layout, mDataList) {
//
//			@Override
//			public void convert(UniversalViewHolder holder, HouseImageInfo info) {
//				
//				
//			}
//			
//		};
//		
//		mHouseInfoGridview.setAdapter(adapter);
		
//		imageUrlList
//		.add("http://b.hiphotos.baidu.com/image/pic/item/d01373f082025aaf95bdf7e4f8edab64034f1a15.jpg");
//		imageUrlList
//				.add("http://g.hiphotos.baidu.com/image/pic/item/6159252dd42a2834da6660c459b5c9ea14cebf39.jpg");
//		imageUrlList
//				.add("http://d.hiphotos.baidu.com/image/pic/item/adaf2edda3cc7cd976427f6c3901213fb80e911c.jpg");
//		imageUrlList
//				.add("http://g.hiphotos.baidu.com/image/pic/item/b3119313b07eca80131de3e6932397dda1448393.jpg");
		
		
	}
	
	
	private void initBanner(List<String> imageUrlList) {
		if (imageUrlList.size() == 0){
			FrameLayout viewflowContent = (FrameLayout) findViewById(R.id.id_viewflow_framelayout);
			viewflowContent.setVisibility(View.GONE);
			return;
		}
		mViewFlow.setAdapter(new ImagePagerAdapter(HouseDetailInfoActivity.this, imageUrlList,
				null, null).setInfiniteLoop(true));
		mViewFlow.setmSideBuffer(imageUrlList.size()); // 实际图片张数，
														// 我的ImageAdapter实际图片张数为3
		mFlowIndicator.setIndicatorCount(imageUrlList.size());
		
		mViewFlow.setFlowIndicator(mFlowIndicator);
		mViewFlow.setTimeSpan(3000);
		mViewFlow.setSelection(imageUrlList.size() * 1000); // 设置初始位置
		mViewFlow.startAutoFlowTimer(); // 启动自动播放
		mFlowIndicator.requestLayout();
		mFlowIndicator.invalidate();
	}
	
	

	private void getHouseDetailInfoByHouseId(String rentNo){
		showLoadingView();
		mLoadingView.setVisibility(View.VISIBLE);
		String url = CommonUtil.mUserHost+"Services.asmx?op=GetHouseDetailInfo";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mHouseDetailAction));
		rpc.addProperty("rentNo", rentNo); 
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mHouseDetailAction, rpc);
		mPresenter.startPresentServiceTask();
	}
	
	private void getHouseDetailImageListByHouseId(String rentNo){
		
		String url = CommonUtil.mUserHost+"services.asmx?op=GetRentImageList";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mHouseImageListAction));
		rpc.addProperty("rentNo", rentNo); 
		mPresenter.readyPresentServiceParams(getApplicationContext(), url, mHouseImageListAction, rpc);
		mPresenter.startPresentServiceTask();
	}
	

	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 100){
				dismissLoadingView();
				jsonHouseInfoToView((String)msg.obj);
				showLoadingView();
				getHouseDetailImageListByHouseId(mRentNo);
			}else if (msg.what == 101){
			
				
			}else if (msg.what == 200){
				dismissLoadingView();
				jsonHouseImageListData((String)msg.obj);
				Log.i("mingguo", "handle message   image url list size  "+imageUrlList.size());
				initBanner(imageUrlList);
			}
		}
		
	};
	
	private void jsonHouseInfoToView(String value){
		if (value != null){
			JSONArray array;
			try {
				array = new JSONArray(value);
				JSONObject object = array.optJSONObject(0);
				if (object != null){
					mHouseId.setText(object.getString("RentNO"));
					mRentName.setText(object.getString("ROwner"));
					mRentPhone.setText(object.getString("ROwnerTel"));
					mRentArea.setText(object.getString("RRentArea")+" 平米");
					mRentProperty.setText(object.getString("RPropertyDesc"));
					mRentType.setText(object.getString("RRoomTypeDesc"));
					mRentDirection.setText(object.getString("RDirectionDesc"));
					mRentFloor.setText(object.getString("RFloor")+"/"+object.getString("RTotalFloor")+"层");
					mRentAddress.setText(object.getString("RAddress"));
					mRentStatus.setText(object.getString("IsAvailable"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void jsonHouseImageListData(String value){
		if (value != null){
			try {
				JSONObject object = new JSONObject(value);
				if (object != null){
					String imageCount = object.optString("count");
					int count = Integer.parseInt(imageCount);
					Log.w("mingguo", "countt  "+count);
					if (count > 0){
						if (count == 1){
							String imageUrl1 = object.optString("Image0");
							imageUrlList.add(mImageUrlPrefix+mImageUrlPrefix+imageUrl1);
						}else if (count == 2){
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							imageUrlList.add(mImageUrlPrefix+imageUrl1);
							imageUrlList.add(mImageUrlPrefix+imageUrl2);
						}else if (count == 3){
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							String imageUrl3 = object.optString("Image2");
							imageUrlList.add(mImageUrlPrefix+imageUrl1);
							imageUrlList.add(mImageUrlPrefix+imageUrl2);
							imageUrlList.add(mImageUrlPrefix+imageUrl3);
						}else if (count == 4){
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							String imageUrl3 = object.optString("Image2");
							String imageUrl4 = object.optString("Image3");
							imageUrlList.add(mImageUrlPrefix+imageUrl1);
							imageUrlList.add(mImageUrlPrefix+imageUrl2);
							imageUrlList.add(mImageUrlPrefix+imageUrl3);
							imageUrlList.add(mImageUrlPrefix+imageUrl4);
						}else if (count == 5){
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							String imageUrl3 = object.optString("Image2");
							String imageUrl4 = object.optString("Image3");
							String imageUrl5 = object.optString("Image4");
							imageUrlList.add(mImageUrlPrefix+imageUrl1);
							imageUrlList.add(mImageUrlPrefix+imageUrl2);
							imageUrlList.add(mImageUrlPrefix+imageUrl3);
							imageUrlList.add(mImageUrlPrefix+imageUrl4);
							imageUrlList.add(mImageUrlPrefix+imageUrl5);
						}else if (count == 6){
							String imageUrl1 = object.optString("Image0");
							String imageUrl2 = object.optString("Image1");
							String imageUrl3 = object.optString("Image2");
							String imageUrl4 = object.optString("Image3");
							String imageUrl5 = object.optString("Image4");
							String imageUrl6 = object.optString("Image5");
							imageUrlList.add(mImageUrlPrefix+imageUrl1);
							imageUrlList.add(mImageUrlPrefix+imageUrl2);
							imageUrlList.add(mImageUrlPrefix+imageUrl3);
							imageUrlList.add(mImageUrlPrefix+imageUrl4);
							imageUrlList.add(mImageUrlPrefix+imageUrl5);
							imageUrlList.add(mImageUrlPrefix+imageUrl6);
						}
					}
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
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

	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE){
					mLoadingView.setVisibility(View.INVISIBLE);
					return false;
				}
			}
			return super.onKeyDown(keyCode, event);
		}

	@Override
	public void onStatusStart() {
		
		
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		
		Log.i("mingguo", "on success  action "+action+"  msg  "+templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mHouseDetailAction)){
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
//			}else if (action.equals(mConfirmRentAttribute)){
//				mHandler.sendEmptyMessageDelayed(101, 10);
//			}else if (action.equals(mCompleteRentAttribute)){
//				mHandler.sendEmptyMessageDelayed(102, 1000);
			}else if (action.equals(mHouseImageListAction)){
				Message message = mHandler.obtainMessage();
				message.what = 200;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}
	
	

}
