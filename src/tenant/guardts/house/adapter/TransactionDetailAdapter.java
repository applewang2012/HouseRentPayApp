package tenant.guardts.house.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import tenant.guardts.house.R;
import tenant.guardts.house.bean.Detail;
import tenant.guardts.house.util.UtilTool;

public class TransactionDetailAdapter extends BaseAdapter {
	Context context;
	ArrayList<Detail> result;
	

	public TransactionDetailAdapter(Context context, ArrayList<Detail> result) {
		super();
		this.context = context;
		this.result = result;
	}

	@Override
	public int getCount() {
		return result!=null?result.size():0;
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
		if(convertView==null){
			convertView=View.inflate(context, R.layout.transaction_detail_item_layout, null);
			holder=new ViewHolder();
			holder.tvMemo=(TextView) convertView.findViewById(R.id.detail_type);
			holder.tvFee=(TextView) convertView.findViewById(R.id.detail_sum);
			holder.tvDate=(TextView) convertView.findViewById(R.id.detail_date);
//			holder.tvType=(TextView) convertView.findViewById(R.id.detail_mode);
			holder.tvDesp=(TextView) convertView.findViewById(R.id.detail_desp);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
			
		}
		holder.tvMemo.setText(result.get(position).memo+"成功");
		holder.tvFee.setText(result.get(position).memo+"金额: ¥"+result.get(position).fee);
		int type=Integer.valueOf(result.get(position).payType);
		/*if(type==0){
			holder.tvType.setText("微信支付");
			
		}else if(type==1){
			holder.tvType.setText("钱包支付");
			
		}*/
		String str = result.get(position).payDate;
		holder.tvDate.setText(result.get(position).memo+"时间: "+UtilTool.stampToNormalDate(str.substring(6, str.length()-2)));
		holder.tvDesp.setText(result.get(position).memo+"描述: "+result.get(position).billDesc);
		return convertView;
	}
	class ViewHolder{
		TextView tvMemo;
		TextView tvFee;
		TextView tvType;
		TextView tvDate;
		TextView tvDesp;
	}
}
