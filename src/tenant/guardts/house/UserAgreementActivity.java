package tenant.guardts.house;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.bannerview.ViewPagerFixed;

public class UserAgreementActivity extends BaseActivity {

	private MyPageAdapter adapter;
	private ArrayList<View> listViews = new ArrayList<>();
	private ViewPagerFixed pager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_user_agreement);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("用户服务协议");
		
		ImageView imgView1 = new ImageView(this);     
        ImageView imgView2 = new ImageView(this);    
        ImageView imgView3 = new ImageView(this);  
        ImageView imgView4 = new ImageView(this);  
        imgView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView4.setScaleType(ImageView.ScaleType.CENTER_CROP);
		listViews.add(imgView1);
		listViews.add(imgView2);
		listViews.add(imgView3);
		listViews.add(imgView4);
		pager = (ViewPagerFixed) findViewById(R.id.agreenment_gallery);
		pager.setOnPageChangeListener(pageChangeListener);
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin((int)getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
		pager.setCurrentItem(0);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
//			location = arg0;
//			mSelectedViewNum.setText((location+1) + "/"+mTotalNum);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);
				
			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
