package tenant.guardts.house.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import tenant.guardts.house.R;
import tenant.guardts.house.model.HouseInfoModel;

public class PublishedRecordAdapter extends BaseAdapter implements OnClickListener {
	Context context;
	ArrayList<HouseInfoModel> list;
	CallBack mCallBack;
	
	
	public PublishedRecordAdapter(Context context, ArrayList<HouseInfoModel> list, CallBack mCallBack) {
		super();
		this.context = context;
		this.list = list;
		this.mCallBack = mCallBack;
	}

	public interface CallBack {
		public void click(View v);
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
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
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.house_history_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.id_house_address);
			holder.type = (TextView) convertView.findViewById(R.id.id_house_type);
			holder.direction = (TextView) convertView.findViewById(R.id.id_house_direction);
			holder.floor = (TextView) convertView.findViewById(R.id.id_house_floor);
			holder.del = (Button) convertView.findViewById(R.id.btn_del);
			holder.detail = (Button) convertView.findViewById(R.id.btn_detail);
			holder.history = (Button) convertView.findViewById(R.id.btn_history);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.del.setOnClickListener(this);
		holder.detail.setOnClickListener(this);
		holder.history.setOnClickListener(this);
		holder.del.setTag(position);;
		holder.detail.setTag(position);
		holder.history.setTag(position);
		holder.title.setText(list.get(position).getHouseAddress());
		holder.type.setText(list.get(position).getHouseType());
		holder.direction.setText(list.get(position).getHouseDirection());
		holder.floor.setText(
				list.get(position).getHouseCurrentFloor() + "/" + list.get(position).getHouseTotalFloor() + "层");
		return convertView;
	}

	class ViewHolder {
		TextView title;
		TextView type;
		TextView direction;
		TextView floor;
		Button del;
		Button detail;
		Button history;

	}

	@Override
	public void onClick(View v) {
		mCallBack.click(v);
	}

}
