package tenant.guardts.house;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import tenant.guardts.house.model.ActivityController;
import tenant.guardts.house.model.HouseInfoModel;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.search.ExpandMenuView;
import tenant.guardts.house.search.ExpandMenuView.OnButtonClickListener;
import tenant.guardts.house.search.HouseInfo;
import tenant.guardts.house.search.Item;
import tenant.guardts.house.search.Province;
import tenant.guardts.house.search.SingleListFilterView;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;

/**
 * Copyright 2016 CoderDream's Eclipse
 * 
 * All right reserved.
 * 
 * Created on 2016年3月17日 下午4:59:44
 * 
 * Update on 2016年3月17日 下午4:59:44
 * 
 * @author xiaoming
 *
 * @mail wangfeng.wf@warmdoc.com
 * 
 * @tags An overview of this file: 可扩展的条件筛选菜单Demo主页
 * 
 */
public class SearchResultActivity extends BaseActivity {

	/**
	 * 列表原数据
	 */
	// private List<Item> allItems;
	/**
	 * 可扩展的条件筛选菜单组合控件
	 */
	private ExpandMenuView expandTabView;
	/**
	 * 筛选条件视图集合
	 */
	private ArrayList<View> mViewArray;
	private ListView mSearchViewList;
	/**
	 * 城市筛选条件数据
	 */
	private List<Province> allCitys;
	/**
	 * 等级筛选条件数据
	 */
	// private List<HouseInfo> grads;

	/**
	 * 筛选后的数据
	 */
	private List<Item> items;

	// private ArrayList<String> superItemDatas;

	// 筛选条件
	private String cityName = null;
	private String gradId = null, gradId2 = null;
	private int sort = 0;
	private ArrayList<HouseInfo> grads2;
	private UniversalAdapter<HouseInfoModel> mAdapter;
	private List<HouseInfoModel> mHouseInfoList = new ArrayList<>();
	private Context mContext;
	private HoursePresenter mPresent;
	private String mSearchAction = "http://tempuri.org/GetHousesByCondition";
	private String mHouseType = "", mRentType = "";
	private int mPageCount = 1000;

	// private EditText mAddressEdit;
	private TextView mNoContent;

	private String mSearch_tag;
	private String mStartTime;
	private String mEndTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.house_search_layout);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.titlebar);
		// TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		// mTitleBar.setText("短租共享");
		ActivityController.addActivity(this);
		TextView searchButton = (TextView) findViewById(R.id.button_search);
		searchButton.setVisibility(View.VISIBLE);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(SearchResultActivity.this, SearchFilterActivity.class), 1);

			}
		});

		mContext = getApplicationContext();
		mPresent = new HoursePresenter(mContext, SearchResultActivity.this);
		mSearch_tag = getIntent().getStringExtra("search_tag");
		mStartTime = getIntent().getStringExtra("start_time");
		mEndTime = getIntent().getStringExtra("end_time");
		rentType = getIntent().getStringExtra("rent_type");
		houseMode = getIntent().getStringExtra("house_mode");

		initView();
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		List<String> typeItem = new ArrayList<String>();
		typeItem.add("房型");
		typeItem.add("一室");
		typeItem.add("两室");
		typeItem.add("三室");

<<<<<<< Updated upstream
				List<String> rentItem = new ArrayList<String>();
				rentItem.add("租赁类型");
				rentItem.add("日租");
				rentItem.add("月租");
//				for (int i = 0; i < grads2.size(); i++) {
//					gradItems2.add(grads2.get(i).getGradName());
//				}
		final SingleListFilterView rentTypeFilter = new SingleListFilterView(this, rentItem, "租赁类型");
=======
		List<String> rentItem = new ArrayList<String>();
		rentItem.add("全部");
		rentItem.add("日租");
		rentItem.add("月租");
		rentItem.add("时租");
		// for (int i = 0; i < grads2.size(); i++) {
		// gradItems2.add(grads2.get(i).getGradName());
		// }
		final SingleListFilterView rentTypeFilter = new SingleListFilterView(this, rentItem, "全部");
>>>>>>> Stashed changes
		rentTypeFilter.setOnSelectListener(new SingleListFilterView.OnSelectListener() {

			@Override
			public void getValue(String showText, int position) {
				refreshScreen(rentTypeFilter, showText, -1, position);
			}
		});
<<<<<<< Updated upstream
		
		final SingleListFilterView typeFilter = new SingleListFilterView(this, typeItem, "房型");
=======

		final SingleListFilterView typeFilter = new SingleListFilterView(this, typeItem, "全部");
>>>>>>> Stashed changes
		typeFilter.setOnSelectListener(new SingleListFilterView.OnSelectListener() {

			@Override
			public void getValue(String showText, int position) {
				refreshScreen(typeFilter, showText, -1, position);
			}
		});

		// 排序
		List<String> showList = new ArrayList<String>();
		showList.add("全部");
		showList.add("20条");
		showList.add("50条");
		showList.add("100条");
		final SingleListFilterView showListFilter = new SingleListFilterView(this, showList, "排序筛选");
		showListFilter.setOnSelectListener(new SingleListFilterView.OnSelectListener() {

			@Override
			public void getValue(String showText, int position) {
				refreshScreen(showListFilter, showText, -1, position);
			}
		});

		// 添加条件筛选控件到数据集合中
		mViewArray = new ArrayList<View>();
		mViewArray.add(typeFilter);
		mViewArray.add(rentTypeFilter);
		mViewArray.add(showListFilter);

		ArrayList<String> orignList = new ArrayList<String>();
		if (!TextUtils.isEmpty(rentType) && !TextUtils.isEmpty(houseMode)) {
			orignList.add(houseMode);
			orignList.add(rentType);
			orignList.add("全部");
			if ("一室".equals(houseMode)) {
				mHouseType="01";
			} else if ("两室".equals(houseMode)) {
				mHouseType="02";

			} else if ("三室".equals(houseMode)) {
				mHouseType="03";

			}
			if ("日租".equals(rentType)) {
				mRentType="02";
			} else if ("月租".equals(rentType)) {
				mRentType="01";
			} else if ("时租".equals(rentType)) {
				mRentType="";
			}
		} else {

			orignList.add("全部");
			orignList.add("全部");
			orignList.add("全部");

		}
		// 给组合控件设置数据
		expandTabView.setValue(orignList, mViewArray);

		// 处理组合控件按钮点击事件
		expandTabView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClick(int selectPosition, boolean isChecked) {
				LogUtil.w("mingguo", "select position  " + selectPosition + "  isChecked   " + isChecked);
			}
		});

		searchHouseByFilterCondition(mSearch_tag);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			mSearch_tag = bundle.getString("search_tag");
			LogUtil.e("mingguo", "scan  result  " + mSearch_tag);
			TextView showSearch = (TextView) findViewById(R.id.button_search);

			if (mSearch_tag == null || mSearch_tag.equals("")) {
				showSearch.setText("想租房？来连心锁！");
				showSearch.setTextColor(Color.parseColor("#cccccc"));

			} else {
				showSearch.setText(mSearch_tag);
				showSearch.setTextColor(Color.parseColor("#ffffff"));
				searchHouseByFilterCondition(mSearch_tag);
			}
		}
	}

	private void searchHouseByFilterCondition(String inputCotent) {

		String url = CommonUtil.mUserHost + "Services.asmx?op=GetHousesByCondition";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mSearchAction));
		rpc.addProperty("pageSize", mPageCount);
		rpc.addProperty("pageIndex", 1);
		rpc.addProperty("housetype", mHouseType);
		rpc.addProperty("rentType", mRentType);
		rpc.addProperty("address", inputCotent);
		rpc.addProperty("isAvalible", "0");
		rpc.addProperty("userID", "1");
		rpc.addProperty("startdate", mStartTime);
		rpc.addProperty("endate", mEndTime);
		mPresent.readyPresentServiceParams(this, url, mSearchAction, rpc);
		mPresent.startPresentServiceTask(true);
		LogUtil.w("mingguo", "start search house " + mPageCount + "  type  " + mHouseType + " rent type  " + mRentType);
	}

	private void initAdapter() {
		mAdapter = new UniversalAdapter<HouseInfoModel>(getApplicationContext(), R.layout.house_search_list_item,
				mHouseInfoList) {

			@Override
			public void convert(UniversalViewHolder holder, HouseInfoModel info) {
				View holderView = holder.getConvertView();
				TextView addressTextView = (TextView) holderView.findViewById(R.id.id_house_address);
				TextView typeTextView = (TextView) holderView.findViewById(R.id.id_house_type);
				TextView directionTextView = (TextView) holderView.findViewById(R.id.id_house_direction);
				TextView floorTextView = (TextView) holderView.findViewById(R.id.id_house_floor);
				TextView areaView = (TextView) holderView.findViewById(R.id.id_house_area);
				addressTextView.setText(info.getHouseAddress());
				typeTextView.setText(info.getHouseType());
				directionTextView.setText(info.getHouseDirection());
				areaView.setText(info.getHouseArea() + "平米");
				floorTextView.setText(info.getHouseCurrentFloor() + "/" + info.getHouseTotalFloor() + "层");
				// statusTextView.setText(info.getHouseStatus());
			}
		};
	}

	private void initView() {

		// mContentLayout =
		// (LinearLayout)findViewById(R.id.id_frament_house_cotent);
		mNoContent = (TextView) findViewById(R.id.id_frament_house_no_cotent);

		mNoContent.setVisibility(View.INVISIBLE);
		// mContentLayout.setVisibility(View.INVISIBLE);
		expandTabView = (ExpandMenuView) findViewById(R.id.expandTabView);
		mSearchViewList = (ListView) findViewById(R.id.listView);
		items = new ArrayList<Item>();
		initAdapter();
		mSearchViewList.setAdapter(mAdapter);
		// mAddressEdit = (EditText)findViewById(R.id.id_input_address);
		// Button searchButton =
		// (Button)findViewById(R.id.id_modify_password_confirm);
		// searchButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// searchHouseByFilterCondition("");
		//
		// }
		// });
		//
		mSearchViewList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (CommonUtil.mUserLoginName == null || CommonUtil.mUserLoginName.equals("")) {
					Toast.makeText(SearchResultActivity.this, "您尚未登录，请登录后再进行操作！", Toast.LENGTH_LONG).show();
					Intent loginIntent = new Intent(mContext, LoginUserActivity.class);
					loginIntent.putExtra("intent_status", true);
					startActivity(loginIntent);
				} else {
					if (mHouseInfoList.get(position).getHouseId() != null
							&& !mHouseInfoList.get(position).getHouseId().equals("")) {
						Intent detailIntent = new Intent(mContext, HouseDetailInfoActivity.class);
						detailIntent.putExtra("rentNo", mHouseInfoList.get(position).getHouseId());
						startActivity(detailIntent);
					}
				}
			}
		});

	}

	/**
	 * 更新筛选条件
	 * 
	 * @param view
	 * @param showText
	 * @param superPosition
	 * @param pos
	 *            选中的位置
	 */
	private void refreshScreen(View view, String showText, int superPosition, int pos) {
		expandTabView.closeView();
		int position = getPositon(view);
		if (position >= 0)
			expandTabView.setTitle(showText, position);

		items.clear();
		switch (position) {
		case 0:// 城市筛选
				// if(superPosition == 0){
				// cityName = null;
				// break;
				// }
				// Province province = allCitys.get(superPosition);
				// List<City> citys = province.getCitys();
				// if(citys == null || citys.size() == 0 || pos == 0){
				// cityName = province.getName();
				// break;
				// }
				// City city = citys.get(pos);
				// cityName = city.getCityName();
				// if(pos == 0){
				// gradId2 = null;
				// break;
				// }
				// HouseInfo grad2 = grads2.get(pos);
				// gradId2 = grad2.getId();
			LogUtil.w("mingguo", "postion  == 0  pos  " + pos);
			if (pos == 0) {
				mHouseType = "";
			} else if (pos == 1) {
				mHouseType = "01";
			} else if (pos == 2) {
				mHouseType = "02";
			} else if (pos == 3) {
				mHouseType = "03";
			}
			break;
		case 1:// 等级筛选
				// if(pos == 0){
				// gradId = null;
				// break;
				// }
				// HouseInfo grad = grads.get(pos);
				// gradId = grad.getId();
			LogUtil.w("mingguo", "postion  == 1  pos  " + pos);
			if (pos == 0) {
				mRentType = "";
			} else if (pos == 1) {
				mRentType = "02";
			} else if (pos == 2) {
				mRentType = "01";
			}
			break;
		case 2:// 排序
			sort = pos;
			LogUtil.w("mingguo", "postion  == 2  pos  " + pos);
			if (pos == 0) {
				mPageCount = 999;
			} else if (pos == 1) {
				mPageCount = 20;
			} else if (pos == 2) {
				mPageCount = 50;
			} else if (pos == 3) {
				mPageCount = 100;
			}
			break;
		}
		searchHouseByFilterCondition(mSearch_tag);
	}

	/**
	 * 获取当前点击的位置
	 * 
	 * @param tView
	 * @return
	 */
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView)
				return i;
		}

		return -1;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 100) {
				if (msg.obj != null) {

					jsonSearchResultData((String) msg.obj);
					if (mHouseInfoList.size() == 0) {
						mSearchViewList.setVisibility(View.GONE);
						mNoContent.setVisibility(View.VISIBLE);
					} else {
						mSearchViewList.setVisibility(View.VISIBLE);
						mNoContent.setVisibility(View.INVISIBLE);
						LogUtil.w("housefragment", "house list  " + mHouseInfoList.size());
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}

	};
	private String houseMode;
	private String rentType;

	private void jsonSearchResultData(String obj) {
		try {
			JSONArray array = new JSONArray(obj);
			if (array != null) {
				LogUtil.w("mingguo", "parse house info " + array.length());
				mHouseInfoList.clear();
				for (int item = 0; item < array.length(); item++) {

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
					houseModel.setHouseArea(itemJsonObject.optString("RRentArea"));
					mHouseInfoList.add(houseModel);
				}
			}
			LogUtil.w("mingguo", "search  result  mHouseInfoList  " + mHouseInfoList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		// TODO Auto-generated method stub
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mSearchAction)) {
				Message msgMessage = mHandler.obtainMessage();
				msgMessage.what = 100;
				msgMessage.obj = templateInfo;
				msgMessage.sendToTarget();
			}
		}
	}

	@Override
	public void onStatusError(String action, String error) {
		// TODO Auto-generated method stub
		super.onStatusError(action, error);
		LogUtil.w("mingguo", "on status error  action  " + error);
	}

}
