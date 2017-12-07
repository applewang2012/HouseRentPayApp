package tenant.guardts.house.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tenant.guardts.house.R;
import tenant.guardts.house.model.Retinue;
import tenant.guardts.house.model.RetinuesResult;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class CommonRetinueAdapter extends BaseAdapter implements OnClickListener {
	Context context;
	ArrayList<RetinuesResult> list;
	CallBack mCallBack;
	private Map<Integer,Boolean> map=new HashMap<>();// 存放已被选中的CheckBox
	
	
	public CommonRetinueAdapter(Context context, ArrayList<RetinuesResult> list, CallBack mCallBack) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_common_retinue, null);
			holder.name=(TextView) convertView.findViewById(R.id.name);
			holder.idcard=(TextView) convertView.findViewById(R.id.idcard);
			holder.box=(CheckBox) convertView.findViewById(R.id.checkbox);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.box.setOnClickListener(this);
		holder.box.setTag(position);
		holder.name.setText(list.get(position).name);
		
		holder.idcard.setText(list.get(position).idcard);
		holder.box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				 if(isChecked==true){
	                    map.put(position,true);
	                }else {
	                    map.remove(position);
	                }
			}
		});
		
		if(map!=null&&map.containsKey(position)){
            holder.box.setChecked(true);
        }else {
            holder.box.setChecked(false);
        }
		return convertView;
	}

	class ViewHolder {
		TextView name;
		TextView idcard;
		CheckBox box;

	}

	@Override
	public void onClick(View v) {
		mCallBack.click(v);
	}

}
