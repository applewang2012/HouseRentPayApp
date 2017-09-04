package tenant.guardts.house.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.R;

public class HistoryRecordsAdapter extends BaseAdapter {
	Context ctx;
	ArrayList<String> list;

	

	public HistoryRecordsAdapter(Context ctx, ArrayList<String> list) {
		super();
		this.ctx = ctx;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list!=null?list.size():0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(ctx,R.layout.history_listview_item, null);		
			holder = new ViewHolder();
			holder.district = (TextView) convertView.findViewById(R.id.history_item_textview);
			convertView.setTag(holder);
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.district.setText(list.get(position));
		
		
		
		
		return convertView;
	}

	class ViewHolder {
		ImageView image;
		TextView district;
	}

}
