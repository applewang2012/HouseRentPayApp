package tenant.guardts.house.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class HomeFragmentListView extends ListView {

	public HomeFragmentListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HomeFragmentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HomeFragmentListView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
