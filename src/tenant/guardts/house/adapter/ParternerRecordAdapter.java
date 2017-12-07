package tenant.guardts.house.adapter;

import java.util.ArrayList;

import tenant.guardts.house.R;
import tenant.guardts.house.model.Retinue;
import tenant.guardts.house.model.RetinuesResult;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ParternerRecordAdapter extends BaseAdapter implements OnClickListener {
	Context context;
	ArrayList<RetinuesResult> list;
	CallBack mCallBack;
	
	
	public ParternerRecordAdapter(Context context, ArrayList<RetinuesResult> list, CallBack mCallBack) {
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
			convertView = View.inflate(context, R.layout.parterner_record_item, null);
			holder.name=(TextView) convertView.findViewById(R.id.parterner_name);
			holder.idcard=(TextView) convertView.findViewById(R.id.parterner_idcard);
			holder.edit=(TextView) convertView.findViewById(R.id.parterner_edit);
			holder.del=(TextView) convertView.findViewById(R.id.parterner_del);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.del.setOnClickListener(this);
		holder.edit.setOnClickListener(this);
		holder.del.setTag(position);
		holder.edit.setTag(position);
		holder.name.setText(list.get(position).name);
		holder.idcard.setText(list.get(position).idcard);
		
		return convertView;
	}

	class ViewHolder {
		TextView name;
		TextView idcard;
		TextView edit;
		TextView del;

	}

	@Override
	public void onClick(View v) {
		mCallBack.click(v);
	}

}
