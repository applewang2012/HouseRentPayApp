package tenant.guardts.house.adapter;

import java.util.ArrayList;

import tenant.guardts.house.R;
import tenant.guardts.house.model.EvaluationDetail;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EvaluationDetailAdapter extends BaseAdapter {
	Context context;
	ArrayList<EvaluationDetail> list;
	
	public EvaluationDetailAdapter(Context context, ArrayList<EvaluationDetail> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(context, R.layout.evaluation_detail_item, null);
		holder.user=(TextView) convertView.findViewById(R.id.detail_user);
		holder.date=(TextView) convertView.findViewById(R.id.detail_date);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.user.setText(list.get(position).getUser());
		holder.date.setText(list.get(position).getDate());
		return convertView;
	}
	class ViewHolder{
		TextView user;
		TextView date;
		
	}

}
