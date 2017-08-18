package tenant.guardts.house.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class HistoryRecordsView extends ListView {

	public HistoryRecordsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HistoryRecordsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HistoryRecordsView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
