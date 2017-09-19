package tenant.guardts.house.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import tenant.guardts.house.R;
import tenant.guardts.house.model.RentRecord;
import tenant.guardts.house.util.UtilTool;

public class RentalDetailAdapter extends BaseExpandableListAdapter {
	Context context;
	ArrayList<RentRecord> list;

	public RentalDetailAdapter(Context context, ArrayList<RentRecord> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return list.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildHolder holder;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = View.inflate(context, R.layout.expandable_child_item, null);
			holder.start = (TextView) convertView.findViewById(R.id.start);
			holder.end = (TextView) convertView.findViewById(R.id.end);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.phone = (TextView) convertView.findViewById(R.id.phone);
			holder.fee = (TextView) convertView.findViewById(R.id.fee);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		String startTime = list.get(groupPosition).RRAStartDate;
		String endTime = list.get(groupPosition).RRAEndDate;
		holder.start.setText(UtilTool.stampToDateTime(startTime.substring(6, startTime.length() - 2)));
		holder.end.setText(UtilTool.stampToDateTime(endTime.substring(6, endTime.length() - 2)));
		holder.name.setText(list.get(groupPosition).RRAContactName);
		holder.phone.setText(list.get(groupPosition).RRAContactTel);
		holder.fee.setText(list.get(groupPosition).RRentPrice);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {

		return list.get(groupPosition).RRAStartDate;

	}

	@Override
	public int getGroupCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.expandable_group_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String startTime = list.get(groupPosition).RRAStartDate;
		;
		holder.title.setText("入住时间" + UtilTool.stampToDateTime(startTime.substring(6, startTime.length() - 2)));
		if (isExpanded) {
			holder.img.setImageResource(R.drawable.history_up);
		} else {
			holder.img.setImageResource(R.drawable.history_down);

		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class ViewHolder {
		TextView title;
		ImageView img;
	}

	class ChildHolder {
		TextView start;
		TextView end;
		TextView name;
		TextView phone;
		TextView fee;
	}

}
