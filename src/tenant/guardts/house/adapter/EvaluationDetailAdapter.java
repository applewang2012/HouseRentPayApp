package tenant.guardts.house.adapter;

import java.util.ArrayList;

import tenant.guardts.house.R;
import tenant.guardts.house.model.EvaluationDetail;
import tenant.guardts.house.view.CustomRatingBar2;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
		holder.serviceBar=(CustomRatingBar2) convertView.findViewById(R.id.evaluation_service_rating);
		holder.environmentBar=(CustomRatingBar2) convertView.findViewById(R.id.evaluation_environmental_rating);
		holder.pirceBar=(CustomRatingBar2) convertView.findViewById(R.id.evaluation_price_rating);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.user.setText(list.get(position).getUser());
		holder.date.setText(list.get(position).getDate());
		holder.serviceBar.setScore(2);
//		holder.serviceBar.setScore(Integer.valueOf(list.get(position).getServiceRating()));
		holder.environmentBar.setScore(Integer.valueOf(list.get(position).getEnvironmentalRating()));
		holder.pirceBar.setScore(Integer.valueOf(list.get(position).getPriceRating()));
		Toast.makeText(context, list.get(position).getServiceRating()+"="+list.get(position).getEnvironmentalRating()+"="+list.get(position).getPriceRating(), Toast.LENGTH_SHORT).show();
		return convertView;
	}
	class ViewHolder{
		TextView user;
		TextView date;
		CustomRatingBar2 serviceBar;
		CustomRatingBar2 environmentBar;
		CustomRatingBar2 pirceBar;
		
	}

}
