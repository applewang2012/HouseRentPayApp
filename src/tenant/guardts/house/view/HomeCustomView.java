package tenant.guardts.house.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import tenant.guardts.house.R;

public class HomeCustomView extends LinearLayout {
	TextView tv;
	ImageView img;
	private int background;
	private int textColor;

	public HomeCustomView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View view = LayoutInflater.from(context).inflate(R.layout.custom_view_layout,this, true);
		tv=(TextView) view.findViewById(R.id.textView1);
		img=(ImageView) view.findViewById(R.id.imageView1);
	}

	public HomeCustomView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HomeCustomView(Context context) {
		this(context, null);
	}
	
	public void setBackgroundAndTextColor(int resid,String color){
		
	}
	
	public void setImageAndContent(int resid,String content){
		tv.setText(content);
		img.setBackgroundResource(resid);
	}
	
	
	
	
	
	
	
	
	

}
