package tenant.guardts.house.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import tenant.guardts.house.R;

public class CustomRatingBar extends LinearLayout implements OnClickListener {

	private View view;
	private CheckBox box1;
	private CheckBox box2;
	private CheckBox box3;
	private CheckBox box4;
	private CheckBox box5;

	private int currentPos;
	private int lastPos;
	private CheckBox[] box;
	private int score;

	private int getScore() {
		return score;
	}

	private void setScore(int score) {
		this.score = score;
	}

	public CustomRatingBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		view = LayoutInflater.from(context).inflate(R.layout.rating_bar, this, true);
		box1 = (CheckBox) view.findViewById(R.id.c1);
		box2 = (CheckBox) view.findViewById(R.id.c2);
		box3 = (CheckBox) view.findViewById(R.id.c3);
		box4 = (CheckBox) view.findViewById(R.id.c4);
		box5 = (CheckBox) view.findViewById(R.id.c5);
		box = new CheckBox[] { box1, box2, box3, box4, box5 };
	
		
		initEvent();

	}

	public void initEvent() {
		box1.setOnClickListener(this);
		box2.setOnClickListener(this);
		box3.setOnClickListener(this);
		box4.setOnClickListener(this);
		box5.setOnClickListener(this);

	}

	public CustomRatingBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomRatingBar(Context context) {
		this(context, null);
	}


	@Override
	public void onClick(View v) {
		int count = 0;
		lastPos = currentPos;
		switch (v.getId()) {
		case R.id.c1:
			currentPos = 0;
			break;
		case R.id.c2:
			currentPos = 1;
			break;
		case R.id.c3:
			currentPos = 2;
			break;
		case R.id.c4:
			currentPos = 3;
			break;
		case R.id.c5:
			currentPos = 4;
			break;

		}

		for (int i = 0; i < box.length; i++) {
			if (box[i] != null) {

				if (currentPos > lastPos) {
					if (i >= lastPos && i <= currentPos) {
						box[i].setChecked(true);
						
					}
				} else if (currentPos < lastPos) {
					if (i > currentPos && i <= lastPos) {

						box[i].setChecked(false);
					
					}
				}
				if(box[i].isChecked()){
					count++;
					
				}

				
			}
			

		}
		setScore(count);
		

	}

	/**
	 * 获得评价的分数
	 * 
	 * @return
	 */
	public int getCount() {
		return getScore();

	}

}
