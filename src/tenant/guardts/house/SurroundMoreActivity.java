package tenant.guardts.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tenant.guardts.house.headergridview.StickyGridHeadersBaseAdapter;
import tenant.guardts.house.headergridview.StickyGridHeadersGridView;
import tenant.guardts.house.model.SurroundInfo;
import tenant.guardts.house.util.GlobalUtil;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 	Copyright	2016	CoderDream's Eclipse
 * 
 * 	All right reserved.
 * 	
 * 	Created on 2016年3月17日 下午4:59:44
 * 	
 * 	Update on 2016年3月17日 下午4:59:44
 * 
 * 	@author xiaoming
 *	
 * 	@mail wangfeng.wf@warmdoc.com
 * 
 * 	@tags An overview of this file: 可扩展的条件筛选菜单Demo主页
 * 
 */
public class SurroundMoreActivity extends BaseActivity{

	
	private Context mContext;
	
	private List<SurroundInfo> mDataList = new ArrayList<>();
	private StickyGridHeadersGridView mHeadersGridView;
	private List<String> mTitleList = new ArrayList<>();
	private Map<Integer, String[]> mContentMap = new HashMap<>();
	private List<String> mContentList = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.more_surround_life_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView textBar = (TextView)findViewById(R.id.id_titlebar);
		textBar.setText("更多周边");
		mContext = getApplicationContext();
		initView();
		initData();
	}
	private void initView(){
		mHeadersGridView = (StickyGridHeadersGridView) findViewById(R.id.sgv);
		mHeadersGridView.setAdapter(new StickyGridHeadersBaseAdapter() {

			private int mCurrentHeader;

			@Override
			public void unregisterDataSetObserver(DataSetObserver arg0) {

			}

			@Override
			public void registerDataSetObserver(DataSetObserver arg0) {

			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public int getViewTypeCount() {
				return 1;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
	                final Button b = new Button(mContext);
	                arg1 = b;
					b.setWidth(10);
					b.setHeight(50);
					b.setText(mContentList.get(arg0));
					b.setTextSize(15);
					b.setTextColor(Color.parseColor("#000000"));
					//b.setBackgroundColor(Color.parseColor("#0b6cfe"));
					b.setBackgroundColor(Color.parseColor("#00000000"));
					final String searchText = mContentList.get(arg0);
					b.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							//searchNearbyProcess(searchText);
							Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
							surroundIntent.putExtra("search_text", searchText);
							startActivity(surroundIntent);
						}
					});
	            return b;
			}

			@Override
			public int getItemViewType(int arg0) {
				return 0;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				return getContentCount();
			}

			@Override
			public boolean isEnabled(int position) {
				return false;
			}

			@Override
			public boolean areAllItemsEnabled() {
				return false;
			}

			@Override
			public int getNumHeaders() {
				return mTitleList.size();
			}

			@Override
			public View getHeaderView(int position, View convertView,
					ViewGroup parent) {
//				TitleViewHolder holder = null;
//	            if (convertView == null) {
//	                holder = new TitleViewHolder();
	                TextView b = new TextView(mContext);
	                convertView = b;
					b.setWidth(200);
					b.setHeight(150);
					b.setTextSize(20);
					b.setPadding(20, 0, 0, 0);
					b.setGravity(Gravity.BOTTOM);
					b.setTextColor(Color.parseColor("#0b6cfe"));
					b.setBackgroundColor(Color.parseColor("#00000000"));
					b.setText(mTitleList.get(position));
//					holder.textView = b;
//	                convertView.setTag(holder);
//	            } else {
//	                holder = (TitleViewHolder)convertView.getTag();
//	            }
				return b;
			}

			@Override
			public int getCountForHeader(int header) {
				mCurrentHeader = header;
				return mContentMap.get(header).length;
				
			}
		});
		final EditText inputSurround = (EditText)findViewById(R.id.id_surround_search_input);
		Button searchButton = (Button)findViewById(R.id.id_surround_search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (inputSurround.getText().toString() == null || inputSurround.getText().toString().equals("")){
					GlobalUtil.shortToast(mContext, "请输入您感兴趣周边信息！", mContext.getResources().getDrawable(R.drawable.ic_dialog_no));
					return;
				}
				Intent surroundIntent = new Intent(mContext, SurroundResultActivity.class);
				surroundIntent.putExtra("search_text", inputSurround.getText().toString());
				startActivity(surroundIntent);
				
			}
		});
		
	}
	
	private void initData(){
	     
		String [] title = getResources().getStringArray(R.array.surround_title);
		for (int titleIndex = 0; titleIndex < 8; titleIndex++){
			mTitleList.add(title[titleIndex]);
		}
		String [] childHot = getResources().getStringArray(R.array.surround_content_hot);
		String [] childEat = getResources().getStringArray(R.array.surround_content_eat);
		String [] childHotel = getResources().getStringArray(R.array.surround_content_hotel);
		String [] childTravel = getResources().getStringArray(R.array.surround_content_travel);
		String [] childLeasure = getResources().getStringArray(R.array.surround_content_leasure);
		String [] childLive = getResources().getStringArray(R.array.surround_content_live);
		String [] childJiandian = getResources().getStringArray(R.array.surround_content_jingdian);
		String [] childBuy = getResources().getStringArray(R.array.surround_content_buy);
		for (int contentIndex = 0; contentIndex < 8; contentIndex++){
			if (contentIndex == 0){
				mContentMap.put(contentIndex, childHot);
			}else if (contentIndex == 1){
				mContentMap.put(contentIndex, childEat);
			}else if (contentIndex == 2){
				mContentMap.put(contentIndex, childHotel);
			}else if (contentIndex == 3){
				mContentMap.put(contentIndex, childTravel);
			}else if (contentIndex == 4){
				mContentMap.put(contentIndex, childLeasure);
			}else if (contentIndex == 5){
				mContentMap.put(contentIndex, childLive);
			}else if (contentIndex == 6){
				mContentMap.put(contentIndex, childJiandian);
			}else if (contentIndex == 7){
				mContentMap.put(contentIndex, childBuy);
			}
		}
		
		for (int index = 0; index < mContentMap.size(); index++){
			String [] childContent = mContentMap.get(index);
			for (int childIndex = 0; childIndex < childContent.length; childIndex++){
				mContentList.add(childContent[childIndex]);
			}
		}
	}
	
	
	private int getContentCount(){
		int num = 0;
		for (int i =0; i < mContentMap.size(); i++){
			int childCount = mContentMap.get(i).length;
			num = num + childCount;
		}
		return num;
	}
	

	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	
}
